package com.mxs.ojproject.judge.codesandbox.impl;

import com.mxs.ojproject.judge.codesandbox.CodeSandbox;
import com.mxs.ojproject.judge.codesandbox.model.ExecuteCodeRequest;
import com.mxs.ojproject.judge.codesandbox.model.ExecuteCodeResponse;
import org.springframework.stereotype.Service;

@Service("thirdPartyCodeSandbox")
public class ThirdPartyCodeSandbox implements CodeSandbox {

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("ThirdPartyCodeSandbox");
        return null;
    }
}
