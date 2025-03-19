package com.mxs.mxscodesandbox.utils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;

public class MountUtil2 {

    public static String mount(DockerClient dockerClient, String image, String LinuxuserCodeParentPath) {
        // Step 1: Create an initial container with writable rootfs
        CreateContainerResponse initialContainer = dockerClient.createContainerCmd(image)
                .withHostConfig(new HostConfig()
                        .withMemory(100 * 1000 * 1000L)
                        .withMemorySwap(0L)
                        .withCpuCount(1L)
                        .withReadonlyRootfs(false)) // Temporarily disable read-only rootfs
                .withCmd("tail", "-f", "/dev/null") // Command to keep container running
                .exec();

        String initialContainerId = initialContainer.getId();
        System.out.println("Initial container created with ID: " + initialContainerId);

    // Step 2: Start the initial container to create the /app directory
        dockerClient.startContainerCmd(initialContainerId).exec();
        System.out.println("Starting initial container...");
    // Step 3: Wait for the container to be in "running" state
        InspectContainerResponse containerInfo = dockerClient.inspectContainerCmd(initialContainerId).exec();
        while (!containerInfo.getState().getRunning()) {
            try {
                Thread.sleep(1000); // Wait a second before checking again
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            containerInfo = dockerClient.inspectContainerCmd(initialContainerId).exec();
        }
        System.out.println("Initial container is now running.");
    // Step 4: Execute mkdir -p /app inside the initial container
        String execCreateId = dockerClient.execCreateCmd(initialContainerId)
                .withCmd("mkdir", "-p", "/app")
                .withAttachStdout(true)
                .withAttachStderr(true)
                .exec()
                .getId();
        // 启动 exec 命令并处理输出
        try {
            dockerClient.execStartCmd(execCreateId).exec(new ResultCallback.Adapter<Frame>() {
                @Override
                public void onNext(Frame frame) {
                    System.out.println(new String(frame.getPayload()));
                }
            }).awaitCompletion();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("/app directory created in the initial container.");

    // Step 5: Stop and remove the initial container
        dockerClient.stopContainerCmd(initialContainerId).exec();
        dockerClient.removeContainerCmd(initialContainerId).exec();
        System.out.println("Initial container removed.");

    // Step 6: Create a new container with read-only rootfs and volume mounted at /app
        CreateContainerResponse finalContainer = dockerClient.createContainerCmd(image)
                .withHostConfig(new HostConfig()
                        .withMemory(100 * 1000 * 1000L)
                        .withMemorySwap(0L)
                        .withCpuCount(1L)
                        .withReadonlyRootfs(true)
                        .withBinds(new Bind(LinuxuserCodeParentPath, new Volume("/app"))))
                .withCmd("tail", "-f", "/dev/null")  // Use tail -f /dev/null to keep the container running
                .exec();

        String finalContainerId = finalContainer.getId();
        System.out.println("Final container created with ID: " + finalContainerId);

    // Step 7: Start the final container
        dockerClient.startContainerCmd(finalContainerId).exec();
        System.out.println("Final container started with /app directory mounted.");

        return finalContainerId;
    }
}
