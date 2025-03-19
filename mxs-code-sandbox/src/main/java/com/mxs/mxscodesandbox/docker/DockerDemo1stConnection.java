package com.mxs.mxscodesandbox.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.mxs.mxscodesandbox.config.config.DockerProperties;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

public class DockerDemo1stConnection {
    public static void main(String[] args) {

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

        // Step 4: Test connection and execute a command. 测试连接并执行命令
        try {
            /*PingCmd pingCmd = dockerClient.pingCmd();
            pingCmd.exec();*/
            dockerClient.pingCmd().exec();  // Sends a ping to the Docker daemon
            System.out.println("Docker is reachable.");
        } catch (Exception e) {
            System.err.println("Failed to connect to Docker: " + e.getMessage());
        }
    }
}
