package com.mxs.ojproject.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * Question submission table
 * @TableName question_submit
 */
@TableName(value ="question_submit")
@Data
public class QuestionSubmit implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Programming language
     */
    private String language;

    /**
     * Code
     */
    private String code;

    /**
     * Judge information (JSON object)
     */
    private String judgeInfo;

    /**
     * 0 - Pending, 1 - Judging, 2 - Success, 3 - Failure
     */
    private Integer status;

    /**
     * Question ID
     */
    private Long questionId;

    /**
     * Who submitted
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