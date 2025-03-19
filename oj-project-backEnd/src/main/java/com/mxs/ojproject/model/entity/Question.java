package com.mxs.ojproject.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * Question table
 * @TableName question
 */
@TableName(value ="question")
@Data
public class Question implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
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
    private String tags;

    /**
     * Answer to the question
     */
    private String answer;

    /**
     * Number of submissions
     */
    private Integer submitNum;

    /**
     * Number of accepted submissions
     */
    private Integer acceptNum;

    /**
     * Judge cases (JSON array: List)
     */
    private String judgeCase;

    /**
     * Judge configuration (JSON object)
     */
    private String judgeConfig;

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
     * Is deleted
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}