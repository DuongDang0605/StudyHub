package org.example.studyhub.mapper;

import org.example.studyhub.dto.SettingDTO;
import org.example.studyhub.model.Setting;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SettingMapper {

    public SettingDTO toDTO(Setting setting) {
        if (setting == null) return null;

        SettingDTO dto = new SettingDTO();
        dto.setId(setting.getId());
        dto.setName(setting.getName());
        dto.setValue(setting.getValue());
        dto.setPriority(setting.getPriority());
        dto.setDescription(setting.getDescription());
        dto.setStatus(setting.getStatus());
        dto.setCreatedAt(setting.getCreatedAt());
        dto.setUpdatedAt(setting.getUpdatedAt());

        if (setting.getType() != null) {
            dto.setTypeId(setting.getType().getId());
            dto.setTypeName(setting.getType().getName());
        }

        return dto;
    }

    public Setting toEntity(SettingDTO dto) {
        if (dto == null) return null;

        Setting setting = new Setting();
        setting.setName(dto.getName());
        setting.setValue(dto.getValue());
        setting.setPriority(dto.getPriority());
        setting.setDescription(dto.getDescription());
        setting.setStatus(dto.getStatus());

        // Only set type ID, let service resolve the full object
        if (dto.getTypeId() != null) {
            Setting type = new Setting();
            type.setId(dto.getTypeId());
            setting.setType(type);
        }

        return setting;
    }

    public List<SettingDTO> toDTOList(List<Setting> settings) {
        if (settings == null) return null;
        return settings.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Setting updateEntity(Setting existing, SettingDTO dto) {
        if (existing == null || dto == null) return existing;

        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getValue() != null) existing.setValue(dto.getValue());
        if (dto.getPriority() != null) existing.setPriority(dto.getPriority());
        if (dto.getDescription() != null) existing.setDescription(dto.getDescription());
        if (dto.getStatus() != null) existing.setStatus(dto.getStatus());

        if (dto.getTypeId() != null) {
            Setting type = new Setting();
            type.setId(dto.getTypeId());
            existing.setType(type);
        }

        return existing;
    }
}