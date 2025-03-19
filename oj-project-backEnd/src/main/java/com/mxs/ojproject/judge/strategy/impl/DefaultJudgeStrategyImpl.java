package com.mxs.ojproject.judge.strategy.impl;

import java.util.List;

import com.mxs.ojproject.model.entity.Question;
import com.mxs.ojproject.model.entity.QuestionSubmit;

import cn.hutool.json.JSONUtil;
import com.mxs.ojproject.judge.strategy.JudgeContext;
import com.mxs.ojproject.judge.strategy.JudgeStrategy;
import com.mxs.ojproject.model.dto.question.JudgeCase;
import com.mxs.ojproject.model.dto.question.JudgeConfig;
import com.mxs.ojproject.judge.codesandbox.model.JudgeInfo;
import com.mxs.ojproject.model.enums.JudgeInfoMessageEnum;

public class DefaultJudgeStrategyImpl implements JudgeStrategy {
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {

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
        Long memory = judgeInfo.getMemory();
        Long time = judgeInfo.getTime();

        if (memory > standardMemoryLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfo.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfo;
        }
        if (time > standardTimeLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeInfo.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfo;
        }


        return judgeInfo;
    }
}
