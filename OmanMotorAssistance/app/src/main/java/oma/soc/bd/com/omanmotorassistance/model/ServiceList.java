package oma.soc.bd.com.omanmotorassistance.model;

public class ServiceList {
    String serviceName,serviceID,serviceStatus,serviceprice,serviceByName, serviceByID;

    public ServiceList(String serviceName, String serviceID, String serviceStatus, String serviceprice, String serviceByName, String serviceByID) {
        this.serviceName = serviceName;
        this.serviceID = serviceID;
        this.serviceStatus = serviceStatus;
        this.serviceprice = serviceprice;
        this.serviceByName = serviceByName;
        this.serviceByID = serviceByID;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceID() {
        return serviceID;
    }

    public void setServiceID(String serviceID) {
        this.serviceID = serviceID;
    }

    public String getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public String getServiceprice() {
        return serviceprice;
    }

    public void setServiceprice(String serviceprice) {
        this.serviceprice = serviceprice;
    }

    public String getServiceByName() {
        return serviceByName;
    }

    public void setServiceByName(String serviceByName) {
        this.serviceByName = serviceByName;
    }

    public String getServiceByID() {
        return serviceByID;
    }

    public void setServiceByID(String serviceByID) {
        this.serviceByID = serviceByID;
    }
}
