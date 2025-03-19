package com.mxs.mxscodesandbox;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import com.mxs.mxscodesandbox.model.ExecuteCodeRequest;
import com.mxs.mxscodesandbox.model.ExecuteCodeResponse;
import com.mxs.mxscodesandbox.model.JudgeInfo;
import com.mxs.mxscodesandbox.model.ProcessExecuteMsg;
import com.mxs.mxscodesandbox.utils.ProcessUtils;
import org.springframework.stereotype.Service;

@Service
public class JavaNativeCodeSandboxImpl implements CodeSandbox {

    private static final String GLOBAL_CODE_DIR_NAME = "tmpcode";

    private static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";

    public static void main(String[] args) {
        JavaNativeCodeSandboxImpl sandbox = new JavaNativeCodeSandboxImpl();
        ExecuteCodeRequest request = new ExecuteCodeRequest();
        request.setInputList(Arrays.asList("1 2", "1 3"));
        //get User's code
        String code = ResourceUtil.readStr("testcode/simple/Main.java", StandardCharsets.UTF_8);
        request.setCode(code);
        request.setLanguage("java");

        ExecuteCodeResponse response = sandbox.executeCode(request);
        System.out.println(response);
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
        //FileUtil is in Hutool
        if (!FileUtil.exist(globalCodePathName)) {
            FileUtil.mkdir(globalCodePathName);
        }

        //用户代码存放,父级目录，每个都隔离开
        String userCodeParentPath = globalCodePathName + File.separator + UUID.randomUUID();
        //用户 Main.java 文件的存放路径
        String userCodePath = userCodeParentPath + File.separator + GLOBAL_JAVA_CLASS_NAME;

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
        } catch (Exception e) {
            return getErrorResponse(e);
        }

        //3. 执行.class文件，得到输出结果
        List<ProcessExecuteMsg> processExecuteMsgList = new ArrayList<>();
        for (String inputArgs : inputList) {
            String runCmd = String.format("java -cp %s Main %s", userCodeParentPath, inputArgs);
            try {
                Process runProcess = Runtime.getRuntime().exec(runCmd);
                //apply ProcessUtils
                ProcessExecuteMsg executeMsg = ProcessUtils.doProcessMsg(runProcess, "run: ");
                System.out.println(executeMsg);
                processExecuteMsgList.add(executeMsg);
            } catch (Exception e) {
                return getErrorResponse(e);
            }
        }
        //4. 处理utils执行程序的返回值，并封装sandbox整体的返回值
        ExecuteCodeResponse response = new ExecuteCodeResponse();
        //caculate time
        long maxTime = 0;
        List<String> outputList = new ArrayList<>();
        for (ProcessExecuteMsg processExecuteMsg : processExecuteMsgList) {
            String errorMessage = processExecuteMsg.getErrorMessage();
            if (errorMessage != null && errorMessage != "") {
                response.setMessage(errorMessage);
                //status =3 FAILED
                response.setStatus(3);
            }
            outputList.add(processExecuteMsg.getMessage());
            Long time = processExecuteMsg.getTime();
            if (time != null) {
                maxTime = Math.max(maxTime, time);
            }
        }
        response.setMessage("");
        response.setOutputList(outputList);
        if (outputList.size() == processExecuteMsgList.size()) {
            response.setStatus(1);
        }
        //sandbox doesn't response for judge messages
        JudgeInfo judgeInfo = new JudgeInfo();
        //judgeInfo.setMessage(); //judgeInfo.setMemory();
        judgeInfo.setTime(maxTime);
        response.setJudgeInfo(judgeInfo);

        //5. 文件清理
        if(FileUtil.getAbsolutePath(userCodeParentPath)!=null){
            System.out.println(FileUtil.getAbsolutePath(userCodeParentPath));
            boolean result = FileUtil.del(userCodeParentPath);
            System.out.println("DELETE temcode: "+(result? "SUCCESS":"FAILED"));
        }

        return response;
    }

    //e handle
    private ExecuteCodeResponse getErrorResponse(Throwable e){
        ExecuteCodeResponse response = new ExecuteCodeResponse();
        response.setOutputList(new ArrayList<>());
        response.setMessage(e.getMessage());
        response.setStatus(2);
        response.setJudgeInfo(null);

        return response;
    }

}
