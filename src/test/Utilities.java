package test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipOutputStream;

import org.apache.commons.text.StringEscapeUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import jxl.Cell;
import jxl.Sheet;

public class Utilities {
	WebDriver driver;
	JavascriptExecutor js;
	String systemName;
	List<String> filesListInDir = new ArrayList<String>();
	static FileOutputStream F_OUT;
	static ZipOutputStream Z_OUT;
	static int N;
	static FileLock lock;
	List paths;
	boolean testcase = false;
	int col = 15;
	String orderlist = "stop";
	String subOrderList = "stop";
	int listVal = 0;
	int numlistVal = 0;
	int oldnumlistVal = 0;
	int subnumbercnt = 0;
	int oldlistVal = 0;
	int orderListNumber = 0;
	int subOrderListNumber = 0;
	int tempListNumber = 0;
	int tempsubOrderListNumber = 0;
	int subNumberCount = 0;
	int numberCount = 0;
	int count = 0;
	int fromIndex = 0;
	int slashsubnumbercount = 0;

	public void openBrowser(String headless) throws IOException {

		DesiredCapabilities chrome = new DesiredCapabilities();

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--test-type");
		options.addArguments("--disable-popup-blocking");
		options.addArguments("--proxy-server='direct://'");
		options.addArguments("--proxy-bypass-list=*");
		if (headless.equals("No")) {
			options.addArguments("--headless");
		}
		options.setCapability(ChromeOptions.CAPABILITY, chrome);
		chrome.setJavascriptEnabled(true);
		systemName = System.getProperty("os.name").toLowerCase();
		if (systemName.contains("mac")) {
			System.setProperty("webdriver.chrome.driver", "chromedriver");
		} else {
			System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
		}

		driver = new ChromeDriver(options);
		Frame1.driverFrame = driver;
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
	}

	public void closeBrowser() {
		driver.close();
		driver.quit();
	}

	public static List<String> extractUrls(String text) {
		List<String> containedUrls = new ArrayList<String>();
		String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
		Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
		Matcher urlMatcher = pattern.matcher(text);

		while (urlMatcher.find()) {
			containedUrls.add(text.substring(urlMatcher.start(0), urlMatcher.end(0)));
		}

		return containedUrls;
	}

	@SuppressWarnings("deprecation")
	public void macTextFormat(String imagePath, Cell descriptionType, String htmlTag, Sheet sh1, int row)
			throws InterruptedException {
		String oltagString = AddBugTask.oltagStringGlobal;
		int countTag = 0;
		if (htmlTag.contains("p[")) {
			// objective / reference
			countTag = Integer.parseInt(htmlTag.replace("p[", "").replace("]", ""));
		} else {
			// cos
			countTag = driver.findElements(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p")).size();
		}

		String cosString = descriptionType.getContents();
		String ACString = "";
		File ACFile = new File("AC\\" + cosString);
		if (ACFile.exists()) {
//			BufferedReader reader;
			try {

				Scanner scanner = new Scanner(ACFile);
				while (scanner.hasNextLine()) {
					ACString = ACString + scanner.nextLine() + "\n";
				}
				scanner.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			cosString = ACString;
			AddBugTask.ACFileAvailable = "true";
		} else {
			AddBugTask.ACFileAvailable = "false";
		}
		String[] arrSplit = cosString.split("\n");

		int attachmentCount = 0;
		boolean exists = false;
		boolean alreadybold = false;

		for (int ar = 0; ar < arrSplit.length; ar++) {
			System.out.println("arrSplit[ar] = " + arrSplit[ar]);

			if (arrSplit[ar].toLowerCase().contains("{number}")) {
				orderListNumber++;
				orderlist = "start";
				listVal = 1;
				numlistVal = 1;
				oltagString = oltagString + "ol[" + orderListNumber + "]/";

				driver.findElement(
						By.xpath("//div[@id='description']//button[@aria-label='Ordered list (CTRL+SHIFT+NUM7)']"))
						.click();

				if (arrSplit[ar].toLowerCase().contains("{/number}")) {
					orderlist = "end";
					subnumbercnt = 0;
				}
			} else if (arrSplit[ar].toLowerCase().contains("{subnumber}")) {
				numlistVal = listVal;
				subnumbercnt = subnumbercnt + 1;

				driver.findElement(By.xpath(
						"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/" + oltagString + "li[" + (listVal) + "]"))
						.sendKeys(Keys.CONTROL + "]");

				if (orderlist.equals("end") || orderlist.equals("stop")) {
					subOrderListNumber = 1;
				} else {
					if (subOrderList.equals("start")) {
						tempsubOrderListNumber = subOrderListNumber;
						subOrderListNumber = 1;
					} else {
						subOrderListNumber = subOrderListNumber + 1;
					}
				}

				oltagString = oltagString + "ol[" + subOrderListNumber + "]/";
				oldlistVal = listVal;

				subNumberCount = subNumberCount + 1;
				subOrderList = "start";

				listVal = 1;
				tempListNumber = orderListNumber;

				if (arrSplit[ar].toLowerCase().contains("{/subnumber}")) {
					subNumberCount = subNumberCount - 1;
					subOrderList = "end";

					String str = arrSplit[ar].toLowerCase();
					slashsubnumbercount = 0;
					fromIndex = 0;
					String strFind = "{/subnumber}";

					while ((fromIndex = str.indexOf(strFind, fromIndex)) != -1) {
						slashsubnumbercount++;
						fromIndex++;
					}

					if (arrSplit[ar].toLowerCase().contains("{/number}")) {
						orderlist = "end";
						subnumbercnt = 0;
					}
				}
			} else if (arrSplit[ar].toLowerCase().contains("{/subnumber}")) {
				subNumberCount = subNumberCount - 1;
				subOrderList = "end";

				String str = arrSplit[ar].toLowerCase();
				slashsubnumbercount = 0;
				fromIndex = 0;
				String strFind = "{/subnumber}";

				while ((fromIndex = str.indexOf(strFind, fromIndex)) != -1) {
					slashsubnumbercount++;
					fromIndex++;
				}

				if (arrSplit[ar].toLowerCase().contains("{/number}")) {
					System.out
							.println("999999 slash sub number with slash number orderListNumber = " + orderListNumber);
					orderlist = "end";
					subnumbercnt = 0;
				}
			} else if (arrSplit[ar].toLowerCase().contains("{/number}")) {
				orderlist = "end";
				subnumbercnt = 0;
				if (arrSplit[ar].toLowerCase().contains("{/subnumber}")) {
					subNumberCount = subNumberCount - 1;
					subOrderList = "end";
				}
			}

			exists = false;
			arrSplit[ar] = arrSplit[ar].replace("{number}", "");
			arrSplit[ar] = arrSplit[ar].replace("{/number}", "");
			arrSplit[ar] = arrSplit[ar].replace("{subnumber}", "");
			arrSplit[ar] = arrSplit[ar].replace("{/subnumber}", "");
			File tempFile = new File(imagePath + arrSplit[ar]);
			String imageURL = "";
			if (arrSplit[ar].isEmpty()) {
				imageURL = arrSplit[ar];
			} else {
				exists = tempFile.exists();
				imageURL = imagePath + arrSplit[ar];
			}

			if (exists != true) {
				List<String> extractedUrls = extractUrls(arrSplit[ar]);
				if (extractedUrls.size() > 0) {
					for (int urlCount = 0; urlCount < extractedUrls.size(); urlCount++) {
						if (arrSplit[ar].contains(("<" + extractedUrls.get(urlCount).replace("<", "") + "<"))) {

						} else {
							arrSplit[ar] = arrSplit[ar].replace(extractedUrls.get(urlCount),
									("<" + extractedUrls.get(urlCount) + "<" + extractedUrls.get(urlCount) + "<"))
									.replace("<<", "<");
						}
					}
				}

				if (orderlist.equals("stop")) {
					if (driver
							.findElement(
									By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
							.getText().isEmpty()) {
						int cosLinkCount = 0;
						int cosBoldConntentCount = 0;
						int cosColorConntentCount = 0;
						int cosTagUserSymbolCount = 0;

						for (int i = 0; i < arrSplit[ar].length(); i++) {
							if (arrSplit[ar].charAt(i) == '<') {
								cosLinkCount++;
							}
							if (arrSplit[ar].charAt(i) == '$') {
								cosBoldConntentCount++;
							}
							if (arrSplit[ar].charAt(i) == '^') {
								cosColorConntentCount++;
							}
							if (arrSplit[ar].charAt(i) == '~') {
								cosTagUserSymbolCount++;
							}
						}
						boolean enter = false;
						if (driver
								.findElements(By.xpath(
										"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag + "]/b"))
								.size() > 0) {
							if (driver
									.findElement(By.xpath(
											"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag + "]/b"))
									.getText().isEmpty()
									|| driver.findElements(
											By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag
													+ "]/b/br"))
											.size() > 0) {
								js = (JavascriptExecutor) driver;
								js.executeScript("arguments[0].remove()", driver.findElement(By.xpath(
										"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag + "]/b")));

								js.executeScript(
										"arguments[0].innerHTML = '" + StringEscapeUtils.escapeEcmaScript(driver
												.findElement(
														By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
																+ countTag + "]"))
												.getAttribute("innerHTML").trim() + "<br>") + "'",
										driver.findElement(
												By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
														+ countTag + "]")));
							}
						}
						System.out.println("xxxxxxxxxxxxxxxxxxxxxxx");
						System.out.println("before cosBoldConntentCount = " + cosBoldConntentCount);
						System.out.println("before cosLinkCount = " + cosLinkCount);
						System.out.println("before cosTagUserSymbolCount = " + cosTagUserSymbolCount);

						if (cosBoldConntentCount > 0) {
							if (cosBoldConntentCount % 2 == 0) {
							} else {
								cosBoldConntentCount = 0;
							}
						} else {
							cosBoldConntentCount = 0;
						}

						if (cosLinkCount > 0) {
							if (cosLinkCount % 3 == 0) {
							} else {
								cosLinkCount = 0;
							}
						} else {
							cosLinkCount = 0;
						}

						if (cosTagUserSymbolCount > 0) {
							if (cosTagUserSymbolCount % 2 == 0) {
							} else {
								cosTagUserSymbolCount = 0;
							}
						} else {
							cosTagUserSymbolCount = 0;
						}

						System.out.println("after cosBoldConntentCount = " + cosBoldConntentCount);
						System.out.println("after cosLinkCount = " + cosLinkCount);
						System.out.println("after cosTagUserSymbolCount = " + cosTagUserSymbolCount);
						System.out.println("==========================");

						if (cosLinkCount > 0 || cosBoldConntentCount > 0 || cosColorConntentCount > 0
								|| cosTagUserSymbolCount > 0) {
							int index = arrSplit[ar].indexOf('<');
							int boldindex = arrSplit[ar].indexOf('$');
							int colorindex = arrSplit[ar].indexOf('^');
							int tagUserSymbolindex = arrSplit[ar].indexOf('~');

							int[] a = new int[cosLinkCount + cosBoldConntentCount + cosColorConntentCount
									+ cosTagUserSymbolCount];

							int x = 0;
							while (index >= 0) {
								a[x] = index;
								index = arrSplit[ar].indexOf('<', index + 1);
								x++;
							}

							while (boldindex >= 0) {
								a[x] = boldindex;
								boldindex = arrSplit[ar].indexOf('$', boldindex + 1);
								x++;
							}

							while (colorindex >= 0) {
								a[x] = colorindex;
								colorindex = arrSplit[ar].indexOf('^', colorindex + 1);
								x++;
							}

							while (tagUserSymbolindex >= 0) {
								a[x] = tagUserSymbolindex;
								tagUserSymbolindex = arrSplit[ar].indexOf('~', tagUserSymbolindex + 1);
								x++;
							}

							Arrays.sort(a);

							int abc = 0;
							int once = 0;

							while (abc < a.length) {
								if (arrSplit[ar].charAt(0) != '$') {
									if (driver
											.findElement(By.xpath(
													"//*[@id=\"description\"]/div/div[3]/div[2]/div/div[2]/button[1]"))
											.getAttribute("class")
											.equals("note-btn btn btn-default btn-sm note-btn-bold active")) {
										if (systemName.contains("mac")) {

										} else {
											driver.findElement(
													By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
															+ countTag + "]"))
													.sendKeys(Keys.CONTROL + "b");
										}
									} else {

									}
								} else {

								}

								if (once == 0) {

									driver.findElement(By.xpath(
											"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
											.sendKeys(arrSplit[ar].substring(0, a[abc]));
									once = 1;
								}

								if (arrSplit[ar].charAt(a[abc]) == '<') {

									try {
										driver.findElement(By.xpath(
												"//*[@id=\"description\"]/div/div[3]/div[2]/div/div[8]/button[1]"))
												.click();
									} catch (ElementClickInterceptedException e) {
										js = (JavascriptExecutor) driver;
										js.executeScript("window.scrollBy(0,-250)");
										driver.findElement(By.xpath(
												"//*[@id=\"description\"]/div/div[3]/div[2]/div/div[8]/button[1]"))
												.click();
									}

									driver.findElement(By.xpath(
											"//*[@id=\"description\"]/div/div[3]/div[5]/div[2]/div/div[2]/div[1]/input"))
											.sendKeys(arrSplit[ar].substring(a[abc] + 1, a[abc + 1]));

									driver.findElement(By.xpath(
											"//*[@id=\"description\"]/div/div[3]/div[5]/div[2]/div/div[2]/div[2]/input"))
											.sendKeys(arrSplit[ar].substring(a[abc + 1] + 1, a[abc + 2]));

									driver.findElement(By.xpath(
											"//*[@id=\"description\"]/div/div[3]/div[5]/div[2]/div/div[3]/button"))
											.click();

									driver.findElement(By.xpath(
											"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
											.sendKeys(Keys.ARROW_RIGHT);

									if (driver.findElements(By.xpath(
											"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag + "]/b"))
											.size() > 0) {
										if (driver
												.findElement(
														By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
																+ countTag + "]/b"))
												.getText().isEmpty()
												|| driver.findElements(
														By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
																+ countTag + "]/b/br"))
														.size() > 0) {

											js = (JavascriptExecutor) driver;
											js.executeScript("arguments[0].remove()",
													driver.findElement(By.xpath(
															"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
																	+ countTag + "]/b")));
										}
									}

									if (abc + 3 < a.length) {
										driver.findElement(
												By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
														+ countTag + "]"))
												.sendKeys(arrSplit[ar].substring(a[abc + 2] + 1, a[abc + 3]));
									} else {
										driver.findElement(
												By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
														+ countTag + "]"))
												.sendKeys(
														arrSplit[ar].substring(a[abc + 2] + 1, arrSplit[ar].length()));
									}
									abc = abc + 3;
								} else if (arrSplit[ar].charAt(a[abc]) == '$') {

									int boldWithColor = 0;

									JavascriptExecutor js = (JavascriptExecutor) driver;
									js.executeScript(
											"arguments[0].innerHTML = '" + StringEscapeUtils.escapeEcmaScript(driver
													.findElement(By.xpath(
															"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
																	+ countTag + "]"))
													.getAttribute("innerHTML").replace("<b><br></b>", "")
													.replace("<br>", "").replace("&nbsp;&nbsp;", "").trim() + " <b>"
													+ arrSplit[ar].substring(a[abc] + 1, a[abc + 1]) + "</b>") + "'",
											driver.findElement(
													By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
															+ countTag + "]")));

									if (arrSplit[ar].charAt(a[abc + 1]) == '^') {
										if (driver.findElement(By.xpath(
												"//*[@id=\"description\"]/div/div[3]/div[2]/div/div[2]/button[1]"))
												.getAttribute("class")
												.equals("note-btn btn btn-default btn-sm note-btn-bold active")) {
											alreadybold = true;
										} else {
											alreadybold = false;
										}

										boldWithColor = 1;
										try {
											driver.findElement(By.xpath(
													"//*[@id=\"description\"]/div/div[3]/div[2]/div/div[4]/div/button[2]"))
													.click();
										} catch (ElementClickInterceptedException e) {
											js = (JavascriptExecutor) driver;
											js.executeScript("window.scrollBy(0,-250)");
											driver.findElement(By.xpath(
													"//*[@id=\"description\"]/div/div[3]/div[2]/div/div[4]/div/button[2]"))
													.click();
										}
										// Click Starting Color Code

										if (driver.findElements(By.xpath("//button[@style='background-color:#"
												+ arrSplit[ar].substring(a[abc + 1] + 1, a[abc + 2])
												+ "' and @data-event='foreColor']")).size() > 0) {
											driver.findElement(By.xpath("//button[@style='background-color:#"
													+ arrSplit[ar].substring(a[abc + 1] + 1, a[abc + 2])
													+ "' and @data-event='foreColor']")).click();
										} else {
											Frame1.appendText("Start color code is not proper");
										}

										if (alreadybold == true) {
											if (driver.findElement(By.xpath(
													"//*[@id=\"description\"]/div/div[3]/div[2]/div/div[2]/button[1]"))
													.getAttribute("class")
													.equals("note-btn btn btn-default btn-sm note-btn-bold active")) {
											} else {
												if (systemName.contains("mac")) {

												} else {
													driver.findElement(By.xpath(
															"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
																	+ countTag + "]"))
															.sendKeys(Keys.CONTROL + "b");
												}
											}
											alreadybold = false;
										}

										driver.findElement(
												By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
														+ countTag + "]"))
												.sendKeys(arrSplit[ar].substring(a[abc + 2] + 1, a[abc + 3]));

										try {
											driver.findElement(By.xpath(
													"//*[@id=\"description\"]/div/div[3]/div[2]/div/div[4]/div/button[2]"))
													.click();
										} catch (ElementClickInterceptedException e) {
											js = (JavascriptExecutor) driver;
											js.executeScript("window.scrollBy(0,-250)");
											driver.findElement(By.xpath(
													"//*[@id=\"description\"]/div/div[3]/div[2]/div/div[4]/div/button[2]"))
													.click();
										}

										if (driver.findElements(By.xpath("//button[@style='background-color:#"
												+ arrSplit[ar].substring(a[abc + 3] + 1, a[abc + 4])
												+ "' and @data-event='foreColor']")).size() > 0) {
											driver.findElement(By.xpath("//button[@style='background-color:#"
													+ arrSplit[ar].substring(a[abc + 3] + 1, a[abc + 4])
													+ "' and @data-event='foreColor']")).click();
										} else {
											Frame1.appendText("End color code is not proper");
										}

										if (abc + 4 < a.length) {
											driver.findElement(
													By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
															+ countTag + "]"))
													.sendKeys(arrSplit[ar].substring(a[abc + 4] + 1, a[abc + 5]));
										} else {
											driver.findElement(
													By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
															+ countTag + "]"))
													.sendKeys(arrSplit[ar].substring(a[abc + 4] + 1,
															arrSplit[ar].length()));
										}
										abc = abc + 4;
									}

									if (abc + 2 < a.length) {
										js = (JavascriptExecutor) driver;
										js.executeScript(
												"arguments[0].innerHTML = '" + StringEscapeUtils.escapeEcmaScript(driver
														.findElement(By.xpath(
																"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
																		+ countTag + "]"))
														.getAttribute("innerHTML").replace("<b><br></b>", "")
														.replace("<br>", "").replace("&nbsp;&nbsp;", "").trim()
														+ arrSplit[ar].substring(a[abc + 1] + 1, a[abc + 2])) + "'",
												driver.findElement(
														By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
																+ countTag + "]")));
										driver.findElement(
												By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
														+ countTag + "]"))
												.sendKeys(Keys.COMMAND + "" + Keys.ARROW_RIGHT);
									} else {
										js = (JavascriptExecutor) driver;
										js.executeScript(
												"arguments[0].innerHTML = '" + StringEscapeUtils.escapeEcmaScript(driver
														.findElement(By.xpath(
																"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
																		+ countTag + "]"))
														.getAttribute("innerHTML").replace("<b><br></b>", "")
														.replace("<br>", "").replace("&nbsp;&nbsp;", "").trim()
														+ arrSplit[ar].substring(a[abc + 1] + 1, arrSplit[ar].length()))
														+ "'",
												driver.findElement(
														By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
																+ countTag + "]")));

										driver.findElement(
												By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
														+ countTag + "]"))
												.sendKeys(Keys.COMMAND + "" + Keys.ARROW_RIGHT);
									}
									abc = abc + 2;
								} else if (arrSplit[ar].charAt(a[abc]) == '~') {
									String username = arrSplit[ar].substring(a[abc] + 1, a[abc + 1]);
									driver.findElement(By.xpath(
											"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
											.sendKeys(" @" + username.substring(0, (username.indexOf(" "))));

									if (driver
											.findElement(By.xpath(
													"//body//following::div[1][contains(@class,'note-hint-popover')]"))
											.getAttribute("style").contains("display: block")) {

										int userCount = driver
												.findElements(By.xpath("//div[contains(@class,'note-hint-item')]"))
												.size();
										if (userCount == 1) {
											driver.findElement(
													By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
															+ countTag + "]"))
													.sendKeys(Keys.ENTER);
										} else if (userCount > 1) {

											for (int cnt = 1; cnt <= userCount; cnt++) {

												if (username.toLowerCase()
														.equals(driver.findElement(By.xpath("//div[" + cnt
																+ "][contains(@class,'note-hint-item')]/strong"))
																.getText().toLowerCase())) {

													driver.findElement(By.xpath(
															"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
																	+ countTag + "]"))
															.sendKeys(Keys.ENTER);
												} else {
													driver.findElement(By.xpath(
															"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
																	+ countTag + "]"))
															.sendKeys(Keys.ARROW_DOWN);
												}
											}
										} else {
											Frame1.appendText("Given username is not available");
										}
									} else {
										driver.findElement(
												By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
														+ countTag + "]"))
												.sendKeys(username.substring(username.indexOf(" "), username.length()));
									}
//									
									if (abc + 2 < a.length) {
										js = (JavascriptExecutor) driver;
										js.executeScript(
												"arguments[0].innerHTML = '" + StringEscapeUtils.escapeEcmaScript(driver
														.findElement(By.xpath(
																"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
																		+ countTag + "]"))
														.getAttribute("innerHTML").replace("<b><br></b>", "")
														.replace("<br>", "").replace("&nbsp;&nbsp;", "").trim()
														+ arrSplit[ar].substring(a[abc + 1] + 1, a[abc + 2])) + "'",
												driver.findElement(
														By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
																+ countTag + "]")));
										if (systemName.contains("mac")) {
											driver.findElement(
													By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
															+ countTag + "]"))
													.sendKeys(Keys.COMMAND + "" + Keys.ARROW_RIGHT);
										}
									} else {

										js = (JavascriptExecutor) driver;
										js.executeScript(
												"arguments[0].innerHTML = '" + StringEscapeUtils.escapeEcmaScript(driver
														.findElement(By.xpath(
																"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
																		+ countTag + "]"))
														.getAttribute("innerHTML").replace("<b><br></b>", "")
														.replace("<br>", "").replace("&nbsp;&nbsp;", "").trim()
														+ arrSplit[ar].substring(a[abc + 1] + 1, arrSplit[ar].length()))
														+ "'",
												driver.findElement(
														By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
																+ countTag + "]")));
										driver.findElement(
												By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
														+ countTag + "]"))
												.sendKeys(Keys.COMMAND + "" + Keys.ARROW_RIGHT);
									}
									abc = abc + 2;
								} else {

									if (arrSplit[ar].charAt(a[abc]) == '\n') {

										enter = true;
										driver.findElement(
												By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
														+ countTag + "]"))
												.sendKeys(Keys.RETURN);
									} else {

										driver.findElement(
												By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
														+ countTag + "]"))
												.sendKeys(arrSplit[ar].substring(a[abc], a[abc + 1]));
									}

									abc++;

								}
							}
						} else {
							System.out.println("123123123 = " + arrSplit[ar]);
							System.out.println("StringEscapeUtils.escapeEcmaScript(arrSplit[ar]) = "
									+ StringEscapeUtils.escapeEcmaScript(arrSplit[ar]));
							JavascriptExecutor js = (JavascriptExecutor) driver;
							js.executeScript(
									"arguments[0].innerHTML = '" + StringEscapeUtils.escapeEcmaScript(arrSplit[ar])
											+ "'",
									driver.findElement(By.xpath(
											"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag + "]")));
						}
						if (enter == true) {
							enter = false;
						} else {
							driver.findElement(
									By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
									.sendKeys(Keys.ENTER);
						}
					}
				} else {
					try {
						driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/" + oltagString
								+ "li[" + listVal + "]")).getText();
					} catch (NoSuchElementException e) {
						int lisize = driver
								.findElements(By.xpath(
										"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/" + oltagString + "li"))
								.size();
						listVal = lisize;
					}

					if (driver.findElement(By.xpath(
							"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/" + oltagString + "li[" + listVal + "]"))
							.getText().isEmpty()) {
						int cosLinkCount = 0;
						int cosBoldConntentCount = 0;
						int cosColorConntentCount = 0;
						int cosTagUserSymbolCount = 0;

						for (int i = 0; i < arrSplit[ar].length(); i++) {
							if (arrSplit[ar].charAt(i) == '<') {
								cosLinkCount++;
							}
							if (arrSplit[ar].charAt(i) == '$') {
								cosBoldConntentCount++;
							}
							if (arrSplit[ar].charAt(i) == '^') {
								cosColorConntentCount++;
							}
							if (arrSplit[ar].charAt(i) == '~') {
								cosTagUserSymbolCount++;
							}
						}
						boolean enter = false;
						if (driver.findElements(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
								+ oltagString + "li[" + listVal + "]" + "/b")).size() > 0) {
							if (driver
									.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
											+ oltagString + "li[" + listVal + "]" + "/b"))
									.getText().isEmpty()
									|| driver.findElements(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
											+ oltagString + "li[" + listVal + "]" + "/b/br")).size() > 0) {
								js = (JavascriptExecutor) driver;
								js.executeScript("arguments[0].remove()",
										driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
												+ oltagString + "li[" + listVal + "]" + "/b")));

								js.executeScript(
										"arguments[0].innerHTML = '" + StringEscapeUtils.escapeEcmaScript(driver
												.findElement(
														By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
																+ oltagString + "li[" + listVal + "]"))
												.getAttribute("innerHTML").trim() + "<br>") + "'",
										driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
												+ oltagString + "li[" + listVal + "]")));
							}
						}

						if (cosLinkCount > 0 || cosBoldConntentCount > 0 || cosColorConntentCount > 0
								|| cosTagUserSymbolCount > 0) {
							int index = arrSplit[ar].indexOf('<');
							int boldindex = arrSplit[ar].indexOf('$');
							int colorindex = arrSplit[ar].indexOf('^');
							int tagUserSymbolindex = arrSplit[ar].indexOf('~');

							int[] a = new int[cosLinkCount + cosBoldConntentCount + cosColorConntentCount
									+ cosTagUserSymbolCount];

							int x = 0;
							while (index >= 0) {
								a[x] = index;
								index = arrSplit[ar].indexOf('<', index + 1);
								x++;
							}

							while (boldindex >= 0) {
								a[x] = boldindex;
								boldindex = arrSplit[ar].indexOf('$', boldindex + 1);
								x++;
							}

							while (colorindex >= 0) {
								a[x] = colorindex;
								colorindex = arrSplit[ar].indexOf('^', colorindex + 1);
								x++;
							}

							while (tagUserSymbolindex >= 0) {
								a[x] = tagUserSymbolindex;
								tagUserSymbolindex = arrSplit[ar].indexOf('~', tagUserSymbolindex + 1);
								x++;
							}

							Arrays.sort(a);

							int abc = 0;
							int once = 0;

							while (abc < a.length) {
								if (arrSplit[ar].charAt(0) != '$') {
									if (driver
											.findElement(By.xpath(
													"//*[@id=\"description\"]/div/div[3]/div[2]/div/div[2]/button[1]"))
											.getAttribute("class")
											.equals("note-btn btn btn-default btn-sm note-btn-bold active")) {
										if (systemName.contains("mac")) {

										} else {
											driver.findElement(
													By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
															+ oltagString + "li[" + listVal + "]"))
													.sendKeys(Keys.CONTROL + "b");
										}
									} else {

									}
								} else {

								}

								if (once == 0) {

									driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
											+ oltagString + "li[" + listVal + "]"))
											.sendKeys(arrSplit[ar].substring(0, a[abc]));
									once = 1;
								}

								if (arrSplit[ar].charAt(a[abc]) == '<') {

									try {
										driver.findElement(By.xpath(
												"//*[@id=\"description\"]/div/div[3]/div[2]/div/div[8]/button[1]"))
												.click();
									} catch (ElementClickInterceptedException e) {
										js = (JavascriptExecutor) driver;
										js.executeScript("window.scrollBy(0,-250)");
										driver.findElement(By.xpath(
												"//*[@id=\"description\"]/div/div[3]/div[2]/div/div[8]/button[1]"))
												.click();
									}

									driver.findElement(By.xpath(
											"//*[@id=\"description\"]/div/div[3]/div[5]/div[2]/div/div[2]/div[1]/input"))
											.sendKeys(arrSplit[ar].substring(a[abc] + 1, a[abc + 1]));

									driver.findElement(By.xpath(
											"//*[@id=\"description\"]/div/div[3]/div[5]/div[2]/div/div[2]/div[2]/input"))
											.sendKeys(arrSplit[ar].substring(a[abc + 1] + 1, a[abc + 2]));

									driver.findElement(By.xpath(
											"//*[@id=\"description\"]/div/div[3]/div[5]/div[2]/div/div[3]/button"))
											.click();

									driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
											+ oltagString + "li[" + listVal + "]")).sendKeys(Keys.ARROW_RIGHT);

									if (driver
											.findElements(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
													+ oltagString + "li[" + listVal + "]" + "/b"))
											.size() > 0) {
										if (driver
												.findElement(
														By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
																+ oltagString + "li[" + listVal + "]" + "/b"))
												.getText().isEmpty()
												|| driver.findElements(
														By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
																+ oltagString + "li[" + listVal + "]" + "/b/br"))
														.size() > 0) {

											js = (JavascriptExecutor) driver;
											js.executeScript("arguments[0].remove()",
													driver.findElement(By
															.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
																	+ oltagString + "li[" + listVal + "]" + "/b")));
										}
									}

									if (abc + 3 < a.length) {
										driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
												+ oltagString + "li[" + listVal + "]"))
												.sendKeys(arrSplit[ar].substring(a[abc + 2] + 1, a[abc + 3]));
									} else {
										driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
												+ oltagString + "li[" + listVal + "]")).sendKeys(
														arrSplit[ar].substring(a[abc + 2] + 1, arrSplit[ar].length()));
									}
									abc = abc + 3;
								} else if (arrSplit[ar].charAt(a[abc]) == '$') {

									int boldWithColor = 0;

									JavascriptExecutor js = (JavascriptExecutor) driver;
									js.executeScript(
											"arguments[0].innerHTML = '" + StringEscapeUtils.escapeEcmaScript(driver
													.findElement(By
															.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
																	+ oltagString + "li[" + listVal + "]"))
													.getAttribute("innerHTML").replace("<b><br></b>", "")
													.replace("<br>", "").replace("&nbsp;&nbsp;", "").trim() + " <b>"
													+ arrSplit[ar].substring(a[abc] + 1, a[abc + 1]) + "</b>") + "'",
											driver.findElement(
													By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
															+ oltagString + "li[" + listVal + "]")));

									if (arrSplit[ar].charAt(a[abc + 1]) == '^') {
										if (driver.findElement(By.xpath(
												"//*[@id=\"description\"]/div/div[3]/div[2]/div/div[2]/button[1]"))
												.getAttribute("class")
												.equals("note-btn btn btn-default btn-sm note-btn-bold active")) {
											alreadybold = true;
										} else {
											alreadybold = false;
										}

										boldWithColor = 1;
										try {
											driver.findElement(By.xpath(
													"//*[@id=\"description\"]/div/div[3]/div[2]/div/div[4]/div/button[2]"))
													.click();
										} catch (ElementClickInterceptedException e) {
											js = (JavascriptExecutor) driver;
											js.executeScript("window.scrollBy(0,-250)");
											driver.findElement(By.xpath(
													"//*[@id=\"description\"]/div/div[3]/div[2]/div/div[4]/div/button[2]"))
													.click();
										}

										// Click Starting Color Code

										if (driver.findElements(By.xpath("//button[@style='background-color:#"
												+ arrSplit[ar].substring(a[abc + 1] + 1, a[abc + 2])
												+ "' and @data-event='foreColor']")).size() > 0) {
											driver.findElement(By.xpath("//button[@style='background-color:#"
													+ arrSplit[ar].substring(a[abc + 1] + 1, a[abc + 2])
													+ "' and @data-event='foreColor']")).click();
										} else {
											Frame1.appendText("Start color code is not proper");
										}

										if (alreadybold == true) {
											if (driver.findElement(By.xpath(
													"//*[@id=\"description\"]/div/div[3]/div[2]/div/div[2]/button[1]"))
													.getAttribute("class")
													.equals("note-btn btn btn-default btn-sm note-btn-bold active")) {
											} else {
												if (systemName.contains("mac")) {

												} else {
													driver.findElement(By
															.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
																	+ oltagString + "li[" + listVal + "]"))
															.sendKeys(Keys.CONTROL + "b");
												}
											}
											alreadybold = false;
										}

										driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
												+ oltagString + "li[" + listVal + "]"))
												.sendKeys(arrSplit[ar].substring(a[abc + 2] + 1, a[abc + 3]));

										try {
											driver.findElement(By.xpath(
													"//*[@id=\"description\"]/div/div[3]/div[2]/div/div[4]/div/button[2]"))
													.click();
										} catch (ElementClickInterceptedException e) {
											js = (JavascriptExecutor) driver;
											js.executeScript("window.scrollBy(0,-250)");
											driver.findElement(By.xpath(
													"//*[@id=\"description\"]/div/div[3]/div[2]/div/div[4]/div/button[2]"))
													.click();
										}

										if (driver.findElements(By.xpath("//button[@style='background-color:#"
												+ arrSplit[ar].substring(a[abc + 3] + 1, a[abc + 4])
												+ "' and @data-event='foreColor']")).size() > 0) {
											driver.findElement(By.xpath("//button[@style='background-color:#"
													+ arrSplit[ar].substring(a[abc + 3] + 1, a[abc + 4])
													+ "' and @data-event='foreColor']")).click();
										} else {
											Frame1.appendText("End color code is not proper");
										}

										if (abc + 4 < a.length) {
											driver.findElement(
													By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
															+ oltagString + "li[" + listVal + "]"))
													.sendKeys(arrSplit[ar].substring(a[abc + 4] + 1, a[abc + 5]));
										} else {
											driver.findElement(
													By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
															+ oltagString + "li[" + listVal + "]"))
													.sendKeys(arrSplit[ar].substring(a[abc + 4] + 1,
															arrSplit[ar].length()));
										}
										abc = abc + 4;
									}

									if (abc + 2 < a.length) {
										js = (JavascriptExecutor) driver;
										js.executeScript(
												"arguments[0].innerHTML = '" + StringEscapeUtils.escapeEcmaScript(driver
														.findElement(By.xpath(
																"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
																		+ oltagString + "li[" + listVal + "]"))
														.getAttribute("innerHTML").replace("<b><br></b>", "")
														.replace("<br>", "").replace("&nbsp;&nbsp;", "").trim()
														+ arrSplit[ar].substring(a[abc + 1] + 1, a[abc + 2])) + "'",
												driver.findElement(
														By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
																+ oltagString + "li[" + listVal + "]")));
										driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
												+ oltagString + "li[" + listVal + "]"))
												.sendKeys(Keys.COMMAND + "" + Keys.ARROW_RIGHT);
									} else {
										js = (JavascriptExecutor) driver;
										js.executeScript(
												"arguments[0].innerHTML = '" + StringEscapeUtils.escapeEcmaScript(driver
														.findElement(By.xpath(
																"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
																		+ oltagString + "li[" + listVal + "]"))
														.getAttribute("innerHTML").replace("<b><br></b>", "")
														.replace("<br>", "").replace("&nbsp;&nbsp;", "").trim()
														+ arrSplit[ar].substring(a[abc + 1] + 1, arrSplit[ar].length()))
														+ "'",
												driver.findElement(
														By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
																+ oltagString + "li[" + listVal + "]")));

										driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
												+ oltagString + "li[" + listVal + "]"))
												.sendKeys(Keys.COMMAND + "" + Keys.ARROW_RIGHT);
									}
									abc = abc + 2;
								} else if (arrSplit[ar].charAt(a[abc]) == '~') {
									String username = arrSplit[ar].substring(a[abc] + 1, a[abc + 1]);
									driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
											+ oltagString + "li[" + listVal + "]"))
											.sendKeys(" @" + username.substring(0, (username.indexOf(" "))));

									if (driver
											.findElement(By.xpath(
													"//body//following::div[1][contains(@class,'note-hint-popover')]"))
											.getAttribute("style").contains("display: block")) {

										int userCount = driver
												.findElements(By.xpath("//div[contains(@class,'note-hint-item')]"))
												.size();
										if (userCount == 1) {
											driver.findElement(
													By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
															+ oltagString + "li[" + listVal + "]"))
													.sendKeys(Keys.ENTER);
										} else if (userCount > 1) {

											for (int cnt = 1; cnt <= userCount; cnt++) {

												if (username.toLowerCase()
														.equals(driver.findElement(By.xpath("//div[" + cnt
																+ "][contains(@class,'note-hint-item')]/strong"))
																.getText().toLowerCase())) {

													driver.findElement(By
															.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
																	+ oltagString + "li[" + listVal + "]"))
															.sendKeys(Keys.ENTER);
												} else {
													driver.findElement(By
															.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
																	+ oltagString + "li[" + listVal + "]"))
															.sendKeys(Keys.ARROW_DOWN);
												}
											}
										} else {
											Frame1.appendText("Given username is not available");
										}
									} else {
										driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
												+ oltagString + "li[" + listVal + "]"))
												.sendKeys(username.substring(username.indexOf(" "), username.length()));
									}
//									
									if (abc + 2 < a.length) {
										js = (JavascriptExecutor) driver;
										js.executeScript(
												"arguments[0].innerHTML = '" + StringEscapeUtils.escapeEcmaScript(driver
														.findElement(By.xpath(
																"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
																		+ oltagString + "li[" + listVal + "]"))
														.getAttribute("innerHTML").replace("<b><br></b>", "")
														.replace("<br>", "").replace("&nbsp;&nbsp;", "").trim()
														+ arrSplit[ar].substring(a[abc + 1] + 1, a[abc + 2])) + "'",
												driver.findElement(
														By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
																+ oltagString + "li[" + listVal + "]")));
										if (systemName.contains("mac")) {
											driver.findElement(
													By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
															+ oltagString + "li[" + listVal + "]"))
													.sendKeys(Keys.COMMAND + "" + Keys.ARROW_RIGHT);
										}
									} else {

										js = (JavascriptExecutor) driver;
										js.executeScript(
												"arguments[0].innerHTML = '" + StringEscapeUtils.escapeEcmaScript(driver
														.findElement(By.xpath(
																"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
																		+ oltagString + "li[" + listVal + "]"))
														.getAttribute("innerHTML").replace("<b><br></b>", "")
														.replace("<br>", "").replace("&nbsp;&nbsp;", "").trim()
														+ arrSplit[ar].substring(a[abc + 1] + 1, arrSplit[ar].length()))
														+ "'",
												driver.findElement(
														By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
																+ oltagString + "li[" + listVal + "]")));
										driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
												+ oltagString + "li[" + listVal + "]"))
												.sendKeys(Keys.COMMAND + "" + Keys.ARROW_RIGHT);
									}
									abc = abc + 2;
								} else {

									if (arrSplit[ar].charAt(a[abc]) == '\n') {

										enter = true;
										driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
												+ oltagString + "li[" + listVal + "]")).sendKeys(Keys.RETURN);
									} else {

										driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
												+ oltagString + "li[" + listVal + "]"))
												.sendKeys(arrSplit[ar].substring(a[abc], a[abc + 1]));
									}

									abc++;

								}
							}
						} else {
							JavascriptExecutor js = (JavascriptExecutor) driver;
							js.executeScript(
									"arguments[0].innerHTML = '" + StringEscapeUtils.escapeEcmaScript(arrSplit[ar])
											+ "'",
									driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
											+ oltagString + "li[" + listVal + "]")));
						}
						if (enter == true) {
							enter = false;
						} else {
							driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/"
									+ oltagString + "li[" + listVal + "]")).sendKeys(Keys.ENTER);
						}
					}
				}
			} else {
				attachmentCount++;

				try {
					driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[2]/div/div[8]/button[2]"))
							.click();
				} catch (ElementClickInterceptedException e) {
					Frame1.appendText("catch");
					js = (JavascriptExecutor) driver;
					js.executeScript("window.scrollBy(0,-250)");
					driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[2]/div/div[8]/button[2]"))
							.click();
				}

				driver.findElement(By.name("files")).sendKeys(imageURL);

				int tmp = 0;
				long t = System.currentTimeMillis();
				long end = t + 100000;

				do {
					if (System.currentTimeMillis() > end) {
						Frame1.appendText("image upload timeout");
						tmp = 1;
						break;
					}

					if (driver
							.findElements(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]//following::img["
									+ attachmentCount + "]"))
							.size() > 0) {
						if (driver.findElement(
								By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]//following::img["
										+ attachmentCount + "]"))
								.isDisplayed()) {
							Frame1.appendText("File is attached");
							tmp = 1;
							if (arrSplit.length > 1) {
								driver.findElement(By
										.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
										.sendKeys(Keys.ENTER);
							}
						}
					} else {
						Frame1.appendText("File is still not attached");
						tmp = 0;
					}
				} while (tmp == 0);
			}

			if (orderlist.equals("start") || orderlist.equals("end")) {
				listVal++;
			} else {
				countTag++;
			}

			if (orderlist.equals("end")) {
				driver.findElement(
						By.xpath("//div[@id='description']//button[@aria-label='Ordered list (CTRL+SHIFT+NUM7)']"))
						.click();
//				driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
//						.sendKeys(Keys.ENTER);
				orderlist = "stop";
				subOrderList = "stop";
				oltagString = "";
				AddBugTask.oltagStringGlobal = "";
				subOrderListNumber = 0;
			}

			if (subOrderList.equals("end")) {
				int k = 0;
				while (k < slashsubnumbercount) {
					subnumbercnt = subnumbercnt - 1;
					driver.findElement(By.xpath(
							"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/" + oltagString + "li[" + listVal + "]"))
							.sendKeys(Keys.CONTROL + "[");
					k++;

					subOrderList = "stop";
					orderListNumber = tempListNumber;

					listVal = oldlistVal;

					String str = oltagString;
					count = 0;
					fromIndex = 0;

					String strFind = "/ol[" + subOrderListNumber + "]/";

					while ((fromIndex = str.indexOf(strFind, fromIndex)) != -1) {
						count++;
						fromIndex++;
					}

					if (tempsubOrderListNumber != 0) {
						oltagString = oltagString.substring(0, oltagString.length() - 6);
//					oltagString = oltagString.replace("/ol[" + subOrderListNumber + "]/", "/");
					} else {
						oltagString = oltagString.substring(0, oltagString.length() - 6);
//					oltagString = oltagString.replace("/ol[" + subOrderListNumber + "]/", "/");
					}

					if (tempsubOrderListNumber != 0) {
						subOrderListNumber = tempsubOrderListNumber;
					}
				}

				if (subnumbercnt == 0) {
					numlistVal++;
					subOrderList = "stop";
					listVal = numlistVal;
				}

				k = 0;
				slashsubnumbercount = 0;
			}
		}

		if (AddBugTask.ACFileAvailable.equals("true")) {
			if (orderlist.equals("start")) {
				driver.findElement(
						By.xpath("//div[@id='description']//button[@aria-label='Ordered list (CTRL+SHIFT+NUM7)']"))
						.click();
				driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
						.sendKeys(Keys.ENTER);
				orderlist = "stop";
			}
		}

		AddBugTask.oltagStringGlobal = oltagString;
	}

	public void removeExtraSpace() throws InterruptedException, IOException, GeneralSecurityException {
		orderListNumber = 0;
		subOrderListNumber = 0;
		tempListNumber = 0;
		tempsubOrderListNumber = 0;
		subNumberCount = 0;
		numberCount = 0;
		orderlist = "stop";
		subOrderList = "stop";
		listVal = 0;
		oldlistVal = 0;
		numlistVal = 0;
		oldnumlistVal = 0;
		subnumbercnt = 0;
		count = 0;
		fromIndex = 0;
		slashsubnumbercount = 0;

		Frame1.appendText("Removing Extra Space");
//		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
//		LocalDateTime now = LocalDateTime.now();
//		Frame1.appendText(dtf.format(now));

		int pTag = 2;
		int totalPTag = driver.findElements(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p")).size();
		while (pTag <= totalPTag) {
//			dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
//			now = LocalDateTime.now();
//			Frame1.appendText("time 1 = " + dtf.format(now));
			if (driver.findElements(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + pTag + "]"))
					.size() > 0) {
//				dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
//				now = LocalDateTime.now();
//				Frame1.appendText("time 2 = " + dtf.format(now));
				if (pTag == 2 && driver
						.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + pTag + "]"))
						.getText().isEmpty()) {
					pTag = pTag + 2;
				} else {
//					dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
//					now = LocalDateTime.now();
//					Frame1.appendText("time 3 = " + dtf.format(now));
					if (driver
							.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + pTag + "]"))
							.getText().isEmpty()) {
//						dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
//						now = LocalDateTime.now();
//						Frame1.appendText("time 4 = " + dtf.format(now));
						List<WebElement> myResult = driver.findElements(
								By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + pTag + "]/img"));
//						dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
//						now = LocalDateTime.now();
//						Frame1.appendText("time 14 = " + dtf.format(now));
						if (driver
								.findElements(By
										.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + pTag + "]/img"))
								.size() > 0) {
							pTag++;
						} else {
//							dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
//							now = LocalDateTime.now();
//							Frame1.appendText("time 5 = " + dtf.format(now));
							if (driver
									.findElements(By.xpath(
											"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + (pTag + 1) + "]"))
									.size() > 0
									&& driver
											.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
													+ (pTag + 1) + "]"))
											.getText()
											.equals(GetSheetData.getData("Dev Tracker!B6").get(0).get(0).toString())) {
								js = (JavascriptExecutor) driver;
								js.executeScript("arguments[0].remove()", driver.findElement(
										By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + pTag + "]")));
							} else if (driver
									.findElements(By.xpath(
											"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + (pTag + 1) + "]"))
									.size() > 0
									&& driver
											.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
													+ (pTag + 1) + "]"))
											.getText()
											.equals(GetSheetData.getData("Dev Tracker!B7").get(0).get(0).toString())) {
								js = (JavascriptExecutor) driver;
								js.executeScript("arguments[0].remove()", driver.findElement(
										By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + pTag + "]")));
							} else {
//								dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
//								now = LocalDateTime.now();
//								Frame1.appendText("time 6 = " + dtf.format(now));
								pTag++;
							}
						}
					} else {
//						dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
//						now = LocalDateTime.now();
//						Frame1.appendText("time 7 = " + dtf.format(now));
						pTag++;
					}
//					dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
//					now = LocalDateTime.now();
//					Frame1.appendText("time 8 = " + dtf.format(now));
					if (driver
							.findElements(
									By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + pTag + "]/br"))
							.size() > 0) {
						js = (JavascriptExecutor) driver;
						js.executeScript("arguments[0].remove()", driver.findElement(
								By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + pTag + "]/br")));
					}
				}
			} else {
				break;
			}
		}
		Frame1.appendText("extra space remove done");

//		dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
//		now = LocalDateTime.now();
//		Frame1.appendText(dtf.format(now));
	}

	public void checkLoader() throws InterruptedException {
		int tmp = 1;
		long t = System.currentTimeMillis();
		long end = t + 80000;

		do {
			if (System.currentTimeMillis() > end) {
				Frame1.appendText("timeout");
				tmp = 1;
			}

			if (driver.findElements(By.id("indicator")).size() > 0
					&& driver.findElement(By.id("indicator")).getAttribute("class").equals("indicator hide")) {
				tmp = 0;
			} else {
				tmp = 1;
			}

		} while (tmp == 1);
	}

	public boolean checkElementAvailibility(String xpath) {
		int tmp = 1;
		long t = System.currentTimeMillis();
		long end = t + 80000;

		do {
			if (System.currentTimeMillis() > end) {
				Frame1.appendText("timeout");
				tmp = 1;
			}

			if (driver.findElements(By.xpath(xpath)).size() > 0) {
				tmp = 0;
			} else {
				tmp = 1;
			}

		} while (tmp == 1);

		if (tmp == 0) {
			return true;
		} else {
			return false;
		}
	}
}
