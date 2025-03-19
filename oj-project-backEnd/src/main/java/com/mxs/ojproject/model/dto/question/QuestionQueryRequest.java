package com.mxs.ojproject.model.dto.question;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.mxs.ojproject.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionQueryRequest extends PageRequest implements Serializable {

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
     * Answer to the question
     */
    private String answer;

    /**
     * Number of accepted submissions
     */
    private Integer acceptNum;

    /**
     * User ID who created the question
     */
    private Long userId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}