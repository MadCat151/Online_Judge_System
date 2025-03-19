package com.mxs.ojproject.model.dto.questionsubmit;

import com.mxs.ojproject.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;


import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionSubmitQueryRequest extends PageRequest implements Serializable {

    private String language;
    private Integer status;
    private Long questionId;
    private Long userId;

    private static final long serialVersionUID = 1L;
}
