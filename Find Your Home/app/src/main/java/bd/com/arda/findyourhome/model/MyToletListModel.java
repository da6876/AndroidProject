package bd.com.arda.findyourhome.model;

import java.io.Serializable;

/**
 * Created by user on 6/30/2018.
 */

public class MyToletListModel implements Serializable {

    private String TOLET_INFO_ID, USER_ACC_ID, TOLET_NAME, TOLET_DETAILS, ADDRESS,
            LATTITUDE, LOGLITUTDE, PRICE, BATHS, BEDS, FLOORS, AVAILABLE_FROM,
            CONTACT_PERSON_NM, CONTACT_PERSON_PHN, CONTACT_PERSON_EML, TOLET_TYPE_ID,
            TOLET_TYPE_NAME, CREATE_DATA, CREATE_BY;

    public MyToletListModel(String TOLET_INFO_ID, String USER_ACC_ID, String TOLET_NAME, String TOLET_DETAILS, String ADDRESS,
                            String LATTITUDE, String LOGLITUTDE, String PRICE, String BATHS, String BEDS, String FLOORS,
                            String AVAILABLE_FROM, String CONTACT_PERSON_NM, String CONTACT_PERSON_PHN, String CONTACT_PERSON_EML,
                            String TOLET_TYPE_ID, String TOLET_TYPE_NAME, String CREATE_DATA, String CREATE_BY) {
        this.TOLET_INFO_ID = TOLET_INFO_ID;
        this.USER_ACC_ID = USER_ACC_ID;
        this.TOLET_NAME = TOLET_NAME;
        this.TOLET_DETAILS = TOLET_DETAILS;
        this.ADDRESS = ADDRESS;
        this.LATTITUDE = LATTITUDE;
        this.LOGLITUTDE = LOGLITUTDE;
        this.PRICE = PRICE;
        this.BATHS = BATHS;
        this.BEDS = BEDS;
        this.FLOORS = FLOORS;
        this.AVAILABLE_FROM = AVAILABLE_FROM;
        this.CONTACT_PERSON_NM = CONTACT_PERSON_NM;
        this.CONTACT_PERSON_PHN = CONTACT_PERSON_PHN;
        this.CONTACT_PERSON_EML = CONTACT_PERSON_EML;
        this.TOLET_TYPE_ID = TOLET_TYPE_ID;
        this.TOLET_TYPE_NAME = TOLET_TYPE_NAME;
        this.CREATE_DATA = CREATE_DATA;
        this.CREATE_BY = CREATE_BY;
    }

    public String getTOLET_INFO_ID() {
        return TOLET_INFO_ID;
    }

    public void setTOLET_INFO_ID(String TOLET_INFO_ID) {
        this.TOLET_INFO_ID = TOLET_INFO_ID;
    }

    public String getUSER_ACC_ID() {
        return USER_ACC_ID;
    }

    public void setUSER_ACC_ID(String USER_ACC_ID) {
        this.USER_ACC_ID = USER_ACC_ID;
    }

    public String getTOLET_NAME() {
        return TOLET_NAME;
    }

    public void setTOLET_NAME(String TOLET_NAME) {
        this.TOLET_NAME = TOLET_NAME;
    }

    public String getTOLET_DETAILS() {
        return TOLET_DETAILS;
    }

    public void setTOLET_DETAILS(String TOLET_DETAILS) {
        this.TOLET_DETAILS = TOLET_DETAILS;
    }

    public String getADDRESS() {
        return ADDRESS;
    }

    public void setADDRESS(String ADDRESS) {
        this.ADDRESS = ADDRESS;
    }

    public String getLATTITUDE() {
        return LATTITUDE;
    }

    public void setLATTITUDE(String LATTITUDE) {
        this.LATTITUDE = LATTITUDE;
    }

    public String getLOGLITUTDE() {
        return LOGLITUTDE;
    }

    public void setLOGLITUTDE(String LOGLITUTDE) {
        this.LOGLITUTDE = LOGLITUTDE;
    }

    public String getPRICE() {
        return PRICE;
    }

    public void setPRICE(String PRICE) {
        this.PRICE = PRICE;
    }

    public String getBATHS() {
        return BATHS;
    }

    public void setBATHS(String BATHS) {
        this.BATHS = BATHS;
    }

    public String getBEDS() {
        return BEDS;
    }

    public void setBEDS(String BEDS) {
        this.BEDS = BEDS;
    }

    public String getFLOORS() {
        return FLOORS;
    }

    public void setFLOORS(String FLOORS) {
        this.FLOORS = FLOORS;
    }

    public String getAVAILABLE_FROM() {
        return AVAILABLE_FROM;
    }

    public void setAVAILABLE_FROM(String AVAILABLE_FROM) {
        this.AVAILABLE_FROM = AVAILABLE_FROM;
    }

    public String getCONTACT_PERSON_NM() {
        return CONTACT_PERSON_NM;
    }

    public void setCONTACT_PERSON_NM(String CONTACT_PERSON_NM) {
        this.CONTACT_PERSON_NM = CONTACT_PERSON_NM;
    }

    public String getCONTACT_PERSON_PHN() {
        return CONTACT_PERSON_PHN;
    }

    public void setCONTACT_PERSON_PHN(String CONTACT_PERSON_PHN) {
        this.CONTACT_PERSON_PHN = CONTACT_PERSON_PHN;
    }

    public String getCONTACT_PERSON_EML() {
        return CONTACT_PERSON_EML;
    }

    public void setCONTACT_PERSON_EML(String CONTACT_PERSON_EML) {
        this.CONTACT_PERSON_EML = CONTACT_PERSON_EML;
    }

    public String getTOLET_TYPE_ID() {
        return TOLET_TYPE_ID;
    }

    public void setTOLET_TYPE_ID(String TOLET_TYPE_ID) {
        this.TOLET_TYPE_ID = TOLET_TYPE_ID;
    }

    public String getTOLET_TYPE_NAME() {
        return TOLET_TYPE_NAME;
    }

    public void setTOLET_TYPE_NAME(String TOLET_TYPE_NAME) {
        this.TOLET_TYPE_NAME = TOLET_TYPE_NAME;
    }


    public String getCREATE_DATA() {
        return CREATE_DATA;
    }

    public void setCREATE_DATA(String CREATE_DATA) {
        this.CREATE_DATA = CREATE_DATA;
    }

    public String getCREATE_BY() {
        return CREATE_BY;
    }

    public void setCREATE_BY(String CREATE_BY) {
        this.CREATE_BY = CREATE_BY;
    }
}
