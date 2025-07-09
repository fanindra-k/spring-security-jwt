package com.kushwaha.book.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final Environment env;


    @Async
    public void sendEmail(
            String to,
            String username,
            EmailTemplateName emailTemplate,
            String confirmationURL,
            String activationCode,
            String subject
    ) throws MessagingException {
        String templateName;
        if(emailTemplate == null) {
            templateName = "confirm-email";
        }
        else{
            templateName = emailTemplate.name();
        }
        MimeMessage mineMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mineMessage, MULTIPART_MODE_MIXED, UTF_8.name());
        Map<String , Object> properties = new HashMap<>();
        properties.put("username", username);
        properties.put("confirmationURL", confirmationURL);
        properties.put("activation_code", activationCode);
        Context context = new Context();
        context.setVariables(properties);
        String mailFrom = env.getProperty("eduflow.mail");
        helper.setTo(to);
        helper.setFrom(mailFrom);
        helper.setSubject(subject);

        String template = templateEngine.process(templateName, context);
        helper.setText(template, true);
        mailSender.send(mineMessage);
    }
}
