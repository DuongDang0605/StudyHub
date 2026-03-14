package org.example.studyhub.service.impl;

import java.util.List;
import java.util.Optional;

import org.example.studyhub.dto.SettingDTO;
import org.example.studyhub.mapper.SettingMapper;
import org.example.studyhub.model.Setting;
import org.example.studyhub.repository.SettingRepository;
import org.example.studyhub.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SettingServiceImpl implements SettingService {
    @Autowired
    private SettingRepository settingRepository;
    @Autowired
    private SettingMapper settingMapper;

    @Override
    public List<SettingDTO> getAllRole(){
        List<Setting> settings = settingRepository.getAllRoles();
        return settingMapper.toDTOList(settings);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SettingDTO> getSettingsByType(String type) {
        // Convert type string to typeId based on the existing pattern
        Long typeId = null;
        if ("CATEGORY".equals(type)) {
            typeId = 2L; // Based on getAllRoles() using type.id = 1 for roles
        } else if ("LEVEL".equals(type)) {
            typeId = 3L; // Assuming LEVEL has typeId = 3
        }
        
        if (typeId != null) {
            List<Setting> settings = settingRepository.findByTypeIdAndStatus(typeId, "ACTIVE");
            return settingMapper.toDTOList(settings);
        }
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Setting> getSettingById(Long id) {
        return settingRepository.findById(id);
    }

    @Override
    public SettingDTO createSetting(SettingDTO settingDTO) {
        Setting setting = settingMapper.toEntity(settingDTO);
        Setting savedSetting = settingRepository.save(setting);
        return settingMapper.toDTO(savedSetting);
    }

    @Override
    public SettingDTO updateSetting(Long id, SettingDTO settingDTO) {
        Setting existingSetting = settingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Setting not found with id: " + id));
        
        settingMapper.updateEntity(existingSetting, settingDTO);
        Setting updatedSetting = settingRepository.save(existingSetting);
        return settingMapper.toDTO(updatedSetting);
    }

    @Override
    public void deleteSetting(Long id) {
        if (!settingRepository.existsById(id)) {
            throw new RuntimeException("Setting not found with id: " + id);
        }
        settingRepository.deleteById(id);
    }
}
