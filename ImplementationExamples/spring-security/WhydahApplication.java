package no.nkk.judgedirectory.web.security;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by gunnar on 2/17/14.
 */
@XmlRootElement(name = "application")
public class WhydahApplication {
	private String id;
	private String applicationName;
	private WhydahOrganization organization;

	@XmlAttribute(name = "ID")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	@XmlElement(name = "organization")
	public WhydahOrganization getOrganization() {
		return organization;
	}

	public void setOrganization(WhydahOrganization organization) {
		this.organization = organization;
	}
}
