package no.nkk.judgedirectory.web.security;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by gunnar on 2/17/14.
 */
@XmlRootElement(name = "organization")
public class WhydahOrganization {
	private String id;
	private String organizationName;
	private WhydahRole[] role;

	@XmlAttribute(name = "ID")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	@XmlElement(name = "role")
	public WhydahRole[] getRole() {
		return role;
	}

	public void setRole(WhydahRole[] role) {
		this.role = role;
	}
}
