package serviceprovideroma.soc.bd.com.serviceprovideroma.model;

public class ServiceListPandding {
    String SERVICE_ID,SERVICE_TYPE_NAME,TOKEN_NO,LOCATION_NAME,SERVICE_CHARGE,MOBILE_NUM,SERVICE_STATUS,
            REMARKS,USER_ADDRESS,USER_LATITUDE,USER_LONGITUDE,CREATE_DATA,USER_INFO_NAME,USER_PHONE,USER_EMAIL;

    public String getSERVICE_ID() {
        return SERVICE_ID;
    }

    public void setSERVICE_ID(String SERVICE_ID) {
        this.SERVICE_ID = SERVICE_ID;
    }

    public ServiceListPandding(String SERVICE_ID, String SERVICE_TYPE_NAME, String TOKEN_NO, String LOCATION_NAME, String SERVICE_CHARGE, String MOBILE_NUM, String SERVICE_STATUS, String REMARKS, String USER_ADDRESS, String USER_LATITUDE, String USER_LONGITUDE, String CREATE_DATA, String USER_INFO_NAME, String USER_PHONE, String USER_EMAIL) {
        this.SERVICE_ID = SERVICE_ID;
        this.SERVICE_TYPE_NAME = SERVICE_TYPE_NAME;
        this.TOKEN_NO = TOKEN_NO;
        this.LOCATION_NAME = LOCATION_NAME;
        this.SERVICE_CHARGE = SERVICE_CHARGE;
        this.MOBILE_NUM = MOBILE_NUM;
        this.SERVICE_STATUS = SERVICE_STATUS;
        this.REMARKS = REMARKS;
        this.USER_ADDRESS = USER_ADDRESS;
        this.USER_LATITUDE = USER_LATITUDE;
        this.USER_LONGITUDE = USER_LONGITUDE;
        this.CREATE_DATA = CREATE_DATA;
        this.USER_INFO_NAME = USER_INFO_NAME;
        this.USER_PHONE = USER_PHONE;
        this.USER_EMAIL = USER_EMAIL;
    }

    public String getSERVICE_TYPE_NAME() {
        return SERVICE_TYPE_NAME;
    }

    public void setSERVICE_TYPE_NAME(String SERVICE_TYPE_NAME) {
        this.SERVICE_TYPE_NAME = SERVICE_TYPE_NAME;
    }

    public String getTOKEN_NO() {
        return TOKEN_NO;
    }

    public void setTOKEN_NO(String TOKEN_NO) {
        this.TOKEN_NO = TOKEN_NO;
    }

    public String getLOCATION_NAME() {
        return LOCATION_NAME;
    }

    public void setLOCATION_NAME(String LOCATION_NAME) {
        this.LOCATION_NAME = LOCATION_NAME;
    }

    public String getSERVICE_CHARGE() {
        return SERVICE_CHARGE;
    }

    public void setSERVICE_CHARGE(String SERVICE_CHARGE) {
        this.SERVICE_CHARGE = SERVICE_CHARGE;
    }

    public String getMOBILE_NUM() {
        return MOBILE_NUM;
    }

    public void setMOBILE_NUM(String MOBILE_NUM) {
        this.MOBILE_NUM = MOBILE_NUM;
    }

    public String getSERVICE_STATUS() {
        return SERVICE_STATUS;
    }

    public void setSERVICE_STATUS(String SERVICE_STATUS) {
        this.SERVICE_STATUS = SERVICE_STATUS;
    }

    public String getREMARKS() {
        return REMARKS;
    }

    public void setREMARKS(String REMARKS) {
        this.REMARKS = REMARKS;
    }

    public String getUSER_ADDRESS() {
        return USER_ADDRESS;
    }

    public void setUSER_ADDRESS(String USER_ADDRESS) {
        this.USER_ADDRESS = USER_ADDRESS;
    }

    public String getUSER_LATITUDE() {
        return USER_LATITUDE;
    }

    public void setUSER_LATITUDE(String USER_LATITUDE) {
        this.USER_LATITUDE = USER_LATITUDE;
    }

    public String getUSER_LONGITUDE() {
        return USER_LONGITUDE;
    }

    public void setUSER_LONGITUDE(String USER_LONGITUDE) {
        this.USER_LONGITUDE = USER_LONGITUDE;
    }

    public String getCREATE_DATA() {
        return CREATE_DATA;
    }

    public void setCREATE_DATA(String CREATE_DATA) {
        this.CREATE_DATA = CREATE_DATA;
    }

    public String getUSER_INFO_NAME() {
        return USER_INFO_NAME;
    }

    public void setUSER_INFO_NAME(String USER_INFO_NAME) {
        this.USER_INFO_NAME = USER_INFO_NAME;
    }

    public String getUSER_PHONE() {
        return USER_PHONE;
    }

    public void setUSER_PHONE(String USER_PHONE) {
        this.USER_PHONE = USER_PHONE;
    }

    public String getUSER_EMAIL() {
        return USER_EMAIL;
    }

    public void setUSER_EMAIL(String USER_EMAIL) {
        this.USER_EMAIL = USER_EMAIL;
    }
}
