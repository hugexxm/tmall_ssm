package com.xxm.tmall.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxm.tmall.pojo.Category;
import com.xxm.tmall.pojo.Product;
import com.xxm.tmall.service.CategoryService;
import com.xxm.tmall.service.ProductService;
import com.xxm.tmall.util.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("")
public class ProductController {

    @Resource
    CategoryService categoryService;

    @Resource
    ProductService productService;

    @RequestMapping("admin_product_list")
    public String list(Model model,  Page page , int cid) {
        Category category = categoryService.get(cid);

        PageHelper.offsetPage(page.getStart(), page.getCount());
        List<Product> products = productService.list(cid);

        int total = (int) new PageInfo<>(products).getTotal();
        page.setTotal(total);
        page.setParam("&cid=" + cid);
        model.addAttribute("c", category);
        model.addAttribute("ps", products);
        model.addAttribute("page", page);
        return "admin/listProduct";
    }

    @RequestMapping("admin_product_add")
    public String add(Product product) { // input: name, subTitle, originalPrice, promotePrice, stock, cid
        productService.add(product);
        return "redirect:/admin_product_list?cid=" + product.getCid(); // 这里的Integer自动转为了int？
    }

    @RequestMapping("admin_product_edit")
    public String edit(Model model, int id) {
        Product product = productService.get(id);
        Category category = categoryService.get(product.getCid()); // 其实完全没必要这样做，只是为了配合前端。
        product.setCategory(category);
        model.addAttribute("p", product);
        return "admin/editProduct";
    }

    @RequestMapping("admin_product_update")
    public String update(Product product) {
        productService.update(product);
        return "redirect:/admin_product_list?cid=" + product.getCid();
    }

    @RequestMapping("admin_product_delete")
    public String delete(int id) {
        Product product = productService.get(id);
        int cid = product.getCid();
        productService.delete(id);
        return "redirect:/admin_product_list?cid=" + cid;
    }
}
