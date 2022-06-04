package com.ept.powersupport.controller.business.mece;

import com.ept.powersupport.reqObj.BusInitInfo;
import com.ept.powersupport.service.business.Impl.BusinessServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 商家初始化
 */
@RestController
@RequestMapping("/business/mece")
public class MeceInitial {

    @Autowired
    private BusinessServiceImpl businessService;

    //商家logoUrl
    private String business_logoUrl = null;
    //店铺背景Url
    private String business_backdropUrl = null;

    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * 上传商家logo
     * @param business_logo
     */
    @RequestMapping("/uploadLogo")
    private void recLogo(@RequestParam MultipartFile business_logo) {

        //设置编码
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setHeader("Content-Type", "text/html; charset = utf-8");

        if(business_logo.isEmpty()) {
            return;
        }

        business_logoUrl = businessService.up7x_business_logo(business_logo);

        try{
            if(business_logoUrl != null) {
                httpServletResponse.getWriter().write("上传成功");
                return;
            }
            httpServletResponse.getWriter().write("上传失败");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传主页背景
     * @param business_backdrop
     */
    @RequestMapping("/uploadDrop")
    private void recDrop(@RequestParam MultipartFile business_backdrop) {
        if(business_backdrop.isEmpty()) {
            return;
        }

        business_backdropUrl = businessService.up7x_business_backdrop(business_backdrop);

        try{
            if(business_backdropUrl != null) {
                httpServletResponse.getWriter().write("上传成功");
                return;
            }
            httpServletResponse.getWriter().write("上传失败");
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 商家信息
     * @param busInitInfo
     */
    @RequestMapping("/updateBusInfo")
    private void meceInitial(BusInitInfo busInitInfo) {

        //设置编码
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setHeader("Content-Type", "text/html; charset = utf-8");

        if(business_logoUrl == null) {
            try{
                httpServletResponse.getWriter().write("请上传商家logo!");
            }catch (Exception e) {
                e.printStackTrace();
            }
            httpServletResponse.setHeader("refresh", "2;url=/test.html");
            return;
        }

        if(business_backdropUrl == null){
            try{
                httpServletResponse.getWriter().write("请上传主页背景!");
            }catch (Exception e) {
                e.printStackTrace();
            }
            httpServletResponse.setHeader("refresh", "2;url=/test.html");
            return;
        }

        busInitInfo.setBusiness_logoUrl(business_logoUrl);
        busInitInfo.setBusiness_backdropUrl(business_backdropUrl);

        //更新商家信息
        try{
            if(businessService.businessInfoUpdate(busInitInfo)) {
                business_logoUrl = null;
                business_backdropUrl = null;
                httpServletResponse.getWriter().write("商家信息已更新");
            }else {
                httpServletResponse.getWriter().write("商家信息更新失败，请检查输入");
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        httpServletResponse.setHeader("refresh", "2;url=/test.html");
        return;

    }
}
