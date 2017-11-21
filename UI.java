import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class UI extends JFrame{

	private Thread botMessage;
	private Thread botInbox;
	private JTextArea logs;
	JScrollPane scroll;
	
	UI(){
		super();
		this.setVisible(true);
		this.setTitle("Instagram Manager");
		this.setLocationRelativeTo(null);
		this.setSize(600, 300);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        if (JOptionPane.showConfirmDialog(null, 
		            "Are you sure to close this window? This will turn off the bot.", "Really Closing?", 
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
						Bot b = new Bot(0, "", new UI());
						b.stopBot();
		            System.exit(0);
		        }
		    }
		});
		
		JPanel pan = new JPanel();		
		JLabel emailLabel = new JLabel("email");
		JTextField email = new JTextField();
		email.setText("pierre.leroy.mail@gmail.com");
		email.setPreferredSize(new Dimension(200, 20));
		JLabel passwdLabel = new JLabel("password");
		JTextField passwd = new JTextField();
		passwd.setPreferredSize(new Dimension(200, 20));
		passwd.setText("badgateway");
				
		JLabel error = new JLabel("Wrong email or password");
		error.setForeground(new Color(255, 0, 0));
		error.setVisible(false);
		
		logs = new JTextArea();
		scroll = new JScrollPane(logs);
		scroll.setPreferredSize(new Dimension(500, 150));
		
		JButton start = new JButton("Launch bot");		
		JButton stop = new JButton("Stop bot");
		stop.setVisible(false);
		
		UI it = this;
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HTTPRequest test = new HTTPRequest("http://45.76.11.241/connection/instagram/postConnection.php", "POST");
				test.addArgument("apiCall", "java");
				test.addArgument("email", email.getText());
				test.addArgument("password", passwd.getText());
				String resp = test.send();
				if(resp.equals("error")) {
					error.setVisible(true);
				}else { // good account
					try {
						int instaface_id = Integer.parseInt(resp);
						error.setVisible(false);
						start.setVisible(false);
						stop.setVisible(true);
						
						botMessage = new Thread(new Bot(instaface_id, "ymessage", it));
						botMessage.start();
						
						botInbox = new Thread(new Bot(instaface_id, "yinbox", it));
						botInbox.start();
						
					}catch(Exception err) {
						System.err.println("Unknown error: "+resp);
					}
				}
			}
		});
		stop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//stopping running thread
				botMessage.stop();
				botInbox.stop();
				// stopping all PHP process
				Bot b = new Bot(0, "", it);
				b.stopBot();
				it.addLog("Bot stopped.");
				start.setVisible(true);
				stop.setVisible(false);
			}
		});

		pan.add(emailLabel);
		pan.add(email);
		pan.add(passwdLabel);
		pan.add(passwd);
		pan.add(start);
		pan.add(stop);
		pan.add(error);
		pan.add(scroll);
		
		this.setContentPane(pan);
		this.validate();
		
		start.setEnabled(false);
//		this.firstLaunch();
		this.addLog("Ready to go!");
		start.setEnabled(true);
	}

	public void addLog(String log) {
		this.logs.append(log+"\n");
		//scrolling
		JScrollBar scrollBar = this.scroll.getVerticalScrollBar();
		scrollBar.setValue(scrollBar.getMaximum());
		this.revalidate();
	}

//	private void firstLaunch(){
//		try {
//			String zipFile = "php.zip";
//			String dir = new java.io.File(".").getCanonicalPath();
//			String destDir = dir+"/src/";
//			String dirFiles = dir+"/src/php/";
//			File f = new File(dirFiles+"ymessage.php");
//			this.addLog(f.getAbsolutePath());
//			if(!f.exists()) {
//				this.addLog("Files not found");
////				URL zipPath = UI.class.getResource(zipFile);
////				this.extractFolder(zipPath.getFile());
//			}else {
//				this.addLog("I found the file!");
//			}
//		}catch(Exception e) {
//			e.printStackTrace();
//			this.addLog(e.getMessage());
//		}
//    }
//	
//	private void extractFolder(String zipFile) throws ZipException, IOException{
//		int BUFFER = 2048;
//	    File file = new File(zipFile);
//
//	    ZipFile zip = new ZipFile(file);
//	    String newPath = zipFile.substring(0, zipFile.length() - 4);
//
//	    new File(newPath).mkdir();
//	    Enumeration zipFileEntries = zip.entries();
//
//	    // Process each entry
//	    while (zipFileEntries.hasMoreElements()){
//	        // grab a zip file entry
//	        ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
//	        String currentEntry = entry.getName();
//	        File destFile = new File(newPath, currentEntry);
//	        System.out.println("destination: "+destFile);
//	        this.addLog("unzip \t"+destFile);
//	        //destFile = new File(newPath, destFile.getName());
//	        File destinationParent = destFile.getParentFile();
//
//	        // create the parent directory structure if needed
//	        destinationParent.mkdirs();
//
//	        if (!entry.isDirectory()){
//	            BufferedInputStream is = new BufferedInputStream(zip
//	            .getInputStream(entry));
//	            int currentByte;
//	            // establish buffer for writing file
//	            byte data[] = new byte[BUFFER];
//
//	            // write the current file to disk
//	            FileOutputStream fos = new FileOutputStream(destFile);
//	            BufferedOutputStream dest = new BufferedOutputStream(fos,
//	            BUFFER);
//
//	            // read and write until last byte is encountered
//	            while ((currentByte = is.read(data, 0, BUFFER)) != -1){
//	                dest.write(data, 0, currentByte);
//	            }
//	            dest.flush();
//	            dest.close();
//	            is.close();
//	        }
//
//	        if (currentEntry.endsWith(".zip")){
//	            // found a zip file, try to open
//	            this.extractFolder(destFile.getAbsolutePath());
//	        }
//	    }
//	    this.addLog("DONE");
//	}
}
