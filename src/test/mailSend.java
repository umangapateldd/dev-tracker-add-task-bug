package test;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class mailSend {
	// for example, smtp.mailgun.org
	String SMTP_SERVER = "smtp.gmail.com";
	String USERNAME = "vivekbhatt.devdigital@gmail.com";
	String PASSWORD = "Vivekbhatt@123";

	String EMAIL_FROM = "vivekbhatt.devdigital@gmail.com";
	String EMAIL_TO = "vivek.bhatt@devdigital.com";
	String CC_Email_Nidhi = "nidhi.jani@devdigital.com";

	public void mail(String filename, String username, String processStart) throws IOException, InterruptedException {

		String EMAIL_SUBJECT = "Script run by " + username;

		Properties prop = System.getProperties();
		prop.put("mail.smtp.host", SMTP_SERVER); // optional, defined in
													// SMTPTransport
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.port", "587"); // default port 25
		prop.put("mail.smtp.starttls.enable", "true"); // default port 25

//		Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
//			protected PasswordAuthentication getPasswordAuthentication() {
//				return new PasswordAuthentication(USERNAME, PASSWORD);
//			}
//		});
		Session session = Session.getDefaultInstance(prop, null);
		try {
			Message msg = new MimeMessage(session);

			// from
			msg.setFrom(new InternetAddress(EMAIL_FROM));

			// to
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(EMAIL_TO));

			// cc
			msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(CC_Email_Nidhi));

			// subject
			msg.setSubject(EMAIL_SUBJECT);

			// Create the message part
			BodyPart messageBodyPart = new MimeBodyPart();

			// Now set the actual message
			if (processStart.equals("start")) {
				messageBodyPart.setText("Process Start");
			}

			// Create a multipar message
			Multipart multipart = new MimeMultipart();

			// Set text message part
			multipart.addBodyPart(messageBodyPart);

			if (processStart.equals("complete")) {
				// Part two is attachment
				File file = new File(filename);
				if (file.exists()) {
					messageBodyPart.setText("Report file is available");
					messageBodyPart = new MimeBodyPart();
					DataSource source = new FileDataSource(filename);
					messageBodyPart.setDataHandler(new DataHandler(source));
					messageBodyPart.setFileName(filename);
					multipart.addBodyPart(messageBodyPart);
				} else {
					messageBodyPart.setText("Task / Bug is added - Report file is not available");
				}
			}

			// Send the complete message parts
			msg.setContent(multipart);

			// content
//			msg.setText(EMAIL_TEXT);

			// msg.setSentDate(new Date());

			// Get SMTPTransport
//			SMTPTransport t = (SMTPTransport) session.getTransport("smtp");

			// connect
//			t.connect(SMTP_SERVER, USERNAME, PASSWORD);

			// send

//			t.sendMessage(msg, msg.getAllRecipients());
			Thread.sleep(2000);
			Transport transport = session.getTransport("smtp");
			transport.connect(SMTP_SERVER, USERNAME, PASSWORD);
			transport.sendMessage(msg, msg.getAllRecipients());
//			transport.close();
			File file = new File(filename);
			if (file.exists()) {
				file.delete();
			}
			transport.close();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

}
