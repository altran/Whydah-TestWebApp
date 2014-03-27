package no.nkk.judgedirectory.web.security;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by gunnar on 2/17/14.
 */
@XmlRootElement(name = "applicationcredential")
public class WhydahApplicationcredential {

	private WhydahApplicationcredentialParams params;

	public WhydahApplicationcredential(String applicationID, String applicationSecret) {
		this.params = new WhydahApplicationcredentialParams();
		this.params.setApplicationID(applicationID);
		this.params.setApplicationSecret(applicationSecret);
	}

	protected WhydahApplicationcredential() {
	}

	@XmlElement(name = "params")
	protected WhydahApplicationcredentialParams getParams() {
		return params;
	}

	protected void setParams(WhydahApplicationcredentialParams params) {
		this.params = params;
	}

	private static class WhydahApplicationcredentialParams {
		private String applicationID, applicationSecret;

		public String getApplicationID() {
			return applicationID;
		}

		public void setApplicationID(String applicationID) {
			this.applicationID = applicationID;
		}

		public String getApplicationSecret() {
			return applicationSecret;
		}

		public void setApplicationSecret(String applicationSecret) {
			this.applicationSecret = applicationSecret;
		}
	}
}
