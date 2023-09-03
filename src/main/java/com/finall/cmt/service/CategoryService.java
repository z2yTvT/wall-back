package com.finall.cmt.service;

import com.finall.cmt.entity.Category;

import java.util.List;


public interface CategoryService {

    void insertCategory(Category category);

    void updateCategory(Category category);

    void deleteCategoryById(int categoryId);

    Category selectCategoryById(int categoryId);

    Category selectCategoryByName(String categoryName);

    List<Category> selectAllCategory();
}
