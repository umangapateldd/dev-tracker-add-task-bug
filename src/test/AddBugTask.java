package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Listeners;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

@Listeners(ListenerTest.class)
public class AddBugTask extends Utilities {

	mailSend mailSend = new mailSend();
	Cell username;
	Cell password;
	Cell milestone;
	Cell taskcategory;
	Cell taskTitle;
	Cell objective;
	Cell references;
	Cell cos;
	Cell pmComment;
	Cell pmName;
	boolean ApprovedBugsByPM = false;
	boolean DeferredBugsByPM = false;
	Cell dependent;
	Cell successor;
	Cell priority;
	String taskStatus;
	Cell assignee;
	Cell reporter;
	Cell uploadDocuments;
	Cell originator;
	Cell project_name_next_row_acceptance_criteria = null;
	String uname;
	String zipFilename = "test-output.zip";
	String outputFolderName = "test-output";
	String renamedFileName = "testoutput.pdf";
	String DevTrackerNumber = "";
	boolean BranchCreateSheet = false;
	String BranchMilestone = "baseproject";
	Cell DevTrackerURL;
	Cell taskType;
	String DevTrackerStageURL;
	String version = "v1.1";
	String error = "";
	String headless;
	File file;
	Sheet sh1;
	Workbook wb;
	DateTimeFormatter dtf;
	LocalDateTime now;
	String acceptanceCriteria = "false";
	String acceptanceCriteria_nextRow = "false";
	String projectName;
	String bug_tracking_sheet;
	CreateBugTrackingReport createBugTrackingReport;
	static String oltagStringGlobal = "";
	static String ACFileAvailable = "false";
	String DevTrackerStageAccessUsername;
	String DevTrackerStageAccessPassword;
	MultipleFileUpload multipleFileUpload;
	String imagePath;

	public boolean checkFiles() {
		File file = new File("tokens/StoredCredential");
		if (file.exists()) {
			file = new File("client_secret.json");
			if (file.exists()) {
				return true;
			} else {
				Frame1.appendText("client_secret json file is not available.");
				Frame1.appendText(
						"Please download latest build from https://drive.google.com/open?id=1dI-bVzWUoGtyLgeywZzLBii99lJYVNCG");
				return false;
			}
		} else {
			Frame1.appendText("StoredCredential file is not available in tokens folder.");
			Frame1.appendText(
					"Please download latest build from https://drive.google.com/open?id=1dI-bVzWUoGtyLgeywZzLBii99lJYVNCG");
			return false;
		}
	}

	public boolean checkVersion() throws GeneralSecurityException, IOException, BiffException, InterruptedException {
		GetSheetData.googleSheetConnection();
		File src;
		if (Frame1.filePath.equals("")) {
			src = new File("Ticket.xls");
		} else {
			src = new File(Frame1.filePath);
		}

		wb = Workbook.getWorkbook(src);
		sh1 = wb.getSheet(0);
		username = sh1.getCell(1, 1);
		uname = username.getContents();

		if (GetSheetData.getData("Dev Tracker!D1").get(0).get(0).toString().equals(version)) {
			return true;
		} else {
			Frame1.appendText("Please download latest build from "
					+ GetSheetData.getData("Dev Tracker!D2").get(0).get(0).toString());
			mailSend.mail(renamedFileName, uname,
					"Version is mismatch. " + uname + " user is working on " + version + " build");
			return false;
		}
	}

	public void downloadFile() throws GeneralSecurityException, IOException, InterruptedException {
		openBrowser(headless);
		driver.get("https://docs.google.com/uc?id=156pRgW5rm5-oghUuqptbcsDaA5czoytY&export=download");
		Thread.sleep(5000);
//		URL url = new URL("https://docs.google.com/uc?id=156pRgW5rm5-oghUuqptbcsDaA5czoytY&export=download");
		File folder = new File("F:\\MscIT\\AFD\\dev-tracker-add-task-bug\\Download Files\\");
		File files[] = folder.listFiles();
		for (File f : files) {
			System.out.println(f.getName() + " " + f.lastModified());
		}
		Thread.sleep(5000);

//		DriveFile googleDriveFile  = Drive.DriveApi.getFile(googleApiClient, driveId);
//		MetadataResult mdRslt = googleDriveFile .getMetadata(googleApiClient).await();
//		if (mdRslt != null && mdRslt.getStatus().isSuccess()) {
//		  mdRslt.getMetadata().getTitle();
//		}

//		URL url;
//		URLConnection con;
//		DataInputStream dis;
//		FileOutputStream fos;
//		byte[] fileData;
//		try {
//			url = new URL("https://docs.google.com/uc?id=156pRgW5rm5-oghUuqptbcsDaA5czoytY&export=download"); // File Location goes here
//			con = url.openConnection(); // open the url connection.
//			dis = new DataInputStream(con.getInputStream());
//			fileData = new byte[con.getContentLength()];
//			for (int q = 0; q < fileData.length; q++) {
//				fileData[q] = dis.readByte();
//			}
//			dis.close(); // close the data input stream
//			fos = new FileOutputStream(new File("Download File/abc.png")); // FILE Save Location goes
//																								// here
//			fos.write(fileData); // write out the file we want to save.
//			fos.close(); // close the output stream writer
//		} catch (Exception m) {
//			System.out.println(m);
//		}
	}

	@org.testng.annotations.Test
	public void add_bug_task() throws Exception {
		if (checkVersion() == true) {
			dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
			now = LocalDateTime.now();
			Frame1.appendText(dtf.format(now));

			String[] sheetNames = wb.getSheetNames();

			DevTrackerStageURL = GetSheetData.getData("Dev Tracker!B8").get(0).get(0).toString();
			DevTrackerStageAccessUsername = GetSheetData.getData("Dev Tracker!B9").get(0).get(0).toString();
			DevTrackerStageAccessPassword = GetSheetData.getData("Dev Tracker!B10").get(0).get(0).toString();

			// column, row
			DevTrackerURL = sh1.getCell(1, 0);

			password = sh1.getCell(1, 2);

//			if (Frame1.imageDirPath == null || Frame1.imageDirPath.isEmpty()) {
//				imagePath = sh1.getCell(1, 3).getContents();
//			} else {
//				imagePath = Frame1.imageDirPath;
//			}

			imagePath = Frame1.imageDirPath;

			Frame1.appendText("image path = " + imagePath);

			bug_tracking_sheet = sh1.getCell(3, 0).getContents();

			if (DevTrackerURL.getContents().trim().equals(DevTrackerStageURL)) {
			} else {
				if (GetSheetData.getData("Dev Tracker!B1").get(0).get(0).toString().toLowerCase().equals("yes")) {
					mailSend.mail(Frame1.filePath, uname, "start");
				} else {
					Frame1.appendText("no option for mail on start");
				}
			}

			multipleFileUpload = new MultipleFileUpload();
			createBugTrackingReport = new CreateBugTrackingReport();

			if (Frame1.rdbVal == null) {
				headless = "Yes";
			} else {
				headless = Frame1.rdbVal;
			}

			openBrowser(headless);

			driver.get(DevTrackerURL.getContents());

			if (DevTrackerURL.getContents().trim().equals(DevTrackerStageURL)) {
				driver.findElement(By.name("access_login")).sendKeys(DevTrackerStageAccessUsername);
				driver.findElement(By.name("access_password")).sendKeys(DevTrackerStageAccessPassword);
				driver.findElement(By.name("access_password")).sendKeys(Keys.ENTER);
			}

			driver.findElement(By.name("username")).click();
			driver.findElement(By.name("username")).clear();
			driver.findElement(By.name("username")).sendKeys(uname);
			driver.findElement(By.name("password")).click();
			driver.findElement(By.name("password")).clear();
			driver.findElement(By.name("password")).sendKeys(password.getContents());
			driver.findElement(By.name("password")).sendKeys(Keys.ENTER);

			for (int i = 0; i < sheetNames.length; i++) {
//				if (sheetNames[i].equals("BranchCreate")) {
//					BranchCreateSheet = true;
//					break;
//				}
				if (sheetNames[i].equals(GetSheetData.getData("Dev Tracker!B11").get(0).get(0).toString())) {
					// bug / task status - TO DO
					ApprovedBugsByPM = true;
					DeferredBugsByPM = false;
					sh1 = wb.getSheet(sheetNames[i]);
					executeSheet(GetSheetData.getData("Dev Tracker!B12").get(0).get(0).toString());
				} else if (sheetNames[i].equals(GetSheetData.getData("Dev Tracker!B13").get(0).get(0).toString())) {
					// bug / task status - Deferred
					DeferredBugsByPM = true;
					ApprovedBugsByPM = false;
					sh1 = wb.getSheet(sheetNames[i]);
					executeSheet(GetSheetData.getData("Dev Tracker!B14").get(0).get(0).toString());
				} else {
					// bug / task status - as per sheet
					DeferredBugsByPM = false;
					ApprovedBugsByPM = false;
					sh1 = wb.getSheet(sheetNames[i]);
					executeSheet("Any");
				}
			}
		}
	}

	public void executeSheet(String taskStatus) throws Exception {

		Thread.sleep(2000);

		int row = 5;

		System.out.println("row count = " + sh1.getRows());
		while (row < sh1.getRows()) {
			// column, row
			Cell project_name = sh1.getCell(0, row);

			if ((row + 1) < sh1.getRows()) {
				System.out.println("set value project_name_next_row_acceptance_criteria");
				project_name_next_row_acceptance_criteria = sh1.getCell(0, row + 1);
			} else {
				System.out.println("set value project_name_next_row_acceptance_criteria");
				project_name_next_row_acceptance_criteria = null;
			}

			if (project_name.getContents().isEmpty()) {
				Frame1.appendText("Data are completed");
				break;
			}

			if (project_name_next_row_acceptance_criteria == null
					|| project_name_next_row_acceptance_criteria.getContents().isEmpty()) {
				System.out.println("don't check next row");
				acceptanceCriteria_nextRow = "false";
			}

			if (project_name.getContents().equals(GetSheetData.getData("Dev Tracker!B7").get(0).get(0).toString())) {

				if (ACFileAvailable.equals("true")) {
					acceptanceCriteria = "false";
					acceptanceCriteria_nextRow = "false";
				} else {
					System.out.println("acceptanceCriteria = true");

					acceptanceCriteria = "true";
					cos = sh1.getCell(14, row);
				}
			} else {
				ACFileAvailable = "false";
				System.out.println("acceptanceCriteria = false");

				if (project_name_next_row_acceptance_criteria != null && project_name_next_row_acceptance_criteria
						.getContents().equals(GetSheetData.getData("Dev Tracker!B7").get(0).get(0).toString())) {
					System.out.println("acceptanceCriteria_nextRow = true");
					acceptanceCriteria_nextRow = "true";
				} else {
					System.out.println("acceptanceCriteria_nextRow = false");
					acceptanceCriteria_nextRow = "false";
				}

				acceptanceCriteria = "false";

			}

			if (acceptanceCriteria.equals("false")) {
				milestone = sh1.getCell(1, row);
				taskcategory = sh1.getCell(2, row);
				taskType = sh1.getCell(9, row);
				taskTitle = sh1.getCell(11, row);
				objective = sh1.getCell(12, row);
				references = sh1.getCell(13, row);
				cos = sh1.getCell(14, row);
				pmComment = sh1.getCell(15, row);
				pmName = sh1.getCell(15, 4);
				dependent = sh1.getCell(3, row);
				successor = sh1.getCell(4, row);
				priority = sh1.getCell(10, row);
				if (taskStatus.equals("Any")) {
					taskStatus = sh1.getCell(16, row).getContents();
				}
				assignee = sh1.getCell(5, row);
				reporter = sh1.getCell(6, row);
				uploadDocuments = sh1.getCell(8, row);
				originator = sh1.getCell(7, row);

				if (milestone.getContents().isEmpty() || taskcategory.getContents().isEmpty()
						|| taskType.getContents().isEmpty() || taskTitle.getContents().isEmpty()
						|| objective.getContents().isEmpty() || priority.getContents().isEmpty()) {
					Frame1.appendText(
							"Milestone / Task Category / Task Type / Task Title / Objective / Priority data are not added in "
									+ (row + 1) + " row");
					driver.close();
					driver.quit();
//				System.exit(1);
					break;
				}

				BranchMilestone = milestone.getContents();
				Frame1.appendText(taskType.getContents() + " is adding");
				Frame1.appendText("Title is = " + taskTitle.getContents());
				testcase = false;

				if (project_name.getContents().matches("[0-9]+")) {
					driver.get(DevTrackerURL.getContents() + "index.php?route=common/task/loadDetailForm&project_id="
							+ project_name.getContents());
				} else {
					driver.get(DevTrackerURL.getContents() + "index.php?route=common/task/loadDetailForm&project_id=0");

					driver.findElement(By.xpath(GetSheetData.getData("Dev Tracker Xpath!B1").get(0).get(0).toString()))
							.click();
					driver.findElement(By.xpath(GetSheetData.getData("Dev Tracker Xpath!B2").get(0).get(0).toString()))
							.sendKeys(project_name.getContents());
					driver.findElement(By.xpath(GetSheetData.getData("Dev Tracker Xpath!B2").get(0).get(0).toString()))
							.sendKeys(Keys.ENTER);

				}

				if (driver.findElements(By.xpath("//*[@id=\"gritter-item-1\"]/div[2]/a")).size() > 0) {
					driver.findElement(By.xpath("//*[@id=\"gritter-item-1\"]/div[2]/a")).click();
				}

				projectName = driver
						.findElement(By.xpath(GetSheetData.getData("Dev Tracker Xpath!B3").get(0).get(0).toString()))
						.getText();

				Select selec = new Select(driver.findElement(By.id("milestone_id")));

				Boolean found = false;
				List<WebElement> allOptions = selec.getOptions();

				for (WebElement we : allOptions) {
					if (we.getText().equals(milestone.getContents())) {
						found = true;

					}
				}
				if (found == true) {
					selec.selectByVisibleText(milestone.getContents());
				} else {
					// Create new milestone
					driver.findElement(By.id("addmilestone")).click();
					driver.findElement(By.id("milestone_name")).sendKeys(milestone.getContents());
					((JavascriptExecutor) driver).executeScript("arguments[0].removeAttribute('readonly','readonly')",
							driver.findElement(By.id("milestone_startdate")));
					driver.findElement(By.id("milestone_startdate")).sendKeys("abc");
					driver.findElement(By.id("milestone_startdate")).sendKeys(Keys.TAB);

					((JavascriptExecutor) driver).executeScript("arguments[0].removeAttribute('readonly','readonly')",
							driver.findElement(By.id("milestone_enddate")));
					driver.findElement(By.id("milestone_enddate")).sendKeys("abc");
					driver.findElement(By.id("milestone_enddate")).sendKeys(Keys.TAB);

					driver.findElement(By.id("savemilestone")).click();
				}

				Thread.sleep(2500);

				try {
					Select selec1 = new Select(driver.findElement(By.id("taskcategory_id")));

					found = false;
					List<WebElement> allOptions1 = selec1.getOptions();

					for (WebElement we : allOptions1) {
						if (we.getText().equals(taskcategory.getContents())) {
							found = true;
						}
					}

					if (found == true) {
						selec1.selectByVisibleText(taskcategory.getContents());
					} else {
						// Create new taskcategory
						driver.findElement(By.id("addcategory")).click();
						driver.findElement(By.id("category_name")).sendKeys(taskcategory.getContents());
						driver.findElement(By.id("savecategory")).click();
					}
				} catch (StaleElementReferenceException e) {
					Select selec1 = new Select(driver.findElement(By.id("taskcategory_id")));

					found = false;
					List<WebElement> allOptions1 = selec1.getOptions();

					for (WebElement we : allOptions1) {
						if (we.getText().equals(taskcategory.getContents())) {
							found = true;
						}
					}

					if (found == true) {
						selec1.selectByVisibleText(taskcategory.getContents());
					} else {
						// Create new taskcategory
						driver.findElement(By.id("addcategory")).click();
						driver.findElement(By.id("category_name")).sendKeys(taskcategory.getContents());
						driver.findElement(By.id("savecategory")).click();
					}
				}

				task_bug_radio_button_selection();

				// submit button click for validation verification
				driver.findElement(By.xpath(GetSheetData.getData("Dev Tracker Xpath!B4").get(0).get(0).toString()))
						.click();

				if (driver.findElements(By.xpath("//*[@id='parsley-id-multiple-type_id']/li")).size() > 0) {
					Frame1.appendText(taskType.getContents() + " selection again");
					error = taskType.getContents() + " selection again";

					task_bug_radio_button_selection();
				}

				driver.findElement(By.id("task_name")).sendKeys(taskTitle.getContents());

				js = (JavascriptExecutor) driver;
				js.executeScript("window.scrollBy(0,250)");

				driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]")).click();

				// reference
				if (references.getContents().isEmpty()) {
				} else {
					macTextFormat(imagePath, "", "", references, "p[4]", sh1, row);
				}

				// Objective / Steps to Recreate
				macTextFormat(imagePath, "", "", objective, "p[2]", sh1, row);
			}

			// COS
			macTextFormat(imagePath, pmName.getContents(), pmComment.getContents(), cos, "xyz", sh1, row);

			if (acceptanceCriteria.equals("false")) {
//				removeExtraSpace();

				// Description verification

				if ((driver
						.findElements(By.xpath("//span[text()='"
								+ GetSheetData.getData("Dev Tracker!B4").get(0).get(0).toString() + "']"))
						.size() > 0
						|| driver
								.findElements(By.xpath("//span[text()='"
										+ GetSheetData.getData("Dev Tracker!B5").get(0).get(0).toString() + "']"))
								.size() > 0)
						&& driver
								.findElements(By.xpath("//span[text()='"
										+ GetSheetData.getData("Dev Tracker!B6").get(0).get(0).toString() + "']"))
								.size() > 0
						&& driver
								.findElements(By.xpath("//span[text()='"
										+ GetSheetData.getData("Dev Tracker!B7").get(0).get(0).toString() + "']"))
								.size() > 0) {
					Frame1.appendText("Description is added properly");
				} else {
					Frame1.appendText("Issue in added description");
				}

				js = (JavascriptExecutor) driver;
				js.executeScript("window.scrollBy(0,350)");

				// Add Dependent Predecessor
				if (dependent.getContents().isEmpty()) {
				} else {
					try {
						String[] arrSplit = dependent.getContents().split("/");

						for (int i = 0; i < arrSplit.length; i++) {
							driver.findElement(
									By.xpath(GetSheetData.getData("Dev Tracker Xpath!B5").get(0).get(0).toString()))
									.click();
							driver.findElement(
									By.xpath(GetSheetData.getData("Dev Tracker Xpath!B6").get(0).get(0).toString()))
									.sendKeys(arrSplit[i]);
							Thread.sleep(2000);
							driver.findElement(
									By.xpath(GetSheetData.getData("Dev Tracker Xpath!B6").get(0).get(0).toString()))
									.sendKeys(Keys.ENTER);
							Thread.sleep(1500);
							driver.findElement(
									By.xpath(GetSheetData.getData("Dev Tracker Xpath!B7").get(0).get(0).toString()))
									.click();

							if (driver
									.findElements(By.xpath(
											GetSheetData.getData("Dev Tracker Xpath!B8").get(0).get(0).toString()))
									.size() > 0) {
								if (driver
										.findElement(By.xpath(
												GetSheetData.getData("Dev Tracker Xpath!B8").get(0).get(0).toString()))
										.getText().startsWith("Please select any ")) {
									error = "Issue " + arrSplit[i] + " Dependent Predecessor task Popup text";
									Frame1.appendText("Some issue in Dependent task Popup text");
								} else {
									error = "Issue " + arrSplit[i] + " Dependent Predecessor task Popup text";
									Frame1.appendText("Some issue in Dependent task selection");
								}
							}
						}

						// Verify Dependent Predecessor
						int dependentCount = 1;
						arrSplit = dependent.getContents().split("/");
						for (int i = 0; i < arrSplit.length; i++) {
							Thread.sleep(1000);
							if (driver.findElement(By.xpath("//*[@id='frmaddedit']/div[7]/div[3]/table/tbody/tr["
									+ dependentCount + "]/td[1]/a")).getText().equals(arrSplit[i])) {
								Frame1.appendText("Dependent Predecessor task is added");
								dependentCount++;
							} else {
								Frame1.appendText(
										arrSplit[i] + " Dependent Predecessor is not attached. Trying to add again.");

								driver.findElement(By.xpath("//*[@id='frmaddedit']/div[7]/div[3]/table/tbody/tr["
										+ dependentCount + "]/td[4]/i[1]")).click();
								Thread.sleep(1500);

								driver.findElement(
										By.xpath(GetSheetData.getData("Dev Tracker Xpath!B5").get(0).get(0).toString()))
										.click();
								driver.findElement(
										By.xpath(GetSheetData.getData("Dev Tracker Xpath!B6").get(0).get(0).toString()))
										.sendKeys(arrSplit[i]);
								Thread.sleep(2000);
								driver.findElement(
										By.xpath(GetSheetData.getData("Dev Tracker Xpath!B6").get(0).get(0).toString()))
										.sendKeys(Keys.ENTER);
								Thread.sleep(1500);
								driver.findElement(
										By.xpath(GetSheetData.getData("Dev Tracker Xpath!B7").get(0).get(0).toString()))
										.click();

								error = "Issue " + arrSplit[i] + " Dependent Predecessor is not matched with "
										+ driver.findElement(
												By.xpath("//*[@id='frmaddedit']/div[7]/div[3]/table/tbody/tr["
														+ dependentCount + "]/td[1]/a"))
												.getText();
								mailSend.mail(renamedFileName, uname, error);
							}
						}
					} catch (ElementClickInterceptedException e) {
						js = (JavascriptExecutor) driver;
						js.executeScript("window.scrollBy(0,350)");
					}
				}

				// Add Dependent Successor
				if (successor.getContents().isEmpty()) {
				} else {
					try {
						String[] arrSplit = successor.getContents().split("/");

						for (int i = 0; i < arrSplit.length; i++) {
							driver.findElement(
									By.xpath(GetSheetData.getData("Dev Tracker Xpath!B9").get(0).get(0).toString()))
									.click();
							driver.findElement(
									By.xpath(GetSheetData.getData("Dev Tracker Xpath!B10").get(0).get(0).toString()))
									.sendKeys(arrSplit[i]);
							Thread.sleep(2000);
							driver.findElement(
									By.xpath(GetSheetData.getData("Dev Tracker Xpath!B10").get(0).get(0).toString()))
									.sendKeys(Keys.ENTER);
							Thread.sleep(1500);
							driver.findElement(
									By.xpath(GetSheetData.getData("Dev Tracker Xpath!B11").get(0).get(0).toString()))
									.click();

							if (driver
									.findElements(By.xpath(
											GetSheetData.getData("Dev Tracker Xpath!B12").get(0).get(0).toString()))
									.size() > 0) {
								if (driver
										.findElement(By.xpath(
												GetSheetData.getData("Dev Tracker Xpath!B12").get(0).get(0).toString()))
										.getText().startsWith("Please select any ")) {
									error = "Issue " + arrSplit[i] + " Dependent Successor task Popup text";
									Frame1.appendText("Some issue in successor task Popup text");
								} else {
									error = "Issue " + arrSplit[i] + " Dependent Successor task selection";
									Frame1.appendText("Some issue in successor task selection");
								}
							}
						}

						// Verify Dependent Successor
						int dependentCount = 1;
						arrSplit = successor.getContents().split("/");
						for (int i = 0; i < arrSplit.length; i++) {
							Thread.sleep(1000);
							if (driver.findElement(By.xpath("//*[@id='frmaddedit']/div[8]/div[3]/table/tbody/tr["
									+ dependentCount + "]/td[1]/a")).getText().equals(arrSplit[i])) {
								Frame1.appendText("Dependent Successor task is added");
								dependentCount++;
							} else {
								Frame1.appendText(
										arrSplit[i] + " Dependent Successor is not attached. Trying to add again.");

								driver.findElement(By.xpath("//*[@id='frmaddedit']/div[8]/div[3]/table/tbody/tr["
										+ dependentCount + "]/td[4]/i[1]")).click();
								Thread.sleep(1500);

								driver.findElement(
										By.xpath(GetSheetData.getData("Dev Tracker Xpath!B9").get(0).get(0).toString()))
										.click();
								driver.findElement(By
										.xpath(GetSheetData.getData("Dev Tracker Xpath!B10").get(0).get(0).toString()))
										.sendKeys(arrSplit[i]);
								Thread.sleep(2000);
								driver.findElement(By
										.xpath(GetSheetData.getData("Dev Tracker Xpath!B10").get(0).get(0).toString()))
										.sendKeys(Keys.ENTER);
								Thread.sleep(1500);
								driver.findElement(By
										.xpath(GetSheetData.getData("Dev Tracker Xpath!B11").get(0).get(0).toString()))
										.click();

								error = "Issue " + arrSplit[i] + " Dependent Successor is not matched with "
										+ driver.findElement(
												By.xpath("//*[@id='frmaddedit']/div[8]/div[3]/table/tbody/tr["
														+ dependentCount + "]/td[1]/a"))
												.getText();
								mailSend.mail(renamedFileName, uname, error);
							}
						}
					} catch (ElementClickInterceptedException e) {
						js = (JavascriptExecutor) driver;
						js.executeScript("window.scrollBy(0,250)");
					}
				}

				if (priority.getContents().isEmpty()) {
					Frame1.appendText("Priority should be required");
					driver.close();
					driver.quit();
				} else {
					Select selec2 = new Select(driver.findElement(By.id("priority_id")));
					selec2.selectByVisibleText(priority.getContents());
				}

				if (taskStatus.isEmpty()) {
					Frame1.appendText("Status is not available so set as default");
				} else {

					List<WebElement> options = driver.findElements(By.xpath("//select[@id='status']/option"));

					for (WebElement option : options) {
						if (option.getText().contains(taskStatus)) {
							Thread.sleep(1500);
							option.click();
							break;
						}
					}
				}

				System.out.println("assignee.getContents() = " + assignee.getContents());
				if (assignee.getContents().isEmpty()) {
					if (DeferredBugsByPM == true) {
						assignee = pmName;
					} else {
						Frame1.appendText("Assignee user is not available in excel sheet");
					}
				} else {
					List<WebElement> options = driver.findElements(By.xpath("//select[@id='assign_user_id']/option"));
					for (WebElement option : options) {
						if (option.getText().contains(assignee.getContents())) {
							Thread.sleep(1500);
							System.out.println("selected option = " + option.getText());
							option.click();
							break;
						}
					}
				}

				if (reporter.getContents().isEmpty()) {
					Frame1.appendText("Reporter user is not available in excel sheet");
				} else {
					List<WebElement> options = driver.findElements(By.xpath("//select[@id='report_to']/option"));

					for (WebElement option : options) {
						if (option.getText().contains(reporter.getContents())) {
							Thread.sleep(1500);
							option.click();
							break;
						}
					}
				}

				if (uploadDocuments.getContents().isEmpty()) {
				} else {
					multipleFileUpload.fileUpload(driver, imagePath, uploadDocuments);
					driver.findElement(By.id("startall")).click();

					int tmp = 0;

					do {
						if (driver.findElements(By.xpath("//div[@role='progressbar']")).size() > 0
								&& driver.findElement(By.xpath("//div[@role='progressbar']"))
										.getAttribute("aria-valuenow").equals("100")) {
							tmp = 1;
							Frame1.appendText("Documents are uploaded");
						} else if (driver.findElements(By.xpath("//div[@role='progressbar']")).size() > 0
								&& driver.findElement(By.xpath("//div[@role='progressbar']"))
										.getAttribute("aria-valuenow").equals("0")) {
							if (driver.findElements(By.xpath("//span[text()='Upload']")).size() > 0) {
								Frame1.appendText("any one or multiple documents are not attached");
								tmp = 1;
							} else {
								Frame1.appendText("documents are attached");
								tmp = 1;
							}
						} else {
							tmp = 0;
							Frame1.appendText("documents are uploading");
						}
					} while (tmp == 0);
					js = (JavascriptExecutor) driver;
					js.executeScript("window.scrollBy(0,250)");
				}
			}

			if (AddBugTask.ACFileAvailable.equals("true")) {
				removeExtraSpace();
				now = LocalDateTime.now();
				Frame1.appendText(dtf.format(now));
				driver.findElement(By.xpath(GetSheetData.getData("Dev Tracker Xpath!B4").get(0).get(0).toString()))
						.click();
				int size = driver
						.findElements(By.xpath(GetSheetData.getData("Dev Tracker Xpath!B13").get(0).get(0).toString()))
						.size();
				if (size > 0) {
					driver.findElement(By.xpath(GetSheetData.getData("Dev Tracker Xpath!B13").get(0).get(0).toString()))
							.click();
				}
				checkLoader();
				testcase = true;
				error = "complete";
				driver.findElement(By.tagName("body")).sendKeys(Keys.HOME);

				DevTrackerNumber = driver.getCurrentUrl().replace(DevTrackerURL.getContents() + "track/", "");
				Frame1.appendText(DevTrackerNumber);

				if (bug_tracking_sheet.toLowerCase().equals("yes")) {
					createBugTrackingReport.createBugTracking(driver, DevTrackerURL.getContents(),
							taskTitle.getContents(), projectName, originator.getContents(), reporter.getContents(),
							taskType.getContents());
				}
			} else {
				System.out.println("AddBugTask.ACFileAvailable = " + AddBugTask.ACFileAvailable);
				System.out.println(acceptanceCriteria_nextRow + " before remove extra space");
				if (acceptanceCriteria_nextRow.equals("true")) {

				} else {
					removeExtraSpace();
					now = LocalDateTime.now();
					Frame1.appendText(dtf.format(now));
					System.out.println("submit request");
					driver.findElement(By.xpath(GetSheetData.getData("Dev Tracker Xpath!B4").get(0).get(0).toString()))
							.click();
					int size = driver
							.findElements(
									By.xpath(GetSheetData.getData("Dev Tracker Xpath!B13").get(0).get(0).toString()))
							.size();
					if (size > 0) {
						driver.findElement(
								By.xpath(GetSheetData.getData("Dev Tracker Xpath!B13").get(0).get(0).toString()))
								.click();
					}
					checkLoader();
					testcase = true;
					error = "complete";
					driver.findElement(By.tagName("body")).sendKeys(Keys.HOME);

					DevTrackerNumber = driver.getCurrentUrl().replace(DevTrackerURL.getContents() + "track/", "");
					Frame1.appendText(DevTrackerNumber);

					if (bug_tracking_sheet.toLowerCase().equals("yes")) {
						createBugTrackingReport.createBugTracking(driver, DevTrackerURL.getContents(),
								taskTitle.getContents(), projectName, originator.getContents(), reporter.getContents(),
								taskType.getContents());
					}
				}
			}
			row++;
		}
		now = LocalDateTime.now();
		Frame1.appendText(dtf.format(now));
		if (BranchCreateSheet == true) {

			Sheet BranchCreateSheetName = wb.getSheet("BranchCreate");

			driver.get(BranchCreateSheetName.getCell(1, 0).getContents());

			driver.findElement(By.xpath("//*[@id='username']")).clear();
			driver.findElement(By.xpath("//*[@id='username']"))
					.sendKeys(BranchCreateSheetName.getCell(1, 1).getContents());

			driver.findElement(By.xpath("//*[@id='login-submit']")).click();
			driver.findElement(By.xpath("//*[@id='password']")).clear();
			driver.findElement(By.xpath("//*[@id='password']"))
					.sendKeys(BranchCreateSheetName.getCell(1, 2).getContents());

			driver.findElement(By.xpath("//*[@id='login-submit']")).click();

			boolean repositoriesMatch = false;
			int repositoriesrow = 1;
			BranchMilestone = "baseproject";
			while (repositoriesMatch == false) {

				if (checkElementAvailibility(
						"//*[@id='root']/div/div/div[2]/div/div/div[1]/div/div/div/div/section/div/table/tbody/tr["
								+ repositoriesrow + "]/td[1]/div/div[2]/span/a")) {
					if (driver.findElement(By.xpath(
							"//*[@id='root']/div/div/div[2]/div/div/div[1]/div/div/div/div/section/div/table/tbody/tr["
									+ repositoriesrow + "]/td[1]/div/div[2]/span/a"))
							.getText().equals(BranchMilestone)) {
						repositoriesMatch = true;
						driver.findElement(By.xpath(
								"//*[@id=\"root\"]/div/div/div[2]/div/div/div[1]/div/div/div/div/section/div/table/tbody/tr["
										+ repositoriesrow + "]/td[1]/div/div[2]/span/a"))
								.click();

						Thread.sleep(1000);
						// Branches Menu
						if (checkElementAvailibility("//div[text()='Branches']")) {
							driver.findElement(By.xpath("//div[text()='Branches']")).click();

							Thread.sleep(1000);
							// Create Branch Button
							if (checkElementAvailibility(
									"//*[@id='root']/div/div/div[2]/div/div/div[1]/div/div/div/div[1]/div[2]/div[2]/button")) {
								driver.findElement(By.xpath(
										"//*[@id='root']/div/div/div[2]/div/div/div[1]/div/div/div/div[1]/div[2]/div[2]/button"))
										.click();
								Thread.sleep(1000);
								if (checkElementAvailibility("//*[@id='select-branch']/div")) {
									driver.findElement(By.xpath("//*[@id='select-branch']/div")).click();
									Thread.sleep(1000);

									driver.findElement(By.xpath("//*[@id=\"react-select-4-input\"]")).sendKeys("dev");
									Thread.sleep(1000);
									driver.findElement(By.xpath("//*[@id=\"react-select-4-input\"]"))
											.sendKeys(Keys.ENTER);
									Thread.sleep(1000);

									driver.findElement(By.xpath("//input[@name='branchName']"))
											.sendKeys(DevTrackerNumber);

									driver.findElement(By.xpath("//*[@id=\"create-branch-button\"]")).click();
									Thread.sleep(5000);
									break;
								}
							}
						}
					}
				}
				repositoriesrow++;
			}
		}
	}

	@AfterSuite
	public void bugadd_fun_verify() throws Exception, InterruptedException {
		try {

			Frame1.btnFileUpload.setEnabled(true);
			Frame1.txtFileUpload.setEnabled(true);
			Frame1.btnExecuteScript.setEnabled(false);
			Frame1.btnSetImageFolder.setEnabled(true);
			Frame1.txtSetImageFolder.setEnabled(true);
			Frame1.rdbChromeYes.setEnabled(true);
			Frame1.rdbChromeNo.setEnabled(true);
//			Frame1.rdbattachmentFolderFromExcelYes.setEnabled(true);
//			Frame1.rdbattachmentFolderFromExcelNo.setEnabled(true);

			Assert.assertTrue(testcase);

			testcase = true;
		} catch (AssertionError e) {
			Frame1.btnFileUpload.setEnabled(true);
			Frame1.txtFileUpload.setEnabled(true);
			Frame1.btnExecuteScript.setEnabled(true);
			Frame1.btnSetImageFolder.setEnabled(true);
			Frame1.txtSetImageFolder.setEnabled(true);
			Frame1.rdbChromeYes.setEnabled(true);
			Frame1.rdbChromeNo.setEnabled(true);
//			Frame1.rdbattachmentFolderFromExcelYes.setEnabled(false);
//			Frame1.rdbattachmentFolderFromExcelNo.setEnabled(false);

			System.out.println("Script is failed");
			File f = new File(outputFolderName);
			if (f.exists() && f.isDirectory()) {
				File dir = new File(outputFolderName);
				String zipDirName = zipFilename;

				zipDirectory(dir, zipDirName);
			} else {
			}
			Thread.sleep(1500);
			File file = new File(zipFilename); // handler to your ZIP file
			File file2 = new File(renamedFileName); // destination dir of your file
			file.renameTo(file2);
		}

		if (error.isEmpty()) {
			error = "Something problem in script";
		}

		if (Frame1.stop == true) {
		} else {
			mailSend mailSend1 = new mailSend();
			mailSend1.mail(renamedFileName, uname, error);
		}

		Thread.sleep(2000);
		File file = new File(renamedFileName);
		if (file.exists()) {
			file.delete();
		}

		if (Frame1.stop == true) {
		} else {
			driver.close();
			driver.quit();
		}

//		driver.close();
//		driver.quit();

		if (testcase == true) {
			Frame1.alertMessage("Script is done");
		} else {
			Frame1.alertMessage("Script is stopped 2");
		}

		Frame1.btnFileUpload.setEnabled(true);
		Frame1.txtFileUpload.setEnabled(true);
		Frame1.btnExecuteScript.setEnabled(false);
		Frame1.btnSetImageFolder.setEnabled(true);
		Frame1.txtSetImageFolder.setEnabled(true);
		Frame1.rdbChromeYes.setEnabled(true);
		Frame1.rdbChromeNo.setEnabled(true);
//		Frame1.rdbattachmentFolderFromExcelYes.setEnabled(true);
//		Frame1.rdbattachmentFolderFromExcelNo.setEnabled(true);
	}

	public void zipDirectory(File dir, String zipDirName) throws Exception {

		populateFilesList(dir);
		// now zip files one by one
		// create ZipOutputStream to write to the zip file
		FileOutputStream fos = new FileOutputStream(zipDirName);
		ZipOutputStream zos = new ZipOutputStream(fos);
		for (String filePath : filesListInDir) {
//				System.out.println("Zipping " + filePath);
			// for ZipEntry we need to keep only relative file path, so we used substring on
			// absolute path
			ZipEntry ze = new ZipEntry(filePath.substring(dir.getAbsolutePath().length() + 1, filePath.length()));
			zos.putNextEntry(ze);
			// read the file and write to ZipOutputStream
			FileInputStream fis = new FileInputStream(filePath);
			byte[] buffer = new byte[1024];
			int len;
			while ((len = fis.read(buffer)) > 0) {
				zos.write(buffer, 0, len);
			}
			zos.closeEntry();
			fis.close();
		}
		zos.close();
		fos.close();
	}

	public void populateFilesList(File dir) throws Exception {
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isFile())
				filesListInDir.add(file.getAbsolutePath());
			else
				populateFilesList(file);
		}
	}

	public void task_bug_radio_button_selection() {
		if (taskType.getContents().toLowerCase().equals("task")) {
			// Task
			driver.findElement(By.xpath("//*[@class='radio-inline'][1]/input[1]")).click();
		} else if (taskType.getContents().toLowerCase().equals("bug")) {
			// Bug
			driver.findElement(By.xpath("//*[@class='radio-inline'][2]/input[1]")).click();
		}
	}
}
