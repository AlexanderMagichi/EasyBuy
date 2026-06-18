package com.teamchallenge.easybuy.email.exception;

import lombok.Getter;

import java.time.Duration;
import java.time.OffsetDateTime;

/**
 * Exception thrown when a user requests an email token before the previous
 * rate-limiting window has expired.
 */
@Getter
public class TimeTokenException extends RuntimeException {

    private final String email;

    /**
     * Constructs a new TimeTokenException.
     *
     * @param email      The email address associated with the rate-limited request.
     * @param expireTime The timestamp when the current restriction expires.
     */
    public TimeTokenException(String email, OffsetDateTime expireTime) {
        super(buildMessageError(email, expireTime));
        this.email = email;
    }

    /**
     * Helper method to format the error message with the remaining duration.
     *
     * @param email      The email address.
     * @param expireTime The expiry timestamp.
     * @return A human-readable string indicating the remaining time.
     */
    private static String buildMessageError(String email, OffsetDateTime expireTime) {
        StringBuilder stringBuilder = new StringBuilder();
        Duration remainingTime = Duration.between(OffsetDateTime.now(), expireTime);
        long minutes = remainingTime.toMinutesPart();
        long seconds = remainingTime.toSecondsPart();

        stringBuilder.append("Token for email '").append(email).append("' will be expired after: ");
        if (minutes != 0) {
            stringBuilder.append(minutes).append(" min ");
        }
        stringBuilder.append(seconds).append(" sec");
        return stringBuilder.toString();
    }
}