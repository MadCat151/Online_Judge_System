package com.mxs.ojproject.model.dto.question;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class QuestionAddRequest implements Serializable {

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
     * Answer to the question
     */
    private String answer;

    /**
     * Judge cases (JSON array)
     */
    private List<JudgeCase> judgeCase;

    /**
     * Judge configuration (JSON object)
     */
    private JudgeConfig judgeConfig;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}