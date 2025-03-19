package com.mxs.mxscodesandbox.config.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@Component
@ConfigurationProperties(prefix = "docker") // 绑定 "docker" 前缀的配置
public class DockerProperties {
    private String host;
}