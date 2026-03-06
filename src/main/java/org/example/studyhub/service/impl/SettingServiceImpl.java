package org.example.studyhub.service.impl;

import jakarta.transaction.Transactional;
import org.example.studyhub.dto.SettingDTO;
import org.example.studyhub.mapper.SettingMapper;
import org.example.studyhub.model.Setting;
import org.example.studyhub.repository.SettingRepository;
import org.example.studyhub.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
