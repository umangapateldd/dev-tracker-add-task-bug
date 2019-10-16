package test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import jxl.Cell;

public class Utilities {
	WebDriver driver;
	JavascriptExecutor js;
	String systemName;

	public void openBrowser() throws IOException {

		DesiredCapabilities chrome = DesiredCapabilities.chrome();

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--test-type");
		options.addArguments("--disable-popup-blocking");
		options.addArguments("--proxy-server='direct://'");
		options.addArguments("--proxy-bypass-list=*");
		options.setCapability(ChromeOptions.CAPABILITY, chrome);
		chrome.setJavascriptEnabled(true);
		systemName = System.getProperty("os.name").toLowerCase();
		if (systemName.contains("mac")) {
			System.setProperty("webdriver.chrome.driver", "chromedriver");
		} else {
			System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
		}

		driver = new ChromeDriver(options);
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
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

	public void macTextFormat(String imagePath, Cell descriptionType, String htmlTag) throws InterruptedException {
		int countTag = 0;
		System.out.println("htmlTag = " + htmlTag);
		if (htmlTag.contains("p[")) {
			// objective / reference
			countTag = Integer.parseInt(htmlTag.replace("p[", "").replace("]", ""));
		} else {
			// cos
			countTag = driver.findElements(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p")).size();
		}

		String cosString = descriptionType.getContents();

		String[] arrSplit = cosString.split("\n");
		Thread.sleep(1000);
		int attachmentCount = 0;
		boolean exists = false;
		boolean alreadybold = false;
		System.out.println("arrsplit length = " + arrSplit.length);
		for (int ar = 0; ar < arrSplit.length; ar++) {
			System.out.println("arrSplit[" + ar + "] = " + arrSplit[ar]);
			exists = false;
			File tempFile = new File(imagePath + arrSplit[ar]);
			String imageURL = "";
			if (arrSplit[ar].isEmpty()) {
				imageURL = arrSplit[ar];
			} else {
				exists = tempFile.exists();
				imageURL = imagePath + arrSplit[ar];
			}

			System.out.println("imageURL = " + imageURL);
			System.out.println("exists = " + exists);

			if (exists != true) {
				List<String> extractedUrls = extractUrls(arrSplit[ar]);
				System.out.println("extractedUrls.size() = " + extractedUrls.size());
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
				System.out.println("arrSplit[ar] = " + arrSplit[ar]);

				System.out.println("countTag = " + countTag);
				System.out.println(driver
						.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
						.getText());

				if (driver
						.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
						.getText().isEmpty()) {
					System.out.println("qqqqqqqqqqqqqqq");
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
							System.out.println("arrSplit[ar] = " + arrSplit[ar]);
							System.out.println("a data = " + Arrays.toString(a));
							System.out.println("startttttttt " + abc + "  ==  " + arrSplit[ar].charAt(a[abc]));

							if (arrSplit[ar].charAt(0) != '$') {
								System.out.println("1111111111111111111111111111");
								if (driver
										.findElement(By.xpath(
												"//*[@id=\"description\"]/div/div[3]/div[2]/div/div[2]/button[1]"))
										.getAttribute("class")
										.equals("note-btn btn btn-default btn-sm note-btn-bold active")) {
									System.out.println("oooooooooooooooooooooooo");

									driver.findElement(By.xpath(
											"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
											.sendKeys(Keys.COMMAND + "b");

									Thread.sleep(1000);
								} else {
									System.out.println("kkkkkkkkkkkkkkkkkkkkkkkkkk");
								}
							} else {
								System.out.println("22222222222222222222222222");
							}

							if (once == 0) {
								System.out.println("once = " + arrSplit[ar].substring(0, a[abc]));
								driver.findElement(By
										.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
										.sendKeys(arrSplit[ar].substring(0, a[abc]));
								once = 1;
							}

							if (arrSplit[ar].charAt(a[abc]) == '<') {
								System.out.println("< available");
								try {
									driver.findElement(
											By.xpath("//*[@id=\"description\"]/div/div[3]/div[2]/div/div[8]/button[1]"))
											.click();
								} catch (ElementClickInterceptedException e) {
									js = (JavascriptExecutor) driver;
									js.executeScript("window.scrollBy(0,-250)");
									driver.findElement(
											By.xpath("//*[@id=\"description\"]/div/div[3]/div[2]/div/div[8]/button[1]"))
											.click();
								}

								Thread.sleep(1500);
								driver.findElement(By.xpath(
										"//*[@id=\"description\"]/div/div[3]/div[5]/div[2]/div/div[2]/div[1]/input"))
										.sendKeys(arrSplit[ar].substring(a[abc] + 1, a[abc + 1]));

								driver.findElement(By.xpath(
										"//*[@id=\"description\"]/div/div[3]/div[5]/div[2]/div/div[2]/div[2]/input"))
										.sendKeys(arrSplit[ar].substring(a[abc + 1] + 1, a[abc + 2]));

								driver.findElement(
										By.xpath("//*[@id=\"description\"]/div/div[3]/div[5]/div[2]/div/div[3]/button"))
										.click();
								Thread.sleep(1000);
								driver.findElement(By
										.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
										.sendKeys(Keys.ARROW_RIGHT);

								if (driver.findElements(By.xpath(
										"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag + "]/b"))
										.size() > 0) {
									if (driver
											.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
													+ countTag + "]/b"))
											.getText().isEmpty()
											|| driver.findElements(
													By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
															+ countTag + "]/b/br"))
													.size() > 0) {
										System.out.println("b remove");
										js = (JavascriptExecutor) driver;
										js.executeScript("arguments[0].remove()",
												driver.findElement(
														By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
																+ countTag + "]/b")));
									}
								}

								if (abc + 3 < a.length) {
									driver.findElement(By.xpath(
											"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
											.sendKeys(arrSplit[ar].substring(a[abc + 2] + 1, a[abc + 3]));
								} else {
									driver.findElement(By.xpath(
											"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
											.sendKeys(arrSplit[ar].substring(a[abc + 2] + 1, arrSplit[ar].length()));
								}
								abc = abc + 3;
							} else if (arrSplit[ar].charAt(a[abc]) == '$') {
								System.out.println("wwwwwwwwww = " + arrSplit[ar].charAt(a[abc]));
								System.out.println("$ available abc = " + abc);
								int boldWithColor = 0;
								System.out.println("$ content " + arrSplit[ar].substring(a[abc] + 1, a[abc + 1]));

								JavascriptExecutor js = (JavascriptExecutor) driver;
								js.executeScript(
										"arguments[0].innerHTML = '"
												+ driver.findElement(
														By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
																+ countTag + "]"))
														.getAttribute("innerHTML").replace("<b><br></b>", "")
														.replace("<br>", "").replace("&nbsp;&nbsp;", "").trim()
												+ " <b>" + arrSplit[ar].substring(a[abc] + 1, a[abc + 1]) + "</b>'",
										driver.findElement(
												By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
														+ countTag + "]")));

								if (arrSplit[ar].charAt(a[abc + 1]) == '^') {
									if (driver
											.findElement(By.xpath(
													"//*[@id=\"description\"]/div/div[3]/div[2]/div/div[2]/button[1]"))
											.getAttribute("class")
											.equals("note-btn btn btn-default btn-sm note-btn-bold active")) {
										alreadybold = true;
									} else {
										alreadybold = false;
									}
									System.out.println("boldWithColor abc = " + abc);
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
									Thread.sleep(1500);
									// Click Starting Color Code
									System.out.println("get color code");
									System.out.println("get color code abc = " + abc);

									if (driver.findElements(By.xpath("//button[@style='background-color:#"
											+ arrSplit[ar].substring(a[abc + 1] + 1, a[abc + 2])
											+ "' and @data-event='foreColor']")).size() > 0) {
										driver.findElement(By.xpath("//button[@style='background-color:#"
												+ arrSplit[ar].substring(a[abc + 1] + 1, a[abc + 2])
												+ "' and @data-event='foreColor']")).click();
									} else {
										System.out.println("Start color code is not proper");
									}

									if (alreadybold == true) {
										if (driver.findElement(By.xpath(
												"//*[@id=\"description\"]/div/div[3]/div[2]/div/div[2]/button[1]"))
												.getAttribute("class")
												.equals("note-btn btn btn-default btn-sm note-btn-bold active")) {
										} else {
											driver.findElement(
													By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
															+ countTag + "]"))
													.sendKeys(Keys.COMMAND + "b");
											Thread.sleep(1000);
										}
										alreadybold = false;
									}

									System.out.println("get string");
									driver.findElement(By.xpath(
											"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
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
									System.out.println("get end color code");
									System.out.println("get end color code abc = " + abc);

									if (driver.findElements(By.xpath("//button[@style='background-color:#"
											+ arrSplit[ar].substring(a[abc + 3] + 1, a[abc + 4])
											+ "' and @data-event='foreColor']")).size() > 0) {
										driver.findElement(By.xpath("//button[@style='background-color:#"
												+ arrSplit[ar].substring(a[abc + 3] + 1, a[abc + 4])
												+ "' and @data-event='foreColor']")).click();
									} else {
										System.out.println("End color code is not proper");
									}

									System.out.println("lllllllllllll abc = " + abc);
									if (abc + 4 < a.length) {
										System.out.println("if ^ content abc = " + abc);
										System.out.println("if ^ content a length= " + a.length);
										driver.findElement(
												By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
														+ countTag + "]"))
												.sendKeys(arrSplit[ar].substring(a[abc + 4] + 1, a[abc + 5]));
									} else {
										System.out.println("else ^ content abc = " + abc);
										System.out.println("else ^ content a length= " + a.length);
										driver.findElement(
												By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
														+ countTag + "]"))
												.sendKeys(
														arrSplit[ar].substring(a[abc + 4] + 1, arrSplit[ar].length()));
									}
									abc = abc + 4;
								}

								Thread.sleep(1000);

								if (abc + 2 < a.length) {
									System.out.println("if abc + 2 < a.length = "
											+ arrSplit[ar].substring(a[abc + 1] + 1, a[abc + 2]));
									System.out.println("string = " + driver
											.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
													+ countTag + "]"))
											.getAttribute("innerHTML").replace("<b><br></b>", "").replace("<br>", ""));
									js = (JavascriptExecutor) driver;
									js.executeScript(
											"arguments[0].innerHTML = '"
													+ driver.findElement(By.xpath(
															"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
																	+ countTag + "]"))
															.getAttribute("innerHTML").replace("<b><br></b>", "")
															.replace("<br>", "").replace("&nbsp;&nbsp;", "").trim()
													+ arrSplit[ar].substring(a[abc + 1] + 1, a[abc + 2]) + "'",
											driver.findElement(
													By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
															+ countTag + "]")));
									driver.findElement(By.xpath(
											"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
											.sendKeys(Keys.COMMAND + "" + Keys.ARROW_RIGHT);
								} else {
									System.out.println("else abc + 2 < a.length = "
											+ arrSplit[ar].substring(a[abc + 1] + 1, arrSplit[ar].length()));
									System.out.println("string = " + driver
											.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
													+ countTag + "]"))
											.getAttribute("innerHTML").replace("<b><br></b>", "").replace("<br>", ""));

									js = (JavascriptExecutor) driver;
									js.executeScript(
											"arguments[0].innerHTML = '"
													+ driver.findElement(By.xpath(
															"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
																	+ countTag + "]"))
															.getAttribute("innerHTML").replace("<b><br></b>", "")
															.replace("<br>", "").replace("&nbsp;&nbsp;", "").trim()
													+ arrSplit[ar].substring(a[abc + 1] + 1, arrSplit[ar].length())
													+ "'",
											driver.findElement(
													By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
															+ countTag + "]")));

									driver.findElement(By.xpath(
											"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
											.sendKeys(Keys.COMMAND + "" + Keys.ARROW_RIGHT);
								}
								abc = abc + 2;
								System.out.println("abc increase = " + abc);
							} else if (arrSplit[ar].charAt(a[abc]) == '~') {
								System.out.println(Arrays.toString(a));
								System.out.println("~~~~~~~~~~ = " + arrSplit[ar].charAt(a[abc]));
								System.out.println("~ available abc = " + abc);

								System.out.println(arrSplit[ar]);
								String username = arrSplit[ar].substring(a[abc] + 1, a[abc + 1]);
								System.out.println("username = " + username);
								//
								System.out.println("data = " + username.substring(0, (username.indexOf(" "))));

								driver.findElement(By
										.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
										.sendKeys("@" + username.substring(0, (username.indexOf(" "))));

								if (driver
										.findElement(By.xpath(
												"//body//following::div[1][contains(@class,'note-hint-popover')]"))
										.getAttribute("style").contains("display: block")) {
									System.out.println("bbbbbbbbbbbbbbbb");
									int userCount = driver
											.findElements(By.xpath("//div[contains(@class,'note-hint-item')]")).size();
									System.out.println("userCount = " + userCount);
									if (userCount == 1) {
										driver.findElement(
												By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
														+ countTag + "]"))
												.sendKeys(Keys.ENTER);

									} else if (userCount > 1) {

										for (int cnt = 1; cnt <= userCount; cnt++) {
											System.out.println("6666666666");
											if (username.toLowerCase()
													.equals(driver
															.findElement(By.xpath("//div[" + cnt
																	+ "][contains(@class,'note-hint-item')]/strong"))
															.getText().toLowerCase())) {
												System.out.println("77777776");
												driver.findElement(
														By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
																+ countTag + "]"))
														.sendKeys(Keys.ENTER);

											} else {
												System.out.println("888888888888");
												driver.findElement(
														By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
																+ countTag + "]"))
														.sendKeys(Keys.ARROW_DOWN);
											}
										}
									} else {
										System.out.println("Given username is not available");
									}
								} else {
									driver.findElement(By.xpath(
											"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
											.sendKeys(username.substring(username.indexOf(" "), username.length()));
								}
								System.out.println("zzzzzzzzzzzzzzzzz");

								if (abc + 2 < a.length) {
									System.out.println("if abc length ~");
									System.out.println("if abc + 2 < a.length = "
											+ arrSplit[ar].substring(a[abc + 1] + 1, a[abc + 2]));

									System.out.println("string = " + driver
											.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
													+ countTag + "]"))
											.getAttribute("innerHTML").replace("<b><br></b>", "").replace("<br>", ""));
									js = (JavascriptExecutor) driver;
									js.executeScript(
											"arguments[0].innerHTML = '"
													+ driver.findElement(By.xpath(
															"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
																	+ countTag + "]"))
															.getAttribute("innerHTML").replace("<b><br></b>", "")
															.replace("<br>", "").replace("&nbsp;&nbsp;", "").trim()
													+ arrSplit[ar].substring(a[abc + 1] + 1, a[abc + 2]) + "'",
											driver.findElement(
													By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
															+ countTag + "]")));
									driver.findElement(By.xpath(
											"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
											.sendKeys(Keys.COMMAND + "" + Keys.ARROW_RIGHT);
								} else {
									System.out.println("else abc length ~");
									System.out.println("else abc + 2 < a.length = "
											+ arrSplit[ar].substring(a[abc + 1] + 1, arrSplit[ar].length()));
									System.out.println("string = " + driver
											.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
													+ countTag + "]"))
											.getAttribute("innerHTML").replace("<b><br></b>", "").replace("<br>", ""));
									js = (JavascriptExecutor) driver;
									js.executeScript(
											"arguments[0].innerHTML = '"
													+ driver.findElement(By.xpath(
															"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
																	+ countTag + "]"))
															.getAttribute("innerHTML").replace("<b><br></b>", "")
															.replace("<br>", "").replace("&nbsp;&nbsp;", "").trim()
													+ arrSplit[ar].substring(a[abc + 1] + 1, arrSplit[ar].length())
													+ "'",
											driver.findElement(
													By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p["
															+ countTag + "]")));
									driver.findElement(By.xpath(
											"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
											.sendKeys(Keys.COMMAND + "" + Keys.ARROW_RIGHT);
								}
								abc = abc + 2;
							} else {
								System.out.println("str  = " + arrSplit[ar].charAt(a[abc]));
								System.out.println("no available");
								if (arrSplit[ar].charAt(a[abc]) == '\n') {
									System.out.println("n available");
									enter = true;
									driver.findElement(By.xpath(
											"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
											.sendKeys(Keys.RETURN);
								} else {
									System.out.println("n not available");
									driver.findElement(By.xpath(
											"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
											.sendKeys(arrSplit[ar].substring(a[abc], a[abc + 1]));
								}
								System.out.println("a length before abc = " + a.length);
								System.out.println("abc before = " + abc);
								abc++;
								System.out.println("a length after abc = " + a.length);
								System.out.println("abc after = " + abc);
							}
						}
					} else {
						System.out.println("123123123123123 = " + arrSplit[ar]);
						JavascriptExecutor js = (JavascriptExecutor) driver;
						js.executeScript("arguments[0].innerHTML = '" + arrSplit[ar] + "'", driver.findElement(
								By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag + "]")));
					}
					if (enter == true) {
						enter = false;
					} else {
						System.out.println("enter false");
						driver.findElement(
								By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
								.sendKeys(Keys.ENTER);
					}
					Thread.sleep(3000);
				}
			} else {
				attachmentCount++;
				System.out.println("attachment is available");
				try {
					driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[2]/div/div[8]/button[2]"))
							.click();
				} catch (ElementClickInterceptedException e) {
					System.out.println("catch");
					js = (JavascriptExecutor) driver;
					js.executeScript("window.scrollBy(0,-250)");
					driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[2]/div/div[8]/button[2]"))
							.click();
				}
				Thread.sleep(1500);

				driver.findElement(By.name("files")).sendKeys(imageURL);

				int tmp = 0;
				long t = System.currentTimeMillis();
				long end = t + 100000;

				do {
					if (System.currentTimeMillis() > end) {
						System.out.println("image upload timeout");
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
							System.out.println("File is attached");
							tmp = 1;
							if (arrSplit.length > 1) {
								driver.findElement(By
										.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + countTag + "]"))
										.sendKeys(Keys.ENTER);
							}
						}
					} else {
						System.out.println("File is still not attached");
						tmp = 0;
					}
				} while (tmp == 0);
			}
			countTag++;
		}
	}

	public void removeExtraSpace() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		System.out.println(dtf.format(now));
		int pTag = 2;
		int totalPTag = driver.findElements(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p")).size();
		System.out.println("totalPTag = " + totalPTag);
		while (pTag <= totalPTag) {
			if (driver.findElements(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + pTag + "]"))
					.size() > 0) {
				System.out.println("pTag text = " + driver
						.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + pTag + "]"))
						.getText());

				if (pTag == 2 && driver
						.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + pTag + "]"))
						.getText().isEmpty()) {
					System.out.println("p tag " + pTag + " and empty");
					pTag = pTag + 2;
				} else {
					System.out.println("else p tag " + pTag + " and not empty");
					now = LocalDateTime.now();
					System.out.println(dtf.format(now));

					if (!driver
							.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + pTag + "]"))
							.getText().isEmpty()
							|| driver
									.findElements(By.xpath(
											"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + pTag + "]/img"))
									.size() > 0) {
						System.out.println("p tag increase");
						pTag++;
					} else {
						System.out.println("text empty");
						if (driver
								.findElements(By.xpath(
										"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + (pTag + 1) + "]"))
								.size() > 0
								&& driver.findElement(By.xpath(
										"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + (pTag + 1) + "]"))
										.getText().equals("References")) {
							System.out.println("get ref text");
							js = (JavascriptExecutor) driver;
							js.executeScript("arguments[0].remove()", driver.findElement(
									By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + pTag + "]")));
						} else if (driver
								.findElements(By.xpath(
										"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + (pTag + 1) + "]"))
								.size() > 0
								&& driver.findElement(By.xpath(
										"//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + (pTag + 1) + "]"))
										.getText().equals("Conditions of Satisfaction")) {
							System.out.println("get cos text");
							js = (JavascriptExecutor) driver;
							js.executeScript("arguments[0].remove()", driver.findElement(
									By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]/p[" + pTag + "]")));
						} else {
							pTag++;
						}
					}
				}
			} else {
				break;
			}
		}
		System.out.println("extra space remove done");
		dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		now = LocalDateTime.now();
		System.out.println(dtf.format(now));
	}

	public void textFormat(String imagePath, Cell descriptionType) throws InterruptedException {
		String cosString = descriptionType.getContents();

		String[] arrSplit = cosString.split("\n");
		Thread.sleep(1000);
		int attachmentCount = 0;
		System.out.println("arrSplit = " + arrSplit);
		boolean exists = false;
		boolean alreadybold = false;
		boolean boldContentInColor = false;
		for (int ar = 0; ar < arrSplit.length; ar++) {
			exists = false;
			File tempFile = new File(imagePath + arrSplit[ar]);
			String imageURL = "";
			if (arrSplit[ar].isEmpty()) {
				imageURL = arrSplit[ar];
			} else {
				exists = tempFile.exists();
				imageURL = imagePath + arrSplit[ar];
			}

			System.out.println("imageURL = " + imageURL);
			System.out.println("exists = " + exists);

			if (exists != true) {
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
						System.out.println("arrSplit[ar] = " + arrSplit[ar]);
						System.out.println("a data = " + Arrays.toString(a));
						System.out.println("startttttttt " + abc + "  ==  " + arrSplit[ar].charAt(a[abc]));

						if (arrSplit[ar].charAt(0) != '$') {
							System.out.println("1111111111111111111111111111");
							if (driver
									.findElement(
											By.xpath("//*[@id=\"description\"]/div/div[3]/div[2]/div/div[2]/button[1]"))
									.getAttribute("class")
									.equals("note-btn btn btn-default btn-sm note-btn-bold active")) {
								System.out.println("oooooooooooooooooooooooo");

								driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
										.sendKeys(Keys.CONTROL + "b");

								Thread.sleep(1000);
							} else {
								System.out.println("kkkkkkkkkkkkkkkkkkkkkkkkkk");
							}
						} else {
							System.out.println("22222222222222222222222222");
						}

						if (arrSplit[ar].charAt(0) != '^') {
							try {
								if (driver.findElements(By.xpath("//button[@aria-label='Recent Color']")).size() > 0) {
									if (driver.findElement(By.xpath("//button[@aria-label='Recent Color']"))
											.getAttribute("data-forecolor") == null
											|| driver.findElement(By.xpath("//button[@aria-label='Recent Color']"))
													.getAttribute("data-forecolor").equals("000000")) {

									} else {
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

										if (driver.findElements(By.xpath(
												"//button[@style='background-color:#000000' and @data-event='foreColor']"))
												.size() > 0) {
											driver.findElement(By.xpath(
													"//button[@style='background-color:#000000' and @data-event='foreColor']"))
													.click();
										}
									}
								}
							} catch (ElementClickInterceptedException e) {
								js = (JavascriptExecutor) driver;
								js.executeScript("window.scrollBy(0,-250)");
								if (driver.findElements(By.xpath("//button[@aria-label='Recent Color'")).size() > 0) {
									if (driver.findElement(By.xpath("//button[@aria-label='Recent Color'"))
											.getAttribute("data-forecolor").equals("000000")) {

									} else {
										try {
											driver.findElement(By.xpath(
													"//*[@id=\"description\"]/div/div[3]/div[2]/div/div[4]/div/button[2]"))
													.click();
										} catch (ElementClickInterceptedException e1) {
											js = (JavascriptExecutor) driver;
											js.executeScript("window.scrollBy(0,-250)");
											driver.findElement(By.xpath(
													"//*[@id=\"description\"]/div/div[3]/div[2]/div/div[4]/div/button[2]"))
													.click();
										}

										if (driver.findElements(By.xpath(
												"//button[@style='background-color:#000000' and @data-event='foreColor']"))
												.size() > 0) {
											driver.findElement(By.xpath(
													"//button[@style='background-color:#000000' and @data-event='foreColor']"))
													.click();
										}
									}
								}
							}

						}

						if (once == 0) {
							driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
									.sendKeys(arrSplit[ar].substring(0, a[abc]));
							once = 1;
						}

						if (arrSplit[ar].charAt(a[abc]) == '<') {
							System.out.println("< available");
							try {
								driver.findElement(
										By.xpath("//*[@id=\"description\"]/div/div[3]/div[2]/div/div[8]/button[1]"))
										.click();
							} catch (ElementClickInterceptedException e) {
								js = (JavascriptExecutor) driver;
								js.executeScript("window.scrollBy(0,-250)");
								driver.findElement(
										By.xpath("//*[@id=\"description\"]/div/div[3]/div[2]/div/div[8]/button[1]"))
										.click();
							}

							Thread.sleep(1500);
							driver.findElement(By
									.xpath("//*[@id=\"description\"]/div/div[3]/div[5]/div[2]/div/div[2]/div[1]/input"))
									.sendKeys(arrSplit[ar].substring(a[abc] + 1, a[abc + 1]));

							driver.findElement(By
									.xpath("//*[@id=\"description\"]/div/div[3]/div[5]/div[2]/div/div[2]/div[2]/input"))
									.sendKeys(arrSplit[ar].substring(a[abc + 1] + 1, a[abc + 2]));

							driver.findElement(
									By.xpath("//*[@id=\"description\"]/div/div[3]/div[5]/div[2]/div/div[3]/button"))
									.click();
							Thread.sleep(1000);
							driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
									.sendKeys(Keys.ARROW_RIGHT);

							if (abc + 3 < a.length) {
								driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
										.sendKeys(arrSplit[ar].substring(a[abc + 2] + 1, a[abc + 3]));
							} else {
								driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
										.sendKeys(arrSplit[ar].substring(a[abc + 2] + 1, arrSplit[ar].length()));
							}
							abc = abc + 3;
						} else if (arrSplit[ar].charAt(a[abc]) == '$') {
							System.out.println("wwwwwwwwww = " + arrSplit[ar].charAt(a[abc]));
							System.out.println("$ available abc = " + abc);
							int boldWithColor = 0;
							if (driver
									.findElement(
											By.xpath("//*[@id=\"description\"]/div/div[3]/div[2]/div/div[2]/button[1]"))
									.getAttribute("class")
									.equals("note-btn btn btn-default btn-sm note-btn-bold active")) {
							} else {
								driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
										.sendKeys(Keys.CONTROL + "b");
								Thread.sleep(1000);
							}

							driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
									.sendKeys(arrSplit[ar].substring(a[abc] + 1, a[abc + 1]));

							if (arrSplit[ar].charAt(a[abc + 1]) == '^') {
								if (driver
										.findElement(By.xpath(
												"//*[@id=\"description\"]/div/div[3]/div[2]/div/div[2]/button[1]"))
										.getAttribute("class")
										.equals("note-btn btn btn-default btn-sm note-btn-bold active")) {
									alreadybold = true;
								} else {
									alreadybold = false;
								}
								System.out.println("boldWithColor abc = " + abc);
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
								Thread.sleep(1500);
								// Click Starting Color Code
								System.out.println("get color code");
								System.out.println("get color code abc = " + abc);

								if (driver.findElements(By.xpath("//button[@style='background-color:#"
										+ arrSplit[ar].substring(a[abc + 1] + 1, a[abc + 2])
										+ "' and @data-event='foreColor']")).size() > 0) {
									driver.findElement(By.xpath("//button[@style='background-color:#"
											+ arrSplit[ar].substring(a[abc + 1] + 1, a[abc + 2])
											+ "' and @data-event='foreColor']")).click();
								} else {
									System.out.println("Start color code is not proper");
								}

								if (alreadybold == true) {
									if (driver
											.findElement(By.xpath(
													"//*[@id=\"description\"]/div/div[3]/div[2]/div/div[2]/button[1]"))
											.getAttribute("class")
											.equals("note-btn btn btn-default btn-sm note-btn-bold active")) {
									} else {
										driver.findElement(
												By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
												.sendKeys(Keys.CONTROL + "b");
										Thread.sleep(1000);
									}
									alreadybold = false;
								}

								System.out.println("get string");
								driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
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
								System.out.println("get end color code");
								System.out.println("get end color code abc = " + abc);

								if (driver.findElements(By.xpath("//button[@style='background-color:#"
										+ arrSplit[ar].substring(a[abc + 3] + 1, a[abc + 4])
										+ "' and @data-event='foreColor']")).size() > 0) {
									driver.findElement(By.xpath("//button[@style='background-color:#"
											+ arrSplit[ar].substring(a[abc + 3] + 1, a[abc + 4])
											+ "' and @data-event='foreColor']")).click();
								} else {
									System.out.println("End color code is not proper");
								}

								System.out.println("lllllllllllll abc = " + abc);
								if (abc + 4 < a.length) {
									System.out.println("if ^ content abc = " + abc);
									System.out.println("if ^ content a length= " + a.length);
									driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
											.sendKeys(arrSplit[ar].substring(a[abc + 4] + 1, a[abc + 5]));
								} else {
									System.out.println("else ^ content abc = " + abc);
									System.out.println("else ^ content a length= " + a.length);
									driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
											.sendKeys(arrSplit[ar].substring(a[abc + 4] + 1, arrSplit[ar].length()));
								}
								abc = abc + 4;
							}

							driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
									.sendKeys(Keys.CONTROL + "b");

							Thread.sleep(1000);

							if (abc + 2 < a.length) {
								driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
										.sendKeys(arrSplit[ar].substring(a[abc + 1] + 1, a[abc + 2]));
							} else {
								driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
										.sendKeys(arrSplit[ar].substring(a[abc + 1] + 1, arrSplit[ar].length()));
							}
							abc = abc + 2;
							System.out.println("abc increase = " + abc);
						} else if (arrSplit[ar].charAt(a[abc]) == '^') {
							System.out.println("^ available");
							System.out.println("a length in ^ = " + a.length);
							System.out.println("abc = " + abc);

							if (boldContentInColor == true) {

							} else {
								if (driver
										.findElement(By.xpath(
												"//*[@id=\"description\"]/div/div[3]/div[2]/div/div[2]/button[1]"))
										.getAttribute("class")
										.equals("note-btn btn btn-default btn-sm note-btn-bold active")) {
									alreadybold = true;
								} else {
									alreadybold = false;
								}
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
								Thread.sleep(1500);
								// Click Starting Color Code
								System.out.println("get color code");

								if (driver.findElements(By.xpath("//button[@style='background-color:#"
										+ arrSplit[ar].substring(a[abc] + 1, a[abc + 1])
										+ "' and @data-event='foreColor']")).size() > 0) {
									driver.findElement(By.xpath("//button[@style='background-color:#"
											+ arrSplit[ar].substring(a[abc] + 1, a[abc + 1])
											+ "' and @data-event='foreColor']")).click();
								} else {
									System.out.println("Start color code is not proper");
								}

								if (alreadybold == true) {
									if (driver
											.findElement(By.xpath(
													"//*[@id=\"description\"]/div/div[3]/div[2]/div/div[2]/button[1]"))
											.getAttribute("class")
											.equals("note-btn btn btn-default btn-sm note-btn-bold active")) {
									} else {
										driver.findElement(
												By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
												.sendKeys(Keys.CONTROL + "b");
										Thread.sleep(1000);
									}
									alreadybold = false;
								}
								System.out.println("qqqqqqqqqqq");
								System.out.println("get string");
								driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
										.sendKeys(arrSplit[ar].substring(a[abc + 1] + 1, a[abc + 2]));
							}

							if (boldContentInColor == false && arrSplit[ar].charAt(a[abc + 2]) == '$') {
								boldContentInColor = true;
								abc = abc + 2;
								System.out.println("xxxxxxxx");
								if (driver
										.findElement(By.xpath(
												"//*[@id=\"description\"]/div/div[3]/div[2]/div/div[2]/button[1]"))
										.getAttribute("class")
										.equals("note-btn btn btn-default btn-sm note-btn-bold active")) {
								} else {
									driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
											.sendKeys(Keys.CONTROL + "b");
									Thread.sleep(1000);
								}

								driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
										.sendKeys(arrSplit[ar].substring(a[abc] + 1, a[abc + 1]));

								driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
										.sendKeys(Keys.CONTROL + "b");

								Thread.sleep(1000);

								if (abc + 2 < a.length) {
									driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
											.sendKeys(arrSplit[ar].substring(a[abc + 1] + 1, a[abc + 2]));
								} else {
									driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
											.sendKeys(arrSplit[ar].substring(a[abc + 1] + 1, arrSplit[ar].length()));
								}
								abc = abc + 2;
							} else {
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
								System.out.println("get end color code");

								if (boldContentInColor == true) {
									System.out.println("cccccccccccccccccccccccc");
									if (driver.findElements(By.xpath("//button[@style='background-color:#"
											+ arrSplit[ar].substring(a[abc] + 1, a[abc + 1])
											+ "' and @data-event='foreColor']")).size() > 0) {
										driver.findElement(By.xpath("//button[@style='background-color:#"
												+ arrSplit[ar].substring(a[abc] + 1, a[abc + 1])
												+ "' and @data-event='foreColor']")).click();
									} else {
										System.out.println("End color code is not proper");
									}

									if (abc + 2 < a.length) {
										System.out.println("if ^ content abc = " + abc);
										System.out.println("if ^ content a length= " + a.length);
										driver.findElement(
												By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
												.sendKeys(arrSplit[ar].substring(a[abc + 1] + 1, a[abc + 2]));
									} else {
										if (driver.findElement(By.xpath(
												"//*[@id=\"description\"]/div/div[3]/div[2]/div/div[2]/button[1]"))
												.getAttribute("class")
												.equals("note-btn btn btn-default btn-sm note-btn-bold active")) {
											driver.findElement(
													By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
													.sendKeys(Keys.CONTROL + "b");

											Thread.sleep(1000);
										}
										System.out.println("else ^ content abc = " + abc);
										System.out.println("else ^ content a length= " + a.length);
										driver.findElement(
												By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]")).sendKeys(
														arrSplit[ar].substring(a[abc + 1] + 1, arrSplit[ar].length()));
									}
									abc = abc + 2;
								} else {
									if (driver.findElements(By.xpath("//button[@style='background-color:#"
											+ arrSplit[ar].substring(a[abc + 2] + 1, a[abc + 3])
											+ "' and @data-event='foreColor']")).size() > 0) {
										driver.findElement(By.xpath("//button[@style='background-color:#"
												+ arrSplit[ar].substring(a[abc + 2] + 1, a[abc + 3])
												+ "' and @data-event='foreColor']")).click();
									} else {
										System.out.println("End color code is not proper");
									}

									if (abc + 4 < a.length) {
										System.out.println("if ^ content abc = " + abc);
										System.out.println("if ^ content a length= " + a.length);
										driver.findElement(
												By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
												.sendKeys(arrSplit[ar].substring(a[abc + 3] + 1, a[abc + 4]));
									} else {
										System.out.println("else ^ content abc = " + abc);
										System.out.println("else ^ content a length= " + a.length);
										driver.findElement(
												By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]")).sendKeys(
														arrSplit[ar].substring(a[abc + 3] + 1, arrSplit[ar].length()));
									}
									abc = abc + 4;
								}
							}
						} else if (arrSplit[ar].charAt(a[abc]) == '~') {
							System.out.println(Arrays.toString(a));
							System.out.println("wwwwwwwwww = " + arrSplit[ar].charAt(a[abc]));
							System.out.println("~ available abc = " + abc);

							System.out.println(arrSplit[ar]);
							String username = arrSplit[ar].substring(a[abc] + 1, a[abc + 1]);
							System.out.println("username = " + username);
//
							System.out.println("data = " + username.substring(0, (username.indexOf(" "))));

							driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
									.sendKeys("@" + username.substring(0, (username.indexOf(" "))));

							if (driver
									.findElement(
											By.xpath("//body//following::div[1][contains(@class,'note-hint-popover')]"))
									.getAttribute("style").contains("display: block")) {
								System.out.println("bbbbbbbbbbbbbbbb");
								int userCount = driver
										.findElements(By.xpath("//div[contains(@class,'note-hint-item')]")).size();
								System.out.println("userCount = " + userCount);
								if (userCount == 1) {
									driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
											.sendKeys(Keys.ENTER);

								} else if (userCount > 1) {

									for (int cnt = 1; cnt <= userCount; cnt++) {
										System.out.println("6666666666");
										if (username.toLowerCase().equals(driver
												.findElement(By.xpath(
														"//div[" + cnt + "][contains(@class,'note-hint-item')]/strong"))
												.getText().toLowerCase())) {
											System.out.println("77777776");
											driver.findElement(
													By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
													.sendKeys(Keys.ENTER);

										} else {
											System.out.println("888888888888");
											driver.findElement(
													By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
													.sendKeys(Keys.ARROW_DOWN);
										}
									}
								} else {
									System.out.println("Given username is not available");
								}
							} else {
								driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
										.sendKeys(username.substring(username.indexOf(" "), username.length()));
							}
							System.out.println("zzzzzzzzzzzzzzzzz");

							if (abc + 2 < a.length) {
								driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
										.sendKeys(arrSplit[ar].substring(a[abc + 1] + 1, a[abc + 2]));
							} else {
								driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
										.sendKeys(arrSplit[ar].substring(a[abc + 1] + 1, arrSplit[ar].length()));
							}
							abc = abc + 2;
						} else {
							System.out.println("str  = " + arrSplit[ar].charAt(a[abc]));
							System.out.println("no available");
							if (arrSplit[ar].charAt(a[abc]) == '\n') {
								System.out.println("n available");
								enter = true;
								driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
										.sendKeys(Keys.ENTER);
							} else {
								System.out.println("n not available");
								driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
										.sendKeys(arrSplit[ar].substring(a[abc], a[abc + 1]));
							}
							System.out.println("a length before abc = " + a.length);
							System.out.println("abc before = " + abc);
							abc++;
							System.out.println("a length after abc = " + a.length);
							System.out.println("abc after = " + abc);
						}
					}
				} else {
					System.out.println("123123123123123 = " + arrSplit[ar]);
					if (driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[2]/div/div[2]/button[1]"))
							.getAttribute("class").equals("note-btn btn btn-default btn-sm note-btn-bold active")) {
						driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
								.sendKeys(Keys.CONTROL + "b");

						Thread.sleep(1000);
					}
					driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
							.sendKeys(arrSplit[ar]);
				}
				if (enter == true) {
					enter = false;
				} else {
					driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
							.sendKeys(Keys.ENTER);
				}
				Thread.sleep(3000);
			} else {
				attachmentCount++;
				System.out.println("attachment is available");
				try {
					driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[2]/div/div[8]/button[2]"))
							.click();
				} catch (ElementClickInterceptedException e) {
					System.out.println("catch");
					js = (JavascriptExecutor) driver;
					js.executeScript("window.scrollBy(0,-250)");
					driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[2]/div/div[8]/button[2]"))
							.click();
				}
				Thread.sleep(1500);

				driver.findElement(By.name("files")).sendKeys(imageURL);

				int tmp = 0;
				long t = System.currentTimeMillis();
				long end = t + 100000;

				do {
					if (System.currentTimeMillis() > end) {
						System.out.println("image upload timeout");
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
							System.out.println("File is attached");
							tmp = 1;
							if (arrSplit.length > 1) {
								driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]"))
										.sendKeys(Keys.ENTER);
							}
						}
					} else {
						System.out.println("File is still not attached");
						tmp = 0;
					}
				} while (tmp == 0);
			}
		}
		if (attachmentCount > 1) {
			driver.findElement(By.xpath("//*[@id=\"description\"]/div/div[3]/div[3]/div[2]")).sendKeys(Keys.DELETE);
		}
	}
}
