package com.mxs.mxscodesandbox.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallbackTemplate;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageCmd;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;

import java.time.Duration;

public class DockerDemo3CreateContainer {
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

        PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);

        pullImageCmd.exec(new ResultCallbackTemplate<PullImageResultCallback, PullResponseItem>() {
            @Override
            public void onNext(PullResponseItem item) {
                System.out.println("Status: " + item.getStatus());
            }
        }).awaitCompletion();

        System.out.println(image + "Image download completed.");

        // Step 5: Create a container using the same String image

        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image);
        CreateContainerResponse exec = containerCmd.exec();
        System.out.println(exec);
    }
}
