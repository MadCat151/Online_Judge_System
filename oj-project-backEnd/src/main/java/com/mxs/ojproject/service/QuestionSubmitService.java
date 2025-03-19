package com.mxs.ojproject.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mxs.ojproject.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.mxs.ojproject.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.mxs.ojproject.model.entity.QuestionSubmit;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mxs.ojproject.model.entity.User;
import com.mxs.ojproject.model.vo.QuestionSubmitVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
* @author Lenovo
* @description 针对表【question_submit(Question submission table)】的数据库操作Service
* @createDate 2024-09-23 15:31:25
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {

    /**
     * 题目提交
     *
     * @param questionSubmitAddRequest 题目提交信息
     * @param loginUser
     * @return
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

    /**
     * 脱敏三步
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);

    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser);
}
