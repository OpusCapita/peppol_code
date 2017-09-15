package com.opuscapita.peppol.commons.errors.oxalis;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.opuscapita.peppol.commons.errors.oxalis.SendingErrors.RECEIVING_AP_ERROR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Sergejs.Roze
 */
//@Ignore("doesn't work on Estonian computers")
@SpringBootTest
@RunWith(SpringRunner.class)
@EnableConfigurationProperties(value = { OxalisErrorsList.class })
//@TestPropertySource(locations = "classpath:application.yml")
public class OxalisErrorsListTest {
    @Autowired
    private OxalisErrorsList oxalisErrorsList;

    private final static String fixture = "Request failed with rc=500, contents received (7199 characters):<!DOCTYPE html><html><head><title>Apache Tomcat/8.5.6 - Error report</title><style type=\"text/css\">H1 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:22px;} H2 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:16px;} H3 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:14px;} BODY {font-family:Tahoma,Arial,sans-serif;color:black;background-color:white;} B {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;} P {font-family:Tahoma,Arial,sans-serif;background:white;color:black;font-size:12px;}A {color : black;}A.name {color : black;}.line {height: 1px; background-color: #525D76; border: none;}</style> </head><body><h1>HTTP Status 500 - inbound: Temporary failure in name resolution</h1><div class=\"line\"></div><p><b>type</b> Exception report</p><p><b>message</b> <u>inbound: Temporary failure in name resolution</u></p><p><b>description</b> <u>The server encountered an internal error that prevented it from fulfilling this request.</u></p><p><b>exception</b></p><pre>com.netflix.zuul.exception.ZuulException: inbound: Temporary failure in name resolution\n" +
            "\torg.springframework.cloud.netflix.zuul.util.ZuulRuntimeException.&lt;init&gt;(ZuulRuntimeException.java:33)\n" +
            "\torg.springframework.cloud.netflix.zuul.filters.route.SimpleHostRoutingFilter.run(SimpleHostRoutingFilter.java:200)\n" +
            "\tcom.netflix.zuul.ZuulFilter.runFilter(ZuulFilter.java:112)\n" +
            "\tcom.netflix.zuul.FilterProcessor.processZuulFilter(FilterProcessor.java:193)\n" +
            "\tcom.netflix.zuul.FilterProcessor.runFilters(FilterProcessor.java:157)\n" +
            "\tcom.netflix.zuul.FilterProcessor.route(FilterProcessor.java:118)\n" +
            "\tcom.netflix.zuul.ZuulRunner.route(ZuulRunner.java:96)\n" +
            "\tcom.netflix.zuul.http.ZuulServlet.route(ZuulServlet.java:116)\n" +
            "\tcom.netflix.zuul.http.ZuulServlet.service(ZuulServlet.java:81)\n" +
            "\torg.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:52)\n" +
            "\torg.springframework.boot.web.filter.ApplicationContextHeaderFilter.doFilterInternal(ApplicationContextHeaderFilter.java:55)\n" +
            "\torg.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)\n" +
            "\torg.springframework.boot.actuate.trace.WebRequestTraceFilter.doFilterInternal(WebRequestTraceFilter.java:105)\n" +
            "\torg.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)\n" +
            "\torg.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:99)\n" +
            "\torg.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)\n" +
            "\torg.springframework.web.filter.HttpPutFormContentFilter.doFilterInternal(HttpPutFormContentFilter.java:89)\n" +
            "\torg.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)\n" +
            "\torg.springframework.web.filter.HiddenHttpMethodFilter.doFilterInternal(HiddenHttpMethodFilter.java:77)\n" +
            "\torg.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)\n" +
            "\torg.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:197)\n" +
            "\torg.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)\n" +
            "\torg.springframework.boot.actuate.autoconfigure.MetricsFilter.doFilterInternal(MetricsFilter.java:107)\n" +
            "\torg.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)\n" +
            "</pre><p><b>root cause</b></p><pre>java.net.UnknownHostException: inbound: Temporary failure in name resolution\n" +
            "\tjava.net.Inet4AddressImpl.lookupAllHostAddr(Native Method)\n" +
            "\tjava.net.InetAddress$2.lookupAllHostAddr(InetAddress.java:928)\n" +
            "\tjava.net.InetAddress.getAddressesFromNameService(InetAddress.java:1323)\n" +
            "\tjava.net.InetAddress.getAllByName0(InetAddress.java:1276)\n" +
            "\tjava.net.InetAddress.getAllByName(InetAddress.java:1192)\n" +
            "\tjava.net.InetAddress.getAllByName(InetAddress.java:1126)\n" +
            "\torg.apache.http.impl.conn.SystemDefaultDnsResolver.resolve(SystemDefaultDnsResolver.java:45)\n" +
            "\torg.apache.http.impl.conn.DefaultHttpClientConnectionOperator.connect(DefaultHttpClientConnectionOperator.java:111)\n" +
            "\torg.apache.http.impl.conn.PoolingHttpClientConnectionManager.connect(PoolingHttpClientConnectionManager.java:353)\n" +
            "\torg.apache.http.impl.execchain.MainClientExec.establishRoute(MainClientExec.java:380)\n" +
            "\torg.apache.http.impl.execchain.MainClientExec.execute(MainClientExec.java:236)\n" +
            "\torg.apache.http.impl.execchain.ProtocolExec.execute(ProtocolExec.java:184)\n" +
            "\torg.apache.http.impl.execchain.RetryExec.execute(RetryExec.java:88)\n" +
            "\torg.apache.http.impl.execchain.RedirectExec.execute(RedirectExec.java:110)\n" +
            "\torg.apache.http.impl.client.InternalHttpClient.doExecute(InternalHttpClient.java:184)\n" +
            "\torg.apache.http.impl.client.CloseableHttpClient.execute(CloseableHttpClient.java:117)\n" +
            "\torg.springframework.cloud.netflix.zuul.filters.route.SimpleHostRoutingFilter.forwardRequest(SimpleHostRoutingFilter.java:385)\n" +
            "\torg.springframework.cloud.netflix.zuul.filters.route.SimpleHostRoutingFilter.forward(SimpleHostRoutingFilter.java:304)\n" +
            "\torg.springframework.cloud.netflix.zuul.filters.route.SimpleHostRoutingFilter.run(SimpleHostRoutingFilter.java:195)\n" +
            "\tcom.netflix.zuul.ZuulFilter.runFilter(ZuulFilter.java:112)\n" +
            "\tcom.netflix.zuul.FilterProcessor.processZuulFilter(FilterProcessor.java:193)\n" +
            "\tcom.netflix.zuul.FilterProcessor.runFilters(FilterProcessor.java:157)\n" +
            "\tcom.netflix.zuul.FilterProcessor.route(FilterProcessor.java:118)\n" +
            "\tcom.netflix.zuul.ZuulRunner.route(ZuulRunner.java:96)\n" +
            "\tcom.netflix.zuul.http.ZuulServlet.route(ZuulServlet.java:116)\n" +
            "\tcom.netflix.zuul.http.ZuulServlet.service(ZuulServlet.java:81)\n" +
            "\torg.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:52)\n" +
            "\torg.springframework.boot.web.filter.ApplicationContextHeaderFilter.doFilterInternal(ApplicationContextHeaderFilter.java:55)\n" +
            "\torg.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)\n" +
            "\torg.springframework.boot.actuate.trace.WebRequestTraceFilter.doFilterInternal(WebRequestTraceFilter.java:105)\n" +
            "\torg.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)\n" +
            "\torg.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:99)\n" +
            "\torg.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)\n" +
            "\torg.springframework.web.filter.HttpPutFormContentFilter.doFilterInternal(HttpPutFormContentFilter.java:89)\n" +
            "\torg.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)\n" +
            "\torg.springframework.web.filter.HiddenHttpMethodFilter.doFilterInternal(HiddenHttpMethodFilter.java:77)\n" +
            "\torg.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)\n" +
            "\torg.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:197)\n" +
            "\torg.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)\n" +
            "\torg.springframework.boot.actuate.autoconfigure.MetricsFilter.doFilterInternal(MetricsFilter.java:107)\n" +
            "\torg.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)\n" +
            "</pre><p><b>note</b> <u>The full stack trace of the root cause is available in the Apache Tomcat/8.5.6 logs.</u></p><hr class=\"line\"><h3>Apache Tomcat/8.5.6</h3></body></html>";

    @Test
    public void testConfigurationProperties() throws Exception {
        assertNotNull(oxalisErrorsList);
        assertNotNull(oxalisErrorsList.getList());

        OxalisErrorRecognizer recognizer = new OxalisErrorRecognizer(oxalisErrorsList);
        //assertEquals(RECEIVING_AP_ERROR, recognizer.recognize("Failed to recognize error message: Request failed with rc=500, contents received (7199 characters)"));
        assertEquals(RECEIVING_AP_ERROR, recognizer.recognize(fixture));
    }

}
