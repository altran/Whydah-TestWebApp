package net.whydah.sso.web.util;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import net.whydah.sso.config.AppConfig;
import net.whydah.sso.web.data.ApplicationCredential;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

public class SSOHelper {
    private static final Logger log = LoggerFactory.getLogger(SSOHelper.class);
    final URI BASE_URI;

    private WebResource webResource;
    private final HttpClient httpClient;
    private String applicationid;
    private String applicationsecret;

    public SSOHelper(String tokenServiceUri) {
        BASE_URI = UriBuilder.fromUri(tokenServiceUri).build();
        Client c = Client.create();
        webResource = c.resource(BASE_URI);
        httpClient = new HttpClient();
    }


    public String logonApplication() {
        try {
            applicationid = AppConfig.readProperties().getProperty("applicationid");
            applicationsecret = AppConfig.readProperties().getProperty("applicationsecret");
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getLocalizedMessage(), e);
        }
        ApplicationCredential applicationCredential = new ApplicationCredential();
        applicationCredential.setApplicationID(applicationid);
        applicationCredential.setApplicationPassord(applicationsecret);

        String path = webResource.path("/logon").toString();
        PostMethod postMethod = new PostMethod(path);
        postMethod.addParameter("applicationcredential", applicationCredential.toXML());

        try {
            int responseCode = httpClient.executeMethod(postMethod);
            String responseAsString = postMethod.getResponseBodyAsString();

            log.debug("ResponseCode={} when executing {}, ApplicationToken: \n {}  ApplicationCredential: {}", responseCode, path, responseAsString, applicationCredential.toXML());
            /*
            if (responseCode == 201) {
                log.debug("Post" + postMethod.getRequestHeader("Location").getValue());
            }
            if (responseCode == 400) {
                log.debug("Internal error");
            }
            if (responseCode == 406) {
                log.debug("Not accepted");
            }
            if (responseCode == 410) {
                log.debug("Gone");  // check cookie
            }
            if (responseCode == 500 || responseCode == 501) {
                log.debug("Internal error");
                // retry
            }
            */
            return responseAsString;
        } catch (IOException ioe) {
            log.error("logonApplication failed", ioe);
            ioe.printStackTrace();
        } finally {
            postMethod.releaseConnection();
        }
        return null;
    }

    public String getUserTokenByTicket(String appTokenXML, String userticket) {
        if (userticket == null) {
            throw new IllegalArgumentException("userticket cannot be null!");
        }

        String applicationTokenId = XpathHelper.getAppTokenIdFromAppToken(appTokenXML);

        String path = webResource.path("user/").toString() + applicationTokenId + "/get_usertoken_by_userticket"; // webResource.path("/iam/")
        PostMethod postMethod = new PostMethod(path);
        postMethod.addParameter("apptoken", appTokenXML);
        postMethod.addParameter("userticket", userticket);
        log.trace("Executing get_usertoken_by_userticket, path={}, appToken={}, userticket={}", path, appTokenXML, userticket);


        try {
            int responseCode = httpClient.executeMethod(postMethod);
            String userTokenXml = postMethod.getResponseBodyAsString();
            log.trace("Executed get_usertoken_by_userticket, responseCode={}, userToken=\n{}", responseCode, userTokenXml);
            return userTokenXml;
        } catch (IOException ioe) {
            log.error("get_usertoken_by_userticket failed", ioe);
        } finally {
            postMethod.releaseConnection();
        }
        return null;
    }

    public String getUserTokenByTokenID(String appTokenXML, String usertokenID) {
        if (usertokenID == null) {
            throw new IllegalArgumentException("usertokenID cannot be null!");
        }

        String applicationTokenId = XpathHelper.getAppTokenIdFromAppToken(appTokenXML);

        String path = webResource.path("user/").toString() + applicationTokenId + "/get_usertoken_by_usertokenid"; // webResource.path("/iam/")
        PostMethod postMethod = new PostMethod(path);
        postMethod.addParameter("apptoken", appTokenXML);
        postMethod.addParameter("usertokenid", usertokenID);
        log.trace("Executing get_usertoken_by_usertokenid, path={}, appToken={}, usertokenid={}", path, appTokenXML, usertokenID);


        try {
            int responseCode = httpClient.executeMethod(postMethod);
            String userTokenXml = postMethod.getResponseBodyAsString();
            log.trace("Executed get_usertoken_by_usertokenid, responseCode={}, userToken=\n{}", responseCode, userTokenXml);
            return userTokenXml;
        } catch (IOException ioe) {
            log.error("get_usertoken_by_usertokenid failed", ioe);
        } finally {
            postMethod.releaseConnection();
        }
        return null;
    }

    /*
    public void logonApplication() {
        PostMethod p = setUpApplicationLogon();
        HttpClient c = new HttpClient();
        try {
            int v = c.executeMethod(p);
            if (v == 201) {
                log.debug("Post" + p.getRequestHeader("Location").getValue());
            }
            if (v == 400) {
                log.debug("Internal error");
            }
            if (v == 500 || v == 501) {
                log.debug("Internal error");
// retry
            }
            //System.out.println(p.getResponseBodyAsStream());
        } catch (IOException ioe) {
            log.error("", ioe);
        } finally {
            p.releaseConnection();
        }
    }
    private PostMethod setUpApplicationLogon() {
        //TODO baardl Implement application credential
        String requestXML = "";
        PostMethod p = new PostMethod(webResource.path("/logon").toString());
        p.addParameter("applicationcredential",requestXML);
        return p;
    }
    */
}

