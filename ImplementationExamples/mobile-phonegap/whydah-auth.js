var loginServiceUrl = 'http://mobilefirst.no:9997/sso/login';
var tokenServiceUrl = 'http://mobilefirst.no:9998/tokenservice';
var appId = "Mobilefirst Frontend";
var appSecret = "muy4ahTi";

window.phonegap = true;
/*
 $.getScript("cordova.js",
 function() {
 window.phonegap = true;
 });
 */

function logonApplication() {
    var url = tokenServiceUrl + "/logon/";
    console.log("logonApplication with url " + url);

    var appCred = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
    appCred = appCred + "<applicationcredential>";
    appCred = appCred + "<params>";
    appCred = appCred + "<applicationID>" + appId + "</applicationID>";
    appCred = appCred + "<applicationSecret>" + appSecret + "</applicationSecret>";
    appCred = appCred + "</params>";
    appCred = appCred + "</applicationcredential>";

    var req = new XMLHttpRequest();
    req.open("POST", url, false);
    req.setRequestHeader('User-Agent','XMLHTTP/1.0');
    req.setRequestHeader('Content-type','application/x-www-form-urlencoded');

    req.send("applicationcredential=" + appCred);
    console.log("appTokenXml (XML from response): " + req.responseXML);
    return req.responseXML;
}

function getTokenIdFromAppToken(appTokenXML) {
    //Assume only one element named applicationtoken
    var node = appTokenXML.getElementsByTagName("applicationtoken")[0];
    var appTokenId = node.childNodes[0].nodeValue;   //Yes, this is the appTokenId
    console.log("appTokenId=" + appTokenId);
    return appTokenId;
}

function getUsertokenWithTicket(userticket) {
    var appTokenXml = logonApplication();
    var myAppTokenId = getTokenIdFromAppToken(appTokenXml);
    console.log("myAppTokenId=" + myAppTokenId);

    var url = tokenServiceUrl + "/iam/" + myAppTokenId + "/getusertokenbyticketid";
    var req = new XMLHttpRequest();
    req.open("POST", url, false);
    req.setRequestHeader('User-Agent','XMLHTTP/1.0');
    req.setRequestHeader('Content-type','application/x-www-form-urlencoded');

    var appTokenXmlAsString ='';
    if (appTokenXml.xml) {
        appTokenXmlAsString = appTokenXml.xml; // IE
    } else {
        appTokenXmlAsString = (new XMLSerializer()).serializeToString(appTokenXml); // Mozilla
    }

    console.log("getUsertoken using url " + url + ", apptoken=" + appTokenXmlAsString + ", ticketid=" + userticket);
    req.send("apptoken=" + appTokenXmlAsString + "&ticketid=" + userticket);
    console.log("usertoken (XML from response): " + req.responseText);
    return req.responseText;
}

function getUsertokenWithTokenId(tokenId) {
    var appTokenXml = logonApplication();
    var myAppTokenId = getTokenIdFromAppToken(appTokenXml);
    console.log("myAppTokenId=" + myAppTokenId);

    var url = tokenServiceUrl + "/iam/" + myAppTokenId + "/getusertokenbytokenid";
    var req = new XMLHttpRequest();
    req.open("POST", url, false);
    req.setRequestHeader('User-Agent','XMLHTTP/1.0');
    req.setRequestHeader('Content-type','application/x-www-form-urlencoded');

    var appTokenXmlAsString ='';
    if (appTokenXml.xml) {
        appTokenXmlAsString = appTokenXml.xml; // IE
    } else {
        appTokenXmlAsString = (new XMLSerializer()).serializeToString(appTokenXml); // Mozilla
    }

    console.log("getUsertoken using url " + url + ", apptoken=" + appTokenXmlAsString + ", tokenId=" + tokenId);
    req.send("apptoken=" + appTokenXmlAsString + "&tokenId=" + tokenId);
    console.log("usertoken (XML from response): " + req.responseText);
    return req.responseText;
}



function updateAppStateAfterLogin(userTokenXMLString) {
    var parser = new DOMParser();
    var doc = parser.parseFromString(userTokenXMLString,'text/xml');

    token_id = doc.documentElement.getAttribute("id");
    localStorage.mobilefirst_usertoken_id = token_id;
    console.log("token_id updated. token_id=" + token_id);

    myHost = myProductHost + token_id + "/";
    localStorage.mobilefirst_myHost = myHost;
    console.log("myHost updated. myHost=" + myHost);


    var node = doc.getElementsByTagName("uid")[0];
    var whydah_id = node.childNodes[0].nodeValue;
    //localStorage.mobilefirst_whydah_id = whydah_id;
    console.log("getTokenIdFromUserToken found whydah_id=" + whydah_id);


    var url = myHost + "memberid?whydahId=" + whydah_id;
    var req = new XMLHttpRequest();
    req.open("GET", url, false);
    req.setRequestHeader('User-Agent','XMLHTTP/1.0');
    req.setRequestHeader('Content-type','text/plain');
    console.log("get member_id using url " + url);
    req.send();

    var memberIdFromResponse = req.responseText;
    console.log("memberIdFromResponse=" + memberIdFromResponse);
    if (memberIdFromResponse && memberIdFromResponse.length > 10) {
        member_id = memberIdFromResponse;
        localStorage.mobilefirst_member_id = member_id;
    } else {
        console.log("member_id was not updated correctly. memberIdFromResponse=" + memberIdFromResponse);
        logout();
        authenticate();
    }
    gApp.appStart();
}


function authenticate() {
    if (!window.phonegap) {
        console.log("Not on a mobile device. Skip authentication.");
        return;
    }
    //TODO Enable when everything works
    /*
     if (localStorage.mobilefirst_usertoken && localStorage.mobilefirst_usertoken.length > 10) {
     console.log("mobilefirst_usertoken already exist. Skip authentication.");
     return;
     }
     */

    if (window.plugins.childBrowser == null) {
        console.log("Installing childBrowser");
        ChildBrowser.install();
    }

    console.log("Redirect with ChildBrowser to " + loginServiceUrl);
    window.plugins.childBrowser.showWebPage(loginServiceUrl);

    window.plugins.childBrowser.onLocationChange = function(loc){
        console.log("Whydah Auth: onLocationChange : " + loc);

        if (loc.indexOf('/welcome?userticket=') > 0) {
            window.plugins.childBrowser.close();

            var userticket = loc.substring(loc.indexOf("=") + 1);
            console.log("User ticket: " + userticket);


            userToken = getUsertokenWithTicket(userticket);
            localStorage.mobilefirst_usertoken = userToken;
            console.log("mobilefirst_usertoken stored in localStorage: " + localStorage.mobilefirst_usertoken);

            updateAppStateAfterLogin(userToken);
        } else if (loc.indexOf('/welcome?usertokenid=') > 0) {
            window.plugins.childBrowser.close();
            var usertokenid = loc.substring(loc.indexOf("=") + 1);
            console.log("Already logged in. usertokenid: " + usertokenid);

            //if new tokenId, fetch from tokenService.
            //TODO Fetch token using tokenId if token not found in localStorage.
            userToken = getUsertokenWithTokenId(usertokenid);
            localStorage.mobilefirst_usertoken = userToken;
            console.log("mobilefirst_usertoken stored in localStorage: " + localStorage.mobilefirst_usertoken);

            /*
            if (!localStorage.mobilefirst_usertoken || localStorage.mobilefirst_usertoken.length < 10) {
                console.log("localStorage.mobilefirst_usertoken not set. Should fetch token from tokenService.");
            }
            */

            updateAppStateAfterLogin(userToken);

            /*
             if (localStorage.mobilefirst_usertoken && !token_id) {

             }
             */

            //console.log("The ELSE IF clause was triggered");
        } else {
            //console.log("The ELSE clause was triggered");
        }
    };
}


function logout() {
    if (localStorage.mobilefirst_member_id) {
        localStorage.removeItem(mobilefirst_member_id);
    }
    if (localStorage.mobilefirst_myHost) {
        localStorage.removeItem(mobilefirst_myHost);
    }

    if (localStorage.mobilefirst_usertoken) {
        localStorage.removeItem(mobilefirst_usertoken);
    }
    //localStorage.clear();
}
