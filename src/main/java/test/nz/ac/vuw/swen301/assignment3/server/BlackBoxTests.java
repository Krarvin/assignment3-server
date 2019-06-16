package test.nz.ac.vuw.swen301.assignment3.server;

import org.apache.http.client.methods.HttpGet;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.*;
import java.net.URI;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BlackBoxTests {
    private static final String TEST_HOST = "localhost";
    private static final int TEST_PORT = 8080;
    private static final String TEST_PATH = "/resthome4logs/"; // as defined in pom.xml
    private static final String LOG_PATH = TEST_PATH + "logs"; // as defined in pom.xml and web.xml
    private static final String STAT_PATH = TEST_PATH + "stats"; // as defined in pom.xml and web.xml
    private static Process mvn;

    @BeforeClass
    public static void startServer() throws Exception {
        System.out.println("starting server");
        mvn = Runtime.getRuntime().exec("mvn jetty:run");
        Thread.sleep(7000);
    }

    @AfterClass
    public static void stopServer() throws Exception {
        mvn.destroyForcibly();
        Thread.sleep(3000);
    }

    private HttpResponse get(URI uri) throws Exception {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(uri);
        return httpClient.execute(request);
    }

    private boolean isServerReady() throws Exception {
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(TEST_PATH);
        URI uri = builder.build();
        try {
            HttpResponse response = get(uri);
            boolean success = response.getStatusLine().getStatusCode() == 200;

            if (!success) {
                System.err.println("Check whether server is up and running, request to " + uri + " returns " + response.getStatusLine());
            }

            return success;
        }
        catch (Exception x) {
            System.err.println("Encountered error connecting to " + uri + " -- check whether server is running and application has been deployed");
            return false;
        }
    }

    @Test
    public void testValidRequestResponseCode () throws Exception {
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(LOG_PATH)
                .setParameter("limit","5").setParameter("level", "INFO");
        URI uri = builder.build();
        HttpResponse response = get(uri);

        assertEquals(200,response.getStatusLine().getStatusCode());
    }

    @Test
    public void testInvalidRequestResponseCode1 () throws Exception {
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        // query parameter missing
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(LOG_PATH);
        URI uri = builder.build();
        HttpResponse response = get(uri);

        assertEquals(400,response.getStatusLine().getStatusCode());
    }

    @Test
    public void testInvalidRequestResponseCode2 () throws Exception {
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        // wrong query parameter name
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(LOG_PATH)
                .setParameter("invalidkey","J");
        URI uri = builder.build();
        HttpResponse response = get(uri);

        assertEquals(400,response.getStatusLine().getStatusCode());
    }

    @Test
    public void testValidContentType () throws Exception {
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(LOG_PATH)
                .setParameter("limit","20").setParameter("level","WARN");
        URI uri = builder.build();
        HttpResponse response = get(uri);

        assertNotNull(response.getFirstHeader("Content-Type"));

        // use startsWith instead of assertEquals since server may append char encoding to header value
        assertTrue(response.getFirstHeader("Content-Type").getValue().startsWith("application/json"));
    }

    @Test
    public void testReturnedValues () throws Exception {
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(LOG_PATH)
                .setParameter("name","J");
        URI uri = builder.build();
        HttpResponse response = get(uri);

        String content = EntityUtils.toString(response.getEntity());
        String[] names = content.split(" ");
        Set<String> set = Arrays.stream(names).collect(Collectors.toSet());

        assertTrue(set.contains("Joshua"));
        assertTrue(set.contains("Jason"));
        assertTrue(set.contains("Jasmine"));
    }
}
