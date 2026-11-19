package com.clinic.security;

public record AuthenticatedUser(
        Long userId,
        Long clinicId,
        String mobile,
        String role
) {
}