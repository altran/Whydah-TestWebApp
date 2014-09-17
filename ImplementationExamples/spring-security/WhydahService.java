package no.nkk.judgedirectory.web.security;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.thoughtworks.xstream.XStream;
import org.springframework.beans.factory.annotation.Value;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Created by gunnar on 2/17/14.
 */
public class WhydahService {

	@Value("${whydah.ssoBaseUrl:http://sso.test.no/sso}")
	private String ssoUrl;

	@Value("${whydah.ssoBaseUrl:http://sso.test.no/tokenservice}")
	private String tokeservice;

	@Value("${whydah.applicationId:JudgeDirectory}")
	private String applicationId;

	@Value("${whydah.applicationSecret:secret}")
	private String applicationSecret;

	private Client restClient = Client.create();

	protected WhydahLogonToken applicationLogon() {
		WebResource restResource = restClient.resource(tokeservice + "/logon");
		WebResource.Builder builder = restResource.accept(MediaType.APPLICATION_XML).type(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
		WhydahApplicationcredential applicationcredential = new WhydahApplicationcredential(applicationId, applicationSecret);
		MultivaluedMap formData = new MultivaluedMapImpl();
		formData.add("applicationcredential", new XStream().toXML(applicationcredential));
		return builder.post(WhydahLogonToken.class, formData);
	}

	protected WhydahUserToken getUserToken(WhydahLogonToken applicationToken, String ticket) {
		WebResource restResource = restClient.resource(String.format("%s/user/%s/get_usertoken_by_userticket", tokeservice, applicationToken.getApplicationtoken()));
		WebResource.Builder builder = restResource.accept(MediaType.APPLICATION_XML).type(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
		MultivaluedMap formData = new MultivaluedMapImpl();
		formData.add("apptoken", new XStream().toXML(applicationToken));
		formData.add("userticket", ticket);
		return builder.post(WhydahUserToken.class, formData);
//		String data = builder.post(String.class, formData);
//		return null;
	}

	public String getSsoUrl() {
		return ssoUrl;
	}
}
