package com.xxm.tmall.service.impl;

import com.xxm.tmall.mapper.CategoryMapper;
import com.xxm.tmall.pojo.Category;
import com.xxm.tmall.pojo.CategoryExample;
import com.xxm.tmall.service.CategoryService;
import com.xxm.tmall.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("categoryService") // 这是一个bean
public class CategoryServiceImpl implements CategoryService {

    @Resource // Autowire 和 Resoure ，目前都可以
    CategoryMapper categoryMapper;

    @Override
    public List<Category> list() {
        CategoryExample categoryExample = new CategoryExample();
        return categoryMapper.selectByExample(categoryExample);
    }

    @Override
    public void add(Category category) {
        categoryMapper.insert(category);
    }

    @Override
    public void delete(int id) {
        categoryMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Category get(int id) {
        return categoryMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(Category category) {
        categoryMapper.updateByPrimaryKeySelective(category);
    }
}
