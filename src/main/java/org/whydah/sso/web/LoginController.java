package org.whydah.sso.web;

import org.whydah.sso.web.util.SSOHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Controller
public class LoginController {
    private final SSOHelper sso = new SSOHelper();
    static String USER_TOKEN_REFERENCE_NAME = "usertokenid";
    static String REDIRECT_TO_LOGIN_SERVICE = "redirect:http://"+getHost()+":9997/sso/login" + "?redirectURI=http://"+getHost()+":9990/test/hello";
    static String REDIRECT_TO_LOGOUT_SERVICE = "redirect:http://"+getHost()+":9997/sso/logout" + "?redirectURI=http://"+getHost()+":9990/test/hello";
    static String LOGOUT_SERVICE = "http://"+getHost()+":9997/sso/logoutaction" + "?redirectURI=http://"+getHost()+":9990/test/logout";

    @RequestMapping("/hello")
    public String hello(HttpServletRequest request, HttpServletResponse response, Model model) {

        String userTokenID = request.getParameter(USER_TOKEN_REFERENCE_NAME);
        if (userTokenID != null && userTokenID.length() > 3) {
            System.out.println("Looking for userTokenID (URL param):" + userTokenID);
            if (sso.getUserToken(userTokenID).length() > 10) {
                model.addAttribute("token", sso.getUserToken(userTokenID));
                model.addAttribute("logouturl",  LOGOUT_SERVICE);
                model.addAttribute("realname",getRealName(sso.getUserToken(userTokenID)));
                return "hello";
            } else {
                removeUserTokenCookie(request, response);
                return REDIRECT_TO_LOGIN_SERVICE;
            }
        }

        if (hasRightCookie(request)) {
            model.addAttribute("greeting", "Hello world!\n");
            System.out.println("Looking for userTokenID (Cookie):" + sso.getUserToken(getUserTokenIdFromCookie(request)));
            if (sso.getUserToken(getUserTokenIdFromCookie(request)).length() > 10) {
                model.addAttribute("token", sso.getUserToken(getUserTokenIdFromCookie(request)));
                model.addAttribute("logouturl",  LOGOUT_SERVICE);
                model.addAttribute("realname",getRealName(sso.getUserToken(userTokenID)));
                return "hello";
            } else {
                removeUserTokenCookie(request, response);
                return REDIRECT_TO_LOGIN_SERVICE;
            }
        }
        return REDIRECT_TO_LOGIN_SERVICE;
    }

    @RequestMapping("/logout")
    public String logut(HttpServletRequest request, HttpServletResponse response, Model model) {
         return "logout";
    }

    @RequestMapping("/locallogout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Model model) {
        String userTokenId = getUserTokenIdFromCookie(request);

        Cookie cookie = new Cookie(USER_TOKEN_REFERENCE_NAME, "localhost");
        cookie.setValue(USER_TOKEN_REFERENCE_NAME);
        //cookie.setMaxAge(new Long(Long.parseLong(getTokenMaxAge(10000 + sso.getUserToken())) - System.currentTimeMillis()).intValue()); // set
        cookie.setMaxAge(100000);
        //cookie.setDomain("localhost");
        cookie.setValue("");
        response.addCookie(cookie);
        return REDIRECT_TO_LOGOUT_SERVICE + '&' + USER_TOKEN_REFERENCE_NAME + '=' + userTokenId;
    }

    @RequestMapping("/action")
    public String action(HttpServletRequest request, HttpServletResponse response, Model model) {
        model.addAttribute("greeting", "Hello world!\n");

        return "action";
    }


    public static String getHost() {
        String host = "localhost";
        try {
            String hostName = InetAddress.getLocalHost().getHostName();

            InetAddress addrs[] = InetAddress.getAllByName(hostName);

            String myIp = null;
            for (InetAddress addr : addrs) {
                //System.out.println("addr.getHostAddress() = " + addr.getHostAddress());
                //System.out.println("addr.getHostName() = " + addr.getHostName());
                //System.out.println("addr.isAnyLocalAddress() = " + addr.isAnyLocalAddress());
                //System.out.println("addr.isLinkLocalAddress() = " + addr.isLinkLocalAddress());
                //System.out.println("addr.isLoopbackAddress() = " + addr.isLoopbackAddress());
                //System.out.println("addr.isMulticastAddress() = " + addr.isMulticastAddress());
                //System.out.println("addr.isSiteLocalAddress() = " + addr.isSiteLocalAddress());
                //System.out.println("");

                if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress()) {
                    myIp = addr.getHostAddress();
                }
            }
            // System.out.println("\nIP = " + myIp);
            if(myIp != null)
                host = myIp;
        } catch (UnknownHostException e) {
        }
        return host;
    }

    private void removeUserTokenCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = getUserTokenCookie(request);
        if(cookie != null) {
            cookie.setValue(USER_TOKEN_REFERENCE_NAME);
            cookie.setMaxAge(0);
            cookie.setValue("");
            response.addCookie(cookie);
        }
    }

    private Cookie getUserTokenCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        System.out.println("=============> header: " + cookies);
        if (cookies == null) {
            return null;
        }

        for (Cookie cooky : cookies) {
            System.out.println("Cookie: " + cooky.getName());
            if (cooky.getName().equalsIgnoreCase(USER_TOKEN_REFERENCE_NAME)) {
                return cooky;
            }
        }
        return null;
    }

    private boolean hasRightCookie(HttpServletRequest request) {
        return getUserTokenCookie(request) != null;
    }

    private String getUserTokenIdFromCookie(HttpServletRequest request) {
        Cookie cookie = getUserTokenCookie(request);
        if(cookie != null)
            return cookie.getValue();
        else
            return null;
    }

    private String getTokenId(String userTokenXml) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(userTokenXml)));
            XPath xPath = XPathFactory.newInstance().newXPath();

            String expression = "/token/@id";
            XPathExpression xPathExpression =
                    xPath.compile(expression);
            return (xPathExpression.evaluate(doc));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    private String getRealName(String userTokenXml) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(userTokenXml)));
            XPath xPath = XPathFactory.newInstance().newXPath();

            String  expression = "/token/fornavn[1]";
            XPathExpression xPathExpression =
                    xPath.compile(expression);
            String fornavn = (xPathExpression.evaluate(doc));
            expression = "/token/etternavn[1]";
            xPathExpression =
                    xPath.compile(expression);
            String etternavn = (xPathExpression.evaluate(doc));
            return fornavn+" "+etternavn;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    private String getTokenMaxAge(String userTokenXml) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(userTokenXml)));
            XPath xPath = XPathFactory.newInstance().newXPath();

            String expression = "/token/timestamp[1]";
            XPathExpression xPathExpression =
                    xPath.compile(expression);
            return (xPathExpression.evaluate(doc));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


}
