package com.mxs.ojproject.judge.codesandbox;

import com.mxs.ojproject.judge.codesandbox.model.ExecuteCodeRequest;
import com.mxs.ojproject.judge.codesandbox.model.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CodeSandboxProxy implements CodeSandbox {

    private CodeSandbox codeSandbox;

    public CodeSandboxProxy(CodeSandbox codeSandbox) {
        this.codeSandbox = codeSandbox;
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest request) {
        log.info("ExecuteCodeRequest: " + request.toString());
        ExecuteCodeResponse response = codeSandbox.executeCode(request);
        log.info("ExecuteCodeResponse: " + response.toString());
        return response;
    }
}
