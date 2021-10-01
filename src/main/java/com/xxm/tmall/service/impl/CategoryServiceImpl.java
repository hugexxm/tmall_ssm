package com.xxm.tmall.service.impl;

import com.xxm.tmall.mapper.CategoryMapper;
import com.xxm.tmall.pojo.Category;
import com.xxm.tmall.pojo.CategoryExample;
import com.xxm.tmall.pojo.Product;
import com.xxm.tmall.service.CategoryService;
import com.xxm.tmall.service.ProductService;
import com.xxm.tmall.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service("categoryService") // 这是一个bean
public class CategoryServiceImpl implements CategoryService {

    @Resource // Autowire 和 Resoure ，目前都可以
    CategoryMapper categoryMapper;

    @Resource
    ProductService productService;

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

    @Override
    public void fillProducts(Category category) {
        int cid = category.getId();
        List<Product> ps = productService.list(cid);
        category.setProducts(ps);
    }

    @Override
    public void fillProductByRow(Category category) {
        int cid = category.getId();
        List<Product> ps = productService.list(cid);
        int length = ps.size();
        int rowLength = 8; // 可以改为可设置的参数

        List<List<Product>> psByRow = new ArrayList<>();
        int index = 0;
        while(index < length) {
            if(index + rowLength < length) {
                List<Product> subList = ps.subList(index, index + rowLength);
                psByRow.add(subList);
                index += rowLength;
            }
            else {
                List<Product> subList = ps.subList(index, length - 1);
                psByRow.add(subList);
                break;
            }
        }

        category.setProductsByRow(psByRow);
    }

    @Override
    public void fillProducts(List<Category> cs) {
        for(Category c : cs)
            fillProducts(c);
    }

    @Override
    public void fillProductsByRow(List<Category> cs) {
        for(Category c : cs)
            fillProductByRow(c);
    }
}
