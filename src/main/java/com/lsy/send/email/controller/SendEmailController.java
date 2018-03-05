package com.lsy.send.email.controller;

import com.lsy.send.email.service.EmailSendService;
import com.lsy.send.email.utils.WebResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liangsongying on 2018/3/2.
 */
@Controller
public class SendEmailController {
    @Autowired
    private EmailSendService emailSendService;
    @ResponseBody
    @RequestMapping(value = {"/", ""})
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("index");
        return modelAndView;
    }

    @ResponseBody
    @RequestMapping(value = "sendEmail",method = RequestMethod.POST)
    public WebResult sendEmail(String content,String receive) {
        String[] receives = {receive};
        return emailSendService.sendSimpleEmail(receives, "邮件", content) ? WebResult.success("发送成功") : WebResult.failure("发送失败");
    }

    @ResponseBody
    @RequestMapping(value = "sendThymeleafEmail",method = RequestMethod.POST)
    public WebResult sendThymeleafEmail(String content, String receive) {
        Map<String, Object> map = new HashMap<>();
        String[] receives = {receive};
        map.put("content", content);
        return emailSendService.sendTemplateMail(receives,"模版邮件","thymeleaf",map) ? WebResult.success("发送成功") : WebResult.failure("发送失败");
    }
}
