package com.hderma.clinic.domain.irb;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IrbTestRepository extends JpaRepository<IrbTest, Long> {

    // 전체 게시글을 원글->답글 계층 순서로 조회
    @Query(value =
            "WITH RECURSIVE irb_hierarchy AS (" +
                    "    SELECT *, CAST(id AS CHAR(200)) AS path " +
                    "    FROM irb_test " +
                    "    WHERE (depth = 0 OR depth IS NULL) " +
                    "    UNION ALL " +
                    "    SELECT t.*, CONCAT(h.path, ',', t.id) " +
                    "    FROM irb_test t " +
                    "    INNER JOIN irb_hierarchy h ON t.irb_test_id_ref = h.id " +
                    ") " +
                    "SELECT * FROM irb_hierarchy ORDER BY path",
            nativeQuery = true)
    List<IrbTest> findAllHierarchy();

    // 임시저장(is_temp=1) 제외하고 계층 조회
    @Query(value =
            "WITH RECURSIVE irb_hierarchy AS (" +
                    "    SELECT *, CAST(id AS CHAR(200)) AS path " +
                    "    FROM irb_test " +
                    "    WHERE (depth = 0 OR depth IS NULL) AND (is_temp = 0 OR is_temp IS NULL) " +
                    "    UNION ALL " +
                    "    SELECT t.*, CONCAT(h.path, ',', t.id) " +
                    "    FROM irb_test t " +
                    "    INNER JOIN irb_hierarchy h ON t.irb_test_id_ref = h.id " +
                    "    WHERE (t.is_temp = 0 OR t.is_temp IS NULL) " +
                    ") " +
                    "SELECT * FROM irb_hierarchy ORDER BY path",
            nativeQuery = true)
    List<IrbTest> findAllActiveHierarchy();

    long countByStatusAndIsTemp(IrbStatus status, Integer isTemp);

    List<IrbTest> findTop5ByIsTempAndDepthOrderByCreatedAtDesc(Integer isTemp, Integer depth);
}