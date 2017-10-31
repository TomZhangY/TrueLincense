package cn.melina.license.create.imp;

public class License {
	private String sequenceNum;
	private String company;
	private String lastDate;
	private String activeDate;
	private String expireDate;
	private String productID;
	private String storeId;
	private String posId;
	private String activeIP;
	private String PCName;
	private String serviceTerm;
	private String activeMonth;
	
	public String getSequenceNum() {
		return sequenceNum;
	}
	public void setSequenceNum(String sequenceNum) {
		this.sequenceNum = sequenceNum;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getLastDate() {
		return lastDate;
	}
	public void setLastDate(String lastDate) {
		this.lastDate = lastDate;
	}
	public String getActiveDate() {
		return activeDate;
	}
	public void setActiveDate(String activeDate) {
		this.activeDate = activeDate;
	}
	public String getExpireDate() {
		return expireDate;
	}
	public void setExpireDate(String expireDate) {
		this.expireDate = expireDate;
	}
	public String getProductID() {
		return productID;
	}
	public void setProductID(String productID) {
		this.productID = productID;
	}
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	public String getPosId() {
		return posId;
	}
	public void setPosId(String posId) {
		this.posId = posId;
	}
	public String getActiveIP() {
		return activeIP;
	}
	public void setActiveIP(String activeIP) {
		this.activeIP = activeIP;
	}
	public String getPCName() {
		return PCName;
	}
	public void setPCName(String pCName) {
		PCName = pCName;
	}
	public String getServiceTerm() {
		return serviceTerm;
	}
	public void setServiceTerm(String serviceTerm) {
		this.serviceTerm = serviceTerm;
	}
	public String getActiveMonth() {
		return activeMonth;
	}
	public void setActiveMonth(String activeMonth) {
		this.activeMonth = activeMonth;
	}
	@Override
	public String toString() {
		return "License [sequenceNum=" + sequenceNum + ", company=" + company
				+ ", lastDate=" + lastDate + ", activeDate=" + activeDate
				+ ", expireDate=" + expireDate + ", productID=" + productID
				+ ", storeId=" + storeId + ", posId=" + posId + ", activeIP="
				+ activeIP + ", PCName=" + PCName + ", serviceTerm="
				+ serviceTerm + ", activeMonth=" + activeMonth + "]";
	}
}
