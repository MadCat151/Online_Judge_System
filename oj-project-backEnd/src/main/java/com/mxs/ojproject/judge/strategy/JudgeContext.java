package com.mxs.ojproject.judge.strategy;

import com.mxs.ojproject.model.dto.question.JudgeCase;
import com.mxs.ojproject.judge.codesandbox.model.JudgeInfo;
import com.mxs.ojproject.model.entity.Question;
import com.mxs.ojproject.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * Define the parameters passed in the strategy
 */
@Data
public class JudgeContext {

    private JudgeInfo judgeInfo;
    private List<String> inputList;
    private List<String> outputList;
    private List<JudgeCase> judgeCaseList;
    private Question question;
    private QuestionSubmit questionSubmit;
}
