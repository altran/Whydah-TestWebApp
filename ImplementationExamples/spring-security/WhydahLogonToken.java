package no.nkk.judgedirectory.web.security;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Created by gunnar on 2/17/14.
 */
@XmlRootElement(name = "token")
public class WhydahLogonToken {

	private WhydahTokenParams params;

	protected WhydahLogonToken() {
	}

	@XmlElement(name = "params")
	protected WhydahTokenParams getParams() {
		return params;
	}

	protected void setParams(WhydahTokenParams params) {
		this.params = params;
	}


	public String getApplicationtoken() {
		return params == null ? null : params.applicationtoken;
	}

	public String getApplicationid() {
		return params == null ? null : params.applicationid;
	}

	public String getApplicationname() {
		return params == null ? null : params.applicationname;
	}

	public Date getExpires() {
		return params == null ? null : params.expires;
	}

	public boolean isExpired() {
		return params == null ? true : params.expires == null ? true : params.expires.before(new Date());
	}

	private static class WhydahTokenParams {
		private String applicationtoken, applicationid, applicationname;
		private Date expires;

		public String getApplicationtoken() {
			return applicationtoken;
		}

		public void setApplicationtoken(String applicationtoken) {
			this.applicationtoken = applicationtoken;
		}

		public String getApplicationid() {
			return applicationid;
		}

		public void setApplicationid(String applicationid) {
			this.applicationid = applicationid;
		}

		public String getApplicationname() {
			return applicationname;
		}

		public void setApplicationname(String applicationname) {
			this.applicationname = applicationname;
		}

		public Date getExpires() {
			return expires;
		}

		public void setExpires(Date expires) {
			this.expires = expires;
		}
	}
}
