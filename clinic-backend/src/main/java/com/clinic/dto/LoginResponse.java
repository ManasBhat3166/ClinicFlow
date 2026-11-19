package com.clinic.dto;

public class LoginResponse {

    private String token;
    private String tokenType;
    private Long userId;
    private Long clinicId;
    private String name;
    private String mobile;
    private String role;

    public LoginResponse(
            String token,
            Long userId,
            Long clinicId,
            String name,
            String mobile,
            String role) {

        this.token = token;
        this.tokenType = "Bearer";
        this.userId = userId;
        this.clinicId = clinicId;
        this.name = name;
        this.mobile = mobile;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getClinicId() {
        return clinicId;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public String getRole() {
        return role;
    }
}