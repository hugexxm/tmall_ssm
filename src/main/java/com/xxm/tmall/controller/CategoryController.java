package com.xxm.tmall.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxm.tmall.pojo.Category;
import com.xxm.tmall.service.CategoryService;
import com.xxm.tmall.util.ImageUtil;
import com.xxm.tmall.util.Page;
import com.xxm.tmall.util.UploadedImageFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("") // 表示访问的时候无需额外的地址
public class CategoryController {

    @Resource(name = "categoryService")
    CategoryService categoryService;

    @RequestMapping("admin_category_list")
    public String list(Model model, Page page) { // 这个Page参数是如何填充的。因为有个start，自动往里面填充的。因而也会往model里面填充一份。
        System.out.println(categoryService);
        PageHelper.offsetPage(page.getStart(), page.getCount()); // 只需要在执行DAO操作前执行该语句就行。该语句也可以放在serviceImpl中
        List<Category> cs = categoryService.list();
        int total = (int) new PageInfo<>(cs).getTotal();
        //PageInfo pageInfo = new PageInfo(cs); // 这个很棒的
        page.setTotal(total);
        //model.addAttribute(pageInfo);
        model.addAttribute("page", page);
        model.addAttribute("cs", cs);
        return "admin/listCategory";
    }

    @RequestMapping("admin_category_add") // model包含了传进来的所有参数，是一个map
    public String add(Model model, Category c, HttpSession session, UploadedImageFile uploadedImageFile)
        throws IOException {
        categoryService.add(c);
        File imageFolder = new File(session.getServletContext().getRealPath("img/category"));
        File file = new File(imageFolder, c.getId() + ".jpg");
        if(!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        uploadedImageFile.getImage().transferTo(file);
        BufferedImage img = ImageUtil.change2jpg(file);
        ImageIO.write(img, "jpg", file);
        return "redirect:/admin_category_list"; // 加不加 / ，无所谓。因为最终都是要给前端的。前端拿着这个重新请求服务端
//        return "forward:/admin_category_list"; // 加 / :表示相对于项目路径。不加 /：表示相对于当前controller的路径，即类名上的请求路径。这里加不加，没有区别。
    }

    @RequestMapping("admin_category_delete")
    public String delete(int id, HttpSession session) throws IOException {
        categoryService.delete(id);
        File imageFolder = new File(session.getServletContext().getRealPath("img/category"));
        File file = new File(imageFolder, id + ".jpg");
        file.delete();
        return "redirect:/admin_category_list";
    }

    @RequestMapping("admin_category_edit")
    public String edit(Model model, int id) throws IOException {
        Category c = categoryService.get(id);
        model.addAttribute("c", c);
        return "admin/editCategory";
    }

    @RequestMapping("admin_category_update")
    public String update(Category c, HttpSession session, UploadedImageFile uploadedImageFile)
            throws IOException {
        categoryService.update(c);
        MultipartFile image = uploadedImageFile.getImage();
        if(null != image && !image.isEmpty()) {
            File imageFolder = new File(session.getServletContext().getRealPath("img/category"));
            File file = new File(imageFolder, c.getId() + ".jpg");
//            file.delete();
//            if(!file.getParentFile().exists())
//                file.getParentFile().mkdirs();
            image.transferTo(file);
            BufferedImage img = ImageUtil.change2jpg(file);
            ImageIO.write(img, "jpg", file);
        }
        return "redirect:/admin_category_list";
    }
}
