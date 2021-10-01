package com.xxm.tmall.service.impl;

import com.xxm.tmall.mapper.CategoryMapper;
import com.xxm.tmall.mapper.PropertyValueMapper;
import com.xxm.tmall.pojo.*;
import com.xxm.tmall.service.ProductService;
import com.xxm.tmall.service.PropertyService;
import com.xxm.tmall.service.PropertyValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PropertyValueServiceImpl implements PropertyValueService {

    @Resource
    PropertyValueMapper propertyValueMapper;

    @Autowired
    PropertyService propertyService;

    @Override
    public void init(Product p) {
        // 获取该产品的属性。产品-->类别-->属性
        List<Property> pts = propertyService.list(p.getCid());
        for(Property pt : pts) {
            PropertyValue pv = get(p.getId(), pt.getId());
            if(pv == null) {
                pv = new PropertyValue();
                pv.setPid(p.getId());
                pv.setPtid(pt.getId());
                propertyValueMapper.insert(pv);
            }
        }
    }

    @Override
    public void update(PropertyValue pv) {
        propertyValueMapper.updateByPrimaryKeySelective(pv);
    }

    @Override
    public PropertyValue get(int pid, int ptid) {
        PropertyValueExample example = new PropertyValueExample();
        example.createCriteria()
                .andPidEqualTo(pid)
                .andPtidEqualTo(ptid);
        List<PropertyValue> pvs = propertyValueMapper.selectByExample(example);
        if(pvs.isEmpty())
            return null;
        else
            return pvs.get(0);
    }

    @Override
    public List<PropertyValue> list(int pid) {
        PropertyValueExample example = new PropertyValueExample();
        example.createCriteria().andPidEqualTo(pid);
        example.setOrderByClause("id desc");
        List<PropertyValue> pvs = propertyValueMapper.selectByExample(example);
        for(PropertyValue pv : pvs) {
            Property property = propertyService.get(pv.getPtid());
            pv.setProperty(property);
        }
        return pvs;
    }

    public void initTest(Product p) {
        // pruduct-->category-->property
        List<Property> pts = propertyService.list(p.getId()); // 获取该产品下所有的属性
        for(Property pt : pts) {
            PropertyValue ptv = get(p.getId(), pt.getId());
            if(ptv == null) {
                ptv = new PropertyValue();
                ptv.setPid(p.getId());
                ptv.setPtid(pt.getId());
                propertyValueMapper.insert(ptv);
            }
        }
    }
}
