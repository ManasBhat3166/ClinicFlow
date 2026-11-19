package com.clinic.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.dto.ClinicSettingRequest;
import com.clinic.entity.ClinicSetting;
import com.clinic.service.ClinicSettingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clinic-settings")
public class ClinicSettingController {

    private final ClinicSettingService settingService;

    public ClinicSettingController(ClinicSettingService settingService) {
        this.settingService = settingService;
    }

    @PreAuthorize("hasRole('OWNER') and @clinicAccessService.hasClinicAccess(#p0.clinicId)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClinicSetting saveSettings(
            @Valid @RequestBody ClinicSettingRequest request) {

        return settingService.saveSettings(request);
    }

    @PreAuthorize("hasAnyRole('OWNER', 'DOCTOR', 'RECEPTION') and @clinicAccessService.hasClinicAccess(#p0)")
    @GetMapping("/{clinicId}")
    public ClinicSetting getSettings(@PathVariable Long clinicId) {
        return settingService.getSettings(clinicId);
    }
}