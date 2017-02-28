package com.pc.kaizer.netbank;

import android.content.Context;
import android.os.AsyncTask;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
/**
 * Created by rohan on 2/25/2017.
 */

public class SendMail extends AsyncTask<Void,Void,Void> {
    Session session;
    private String recp;
    private String subject;
    private String textMessage;
    private Context ctx;
    SendMail(String recp,String subject,String msg,Context ctx)
    {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("noreplyfirebase@gmail.com", "aa12112786");
            }
        });
        this.recp =recp;
        this.subject =subject;
        this.textMessage =msg;
        this.ctx=ctx;
    }
    @Override
    protected Void doInBackground(Void... params) {
        try{
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("noreplyfirebase@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recp));
            message.setSubject(subject);
            message.setContent(textMessage, "text/html; charset=utf-8");
            Transport.send(message);
        } catch(MessagingException e) {
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
