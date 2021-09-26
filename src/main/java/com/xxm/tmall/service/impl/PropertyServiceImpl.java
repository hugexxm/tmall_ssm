package com.xxm.tmall.service.impl;

import com.xxm.tmall.mapper.PropertyMapper;
import com.xxm.tmall.pojo.Property;
import com.xxm.tmall.pojo.PropertyExample;
import com.xxm.tmall.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("propertyService")
public class PropertyServiceImpl implements PropertyService {

    @Autowired
    PropertyMapper propertyMapper;

    @Override
    public void add(Property property) {
        propertyMapper.insert(property);
    }

    @Override
    public void delete(int id) {
        propertyMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void update(Property property) {
        propertyMapper.updateByPrimaryKeySelective(property);
    }

    @Override
    public Property get(int id) {
        return propertyMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Property> list(int cid) {
        PropertyExample example = new PropertyExample();
        example.createCriteria().andCidEqualTo(cid); // 专门用来封装自定义查询条件的, andCidEqualTo相当于在sql中拼接一个“AND name=Cid”
        example.setOrderByClause("id desc");
        return propertyMapper.selectByExample(example);
    }
}
