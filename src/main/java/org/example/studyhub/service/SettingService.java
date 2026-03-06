package org.example.studyhub.service;

import org.example.studyhub.dto.SettingDTO;
import org.example.studyhub.model.Setting;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SettingService {

    List<SettingDTO> getAllRole();
}
