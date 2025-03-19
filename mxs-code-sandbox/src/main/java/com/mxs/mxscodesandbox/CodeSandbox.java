package com.mxs.mxscodesandbox;

import com.mxs.mxscodesandbox.model.ExecuteCodeRequest;
import com.mxs.mxscodesandbox.model.ExecuteCodeResponse;

public interface CodeSandbox {

    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
