package com.mxs.ojproject.judge.strategy.impl;

import cn.hutool.json.JSONUtil;
import com.mxs.ojproject.judge.strategy.JudgeContext;
import com.mxs.ojproject.judge.strategy.JudgeStrategy;
import com.mxs.ojproject.model.dto.question.JudgeCase;
import com.mxs.ojproject.model.dto.question.JudgeConfig;
import com.mxs.ojproject.judge.codesandbox.model.JudgeInfo;
import com.mxs.ojproject.model.entity.Question;
import com.mxs.ojproject.model.entity.QuestionSubmit;
import com.mxs.ojproject.model.enums.JudgeInfoMessageEnum;

import java.util.List;
import java.util.Optional;

public class JavaLanguageJudgeStrategyImpl implements JudgeStrategy {
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        //expand judgeContext
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        Question question = judgeContext.getQuestion();
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        //judgeInfoMessageEnum is the result of the Judge
        JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED;

        if (outputList.size() != inputList.size()) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
            judgeInfo.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfo;
        }
        for (int i = 0; i < judgeCaseList.size(); i++) {
            JudgeCase judgeCase = judgeCaseList.get(i);
            //compare the standard answer with the output result
            if (!judgeCase.getOutput().equals(outputList.get(i))) {
                judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                judgeInfo.setMessage(judgeInfoMessageEnum.getValue());
                return judgeInfo;
            }
        }
        // standard answer in Question --> JudgeConfig
        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);

        Long standardMemoryLimit = judgeConfig.getMemoryLimit();
        Long standardTimeLimit = judgeConfig.getTimeLimit();

        //answer from user
        Long memory = Optional.ofNullable(judgeInfo.getMemory()).orElse(0L);
        Long time = Optional.ofNullable(judgeInfo.getTime()).orElse(0L);

        if (memory > standardMemoryLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfo.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfo;
        }
        // Java language plus 10s exp.
        long JAVA_PROGRAM_TIME_COST = 10000;
        if (time > standardTimeLimit + JAVA_PROGRAM_TIME_COST) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeInfo.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfo;
        }
        //message
        judgeInfo.setMessage(judgeInfoMessageEnum.getValue());

        return judgeInfo;
    }
}
