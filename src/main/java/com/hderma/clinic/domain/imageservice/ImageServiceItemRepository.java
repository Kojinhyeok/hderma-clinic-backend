package com.hderma.clinic.domain.imageservice;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ImageServiceItemRepository extends JpaRepository<ImageServiceItem, Long> {
    List<ImageServiceItem> findAllByOrderBySortOrderAscIdAsc();
}