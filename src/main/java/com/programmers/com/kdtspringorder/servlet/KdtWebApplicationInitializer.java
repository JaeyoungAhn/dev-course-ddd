package com.programmers.com.kdtspringorder.servlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;

public class KdtWebApplicationInitializer implements WebApplicationInitializer {
    private static final Logger logger = LoggerFactory.getLogger(TestServlet.class);

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        logger.info("Starting server...");
        ServletRegistration.Dynamic servletRegistration = servletContext.addServlet("test", new TestServlet());
        servletRegistration.addMapping("/*");
        servletRegistration.setLoadOnStartup(1);

    }
}
