package com.finall.cmt.dao;

import com.finall.cmt.entity.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface CategoryDao {

    @Insert("insert into category(category_id, category_name) values(#{categoryId}, #{categoryName})")
    void insertCategory(Category category);

    @Update("<script>" +
            "update category " +
            "<set>" +
            "<if test ='categoryName != null'>category_name = #{categoryName}</if>" +
            "</set>" +
            "where category_id = #{categoryId}" +
            "</script>")
    void updateCategory(Category category);

    @Delete("delete from category where category_id = #{categoryId}")
    void deleteCategoryById(int categoryId);

    @Select("select category_id, category_name from category where category_id = #{categoryId}")
    Category selectCategoryById(int categoryId);

    @Select("select category_id, category_name from category where category_name = #{categoryName}")
    Category selectCategoryByName(String categoryName);

    @Select("select category_id, category_name from category")
    List<Category> selectAllCategory();
}
