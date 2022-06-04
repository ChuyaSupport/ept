package com.ept.powersupport.controller.business.mece;

import com.ept.powersupport.service.business.Impl.BusinessServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * 设置拼团
 */
@RequestMapping("/business/mece")
@RestController
public class SetGrpInfo {

    @Autowired
    private BusinessServiceImpl businessService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @RequestMapping("/setGrpInfo")
    public void setGrpInfo(@RequestParam String com_id, @RequestParam String start_time,
                           @RequestParam String end_time, @RequestParam String grpNum, String freeNum) {

        System.out.println(start_time);

        //设置编码
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setHeader("Content-Type", "text/html; charset = utf-8");

        if(businessService.setGroup(com_id, grpNum, start_time, end_time, freeNum)) {
            try {
                httpServletResponse.getWriter().write("设置拼团成功!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                httpServletResponse.getWriter().write("设置拼团失败!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        httpServletResponse.setHeader("refresh", "2;url=/test.html");
    }
}
