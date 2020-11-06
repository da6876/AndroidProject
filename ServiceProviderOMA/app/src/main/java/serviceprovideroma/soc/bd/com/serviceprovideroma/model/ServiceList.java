package serviceprovideroma.soc.bd.com.serviceprovideroma.model;

public class ServiceList {
    String serviceName,serviceID,serviceStatus,serviceprice,serviceByName,serviceByStatus;

    public ServiceList(String serviceName, String serviceID, String serviceStatus, String serviceprice, String serviceByName, String serviceByStatus) {
        this.serviceName = serviceName;
        this.serviceID = serviceID;
        this.serviceStatus = serviceStatus;
        this.serviceprice = serviceprice;
        this.serviceByName = serviceByName;
        this.serviceByStatus = serviceByStatus;
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

    public String getServiceByStatus() {
        return serviceByStatus;
    }

    public void setServiceByStatus(String serviceByStatus) {
        this.serviceByStatus = serviceByStatus;
    }
}
