package com.krohinc.gmailer;

import java.util.Properties;
 
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
 
public class Gmail {
 
    public static void main(String[] args) {
 
        final String username = "crowbird.com@gmail.com";
        final String password = "insertpassword";
 
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
 
        Session session = Session.getInstance(props,
          new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
          });
 
        try {
 
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("test-akroh@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse("andrew.kroh@me.com"));
            message.setSubject("Testing Subject");
            message.setText("Dear Andrew,"
                + "\n\n This is a test.");
 
            Transport.send(message);
 
            System.out.println("Done");
 
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}

