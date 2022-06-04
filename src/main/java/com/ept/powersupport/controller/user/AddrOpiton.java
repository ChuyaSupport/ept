package com.ept.powersupport.controller.user;

import com.ept.powersupport.entity.UserAddr;
import com.ept.powersupport.service.login.Impl.LoginServiceImpl;
import com.ept.powersupport.service.user.Impl.UserServiceImpl;
import com.ept.powersupport.util.DesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/addr")
public class AddrOpiton {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserAddr userAddr;

    @Autowired
    private LoginServiceImpl loginService;

    /**
     * 查询上一次使用地址
     * @param eptcode
     * @return
     */
    @RequestMapping("/lastAddr")
    public Object lastAddr(@RequestParam String eptcode, @RequestParam String business_id) {

        //解码eptcode，获取openid
        String openid = DesUtil.decrypt(eptcode);

        //检查登录
        if(!loginService.checkIsLogin(openid)) {
            return null;
        }

        return userService.lsRctAddr(openid, business_id);
    }

    /**
     * 查询所有收货地址信息
     * @param eptcode
     * @return
     */
    @RequestMapping("/lsAddr")
    public Object lsAddr(@RequestParam String eptcode, @RequestParam String business_id) {

        //解码eptcode，获取openid
        String openid = DesUtil.decrypt(eptcode);

        //检查登录
        if(!loginService.checkIsLogin(openid)) {
            return null;
        }

        return userService.lsAddr(openid, business_id);

    }

    @RequestMapping("/newAddr")
    public Object newAddr(@RequestParam String eptcode, @RequestParam String addr, @RequestParam String name,
                          @RequestParam String gender, @RequestParam String phone, @RequestParam String user_longitude, @RequestParam String user_latitude) {

        //解码eptcode，获取openid
        String openid = DesUtil.decrypt(eptcode);

        //检查登录
        if(!loginService.checkIsLogin(openid)) {
            return null;
        }

        if(!(gender.equals("F") || gender.equals("M"))){
            return 0;
        }

        userAddr.setOpenid(openid);
        userAddr.setAddr(addr);
        userAddr.setName(name);
        userAddr.setGender(gender);
        userAddr.setPhone(phone);
        userAddr.setUser_longitude(user_longitude);
        userAddr.setUser_latitude(user_latitude);

        if(userService.newAddr(userAddr))
            return 1;
        return 0;
    }

    @RequestMapping("/delAddr")
    public Object deleteAddr(@RequestParam String eptcode, @RequestParam String addr_id) {
        //解码eptcode，获取openid
        String openid = DesUtil.decrypt(eptcode);

        //检查登录
        if(!loginService.checkIsLogin(openid)) {
            return null;
        }

        return userService.delAddr(openid, addr_id) ? 1 : 0;
    }
}
