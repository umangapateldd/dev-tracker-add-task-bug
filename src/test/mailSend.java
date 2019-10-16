package test;

import java.io.IOException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.sun.mail.smtp.SMTPTransport;

public class mailSend {
	public void mail() throws IOException {

		// for example, smtp.mailgun.org
		String SMTP_SERVER = "smtp.gmail.com";
		String USERNAME = "vivekbhatt.devdigital@gmail.com";
		String PASSWORD = "Vivekbhatt@123";

		String EMAIL_FROM = "vivekbhatt.devdigital@gmail.com";
		String EMAIL_TO = "vivekbhatt.devdigital@gmail.com";
		String EMAIL_TO_CC = "";

		String EMAIL_SUBJECT = "Test Send Email via SMTP";
		String EMAIL_TEXT = "Hello Java Mail \n ABC123";

		Properties prop = System.getProperties();
		prop.put("mail.smtp.host", SMTP_SERVER); // optional, defined in
													// SMTPTransport
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.port", "587"); // default port 25
		prop.put("mail.smtp.starttls.enable", "true"); // default port 25

		javax.mail.Session session = javax.mail.Session.getInstance(prop, null);
		Message msg = new MimeMessage(session);

		try {

			// from
			msg.setFrom(new InternetAddress(EMAIL_FROM));

			// to
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(EMAIL_TO, false));

			// cc
			msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(EMAIL_TO_CC, false));

			// subject
			msg.setSubject(EMAIL_SUBJECT);

			// Create the message part
			BodyPart messageBodyPart = new MimeBodyPart();

			// Now set the actual message
			messageBodyPart.setText("This is message body");

			// Create a multipar message
			Multipart multipart = new MimeMultipart();

			// Set text message part
			multipart.addBodyPart(messageBodyPart);

			// Part two is attachment
			messageBodyPart = new MimeBodyPart();
			String filename = "createparagraph.doc";
			DataSource source = new FileDataSource(filename);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(filename);
			multipart.addBodyPart(messageBodyPart);

			// Send the complete message parts
			msg.setContent(multipart);

			// content
			// msg.setText(EMAIL_TEXT);

			// msg.setSentDate(new Date());

			// Get SMTPTransport
			SMTPTransport t = (SMTPTransport) session.getTransport("smtp");

			// connect
			t.connect(SMTP_SERVER, USERNAME, PASSWORD);

			// send
			t.sendMessage(msg, msg.getAllRecipients());

			t.close();

		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

}
