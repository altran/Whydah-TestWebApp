package net.whydah.sso.web;

import junit.framework.TestCase;

public class EnvironmentTest extends TestCase {

    public void testGetHost() {
        String host = LoginController.getHost();
        System.out.printf("Host:" + host);
    }
}
