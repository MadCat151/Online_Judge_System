/*
package com.mxs.ojproject.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mxs.ojproject.common.BaseResponse;
import com.mxs.ojproject.common.ErrorCode;
import com.mxs.ojproject.common.ResultUtils;
import com.mxs.ojproject.exception.BusinessException;
import com.mxs.ojproject.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.mxs.ojproject.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.mxs.ojproject.model.entity.QuestionSubmit;
import com.mxs.ojproject.model.entity.User;
import com.mxs.ojproject.model.vo.QuestionSubmitVO;
import com.mxs.ojproject.service.QuestionSubmitService;
import com.mxs.ojproject.service.UserService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Deprecated
@RestController
@RequestMapping("/question_submit")
@Slf4j
public class QuestionSubmitController {

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private UserService userService;

    */
/**
     * Submit
     *//*

    @PostMapping("/")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
                                               HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // loginUser
        final User loginUser = userService.getLoginUser(request);
        long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(questionSubmitId);
    }

    */
/**
     * listQuestonSubmitByPage
     *//*

    @PostMapping("/list/page")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody
                                                                         QuestionSubmitQueryRequest request,
                                                                         HttpServletRequest httpServletRequest) {

        long current = request.getCurrent();
        long size = request.getPageSize();
        //这个page是原始分页，未脱敏
        Page<QuestionSubmit> page = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(request));

        final User loginUser = userService.getLoginUser(httpServletRequest);
        //这里脱敏后才是VO
        Page<QuestionSubmitVO> safePage = questionSubmitService.getQuestionSubmitVOPage(page, loginUser);

        return ResultUtils.success(safePage);
    }
}
*/
