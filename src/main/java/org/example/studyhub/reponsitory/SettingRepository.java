package org.example.studyhub.reponsitory;

import org.example.studyhub.model.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SettingRepository extends JpaRepository<Setting,Long> {

    @Query("select s from Setting s where s.id = :roleId ")
    Setting findRoleByRoleId(Long roleId);
}
