package com.mxs.mxscodesandbox.model;

import lombok.Data;

/**
 * Judging Information
 */
@Data
public class JudgeInfo {

    /**
     * JudgeInfoMessageEnum. Program Execution Information
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
