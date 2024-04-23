package com.pweb.springBackend.DTOs.requests;

import com.pweb.springBackend.enums.FeedbackType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackDTO {
    private FeedbackType feedbackType;
    private Integer satisfactionLevel;
    private Boolean acceptTerms;
    private String comment;

    public FeedbackDTO() {
    }

    public FeedbackDTO(FeedbackType feedbackType, Integer satisfactionLevel, Boolean acceptTerms, String comment) {
        this.feedbackType = feedbackType;
        this.satisfactionLevel = satisfactionLevel;
        this.acceptTerms = acceptTerms;
        this.comment = comment;
    }
}
