package cn.melina.license.create;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class RequestLicense {
	private static Logger logger = Logger.getLogger(RequestLicense.class);
	
	private CreateLicense createLicense ;
	
	private String licenseFile ="conf/param.properties";
	
	public CreateLicense getCreateLicense() {
		return createLicense;
	}

	public void setCreateLicense(CreateLicense createLicense) {
		this.createLicense = createLicense;
	}
	
	public String getLicenseFile() {
		return licenseFile;
	}

	public void setLicenseFile(String licenseFile) {
		this.licenseFile = licenseFile;
	}

	public String create(String request){
		License license = new License();
		BufferedInputStream buffer = null;
		ByteArrayOutputStream outSteam = null;
		try {
			logger.debug("request");
			JSONObject requestData = new JSONObject(request);
			license.setSequenceNum(valueIsNull(requestData,"SequenceNum"));
			license.setCompany(valueIsNull(requestData,"Company"));
			license.setActiveMonth(valueIsNull(requestData,"ActiveMonth"));
			license.setServiceTerm(valueIsNull(requestData,"ServiceTerm"));
			license.setPCName(valueIsNull(requestData,"PCName"));
			license.setActiveIP(valueIsNull(requestData,"ActiveIP"));
			license.setPosId(valueIsNull(requestData,"PosId"));
			license.setStoreId(valueIsNull(requestData,"StoreId"));
			license.setProductID(valueIsNull(requestData,"ProductID"));
			license.setActiveDate(valueIsNull(requestData,"ActiveDate"));
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date d = new Date();
			license.setLastDate(sdf.format(d));
//			d.getMonth()
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			c.set(Calendar.MONTH, c.get(Calendar.MONTH) + Integer.parseInt(valueIsNull(requestData,"ActiveMonth")));
			license.setExpireDate(sdf.format(c.getTime()));
			logger.debug(license);
			//
//			boolean isTrue = saveToDB(license);
			
			//获取参数
			createLicense.setParam(licenseFile,license);
			//生成证书
			boolean success= createLicense.create();
			Map<String,Object> map = new  HashMap<String,Object>();
			map.put("result", success);
			if(success){
				Properties prop = new Properties();
				InputStream in = new  FileInputStream("conf/param.properties");
				try {
					prop.load(in);
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println(prop.getProperty("licPath"));
				buffer = new BufferedInputStream(new FileInputStream(prop.getProperty("licPath")));
				BufferedOutputStream bs = new BufferedOutputStream(new FileOutputStream("conf/bigdata1.lic"));
				outSteam = new ByteArrayOutputStream(1024);
				byte[] b = new byte[1024];
				int i = -1;
				while(( i=buffer.read(b)) != -1){
					outSteam.write(b,0,i);
				}
				System.out.println(outSteam.size());;
				String ss = Base64.encodeBase64String(outSteam.toByteArray());
				System.out.println(ss);
				bs.write(Base64.decodeBase64(ss));
				bs.close();
				map.put("license", ss);
			}
			JSONObject j = new JSONObject(map);
			return j.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if(buffer != null) buffer.close();
				if(outSteam != null) outSteam.close();
			} catch (Exception e2) {
				
			}
		}
		return null;
	}
	
	private String valueIsNull(JSONObject obj,String s){
		String value = null ;
		if(obj.isNull(s)){
			value = "";
		}else{
			try {
				value = obj.getString(s) ;
			} catch (JSONException e) {
				e.printStackTrace();
				logger.error(e);
			}
		}
		return value;
	}
	
	public static void main(String[] args) {
		System.out.println(System.getProperties());
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Date d = new Date();
//		Calendar c = Calendar.getInstance();
//		c.setTime(d);
//		System.out.println(sdf.format(c.getTime()));
//		c.set(Calendar.MONTH, c.get(Calendar.MONTH)+12);
//		System.out.println(sdf.format(c.getTime()));
//		{"SequenceNum":"81AC7607-1E47-CB11-BC6A-8C7EF84170A7","Company":"XXX公司","ActiveDate":"2017-10-20 11:11:11","ActiveMonth":"6","PCName":"Tom","ProductID":"XXX","StoreId":"34","PosId":"23","ActiveIP":"192.168.1.1","ServiceTerm":"VEI"}
//		{"result":"true","license":""}
		
	}
}
