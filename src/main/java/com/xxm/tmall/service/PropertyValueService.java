package com.xxm.tmall.service;

import com.xxm.tmall.pojo.Product;
import com.xxm.tmall.pojo.PropertyValue;

import java.util.List;

public interface PropertyValueService {

    /**
     * 这个方法的作用是初始化PropertyValue。
     * 为什么要初始化呢？
     * 因为对于PropertyValue的管理，没有增加，只有修改。
     * 所以需要通过初始化来进行自动地增加，以便于后面的修改。
     */
    void init(Product p); // 初始化可以放在List<PropertyValue> list()中，也可以在controller中单独调用

    void initTest(Product p);
    //void add(PropertyValue pv); // 属性的增加和删除只能category中进行

    //void delete(int id); // 属性的增加和删除只能在category中进行

    void update(PropertyValue pv);

    PropertyValue get(int pid, int ptid); // get结果应该只有一个。取list结果的第一个就行

    List<PropertyValue> list(int pid); // 根据需要写，一般是某个产品下的属性值。不需要加上 ptid

}
