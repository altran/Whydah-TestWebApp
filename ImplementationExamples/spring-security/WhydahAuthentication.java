package no.nkk.judgedirectory.web.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by gunnar on 2/14/14.
 */
public class WhydahAuthentication implements Authentication {
	private final String username;
	private final UserDetailsImpl user;
	private final WebAuthenticationDetails details;
	private final Collection<? extends GrantedAuthority> authorities;

	private boolean authenticated;

	public WhydahAuthentication(String username, UserDetailsImpl user, WebAuthenticationDetails details) {
		this.username = username;
		this.user = user;
		this.details = details;
		this.authorities = new HashSet<>(user.getAuthorities());
	}

	@Override public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override public Object getCredentials() {
		return null;
	}

	@Override public Object getDetails() {
		return details;
	}

	@Override public Object getPrincipal() {
		return user;
	}

	@Override public boolean isAuthenticated() {
		return authenticated;
	}

	@Override public void setAuthenticated(boolean authenticated) throws IllegalArgumentException {
		this.authenticated = authenticated;
	}

	@Override public String getName() {
		return username;
	}
}
