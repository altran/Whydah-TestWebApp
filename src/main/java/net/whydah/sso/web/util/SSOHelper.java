package net.whydah.sso.web.util;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import net.whydah.sso.config.AppConfig;
import net.whydah.sso.web.data.ApplicationCredential;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLHandshakeException;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;

public class SSOHelper {
    private static final Logger log = LoggerFactory.getLogger(SSOHelper.class);
    final URI BASE_URI;
    private WebResource tokenServiceResource;
    private final HttpClient httpClient;
    private String applicationid;
    private String applicationname;
    private String applicationsecret;
    private ApplicationCredential applicationCredential;

    public SSOHelper(String tokenServiceUri) {

        SSLTool.disableCertificateValidation();
        BASE_URI = UriBuilder.fromUri(tokenServiceUri).build();
        Client c = Client.create();
        tokenServiceResource = c.resource(BASE_URI);
        httpClient = new HttpClient();
        try {
            applicationname = AppConfig.readProperties().getProperty("applicationname");
            applicationid = AppConfig.readProperties().getProperty("applicationid");
            applicationsecret = AppConfig.readProperties().getProperty("applicationsecret");
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getLocalizedMessage(), e);
        }
        applicationCredential = new ApplicationCredential();
        applicationCredential.setApplicationID(applicationid);
        applicationCredential.setApplicationPassord(applicationsecret);
        applicationCredential.setApplicationName(applicationname);
    }


    public String logonApplication() {
        WebResource logonResource = tokenServiceResource.path("/logon");
        PostMethod postMethod = new PostMethod(logonResource.toString());
        postMethod.addParameter(RequestHelper.APPLICATIONCREDENTIAL, applicationCredential.toXML());

        String targetUrl = logonResource.getURI().toString();
        try {
            int responseCode = httpClient.executeMethod(postMethod);
            String responseAsString = postMethod.getResponseBodyAsString();
            log.trace("ResponseCode={} when executing {}, ApplicationToken: \n {}  ApplicationCredential: {}", responseCode, targetUrl, responseAsString, applicationCredential.toXML());
            return responseAsString;
        } catch (ConnectException ce) {
            log.error("logonApplication failed. targetUrl={}, ConnectException: {}", targetUrl, ce.getMessage());
        } catch (SSLHandshakeException sslE) {
            log.error("logonApplication failed. targetUrl={}, SSLHandshakeException: {}", targetUrl, sslE.getMessage());
        } catch (IOException ioe) {
            log.error("logonApplication failed. targetUrl={}", targetUrl, ioe);
        } finally {
            postMethod.releaseConnection();
        }
        return null;
    }

    public String getUserTokenByTicket(String appTokenXML, String userticket) {
        if (appTokenXML == null || appTokenXML.isEmpty()) {
            throw new IllegalArgumentException("appTokenXML cannot be null or empty!");
        }
        if (userticket == null || userticket.isEmpty()) {
            throw new IllegalArgumentException("userticket cannot be null or empty!");
        }
        String applicationTokenId = XpathHelper.getAppTokenIdFromAppToken(appTokenXML);
        String path = tokenServiceResource.path("user/").toString() + applicationTokenId + "/get_usertoken_by_userticket"; // webResource.path("/iam/")
        PostMethod postMethod = new PostMethod(path);
        postMethod.addParameter(RequestHelper.APPTOKEN, appTokenXML);
        postMethod.addParameter(RequestHelper.USERTICKET, userticket);
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
        if (appTokenXML == null || appTokenXML.isEmpty()) {
            throw new IllegalArgumentException("appTokenXML cannot be null or empty!");
        }
        if (usertokenID == null || usertokenID.isEmpty()) {
            throw new IllegalArgumentException("usertokenID cannot be null or empty!");
        }

        String applicationTokenId = XpathHelper.getAppTokenIdFromAppToken(appTokenXML);
        String path = tokenServiceResource.path("user/").toString() + applicationTokenId + "/get_usertoken_by_usertokenid"; // webResource.path("/iam/")
        PostMethod postMethod = new PostMethod(path);
        postMethod.addParameter(RequestHelper.APPTOKEN, appTokenXML);
        postMethod.addParameter(RequestHelper.USERTOKENID, usertokenID);
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


}

