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

	/**
	 * Launch the application.
	 */
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
		frame.setBounds(100, 100, 731, 422);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel jp = new JPanel();
		frame.getContentPane().setLayout(null);

		JTextField txtFileUpload = new JTextField();
		JTextField txtSetImageFolder = new JTextField();
		JButton btnExecuteScript = new JButton("Execute Script");
		JButton btnStopExecution = new JButton("Stop Execution");
		btnExecuteScript.setEnabled(false);
		JButton btnFileUpload = new JButton("Upload file");
		JButton btnSetImageFolder = new JButton("Select image path");
		JLabel fileUploadLabel = new JLabel();
		JLabel imageFolderLabel = new JLabel();
		JLabel orlabel1 = new JLabel("OR");
		JLabel orlabel2 = new JLabel("OR");
		JLabel lblBrowserDisplay = new JLabel("Browser Display ?");
		JRadioButton rdbChromeYes = new JRadioButton();
		JRadioButton rdbChromeNo = new JRadioButton();
		ButtonGroup G1 = new ButtonGroup();

		textArea = new JTextArea();

		jfile = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		jdir = new JFileChooser();
		jdir.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		// x,y,width,height
		btnFileUpload.setBounds(10, 11, 140, 41);

		fileUploadLabel.setBounds(460, 15, 209, 28);
		imageFolderLabel.setBounds(460, 65, 209, 28);

		btnSetImageFolder.setBounds(10, 58, 140, 41);
		btnExecuteScript.setBounds(10, 106, 140, 41);
		btnStopExecution.setBounds(193, 101, 125, 46);

		txtFileUpload.setBounds(193, 17, 260, 28);
		txtSetImageFolder.setBounds(193, 64, 260, 28);

		orlabel1.setBounds(163, 17, 30, 28);
		orlabel2.setBounds(163, 64, 30, 28);

		rdbChromeYes.setBounds(550, 112, 52, 28);
		rdbChromeNo.setBounds(617, 112, 52, 28);

		lblBrowserDisplay.setBounds(434, 112, 110, 28);

		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setBounds(10, 158, 443, 214);
		frame.getContentPane().add(textArea);

		frame.getContentPane().add(btnExecuteScript);
		frame.getContentPane().add(btnStopExecution);
		frame.getContentPane().add(fileUploadLabel);
		frame.getContentPane().add(btnFileUpload);
		frame.getContentPane().add(btnSetImageFolder);
		frame.getContentPane().add(imageFolderLabel);
		frame.getContentPane().add(txtFileUpload);
		frame.getContentPane().add(txtSetImageFolder);
		frame.getContentPane().add(orlabel1);
		frame.getContentPane().add(orlabel2);
		frame.getContentPane().add(rdbChromeYes);
		frame.getContentPane().add(rdbChromeNo);
		frame.getContentPane().add(lblBrowserDisplay);

		rdbChromeYes.setText("Yes");
		rdbChromeNo.setText("No");
		rdbChromeNo.setSelected(true);

		G1.add(rdbChromeYes);
		G1.add(rdbChromeNo);

		JScrollPane sampleScrollPane = new JScrollPane(textArea);
		sampleScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sampleScrollPane.setBounds(10, 158, 443, 214);
		frame.getContentPane().add(sampleScrollPane);

		AddBugTask test = new AddBugTask();
		btnExecuteScript.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							if (filePath.isEmpty()) {
								JOptionPane.showMessageDialog(null, "Please select excel file");
							} else if (imageFolderLabel.getText().isEmpty()) {
								JOptionPane.showMessageDialog(null, "Please select appropriate folder");
							} else {
								if (test.checkFiles() == true) {
									if (test.checkVersion() == true) {
										if (rdbChromeNo.isSelected() == true) {
											rdbVal = rdbChromeNo.getText();
										} else if (rdbChromeYes.isSelected() == true) {
											rdbVal = rdbChromeYes.getText();
										}

										System.out.println(rdbVal);
										Frame1.appendText("Execution Start");
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
							Frame1.driverFrame.close();
							Frame1.driverFrame.quit();
						} catch (Exception e) {
							alertMessage("Script is stopped 1");
						}
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
								fileUploadLabel.setText(jfile.getSelectedFile().getName());
								if (!imageFolderLabel.getText().isEmpty()) {
									btnExecuteScript.setEnabled(true);
								}
							} else {
								filePath = "";
								fileUploadLabel.setText("");
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
							imageFolderLabel.setText(dirPath.getAbsolutePath());
							if (!fileUploadLabel.getText().isEmpty()) {
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
							fileUploadLabel.setText(ext.getName());
							if (!imageFolderLabel.getText().isEmpty()) {
								btnExecuteScript.setEnabled(true);
							}
						} else {
							filePath = "";
							alertMessage("Please enter only xls file");
						}
					} else {
						alertMessage("Please enter only xls file");
					}
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
						imageFolderLabel.setText(path);
						if (!fileUploadLabel.getText().isEmpty()) {
							btnExecuteScript.setEnabled(true);
						}
					} else {
						imageFolderLabel.setText("");
						alertMessage("Please select appropriate folder");
					}
				}
			}
		});
	}

	public static void appendText(String text) {
		textArea.append(text);
		textArea.append("\n");
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
