package com.mxs.ojproject.judge.codesandbox.impl;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.mxs.ojproject.common.ErrorCode;
import com.mxs.ojproject.exception.BusinessException;
import com.mxs.ojproject.judge.codesandbox.CodeSandbox;
import com.mxs.ojproject.judge.codesandbox.model.ExecuteCodeRequest;
import com.mxs.ojproject.judge.codesandbox.model.ExecuteCodeResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service("remoteCodeSandbox")
public class RemoteCodeSandbox implements CodeSandbox {

    private static final String AUTH_REQUEST_HEADER = "AUTH";
    private static final String AUTH_REQUEST_SECRET = "MAD";

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("RemoteCodeSandbox");
        String url = "http://localhost:8083/executeCode";
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        //String httpReponseBodyStr = HttpUtil.createPost(url).body(json).execute().body();
        HttpResponse httpResponse = HttpUtil.createPost(url)
                .body(json)
                .header(AUTH_REQUEST_HEADER, AUTH_REQUEST_SECRET)
                .execute();
        String httpReponseBodyStr = httpResponse.body();
        System.out.println("httpReponseBodyStr: " + httpReponseBodyStr);
        if (StringUtils.isBlank(httpReponseBodyStr)) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR,
                    "executeCode remoteSandbox error, message = " + httpReponseBodyStr);
        }
        return JSONUtil.toBean(httpReponseBodyStr, ExecuteCodeResponse.class);
    }
}
