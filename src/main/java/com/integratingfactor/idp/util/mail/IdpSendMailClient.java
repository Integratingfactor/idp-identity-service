package com.integratingfactor.idp.util.mail;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class IdpSendMailClient {
    private static Logger LOG = Logger.getLogger(IdpSendMailClient.class.getName());

    public void sendEmail(MailDetails email) throws UnsupportedEncodingException, MessagingException {
        LOG.info("Sending email to " + email.getTo().getName() + "\n" + email.getMsg());
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(email.getFrom().getAddress(), email.getFrom().getName()));
        msg.addRecipient(Message.RecipientType.TO,
                new InternetAddress(email.getTo().getAddress(), email.getTo().getName()));
        msg.setSubject(email.getSub());
        msg.setText(email.getMsg());
        Transport.send(msg);
    }
}
