package com.mxs.ojproject.judge;

import cn.hutool.json.JSONUtil;
import com.mxs.ojproject.common.ErrorCode;
import com.mxs.ojproject.exception.BusinessException;
import com.mxs.ojproject.judge.codesandbox.CodeSandboxFactory;
import com.mxs.ojproject.judge.codesandbox.model.ExecuteCodeRequest;
import com.mxs.ojproject.judge.codesandbox.model.ExecuteCodeResponse;
import com.mxs.ojproject.judge.strategy.JudgeContext;
import com.mxs.ojproject.judge.strategy.JudgeManager;
import com.mxs.ojproject.judge.strategy.JudgeStrategy;
import com.mxs.ojproject.model.dto.question.JudgeCase;
import com.mxs.ojproject.judge.codesandbox.model.JudgeInfo;
import com.mxs.ojproject.model.entity.Question;
import com.mxs.ojproject.model.entity.QuestionSubmit;
import com.mxs.ojproject.model.enums.JudgeInfoMessageEnum;
import com.mxs.ojproject.model.enums.QuestionSubmitStatusEnum;
import com.mxs.ojproject.service.QuestionService;
import com.mxs.ojproject.service.QuestionSubmitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JudgeServiceImpl implements JudgeService {

    @Autowired
    QuestionService questionService;

    @Autowired
    QuestionSubmitService questionSubmitService;

    @Autowired
    CodeSandboxFactory factory;

    @Autowired
    JudgeManager judgeManager;

    @Value("${codeSandbox.type}")
    private String codeType;

    @Override
    public Integer doJudge(Long questionSubmitId) {
        System.out.println("in doJudge. questionSubmitId: " + questionSubmitId);

        //1. pass Id, find submit and question
        QuestionSubmit submit = questionSubmitService.getById(questionSubmitId);
        System.out.println("QuestionSubmit: " + submit);
        if (questionSubmitId == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Not Found");
        }
        Long id = submit.getQuestionId();
        Question question = questionService.getById(id);
        System.out.println("Question: " + question);
        if (question == null) {
            throw new BusinessException(
                    ErrorCode.NOT_FOUND_ERROR, "question doesn't exist");
        }

        //2. check status & change status to RUNNING.getValue() == 1
        if (submit.getStatus() != QuestionSubmitStatusEnum.WAITING.getValue()) {
            throw new BusinessException(
                    ErrorCode.OPERATION_ERROR, "The problem is currently being judged.");
        }

        //3. make ExecuteCodeRequest request
        String language = submit.getLanguage();
        String code = submit.getCode();
        //Question: Judge cases (JSON array: List)
        //List<String> inputList = Arrays.asList("1 2", "3 4");
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = new ArrayList<>();
        for (JudgeCase judgeCase : judgeCaseList) {
            inputList.add(judgeCase.getInput());
        }

        //4. execute sandbox
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();

        ExecuteCodeResponse executeCodeResponse = factory.factoryWithProxy(
                codeType, executeCodeRequest);

        List<String> outputList = executeCodeResponse.getOutputList();
        Integer status = executeCodeResponse.getStatus();
        System.out.println("status after executing sandbox: " + status);
        //5. judge response
        JudgeStrategy judgeStrategy = judgeManager.manageJudge(language);

        JudgeContext context = new JudgeContext();
        context.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        context.setInputList(inputList);
        context.setOutputList(outputList);
        context.setJudgeCaseList(judgeCaseList);
        context.setQuestion(question);
        //context.setQuestionSubmit();
        JudgeInfo judgeInfo = judgeStrategy.doJudge(context);
        System.out.println("JudgeInfo: " + judgeInfo);
        //6. update new submitUpdate
        QuestionSubmit submitUpdate = new QuestionSubmit();
        submitUpdate.setId(questionSubmitId);
        if (judgeInfo.getMessage().equals(JudgeInfoMessageEnum.ACCEPTED.getValue())) {
            // status =2
            status = QuestionSubmitStatusEnum.SUCCEED.getValue();
        } else {
            // status =3
            status = QuestionSubmitStatusEnum.FAILED.getValue();
        }
        submitUpdate.setStatus(status);
        submitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));

        Boolean update = questionSubmitService.updateById(submitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "SYSTEM_ERROR");
        }
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        System.out.println("6. QuestionSubmit:" + questionSubmit);
        status = questionSubmit.getStatus();
        System.out.println("status: " + QuestionSubmitStatusEnum.getEnumByValue(status).getText());
        return status;
    }
}
