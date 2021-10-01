package com.xxm.tmall.service.impl;

import com.xxm.tmall.mapper.OrderItemMapper;
import com.xxm.tmall.pojo.Order;
import com.xxm.tmall.pojo.OrderItem;
import com.xxm.tmall.pojo.OrderItemExample;
import com.xxm.tmall.pojo.Product;
import com.xxm.tmall.service.OrderItemService;
import com.xxm.tmall.service.ProductService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    @Resource
    OrderItemMapper orderItemMapper;

    @Resource
    ProductService productService;

    @Override
    public void add(OrderItem orderItem) {
        orderItemMapper.insert(orderItem);
    }

    @Override
    public void delete(int id) {
        orderItemMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void update(OrderItem orderItem) {
        orderItemMapper.updateByPrimaryKeySelective(orderItem);
    }

    @Override
    public OrderItem get(int id) {
        return orderItemMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<OrderItem> list() {
        OrderItemExample example = new OrderItemExample();
        example.setOrderByClause("id desc");
        List<OrderItem> orderItems = orderItemMapper.selectByExample(example);
        return orderItems;
    }

    // 批处理
    @Override
    public void fill(List<Order> orders) {
        for(Order order : orders) {
            fill(order);
        }
    }

    // 这个干了好多事情啊
    @Override
    public void fill(Order order) { // orderItem total totalNumber
        OrderItemExample example = new OrderItemExample();
        example.createCriteria().andOidEqualTo(order.getId());
        example.setOrderByClause("id desc");
        List<OrderItem> orderItems = orderItemMapper.selectByExample(example);
        setProduct(orderItems);

        float total = 0;
        int totalNumber = 0;
        for(OrderItem oi : orderItems) {
            totalNumber += oi.getNumber();
            total += oi.getNumber() * oi.getProduct().getPromotePrice();
        }

        order.setTotal(total);
        order.setTotalNumber(totalNumber);
        order.setOrderItems(orderItems);
    }

    @Override
    public int getSaleCount(int pid) {
        OrderItemExample example = new OrderItemExample();
        example.createCriteria().andPidEqualTo(pid);
        List<OrderItem> ois = orderItemMapper.selectByExample(example);
        int count = 0;
        for(OrderItem oi : ois) {
            count += oi.getNumber();
        }
        return count;
    }

    // 批处理
    public void setProduct(List<OrderItem> orderItems) {
        for(OrderItem oi : orderItems)
            setProduct(oi);
    }

    public void setProduct(OrderItem orderItem) {
        Product p = productService.get(orderItem.getPid());
        orderItem.setProduct(p);
    }
}
