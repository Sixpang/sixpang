package org.sixpang.authservice.application.client.dto;

public enum UserStatus {
    PENDING,
    APPROVED,
    REJECTED;

    public boolean isApproved() {
        return this == APPROVED;
    }

    public boolean isRejected() {
        return this == REJECTED;
    }
}
