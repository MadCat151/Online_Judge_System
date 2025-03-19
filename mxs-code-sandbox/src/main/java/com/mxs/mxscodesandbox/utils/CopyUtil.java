package com.mxs.mxscodesandbox.utils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.core.command.ExecStartResultCallback;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;

public class CopyUtil {

    public static String copyFile(DockerClient dockerClient, String image, String hostPath) throws URISyntaxException {
        URL seccompUrl = CopyUtil.class.getClassLoader().getResource("com/mxs/mxcodesandbox/config/seccomp-profile.json");
        // Step 5-1: Create container
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image);
        HostConfig hostConfig = new HostConfig();
        // 获取 seccomp 配置文件的路径
        if (seccompUrl != null) {
            String seccompPath = Paths.get(seccompUrl.toURI()).toString();
            hostConfig.withSecurityOpts(Arrays.asList("seccomp=" + seccompPath));
        } else {
            hostConfig.withSecurityOpts(Arrays.asList("seccomp=unconfined")); //default
        }
        hostConfig.withMemory(100 * 1000 * 1000L);
        hostConfig.withMemorySwap(0L);
        hostConfig.withCpuCount(1L);
        System.out.println("hostPath: " + hostPath);

        CreateContainerResponse container = containerCmd
                .withHostConfig(hostConfig)
                .withNetworkDisabled(true)
                .withReadonlyRootfs(false)
                .withAttachStdin(true)
                .withAttachStderr(true)
                .withAttachStdout(true)
                .withCmd("tail", "-f", "/dev/null") // Persistent running command
                .exec();

        /*CreateContainerResponse container = dockerClient.createContainerCmd(image)
                .withCmd("tail", "-f", "/dev/null") // Persistent running command
                .withNetworkDisabled(true)
                .withReadonlyRootfs(false)
                .exec();*/

        String containerId = container.getId();
        System.out.println("Created container with ID: " + containerId);

        // Step 5-2: Start container
        dockerClient.startContainerCmd(containerId).exec();
        System.out.println("Container is running with tail -f /dev/null to keep it active.");
        // Step 5-3: Wait for container to start running
        InspectContainerResponse containerInfo = dockerClient.inspectContainerCmd(containerId).exec();
        while (!containerInfo.getState().getRunning()) {
            try {
                Thread.sleep(1000); // Wait a second before checking again
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            containerInfo = dockerClient.inspectContainerCmd(containerId).exec();
        }

        // Step 5-4: Create /app directory inside the container
        String execCreateId = dockerClient.execCreateCmd(containerId)
                .withCmd("mkdir", "-p", "/app")
                .withAttachStdout(true)
                .withAttachStderr(true)
                .exec()
                .getId();

        // Start the command to ensure it's actually executed
        try {
            dockerClient.execStartCmd(execCreateId)
                    .exec(new ExecStartResultCallback(System.out, System.err))
                    .awaitCompletion();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("/app directory created in container.");
        // Step 5-5: Copy files into the container
        String localFilePath = hostPath + "/Main.class"; // 本地文件路径
        dockerClient.copyArchiveToContainerCmd(containerId)
                .withHostResource(localFilePath)
                .withRemotePath("/app/")
                .exec();

        // Step 5-6: Run a command to verify file copy
        String execId = dockerClient.execCreateCmd(containerId)
                .withCmd("ls", "-la", "/app")
                .withAttachStdout(true)
                .withAttachStderr(true)
                .exec()
                .getId();


        System.out.println("/app directory created in container.");
        return containerId;
    }
}
