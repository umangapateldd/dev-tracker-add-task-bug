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
			String originator, String reporter, String taskType)
			throws InterruptedException, IOException, RowsExceededException, WriteException {
		File outputWorkbook;
		WritableWorkbook workbook1;
		WritableSheet sheet1;
		outputWorkbook = new File("TicketTracker.xls");

		// BugTracking excel header content

		String[] headerContent = { "Date", "Title", "Originator", "Corrected By", "Reported By", "DT Link", "Project",
				"Is Common", "Repeat Reopen", "Unit Tested? (0/1)",
				"DT Task Name(This Column is for adding Task ID to Generate DT Link Automatically)", "Task / Bug" };

		if (outputWorkbook.exists() != true) {
			workbook1 = Workbook.createWorkbook(outputWorkbook);
			sheet1 = workbook1.createSheet("TicketTracker", 0);

			for (int headerContentData = 0; headerContentData < headerContent.length; headerContentData++) {
				Label label = new Label(headerContentData, 0, headerContent[headerContentData]);
				sheet1.addCell(label);
			}

			workbook1.write();
			workbook1.close();
		}

		FileInputStream fsIP = new FileInputStream(new File("TicketTracker.xls"));
		HSSFWorkbook wb = new HSSFWorkbook(fsIP);
		HSSFSheet worksheet = wb.getSheetAt(0);

		int rowCount = worksheet.getLastRowNum() - worksheet.getFirstRowNum();
		Row row = worksheet.getRow(0);

		// Create a loop over the cell of newly created Row

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
			String tasktype = taskType;

			String data[] = { taskDate, title, originatorName, CorrectedBy, ReportedBy, DTLink, project_Name, IsCommon,
					RepeatReopen, UnitTested, DTTaskName, tasktype };

			for (int j = 0; j < row.getLastCellNum(); j++) {
				Cell cell = newRow.createCell(j);
				cell.setCellValue(data[j]);
			}
		}
		fsIP.close();

		FileOutputStream outputStream = new FileOutputStream("TicketTracker.xls");
		wb.write(outputStream);
		outputStream.close();
	}
}
