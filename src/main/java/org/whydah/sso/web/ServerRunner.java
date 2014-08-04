package org.whydah.sso.web;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.web.servlet.DispatcherServlet;

public class ServerRunner {
    public static final int PORT_NO = 9990;
    public static final String TESTURL = "http://localhost:"+ PORT_NO + "/test/action";

    private Server server;


    public static void main(String[] arguments) throws Exception {
        ServerRunner serverRunner = new ServerRunner();
        serverRunner.start();
        serverRunner.join();
    }

    public ServerRunner()  {
        server = new Server(PORT_NO);
        ServletContextHandler context = new ServletContextHandler(server, "/test");

        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        dispatcherServlet.setContextConfigLocation("classpath:webapp/sso/mvc-config.xml");
        ServletHolder servletHolder = new ServletHolder(dispatcherServlet);
        context.addServlet(servletHolder, "/*");
    }

    public void start() throws Exception {
        server.start();
    }

    public void stop() throws Exception {
        server.stop();
    }

    public void join() throws InterruptedException {
        server.join();
    }
}
