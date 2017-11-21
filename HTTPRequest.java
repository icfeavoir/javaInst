import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public class HTTPRequest {
	
	private String url;
	private boolean isPost;
	private Map<String, Object> data;
	
	public HTTPRequest(String url){
		this(url, "GET");
	}
	
	public HTTPRequest(String url, String type){
		if(type != "GET" && type != "POST"){
			throw new Error("Type must be GET or POST");
		}else{
			this.url = url;
			this.isPost = type == "POST";
			this.data = new HashMap<String, Object>();
		}
	}
	
	public String getUrl(){
		return this.url;
	}
	public Map<String, Object> getData(){
		return this.data;
	}
	public boolean getIsPost(){
		return this.isPost;
	}
	
	public void addArgument(String key, Object value){
		this.data.put(key, value);
	}
	
	public String send(){
		return this.getIsPost()?sendPost():sendGet();
	}
	
	private String sendPost(){
		BufferedReader reader = null;
        StringBuilder sb = null;
        String dataToSend = "";
        try {
            for(Map.Entry<String, Object> entry : this.data.entrySet()) {
                String key = entry.getKey();
                String value = ""+entry.getValue();
                
                dataToSend += URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8") + "&";
            }
            
            URL url = new URL(this.getUrl());
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(dataToSend);
            wr.flush();

            //response
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            sb = new StringBuilder();
            String line = null;

            while((line = reader.readLine()) != null){
                sb.append(line);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        
        return sb.toString();
	}
	
	private String sendGet(){
		BufferedReader reader = null;
        StringBuilder sb = null;
        String dataToSend = "";
        try {
            for(Map.Entry<String, Object> entry : this.data.entrySet()) {
                String key = entry.getKey();
                String value = ""+entry.getValue();
                
                dataToSend += URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8") + "&";
            }
                        
            URL url = new URL(this.getUrl()+"?"+dataToSend);
            URLConnection conn = url.openConnection();
            //response
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            sb = new StringBuilder();
            String line = null;

            while((line = reader.readLine()) != null){
                sb.append(line);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        
        return sb.toString();
	}
}
