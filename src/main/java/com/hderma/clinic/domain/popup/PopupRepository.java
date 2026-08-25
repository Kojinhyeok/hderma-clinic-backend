package com.hderma.clinic.domain.popup;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface PopupRepository extends JpaRepository<Popup, Long> {
    List<Popup> findAllByIsActiveTrueAndStartDateBeforeAndEndDateAfterOrderByPopupOrderAsc(
        LocalDateTime now1, LocalDateTime now2);
}