package com.mxs.ojproject.model.vo;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.mxs.ojproject.model.dto.question.JudgeConfig;
import com.mxs.ojproject.model.entity.Question;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Return to the frontend. Filter field
 */
@Data
public class QuestionVO implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * Title
     */
    private String title;

    /**
     * Content
     */
    private String content;

    /**
     * Tag list (JSON array)
     */
    private List<String> tags;

    /**
     * Number of submissions
     */
    private Integer submitNum;

    /**
     * Number of accepted submissions
     */
    private Integer acceptNum;

    /**
     * Judge configuration (JSON object)
     */
    private JudgeConfig judgeConfig;

    /**
     * Number of thumbs-up
     */
    private Integer thumbNum;

    /**
     * Number of favorites
     */
    private Integer favourNum;

    /**
     * User ID who created the question
     */
    private Long userId;

    /**
     * Creation time
     */
    private Date createTime;

    /**
     * Update time
     */
    private Date updateTime;

    /**
     * User Info who created the question
     */
    private UserVO userVO;

    private static final long serialVersionUID = 1L;

    /**
     * Wrapper class to object
     *
     * @param questionVO
     * @return
     */
    public static Question voToObj(QuestionVO questionVO) {
        if (questionVO == null) {
            return null;
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionVO, question);
        List<String> tagList = questionVO.getTags();
        if (tagList != null) {
            question.setTags(JSONUtil.toJsonStr(tagList));
        }
        JudgeConfig voJudgeConfig = questionVO.getJudgeConfig();
        if (voJudgeConfig != null) {
            question.setJudgeConfig(JSONUtil.toJsonStr(voJudgeConfig));
        }
        return question;
    }

    /**
     * Object to wrapper class
     *
     * @param question
     * @return
     */
    public static QuestionVO objToVo(Question question) {
        if (question == null) {
            return null;
        }
        QuestionVO questionVO = new QuestionVO();
        BeanUtils.copyProperties(question, questionVO);
        List<String> tagList = JSONUtil.toList(question.getTags(), String.class);
        questionVO.setTags(tagList);
        String judgeConfigStr = question.getJudgeConfig();
        questionVO.setJudgeConfig(JSONUtil.toBean(judgeConfigStr, JudgeConfig.class));
        return questionVO;
    }
}