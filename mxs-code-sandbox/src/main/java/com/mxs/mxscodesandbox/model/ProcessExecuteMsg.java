package com.mxs.mxscodesandbox.model;

import lombok.Data;

@Data
public class ProcessExecuteMsg {

    private Integer exitValue;

    private String message;

    private  String errorMessage;

    private Long time;

    private Long memory;

}
