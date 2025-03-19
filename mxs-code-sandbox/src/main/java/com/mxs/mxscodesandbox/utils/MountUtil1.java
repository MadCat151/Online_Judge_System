package com.mxs.mxscodesandbox.utils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;

public class MountUtil1 {

    public static String mount(DockerClient dockerClient, String image, String linuxuserCodeParentPath){
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image);
        HostConfig hostConfig = new HostConfig();
        hostConfig.withMemory(100 * 1000 * 1000L);
        hostConfig.withMemorySwap(0L);
        hostConfig.withCpuCount(1L);
        System.out.println("LinuxuserCodeParentPath: " + linuxuserCodeParentPath);

        CreateContainerResponse createContainerResponse = containerCmd
                .withHostConfig(hostConfig)
                .withNetworkDisabled(true)
                .withReadonlyRootfs(true)
                .withAttachStdin(true)
                .withAttachStderr(true)
                .withAttachStdout(true)
                .withTty(true)
                .exec();

        String containerId = createContainerResponse.getId();
        System.out.println("createContainerResponseId: " + containerId);


        // start
        dockerClient.startContainerCmd(containerId).exec();
        // wait for container to start running
        InspectContainerResponse containerInfo = dockerClient.inspectContainerCmd(containerId).exec();
        while (!containerInfo.getState().getRunning()) {
            try {
                Thread.sleep(1000); // Wait a second before checking again
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            containerInfo = dockerClient.inspectContainerCmd(containerId).exec();
        }
        return createContainerResponse.getId();
    }
}
