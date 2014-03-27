package no.nkk.judgedirectory.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by gunnar on 2/14/14.
 */
public class WhydahEntryPoint implements AuthenticationEntryPoint {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private WhydahService whydahService;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
		String returnUrl = (request.isSecure() ? "https" : "http") + "://" + request.getServerName();
		if(request.getServerPort() > 0) {
			if((request.isSecure() && request.getServerPort() != 443) || (!request.isSecure() && request.getServerPort() != 80)) {
				returnUrl += ":" + request.getServerPort();
			}
		}
		returnUrl += request.getRequestURI();

		String ticket = request.getParameter("userticket");
		if (ticket != null && !ticket.trim().isEmpty()) {
			WhydahLogonToken applicationToken = whydahService.applicationLogon();
			// application logon
			// 201, 400, 406, 500, 501
			// get user token
			// 201, 400, 404, 406, 415, 500, 501
			// Auth complete

			WhydahUserToken userToken = whydahService.getUserToken(applicationToken, ticket);
			String username = userToken.getEmail(); // TODO
			UserDetailsImpl user = new UserDetailsImpl(applicationToken, userToken);

			WhydahAuthentication authentication = new WhydahAuthentication(username, user, new WebAuthenticationDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(authentication));
			response.sendRedirect(response.encodeRedirectURL(returnUrl));
		} else {
			// Redirect to whydah.
			response.sendRedirect(response.encodeRedirectURL(String.format("%s/login?redirectURI=%s", whydahService.getSsoUrl(), returnUrl)));
		}
	}
}
