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
	
	@org.testng.annotations.Test
	public void add_bug_task() throws Exception {

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		System.out.println(dtf.format(now));

		File src = new File("Ticket.xls");
		Workbook wb = Workbook.getWorkbook(src);
		Sheet sh1 = wb.getSheet(0);

		// column, row
		Cell DevTrackerURL = sh1.getCell(1, 0);
		username = sh1.getCell(1, 1);
		Cell password = sh1.getCell(1, 2);
		String imagePath = sh1.getCell(1, 3).getContents();
		String bug_tracking_sheet = sh1.getCell(3, 0).getContents();

		MultipleFileUpload multipleFileUpload = new MultipleFileUpload();
		CreateBugTrackingReport createBugTrackingReport = new CreateBugTrackingReport();

		openBrowser();

		driver.get(DevTrackerURL.getContents());

		if (DevTrackerURL.getContents().trim().equals("https://devtracker.devdigdev.com/")) {
			driver.findElement(By.name("access_login")).sendKeys("devtracker");
			driver.findElement(By.name("access_password")).sendKeys("devtracker@022015");
			driver.findElement(By.name("access_password")).sendKeys(Keys.ENTER);
		}
		Thread.sleep(1000);
		driver.findElement(By.name("username")).click();
		driver.findElement(By.name("username")).sendKeys(username.getContents());
		driver.findElement(By.name("password")).click();
		driver.findElement(By.name("password1")).sendKeys(password.getContents());
		driver.findElement(By.name("password")).sendKeys(Keys.ENTER);

		Thread.sleep(2000);

		int row = 5;

		while (row < sh1.getRows()) {
			// column, row
			Cell project_name = sh1.getCell(0, row);
			Cell milestone = sh1.getCell(1, row);
			Cell taskcategory = sh1.getCell(2, row);
			Cell taskType = sh1.getCell(3, row);
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
				System.out.println("Data are completed");
				break;
			}

			if (milestone.getContents().isEmpty() || taskcategory.getContents().isEmpty()
					|| taskType.getContents().isEmpty() || taskTitle.getContents().isEmpty()
					|| objective.getContents().isEmpty() || priority.getContents().isEmpty()) {
				System.out.println(
						"Milestone / Task Category / Task Type / Task Title / Objective / Priority data are not added in "
								+ (row + 1) + " row");
				driver.close();
				driver.quit();
				System.exit(1);
			}

			System.out.println(taskType.getContents() + " is adding");
			System.out.println("Title is = " + taskTitle.getContents());
			testcase = false;

			if (project_name.getContents().matches("[0-9]+")) {
				driver.get(DevTrackerURL.getContents() + "index.php?route=common/task/loadDetailForm&project_id="
						+ project_name.getContents());
				Thread.sleep(2000);
			} else {
				driver.get(DevTrackerURL.getContents() + "index.php?route=common/task/loadDetailForm&project_id=0");
				Thread.sleep(2000);
				driver.findElement(By.xpath("//*[@id=\"frmaddedit\"]/div[1]/div/div")).click();
				driver.findElement(By.xpath("//*[@id=\"frmaddedit\"]/div[1]/div/div/div/input"))
						.sendKeys(project_name.getContents());
				driver.findElement(By.xpath("//*[@id=\"frmaddedit\"]/div[1]/div/div/div/input")).sendKeys(Keys.ENTER);
				Thread.sleep(2000);
			}

			String projectName = driver.findElement(By.xpath("//*[@id=\"header\"]/div[2]/ul[1]/li[3]/h4/a")).getText();

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

			if (taskType.getContents().toLowerCase().equals("task")) {
				// Task
				driver.findElement(By.xpath("//*[@class='radio-inline'][1]/input[1]")).click();
			} else if (taskType.getContents().toLowerCase().equals("bug")) {
				// Bug
				driver.findElement(By.xpath("//*[@class='radio-inline'][2]/input[1]")).click();
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

			if ((driver.findElements(By.xpath("//span[text()='Objective']")).size() > 0
					|| driver.findElements(By.xpath("//span[text()='Steps to Recreate']")).size() > 0)
					&& driver.findElements(By.xpath("//span[text()='References']")).size() > 0
					&& driver.findElements(By.xpath("//span[text()='Conditions of Satisfaction']")).size() > 0) {
				System.out.println("Description is added properly");
			} else {
				System.out.println("Issue in added description");
			}

			Thread.sleep(1000);

			js = (JavascriptExecutor) driver;
			js.executeScript("window.scrollBy(0,250)");

			// Add Dependent Predecessor

			if (dependent.getContents().isEmpty()) {
			} else {
				try {
					String[] arrSplit = dependent.getContents().split("/");

					for (int i = 0; i < arrSplit.length; i++) {
						driver.findElement(By.xpath("//*[@id=\"frmaddedit\"]/div[7]/div[1]/div[1]/div")).click();
						driver.findElement(By.xpath("//*[@id=\"frmaddedit\"]/div[7]/div[1]/div[1]/div/div/input"))
								.sendKeys(arrSplit[i]);
						Thread.sleep(1000);
						driver.findElement(By.xpath("//*[@id=\"frmaddedit\"]/div[7]/div[1]/div[1]/div/div/input"))
								.sendKeys(Keys.ENTER);
						Thread.sleep(1000);
						driver.findElement(By.xpath("//*[@id=\"frmaddedit\"]/div[7]/div[1]/div[2]/button")).click();

						if (driver.findElements(By.xpath("/html/body/div[15]/div[2]/div/div[1]/div")).size() > 0) {
							if (driver.findElement(By.xpath("/html/body/div[15]/div[2]/div/div[1]/div")).getText()
									.startsWith("Please select any ")) {
								System.out.println("Some issue in Dependent task Popup text");
							} else {
								System.out.println("Some issue in Dependent task selection");
							}
						} else {
							System.out.println("Dependent task is added");
						}
					}
				} catch (ElementClickInterceptedException e) {
					js = (JavascriptExecutor) driver;
					js.executeScript("window.scrollBy(0,250)");
				}
			}

			// Add Dependent Successor

			if (successor.getContents().isEmpty()) {
			} else {
				try {
					String[] arrSplit = successor.getContents().split("/");

					for (int i = 0; i < arrSplit.length; i++) {
						driver.findElement(By.xpath("//*[@id=\"frmaddedit\"]/div[8]/div[1]/div[1]/div")).click();
						driver.findElement(By.xpath("//*[@id=\"frmaddedit\"]/div[8]/div[1]/div[1]/div/div/input"))
								.sendKeys(arrSplit[i]);
						Thread.sleep(1000);
						driver.findElement(By.xpath("//*[@id=\"frmaddedit\"]/div[8]/div[1]/div[1]/div/div/input"))
								.sendKeys(Keys.ENTER);
						Thread.sleep(1000);
						driver.findElement(By.xpath("//*[@id=\"frmaddedit\"]/div[8]/div[1]/div[2]/button")).click();

						if (driver.findElements(By.xpath("/html/body/div[15]/div[2]/div/div[1]/div")).size() > 0) {
							if (driver.findElement(By.xpath("/html/body/div[15]/div[2]/div/div[1]/div")).getText()
									.startsWith("Please select any ")) {
								System.out.println("Some issue in successor task Popup text");
							} else {
								System.out.println("Some issue in successor task selection");
							}
						} else {
							System.out.println("successor task is added");
						}
					}
				} catch (ElementClickInterceptedException e) {
					js = (JavascriptExecutor) driver;
					js.executeScript("window.scrollBy(0,250)");
				}
			}

			if (priority.getContents().isEmpty()) {
				System.out.println("Priority should be required");
				driver.close();
				driver.quit();
				System.exit(1);
			} else {
				Select selec2 = new Select(driver.findElement(By.id("priority_id")));
				selec2.selectByVisibleText(priority.getContents());
			}

			if (taskStatus.getContents().isEmpty()) {
				System.out.println("Status is not available so set as default");
			} else {
				List<WebElement> options = driver.findElements(By.xpath("//select[@id='status']/option"));

				for (WebElement option : options) {
					if (option.getText().contains(taskStatus.getContents())) {
						option.click();
						break;
					}
				}
			}

			if (assignee.getContents().isEmpty()) {
				System.out.println("Assignee user is not available in excel sheet");
			} else {
				List<WebElement> options = driver.findElements(By.xpath("//select[@id='assign_user_id']/option"));

				for (WebElement option : options) {
					if (option.getText().contains(assignee.getContents())) {
						option.click();
						break;
					}
				}
			}

			if (reporter.getContents().isEmpty()) {
				System.out.println("Reporter user is not available in excel sheet");
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
				System.out.println("Documents are not available in excel sheet");
			} else {
				System.out.println("driver sta = " + driver);
				multipleFileUpload.fileUpload(driver, imagePath, uploadDocuments);
				driver.findElement(By.id("startall")).click();
				Thread.sleep(1500);

				int tmp = 0;

				do {
					if (driver.findElement(By.xpath("//div[@role='progressbar']")).getAttribute("aria-valuenow")
							.equals("100")) {
						tmp = 1;
						System.out.println("Documents are uploaded");
					} else if (driver.findElement(By.xpath("//div[@role='progressbar']")).getAttribute("aria-valuenow")
							.equals("0")) {
						if (driver.findElements(By.xpath("//span[text()='Upload']")).size() > 0) {
							System.out.println("any one or multiple documents are not attached");
							tmp = 1;
						} else {
							System.out.println("documents are attached");
							tmp = 1;
						}
					} else {
						tmp = 0;
					}
				} while (tmp == 0);
				js = (JavascriptExecutor) driver;
				js.executeScript("window.scrollBy(0,250)");
			}

			now = LocalDateTime.now();
			System.out.println(dtf.format(now));
//			driver.findElement(By.xpath("//*[@id=\"frmaddedit\"]/div[26]/div/div/button[1]")).click();
			testcase = true;
			checkLoader();
			driver.findElement(By.tagName("body")).sendKeys(Keys.HOME);
			if (bug_tracking_sheet.toLowerCase().equals("yes")) {
				createBugTrackingReport.createBugTracking(driver, DevTrackerURL.getContents(), taskTitle.getContents(),
						projectName, originator.getContents(), reporter.getContents(), taskType.getContents());
			}

			row++;
		}
		now = LocalDateTime.now();
		System.out.println(dtf.format(now));

//		driver.close();
//		driver.quit();
//		System.exit(0);
	}

	@AfterSuite
	public void bugadd_fun_verify() throws Exception, InterruptedException {
		String zipFilename = "test-output.zip";
		String outputFolderName = "test-output";
		String renamedFileName = "testoutput.txt";
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
			boolean success = file.renameTo(file2);
			mailSend.mail(renamedFileName, username.getContents());
			Thread.sleep(2000);

			file = new File(renamedFileName);
			if (file.exists()) {
				file.delete();
			}
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
}
