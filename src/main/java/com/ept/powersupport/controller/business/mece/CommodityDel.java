package com.ept.powersupport.controller.business.mece;

import com.ept.powersupport.service.business.Impl.BusinessServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/business/mece")
public class CommodityDel {

    @Autowired
    private BusinessServiceImpl businessService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @RequestMapping("/com_delete")
    public void commodityDel(@RequestParam String com_id) {

        //设置编码
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setHeader("Content-Type", "text/html; charset = utf-8");

        if(businessService.commodityDel(com_id)) {
            try {
                httpServletResponse.getWriter().write("1");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                httpServletResponse.getWriter().write("0");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        httpServletResponse.setHeader("refresh", "2;url=/test.html");
    }
}
