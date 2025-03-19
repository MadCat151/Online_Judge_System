package com.mxs.mxscodesandbox.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.async.ResultCallbackTemplate;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class DockerDemo4CRUD {
    public static void main(String[] args) throws InterruptedException {

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


        // Step 4: Pull the nginx image
        String image = "nginx:latest";

        /*PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);

        pullImageCmd.exec(new ResultCallbackTemplate<PullImageResultCallback, PullResponseItem>() {
            @Override
            public void onNext(PullResponseItem item) {
                System.out.println("Status: " + item.getStatus());
            }
        }).awaitCompletion();

        System.out.println(image + "Image download completed.");*/

        /*List<Image> imageList = dockerClient.listImagesCmd().exec();
        for(Image image: imageList){
            System.out.println("Image ID: " + image.getId()
                    + " Repository: " + Arrays.toString(image.getRepoTags()));
        }*/

        // Step 5: Create a container using the same String image

        /*CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image);
        CreateContainerResponse createContainerResponse = containerCmd.exec();
        System.out.println("createContainerResponseId: " + createContainerResponse.getId());*/

        // Step 6: Get container list
        ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();
        List<Container> containerList = listContainersCmd.withShowAll(true).exec();
        for (Container container : containerList) {
            String containerId = container.getId();
            if (!(container.getStatus().contains("Up"))) {

                // start
                dockerClient.startContainerCmd(containerId).exec();
                // hold
                //Thread.sleep(10000);
                // logs
                /*dockerClient.logContainerCmd(containerId)
                        .withStdOut(true)
                        .withStdErr(true)
                        .exec(new ResultCallback.Adapter<Frame>() {
                            public void onNext(Frame frame) {
                                System.out.println(new String(frame.getPayload()));
                            }
                        })
                        .awaitCompletion();*/
            }
        }
        // list all containers
        listContainersCmd = dockerClient.listContainersCmd();
        containerList = listContainersCmd.withShowAll(true).exec();
        for (Container container : containerList) {
            String containerId = container.getId();

            System.out.println("ContainerId: " + containerId
                    + " image: " + container.getImage()
                    + " ContainerName: " + Arrays.toString(container.getNames())
                    + " Status: " + container.getStatus());
        }

        // Step 7: Start container
        //dockerClient.startContainerCmd(containerId).exec();

        // Step 8: logs
        /*dockerClient.logContainerCmd("10737945c2f1a19e739da73bdb4aab85ef89070f9849e22d97971467bb44a87c")
                .withStdOut(true)
                .withStdErr(true)
                .exec(new ResultCallback.Adapter<Frame>() {
                    public void onNext(Frame frame) {
                        System.out.println(new String(frame.getPayload()));
                    }
                })
                .awaitCompletion();*/

        // Step 9: Delete container
        String containerId = "406009deac2df3fca40204f23686ffa676a4ed15a5d7a89932d4e84bf7e6d9fd";
        //dockerClient.removeContainerCmd(containerId).exec();
        //dockerClient.removeContainerCmd(containerId).withForce(true).exec();

        // Step 10: Delete image
        //dockerClient.removeImageCmd(image);
    }
}
