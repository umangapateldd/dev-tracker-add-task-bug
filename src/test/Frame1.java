package test;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileSystemView;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.TestNG;

public class Frame1 extends Utilities {

	private JFrame frame;
	static JTextArea textArea;
	JFileChooser jfile;
	JFileChooser jdir;
	static String filePath = "";
	static String imageDirPath = "";
	File dirPath = null;
	static WebDriver driverFrame;
	static String rdbVal;
	static boolean stop = false;
	static JButton btnFileUpload;
	static JButton btnSetImageFolder;
	static JTextField txtFileUpload;
	static JTextField txtSetImageFolder;
	static JButton btnExecuteScript;
	static JButton btnStopExecution;
	static JRadioButton rdbChromeYes;
	static JRadioButton rdbChromeNo;
	static JRadioButton rdbattachmentFolderFromExcelYes;
	static JRadioButton rdbattachmentFolderFromExcelNo;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Frame1 window = new Frame1();
					window.frame.setVisible(true);
				} catch (Exception e) {
					alertMessage(e.toString());
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Frame1() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 731, 468);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel jp = new JPanel();
		frame.getContentPane().setLayout(null);
		textArea = new JTextArea();

		txtFileUpload = new JTextField("Please enter xls file path");
		txtSetImageFolder = new JTextField("Please enter appropriate folder");
		btnExecuteScript = new JButton("Execute Script");
		btnStopExecution = new JButton("Stop Execution");
		btnExecuteScript.setEnabled(false);
		btnFileUpload = new JButton("Upload xls file");
		btnSetImageFolder = new JButton("Select image path");
		JLabel lblfileUpload = new JLabel();
		JLabel lblImageFolder = new JLabel();
		JLabel orlabel1 = new JLabel("OR");
		JLabel orlabel2 = new JLabel("OR");
		JLabel lblBrowserDisplay = new JLabel("Browser Display ?");
		rdbChromeYes = new JRadioButton();
		rdbChromeNo = new JRadioButton();
		ButtonGroup G1 = new ButtonGroup();
		ButtonGroup G2 = new ButtonGroup();

		jfile = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		jdir = new JFileChooser();
		jdir.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		// x,y,width,height
		btnFileUpload.setBounds(10, 11, 140, 41);

		lblfileUpload.setBounds(460, 15, 209, 28);
		lblImageFolder.setBounds(460, 116, 209, 28);

		btnSetImageFolder.setBounds(10, 109, 140, 41);
		btnExecuteScript.setBounds(10, 157, 140, 41);
		btnStopExecution.setBounds(193, 152, 125, 46);

		txtFileUpload.setBounds(193, 17, 260, 28);
		txtSetImageFolder.setBounds(193, 115, 260, 28);

		orlabel1.setBounds(163, 17, 30, 28);
		orlabel2.setBounds(163, 115, 30, 28);

		rdbChromeYes.setBounds(550, 163, 52, 28);
		rdbChromeNo.setBounds(617, 163, 52, 28);

		lblBrowserDisplay.setBounds(434, 163, 110, 28);

		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setBounds(1, 1, 601, 270);
		frame.getContentPane().add(textArea);

		frame.getContentPane().add(btnExecuteScript);
		frame.getContentPane().add(btnStopExecution);
		frame.getContentPane().add(lblfileUpload);
		frame.getContentPane().add(btnFileUpload);
		frame.getContentPane().add(btnSetImageFolder);
		frame.getContentPane().add(lblImageFolder);
		frame.getContentPane().add(txtFileUpload);
		frame.getContentPane().add(txtSetImageFolder);
		frame.getContentPane().add(orlabel1);
		frame.getContentPane().add(orlabel2);
		frame.getContentPane().add(rdbChromeYes);
		frame.getContentPane().add(rdbChromeNo);
		frame.getContentPane().add(lblBrowserDisplay);

		rdbChromeYes.setText("Yes");
		rdbChromeNo.setText("No");
		rdbChromeYes.setSelected(true);

		G1.add(rdbChromeYes);
		G1.add(rdbChromeNo);

		JScrollPane sampleScrollPane = new JScrollPane(textArea);
		sampleScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sampleScrollPane.setBounds(10, 209, 443, 214);
		frame.getContentPane().add(sampleScrollPane);

		JLabel attachmentFolderFromExcel = new JLabel("Attachments folder from excel ?");
		attachmentFolderFromExcel.setBounds(10, 70, 200, 28);
		frame.getContentPane().add(attachmentFolderFromExcel);

		rdbattachmentFolderFromExcelYes = new JRadioButton();
		rdbattachmentFolderFromExcelYes.setText("Yes");
		rdbattachmentFolderFromExcelYes.setBounds(216, 70, 52, 28);
		frame.getContentPane().add(rdbattachmentFolderFromExcelYes);

		rdbattachmentFolderFromExcelNo = new JRadioButton();
		rdbattachmentFolderFromExcelNo.setText("No");
		rdbattachmentFolderFromExcelNo.setSelected(true);
		rdbattachmentFolderFromExcelNo.setBounds(283, 70, 52, 28);
		frame.getContentPane().add(rdbattachmentFolderFromExcelNo);

		G2.add(rdbattachmentFolderFromExcelYes);
		G2.add(rdbattachmentFolderFromExcelNo);

		AddBugTask test = new AddBugTask();
		btnExecuteScript.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							if (filePath.isEmpty() && rdbattachmentFolderFromExcelNo.isSelected()) {
								JOptionPane.showMessageDialog(null, "Please select excel file");
							} else if (lblImageFolder.getText().isEmpty()
									&& rdbattachmentFolderFromExcelNo.isSelected()) {
								JOptionPane.showMessageDialog(null, "Please select appropriate folder");
							} else {
								if (test.checkFiles() == true) {
									if (test.checkVersion() == true) {
										if (rdbChromeNo.isSelected() == true) {
											rdbVal = rdbChromeNo.getText();
										} else if (rdbChromeYes.isSelected() == true) {
											rdbVal = rdbChromeYes.getText();
										}

										Frame1.appendText("Execution Start");

										// Fields enable - disable

										btnFileUpload.setEnabled(false);
										txtFileUpload.setEnabled(false);
										btnExecuteScript.setEnabled(false);
										btnSetImageFolder.setEnabled(false);
										txtSetImageFolder.setEnabled(false);
										rdbChromeYes.setEnabled(false);
										rdbChromeNo.setEnabled(false);
										rdbattachmentFolderFromExcelYes.setEnabled(false);
										rdbattachmentFolderFromExcelNo.setEnabled(false);
										btnStopExecution.setEnabled(true);

										TestNG testSuite = new TestNG();
										testSuite.setTestClasses(new Class[] { AddBugTask.class });
										testSuite.run();
									}
								}
//								frame.dispose();
							}
						} catch (Exception e) {
							alertMessage(e.toString());
						}
					}
				}).start();

			}
		});

		btnStopExecution.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							JavascriptExecutor js = (JavascriptExecutor) Frame1.driverFrame;
							js.executeScript("return window.stop");
							stop = true;

							test.mailSend.mail("abc.txt", test.username.getContents(), "Execution is stopped manually");
						} catch (Exception e) {
							System.out.println(e);
							alertMessage("Script is stopped 1");
						}
						Frame1.driverFrame.close();
						Frame1.driverFrame.quit();

						btnFileUpload.setEnabled(true);
						txtFileUpload.setEnabled(true);
						btnExecuteScript.setEnabled(false);
						btnSetImageFolder.setEnabled(true);
						txtSetImageFolder.setEnabled(true);
						rdbChromeYes.setEnabled(true);
						rdbChromeNo.setEnabled(true);
						rdbattachmentFolderFromExcelYes.setEnabled(true);
						rdbattachmentFolderFromExcelNo.setEnabled(true);
					}
				}).start();
			}
		});

		btnFileUpload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new Thread(new Runnable() {
					@Override
					public void run() {

						// invoke the showsSaveDialog function to show the save dialog
						int r = jfile.showSaveDialog(null);

						// if the user selects a file
						if (r == JFileChooser.APPROVE_OPTION) {
							// set the label to the path of the selected file
							filePath = jfile.getSelectedFile().getAbsolutePath();
							File ext = new File(filePath);
							if (getExtension(ext).equals("xls")) {
								lblfileUpload.setText(jfile.getSelectedFile().getName());
								txtFileUpload.setText(filePath);
								if (!lblImageFolder.getText().isEmpty()) {
									btnExecuteScript.setEnabled(true);
								} else if (rdbattachmentFolderFromExcelYes.isSelected()) {
									btnExecuteScript.setEnabled(true);
								}
							} else {
								filePath = "";
								lblfileUpload.setText("");
								alertMessage("Please select only xls file");
							}
						}
						// if the user cancelled the operation
						else
							JOptionPane.showMessageDialog(null, "the user cancelled the operation");

						// file download
//						BufferedInputStream in = null;
//						FileOutputStream fout = null;
//						try {
//							in = new BufferedInputStream(
//									new URL("https://drive.google.com/open?id=1i4yYLVAnJ6dRJnAQivo9g5KTkXB7OOjJ")
//											.openStream());
//							fout = new FileOutputStream("E:\\client.webm");
//
//							byte data[] = new byte[1024];
//							int count;
//							while ((count = in.read(data, 0, 1024)) != -1) {
//								fout.write(data, 0, count);
//							}
//						} catch (MalformedURLException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						} finally {
//							if (in != null)
//								try {
//									in.close();
//								} catch (IOException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//							if (fout != null)
//								try {
//									fout.close();
//									JOptionPane.showMessageDialog(null, "the user cancelled the operation");
//								} catch (IOException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//						}

						// file download
					}
				}).start();

			}
		});

		btnSetImageFolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new Thread(new Runnable() {
					@Override
					public void run() {

						// invoke the showsSaveDialog function to show the save dialog
						int r = jdir.showSaveDialog(null);

						// if the user selects a file
						if (r == JFileChooser.APPROVE_OPTION)

						{
							// set the label to the path of the selected file
//							jdir.text
							dirPath = jdir.getSelectedFile();
							imageDirPath = dirPath.getAbsolutePath();

							String path = dirPath.getAbsolutePath();
							char last_char = path.charAt(path.length() - 1);
							if (last_char == '\\') {
							} else {
								path = path + "\\";
							}
							imageDirPath = path;
							lblImageFolder.setText(imageDirPath);
							txtSetImageFolder.setText(imageDirPath);
							if (!lblfileUpload.getText().isEmpty()) {
								btnExecuteScript.setEnabled(true);
							}
						}
						// if the user cancelled the operation
						else
							JOptionPane.showMessageDialog(null, "the user cancelled the operation");
					}
				}).start();

			}
		});

		txtFileUpload.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				if (!txtFileUpload.getText().isEmpty()) {
					filePath = txtFileUpload.getText();
					File ext = new File(filePath);
					if (ext.exists()) {
						if (getExtension(ext) == null) {
							filePath = "";
							alertMessage("Please enter only xls file");
						} else if (getExtension(ext).equals("xls")) {
							lblfileUpload.setText(ext.getName());
							if (!lblImageFolder.getText().isEmpty()) {
								btnExecuteScript.setEnabled(true);
							} else if (rdbattachmentFolderFromExcelYes.isSelected()) {
								btnExecuteScript.setEnabled(true);
							}
						} else {
							filePath = "";
							btnExecuteScript.setEnabled(false);
							alertMessage("Please enter only xls file");
						}
					} else {
						lblfileUpload.setText("");
						btnExecuteScript.setEnabled(false);
						txtFileUpload.setText("Please enter xls file path");
						alertMessage("Please enter only xls file");
					}
				} else {
					lblfileUpload.setText("");
					btnExecuteScript.setEnabled(false);
					txtFileUpload.setText("Please enter xls file path");
				}
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				if (txtFileUpload.getText().equals("Please enter xls file path")) {
					txtFileUpload.setText("");
				}
			}
		});

		txtSetImageFolder.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				if (!txtSetImageFolder.getText().isEmpty()) {
					String path = txtSetImageFolder.getText();
					char last_char = path.charAt(path.length() - 1);
					if (last_char == '\\') {
					} else {
						path = path + "\\";
					}
					File ext = new File(path);
					imageDirPath = path;
					if (ext.exists()) {
						lblImageFolder.setText(path);
						if (!lblfileUpload.getText().isEmpty()) {
							btnExecuteScript.setEnabled(true);
						}
					} else {
						lblImageFolder.setText("");
						txtSetImageFolder.setText("Please enter appropriate folder");
						btnExecuteScript.setEnabled(false);
						alertMessage("Please select appropriate folder");
					}
				} else {
					lblImageFolder.setText("");
					btnExecuteScript.setEnabled(false);
					txtSetImageFolder.setText("Please enter appropriate folder");
				}
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				if (txtSetImageFolder.getText().equals("Please enter appropriate folder")) {
					txtSetImageFolder.setText("");
				}
			}
		});

		rdbattachmentFolderFromExcelYes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnSetImageFolder.setEnabled(false);
				txtSetImageFolder.setEnabled(false);
				lblImageFolder.setText("");
				imageDirPath = "";
				if (!lblfileUpload.getText().isEmpty()) {
					btnExecuteScript.setEnabled(true);
				}
			}
		});

		rdbattachmentFolderFromExcelNo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnSetImageFolder.setEnabled(true);
				txtSetImageFolder.setEnabled(true);
				if (btnExecuteScript.isEnabled()) {
					btnExecuteScript.setEnabled(false);
				}
			}
		});
	}

	public static void appendText(String text) {
		if (textArea == null) {
			System.out.println(text);
		} else {
			textArea.append(text);
			textArea.append("\n");
		}
	}

	public static void alertMessage(String text) {
		JOptionPane.showMessageDialog(null, text);
	}

	public static String getExtension(File f) {

		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');
		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;

	}
}
