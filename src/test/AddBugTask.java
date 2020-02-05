package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

@Listeners(ListenerTest.class)
public class AddBugTask extends Utilities {

	mailSend mailSend = new mailSend();
	Cell username;
	String zipFilename = "test-output.zip";
	String outputFolderName = "test-output";
	String renamedFileName = "testoutput.txt";
	String DevTrackerNumber = "";
	boolean BranchCreateSheet = false;
	String BranchMilestone = "baseproject";
	Cell DevTrackerURL;
	Cell taskType;
	String DevTrackerStageURL;
	String version = "v1.1";
	String error = "";
	String headless = "";

	public void add_bug_task(String filePath, String imageDirPath) throws Exception {
		File file = new File("tokens/StoredCredential");
		if (file.exists()) {
		} else {
//			System.out.println();
			Frame1.appendText("StoredCredential file is not available in tokens folder.");
			Frame1.appendText(
					"Please download latest build from https://drive.google.com/open?id=1dI-bVzWUoGtyLgeywZzLBii99lJYVNCG");
			System.exit(0);
		}
		
		Thread.sleep(10000);

		file = new File("client_secret.json");
		if (file.exists()) {
		} else {
//			System.out.println();
			Frame1.appendText("client_secret json file is not available.");
			Frame1.appendText(
					"Please download latest build from https://drive.google.com/open?id=1dI-bVzWUoGtyLgeywZzLBii99lJYVNCG");
			System.exit(0);
		}

		GetSheetData.googleSheetConnection();
		File src = new File(filePath);
		Workbook wb = Workbook.getWorkbook(src);
		Sheet sh1 = wb.getSheet(0);
		username = sh1.getCell(1, 1);

		if (GetSheetData.getData("Dev Tracker!D1").get(0).get(0).toString().equals(version)) {
		} else {
//			System.out.println();
			Frame1.appendText("Please download latest build from "
					+ GetSheetData.getData("Dev Tracker!D2").get(0).get(0).toString());
			mailSend.mail(renamedFileName, username.getContents(),
					"Version is mismatch. " + username.getContents() + " user is working on " + version + " build");
			System.exit(0);
		}

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		Frame1.appendText(dtf.format(now));

		String[] sheetNames = wb.getSheetNames();
		for (int i = 0; i < sheetNames.length; i++) {
			if (sheetNames[i].equals("BranchCreate")) {
				BranchCreateSheet = true;
				break;
			}
		}

		DevTrackerStageURL = GetSheetData.getData("Dev Tracker!B8").get(0).get(0).toString();
		String DevTrackerStageAccessUsername = GetSheetData.getData("Dev Tracker!B9").get(0).get(0).toString();
		String DevTrackerStageAccessPassword = GetSheetData.getData("Dev Tracker!B10").get(0).get(0).toString();

		// column, row
		DevTrackerURL = sh1.getCell(1, 0);

		Cell password = sh1.getCell(1, 2);
//		String imagePath = sh1.getCell(1, 3).getContents();
		String imagePath = imageDirPath + "\\";
		String bug_tracking_sheet = sh1.getCell(3, 0).getContents();

		if (DevTrackerURL.getContents().trim().equals(DevTrackerStageURL)) {

		} else {
			if (GetSheetData.getData("Dev Tracker!B1").get(0).get(0).toString().toLowerCase().equals("yes")) {
				mailSend.mail(renamedFileName, username.getContents(), "start");
			} else {
				Frame1.appendText("no option for mail on start");
			}
		}

		MultipleFileUpload multipleFileUpload = new MultipleFileUpload();
		CreateBugTrackingReport createBugTrackingReport = new CreateBugTrackingReport();

		headless = sh1.getCell(3, 1).getContents();
		openBrowser(headless);

		driver.get(DevTrackerURL.getContents());

		if (DevTrackerURL.getContents().trim().equals(DevTrackerStageURL)) {
			driver.findElement(By.name("access_login")).sendKeys(DevTrackerStageAccessUsername);
			driver.findElement(By.name("access_password")).sendKeys(DevTrackerStageAccessPassword);
			driver.findElement(By.name("access_password")).sendKeys(Keys.ENTER);
		}
		Thread.sleep(1000);
		driver.findElement(By.name("username")).click();
		driver.findElement(By.name("username")).clear();
		driver.findElement(By.name("username")).sendKeys(username.getContents());
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password")).clear();
		driver.findElement(By.name("password")).sendKeys(password.getContents());
		driver.findElement(By.name("password")).sendKeys(Keys.ENTER);

		Thread.sleep(2000);

		int row = 5;

		while (row < sh1.getRows()) {
			// column, row
			Cell project_name = sh1.getCell(0, row);
			Cell milestone = sh1.getCell(1, row);
			Cell taskcategory = sh1.getCell(2, row);
			taskType = sh1.getCell(3, row);
			Cell taskTitle = sh1.getCell(4, row);
			Cell objective = sh1.getCell(5, row);
			Cell references = sh1.getCell(6, row);
			Cell cos = sh1.getCell(7, row);
			Cell dependent = sh1.getCell(8, row);
			Cell successor = sh1.getCell(9, row);
			Cell priority = sh1.getCell(10, row);
			Cell taskStatus = sh1.getCell(11, row);
			Cell assignee = sh1.getCell(12, row);
			Cell reporter = sh1.getCell(13, row);
			Cell uploadDocuments = sh1.getCell(14, row);
			Cell originator = sh1.getCell(15, row);

			if (project_name.getContents().isEmpty()) {
				Frame1.appendText("Data are completed");
				break;
			}

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
				Thread.sleep(2000);
			} else {
				driver.get(DevTrackerURL.getContents() + "index.php?route=common/task/loadDetailForm&project_id=0");
				Thread.sleep(2000);
				driver.findElement(By.xpath(GetSheetData.getData("Dev Tracker Xpath!B1").get(0).get(0).toString()))
						.click();
				driver.findElement(By.xpath(GetSheetData.getData("Dev Tracker Xpath!B2").get(0).get(0).toString()))
						.sendKeys(project_name.getContents());
				driver.findElement(By.xpath(GetSheetData.getData("Dev Tracker Xpath!B2").get(0).get(0).toString()))
						.sendKeys(Keys.ENTER);
				Thread.sleep(2000);
			}

			if (driver.findElements(By.xpath("//*[@id=\"gritter-item-1\"]/div[2]/a")).size() > 0) {
				driver.findElement(By.xpath("//*[@id=\"gritter-item-1\"]/div[2]/a")).click();
				Thread.sleep(2000);
			}

			String projectName = driver
					.findElement(By.xpath(GetSheetData.getData("Dev Tracker Xpath!B3").get(0).get(0).toString()))
					.getText();

			Select selec = new Select(driver.findElement(By.id("milestone_id")));

			Boolean found = false;
			List<WebElement> allOptions = selec.getOptions();
			Thread.sleep(1000);
			for (WebElement we : allOptions) {
				if (we.getText().equals(milestone.getContents())) {
					found = true;
					Thread.sleep(1000);
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

			Thread.sleep(1000);
			try {
				Select selec1 = new Select(driver.findElement(By.id("taskcategory_id")));

				found = false;
				List<WebElement> allOptions1 = selec1.getOptions();
				Thread.sleep(1000);
				for (WebElement we : allOptions1) {
					if (we.getText().equals(taskcategory.getContents())) {
						found = true;
						Thread.sleep(1000);
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

				Thread.sleep(1000);
				found = false;
				List<WebElement> allOptions1 = selec1.getOptions();

				for (WebElement we : allOptions1) {
					if (we.getText().equals(taskcategory.getContents())) {
						found = true;
					}
				}
				Thread.sleep(1000);
				if (found == true) {
					selec1.selectByVisibleText(taskcategory.getContents());
				} else {
					// Create new taskcategory
					driver.findElement(By.id("addcategory")).click();
					driver.findElement(By.id("category_name")).sendKeys(taskcategory.getContents());
					driver.findElement(By.id("savecategory")).click();
				}
			}

			Thread.sleep(1000);

			task_bug_radio_button_selection();

			// submit button click for validation verification
			driver.findElement(By.xpath(GetSheetData.getData("Dev Tracker Xpath!B4").get(0).get(0).toString())).click();

			Thread.sleep(1000);
			if (driver.findElements(By.xpath("//*[@id='parsley-id-multiple-type_id']/li")).size() > 0) {
				Frame1.appendText(taskType.getContents() + " selection again");
				error = taskType.getContents() + " selection again";
				Thread.sleep(1000);
				task_bug_radio_button_selection();
			}

			driver.findElement(By.id("task_name")).sendKeys(taskTitle.getContents());

			js = (JavascriptExecutor) driver;
			js.executeScript("window.scrollBy(0,250)");

			driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]")).click();
			Thread.sleep(1000);

			// reference
			if (references.getContents().isEmpty()) {
			} else {
				macTextFormat(imagePath, references, "p[4]");
//				if (systemName.contains("mac")) {
//					macTextFormat(imagePath, references, "p[4]");
//				} else {
//					textFormat(imagePath, references);
//				}
			}

			// Objective / Steps to Recreate

//			Actions action = new Actions(driver);
//			if (systemName.contains("mac")) {
//				macTextFormat(imagePath, objective, "p[2]");
//			} else {
//				action.keyDown(Keys.CONTROL).sendKeys(Keys.HOME).build().perform();
//				action.keyUp(Keys.CONTROL).build().perform();
//
//				driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
//						.sendKeys(Keys.ARROW_DOWN);
//				textFormat(imagePath, objective);
//				driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]")).sendKeys(Keys.DELETE);
//			}

			macTextFormat(imagePath, objective, "p[2]");

			// COS

//			action = new Actions(driver);

			macTextFormat(imagePath, cos, "xyz");
//			if (systemName.contains("mac")) {
//				macTextFormat(imagePath, cos, "xyz");
//			} else {
//				action.keyDown(Keys.CONTROL).sendKeys(Keys.END).build().perform();
//				action.keyUp(Keys.CONTROL).build().perform();
//
//				textFormat(imagePath, cos);
//				driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
//						.sendKeys(Keys.BACK_SPACE);
//			}

			removeExtraSpace();

			// Description verification

			if ((driver
					.findElements(By.xpath(
							"//span[text()='" + GetSheetData.getData("Dev Tracker!B4").get(0).get(0).toString() + "']"))
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

			Thread.sleep(1000);

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
								.findElements(
										By.xpath(GetSheetData.getData("Dev Tracker Xpath!B8").get(0).get(0).toString()))
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
						if (driver.findElement(By.xpath(
								"//*[@id='frmaddedit']/div[7]/div[3]/table/tbody/tr[" + dependentCount + "]/td[1]/a"))
								.getText().equals(arrSplit[i])) {
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
									+ driver.findElement(By.xpath("//*[@id='frmaddedit']/div[7]/div[3]/table/tbody/tr["
											+ dependentCount + "]/td[1]/a")).getText();
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
								.findElements(By
										.xpath(GetSheetData.getData("Dev Tracker Xpath!B12").get(0).get(0).toString()))
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
						if (driver.findElement(By.xpath(
								"//*[@id='frmaddedit']/div[8]/div[3]/table/tbody/tr[" + dependentCount + "]/td[1]/a"))
								.getText().equals(arrSplit[i])) {
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

							error = "Issue " + arrSplit[i] + " Dependent Successor is not matched with "
									+ driver.findElement(By.xpath("//*[@id='frmaddedit']/div[8]/div[3]/table/tbody/tr["
											+ dependentCount + "]/td[1]/a")).getText();
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
				System.exit(1);
			} else {
				Select selec2 = new Select(driver.findElement(By.id("priority_id")));
				selec2.selectByVisibleText(priority.getContents());
			}

			if (taskStatus.getContents().isEmpty()) {
				Frame1.appendText("Status is not available so set as default");
			} else {
				Thread.sleep(1500);
				List<WebElement> options = driver.findElements(By.xpath("//select[@id='status']/option"));

				for (WebElement option : options) {
					if (option.getText().contains(taskStatus.getContents())) {
						option.click();
						break;
					}
				}
			}

			if (assignee.getContents().isEmpty()) {
				Frame1.appendText("Assignee user is not available in excel sheet");
			} else {
				Thread.sleep(1500);
				List<WebElement> options = driver.findElements(By.xpath("//select[@id='assign_user_id']/option"));
				for (WebElement option : options) {
					if (option.getText().contains(assignee.getContents())) {
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
						option.click();
						break;
					}
				}
			}

			if (uploadDocuments.getContents().isEmpty()) {
				Frame1.appendText("Documents are not available in excel sheet");
			} else {
				multipleFileUpload.fileUpload(driver, imagePath, uploadDocuments);
				driver.findElement(By.id("startall")).click();
				Thread.sleep(1500);

				int tmp = 0;

				do {
					if (driver.findElements(By.xpath("//div[@role='progressbar']")).size() > 0
							&& driver.findElement(By.xpath("//div[@role='progressbar']")).getAttribute("aria-valuenow")
									.equals("100")) {
						tmp = 1;
						Frame1.appendText("Documents are uploaded");
					} else if (driver.findElements(By.xpath("//div[@role='progressbar']")).size() > 0
							&& driver.findElement(By.xpath("//div[@role='progressbar']")).getAttribute("aria-valuenow")
									.equals("0")) {
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

			now = LocalDateTime.now();
			Frame1.appendText(dtf.format(now));
//			driver.findElement(By.xpath(GetSheetData.getData("Dev Tracker Xpath!B4").get(0).get(0).toString())).click();
			checkLoader();
			testcase = true;
			error = "complete";
			driver.findElement(By.tagName("body")).sendKeys(Keys.HOME);

			DevTrackerNumber = driver.getCurrentUrl().replace(DevTrackerURL.getContents() + "track/", "");
			Frame1.appendText(DevTrackerNumber);

			if (bug_tracking_sheet.toLowerCase().equals("yes")) {
				createBugTrackingReport.createBugTracking(driver, DevTrackerURL.getContents(), taskTitle.getContents(),
						projectName, originator.getContents(), reporter.getContents(), taskType.getContents());
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
			Thread.sleep(1000);
			driver.findElement(By.xpath("//*[@id='login-submit']")).click();
			driver.findElement(By.xpath("//*[@id='password']")).clear();
			driver.findElement(By.xpath("//*[@id='password']"))
					.sendKeys(BranchCreateSheetName.getCell(1, 2).getContents());
			Thread.sleep(1000);
			driver.findElement(By.xpath("//*[@id='login-submit']")).click();

			boolean repositoriesMatch = false;
			int repositoriesrow = 1;
			BranchMilestone = "baseproject";
			while (repositoriesMatch == false) {
				Thread.sleep(1500);
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

//		driver.close();
//		driver.quit();
//		System.exit(0);
	}

	@AfterSuite
	public void bugadd_fun_verify() throws Exception, InterruptedException {
		try {
			Assert.assertTrue(testcase);
		} catch (AssertionError e) {
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

		mailSend.mail(renamedFileName, username.getContents(), error);

		Thread.sleep(2000);

		File file = new File(renamedFileName);
		if (file.exists()) {
			file.delete();
		}
		driver.close();
		driver.quit();
		System.exit(0);
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
