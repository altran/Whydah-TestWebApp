package org.whydah.sso.web;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: totto
 * Date: 2/7/11
 * Time: 8:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class EnvironmentTest  extends TestCase {

    public void testGetHost() {
        String host = LoginController.getHost();
        System.out.printf("Host:"+host);
    }
}
