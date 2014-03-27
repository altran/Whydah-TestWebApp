package no.nkk.judgedirectory.web.security;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Created by gunnar on 2/17/14.
 */
@XmlRootElement(name = "token")
public class WhydahUserToken {

	private String id;
	private String uid;
	private Integer securitylevel;
	private String personRef;
	private String firstname;
	private String lastname;
	private String email;
	private Date timestamp;
	private Long lifespan;
	private String issuer;

	private WhydahApplication[] application;

	@XmlAttribute
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Integer getSecuritylevel() {
		return securitylevel;
	}

	public void setSecuritylevel(Integer securitylevel) {
		this.securitylevel = securitylevel;
	}

	public String getPersonRef() {
		return personRef;
	}

	public void setPersonRef(String personRef) {
		this.personRef = personRef;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Long getLifespan() {
		return lifespan;
	}

	public void setLifespan(Long lifespan) {
		this.lifespan = lifespan;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	@XmlElement(name = "application")
	public WhydahApplication[] getApplication() {
		return application;
	}

	public void setApplication(WhydahApplication[] application) {
		this.application = application;
	}
}
