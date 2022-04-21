package com.my.community.controller;

import com.my.community.annotation.LoginRequired;
import com.my.community.entity.User;
import com.my.community.service.serviceimpl.UserServiceImpl;
import com.my.community.util.HostHolder;
import com.my.community.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * 个人设置接口
 *
 * @return
 */
@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private HostHolder hostHolder;
    @Value("${community.path.domin}")
    private String doMin;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Value("${community.path.upload}")
    private String uploadPath;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    /**
     * 上传头像
     *
     * @param headerImage
     * @param model
     * @return
     */
    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片!");
            return "/site/setting";
        }
        //获取文件名
        String filename = headerImage.getOriginalFilename();
        //获取文件后缀名
        String suffix = filename.substring(filename.lastIndexOf(".") + 1);
        //判断后缀是否为空
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件的格式不正确!");
            return "/site/setting";
        }
        //因为用户上传的图片可能重名,所以要重新生成一个随机的名字拼上文件的后缀
        filename = MD5Util.generateUUID() + suffix;
        //确定文件存放的路径
        File file = new File(uploadPath + "/" + filename);
        try {
            //将用户上传的文件写入到file中
            headerImage.transferTo(file);
        } catch (IOException e) {
            log.error("上传文件失败:" + e.getMessage());
            throw new RuntimeException("上传文件失败,服务器出现异常!", e);
        }
        //更新当前用户头像的路径(web访问路径)
        //http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        userService.updateHeader(user.getId(), doMin + contextPath + "/user/header/" + filename);
        return "redirect:/index";
    }

    /**
     * 获取头像
     *
     * @param fileName
     * @param response
     */
    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        fileName = uploadPath + "/" + fileName;
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        response.setContentType("image/" + suffix);
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            OutputStream outputStream = response.getOutputStream();
            byte[] bytes = new byte[1024];
            int count = 0;
            while ((count = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, count);
            }
        } catch (IOException e) {
            log.error("读取头像失败:" + e.getMessage());
        } finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping(path = "/updatePassword", method = RequestMethod.POST)
    public String updatePassword(String oldPassword, String newPassword, String reNewPassword, Model model) {
        //获取当前用户
        User user = hostHolder.getUser();

        //前端传过来的用户输入的原密码
        String oldPass = MD5Util.md5(oldPassword + user.getSalt());
        //判断原密码是否一致
        if (!user.getPassword().equals(oldPass)) {
            model.addAttribute("PasswordError", "原密码不正确!");
            return "/site/setting";
        }
        //判断新密码长度
        if (newPassword.length() < 6) {
            model.addAttribute("PasswordLengthError", "密码不能少于六位!");
            return "/site/setting";
        }

        //判断两次密码输入是否一致
        if (!newPassword.equals(reNewPassword)) {
            model.addAttribute("RepeatPasswordError", "两次输入的密码不一致!");
            return "/site/setting";
        }

        //判断新密码是否与原密码一致
        String s = MD5Util.md5(reNewPassword + user.getSalt());
        if (user.getPassword().equals(s)) {
            model.addAttribute("NewPasswordError", "新密码不能与原密码一致!");
            return "/site/setting";
        }
        String newPass = MD5Util.md5(reNewPassword + user.getSalt());
        userService.updatePassword(user.getId(), newPass);
        return "redirect:/login";
    }
}
