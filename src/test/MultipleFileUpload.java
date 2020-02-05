package test;

import java.io.File;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import jxl.Cell;

public class MultipleFileUpload extends Utilities {

	public void fileUpload(WebDriver driver, String globalPath, Cell description) throws InterruptedException {
		String fileString = description.getContents();

		String[] stringSplit = fileString.split("\n");
		Thread.sleep(1000);
//		int attachmentCount = 0;
		for (int ar = 0; ar < stringSplit.length; ar++) {
			boolean fileExists = false;
			File filePath = new File(globalPath + stringSplit[ar]);
			Frame1.appendText("tempFile = " + filePath);
			String fileURL = "";
			if (stringSplit[ar].isEmpty()) {
				fileURL = stringSplit[ar];
			} else {
				fileExists = filePath.exists();
				fileURL = globalPath + stringSplit[ar];
			}

			if (fileExists != true) {
			} else {
				driver.findElement(By.xpath("// *[@id=\"basic-uploader\"]/div[1]/div[1]/span[1]/input"))
						.sendKeys(fileURL);

//				attachmentCount++;
//				System.out.println("document is available");
//				try {
//					driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[2]/div/div[8]/button[2]"))
//							.click();
//				} catch (ElementClickInterceptedException e) {
//					js = (JavascriptExecutor) driver;
//					js.executeScript("window.scrollBy(0,-250)");
//					driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[2]/div/div[8]/button[2]"))
//							.click();
//				}
//				Thread.sleep(1500);
//
//				driver.findElement(By.name("files")).sendKeys(imageURL);
//
//				int tmp = 0;
//				long t = System.currentTimeMillis();
//				long end = t + 40000;
//
//				do {
//					if (System.currentTimeMillis() > end) {
//						System.out.println("image upload timeout");
//						tmp = 1;
//						break;
//					}
//
//					if (driver
//							.findElements(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]//following::img["
//									+ attachmentCount + "]"))
//							.size() > 0) {
//						if (driver.findElement(
//								By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]//following::img["
//										+ attachmentCount + "]"))
//								.isDisplayed()) {
//							System.out.println("File is attached");
//							tmp = 1;
//							if (arrSplit.length > 1) {
//								driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
//										.sendKeys(Keys.ENTER);
//							}
//						}
//					} else {
//						System.out.println("File is still not attached");
//						tmp = 0;
//					}
//				} while (tmp == 0);

			}
		}
	}
}
