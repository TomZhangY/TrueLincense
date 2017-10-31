package cn.melina.license.create.imp;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.security.auth.x500.X500Principal;
import javax.sql.DataSource;

import license.identify.IUniqueIdentify;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.tap.server.datasource.DataSourceServer;
import org.tap.server.datasource.IDataSource;

import cn.melina.license.create.ICreateLicense;
import de.schlichtherle.license.CipherParam;
import de.schlichtherle.license.DefaultCipherParam;
import de.schlichtherle.license.DefaultKeyStoreParam;
import de.schlichtherle.license.DefaultLicenseParam;
import de.schlichtherle.license.KeyStoreParam;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseManager;
import de.schlichtherle.license.LicenseParam;

public class CreateLicense implements ICreateLicense{
	
	private static Logger logger = Logger.getLogger(CreateLicense.class);
	
	public IDataSource dataSource = null;
	
	private IUniqueIdentify uniqueIdentify;
	
	private LicenseParameter licenseParameter;
	
	private final static X500Principal DEFAULTHOLDERANDISSUER = new X500Principal(
			"CN=Duke、OU=JavaSoft、O=Sun Microsystems、C=US");
	
	public IUniqueIdentify getUniqueIdentify() {
		return uniqueIdentify;
	}

	public void setUniqueIdentify(IUniqueIdentify uniqueIdentify) {
		this.uniqueIdentify = uniqueIdentify;
	}
	
	public LicenseParameter getLicenseParameter() {
		return licenseParameter;
	}

	public void setLicenseParameter(LicenseParameter licenseParameter) {
		this.licenseParameter = licenseParameter;
	}

	public String create(String requestJson){
		License license = new License();
		BufferedInputStream buffer = null;
		ByteArrayOutputStream outSteam = null;
		Map<String,Object> returnMap = new  HashMap<String,Object>();
		try {
			JSONObject requestData = new JSONObject(requestJson);
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
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			c.set(Calendar.MONTH, c.get(Calendar.MONTH) + Integer.parseInt(valueIsNull(requestData,"ActiveMonth")));
			license.setExpireDate(sdf.format(c.getTime()));
			logger.debug(license);
			
			//先检查数据库是否有记录并且没到期
			boolean success = selectDB(license);
			if(success){
				LicenseManager licenseManager = LicenseManagerHolder
						.getLicenseManager(initLicenseParams0());
				licenseManager.store((createLicenseContent(license)), new File(licenseParameter.getLicPath()));	
				buffer = new BufferedInputStream(new FileInputStream(licenseParameter.getLicPath()));
				outSteam = new ByteArrayOutputStream(1024);
				byte[] b = new byte[1024];
				int i = -1;
				while(( i=buffer.read(b)) != -1){
					outSteam.write(b,0,i);
				}
				String content = Base64.encodeBase64String(outSteam.toByteArray());
				buffer.close();
				//save To DB
				saveToDB(license);
				returnMap.put("license", content);
				returnMap.put("state", "00");
				returnMap.put("result", "create success");
			}else{
				returnMap.put("state", "11");
				returnMap.put("result", " it already exist and not expire");
			}
		} catch (Exception e) {
			returnMap.put("state", "12");
			returnMap.put("result", "系统异常");
			logger.error(e);
		}
		JSONObject j = new JSONObject(returnMap);
		return j.toString();
	}

	// 返回生成证书时需要的参数
	private LicenseParam initLicenseParams0() {
		Preferences preference = Preferences
				.userNodeForPackage(CreateLicense.class);
		// 设置对证书内容加密的对称密码
		CipherParam cipherParam = new DefaultCipherParam(licenseParameter.getStorePwd());
		// 参数1,2从哪个Class.getResource()获得密钥库;参数3密钥库的别名;参数4密钥库存储密码;参数5密钥库密码
		KeyStoreParam privateStoreParam = new DefaultKeyStoreParam(
				CreateLicense.class, licenseParameter.getPriPath(), licenseParameter.getPrivatealias(), licenseParameter.getStorePwd(), licenseParameter.getKeyPwd());
		LicenseParam licenseParams = new DefaultLicenseParam(licenseParameter.getSubject(),
				preference, privateStoreParam, cipherParam);
		return licenseParams;
	}
	
	private final LicenseContent createLicenseContent(License license) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		LicenseContent content = null;
		content = new LicenseContent();
		content.setSubject(licenseParameter.getSubject());
		content.setHolder(DEFAULTHOLDERANDISSUER);
		content.setIssuer(DEFAULTHOLDERANDISSUER);
		try {
			content.setIssued(format.parse(license.getActiveDate()));
			content.setNotBefore(format.parse(license.getActiveDate()));
			content.setNotAfter(format.parse(license.getExpireDate()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		content.setConsumerType(consumerType);
//		content.setConsumerAmount(consumerAmount);
//		content.setInfo(info);
		// 扩展
		content.setExtra(license.getSequenceNum());
		return content;
	}

	private String valueIsNull(JSONObject obj, String s) {
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
	
	private boolean selectDB(License license){
		DataSource ds = null;
		Connection conn = null;
		PreparedStatement pstmt = null ;
		ResultSet rs = null;
		try {
			dataSource = DataSourceServer.ds;
			ds = dataSource.getDataSource();
			conn = ds.getConnection();
			if(conn != null ){
				String selectSql = "select ExpireDate from License where SequenceNum = ?  and  ExpireDate > ?" ;
				pstmt = (PreparedStatement) conn.prepareStatement(selectSql);
				pstmt.setString(1, license.getSequenceNum());
				pstmt.setString(2, license.getActiveDate());
				rs = pstmt.executeQuery();
				if (rs.next()) {
					logger.debug("it already exist,SequenceNum = "+license.getSequenceNum()+",and ExpireDate = "+rs.getString(1));
					return false;
				}else{
					return true;
				}
			}	
		} catch (Exception e) {
			logger.error(e);
		} finally{
			try {
				if (null != rs) {
					rs.close();
				}
				if (null != pstmt) {
					pstmt.close();
				}
			} catch (Exception e2) {
				logger.error(e2);
			}
		}
		return false;
	}
	
	private void saveToDB(License license){
		DataSource ds = null;
		Connection conn = null;
		PreparedStatement pstmt = null ;
		try {
			dataSource = DataSourceServer.ds;
			ds = dataSource.getDataSource();
			conn = ds.getConnection();
			if (conn != null) {
				String updateSql = "insert into License (SequenceNum,Company,LastDate,ActiveDate,ActiveMonth,ExpireDate,PCName,ProductID,StoreId,PosId,ActiveIP,ServiceTerm) values (?,?,?,?,?,?,?,?,?,?,?,?)";
				pstmt = (PreparedStatement) conn.prepareStatement(updateSql);
				pstmt.setString(1, license.getSequenceNum());
				pstmt.setString(2, license.getCompany());
				pstmt.setString(3, license.getLastDate());
				pstmt.setString(4, license.getActiveDate());
				pstmt.setString(5, license.getActiveMonth());
				pstmt.setString(6, license.getExpireDate());
				pstmt.setString(7, license.getPCName());
				pstmt.setString(8, license.getProductID());
				pstmt.setString(9, license.getStoreId());
				pstmt.setString(10, license.getPosId());
				pstmt.setString(11, license.getActiveIP());
				pstmt.setString(12, license.getServiceTerm());
				int flag = pstmt.executeUpdate();
				if (flag > 0) {
					logger.debug("sql execute success");
				}
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}
}
