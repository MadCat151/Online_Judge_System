package com.mxs.ojproject.model.dto.question;

import lombok.Data;

@Data
public class JudgeConfig {

    /**
     * Time Limit (ms)
     */
    private Long timeLimit;

    /**
     * Memory Limit (KB)
     */
    private Long memoryLimit;

    /**
     * Stack Limit (KB)
     */
    private Long stackLimit;
}
