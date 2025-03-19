package com.mxs.ojproject.judge.codesandbox;

import com.mxs.ojproject.judge.codesandbox.impl.ExampleCodeSandbox;
import com.mxs.ojproject.judge.codesandbox.impl.RemoteCodeSandbox;
import com.mxs.ojproject.judge.codesandbox.impl.ThirdPartyCodeSandbox;
import com.mxs.ojproject.judge.codesandbox.model.ExecuteCodeRequest;
import com.mxs.ojproject.judge.codesandbox.model.ExecuteCodeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CodeSandboxFactory {
    @Autowired
    private ExampleCodeSandbox exampleCodeSandbox;

    @Autowired
    private RemoteCodeSandbox remoteCodeSandbox;

    @Autowired
    private ThirdPartyCodeSandbox thirdPartyCodeSandbox;

    public static CodeSandbox newInstance(String type) {

        switch (type) {
            case "example":
                return new ExampleCodeSandbox();

            case "remote":
                return new RemoteCodeSandbox();

            case "thirdParty":
                return new ThirdPartyCodeSandbox();

            default:
                return new ExampleCodeSandbox();
        }
    }

    public CodeSandbox newInstance2(String type) {

        switch (type) {
            case "example":
                return exampleCodeSandbox;

            case "remote":
                return remoteCodeSandbox;

            case "thirdParty":
                return thirdPartyCodeSandbox;

            default:
                return exampleCodeSandbox;
        }
    }

    public ExecuteCodeResponse factory(String type, ExecuteCodeRequest request) {

        CodeSandbox codeSandbox = newInstance2(type);

        ExecuteCodeResponse response = codeSandbox.executeCode(request);
        return response;

    }

    public ExecuteCodeResponse factoryWithProxy(String type, ExecuteCodeRequest request) {
        CodeSandbox codeSandbox = newInstance2(type);
        //codeSandbox = new CodeSandboxProxy(codeSandbox);
        CodeSandboxProxy codeSandboxProxy = new CodeSandboxProxy(codeSandbox);

        ExecuteCodeResponse response = codeSandboxProxy.executeCode(request);
        return response;
    }
}