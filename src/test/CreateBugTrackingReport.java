package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.openqa.selenium.WebDriver;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class CreateBugTrackingReport extends Utilities {

	public void createBugTracking(WebDriver driver, String DevTrackerURL, String taskTitle, String projectName,
			String originator, String reporter)
			throws InterruptedException, IOException, RowsExceededException, WriteException {
		File outputWorkbook;
		WritableWorkbook workbook1;
		WritableSheet sheet1;
		outputWorkbook = new File("BugTracking.xls");

		// BugTracking excel header content

		String[] headerContent = { "Date", "Title", "Originator", "Corrected By", "Reported By", "DT Link", "Project",
				"Is Common", "Repeat Reopen", "Unit Tested? (0/1)",
				"DT Task Name(This Column is for adding Task ID to Generate DT Link Automatically)" };

		if (outputWorkbook.exists() != true) {
			workbook1 = Workbook.createWorkbook(outputWorkbook);
			sheet1 = workbook1.createSheet("BugTracking", 0);

			for (int headerContentData = 0; headerContentData < headerContent.length; headerContentData++) {
				Label label = new Label(headerContentData, 0, headerContent[headerContentData]);
				sheet1.addCell(label);
			}

			workbook1.write();
			workbook1.close();
		}

		FileInputStream fsIP = new FileInputStream(new File("BugTracking.xls"));
		HSSFWorkbook wb = new HSSFWorkbook(fsIP);
		HSSFSheet worksheet = wb.getSheetAt(0);

		System.out.println("worksheet.getLastRowNum() = " + worksheet.getLastRowNum());
		System.out.println("worksheet.getFirstRowNum() = " + worksheet.getFirstRowNum());

		int rowCount = worksheet.getLastRowNum() - worksheet.getFirstRowNum();
		System.out.println("rowCount = " + rowCount);
		Row row = worksheet.getRow(0);
		System.out.println("row.getCell(0) = " + row.getCell(0));
		System.out.println("row.getLastCellNum() " + row.getLastCellNum());

		// Create a loop over the cell of newly created Row

		System.out.println(driver.getCurrentUrl());
		if (driver.getCurrentUrl().contains(DevTrackerURL + "track/")) {
			Row newRow = worksheet.createRow(rowCount + 1);

			String taskID = driver.getCurrentUrl().replace(DevTrackerURL + "track/", "");
			System.out.println(taskID);

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
			LocalDateTime now = LocalDateTime.now();
			String taskDate = dtf.format(now);
			String title = taskTitle;
			String originatorName = originator;
			String CorrectedBy = "";
			String ReportedBy = reporter;
			String DTLink = "=HYPERLINK(CONCATENATE(\"" + DevTrackerURL + "track/\",K" + (rowCount + 2) + "),K"
					+ (rowCount + 2) + ")";
			String project_Name = projectName;
			String IsCommon = "";
			String RepeatReopen = "";
			String UnitTested = "";
			String DTTaskName = taskID;

			String data[] = { taskDate, title, originatorName, CorrectedBy, ReportedBy, DTLink, project_Name, IsCommon,
					RepeatReopen, UnitTested, DTTaskName };

			System.out.println("================");
			for (int j = 0; j < row.getLastCellNum(); j++) {
				System.out.println("j value = " + j);
				System.out.println(data[j]);
				Cell cell = newRow.createCell(j);
				cell.setCellValue(data[j]);
			}
		}
		fsIP.close();

		FileOutputStream outputStream = new FileOutputStream("BugTracking.xls");
		wb.write(outputStream);
		outputStream.close();
	}
}
