package com.mxs.ojproject.judge.codesandbox.model;

import lombok.Data;

/**
 * Judging Information
 */
@Data
public class JudgeInfo {

    /**
     * Program Execution Information
     */
    private String message;

    /**
     * Memory Usage
     */
    private Long memory;

    /**
     * Time Consumption
     */
    private Long time;
}
