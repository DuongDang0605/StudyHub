package org.example.studyhub.service;

import org.example.studyhub.dto.SettingDTO;
import org.example.studyhub.model.Setting;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SettingService {

    List<SettingDTO> getAllRole();
    List<SettingDTO> getSettingsByType(String type);
    Optional<Setting> getSettingById(Long id);
    SettingDTO createSetting(SettingDTO settingDTO);
    SettingDTO updateSetting(Long id, SettingDTO settingDTO);
    void deleteSetting(Long id);
}
