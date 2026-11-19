package com.clinic.dto;

import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;

public class ClinicSettingRequest {

    @NotNull(message = "Clinic ID is required")
    private Long clinicId;

    private Boolean onlineBooking;

    @NotNull(message = "Daily token limit is required")
    private Integer dailyTokenLimit;

    private LocalTime opdStart;

    private LocalTime opdEnd;

    public Long getClinicId() {
        return clinicId;
    }

    public void setClinicId(Long clinicId) {
        this.clinicId = clinicId;
    }

    public Boolean getOnlineBooking() {
        return onlineBooking;
    }

    public void setOnlineBooking(Boolean onlineBooking) {
        this.onlineBooking = onlineBooking;
    }

    public Integer getDailyTokenLimit() {
        return dailyTokenLimit;
    }

    public void setDailyTokenLimit(Integer dailyTokenLimit) {
        this.dailyTokenLimit = dailyTokenLimit;
    }

    public LocalTime getOpdStart() {
        return opdStart;
    }

    public void setOpdStart(LocalTime opdStart) {
        this.opdStart = opdStart;
    }

    public LocalTime getOpdEnd() {
        return opdEnd;
    }

    public void setOpdEnd(LocalTime opdEnd) {
        this.opdEnd = opdEnd;
    }
}