package com.ept.powersupport.controller.business.mece;

import com.ept.powersupport.reqObj.ComInfo;
import com.ept.powersupport.service.business.Impl.BusinessServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 商品上架
 */
@RestController
@RequestMapping("/business/mece")
@Slf4j
public class AddCommodity {

    @Autowired
    private ComInfo comInfo;

    @Autowired
    private BusinessServiceImpl businessService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    private String comImgUrl = null;
    private String dtlImgUrl1 = null;
    private String dtlImgUrl2 = null;
    private String dtlImgUrl3 = null;
    private String dtlImgUrl4 = null;
    private String dtlImgUrl5 = null;


    /**
     * 上传商品主图
     * @param com_img
     */
    @RequestMapping("/uploadComImg")
    public void upload_comImg(@RequestParam MultipartFile com_img) {
        if(com_img.isEmpty())
            return;

        comImgUrl = businessService.up7x_com_img(com_img);

    }

    @RequestMapping("/uploadDtlImg1")
    public void upload_dtlImg1(@RequestParam MultipartFile dtl_img1) {
        if(dtl_img1.isEmpty())
            return;
        dtlImgUrl1 = businessService.up7x_dtl_img1(dtl_img1);
    }

    @RequestMapping("/uploadDtlImg2")
    public void upload_dtlImg2(@RequestParam MultipartFile dtl_img2) {
        if(dtl_img2.isEmpty())
            return;
        dtlImgUrl2 = businessService.up7x_dtl_img2(dtl_img2);
    }

    @RequestMapping("/uploadDtlImg3")
    public void upload_dtlImg3(@RequestParam MultipartFile dtl_img3) {
        if(dtl_img3.isEmpty())
            return;
        dtlImgUrl3 = businessService.up7x_dtl_img3(dtl_img3);
    }

    @RequestMapping("/uploadDtlImg4")
    public void upload_dtlImg4(@RequestParam MultipartFile dtl_img4) {
        if(dtl_img4.isEmpty())
            return;
        dtlImgUrl4 = businessService.up7x_dtl_img4(dtl_img4);
    }

    @RequestMapping("/uploadDtlImg5")
    public void upload_dtlImg5(@RequestParam MultipartFile dtl_img5) {
        if(dtl_img5.isEmpty())
            return;
        dtlImgUrl5 = businessService.up7x_dtl_img5(dtl_img5);
    }

    @RequestMapping("/addCommodity")
    private void addCommodity(@RequestParam String com_name, @RequestParam String com_des, @RequestParam String com_price,
                              @RequestParam String discount_price, @RequestParam String com_type, @RequestParam String com_pchs_num) {

        //设置编码
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setHeader("Content-Type", "text/html; charset = utf-8");


        if (comImgUrl == null) {
            try{
                httpServletResponse.getWriter().write("请上传商品图片！");
            }catch (Exception e) {
                e.printStackTrace();
            }
            httpServletResponse.setHeader("refresh", "2;url=/test.html");
            return;
        }

        if(dtlImgUrl1 == null) {
            try{
                httpServletResponse.getWriter().write("请上传商详情图一！");
            }catch (Exception e) {
                e.printStackTrace();
            }
            httpServletResponse.setHeader("refresh", "2;url=/test.html");
            return;
        }

        comInfo.setCom_imgUrl(comImgUrl);
        comInfo.setDtl_img1Url(dtlImgUrl1);
        comInfo.setDtl_img2Url(dtlImgUrl2);
        comInfo.setDtl_img3Url(dtlImgUrl3);
        comInfo.setDtl_img4Url(dtlImgUrl4);
        comInfo.setDtl_img5Url(dtlImgUrl5);
        comInfo.setCom_name(com_name);
        comInfo.setCom_des(com_des);
        comInfo.setCom_price(com_price);
        comInfo.setCom_type(com_type);
        comInfo.setDiscount_price(discount_price);
        comInfo.setGrp_pchs_num(com_pchs_num);

        if(businessService.addCommodity(comInfo)) {
            try{
                httpServletResponse.getWriter().write("商品上架成功！");
            }catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            try{
                httpServletResponse.getWriter().write("商品上架失败！");
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        httpServletResponse.setHeader("refresh", "2;url=/test.html");
        comImgUrl = null;
        dtlImgUrl1 = null;
        dtlImgUrl2 = null;
        dtlImgUrl3 = null;
        dtlImgUrl4 = null;
        dtlImgUrl5 = null;
        return;

    }
}
