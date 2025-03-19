package com.mxs.ojproject.judge.codesandbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeResponse {

    private List<String> outputList;

    /**
     * Interface Information
     */
    private String message;

    /**
     * Execution Status
     */
    private Integer status;

    /**
     * Judge Information
     */
    private JudgeInfo judgeInfo;
}
