import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Bot extends Thread {
	private int instaface_id;
	private String fileName;
	private UI ui = null;
	
	Bot(int instaface_id, String fileName, UI ui){
		this.instaface_id = instaface_id;
		this.fileName = fileName;
		this.ui = ui;
	}
	
	public void run() {
		this.startBot();
	}
	
	public String startBot() {
		String rep;
		try {
			String getDir = Bot.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
			String[] dirList = getDir.split("/");
			String dir = "";
			for(int i=0; i<dirList.length-1; i++) {
				dir += dirList[i]+"/";
			}
			dir += "resource/php/";
			rep = exec("php "+dir+fileName+".php "+this.instaface_id);
		} catch (Exception e) {
			e.printStackTrace();
			rep = "error";
		}
		return rep;
	}
	
	public String stopBot() {
		System.out.println("Stopped...");
		this.exec("pkill -9 ssh");
		return this.exec("pkill -9 php");
	}
	
	public String exec(String command) {
		StringBuffer output = new StringBuffer();
		Process p;
		String line;
		try {
			p = Runtime.getRuntime().exec(command);

			InputStream inputStream = p.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			while ((line = bufferedReader.readLine()) != null) {
				output.append(line);
				if(this.ui != null) {
					this.ui.addLog(this.fileName+" - "+line);
				}
			}
			p.waitFor();
		}catch (Exception e) {
			System.err.println("error: "+e.getMessage());
		}
		return output.toString();
	}
}