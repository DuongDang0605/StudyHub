package org.example.studyhub.repository;

import org.example.studyhub.model.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SettingRepository extends JpaRepository<Setting, Long> {

    @Query("SELECT s FROM Setting s WHERE s.type.id = 1 AND s.status = 'Active'")
    List<Setting> getAllRoles();

    List<Setting> findByTypeIdAndStatus(Long typeId, String status);
}