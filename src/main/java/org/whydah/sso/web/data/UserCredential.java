package org.whydah.sso.web.data;

public class UserCredential {

    private String userName;
    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String toXML(){
        if (userName== null){
            return templateToken;
        } else {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?> \n " +
            "<usercredential>\n" +
            "    <params>\n" +
            "        <username>"+getUserName()+"</username>\n" +
            "        <password>"+getPassword()+"</password>\n" +
            "    </params> \n" +
            "</usercredential>\n" ;
        }
    }

    String templateToken = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?> \n " +

            "    <usercredential>\n" +
            "        <params>\n" +
            "            <username>"+getUserName()+"</username>\n" +
            "            <password>"+getPassword()+"</password>\n" +
            "        </params> \n" +
            "    </usercredential>\n" +
            "";

}
