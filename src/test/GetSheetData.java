package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

public class GetSheetData extends Utilities {
	private static final String APPLICATION_NAME = "Google Sheets API for Selenium";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "tokens";
	static NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	static String spreadsheetId = "1185TMWEkVxx1JkLaeKjJbH89DCrNoLhyZGVSH8pLiYs";
	static Sheets service;
	/**
	 * Global instance of the scopes required by this quickstart. If modifying these
	 * scopes, delete your previously saved tokens/ folder.
	 */
	private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
	private static final String CREDENTIALS_FILE_PATH = System.getProperty("user.dir") + "\\client_secret.json";

	/**
	 * Creates an authorized Credential object.
	 * 
	 * @param HTTP_TRANSPORT The network HTTP Transport.
	 * @return An authorized Credential object.
	 * @throws IOException If the credentials.json file cannot be found.
	 */
	@SuppressWarnings("unused")
	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
		// Load client secrets.
//		InputStream in = GetSheetData.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		InputStream in = new FileInputStream(CREDENTIALS_FILE_PATH);
		if (in == null) {
			throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
		}
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES)
						.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
						.setAccessType("offline").build();
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
		return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	}

	public static void googleSheetConnection() throws GeneralSecurityException, IOException {
		HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
				.setApplicationName(APPLICATION_NAME).build();
	}

	public static List<List<Object>> getData(String range) throws IOException, GeneralSecurityException {
		if (HTTP_TRANSPORT == null || service == null) {
			GetSheetData.googleSheetConnection();
		}
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();

		// Build a new authorized API client service.

		List<List<Object>> values = response.getValues();
		if (values == null || values.isEmpty()) {
			System.out.println("No data found.");
			return null;
		} else {
			return values;
		}
	}
}