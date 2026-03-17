package org.example.studyhub.repository;

import org.example.studyhub.model.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SettingRepository extends JpaRepository<Setting, Long> {

    @Query("SELECT s FROM Setting s WHERE s.type.id = 1 AND s.status = 'ACTIVE'")
    List<Setting> getAllRoles();

    List<Setting> findByTypeIdAndStatus(Long typeId, String status);
    List<Setting> findByTypeIsNotNullOrderByIdAsc();


    List<Setting> findByTypeIsNullAndStatusOrderByNameAsc(String status);

    @Query(value = """
   SELECT s
   FROM Setting s
   LEFT JOIN s.type t
   WHERE s.type IS NOT NULL
     AND (:typeId IS NULL OR t.id = :typeId)
     AND (:status IS NULL OR s.status = :status)
     AND (:keyword IS NULL
          OR LOWER(s.name) LIKE :keyword
          OR LOWER(COALESCE(s.value, '')) LIKE :keyword)
   ORDER BY s.id ASC
   """,
            countQuery = """
   SELECT COUNT(s)
   FROM Setting s
   LEFT JOIN s.type t
   WHERE s.type IS NOT NULL
     AND (:typeId IS NULL OR t.id = :typeId)
     AND (:status IS NULL OR s.status = :status)
     AND (:keyword IS NULL
          OR LOWER(s.name) LIKE :keyword
          OR LOWER(COALESCE(s.value, '')) LIKE :keyword)
   """)
    Page<Setting> searchSettings(@Param("typeId") Long typeId,
                                 @Param("status") String status,
                                 @Param("keyword") String keyword,
                                 Pageable pageable);


    @Query("""
    SELECT COUNT(s) > 0
    FROM Setting s
    WHERE LOWER(s.name) = LOWER(:name)
      AND ((:typeId IS NULL AND s.type IS NULL) OR (s.type.id = :typeId))
      AND (:excludeId IS NULL OR s.id <> :excludeId)
""")
    boolean existsDuplicateNameInType(@Param("name") String name,
                                      @Param("typeId") Long typeId,
                                      @Param("excludeId") Long excludeId);
}
