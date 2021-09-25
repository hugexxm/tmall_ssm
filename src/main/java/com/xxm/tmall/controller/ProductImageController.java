package com.xxm.tmall.controller;

import com.xxm.tmall.pojo.Category;
import com.xxm.tmall.pojo.Product;
import com.xxm.tmall.pojo.ProductImage;
import com.xxm.tmall.service.CategoryService;
import com.xxm.tmall.service.ProductImageService;
import com.xxm.tmall.service.ProductService;
import com.xxm.tmall.util.ImageUtil;
import com.xxm.tmall.util.UploadedImageFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Component("c")
@RequestMapping("")
public class ProductImageController {

    @Resource
    ProductImageService productImageService;

    @Autowired
    ProductService productService;

    @Resource(name = "categoryService")
    CategoryService categoryService;

    @RequestMapping("admin_productImage_list")
    public String list(Model model, int pid) {
        List<ProductImage> pisSingle = productImageService.list(pid, ProductImageService.type_single);
        List<ProductImage> pisDetail = productImageService.list(pid, ProductImageService.type_detail);
        Product p = productService.get(pid);
        p.setCategory(categoryService.get(p.getCid()));
        model.addAttribute("pisSingle", pisSingle);
        model.addAttribute("pisDetail", pisDetail);
        model.addAttribute("p", p);
        return "admin/listProductImage";
    }

    @RequestMapping("admin_productImage_add")
    public String add(ProductImage productImage, HttpSession session, UploadedImageFile uploadedImageFile)
            throws IOException {
        productImageService.add(productImage);
        if(productImage.getType().equals(ProductImageService.type_single)) { // string的比较要用equals
            File imageFolder = new File(session.getServletContext().getRealPath("img/productSingle"));
            File imageFolderMiddle = new File(session.getServletContext().getRealPath("img/productSingle_middle"));
            File imageFolderSmall = new File(session.getServletContext().getRealPath("img/productSingle_small"));

            File file = new File(imageFolder, productImage.getId() + ".jpg");
            File fileMiddle = new File(imageFolderMiddle, productImage.getId() + ".jpg");
            File fileSmall = new File(imageFolderSmall, productImage.getId() + ".jpg");

            if(!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            if(!fileMiddle.getParentFile().exists())
                file.getParentFile().mkdirs();
            if(!fileSmall.getParentFile().exists())
                file.getParentFile().mkdirs();

            uploadedImageFile.getImage().transferTo(file);
            BufferedImage img = ImageUtil.change2jpg(file);
            ImageIO.write(img, "jpg", file);

            ImageUtil.resizeImage(file, 56, 56, fileSmall);
            ImageUtil.resizeImage(file, 217, 190, fileMiddle);
        }

        if(productImage.getType().equals(ProductImageService.type_detail)) {
            File imageFolder = new File(session.getServletContext().getRealPath("img/productDetail"));
            File file = new File(imageFolder, productImage.getId() + ".jpg");
            if(!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            uploadedImageFile.getImage().transferTo(file);
            BufferedImage img = ImageUtil.change2jpg(file);
            ImageIO.write(img, "jpg", file);
        }

        return "redirect:/admin_productImage_list?pid=" + productImage.getPid();
    }

    @RequestMapping("admin_productImage_delete")
    public String delete(int id, HttpSession session) throws IOException {
        ProductImage pImage = productImageService.get(id);
        productImageService.delete(id);

        if(pImage.getType().equals(ProductImageService.type_single)) {
            File imggeFolder = new File(session.getServletContext().getRealPath("img/productSingle"));
            File file = new File(imggeFolder, id + ".jpg");
            file.delete();

            File imggeFolderMiddle = new File(session.getServletContext().getRealPath("img/productSingle_middle"));
            File fileMiddle = new File(imggeFolderMiddle, id + ".jpg");
            fileMiddle.delete();

            File imggeFolderSmall = new File(session.getServletContext().getRealPath("img/productSingle_small"));
            File fileSmall = new File(imggeFolderSmall, id + ".jpg");
            fileSmall.delete();
        }

        if(pImage.getType().equals(ProductImageService.type_detail)) {
            File imggeFolder = new File(session.getServletContext().getRealPath("img/productDetail"));
            File file = new File(imggeFolder, id + ".jpg");
            file.delete();
        }

        return "redirect:/admin_productImage_list?pid=" + pImage.getPid();
    }

}
