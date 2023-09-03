package com.finall.cmt.service.impl;

import com.finall.cmt.dao.CategoryDao;
import com.finall.cmt.entity.Category;
import com.finall.cmt.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired(required = false)
    CategoryDao categoryDao;

    @Override
    public void insertCategory(Category category) {
        categoryDao.insertCategory(category);
    }

    @Override
    public void updateCategory(Category category) {
        categoryDao.updateCategory(category);
    }

    @Override
    public void deleteCategoryById(int categoryId) {
        categoryDao.deleteCategoryById(categoryId);
    }

    @Override
    public Category selectCategoryById(int categoryId) {
        return categoryDao.selectCategoryById(categoryId);
    }

    @Override
    public Category selectCategoryByName(String categoryName) {
        return categoryDao.selectCategoryByName(categoryName);
    }

    @Override
    public List<Category> selectAllCategory() {
        return categoryDao.selectAllCategory();
    }
}
