package com.PoolingAPI.Service.Serviceimpl;

import com.PoolingAPI.Service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    @Autowired
    private JavaMailSender emailSender;
    @Value("${spring.mail.username}")
    private String senderMail;

    /**
     * @param to :
     * @param subject :
     * @param text :
     **/
    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            String[] recipients=to.split(",");
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderMail);
            message.setTo(recipients);
            message.setSubject(subject);
            message.setText(text);

            emailSender.send(message);
        } catch ( MailException exception ) {
            logger.error(exception.getMessage());
        }
    }

}
