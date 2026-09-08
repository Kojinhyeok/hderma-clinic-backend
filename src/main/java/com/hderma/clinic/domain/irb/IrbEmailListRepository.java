package com.hderma.clinic.domain.irb;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IrbEmailListRepository extends JpaRepository<IrbEmailList, Long> {

    List<IrbEmailList> findAllByIrbTestId(Long irbTestId);
    List<IrbEmailList> findAllByEmailType(String emailType);
    boolean existsByIrbTestIdAndEmailType(Long irbTestId, String emailType);
    Optional<IrbEmailList> findFirstByIrbTestIdOrderByCreatedAtDesc(Long irbTestId);

    // JSON 컬럼(user_list)에서 이메일로 접근 권한 있는 irb_test_id 목록 조회
    @Query(value = "SELECT DISTINCT irb_test_id FROM irb_email_list " +
                   "WHERE JSON_SEARCH(user_list, 'one', :email, NULL, '$[*].email') IS NOT NULL",
           nativeQuery = true)
    List<Long> findIrbTestIdsByEmail(@Param("email") String email);
}