package com.mxs.mxscodesandbox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.async.ResultCallbackTemplate;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.mxs.mxscodesandbox.model.ExecuteCodeRequest;
import com.mxs.mxscodesandbox.model.ExecuteCodeResponse;
import com.mxs.mxscodesandbox.model.JudgeInfo;
import com.mxs.mxscodesandbox.model.ProcessExecuteMsg;
import com.mxs.mxscodesandbox.utils.CopyUtil;
import com.mxs.mxscodesandbox.utils.MountUtil1;
import com.mxs.mxscodesandbox.utils.MountUtil2;
import com.mxs.mxscodesandbox.utils.ProcessUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service("JavaDockerCodeSandboxImpl")
public class JavaDockerCodeSandboxImpl implements CodeSandbox {

    long time = 0L;

    private static final long TIME_OUT = 10000l;

    private static final String GLOBAL_CODE_DIR_NAME = "tmpcode";

    private static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";

    //private static final Boolean FIRST_INIT = true;

    public static void main(String[] args) {
        JavaDockerCodeSandboxImpl sandbox = new JavaDockerCodeSandboxImpl();
        ExecuteCodeRequest request = new ExecuteCodeRequest();
        request.setInputList(Arrays.asList("1 2", "1 3"));
        //get User's code
        String code = ResourceUtil.readStr("testcode/simple/Main.java", StandardCharsets.UTF_8);
        request.setCode(code);
        request.setLanguage("java");

        ExecuteCodeResponse response = sandbox.executeCode(request);
        System.out.println(response);
        /*String userCodeParentPath = "C:\\workspace_planet\\Online_Judge_System\\oj_project-backEnd\\mxs-code-sandbox\\tmpcode\\c90c226c-0efa-47e3-bec3-505704fc96cc";
        File classFile = new File(userCodeParentPath + File.separator + "Main.class");
        System.out.println("Main.class exists: " + classFile.exists());*/
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {

        List<String> inputList = executeCodeRequest.getInputList();
        String language = executeCodeRequest.getLanguage();
        String code = executeCodeRequest.getCode();

        //"user.dir".equals("C:\workspace_planet\Online_Judge_System\oj_project-backEnd\mxs-code-sandbox")
        String userDir = System.getProperty("user.dir");
        //C:\workspace_planet\Online_Judge_System\oj_project-backEnd\mxs-code-sandbox\tmpcode
        String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME;
        String linuxGlobalCodePathName = userDir.replace("\\", "/").replace("C:", "/c") + "/" + GLOBAL_CODE_DIR_NAME; // 转换为 Docker 格式
        System.out.println("linuxglobalCodePathName: " + linuxGlobalCodePathName);
        //FileUtil is in Hutool
        if (!FileUtil.exist(globalCodePathName)) {
            FileUtil.mkdir(globalCodePathName);
        }

        //用户代码存放,父级目录，每个都隔离开
        //String userCodeParentPath = globalCodePathName + File.separator + UUID.randomUUID();
        // 将路径转换为 Docker 格式，例如 /c/workspace...
        UUID uuid = UUID.randomUUID();
        String userCodeParentPath = globalCodePathName + File.separator + uuid;
        String linuxUserCodeParentPath = "/mnt" + linuxGlobalCodePathName + "/" + uuid;
        String linuxPath = "/mnt/c/workspace_planet/Online_Judge_System/oj_project-backEnd/mxs-code-sandbox/tmpcode" + "/" + uuid;
        //用户 Main.java 文件的存放路径
        String userCodePath = userCodeParentPath + File.separator + GLOBAL_JAVA_CLASS_NAME;
        //String linuxUserCodePath = linuxUserCodeParentPath + "/" + GLOBAL_JAVA_CLASS_NAME;
        //1. 写入code到 Main.java
        //writeString是写入文件方法，返回文件Main.java，在temcode内。参数：code, path, 编译器
        File userCodeFile = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);

        //2. 编译代码，得到class文件
        try {
            //String compileUtf_8 = String.format("javac -encoding utf-8 %s", userCodeFile.getAbsolutePath());
            String compile = String.format("javac %s", userCodeFile.getAbsolutePath());
            Process compileProcess = Runtime.getRuntime().exec(compile);
            //apply ProcessUtils
            ProcessExecuteMsg executeMsg = ProcessUtils.doProcessMsg(compileProcess, "compile: ");
            System.out.println(executeMsg);

            // Check if Main.class was generated successfully
            File compiledClassFile = new File(userCodeParentPath + File.separator + "Main.class");
            if (!compiledClassFile.exists()) {
                throw new RuntimeException("Main.class file was not generated as expected.");
            }
            System.out.println("Main.class successfully generated.");
        } catch (Exception e) {
            return getErrorResponse(e);
        }

        //3. 创建自定义 container，把文件复制到创建container内，执行并收集结果
        // Step 1: Configure DockerClientConfig. 配置 DockerClientConfig
        // Step 2: Instantiate DockerHttpClient. 实例化 DockerHttpClient
        // Step 3: Create DockerClient instance. 创建 DockerClient 实例
        // Step 4: Pull the image
        // Step 5: Copy .class file to container
        // Step 6: docker exec optimistic_pare java -cp /app Main 1 3
        // Step 1: Configure DockerClientConfig. 配置 DockerClientConfig
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://192.168.15.130:2375") // Docker Host URL
                .withDockerTlsVerify(false)              // Disable TLS
                .build();
        // Step 2: Instantiate DockerHttpClient. 实例化 DockerHttpClient
        ApacheDockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();
        // Step 3: Create DockerClient instance. 创建 DockerClient 实例
        DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient);

        // Step 4: Pull the image
        String image = "amazoncorretto:17-alpine";
        //String image = "amazoncorretto:17";
        //String image = "openjdk:8-alpine";
        boolean imageExists = dockerClient.listImagesCmd()
                .exec()
                .stream()
                .filter(img -> img.getRepoTags() != null)  // 确保 RepoTags 不是 null
                .anyMatch(img -> Arrays.stream(img.getRepoTags()).anyMatch(tag -> tag.startsWith(image)));

        if (!imageExists) {
            PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);
            PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
                @Override
                public void onNext(PullResponseItem item) {
                    System.out.println("download image: " + item.getStatus());
                    super.onNext(item);
                }
            };
            try {
                pullImageCmd
                        .exec(pullImageResultCallback)
                        .awaitCompletion();
            } catch (InterruptedException e) {
                System.out.println("pullImage");
                throw new RuntimeException(e);
            }
            System.out.println(image + "Image download completed.");
        } else {
            System.out.println(image + " Image already exists.");
        }

        // Step 5: mount 挂载, 把UUID文件夹挂载到 /app
        //String containerId = MountUtil1.mount(dockerClient, image, linuxUserCodeParentPath);
        //MOUNT2
        /*String countainerId = MountUtil2.mount(dockerClient, image, linuxPath3);
        System.out.println("FinalCountainerId: "+countainerId);*/

        // Step 5: Copy .class file to container
        String containerId = null;
        try {
            containerId = CopyUtil.copyFile(dockerClient, image, userCodeParentPath);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        // Step 6: docker exec optimistic_pare java -cp /app Main 1 3
        List<ProcessExecuteMsg> processExecuteMsgList = new ArrayList<>();
        for (String inputArgs : inputList) {
            String[] inputArgsArray = inputArgs.split(" ");
            String[] cmdArray = ArrayUtil.append(new String[]{"java", "-cp", "/app", "Main"}, inputArgsArray);
            // create execute command
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withCmd(cmdArray)
                    .withAttachStdin(true).withAttachStderr(true).withAttachStdout(true)
                    .exec();
            String execCreateId = execCreateCmdResponse.getId();
            System.out.println("create execute command: " + execCreateCmdResponse
                    + "create execute commandId: " + execCreateId);
            ProcessExecuteMsg processExecuteMsg = new ProcessExecuteMsg();
            final String[] message = {null};
            final String[] errorMessage = {null};
            //final boolean[] timeout = {true};
            final AtomicBoolean timeout = new AtomicBoolean(true);

            // create ExecStartResultCallback
            ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback() {
                @Override
                public void onComplete() {
                    timeout.set(false); // 如果在超时前完成，则 onComplete 会被调用，将 timeout 设为 false
                    super.onComplete();
                }

                @Override
                public void onNext(Frame frame) {
                    StreamType streamType = frame.getStreamType();
                    // error
                    if (streamType.STDERR.equals(streamType)) {
                        errorMessage[0] = "error: " + new String(frame.getPayload());
                        System.out.println("error: " + errorMessage[0]);
                    }
                    message[0] = new String(frame.getPayload())
                            .replace("\n", "")
                            .replace("\r", "");
                    System.out.println(message[0]);
                    super.onNext(frame);
                }
            };
            // memory watch
            /*final long[] maxMemory = {0L};
            StatsCmd statsCmd = dockerClient.statsCmd(containerId);
            ResultCallback<Statistics> resultCallback = statsCmd.exec(new ResultCallback<Statistics>() {
                @Override
                public void onNext(Statistics statistics) {
                    System.out.println("memory usage: " + statistics.getMemoryStats().getUsage());
                    maxMemory[0] = Math.max(maxMemory[0],statistics.getMemoryStats().getUsage());
                }
                @Override
                public void onComplete() {
                    try {
                        this.close(); // 确保在完成时关闭回调
                    } catch (IOException e) {
                        System.err.println("Error closing memory stats callback: " + e.getMessage());
                    }
                }
                @Override
                public void onStart(Closeable closeable) {
                }
                @Override
                public void onError(Throwable throwable) {
                }

                @Override
                public void close() throws IOException {
                }
            });
            statsCmd.exec(resultCallback);*/
            //execute
            try {
                //time watch
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                dockerClient.execStartCmd(execCreateId)
                        .exec(execStartResultCallback)
                        .awaitCompletion(TIME_OUT, TimeUnit.MILLISECONDS);
                stopWatch.stop();
                time = stopWatch.getLastTaskTimeMillis();
                //statsCmd.close();
                System.out.println(timeout.get() ? "timeout? YES" : "timeout? NO");
                processExecuteMsg.setMessage(message[0]);
                processExecuteMsg.setErrorMessage(errorMessage[0]);
                processExecuteMsg.setTime(time);
                //processExecuteMsg.setMemory(maxMemory[0]);
                processExecuteMsgList.add(processExecuteMsg);
                System.out.println("processExecuteMsg: " + processExecuteMsg);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                // 确保关闭资源
                /*try {
                    dockerClient.stopContainerCmd(containerId).exec();
                    System.out.println("Container stopped.");
                    // 确保关闭内存监控的回调和 StatsCmd
                    if (resultCallback != null) {
                        resultCallback.close();
                        System.out.println("Memory monitoring callback closed.");
                    }
                    if (statsCmd != null) {
                        statsCmd.close();
                        System.out.println("StatsCmd closed.");
                    }
                } catch (IOException e) {
                    System.err.println("Error closing resources: " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("Error stopping or removing container: " + e.getMessage());
                }*/
            }
        }

        //4. 处理utils执行程序的返回值，并封装sandbox整体的返回值
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        //caculate time
        long maxTime = 0;
        List<String> outputList = new ArrayList<>();
        for (ProcessExecuteMsg processExecuteMsg : processExecuteMsgList) {
            String errorMessage = processExecuteMsg.getErrorMessage();
            if (errorMessage != null && errorMessage != "") {
                executeCodeResponse.setMessage(errorMessage);
                //status =3 FAILED
                executeCodeResponse.setStatus(3);
                break;
            }
            outputList.add(processExecuteMsg.getMessage());
            Long time = processExecuteMsg.getTime();
            if (time != null) {
                maxTime = Math.max(maxTime, time);
            }
        }

        executeCodeResponse.setOutputList(outputList);
        if (outputList.size() == processExecuteMsgList.size()) {
            executeCodeResponse.setStatus(1);
        }
        //sandbox doesn't response for judge messages
        JudgeInfo judgeInfo = new JudgeInfo();
        //judgeInfo.setMessage(); //judgeInfo.setMemory();
        judgeInfo.setTime(maxTime);
        //judgeInfo.setMemory(maxMemory);
        executeCodeResponse.setJudgeInfo(judgeInfo);

        //5. 文件清理
        if (FileUtil.getAbsolutePath(userCodeParentPath) != null) {
            System.out.println(FileUtil.getAbsolutePath(userCodeParentPath));
            boolean result = FileUtil.del(userCodeParentPath);
            System.out.println("DELETE temcode: " + (result ? "SUCCESS" : "FAILED"));
        }
        return executeCodeResponse;
    }

    //e handle
    private ExecuteCodeResponse getErrorResponse(Throwable e) {
        ExecuteCodeResponse response = new ExecuteCodeResponse();
        response.setOutputList(new ArrayList<>());
        response.setMessage(e.getMessage());
        response.setStatus(2);
        response.setJudgeInfo(null);

        return response;
    }

}
