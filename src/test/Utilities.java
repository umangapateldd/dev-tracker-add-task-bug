package test;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipOutputStream;

import org.apache.commons.text.StringEscapeUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
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
	int sumTab = 0;
	int prevSumTab = 0;

	public void openBrowser(String headless) {

		DesiredCapabilities chrome = new DesiredCapabilities();
		String downloadFilepath = "F:\\MscIT\\AFD\\dev-tracker-add-task-bug\\Download Files\\";
		HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
		chromePrefs.put("profile.default_content_settings.popups", 0);
		chromePrefs.put("download.default_directory", downloadFilepath);
		ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("prefs", chromePrefs);
		options.addArguments("--test-type");
		options.addArguments("--disable-popup-blocking");
		options.addArguments("--proxy-server='direct://'");
		options.addArguments("--proxy-bypass-list=*");
		if (headless.toLowerCase().equals("yes")) {
			options.addArguments("--headless");
		}
		options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
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
	public void macTextFormat(String imagePath, String pmName, String pmComment, Cell descriptionType, String htmlTag,
			Sheet shac, int row) throws InterruptedException {
		String oltagString = AddBugTask.oltagStringGlobal;
		int countTag = 0;
		ArrayList<String> olArray = new ArrayList();
		int position = 0;
		String xpathNumbering = "";

		if (htmlTag.contains("p[")) {
			// objective / reference
			countTag = Integer.parseInt(htmlTag.replace("p[", "").replace("]", ""));
		} else {
			// cos
			countTag = driver.findElements(By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p")).size();
		}

		if ("p[4]".equals(htmlTag)) {
			xpathNumbering = "(//div[@id='description']/div/div[3]/div[3]/div[2]//p[4]//following::ol/li)[last()]";
		} else if ("p[2]".equals(htmlTag)) {
			xpathNumbering = "(//div[@id='description']/div/div[3]/div[3]/div[2]//p[4]//preceding::ol/li)[last()]";
		} else {
			xpathNumbering = "(//div[@id='description']/div/div[3]/div[3]/div[2]//span[text()='Acceptance Criteria']//following::ol/li)[last()]";
		}

		String cosString = descriptionType.getContents();
		String onlyCOS = "";

		if (!pmComment.isEmpty()) {
			if (pmComment.equals(".")) {
				onlyCOS = cosString;
				cosString = cosString + "\n~" + pmName.trim() + "~";
			} else {
				onlyCOS = cosString;
				cosString = cosString + "\n$Comment from PM:$ ~" + pmName.trim() + "~\n" + pmComment;
			}
		}

		String[] arrSplit = null;
		if (cosString.startsWith("AAC")) {
			int totalRowsAC = shac.getRows();
			int totalColAC = shac.getColumns();

			boolean shacData = false;

			for (int rowAC = 0; rowAC < totalRowsAC;) {
				for (int colAC = 0; colAC < totalColAC; colAC++) {
					if (onlyCOS.isEmpty()) {
						if (cosString.equals(shac.getCell(colAC, rowAC).getContents())) {
							arrSplit = shac.getCell(colAC, rowAC + 1).getContents().split("\n");
							shacData = true;
							break;
						}
					} else {
						if (onlyCOS.equals(shac.getCell(colAC, rowAC).getContents())) {
							String str1 = "";
							if (pmComment.equals(".")) {
								str1 = shac.getCell(colAC, rowAC + 1).getContents() + "\n~" + pmName.trim() + "~";
							} else {
								str1 = shac.getCell(colAC, rowAC + 1).getContents() + "\n$Comment from PM:$ ~"
										+ pmName.trim() + "~\n" + pmComment;
							}

							arrSplit = str1.split("\n");
							shacData = true;
							break;
						}
					}
				}
				rowAC = rowAC + 2;
			}

			if (!shacData) {
				arrSplit = cosString.split("\n");
			}
		} else {
			arrSplit = cosString.split("\n");
		}

		int attachmentCount = 0;
		boolean exists = false;
		boolean isFile = false;
		boolean alreadybold = false;

		for (int ar = 0; ar < arrSplit.length; ar++) {
			int k1;
			int len;
			String str1;
			char[] characterArray = new char[250];
			int spaceCount = 0;
			sumTab = 0;
			str1 = arrSplit[ar];
			len = str1.length();
			if (arrSplit[ar].toLowerCase(Locale.ENGLISH).contains("{number}") || "start".equals(orderlist)) {
				for (k1 = 0; k1 < len; k1++) {
					if (str1.substring(k1, k1 + 1).equals(" ")) {
						spaceCount++;
					} else {
						break;
					}
				}
				if (spaceCount % 8 == 0) {
					sumTab = spaceCount / 8;
				}
			}

			if ("stop".equals(orderlist)) {
				if (driver.findElement(By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
						.getText().isEmpty()) {

				} else {
					js = (JavascriptExecutor) driver;
					js.executeScript(
							"arguments[0].innerHTML = '" + StringEscapeUtils.escapeEcmaScript(driver
									.findElement(By.xpath(
											"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
									.getAttribute("innerHTML").replace("<b><br></b>", "").replace("<br>", "")
									.replace("&nbsp;&nbsp;", "")) + "'",
							driver.findElement(
									By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]")));

					driver.findElement(
							By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + (countTag) + "]")).click();
					Thread.sleep(1500);
					driver.findElement(
							By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + (countTag) + "]"))
							.sendKeys(Keys.END);
					driver.findElement(
							By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + (countTag) + "]"))
							.sendKeys(Keys.ENTER);
					countTag++;
				}
			}

			if (arrSplit[ar].toLowerCase(Locale.ENGLISH).contains("{number}")) {
				orderListNumber++;
				orderlist = "start";
				listVal = 1;
				numlistVal = 1;
				olArray.add(position, "1");

				oltagString = oltagString + "ol[" + orderListNumber + "]/";
				Thread.sleep(1500);

				js = (JavascriptExecutor) driver;
				js.executeScript("arguments[0].click();", driver.findElement(
						By.xpath("//div[@id='description']//button[@aria-label='Ordered list (CTRL+SHIFT+NUM7)']")));

				if (arrSplit[ar].toLowerCase().contains("{/number}")) {
					orderlist = "end";
					subnumbercnt = 0;
				}
				prevSumTab = sumTab;
			}

			if (prevSumTab != sumTab && prevSumTab > sumTab) {

				if (prevSumTab - sumTab > 1) {
					position = position - (prevSumTab - sumTab);
					olArray.add(position, String.valueOf(Integer.parseInt(olArray.get(position)) - 1));
					int i = 1;
					while (i <= (prevSumTab - sumTab)) {

						Thread.sleep(1000);
//						driver.findElement(By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/" + oltagString
//								+ "li[" + (listVal) + "]")).sendKeys(Keys.CONTROL + "[");

						driver.findElement(By.xpath(xpathNumbering)).sendKeys(Keys.CONTROL + "[");

						oltagString = oltagString.substring(0, oltagString.length() - 6);
						i++;
					}

					subOrderListNumber = Integer.parseInt(olArray.get(position));
				} else {

					olArray.add(position, String.valueOf(Integer.parseInt(olArray.get(position)) - 1));
					position = position - 1;

					olArray.add(position, String.valueOf(Integer.parseInt(olArray.get(position)) - 1));
					driver.findElement(By.xpath(xpathNumbering)).sendKeys(Keys.CONTROL + "[");
					subOrderListNumber = Integer.parseInt(olArray.get(position));

					oltagString = oltagString.substring(0, oltagString.length() - 6);
				}

				listVal = oldlistVal;

			} else if (prevSumTab != sumTab && prevSumTab < sumTab) {
				position = position + 1;
				if (olArray.size() > position) {
					olArray.add(position, String.valueOf(Integer.parseInt(olArray.get(position)) + 1));
				} else {
					olArray.add(position, "1");
				}

				numlistVal = listVal;
				subOrderListNumber = Integer.parseInt(olArray.get(position));
				driver.findElement(By.xpath(xpathNumbering)).sendKeys(Keys.CONTROL + "]");

				oltagString = oltagString + "ol[" + subOrderListNumber + "]/";
				oldlistVal = listVal;

				listVal = 1;
				tempListNumber = orderListNumber;
			}

			if (arrSplit[ar].toLowerCase().contains("{/number}")) {
				if (prevSumTab == 0) {
					driver.findElement(By.xpath(xpathNumbering)).sendKeys(Keys.CONTROL + "[");
				} else {
					driver.findElement(By.xpath(xpathNumbering)).sendKeys(Keys.ENTER);
					Thread.sleep(1500);
					driver.findElement(By.xpath(xpathNumbering)).sendKeys(Keys.ENTER);
				}
				orderlist = "end";
				prevSumTab = 0;
				sumTab = 0;
			}

			prevSumTab = sumTab;

			exists = false;
			arrSplit[ar] = arrSplit[ar].replace("{number}", "");
			arrSplit[ar] = arrSplit[ar].replace("{/number}", "");
			File tempFile = new File(imagePath + arrSplit[ar]);
			String imageURL = "";
			if (arrSplit[ar].isEmpty()) {
				imageURL = arrSplit[ar];
			} else {
				isFile = tempFile.isFile();
				exists = tempFile.exists();
				imageURL = imagePath + arrSplit[ar];
			}

			if (!isFile || arrSplit[ar].isEmpty()) {
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
				} else {
					if (arrSplit[ar].contains("\t")) {
					} else {
						arrSplit[ar] = arrSplit[ar].trim();
					}
				}

				if ("stop".equals(orderlist)) {
					if (driver
							.findElements(
									By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
							.size() <= 0) {
						driver.findElement(
								By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + (countTag - 1) + "]"))
								.click();

						driver.findElement(
								By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + (countTag - 1) + "]"))
								.sendKeys(Keys.END);
						driver.findElement(
								By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + (countTag - 1) + "]"))
								.sendKeys(Keys.ENTER);
					}

					Dimension newDimension = new Dimension(2500, 2768);
					if (!driver.manage().window().getSize().equals(newDimension)) {
						driver.manage().window().setSize(newDimension);
					}

					if (driver
							.findElement(
									By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
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
								.findElements(By
										.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]/b"))
								.size() > 0) {
							if (driver
									.findElement(By.xpath(
											"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]/b"))
									.getText().isEmpty()
									|| driver.findElements(By.xpath(
											"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]/b/br"))
											.size() > 0) {
								js = (JavascriptExecutor) driver;
								js.executeScript("arguments[0].remove()", driver.findElement(By.xpath(
										"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]/b")));

								js.executeScript(
										"arguments[0].innerHTML = '"
												+ StringEscapeUtils.escapeEcmaScript(driver
														.findElement(By.xpath(
																"//*[@id='description']/div/div[3]/div[3]/div[2]/p["
																		+ countTag + "]"))
														.getAttribute("innerHTML") + "<br>")
												+ "'",
										driver.findElement(By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p["
												+ countTag + "]")));
							}
						}

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
													"//*[@id='description']/div/div[3]/div[2]/div/div[2]/button[1]"))
											.getAttribute("class")
											.equals("note-btn btn btn-default btn-sm note-btn-bold active")) {
										if (systemName.contains("mac")) {

										} else {
											driver.findElement(
													By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p["
															+ countTag + "]"))
													.click();

											driver.findElement(
													By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p["
															+ countTag + "]"))
													.sendKeys(Keys.END);

											driver.findElement(
													By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p["
															+ countTag + "]"))
													.sendKeys(Keys.CONTROL + "b");
										}
									} else {

									}
								} else {

								}

								if (once == 0) {
									driver.findElement(By.xpath(
											"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
											.click();

									driver.findElement(By.xpath(
											"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
											.sendKeys(Keys.END);

									driver.findElement(By.xpath(
											"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
											.sendKeys(arrSplit[ar].substring(0, a[abc]));
									once = 1;
								}

								if (arrSplit[ar].charAt(a[abc]) == '<') {

									try {
										driver.findElement(By
												.xpath("//*[@id='description']/div/div[3]/div[2]/div/div[8]/button[1]"))
												.click();
									} catch (ElementClickInterceptedException e) {
										js = (JavascriptExecutor) driver;
										js.executeScript("window.scrollBy(0,-250)");
										driver.findElement(By
												.xpath("//*[@id='description']/div/div[3]/div[2]/div/div[8]/button[1]"))
												.click();
									}

									driver.findElement(By.xpath(
											"//*[@id='description']/div/div[3]/div[5]/div[2]/div/div[2]/div[1]/input"))
											.sendKeys(arrSplit[ar].substring(a[abc] + 1, a[abc + 1]));

									driver.findElement(By.xpath(
											"//*[@id='description']/div/div[3]/div[5]/div[2]/div/div[2]/div[2]/input"))
											.sendKeys(arrSplit[ar].substring(a[abc + 1] + 1, a[abc + 2]));

									driver.findElement(By
											.xpath("//*[@id='description']/div/div[3]/div[5]/div[2]/div/div[3]/button"))
											.click();

									driver.findElement(By.xpath(
											"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
											.sendKeys(Keys.ARROW_RIGHT);

									if (driver.findElements(By.xpath(
											"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]/b"))
											.size() > 0) {
										if (driver
												.findElement(
														By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p["
																+ countTag + "]/b"))
												.getText().isEmpty()
												|| driver.findElements(
														By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p["
																+ countTag + "]/b/br"))
														.size() > 0) {

											js = (JavascriptExecutor) driver;
											js.executeScript("arguments[0].remove()",
													driver.findElement(By
															.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p["
																	+ countTag + "]/b")));
										}
									}

									if (abc + 3 < a.length) {
										driver.findElement(By.xpath(
												"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
												.click();

										driver.findElement(By.xpath(
												"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
												.sendKeys(Keys.END);
										driver.findElement(By.xpath(
												"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
												.sendKeys(arrSplit[ar].substring(a[abc + 2] + 1, a[abc + 3]));
									} else {
										driver.findElement(By.xpath(
												"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
												.click();

										driver.findElement(By.xpath(
												"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
												.sendKeys(Keys.END);
										driver.findElement(By.xpath(
												"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
												.sendKeys(
														arrSplit[ar].substring(a[abc + 2] + 1, arrSplit[ar].length()));
									}
									abc = abc + 3;
								} else if (arrSplit[ar].charAt(a[abc]) == '$') {

									int boldWithColor = 0;

									js = (JavascriptExecutor) driver;
									js.executeScript(
											"arguments[0].innerHTML = '" + StringEscapeUtils.escapeEcmaScript(driver
													.findElement(By
															.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p["
																	+ countTag + "]"))
													.getAttribute("innerHTML").replace("<b><br></b>", "")
													.replace("<br>", "").replace("&nbsp;&nbsp;", "") + " <b>"
													+ arrSplit[ar].substring(a[abc] + 1, a[abc + 1]) + "</b>") + "'",
											driver.findElement(
													By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p["
															+ countTag + "]")));

									if (arrSplit[ar].charAt(a[abc + 1]) == '^') {
										if (driver.findElement(By
												.xpath("//*[@id='description']/div/div[3]/div[2]/div/div[2]/button[1]"))
												.getAttribute("class")
												.equals("note-btn btn btn-default btn-sm note-btn-bold active")) {
											alreadybold = true;
										} else {
											alreadybold = false;
										}

										boldWithColor = 1;
										try {
											driver.findElement(By.xpath(
													"//*[@id='description']/div/div[3]/div[2]/div/div[4]/div/button[2]"))
													.click();
										} catch (ElementClickInterceptedException e) {
											js = (JavascriptExecutor) driver;
											js.executeScript("window.scrollBy(0,-250)");
											driver.findElement(By.xpath(
													"//*[@id='description']/div/div[3]/div[2]/div/div[4]/div/button[2]"))
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
													"//*[@id='description']/div/div[3]/div[2]/div/div[2]/button[1]"))
													.getAttribute("class")
													.equals("note-btn btn btn-default btn-sm note-btn-bold active")) {
											} else {
												if (systemName.contains("mac")) {

												} else {
													driver.findElement(By
															.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p["
																	+ countTag + "]"))
															.sendKeys(Keys.CONTROL + "b");
												}
											}
											alreadybold = false;
										}

										driver.findElement(By.xpath(
												"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
												.sendKeys(arrSplit[ar].substring(a[abc + 2] + 1, a[abc + 3]));

										try {
											driver.findElement(By.xpath(
													"//*[@id='description']/div/div[3]/div[2]/div/div[4]/div/button[2]"))
													.click();
										} catch (ElementClickInterceptedException e) {
											js = (JavascriptExecutor) driver;
											js.executeScript("window.scrollBy(0,-250)");
											driver.findElement(By.xpath(
													"//*[@id='description']/div/div[3]/div[2]/div/div[4]/div/button[2]"))
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
													By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p["
															+ countTag + "]"))
													.sendKeys(arrSplit[ar].substring(a[abc + 4] + 1, a[abc + 5]));
										} else {
											driver.findElement(
													By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p["
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
																"//*[@id='description']/div/div[3]/div[3]/div[2]/p["
																		+ countTag + "]"))
														.getAttribute("innerHTML").replace("<b><br></b>", "")
														.replace("<br>", "").replace("&nbsp;&nbsp;", "")
														+ arrSplit[ar].substring(a[abc + 1] + 1, a[abc + 2])) + "'",
												driver.findElement(
														By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p["
																+ countTag + "]")));

										driver.findElement(By.xpath(
												"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
												.sendKeys(Keys.COMMAND + "" + Keys.ARROW_RIGHT);
									} else {
										js = (JavascriptExecutor) driver;
										js.executeScript(
												"arguments[0].innerHTML = '" + StringEscapeUtils.escapeEcmaScript(driver
														.findElement(By.xpath(
																"//*[@id='description']/div/div[3]/div[3]/div[2]/p["
																		+ countTag + "]"))
														.getAttribute("innerHTML").replace("<b><br></b>", "")
														.replace("<br>", "").replace("&nbsp;&nbsp;", "")
														+ arrSplit[ar].substring(a[abc + 1] + 1, arrSplit[ar].length()))
														+ "'",
												driver.findElement(
														By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p["
																+ countTag + "]")));

										driver.findElement(By.xpath(
												"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
												.sendKeys(Keys.COMMAND + "" + Keys.ARROW_RIGHT);
									}
									abc = abc + 2;
								} else if (arrSplit[ar].charAt(a[abc]) == '~') {
									String username = arrSplit[ar].substring(a[abc] + 1, a[abc + 1]);
									driver.findElement(By.xpath(
											"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
											.click();

									driver.findElement(By.xpath(
											"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
											.sendKeys(Keys.END);

									driver.findElement(By.xpath(
											"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
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
													By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p["
															+ countTag + "]"))
													.sendKeys(Keys.ENTER);
										} else if (userCount > 1) {

											for (int cnt = 1; cnt <= userCount; cnt++) {

												if (username.toLowerCase()
														.equals(driver.findElement(By.xpath("//div[" + cnt
																+ "][contains(@class,'note-hint-item')]/strong"))
																.getText().toLowerCase())) {

													driver.findElement(By
															.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p["
																	+ countTag + "]"))
															.sendKeys(Keys.ENTER);
												} else {
													driver.findElement(By
															.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p["
																	+ countTag + "]"))
															.sendKeys(Keys.ARROW_DOWN);
												}
											}
										} else {
											Frame1.appendText("Given username is not available");
										}
									} else {
										driver.findElement(By.xpath(
												"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
												.sendKeys(username.substring(username.indexOf(" "), username.length()));
									}
//									
									if (abc + 2 < a.length) {
										js = (JavascriptExecutor) driver;
										js.executeScript(
												"arguments[0].innerHTML = '" + StringEscapeUtils.escapeEcmaScript(driver
														.findElement(By.xpath(
																"//*[@id='description']/div/div[3]/div[3]/div[2]/p["
																		+ countTag + "]"))
														.getAttribute("innerHTML").replace("<b><br></b>", "")
														.replace("<br>", "").replace("&nbsp;&nbsp;", "")
														+ arrSplit[ar].substring(a[abc + 1] + 1, a[abc + 2])) + "'",
												driver.findElement(
														By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p["
																+ countTag + "]")));
										if (systemName.contains("mac")) {
											driver.findElement(
													By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p["
															+ countTag + "]"))
													.sendKeys(Keys.COMMAND + "" + Keys.ARROW_RIGHT);
										}
									} else {

										js = (JavascriptExecutor) driver;
										js.executeScript(
												"arguments[0].innerHTML = '" + StringEscapeUtils.escapeEcmaScript(driver
														.findElement(By.xpath(
																"//*[@id='description']/div/div[3]/div[3]/div[2]/p["
																		+ countTag + "]"))
														.getAttribute("innerHTML").replace("<b><br></b>", "")
														.replace("<br>", "").replace("&nbsp;&nbsp;", "")
														+ arrSplit[ar].substring(a[abc + 1] + 1, arrSplit[ar].length()))
														+ "'",
												driver.findElement(
														By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p["
																+ countTag + "]")));
										driver.findElement(By.xpath(
												"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
												.sendKeys(Keys.COMMAND + "" + Keys.ARROW_RIGHT);
									}
									abc = abc + 2;
								} else {

									if (arrSplit[ar].charAt(a[abc]) == '\n') {

										enter = true;
										driver.findElement(By.xpath(
												"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
												.sendKeys(Keys.RETURN);
									} else {

										driver.findElement(By.xpath(
												"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
												.sendKeys(arrSplit[ar].substring(a[abc], a[abc + 1]));
									}

									abc++;

								}
							}
						} else {
							driver.findElement(
									By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
									.click();

							driver.findElement(
									By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
									.sendKeys(Keys.END);
							js = (JavascriptExecutor) driver;
							js.executeScript(
									"arguments[0].innerHTML = '" + StringEscapeUtils.escapeEcmaScript(arrSplit[ar])
											+ "'",
									driver.findElement(By.xpath(
											"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]")));
						}

						if (enter == true) {
							enter = false;
						} else {
							if (arrSplit[ar].isEmpty()) {
								driver.findElement(By.xpath(
										"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + (countTag - 1) + "]"))
										.sendKeys(Keys.ENTER);
							} else {
								driver.findElement(
										By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
										.click();
								Thread.sleep(1500);
								int size = driver.findElements(By.xpath(
										"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + (countTag + 1) + "]"))
										.size();

								if (size > 0) {
									String str = driver
											.findElement(By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p["
													+ (countTag + 1) + "]"))
											.getText();
									if (str.equals("Acceptance Criteria") || str.equals("References")) {
										driver.findElement(By.xpath(
												"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
												.sendKeys(Keys.END);
										driver.findElement(By.xpath(
												"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
												.sendKeys(Keys.ENTER);
									} else {
										driver.findElement(By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p["
												+ (countTag + 1) + "]")).click();

										driver.findElement(By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p["
												+ (countTag + 1) + "]")).sendKeys(Keys.END);
										driver.findElement(By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p["
												+ (countTag + 1) + "]")).sendKeys(Keys.ENTER);

										countTag++;
									}
								} else {
									driver.findElement(By.xpath(
											"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
											.sendKeys(Keys.END);
									driver.findElement(By.xpath(
											"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
											.sendKeys(Keys.ENTER);
								}
							}

						}
					}
				} else {
					if (!arrSplit[ar].isEmpty()) {
						try {
							driver.findElement(By.xpath(xpathNumbering)).getText();
						} catch (NoSuchElementException e) {
							int lisize = driver.findElements(By.xpath(xpathNumbering)).size();
							listVal = lisize;
						}

						if (driver.findElement(By.xpath(xpathNumbering)).getText().isEmpty()) {
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
							if (driver.findElements(By.xpath(xpathNumbering + "/b")).size() > 0) {
								if (driver.findElement(By.xpath(xpathNumbering + "/b")).getText().isEmpty()
										|| driver.findElements(By.xpath(xpathNumbering + "/b/br")).size() > 0) {
									js = (JavascriptExecutor) driver;
									js.executeScript("arguments[0].click();",
											driver.findElement(By.xpath("//*[@data-original-title='Bold (CTRL+B)']")));

									js.executeScript("arguments[0].remove()",
											driver.findElement(By.xpath(xpathNumbering + "/b")));

									js.executeScript(
											"arguments[0].innerHTML = '" + StringEscapeUtils
													.escapeEcmaScript(driver.findElement(By.xpath(xpathNumbering))
															.getAttribute("innerHTML") + "<br>")
													+ "'",
											driver.findElement(By.xpath(xpathNumbering)));
								}
							}

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
										if (driver.findElement(By
												.xpath("//*[@id='description']/div/div[3]/div[2]/div/div[2]/button[1]"))
												.getAttribute("class")
												.equals("note-btn btn btn-default btn-sm note-btn-bold active")) {
											if (systemName.contains("mac")) {

											} else {
												driver.findElement(
														By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/"
																+ oltagString + "li[" + listVal + "]"))
														.sendKeys(Keys.CONTROL + "b");
											}
										} else {

										}
									} else {

									}

									if (once == 0) {

										driver.findElement(
												By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]//li/br"))
												.sendKeys(arrSplit[ar].substring(0, a[abc]));
										once = 1;
									}

									if (arrSplit[ar].charAt(a[abc]) == '<') {

										try {
											driver.findElement(By.xpath(
													"//*[@id='description']/div/div[3]/div[2]/div/div[8]/button[1]"))
													.click();
										} catch (ElementClickInterceptedException e) {
											js = (JavascriptExecutor) driver;
											js.executeScript("window.scrollBy(0,-250)");
											driver.findElement(By.xpath(
													"//*[@id='description']/div/div[3]/div[2]/div/div[8]/button[1]"))
													.click();
										}

										driver.findElement(By.xpath(
												"//*[@id='description']/div/div[3]/div[5]/div[2]/div/div[2]/div[1]/input"))
												.sendKeys(arrSplit[ar].substring(a[abc] + 1, a[abc + 1]));

										driver.findElement(By.xpath(
												"//*[@id='description']/div/div[3]/div[5]/div[2]/div/div[2]/div[2]/input"))
												.sendKeys(arrSplit[ar].substring(a[abc + 1] + 1, a[abc + 2]));

										driver.findElement(By.xpath(
												"//*[@id='description']/div/div[3]/div[5]/div[2]/div/div[3]/button"))
												.click();

										driver.findElement(By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/"
												+ oltagString + "li[" + listVal + "]")).sendKeys(Keys.ARROW_RIGHT);

										if (driver
												.findElements(
														By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/"
																+ oltagString + "li[" + listVal + "]" + "/b"))
												.size() > 0) {
											if (driver
													.findElement(
															By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/"
																	+ oltagString + "li[" + listVal + "]" + "/b"))
													.getText().isEmpty()
													|| driver.findElements(
															By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/"
																	+ oltagString + "li[" + listVal + "]" + "/b/br"))
															.size() > 0) {

												js = (JavascriptExecutor) driver;
												js.executeScript("arguments[0].remove()",
														driver.findElement(By.xpath(
																"//*[@id='description']/div/div[3]/div[3]/div[2]/"
																		+ oltagString + "li[" + listVal + "]" + "/b")));
											}
										}

										if (abc + 3 < a.length) {
											driver.findElement(
													By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/"
															+ oltagString + "li[" + listVal + "]"))
													.sendKeys(arrSplit[ar].substring(a[abc + 2] + 1, a[abc + 3]));
										} else {
											driver.findElement(
													By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/"
															+ oltagString + "li[" + listVal + "]"))
													.sendKeys(arrSplit[ar].substring(a[abc + 2] + 1,
															arrSplit[ar].length()));
										}
										abc = abc + 3;
									} else if (arrSplit[ar].charAt(a[abc]) == '$') {

										int boldWithColor = 0;

										JavascriptExecutor js = (JavascriptExecutor) driver;
										js.executeScript(
												"arguments[0].innerHTML = '" + StringEscapeUtils.escapeEcmaScript(driver
														.findElement(By.xpath(
																"//*[@id='description']/div/div[3]/div[3]/div[2]/"
																		+ oltagString + "li[" + listVal + "]"))
														.getAttribute("innerHTML").replace("<b><br></b>", "")
														.replace("<br>", "").replace("&nbsp;&nbsp;", "") + " <b>"
														+ arrSplit[ar].substring(a[abc] + 1, a[abc + 1]) + "</b>")
														+ "'",
												driver.findElement(
														By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/"
																+ oltagString + "li[" + listVal + "]")));

										if (arrSplit[ar].charAt(a[abc + 1]) == '^') {
											if (driver.findElement(By.xpath(
													"//*[@id='description']/div/div[3]/div[2]/div/div[2]/button[1]"))
													.getAttribute("class")
													.equals("note-btn btn btn-default btn-sm note-btn-bold active")) {
												alreadybold = true;
											} else {
												alreadybold = false;
											}

											boldWithColor = 1;
											try {
												driver.findElement(By.xpath(
														"//*[@id='description']/div/div[3]/div[2]/div/div[4]/div/button[2]"))
														.click();
											} catch (ElementClickInterceptedException e) {
												js = (JavascriptExecutor) driver;
												js.executeScript("window.scrollBy(0,-250)");
												driver.findElement(By.xpath(
														"//*[@id='description']/div/div[3]/div[2]/div/div[4]/div/button[2]"))
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
														"//*[@id='description']/div/div[3]/div[2]/div/div[2]/button[1]"))
														.getAttribute("class")
														.equals("note-btn btn btn-default btn-sm note-btn-bold active")) {
												} else {
													if (systemName.contains("mac")) {

													} else {
														driver.findElement(By.xpath(
																"//*[@id='description']/div/div[3]/div[3]/div[2]/"
																		+ oltagString + "li[" + listVal + "]"))
																.sendKeys(Keys.CONTROL + "b");
													}
												}
												alreadybold = false;
											}

											driver.findElement(
													By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/"
															+ oltagString + "li[" + listVal + "]"))
													.sendKeys(arrSplit[ar].substring(a[abc + 2] + 1, a[abc + 3]));

											try {
												driver.findElement(By.xpath(
														"//*[@id='description']/div/div[3]/div[2]/div/div[4]/div/button[2]"))
														.click();
											} catch (ElementClickInterceptedException e) {
												js = (JavascriptExecutor) driver;
												js.executeScript("window.scrollBy(0,-250)");
												driver.findElement(By.xpath(
														"//*[@id='description']/div/div[3]/div[2]/div/div[4]/div/button[2]"))
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
														By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/"
																+ oltagString + "li[" + listVal + "]"))
														.sendKeys(arrSplit[ar].substring(a[abc + 4] + 1, a[abc + 5]));
											} else {
												driver.findElement(
														By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/"
																+ oltagString + "li[" + listVal + "]"))
														.sendKeys(arrSplit[ar].substring(a[abc + 4] + 1,
																arrSplit[ar].length()));
											}
											abc = abc + 4;
										}

										if (abc + 2 < a.length) {
											js = (JavascriptExecutor) driver;
											js.executeScript("arguments[0].innerHTML = '"
													+ StringEscapeUtils.escapeEcmaScript(driver
															.findElement(By.xpath(
																	"//*[@id='description']/div/div[3]/div[3]/div[2]/"
																			+ oltagString + "li[" + listVal + "]"))
															.getAttribute("innerHTML").replace("<b><br></b>", "")
															.replace("<br>", "").replace("&nbsp;&nbsp;", "")
															+ arrSplit[ar].substring(a[abc + 1] + 1, a[abc + 2]))
													+ "'",
													driver.findElement(
															By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/"
																	+ oltagString + "li[" + listVal + "]")));
											driver.findElement(
													By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/"
															+ oltagString + "li[" + listVal + "]"))
													.sendKeys(Keys.COMMAND + "" + Keys.ARROW_RIGHT);
										} else {
											js = (JavascriptExecutor) driver;
											js.executeScript(
													"arguments[0].innerHTML = '" + StringEscapeUtils
															.escapeEcmaScript(driver.findElement(By.xpath(
																	"//*[@id='description']/div/div[3]/div[3]/div[2]/"
																			+ oltagString + "li[" + listVal + "]"))
																	.getAttribute("innerHTML")
																	.replace("<b><br></b>", "").replace("<br>", "")
																	.replace("&nbsp;&nbsp;", "")
																	+ arrSplit[ar].substring(a[abc + 1] + 1,
																			arrSplit[ar].length()))
															+ "'",
													driver.findElement(
															By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/"
																	+ oltagString + "li[" + listVal + "]")));

											driver.findElement(
													By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/"
															+ oltagString + "li[" + listVal + "]"))
													.sendKeys(Keys.COMMAND + "" + Keys.ARROW_RIGHT);
										}
										abc = abc + 2;
									} else if (arrSplit[ar].charAt(a[abc]) == '~') {
										String username = arrSplit[ar].substring(a[abc] + 1, a[abc + 1]);
										driver.findElement(By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/"
												+ oltagString + "li[" + listVal + "]"))
												.sendKeys(" @" + username.substring(0, (username.indexOf(" "))));

										if (driver.findElement(By.xpath(
												"//body//following::div[1][contains(@class,'note-hint-popover')]"))
												.getAttribute("style").contains("display: block")) {

											int userCount = driver
													.findElements(By.xpath("//div[contains(@class,'note-hint-item')]"))
													.size();
											if (userCount == 1) {
												driver.findElement(
														By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/"
																+ oltagString + "li[" + listVal + "]"))
														.sendKeys(Keys.ENTER);
											} else if (userCount > 1) {

												for (int cnt = 1; cnt <= userCount; cnt++) {

													if (username.toLowerCase()
															.equals(driver.findElement(By.xpath("//div[" + cnt
																	+ "][contains(@class,'note-hint-item')]/strong"))
																	.getText().toLowerCase())) {

														driver.findElement(By.xpath(
																"//*[@id='description']/div/div[3]/div[3]/div[2]/"
																		+ oltagString + "li[" + listVal + "]"))
																.sendKeys(Keys.ENTER);
													} else {
														driver.findElement(By.xpath(
																"//*[@id='description']/div/div[3]/div[3]/div[2]/"
																		+ oltagString + "li[" + listVal + "]"))
																.sendKeys(Keys.ARROW_DOWN);
													}
												}
											} else {
												Frame1.appendText("Given username is not available");
											}
										} else {
											driver.findElement(
													By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/"
															+ oltagString + "li[" + listVal + "]"))
													.sendKeys(username.substring(username.indexOf(" "),
															username.length()));
										}
//									
										if (abc + 2 < a.length) {
											js = (JavascriptExecutor) driver;
											js.executeScript("arguments[0].innerHTML = '"
													+ StringEscapeUtils.escapeEcmaScript(driver
															.findElement(By.xpath(
																	"//*[@id='description']/div/div[3]/div[3]/div[2]/"
																			+ oltagString + "li[" + listVal + "]"))
															.getAttribute("innerHTML").replace("<b><br></b>", "")
															.replace("<br>", "").replace("&nbsp;&nbsp;", "")
															+ arrSplit[ar].substring(a[abc + 1] + 1, a[abc + 2]))
													+ "'",
													driver.findElement(
															By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/"
																	+ oltagString + "li[" + listVal + "]")));
											if (systemName.contains("mac")) {
												driver.findElement(
														By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/"
																+ oltagString + "li[" + listVal + "]"))
														.sendKeys(Keys.COMMAND + "" + Keys.ARROW_RIGHT);
											}
										} else {

											js = (JavascriptExecutor) driver;
											js.executeScript(
													"arguments[0].innerHTML = '" + StringEscapeUtils
															.escapeEcmaScript(driver.findElement(By.xpath(
																	"//*[@id='description']/div/div[3]/div[3]/div[2]/"
																			+ oltagString + "li[" + listVal + "]"))
																	.getAttribute("innerHTML")
																	.replace("<b><br></b>", "").replace("<br>", "")
																	.replace("&nbsp;&nbsp;", "")
																	+ arrSplit[ar].substring(a[abc + 1] + 1,
																			arrSplit[ar].length()))
															+ "'",
													driver.findElement(
															By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/"
																	+ oltagString + "li[" + listVal + "]")));
											driver.findElement(
													By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/"
															+ oltagString + "li[" + listVal + "]"))
													.sendKeys(Keys.COMMAND + "" + Keys.ARROW_RIGHT);
										}
										abc = abc + 2;
									} else {

										if (arrSplit[ar].charAt(a[abc]) == '\n') {

											enter = true;
											driver.findElement(By.xpath(xpathNumbering)).sendKeys(Keys.RETURN);
										} else {

											driver.findElement(By.xpath(xpathNumbering))
													.sendKeys(arrSplit[ar].substring(a[abc], a[abc + 1]));
										}

										abc++;

									}
								}
							} else {
								js = (JavascriptExecutor) driver;
								js.executeScript("arguments[0].innerHTML = '"
										+ StringEscapeUtils.escapeEcmaScript(arrSplit[ar]) + "'",
										driver.findElement(By.xpath(xpathNumbering)));
							}
							if (enter) {
								enter = false;
							} else {
								js = (JavascriptExecutor) driver;
								js.executeScript("arguments[0].click();", driver.findElement(By.xpath(xpathNumbering)));

								driver.findElement(By.xpath(xpathNumbering)).sendKeys(Keys.END);
								Thread.sleep(1000);
								driver.findElement(By.xpath(xpathNumbering)).sendKeys(Keys.ENTER);
							}
						}
					}
				}
			} else {
				attachmentCount++;

				if (exists) {
					try {
						driver.findElement(By.xpath("//*[@id='description']/div/div[3]/div[2]/div/div[8]/button[2]"))
								.click();
					} catch (ElementClickInterceptedException e) {
						Frame1.appendText("catch");
						js = (JavascriptExecutor) driver;
						js.executeScript("window.scrollBy(0,-250)");
						driver.findElement(By.xpath("//*[@id='description']/div/div[3]/div[2]/div/div[8]/button[2]"))
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

						if (driver.findElements(
								By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]//following::img["
										+ attachmentCount + "]"))
								.size() > 0) {
							if (driver.findElement(
									By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]//following::img["
											+ attachmentCount + "]"))
									.isDisplayed()) {
								Frame1.appendText("File is attached");
								tmp = 1;
								if (arrSplit.length > 1) {
									driver.findElement(By.xpath(
											"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
											.sendKeys(Keys.ENTER);
								}

								driver.findElement(By.xpath(
										"//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]/img"))
										.click();
								Thread.sleep(1500);

								driver.findElement(By.xpath(
										"/html/body//div[contains(@style,'display: block')]/div[2]/div[1]/button[1]"))
										.click();
								Thread.sleep(1500);
							}
						} else {
							Frame1.appendText("File is still not attached");
							tmp = 0;
						}
					} while (tmp == 0);
				}
			}

			if (orderlist.equals("start") || orderlist.equals("end")) {
				listVal++;
			} else {
				countTag++;
			}

			if (orderlist.equals("end")) {
//				driver.findElement(
//						By.xpath("//div[@id='description']//button[@aria-label='Ordered list (CTRL+SHIFT+NUM7)']"))
//						.click();
//				driver.findElement(By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
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
					driver.findElement(By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]//li/br"))
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

//					if (tempsubOrderListNumber != 0) {
//						oltagString = oltagString.substring(0, oltagString.length() - 6);
////					oltagString = oltagString.replace("/ol[" + subOrderListNumber + "]/", "/");
//					} else {
//						oltagString = oltagString.substring(0, oltagString.length() - 6);
////					oltagString = oltagString.replace("/ol[" + subOrderListNumber + "]/", "/");
//					}

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

		if ("true".equals(AddBugTask.ACFileAvailable)) {
			if ("start".equals(orderlist)) {
				orderlist = "stop";
			}
		}

		AddBugTask.oltagStringGlobal = oltagString;
	}

	public void removeExtraSpace() {
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

		int pExtratag = 1;
		int totalPTag = driver.findElements(By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p/br")).size();
		while (pExtratag <= totalPTag) {
			js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].remove()",
					driver.findElement(By.xpath("//*[@id='description']/div/div[3]/div[3]/div[2]/p/br")));
			pExtratag++;
		}

		Frame1.appendText("extra space remove done");
	}

	public void checkLoader() {
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
