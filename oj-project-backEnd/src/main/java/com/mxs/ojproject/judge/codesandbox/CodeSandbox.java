package com.mxs.ojproject.judge.codesandbox;

import com.mxs.ojproject.judge.codesandbox.model.ExecuteCodeRequest;
import com.mxs.ojproject.judge.codesandbox.model.ExecuteCodeResponse;

public interface CodeSandbox {

    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
