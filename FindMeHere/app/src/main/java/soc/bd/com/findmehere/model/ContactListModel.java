package soc.bd.com.findmehere.model;

public class ContactListModel {
    private String Name,PhoneNumber;


    public ContactListModel(String Name, String PhoneNumber) {
        this.Name = Name;
        this.PhoneNumber = PhoneNumber;
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

}
