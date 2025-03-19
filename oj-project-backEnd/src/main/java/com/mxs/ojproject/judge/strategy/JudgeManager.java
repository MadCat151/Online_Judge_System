package com.mxs.ojproject.judge.strategy;

import com.mxs.ojproject.judge.strategy.impl.DefaultJudgeStrategyImpl;
import com.mxs.ojproject.judge.strategy.impl.JavaLanguageJudgeStrategyImpl;
import com.mxs.ojproject.judge.codesandbox.model.JudgeInfo;
import com.mxs.ojproject.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

@Service
public class JudgeManager {
    public JudgeStrategy manageJudge(String language){

        JudgeStrategy judgeStrategy = new DefaultJudgeStrategyImpl();

        if(language.equals("java")){
            judgeStrategy = new JavaLanguageJudgeStrategyImpl();
        }
        if(language.equals("cpp")){
            judgeStrategy = new JavaLanguageJudgeStrategyImpl();
        }
        if(language.equals("go")){
            judgeStrategy = new JavaLanguageJudgeStrategyImpl();
        }
        return judgeStrategy;
    }

    public JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategyImpl();
        if ("java".equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategyImpl();
        }
        JudgeInfo judgeInfo = judgeStrategy.doJudge(judgeContext);
        return judgeInfo;
    }
}
