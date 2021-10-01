package com.xxm.tmall.service.impl;

import com.xxm.tmall.mapper.OrderMapper;
import com.xxm.tmall.pojo.Order;
import com.xxm.tmall.pojo.OrderExample;
import com.xxm.tmall.service.OrderService;
import com.xxm.tmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    OrderMapper orderMapper;

    @Autowired
    UserService userService;

    @Override
    public void add(Order order) {
        orderMapper.insert(order);
    }

    @Override
    public void delete(int id) {
        orderMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void update(Order order) {
        orderMapper.updateByPrimaryKeySelective(order);
    }

    @Override
    public Order get(int id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Order> list() {
        OrderExample example = new OrderExample();
        example.setOrderByClause("id desc");
        List<Order> orders = orderMapper.selectByExample(example);
        setUser(orders);
        return orders;
    }

    public void setUser(List<Order> os) {
        for(Order o : os) {
            setUser(o);
        }
    }

    public void setUser(Order o) {
        int uid = o.getUid();
        o.setUser(userService.get(uid));
    }
}
