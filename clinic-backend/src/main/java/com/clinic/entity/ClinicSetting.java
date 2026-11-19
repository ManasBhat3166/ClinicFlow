package com.clinic.entity;

import java.time.LocalTime;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "clinic_settings")
public class ClinicSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "clinic_id", nullable = false, unique = true)
    private Long clinicId;

    @Column(name = "online_booking")
    private Boolean onlineBooking;

    @Column(name = "daily_token_limit", nullable = false)
    private Integer dailyTokenLimit;

    @Column(name = "opd_start")
    private LocalTime opdStart;

    @Column(name = "opd_end")
    private LocalTime opdEnd;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    public Long getId() {
        return id;
    }

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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}