package com.xxm.tmall.test;

import com.xxm.tmall.controller.CategoryController;
import com.xxm.tmall.controller.ProductImageController;
import com.xxm.tmall.pojo.Category;
import com.xxm.tmall.service.CategoryService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class TestTmall {

    public static void main(String args[]){
        //准备分类测试数据：

        test();

    }

    public static void test() {
        int a = 9;
        System.out.println(a);
        // 失败的原因：没有读取spring的配置文件，application.xml。需要通过这个配置文件才能获取到bean。
//        ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"applicationContext.xml"});
//        CategoryService categoryService = (CategoryService) context.getBean("categoryService");
//        Category category = categoryService.get(1);
//
//
//        CategoryController categoryController = (CategoryController) context.getBean("categoryController");
//        ProductImageController productImageController = (ProductImageController) context.getBean("c");
//        //ProductImageController p = new ProductImageController();
//        //productImageController.test();
    }

    public static void dataInsert() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try (
                Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/tmall_ssm?useUnicode=true&characterEncoding=utf8",
                        "root", "123456");
                Statement s = c.createStatement();
        )
        {
            for (int i = 1; i <=10 ; i++) {
                String sqlFormat = "insert into category values (null, '测试分类%d')";
                String sql = String.format(sqlFormat, i);
                s.execute(sql);
            }

            System.out.println("已经成功创建10条分类测试数据");

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}