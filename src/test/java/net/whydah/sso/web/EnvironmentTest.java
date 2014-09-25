package net.whydah.sso.web;

import junit.framework.TestCase;
import net.whydah.sso.web.util.RequestHelper;

public class EnvironmentTest extends TestCase {

    private String getHost;

    public void testGetHost() {
        getHost = RequestHelper.getHost();
        String host = getHost;
        System.out.printf("Host:" + host);
    }
}
