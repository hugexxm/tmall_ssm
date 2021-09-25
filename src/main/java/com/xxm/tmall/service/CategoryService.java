package com.xxm.tmall.service;

import com.xxm.tmall.pojo.Category;
import com.xxm.tmall.util.Page;

import java.util.List;

public interface CategoryService {

    List<Category> list();

    void add(Category category);

    void delete(int id);

    Category get(int id);

    void update(Category category);
}
