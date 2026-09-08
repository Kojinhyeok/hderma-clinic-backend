package com.hderma.clinic.domain.irb;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IrbCategoryRepository extends JpaRepository<IrbCategory, Long> {
    List<IrbCategory> findAllByOrderByIdAsc();
}