package com.mxs.ojproject.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mxs.ojproject.model.dto.question.QuestionQueryRequest;
import com.mxs.ojproject.model.entity.Question;
import com.mxs.ojproject.model.entity.Question;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mxs.ojproject.model.vo.QuestionVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author Lenovo
* @description 针对表【question(Question table)】的数据库操作Service
* @createDate 2024-09-23 15:28:40
*/
public interface QuestionService extends IService<Question> {
    //Validation
    void validQuestion(Question question, boolean add);

    //Get Query Conditions (specific to MyBatis Plus)
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);

    //Get QuestionVO (req with sanitized information for users, excluding judgeCase, etc.)
    QuestionVO getQuestionVO(Question question, HttpServletRequest request);

    //Get Paginated Question Wrapper
    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);
}
