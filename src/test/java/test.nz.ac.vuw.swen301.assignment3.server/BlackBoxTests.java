package test.nz.ac.vuw.swen301.assignment3.server;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.io.FileOutputStream;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.JVM)
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
        Thread.sleep(10000);
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
    public void  testStatsCode() throws Exception{
        Assume.assumeTrue(isServerReady());

        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(LOG_PATH)
                .setParameter("limit","5").setParameter("level", "WARN");
        URI postURI = builder.build();
        HttpPost post = new HttpPost(postURI);
        String output = generateJsonArray();
        StringEntity json = new StringEntity(output);
        post.addHeader("content-type", "application/json");
        post.setEntity(json);
        HttpClient httpclient = HttpClientBuilder.create().build();
        httpclient.execute(post);


        URIBuilder statbuilder = new URIBuilder();
        statbuilder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(STAT_PATH);
        URI getUri = statbuilder.build();
        HttpGet get = new HttpGet(getUri);
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response1 = httpClient.execute(get);
        //asserts that response code is 201 created
        assertEquals(201,response1.getStatusLine().getStatusCode());
    }

    @Test
    public void  testStatsContentType() throws Exception{
        Assume.assumeTrue(isServerReady());

        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(LOG_PATH)
                .setParameter("limit","5").setParameter("level", "WARN");
        URI postURI = builder.build();
        HttpPost post = new HttpPost(postURI);
        String output = generateJsonArray();
        StringEntity json = new StringEntity(output);
        post.addHeader("content-type", "application/json");
        post.setEntity(json);
        HttpClient httpclient = HttpClientBuilder.create().build();
        httpclient.execute(post);


        URIBuilder statbuilder = new URIBuilder();
        statbuilder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(STAT_PATH);
        URI getUri = statbuilder.build();
        HttpGet get = new HttpGet(getUri);
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response1 = httpClient.execute(get);
        //asserts that the content type is application/vnd.ms-excel
        assertEquals("Content-Type: application/vnd.ms-excel", response1.getEntity().getContentType().toString());
    }

    @Test
    public void testStatsDataTypes () throws Exception {
        Assume.assumeTrue(isServerReady());

        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(LOG_PATH)
                .setParameter("limit","5").setParameter("level", "WARN");
        URI postURI = builder.build();
        HttpPost post = new HttpPost(postURI);
        String output = generateJsonArray();
        StringEntity json = new StringEntity(output);
        post.addHeader("content-type", "application/json");
        post.setEntity(json);
        HttpClient httpclient = HttpClientBuilder.create().build();
        httpclient.execute(post);


        URIBuilder statbuilder = new URIBuilder();
        statbuilder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(STAT_PATH);
        URI getUri = statbuilder.build();
        HttpGet get = new HttpGet(getUri);
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response1 = httpClient.execute(get);
        System.out.println(response1.getEntity().getContent());
        XSSFWorkbook workbook = new XSSFWorkbook(response1.getEntity().getContent());
        FileOutputStream fileOut = new FileOutputStream("log-statistics.xlsx");
        workbook.write(fileOut);
        XSSFSheet sheet = workbook.getSheetAt(0);
        for(Row r: sheet){
            Iterator<Cell> headIterator = r.cellIterator();
            Cell cell;
            if(r.getRowNum() == 0){
                while(headIterator.hasNext()){
                    cell = headIterator.next();
                    switch(cell.getCellType()){
                        case NUMERIC:
                            //asserts that if there is a count, it is greater than 0
                            assertTrue(cell.getNumericCellValue() > 0);
                            break;
                        case STRING:
                            //asserts that each cell in first row is a date
                            assertTrue(isValidDate(cell.getStringCellValue()));
                            break;
                    }
                }
            }
            else{
                while(headIterator.hasNext()){
                    cell = headIterator.next();
                    switch(cell.getCellType()){
                        case NUMERIC:
                            //asserts every number is greater than 0
                            assertTrue(cell.getNumericCellValue() > 0);
                            break;
                        case STRING:
                            //asserts that each row is specified
                            assertNotNull(cell.getStringCellValue());
                            break;
                    }
                }
            }
        }
        //asserts status code is 201(created)
        assertEquals(201, response1.getStatusLine().getStatusCode());
    }

    @Test
    public void testValidGetResponseCode () throws Exception {
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(LOG_PATH)
                .setParameter("limit","5").setParameter("level", "INFO");
        URI uri = builder.build();
        HttpResponse response = get(uri);
        //tests that response code is fine
        assertEquals(200,response.getStatusLine().getStatusCode());
    }

    @Test
    public void testInvalidGetResponseCode1 () throws Exception {
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(LOG_PATH);
        URI uri = builder.build();
        HttpResponse response = get(uri);
        //bad request because no paremeter set
        assertEquals(400,response.getStatusLine().getStatusCode());
    }

    @Test
    public void testInvalidGetResponseCode2 () throws Exception {
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(LOG_PATH)
                .setParameter("invalidkey","J");
        URI uri = builder.build();
        HttpResponse response = get(uri);
        //bad request because incorrect parameter name
        assertEquals(400,response.getStatusLine().getStatusCode());
    }

    @Test
    public void testInvalidGetResponseCode3 () throws Exception {
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(LOG_PATH)
                .setParameter("limit","-1").setParameter("level", "INFO");
        URI uri = builder.build();
        HttpResponse response = get(uri);
        //cannot handle negative limits
        assertEquals(400,response.getStatusLine().getStatusCode());
    }

    @Test
    public void testInvalidGetResponseCode4 () throws Exception {
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(LOG_PATH)
                .setParameter("limit","51").setParameter("level", "INFO");
        URI uri = builder.build();
        HttpResponse response = get(uri);
        //50 should be the maximum get
        assertEquals(400,response.getStatusLine().getStatusCode());
    }

    @Test
    public void testInvalidGetResponseCode5 () throws Exception {
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(LOG_PATH)
                .setParameter("limit","5").setParameter("level", "info");
        URI uri = builder.build();
        HttpResponse response = get(uri);
        //should return bad request because info is lower case therefore not an ENUM
        assertEquals(400,response.getStatusLine().getStatusCode());
    }



    @Test
    public void testValidGetContentType () throws Exception {
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
    public void testInvalidPostFormat () throws Exception {
        Assume.assumeTrue(isServerReady());

        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(LOG_PATH)
                .setParameter("limit","5").setParameter("level", "TRACE");
        URI uri = builder.build();
        HttpPost post = new HttpPost(uri);
        String output = generateFaultyJsonArray();
        StringEntity json = new StringEntity(output);
        post.addHeader("content-type", "application/json");
        post.setEntity(json);
        HttpClient httpclient = HttpClientBuilder.create().build();
        HttpResponse postresponse = httpclient.execute(post);
        //bad request because JSONArray does not start with [
        assertEquals(400, postresponse.getStatusLine().getStatusCode());


    }

    @Test
    public void testInvalidPostFormat2 () throws Exception {
        Assume.assumeTrue(isServerReady());

        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(LOG_PATH)
                .setParameter("limit","5").setParameter("level", "TRACE");
        URI uri = builder.build();
        HttpPost post = new HttpPost(uri);
        String output = generateFaultyJsonArray2();
        StringEntity json = new StringEntity(output);
        post.addHeader("content-type", "application/json");
        post.setEntity(json);
        HttpClient httpclient = HttpClientBuilder.create().build();
        HttpResponse postresponse = httpclient.execute(post);
        //bad request because JSONObjects in array don't contain keys in ""
        assertEquals(400, postresponse.getStatusLine().getStatusCode());


    }

    @Test
    public void testvalidPostContentType () throws Exception {
        Assume.assumeTrue(isServerReady());

        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(LOG_PATH)
                .setParameter("limit","5").setParameter("level", "TRACE");
        URI uri = builder.build();
        HttpPost post = new HttpPost(uri);
        String output = generateFaultyJsonArray2();
        StringEntity json = new StringEntity(output);
        post.addHeader("content-type", "application/json");
        post.setEntity(json);
        HttpClient httpclient = HttpClientBuilder.create().build();
        HttpResponse postresponse = httpclient.execute(post);
        assertTrue(postresponse.getFirstHeader("Content-Type").getValue().startsWith("application/json"));

    }


    @Test
    public void testTRACEReturnedValues () throws Exception {
        Assume.assumeTrue(isServerReady());

        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(LOG_PATH)
                .setParameter("limit","5").setParameter("level", "TRACE");
        URI uri = builder.build();
        HttpPost post = new HttpPost(uri);
        String output = generateJsonArray();
        StringEntity json = new StringEntity(output);
        post.addHeader("content-type", "application/json");
        post.setEntity(json);
        HttpClient httpclient = HttpClientBuilder.create().build();
        httpclient.execute(post);

        HttpResponse response = get(uri);
        String jsonString = EntityUtils.toString(response.getEntity());
        JSONArray jsonArray = new JSONArray(jsonString);
        Assert.assertEquals(jsonArray.get(0).toString(), "{\"level\":\"DEBUG\",\"logger\":\"testlogger\",\"id\":\"id5\",\"thread\":\"testing\",\"message\":\"test log5\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
        Assert.assertEquals(jsonArray.get(1).toString(), "{\"level\":\"FATAL\",\"logger\":\"testlogger\",\"id\":\"id4\",\"thread\":\"testing\",\"message\":\"test log4\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
        Assert.assertEquals(jsonArray.get(2).toString(), "{\"level\":\"ERROR\",\"logger\":\"testlogger\",\"id\":\"id3\",\"thread\":\"testing\",\"message\":\"test log3\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
        Assert.assertEquals(jsonArray.get(3).toString(), "{\"level\":\"WARN\",\"logger\":\"testlogger\",\"id\":\"id2\",\"thread\":\"testing\",\"message\":\"test log2\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
        Assert.assertEquals(jsonArray.get(4).toString(), "{\"level\":\"INFO\",\"logger\":\"testlogger\",\"id\":\"id1\",\"thread\":\"testing\",\"message\":\"test log1\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
    }

    @Test
    public void testDEBUGReturnedValues () throws Exception {
        Assume.assumeTrue(isServerReady());

        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(LOG_PATH)
                .setParameter("limit","5").setParameter("level", "DEBUG");
        URI uri = builder.build();
        HttpPost post = new HttpPost(uri);
        String output = generateJsonArray();
        StringEntity json = new StringEntity(output);
        post.addHeader("content-type", "application/json");
        post.setEntity(json);
        HttpClient httpclient = HttpClientBuilder.create().build();
        httpclient.execute(post);

        HttpResponse response = get(uri);
        String jsonString = EntityUtils.toString(response.getEntity());
        JSONArray jsonArray = new JSONArray(jsonString);
        Assert.assertEquals(jsonArray.get(0).toString(), "{\"level\":\"DEBUG\",\"logger\":\"testlogger\",\"id\":\"id5\",\"thread\":\"testing\",\"message\":\"test log5\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
        Assert.assertEquals(jsonArray.get(1).toString(), "{\"level\":\"FATAL\",\"logger\":\"testlogger\",\"id\":\"id4\",\"thread\":\"testing\",\"message\":\"test log4\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
        Assert.assertEquals(jsonArray.get(2).toString(), "{\"level\":\"ERROR\",\"logger\":\"testlogger\",\"id\":\"id3\",\"thread\":\"testing\",\"message\":\"test log3\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
        Assert.assertEquals(jsonArray.get(3).toString(), "{\"level\":\"WARN\",\"logger\":\"testlogger\",\"id\":\"id2\",\"thread\":\"testing\",\"message\":\"test log2\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
        Assert.assertEquals(jsonArray.get(4).toString(), "{\"level\":\"INFO\",\"logger\":\"testlogger\",\"id\":\"id1\",\"thread\":\"testing\",\"message\":\"test log1\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
    }

    @Test
    public void testINFOReturnedValues () throws Exception {
        Assume.assumeTrue(isServerReady());

        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(LOG_PATH)
                .setParameter("limit","5").setParameter("level", "INFO");
        URI uri = builder.build();
        HttpPost post = new HttpPost(uri);
        String output = generateJsonArray();
        StringEntity json = new StringEntity(output);
        post.addHeader("content-type", "application/json");
        post.setEntity(json);
        HttpClient httpclient = HttpClientBuilder.create().build();
        httpclient.execute(post);

        HttpResponse response = get(uri);
        String jsonString = EntityUtils.toString(response.getEntity());
        JSONArray jsonArray = new JSONArray(jsonString);
        Assert.assertEquals(jsonArray.get(0).toString(),"{\"level\":\"FATAL\",\"logger\":\"testlogger\",\"id\":\"id4\",\"thread\":\"testing\",\"message\":\"test log4\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
        Assert.assertEquals(jsonArray.get(1).toString(),"{\"level\":\"ERROR\",\"logger\":\"testlogger\",\"id\":\"id3\",\"thread\":\"testing\",\"message\":\"test log3\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
        Assert.assertEquals(jsonArray.get(2).toString(),"{\"level\":\"WARN\",\"logger\":\"testlogger\",\"id\":\"id2\",\"thread\":\"testing\",\"message\":\"test log2\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
        Assert.assertEquals(jsonArray.get(3).toString(),"{\"level\":\"INFO\",\"logger\":\"testlogger\",\"id\":\"id1\",\"thread\":\"testing\",\"message\":\"test log1\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
    }

    @Test
    public void testWARNReturnedValues () throws Exception {
        Assume.assumeTrue(isServerReady());

        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(LOG_PATH)
                .setParameter("limit","5").setParameter("level", "WARN");
        URI uri = builder.build();
        HttpPost post = new HttpPost(uri);
        String output = generateJsonArray();
        StringEntity json = new StringEntity(output);
        post.addHeader("content-type", "application/json");
        post.setEntity(json);
        HttpClient httpclient = HttpClientBuilder.create().build();
        httpclient.execute(post);

        HttpResponse response = get(uri);
        String jsonString = EntityUtils.toString(response.getEntity());
        JSONArray jsonArray = new JSONArray(jsonString);
        Assert.assertEquals(jsonArray.get(0).toString(),"{\"level\":\"FATAL\",\"logger\":\"testlogger\",\"id\":\"id4\",\"thread\":\"testing\",\"message\":\"test log4\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
        Assert.assertEquals(jsonArray.get(1).toString(),"{\"level\":\"ERROR\",\"logger\":\"testlogger\",\"id\":\"id3\",\"thread\":\"testing\",\"message\":\"test log3\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
        Assert.assertEquals(jsonArray.get(2).toString(),"{\"level\":\"WARN\",\"logger\":\"testlogger\",\"id\":\"id2\",\"thread\":\"testing\",\"message\":\"test log2\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
    }

    @Test
    public void testERRORReturnedValues () throws Exception {
        Assume.assumeTrue(isServerReady());

        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(LOG_PATH)
                .setParameter("limit","5").setParameter("level", "ERROR");
        URI uri = builder.build();
        HttpPost post = new HttpPost(uri);
        String output = generateJsonArray();
        StringEntity json = new StringEntity(output);
        post.addHeader("content-type", "application/json");
        post.setEntity(json);
        HttpClient httpclient = HttpClientBuilder.create().build();
        httpclient.execute(post);

        HttpResponse response = get(uri);
        String jsonString = EntityUtils.toString(response.getEntity());
        JSONArray jsonArray = new JSONArray(jsonString);
        Assert.assertEquals(jsonArray.get(0).toString(),"{\"level\":\"FATAL\",\"logger\":\"testlogger\",\"id\":\"id4\",\"thread\":\"testing\",\"message\":\"test log4\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
        Assert.assertEquals(jsonArray.get(1).toString(),"{\"level\":\"ERROR\",\"logger\":\"testlogger\",\"id\":\"id3\",\"thread\":\"testing\",\"message\":\"test log3\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
    }

    @Test
    public void testFATALReturnedValues () throws Exception {
        Assume.assumeTrue(isServerReady());

        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(LOG_PATH)
                .setParameter("limit","5").setParameter("level", "ERROR");
        URI uri = builder.build();
        HttpPost post = new HttpPost(uri);
        String output = generateJsonArray();
        StringEntity json = new StringEntity(output);
        post.addHeader("content-type", "application/json");
        post.setEntity(json);
        HttpClient httpclient = HttpClientBuilder.create().build();
        httpclient.execute(post);

        HttpResponse response = get(uri);
        String jsonString = EntityUtils.toString(response.getEntity());
        JSONArray jsonArray = new JSONArray(jsonString);
        Assert.assertEquals(jsonArray.get(0).toString(),"{\"level\":\"FATAL\",\"logger\":\"testlogger\",\"id\":\"id4\",\"thread\":\"testing\",\"message\":\"test log4\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
    }


    public String generateJsonArray(){
        String json = "[";
        json += "{\"level\":\"INFO\",\"logger\":\"testlogger\",\"id\":\"id1\",\"thread\":\"testing\",\"message\":\"test log1\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"WARN\",\"logger\":\"testlogger\",\"id\":\"id2\",\"thread\":\"testing\",\"message\":\"test log2\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"ERROR\",\"logger\":\"testlogger\",\"id\":\"id3\",\"thread\":\"testing\",\"message\":\"test log3\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"FATAL\",\"logger\":\"testlogger\",\"id\":\"id4\",\"thread\":\"testing\",\"message\":\"test log4\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"DEBUG\",\"logger\":\"testlogger\",\"id\":\"id5\",\"thread\":\"testing\",\"message\":\"test log5\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}";
        json += "]";
        return json;
    }

    public String generateFaultyJsonArray(){
        String json = "";
        json += "{\"level\":\"INFO\",\"logger\":\"testlogger\",\"id\":\"id1\",\"thread\":\"testing\",\"message\":\"test log1\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"WARN\",\"logger\":\"testlogger\",\"id\":\"id2\",\"thread\":\"testing\",\"message\":\"test log2\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"ERROR\",\"logger\":\"testlogger\",\"id\":\"id3\",\"thread\":\"testing\",\"message\":\"test log3\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"FATAL\",\"logger\":\"testlogger\",\"id\":\"id4\",\"thread\":\"testing\",\"message\":\"test log4\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"DEBUG\",\"logger\":\"testlogger\",\"id\":\"id5\",\"thread\":\"testing\",\"message\":\"test log5\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}";
        json += "]";
        return json;
    }

    public String generateFaultyJsonArray2(){
        String json = "[";
        json += "{level:INFO\",\"logger\":\"testlogger\",\"id\":\"id1\",\"thread\":\"testing\",\"message\":\"test log1\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"WARN\",\"logger\":\"testlogger\",\"id\":\"id2\",\"thread\":\"testing\",\"message\":\"test log2\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"ERROR\",\"logger\":\"testlogger\",\"id\":\"id3\",\"thread\":\"testing\",\"message\":\"test log3\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"FATAL\",\"logger\":\"testlogger\",\"id\":\"id4\",\"thread\":\"testing\",\"message\":\"test log4\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"DEBUG\",\"logger\":\"testlogger\",\"id\":\"id5\",\"thread\":\"testing\",\"message\":\"test log5\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}";
        json += "]";
        return json;
    }

    public static boolean isValidDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }
}
