package cn.melina.license.create;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.tap.server.datasource.DataSourceServer;

import cn.melina.license.create.imp.CreateLicense;

public class licenseCreateTest {
	public static void main(String[] args){
		Map<String,Object> map = new  HashMap<String,Object>();
		map.put("SequenceNum", "E4-7F-B2-20-85-E0");
		map.put("Company","我1");
		map.put("ActiveDate","2017-10-20");
		map.put("ActiveMonth","6");
		map.put("PCName","Tom");
		map.put("ProductID","11");
		map.put("StoreId","34");
		map.put("PosId","23");
		map.put("ActiveIP","192.168.1.1");
		map.put("ServiceTerm","VEI");
		
		BasicConfigurator.configure();
		ApplicationContext context = new FileSystemXmlApplicationContext(
				"conf/spring.xml");
		DataSourceServer dataSource = (DataSourceServer) context
				.getBean("dataSourceServer");
		try {
			dataSource.startup();
		} catch (Exception e) {
			e.printStackTrace();
		}
		CreateLicense re = (CreateLicense)context.getBean("requestLicense");
		String result = re.create(new JSONObject(map).toString());
		System.out.println(result);
	}
}
//keytool -genkey -alias privateKey  -keystore privateKey.store -dname "CN=localhost, OU=localhost, O=localhost, L=SH, ST=SH, C=CN" -keypass Z123456789 -storepass Z987654321
//keytool -export -alias privateKey -keystore privateKey.store -file server.crt -storepass Z987654321
//keytool -import -alias publickey -file server.crt -keystore publicKey.store -storepass Z987654321

//keytool -list -v -keystore privateKey.store -storepass Z987654321
//keytool -printcert -v -file server.crt



