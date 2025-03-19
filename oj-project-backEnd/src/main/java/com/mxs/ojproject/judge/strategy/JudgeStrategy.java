package com.mxs.ojproject.judge.strategy;

import com.mxs.ojproject.judge.codesandbox.model.JudgeInfo;

public interface JudgeStrategy {

    JudgeInfo doJudge(JudgeContext judgeContext);
}
