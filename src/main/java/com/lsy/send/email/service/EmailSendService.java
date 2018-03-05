package com.lsy.send.email.service;

import com.lsy.send.email.compoment.ProfileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Map;


/**
 * Created by liangsongying on 2018/3/2.
 */
@Service
public class EmailSendService {
    private static Logger logger = LoggerFactory.getLogger(EmailSendService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username:null}")
    private String senderEmail;
    @Autowired(required = false)
    private ProfileManager profileManager;


    public boolean sendSimpleEmail(String [] receive, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);//发送者.
        message.setTo(receive);//接收者.
        message.setSubject(subject);//邮件主题.
        message.setText(content);//邮件内容.
        mailSender.send(message);//发送邮件
        return true;
    }

    /**
     * 邮件中使用静态资源
     * @param receive
     * @param subject
     * @param content
     * @param file1Url
     * @param file1Name
     */
    public void sendInlineMail(String [] receive,String subject,String content,String file1Url,String file1Name) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);

            //基本设置.
            message.setFrom(senderEmail);//发送者.
            message.setTo(receive);//接收者.
            message.setSubject(subject);//邮件主题.
            // 邮件内容，第二个参数指定发送的是HTML格式
            //说明：嵌入图片<img src='cid:head'/>，其中cid:是固定的写法，而aaa是一个contentId。
            //content格式：content="<body>这是图片：<img src='cid:head' /></body>"
            message.setText(content, true);
            FileSystemResource file = new FileSystemResource(new File(file1Url));
            message.addInline(file1Name, file);
            mailSender.send(mimeMessage);
        }
        catch (Exception ex){
            logger.error("[sendInlineMail]:{}", ex.getMessage());

        }

    }

    /**
     * 发送模版邮件
     *
     * @param receiver
     * @param templateName
     * @param paramMap
     */
    public boolean sendTemplateMail(String [] receiver, String subject, String templateName,
                                    Map<String, Object> paramMap) {
        if (receiver == null || receiver.length == 0) {
            logger.error("缺失邮件接受者，邮件发送失败");
            return false;
        }
//        String env = profileManager.currentEnvironment();
//        subject =  "【"+env+"】"+subject;
        try {
            logger.info("开始发送邮件 \"{}\" 给 {}", subject, StringUtils.arrayToDelimitedString(receiver, ","));
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            final MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
            messageHelper.setFrom(senderEmail);//发送者.
            messageHelper.setTo(receiver);//接收者.
            messageHelper.setSubject(subject);//邮件主题.

            Context model = new Context();
            for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                model.setVariable(entry.getKey(), entry.getValue());
            }

            String content = templateEngine.process("email/" + templateName, model);
            if (!StringUtils.isEmpty(content)){
                //页面中不允许&单独存在，不然直接就报异常，当编译过后再进行删除
                content = content.replaceAll("<!\\[CDATA\\[", "").replaceAll("]]>", "");
            }

            messageHelper.setText(content, true);//此处设置为true发送为html,false为文本,默认为 false
            mailSender.send(mimeMessage);

        } catch (Exception ex) {
            logger.error("[模版邮件发送错误]:{}", ex.getMessage());
        }
        return false;
    }

    /**
     * 发送带附件的邮件
     * @param receive
     * @param subject
     * @param content
     * @param file1Url
     * @param file1Name
     * @param file2Url
     * @param file2Name
     * @return
     */
    public boolean sendAttachmentsEmail(String [] receive,String subject,String content,String file1Url,String file1Name,String file2Url,String file2Name) {
        try {
            //这个是javax.mail.internet.MimeMessage下的，不要搞错了。
            MimeMessage mimeMessage =  mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            //基本设置.
            helper.setFrom(senderEmail);//发送者.
            helper.setTo(receive);//接收者.
            helper.setSubject(subject);//邮件主题.
            helper.setText(content);//邮件内容.

            //org.springframework.core.io.FileSystemResource下的:
            //附件1,获取文件对象.
            FileSystemResource file1 = new FileSystemResource(new File(file1Url));
            //添加附件，这里第一个参数是在邮件中显示的名称，也可以直接是head.jpg，但是一定要有文件后缀，不然就无法显示图片了。
            helper.addAttachment(file1Name, file1);
            //附件2
            FileSystemResource file2 = new FileSystemResource(new File(file2Url));
            helper.addAttachment(file2Name, file2);

            mailSender.send(mimeMessage);
            return true;
        }catch (Exception ex){
            logger.error("[附件邮件发送错误]:{}", ex.getMessage());
            return false;
        }
    }


    /**
     * 发送带附件的thymeleaf模版邮件
     * @param receive
     * @param subject
     * @param templateName
     * @param paramMap
     * @param file1Url
     * @param file1Name
     * @param file2Url
     * @param file2Name
     * @return
     */
    public boolean sendAttachmentsAndTemplateMail(String [] receive, String subject, String templateName, Map<String, Object> paramMap,String file1Url,String file1Name,String file2Url,String file2Name){
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true,"UTF-8");
            message.setFrom(senderEmail);//发送者.
            message.setTo(receive);//接收者.
            message.setSubject(subject);//邮件主题.
            Context model = new Context();
            for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                model.setVariable(entry.getKey(), entry.getValue());
            }
            String content = templateEngine.process("email/" + templateName, model);
            if (content == null)
                //页面中不允许&单独存在，不然直接就报异常，当编译过后再进行删除
                content = content.replaceAll("<!\\[CDATA\\[", "");
            String messages = content.replaceAll("]]>", "");
            logger.info(messages);
            message.setText(messages, true);//此处设置为true发送为html,false为文本,默认为 false
            //org.springframework.core.io.FileSystemResource下的:
            //附件1,获取文件对象.
            if(file1Url != null){
            FileSystemResource file1 = new FileSystemResource(new File(file1Url));
            //添加附件，这里第一个参数是在邮件中显示的名称，也可以直接是head.jpg，但是一定要有文件后缀，不然就无法显示图片了。
            message.addAttachment(file1Name, file1);}
            //附件2
            if(file1Url != null){
            FileSystemResource file2 = new FileSystemResource(new File(file2Url));
            message.addAttachment(file2Name, file2);}
            mailSender.send(mimeMessage);

        } catch (Exception ex) {
            logger.error("[sendAttachmentsAndTemplateMail]:{}", ex.getMessage());
        }
        return false;
    }

}
