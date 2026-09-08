package com.hderma.clinic.domain.irb;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IrbCategoryService {

    private final IrbCategoryRepository categoryRepository;

    public List<IrbDto.CategoryResponse> getAllCategories() {
        return categoryRepository.findAllByOrderByIdAsc().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public IrbDto.CategoryResponse saveCategory(String categoryCode, String category, Long id) {
        IrbCategory entity = (id != null)
                ? categoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + id))
                : new IrbCategory();
        entity.setCategoryCode(categoryCode);
        entity.setCategory(category);
        return toDto(categoryRepository.save(entity));
    }

    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    private IrbDto.CategoryResponse toDto(IrbCategory c) {
        return IrbDto.CategoryResponse.builder()
                .id(c.getId()).categoryCode(c.getCategoryCode()).category(c.getCategory())
                .build();
    }
}