package cloud.newshive.mini_project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.MailerSendResponse;
import com.mailersend.sdk.emails.Email;
import com.mailersend.sdk.exceptions.MailerSendException;

import cloud.newshive.mini_project.config.SecretsConfigProperties;
import cloud.newshive.mini_project.repository.EmailRepo;

@Service
public class EmailService {

    @Autowired
    SecretsConfigProperties secretsConfig;

    @Autowired
    EmailRepo emailRepo;

    Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    public void sendEmail(String userEmail, String verificationCode) {

        Email email = new Email();

        String senderEmail = "verify@" + secretsConfig.domain();

        email.setFrom("NewsHive", senderEmail);
        email.addRecipient("User", userEmail);

        email.setSubject("Verify your NewsHive login");

        email.setPlain("Your verification code is " + verificationCode + ". This code will expire in 10 minutes.");

        MailerSend ms = new MailerSend();

        ms.setToken(secretsConfig.mailersendApiKey());

        try {    
            MailerSendResponse response = ms.emails().send(email);
            logger.info("Sent verification email to " + userEmail + " with code " + verificationCode + ". MessageID: " + response.messageId);
        } catch (MailerSendException e) {
            logger.error(e.responseBody);
        }
    }

    public void addVerificationCode(String email, String code) {
        emailRepo.addVerificationCode(email, code);
    }

    public String getVerificationCode(String email) {
        return emailRepo.getVerificationCode(email);
    }

    public void deleteVerificationCode(String email) {
        if (emailRepo.deleteVerificationCode(email)) {
            logger.debug("Deleted verification code for " + email);
        } else {
            logger.warn("Unable to delete verification code for " + email);
        }
    }

    public Boolean verifyCode(String email, String code) {
        return code != null && code.equals(getVerificationCode(email));
    }
    
}
