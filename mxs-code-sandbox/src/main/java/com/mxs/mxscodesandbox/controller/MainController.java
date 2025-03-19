package com.mxs.mxscodesandbox.controller;

import com.mxs.mxscodesandbox.CodeSandbox;
import com.mxs.mxscodesandbox.model.ExecuteCodeRequest;
import com.mxs.mxscodesandbox.model.ExecuteCodeResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class MainController {

    private static final String AUTH_REQUEST_HEADER = "AUTH";

    private static final String AUTH_REQUEST_SECRET = "MAD";

    @Autowired
    @Qualifier("JavaDockerCodeSandboxImpl")
    private CodeSandbox codeSandbox;

    @GetMapping("/health2")
    public String healthCheck() {
        return "OK";
    }

    @PostMapping("/executeCode")
    public ExecuteCodeResponse executeCode(@RequestBody ExecuteCodeRequest executeCodeRequest,
                                           HttpServletRequest request, HttpServletResponse response) {
        String authHeader = request.getHeader(AUTH_REQUEST_HEADER);
        if (!authHeader.equals(AUTH_REQUEST_SECRET)) {
            response.setStatus(483);
            return null;
        }
        if (executeCodeRequest == null) {
            throw new RuntimeException("executeCodeRequest is missing");
        }

        return codeSandbox.executeCode(executeCodeRequest);
    }
}
