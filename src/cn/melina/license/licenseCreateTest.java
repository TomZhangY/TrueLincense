package cn.melina.license;

import cn.melina.license.CreateLicense;

public class licenseCreateTest {
	public static void main(String[] args){
		CreateLicense cLicense = new CreateLicense();
		//获取参数
		cLicense.setParam("./param.properties");
		//生成证书
		cLicense.create();
	}
}
//keytool -genkey -alias privateKey  -keystore privateKey.store -dname "CN=localhost, OU=localhost, O=localhost, L=SH, ST=SH, C=CN" -keypass Z123456789 -storepass Z987654321
//keytool -export -alias privateKey -keystore privateKey.store -file server.crt -storepass Z987654321
//keytool -import -alias publickey -file server.crt -keystore publicKey.store -storepass Z987654321

//keytool -list -v -keystore privateKey.store -storepass Z987654321
//keytool -printcert -v -file server.crt



