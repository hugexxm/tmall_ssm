package com.xxm.tmall.service.impl;

import com.xxm.tmall.mapper.ProductMapper;
import com.xxm.tmall.pojo.Category;
import com.xxm.tmall.pojo.Product;
import com.xxm.tmall.pojo.ProductExample;
import com.xxm.tmall.pojo.ProductImage;
import com.xxm.tmall.service.CategoryService;
import com.xxm.tmall.service.ProductImageService;
import com.xxm.tmall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductMapper productMapper;

    @Resource
    CategoryService categoryService;

    @Resource
    ProductImageService productImageService;

    @Override
    public void add(Product product) {
        productMapper.insert(product);
    }

    @Override
    public void delete(int id) {
        productMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void update(Product product) {
        productMapper.updateByPrimaryKeySelective(product);
    }

    @Override
    public Product get(int id) {
        Product product = productMapper.selectByPrimaryKey(id);
        setFirstProductImage(product); // 图片给送上去
        return product;
    }

    @Override
    public List list(int cid) {
        ProductExample example = new ProductExample();
        example.createCriteria().andCidEqualTo(cid);
        example.setOrderByClause("id desc");
        List<Product> ps = productMapper.selectByExample(example);
        setFirstProductImage(ps);
        return ps;
    }

    @Override
    public void setFirstProductImage(Product p) {
        List<ProductImage> images = productImageService.list(p.getId(), ProductImageService.type_single);
        if(!images.isEmpty())
            p.setFirstProductImage(images.get(0));
    }

    public void setFirstProductImage(List<Product> ps) {
        for(Product p : ps) {
            setFirstProductImage(p);
        }
    }

    public void setCategory(List<Product> ps) {
        for(Product p : ps) {
            int cid = p.getCid();
            Category category = categoryService.get(cid);
            p.setCategory(category);
        }
    }

    public void setCategory(Product product) {
        int cid = product.getCid();
        Category category = categoryService.get(cid);
        product.setCategory(category);
    }
}
