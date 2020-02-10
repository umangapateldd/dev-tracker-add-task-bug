package test;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
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
	String EMAIL_TO = "";
	String EMAIL_FROM = "";
//	String CC_Email = "";

	public void mail(String filename, String username, String processStart)
			throws IOException, InterruptedException, GeneralSecurityException {
		EMAIL_TO = GetSheetData.getData("Dev Tracker!B2").get(0).get(0).toString().toLowerCase();
		String[] EMAIL_TOvalues = EMAIL_TO.trim().split(",");

		EMAIL_FROM = USERNAME;

//		CC_Email = GetSheetData.getData("Dev Tracker!B3").get(0).get(0).toString().toLowerCase();
//		String[] CC_Emailvalues = CC_Email.trim().split(",");

		String EMAIL_SUBJECT = "Script run by " + username;

		Properties prop = System.getProperties();
		prop.put("mail.smtp.host", SMTP_SERVER); // optional, defined in
													// SMTPTransport
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.port", "587"); // default port 25
		prop.put("mail.smtp.starttls.enable", "true"); // default port 25

		Session session = Session.getDefaultInstance(prop, null);
		try {
			Message msg = new MimeMessage(session);

			// from
			msg.setFrom(new InternetAddress(EMAIL_FROM));

			// to
			int tmp = 0;
			while (tmp < EMAIL_TOvalues.length) {
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(EMAIL_TOvalues[tmp]));
				tmp++;
			}

			// cc
//			int CCtmp = 0;
//			while (CCtmp < CC_Emailvalues.length) {
//				msg.addRecipient(Message.RecipientType.CC, new InternetAddress(CC_Emailvalues[CCtmp]));
//				CCtmp++;
//			}

			// subject
			msg.setSubject(EMAIL_SUBJECT);

			// Create the message part
			BodyPart messageBodyPart = new MimeBodyPart();
			String txt = "";
			// Now set the actual message
			if (processStart.equals("start")) {
				txt = "Process is started with " + GetSheetData.getData("Dev Tracker!D1").get(0).get(0).toString();
			} else if (!processStart.equals("start") || !processStart.equals("complete")) {
				txt = processStart;
			}

			// Create a multipar message
			Multipart multipart = new MimeMultipart();

			// Set text message part
			multipart.addBodyPart(messageBodyPart);

			File file = new File(filename);
			if (processStart.equals("complete")) {
				// Part two is attachment
				if (file.exists()) {
					txt = "Report file is available";
				} else {
					txt = "Task / Bug is added - Report file is not available";
				}
			}

			messageBodyPart.setText(txt);

			if (file.exists()) {
				messageBodyPart = new MimeBodyPart();
				DataSource source = new FileDataSource(filename);
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setFileName(filename);
				multipart.addBodyPart(messageBodyPart);
			}

			// Send the complete message parts
			msg.setContent(multipart);

			Thread.sleep(2000);
			Transport transport = session.getTransport("smtp");
			transport.connect(SMTP_SERVER, USERNAME, PASSWORD);
			transport.sendMessage(msg, msg.getAllRecipients());			
			file = new File(filename);
			if (file.exists()) {
				file.delete();
			}
			transport.close();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

}
