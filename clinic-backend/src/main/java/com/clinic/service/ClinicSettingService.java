package com.clinic.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clinic.dto.ClinicSettingRequest;
import com.clinic.entity.ClinicSetting;
import com.clinic.repository.ClinicRepository;
import com.clinic.repository.ClinicSettingRepository;

@Service
public class ClinicSettingService {

    private final ClinicSettingRepository settingRepository;
    private final ClinicRepository clinicRepository;

    public ClinicSettingService(
            ClinicSettingRepository settingRepository,
            ClinicRepository clinicRepository) {
        this.settingRepository = settingRepository;
        this.clinicRepository = clinicRepository;
    }

    @Transactional
    public ClinicSetting saveSettings(ClinicSettingRequest request) {

        if (!clinicRepository.existsById(request.getClinicId())) {
            throw new RuntimeException("Clinic not found");
        }

        if (request.getDailyTokenLimit() <= 0) {
            throw new RuntimeException("Daily token limit must be greater than zero");
        }

        if (request.getOpdStart() != null && request.getOpdEnd() != null
                && !request.getOpdStart().isBefore(request.getOpdEnd())) {
            throw new RuntimeException("OPD start time must be before OPD end time");
        }

        ClinicSetting setting = settingRepository
                .findByClinicId(request.getClinicId())
                .orElse(new ClinicSetting());

        setting.setClinicId(request.getClinicId());
        setting.setOnlineBooking(request.getOnlineBooking());
        setting.setDailyTokenLimit(request.getDailyTokenLimit());
        setting.setOpdStart(request.getOpdStart());
        setting.setOpdEnd(request.getOpdEnd());

        return settingRepository.save(setting);
    }

    public ClinicSetting getSettings(Long clinicId) {

        return settingRepository.findByClinicId(clinicId)
                .orElseThrow(() -> new RuntimeException("Clinic settings not found"));
    }
}