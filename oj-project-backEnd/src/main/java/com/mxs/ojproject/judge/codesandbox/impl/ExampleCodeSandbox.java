package com.mxs.ojproject.judge.codesandbox.impl;

import com.mxs.ojproject.judge.codesandbox.CodeSandbox;
import com.mxs.ojproject.judge.codesandbox.model.ExecuteCodeRequest;
import com.mxs.ojproject.judge.codesandbox.model.ExecuteCodeResponse;
import com.mxs.ojproject.judge.codesandbox.model.JudgeInfo;
import com.mxs.ojproject.model.enums.JudgeInfoMessageEnum;
import com.mxs.ojproject.model.enums.QuestionSubmitStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service("exampleCodeSandbox")
public class ExampleCodeSandbox  implements CodeSandbox {

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();


        ExecuteCodeResponse response = new ExecuteCodeResponse();
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
        judgeInfo.setTime(100L);
        judgeInfo.setMemory(100l);

        response.setJudgeInfo(judgeInfo);

        response.setMessage("SUCCESS");
        response.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        response.setOutputList(inputList);

        return response;
    }
}
