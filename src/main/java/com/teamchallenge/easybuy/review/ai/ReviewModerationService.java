package com.teamchallenge.easybuy.review.ai;

public interface ReviewModerationService {

    /**
     * Validates review text against community guidelines.
     *
     * @throws ReviewModerationException if the content violates guidelines
     */
    void moderate(String text);
}
