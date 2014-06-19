package org.whydah.sso.web.util;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import org.whydah.sso.web.data.ApplicationCredential;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

public class SSOHelper {
    private static final URI BASE_URI = UriBuilder.fromUri("https://sso.altran.se/tokenservice/").port(443).build(); //TODO: DO NOT HARDCODE
    private WebResource r;

    public SSOHelper(){
        Client c = Client.create();
        r = c.resource(BASE_URI);
    }

    public void logonApplication() {
        PostMethod p = setUpApplicationLogon();
        HttpClient c = new HttpClient();
        try {
            int v = c.executeMethod(p);
            if (v == 201) {
                System.out.println("Post" + p.getRequestHeader("Location").getValue());
            }
            if (v == 400) {
                System.out.println("Internal error");
            }
            if (v == 500 || v == 501) {
                System.out.println("Internal error");
// retry
            }
            System.out.println(p.getResponseBodyAsStream());

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            p.releaseConnection();
        }
    }

    private PostMethod setUpApplicationLogon() {
        //TODO baardl Implement application credential
        String requestXML = "";
        PostMethod p = new PostMethod(r.path("/logon").toString());
        p.addParameter("applicationcredential",requestXML);
        return p;
    }

    public String getUserToken(String usertokenid) {
        if (usertokenid==null){
            usertokenid="dummy";
        }
        PostMethod p = setupRealApplicationLogon();
        HttpClient c = new HttpClient();
        try {
            int v = c.executeMethod(p);
            if (v == 201) {
                System.out.println("Post" + p.getRequestHeader("Location").getValue());
            }
            if (v == 400) {
                System.out.println("Internal error");
            }
            if (v == 406) {
                System.out.println("Not accepted");
            }
            if (v == 500 || v == 501) {
                System.out.println("Internal error");
// retry
            }
            System.out.println("ApplicationToken:"+p.getResponseBodyAsStream());
            PostMethod p2 = setUpGetUserToken(p,usertokenid);
            v = c.executeMethod(p2);
            if (v == 201) {
                System.out.println("Post" + p2.getRequestHeader("Location").getValue());
            }
            if (v == 400 || v == 404 ) {
                System.out.println("Internal error");
            }
            if (v == 406) {
                System.out.println("Not accepted");
            }
            if (v == 415 ) {
                System.out.println("Internal error, unsupported media type");
            }
            if (v == 500 || v == 501) {
                System.out.println("Internal error");// retry
            }
//            System.out.println("Request:"+p2.
            System.out.println("v:"+v);
            System.out.println("Response:"+p2.getResponseBodyAsStream());
            return p2.getResponseBodyAsStream().toString();


        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            p.releaseConnection();
        }
        return null;
    }


    private PostMethod setUpGetUserToken(PostMethod p,String userTokenid) throws IOException {

        String appTokenXML = p.getResponseBodyAsString();
        String appid = appTokenXML.substring(appTokenXML.indexOf("<applicationtokenID>") + "<applicationtokenID>".length(), appTokenXML.indexOf("</applicationtokenID>"));
        String path = r.path("/iam/").toString() + appid + "/getusertokenbyticket"; // r.path("/iam/")


        System.out.println("POST:"+path);

        PostMethod p2 = new PostMethod(path);
        p2.addParameter("apptoken",appTokenXML);
        p2.addParameter("ticket",userTokenid);

        System.out.println("apptoken:"+appTokenXML);
        System.out.println("usertokenid:"+userTokenid);
        return p2;
    }

    private PostMethod setupRealApplicationLogon() {
        ApplicationCredential acred = new ApplicationCredential();
        acred.setApplicationID("SSOTestWebApp");
        acred.setApplicationPassord("dummy");


        PostMethod p = new PostMethod(r.path("/logon").toString());
        p.addParameter("applicationcredential",acred.toXML());
        return p;
    }
}

