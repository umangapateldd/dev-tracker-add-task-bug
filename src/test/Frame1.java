package test;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileSystemView;

public class Frame1 {

	private JFrame frame;
	static JTextArea textArea;
	JFileChooser jfile;
	JFileChooser jdir;
	String filePath = "";
	String imageDirPath = "";
	File dirPath = null;

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
					e.printStackTrace();
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
		frame.getContentPane().setLayout(null);

		JTextField txtFileUpload = new JTextField();
		JTextField txtSetImageFolder = new JTextField();
		JButton btnNewButton = new JButton("Execute Script");
		btnNewButton.setEnabled(false);
		JButton btnFileUpload = new JButton("Upload file");
		JButton btnSetImageFolder = new JButton("Select image path");
		JLabel fileUploadLabel = new JLabel();
		JLabel imageFolderLabel = new JLabel();
		JLabel orlabel1 = new JLabel("OR");
		JLabel orlabel2 = new JLabel("OR");

		textArea = new JTextArea();
		jfile = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		jdir = new JFileChooser();
		jdir.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		btnFileUpload.setBounds(10, 11, 117, 41);
		fileUploadLabel.setBounds(496, 11, 209, 28);
		btnSetImageFolder.setBounds(10, 58, 117, 41);
		imageFolderLabel.setBounds(496, 50, 209, 28);
		btnNewButton.setBounds(10, 106, 117, 41);
		txtFileUpload.setBounds(193, 17, 260, 28);
		txtSetImageFolder.setBounds(193, 64, 260, 28);
		orlabel1.setBounds(153, 17, 30, 28);
		orlabel2.setBounds(153, 64, 30, 28);

		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setBounds(10, 158, 443, 214);
		frame.getContentPane().add(textArea);

		frame.getContentPane().add(btnNewButton);
		frame.getContentPane().add(fileUploadLabel);
		frame.getContentPane().add(btnFileUpload);
		frame.getContentPane().add(btnSetImageFolder);
		frame.getContentPane().add(imageFolderLabel);
		frame.getContentPane().add(txtFileUpload);
		frame.getContentPane().add(txtSetImageFolder);
		frame.getContentPane().add(orlabel1);
		frame.getContentPane().add(orlabel2);

		AddBugTask test = new AddBugTask();
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							if (filePath.isEmpty()) {
								JOptionPane.showMessageDialog(null, "Please select excel file");
							} else {
								test.add_bug_task(filePath, imageDirPath);
//								frame.dispose();
							}

						} catch (IOException | InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
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
									btnNewButton.setEnabled(true);
								}
							} else {
								filePath = "";
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
								btnNewButton.setEnabled(true);
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
						if (getExtension(ext).equals("xls")) {
							fileUploadLabel.setText(ext.getName());
							if (!imageFolderLabel.getText().isEmpty()) {
								btnNewButton.setEnabled(true);
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
