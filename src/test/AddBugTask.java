package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
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
	Cell estimatedTime;
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
	String version = "v1.2";
	String error = "";
	String headless;
	File file;
	Sheet sh1;
	Sheet shac;
	Workbook wb;
	DateTimeFormatter dtf;
	LocalDateTime now;
	String acceptanceCriteria = "false";
	String projectName;
	String bug_tracking_sheet;
	CreateBugTrackingReport createBugTrackingReport;
	static String oltagStringGlobal = "";
	static String ACFileAvailable = "false";
	String DevTrackerStageAccessUsername;
	String DevTrackerStageAccessPassword;
	MultipleFileUpload multipleFileUpload;
	String imagePath;
	File src;

	public boolean checkFiles() {
		File file = new File("tokens/StoredCredential");
		if (file.exists()) {
			file = new File("client_secret.json");
			if (file.exists()) {
				return true;
			} else {
				Frame1.appendText("client_secret json file is not available.");
				Frame1.appendText(
						"Please download latest build from https://drive.google.com/file/d/1AZ1fZAnymK04nUap41C3FNhZsYKQexnU/view");
				return false;
			}
		} else {
			Frame1.appendText("StoredCredential file is not available in tokens folder.");
			Frame1.appendText(
					"Please download latest build from https://drive.google.com/file/d/1AZ1fZAnymK04nUap41C3FNhZsYKQexnU/view");
			return false;
		}
	}

	public boolean checkVersion() throws GeneralSecurityException, IOException, BiffException, InterruptedException {
		if (GetSheetData.getData("Dev Tracker!D1").get(0).get(0).toString().equals(version)) {
			GetSheetData.googleSheetConnection();

			if (Frame1.filePath.equals("")) {
				src = new File("Ticket.xls");
			} else {
				src = new File(Frame1.filePath);
			}

			wb = Workbook.getWorkbook(src);
			sh1 = wb.getSheet(0);
			shac = wb.getSheet("Automation Acceptance Criteria");
			username = sh1.getCell(1, 1);
			uname = username.getContents();

			return true;
		} else {
			Frame1.appendText("Please download latest build from "
					+ GetSheetData.getData("Dev Tracker!D2").get(0).get(0).toString());
			mailSend.mail(renamedFileName, uname,
					"Version is mismatch. " + uname + " user is working on " + version + " build", Frame1.stage);
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
		ApprovedBugsByPM = false;
		DeferredBugsByPM = false;
		BranchCreateSheet = false;
		ACFileAvailable = "false";

		DevTrackerStageURL = GetSheetData.getData("Dev Tracker!B8").get(0).get(0).toString();
		DevTrackerStageAccessUsername = GetSheetData.getData("Dev Tracker!B9").get(0).get(0).toString();
		DevTrackerStageAccessPassword = GetSheetData.getData("Dev Tracker!B10").get(0).get(0).toString();

		dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
		now = LocalDateTime.now();
		Frame1.appendText(dtf.format(now));

		if (Frame1.filePath.equals("")) {
			src = new File("Ticket.xls");
		} else {
			src = new File(Frame1.filePath);
		}

		wb = Workbook.getWorkbook(src);

		sh1 = wb.getSheet(0);
		shac = wb.getSheet("Automation Acceptance Criteria");

		String[] sheetNames = wb.getSheetNames();

		DevTrackerURL = sh1.getCell(1, 0);

		if (DevTrackerURL.getContents().trim().equals(DevTrackerStageURL)) {
			Frame1.stage = true;
		} else {
			Frame1.stage = false;
		}

		if (checkVersion() == true) {
			password = sh1.getCell(1, 2);

			imagePath = Frame1.imageDirPath;

			Frame1.appendText("image path = " + imagePath);

			bug_tracking_sheet = sh1.getCell(3, 0).getContents();

			if (DevTrackerURL.getContents().trim().equals(DevTrackerStageURL)) {
			} else {
				if (GetSheetData.getData("Dev Tracker!B1").get(0).get(0).toString().equalsIgnoreCase("yes")) {
					mailSend.mail(Frame1.filePath, uname, "start", Frame1.stage);
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
				} else if (sheetNames[i].equals(GetSheetData.getData("Dev Tracker!B15").get(0).get(0).toString())) {
					assert true;
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

		while (row < sh1.getRows()) {
			// column, row
			Cell project_name = sh1.getCell(
					Integer.parseInt(GetSheetData.getData("Excel Sheet Column!B1").get(0).get(0).toString()), row);

			if ((row + 1) < sh1.getRows()) {
				project_name_next_row_acceptance_criteria = sh1.getCell(
						Integer.parseInt(GetSheetData.getData("Excel Sheet Column!B1").get(0).get(0).toString()),
						row + 1);
			} else {
				project_name_next_row_acceptance_criteria = null;
			}

			if (project_name.getContents().isEmpty()) {
				Frame1.appendText("Data are completed");
				break;
			}

			if (project_name_next_row_acceptance_criteria == null
					|| project_name_next_row_acceptance_criteria.getContents().isEmpty()) {

			}

			if (project_name.getContents().equals(GetSheetData.getData("Dev Tracker!B7").get(0).get(0).toString())) {

				if (ACFileAvailable.equals("true")) {
					acceptanceCriteria = "false";
				} else {
					acceptanceCriteria = "true";
					cos = sh1.getCell(
							Integer.parseInt(GetSheetData.getData("Excel Sheet Column!B15").get(0).get(0).toString()),
							row);
				}
			} else {
				ACFileAvailable = "false";
				acceptanceCriteria = "false";
			}

			if ("false".equals(acceptanceCriteria)) {
				milestone = sh1.getCell(
						Integer.parseInt(GetSheetData.getData("Excel Sheet Column!B2").get(0).get(0).toString()), row);
				taskcategory = sh1.getCell(
						Integer.parseInt(GetSheetData.getData("Excel Sheet Column!B3").get(0).get(0).toString()), row);
				taskType = sh1.getCell(
						Integer.parseInt(GetSheetData.getData("Excel Sheet Column!B10").get(0).get(0).toString()), row);
				taskTitle = sh1.getCell(
						Integer.parseInt(GetSheetData.getData("Excel Sheet Column!B12").get(0).get(0).toString()), row);
				objective = sh1.getCell(
						Integer.parseInt(GetSheetData.getData("Excel Sheet Column!B13").get(0).get(0).toString()), row);
				references = sh1.getCell(
						Integer.parseInt(GetSheetData.getData("Excel Sheet Column!B14").get(0).get(0).toString()), row);
				cos = sh1.getCell(
						Integer.parseInt(GetSheetData.getData("Excel Sheet Column!B15").get(0).get(0).toString()), row);
				pmComment = sh1.getCell(
						Integer.parseInt(GetSheetData.getData("Excel Sheet Column!B16").get(0).get(0).toString()), row);
				pmName = sh1.getCell(
						Integer.parseInt(GetSheetData.getData("Excel Sheet Column!B16").get(0).get(0).toString()), 4);
				dependent = sh1.getCell(
						Integer.parseInt(GetSheetData.getData("Excel Sheet Column!B4").get(0).get(0).toString()), row);
				successor = sh1.getCell(
						Integer.parseInt(GetSheetData.getData("Excel Sheet Column!B5").get(0).get(0).toString()), row);
				priority = sh1.getCell(
						Integer.parseInt(GetSheetData.getData("Excel Sheet Column!B11").get(0).get(0).toString()), row);
				if (taskStatus.equals("Any")) {
					taskStatus = sh1.getCell(
							Integer.parseInt(GetSheetData.getData("Excel Sheet Column!B18").get(0).get(0).toString()),
							row).getContents();
				}
				estimatedTime = sh1.getCell(
						Integer.parseInt(GetSheetData.getData("Excel Sheet Column!B17").get(0).get(0).toString()), row);
				assignee = sh1.getCell(
						Integer.parseInt(GetSheetData.getData("Excel Sheet Column!B6").get(0).get(0).toString()), row);
				reporter = sh1.getCell(
						Integer.parseInt(GetSheetData.getData("Excel Sheet Column!B7").get(0).get(0).toString()), row);
				uploadDocuments = sh1.getCell(
						Integer.parseInt(GetSheetData.getData("Excel Sheet Column!B9").get(0).get(0).toString()), row);
				originator = sh1.getCell(
						Integer.parseInt(GetSheetData.getData("Excel Sheet Column!B8").get(0).get(0).toString()), row);

				if (milestone.getContents().isEmpty() || taskcategory.getContents().isEmpty()
						|| taskType.getContents().isEmpty() || taskTitle.getContents().isEmpty()
						|| objective.getContents().isEmpty() || priority.getContents().isEmpty()) {
					Frame1.appendText(
							"Milestone / Task Category / Task Type / Task Title / Objective / Priority data are not added in "
									+ (row + 1) + " row");
					driver.close();
					driver.quit();
					break;
				}

				BranchMilestone = milestone.getContents().trim();
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
				String milestoneText = null;

				for (WebElement we : allOptions) {
					if (we.getText().trim().equals(milestone.getContents().trim())) {
						milestoneText = we.getText();
						found = true;
					}
				}
				if (Boolean.TRUE.equals(found)) {
					selec.selectByVisibleText(milestoneText);
				} else {
					// Create new milestone
					driver.findElement(By.id("addmilestone")).click();
					Thread.sleep(1500);
					driver.findElement(By.id("milestone_name")).clear();
					driver.findElement(By.id("milestone_name")).sendKeys(milestone.getContents().trim());
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

					String taskcategoryText = null;

					for (WebElement we : allOptions1) {
						if (we.getText().trim().equals(taskcategory.getContents().trim())) {
							taskcategoryText = we.getText();
							found = true;
						}
					}

					Thread.sleep(1500);

					if (Boolean.TRUE.equals(found)) {
						selec1.selectByVisibleText(taskcategoryText);
					} else {
						// Create new taskcategory
						driver.findElement(By.id("addcategory")).click();
						Thread.sleep(1500);
						driver.findElement(By.id("category_name")).clear();
						driver.findElement(By.id("category_name")).sendKeys(taskcategory.getContents().trim());
						driver.findElement(By.id("savecategory")).click();
					}
				} catch (StaleElementReferenceException e) {
					Select selec1 = new Select(driver.findElement(By.id("taskcategory_id")));

					found = false;
					List<WebElement> allOptions1 = selec1.getOptions();

					String taskcategoryText = null;

					for (WebElement we : allOptions1) {
						if (we.getText().trim().equals(taskcategory.getContents().trim())) {
							taskcategoryText = we.getText();
							found = true;
						}
					}

					if (Boolean.TRUE.equals(found)) {
						selec1.selectByVisibleText(taskcategoryText);
					} else {
						// Create new taskcategory
						driver.findElement(By.id("addcategory")).click();
						Thread.sleep(1500);
						driver.findElement(By.id("category_name")).clear();
						driver.findElement(By.id("category_name")).sendKeys(taskcategory.getContents().trim());
						driver.findElement(By.id("savecategory")).click();
					}
				}

				task_bug_radio_button_selection();

				// submit button click for validation verification
				driver.findElement(By.xpath(GetSheetData.getData("Dev Tracker Xpath!B4").get(0).get(0).toString()))
						.click();

				if (driver.findElements(By.xpath("//*[@id='taskcategory_id']//following::ul[1]/li")).size() > 0) {
					try {
						Select selec1 = new Select(driver.findElement(By.id("taskcategory_id")));

						found = false;
						List<WebElement> allOptions1 = selec1.getOptions();

						String taskcategoryText = null;

						for (WebElement we : allOptions1) {
							if (we.getText().trim().equals(taskcategory.getContents().trim())) {
								taskcategoryText = we.getText();
								found = true;
							}
						}

						if (Boolean.TRUE.equals(found)) {
							selec1.selectByVisibleText(taskcategoryText);
						} else {
							// Create new taskcategory
							driver.findElement(By.id("addcategory")).click();
							Thread.sleep(1500);
							driver.findElement(By.id("category_name")).clear();
							driver.findElement(By.id("category_name")).sendKeys(taskcategory.getContents().trim());
							driver.findElement(By.id("savecategory")).click();
						}
					} catch (StaleElementReferenceException e) {
						Select selec1 = new Select(driver.findElement(By.id("taskcategory_id")));

						found = false;
						List<WebElement> allOptions1 = selec1.getOptions();

						String taskcategoryText = null;

						for (WebElement we : allOptions1) {
							if (we.getText().trim().equals(taskcategory.getContents().trim())) {
								taskcategoryText = we.getText();
								found = true;
							}
						}

						if (Boolean.TRUE.equals(found)) {
							selec1.selectByVisibleText(taskcategoryText);
						} else {
							// Create new taskcategory
							driver.findElement(By.id("addcategory")).click();
							Thread.sleep(1500);
							driver.findElement(By.id("category_name")).clear();
							driver.findElement(By.id("category_name")).sendKeys(taskcategory.getContents().trim());
							driver.findElement(By.id("savecategory")).click();
						}
					}
				}

				if (driver.findElements(By.xpath("//*[@id='parsley-id-multiple-type_id']/li")).size() > 0) {
					Frame1.appendText(taskType.getContents() + " selection again");
					error = taskType.getContents() + " selection again";

					task_bug_radio_button_selection();
				}

				driver.findElement(By.id("task_name")).clear();
				driver.findElement(By.id("task_name")).sendKeys(taskTitle.getContents());

				js = (JavascriptExecutor) driver;
				js.executeScript("window.scrollBy(0,250)");

				driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]")).click();
				oltagStringGlobal = "";
				// reference
				if (references.getContents().isEmpty()) {
				} else {
					macTextFormat(imagePath, "", "", references, "p[4]", sh1, row);
				}

				oltagStringGlobal = "";
				// Objective / Steps to Recreate
				macTextFormat(imagePath, "", "", objective, "p[2]", sh1, row);
			}

			Dimension newDimension = new Dimension(1920, 1080);
			if (!driver.manage().window().getSize().equals(newDimension)) {
				driver.manage().window().setSize(newDimension);
			}
			oltagStringGlobal = "";
			// COS
			macTextFormat(imagePath, pmName.getContents(), pmComment.getContents(), cos, "xyz", shac, row);

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
								mailSend.mail(renamedFileName, uname, error, Frame1.stage);
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
								mailSend.mail(renamedFileName, uname, error, Frame1.stage);
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

				if (!estimatedTime.getContents().isEmpty()) {
					driver.findElement(By.xpath(GetSheetData.getData("Dev Tracker Xpath!B15").get(0).get(0).toString()))
							.sendKeys(estimatedTime.getContents());
					Thread.sleep(1000);
				}

				if (uploadDocuments.getContents().isEmpty()) {
				} else {
					multipleFileUpload.fileUpload(driver, imagePath, uploadDocuments);
					driver.findElement(By.id("startall")).click();

					int tmp = 0;
					Frame1.appendText("documents are uploading");
					do {
						int size = driver.findElements(By.xpath("//*[@id='basic-uploader']//span[text()='Upload']"))
								.size();
						if (size > 0) {
							tmp = 0;
						} else {
							tmp = 1;
						}
					} while (tmp == 0);
					js = (JavascriptExecutor) driver;
					js.executeScript("window.scrollBy(0,250)");
					Frame1.appendText("documents are attached");
				}
			}

			if (AddBugTask.ACFileAvailable.equals("true")) {
				removeExtraSpace();
				now = LocalDateTime.now();
				Frame1.appendText(dtf.format(now));
				driver.findElement(By.xpath(GetSheetData.getData("Dev Tracker Xpath!B4").get(0).get(0).toString()))
						.click();
				Thread.sleep(1000);

				// Estimate Time error message
				int sizeEstimateTime = driver
						.findElements(By.xpath(GetSheetData.getData("Dev Tracker Xpath!B14").get(0).get(0).toString()))
						.size();
				if (sizeEstimateTime > 0) {
					driver.findElement(By.xpath(GetSheetData.getData("Dev Tracker Xpath!B15").get(0).get(0).toString()))
							.clear();
					Thread.sleep(1000);
					driver.findElement(By.xpath(GetSheetData.getData("Dev Tracker Xpath!B4").get(0).get(0).toString()))
							.click();
				}

				// Alert box for attached word
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
				removeExtraSpace();
				now = LocalDateTime.now();
				Frame1.appendText(dtf.format(now));
				driver.findElement(By.xpath(GetSheetData.getData("Dev Tracker Xpath!B4").get(0).get(0).toString()))
						.click();

				// Estimate Time error message
				int sizeEstimateTime = driver
						.findElements(By.xpath(GetSheetData.getData("Dev Tracker Xpath!B14").get(0).get(0).toString()))
						.size();
				if (sizeEstimateTime > 0) {
					driver.findElement(By.xpath(GetSheetData.getData("Dev Tracker Xpath!B15").get(0).get(0).toString()))
							.clear();
					Thread.sleep(1000);
					driver.findElement(By.xpath(GetSheetData.getData("Dev Tracker Xpath!B4").get(0).get(0).toString()))
							.click();
				}

				// Alert box for attached word
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

			File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			File destFile;

			systemName = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
			if (systemName.contains("mac")) {
				destFile = new File(System.getProperty("user.dir") + "/test-output/problem.png");
			} else {
				destFile = new File(System.getProperty("user.dir") + "\\test-output\\problem.png");
			}
			FileUtils.copyFile(screenshot, destFile);

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
			mailSend1.mail(renamedFileName, uname, error, Frame1.stage);
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
