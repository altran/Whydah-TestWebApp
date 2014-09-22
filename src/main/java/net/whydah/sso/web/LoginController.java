package net.whydah.sso.web;

import net.whydah.sso.config.AppConfig;
import net.whydah.sso.web.util.SSOHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.QueryParam;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Controller
public class LoginController {
	private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    static final String USER_TOKEN_REFERENCE_NAME = "test_whydahusertoken";

    static String REDIRECT_TO_LOGIN_SERVICE; //"redirect:http://"+getHost()+":" + SSO_PORT + "/ssoHelper/login" + "?redirectURI=http://"+getHost()+":" + MY_PORT + "/test/hello";
    static String REDIRECT_TO_LOGOUT_SERVICE; // = "redirect:http://"+getHost()+":" + SSO_PORT + "/ssoHelper/logout" + "?redirectURI=http://"+getHost()+":" + MY_PORT + "/test/hello";
    static String LOGOUT_SERVICE; // = "http://"+getHost()+":" + SSO_PORT + "/ssoHelper/logoutaction" + "?redirectURI=http://"+getHost()+":" + MY_PORT + "/test/logout";

    private String myUri;
    private String ssoLoginWebappUri;
    private String tokenServiceUri;

    private final SSOHelper ssoHelper;


    public LoginController() {
    	try {
            myUri = AppConfig.readProperties().getProperty("myuri");
            ssoLoginWebappUri = AppConfig.readProperties().getProperty("logonserviceurl");
            tokenServiceUri = AppConfig.readProperties().getProperty("tokenservice");

            if (myUri == null || ssoLoginWebappUri == null || tokenServiceUri == null) {
                log.error("Urls not set correctly. Exiting. myUri={}, ssoLoginWebappUri={}, tokenServiceUri={}", myUri, ssoLoginWebappUri, tokenServiceUri);
                System.exit(1);
            }

	    	REDIRECT_TO_LOGIN_SERVICE = "redirect:" + ssoLoginWebappUri + "login?redirectURI=" + myUri + "hello";;
	    	REDIRECT_TO_LOGOUT_SERVICE = "redirect:" + ssoLoginWebappUri + "logout?redirectURI=" + myUri + "hello";
	    	LOGOUT_SERVICE = ssoLoginWebappUri + "logoutaction?redirectURI=" + myUri + "logout";
	    	log.debug("REDIRECT_TO_LOGIN_SERVICE: {}", REDIRECT_TO_LOGIN_SERVICE);
            log.debug("REDIRECT_TO_LOGOUT_SERVICE: {}", REDIRECT_TO_LOGOUT_SERVICE);
            log.debug("LOGOUT_SERVICE: {}", LOGOUT_SERVICE);
    	} catch (IOException e) {
			log.error("Unable to read properties from file.");
			throw new RuntimeException("Unable to read properties", e);
		}

        log.info("LoginController initialized ok. myUri={}, ssoLoginWebappUri={}, tokenServiceUri={}", myUri, ssoLoginWebappUri, tokenServiceUri);
        log.info("Try the service at {}", myUri + "/hello");
        ssoHelper = new SSOHelper(tokenServiceUri);
    }

    @RequestMapping("/hello")
    public String hello(@QueryParam("userticket") String userticket, HttpServletRequest request, HttpServletResponse response, Model model) {
        String applicationTokenXml = ssoHelper.logonApplication();

        //String userTokenID = request.getParameter(USER_TOKEN_REFERENCE_NAME);
        if (userticket != null && userticket.length() > 3) {
            model.addAttribute("greeting", "Resolving user from userticket!\n");
            log.debug("Looking for userticket (URL param):" + userticket);
            String userToken = ssoHelper.getUserTokenByTicket(applicationTokenXml, userticket);
            if (userToken.length() > 10) {
                model.addAttribute("usertoken", userToken);
                model.addAttribute("logouturl",  LOGOUT_SERVICE);
                model.addAttribute("realname", getRealName(userToken));
                return "hello";
            } else {
                return REDIRECT_TO_LOGIN_SERVICE;
            }
        } else  if (hasRightCookie(request)) {
            model.addAttribute("greeting", "Resolving user from Cookie!\n");
            String userTokenIDFromCookie = getUserTokenIdFromCookie(request);
            String userToken = ssoHelper.getUserTokenByTokenID(ssoHelper.logonApplication(), userTokenIDFromCookie);
                log.debug("Looking for userTokenID (Cookie):" + userToken);

                if (userToken.length() > 10) {
                    model.addAttribute("usertoken", userToken);
                    model.addAttribute("logouturl", LOGOUT_SERVICE);
                    model.addAttribute("realname", getRealName(userToken));
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
        //cookie.setMaxAge(new Long(Long.parseLong(getTokenMaxAge(10000 + ssoHelper.getUserToken())) - System.currentTimeMillis()).intValue()); // set
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
                printDebugInfo(addr);

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

	private static void printDebugInfo(InetAddress addr) {
		log.debug("addr.getHostAddress() = " + addr.getHostAddress());
        log.debug("addr.getHostName() = " + addr.getHostName());
        log.debug("addr.isAnyLocalAddress() = " + addr.isAnyLocalAddress());
        log.debug("addr.isLinkLocalAddress() = " + addr.isLinkLocalAddress());
        log.debug("addr.isLoopbackAddress() = " + addr.isLoopbackAddress());
        log.debug("addr.isMulticastAddress() = " + addr.isMulticastAddress());
        log.debug("addr.isSiteLocalAddress() = " + addr.isSiteLocalAddress());
        log.debug("");
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
