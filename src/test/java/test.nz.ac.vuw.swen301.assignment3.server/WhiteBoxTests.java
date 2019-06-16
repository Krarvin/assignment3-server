package test.nz.ac.vuw.swen301.assignment3.server;
import nz.ac.vuw.swen301.assignment3.server.LogEvent;
import nz.ac.vuw.swen301.assignment3.server.LogServlet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class WhiteBoxTests {


    @Test
    public void testInvalidRequestResponseCode1() throws IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        // query parameter missing
        LogServlet service = new LogServlet();
        service.doGet(request,response);

        assertEquals(400,response.getStatus());
    }

    @Test
    public void testInvalidRequestResponseCode2() throws IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("not a valid param name","42");
        MockHttpServletResponse response = new MockHttpServletResponse();
        // wrong query parameter

        LogServlet service = new LogServlet();
        service.doGet(request,response);

        assertEquals(400,response.getStatus());
    }

    @Test
    public void testInvalidRequestResponseCode3() throws IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("limit","51");
        request.setParameter("level","WARN");
        MockHttpServletResponse response = new MockHttpServletResponse();
        // wrong query parameter

        LogServlet service = new LogServlet();
        service.doGet(request,response);

        assertEquals(400,response.getStatus());
    }

    @Test
    public void testInvalidRequestResponseCode4() throws IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("limit","-1");
        request.setParameter("level","WARN");
        MockHttpServletResponse response = new MockHttpServletResponse();
        // wrong query parameter

        LogServlet service = new LogServlet();
        service.doGet(request,response);

        assertEquals(400,response.getStatus());
    }

    @Test
    public void testInvalidRequestResponseCode5() throws IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("limit","5");
        request.setParameter("level","warn");
        MockHttpServletResponse response = new MockHttpServletResponse();
        // wrong query parameter

        LogServlet service = new LogServlet();
        service.doGet(request,response);

        assertEquals(400,response.getStatus());
    }

    @Test
    public void testValidRequestResponseCode() throws IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("level","WARN");
        request.setParameter("limit","1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        LogServlet service = new LogServlet();
        service.doGet(request,response);

        assertEquals(200,response.getStatus());
    }

    @Test
    public void testValidContentType() throws IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("level","WARN");
        request.setParameter("limit", "5");
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogServlet service = new LogServlet();
        service.doGet(request,response);

        assertTrue(response.getContentType().startsWith("application/json"));
    }

    @Test
    public void testExceptionStatus() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("level","TRACE");
        request.setParameter("limit","5");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest postrequest = new MockHttpServletRequest();
        MockHttpServletResponse postresponse = new MockHttpServletResponse();
        String json = "[";
        json += "{level\"INFO\",\"logger\":\"testlogger\",\"id\":\"id1\",\"thread\":\"testing\",\"message\":\"test log1\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "]";
        postrequest.setContentType("application/json");
        postrequest.setContent(json.getBytes());
        LogServlet service = new LogServlet();
        service.doPost(postrequest, postresponse);
        assertEquals(400,postresponse.getStatus());
    }

    @Test
    public void testTraceReturnedValues() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("level","TRACE");
        request.setParameter("limit","5");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest postrequest = new MockHttpServletRequest();
        MockHttpServletResponse postresponse = new MockHttpServletResponse();
        String json = "[";
        json += "{\"level\":\"INFO\",\"logger\":\"testlogger\",\"id\":\"id1\",\"thread\":\"testing\",\"message\":\"test log1\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"WARN\",\"logger\":\"testlogger\",\"id\":\"id2\",\"thread\":\"testing\",\"message\":\"test log2\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"ERROR\",\"logger\":\"testlogger\",\"id\":\"id3\",\"thread\":\"testing\",\"message\":\"test log3\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"FATAL\",\"logger\":\"testlogger\",\"id\":\"id4\",\"thread\":\"testing\",\"message\":\"test log4\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"DEBUG\",\"logger\":\"testlogger\",\"id\":\"id5\",\"thread\":\"testing\",\"message\":\"test log5\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}";
        json += "]";
        postrequest.setContentType("application/json");
        postrequest.setContent(json.getBytes());
        LogServlet service = new LogServlet();
        service.doPost(postrequest, postresponse);
        service.doGet(request,response);

        String result = response.getContentAsString();
        JSONArray jsonArray = new JSONArray(result);
        assertEquals(jsonArray.get(0).toString(), "{\"level\":\"DEBUG\",\"logger\":\"testlogger\",\"id\":\"id5\",\"thread\":\"testing\",\"message\":\"test log5\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
        assertEquals(jsonArray.get(1).toString(), "{\"level\":\"FATAL\",\"logger\":\"testlogger\",\"id\":\"id4\",\"thread\":\"testing\",\"message\":\"test log4\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
        assertEquals(jsonArray.get(2).toString(), "{\"level\":\"ERROR\",\"logger\":\"testlogger\",\"id\":\"id3\",\"thread\":\"testing\",\"message\":\"test log3\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
        assertEquals(jsonArray.get(3).toString(), "{\"level\":\"WARN\",\"logger\":\"testlogger\",\"id\":\"id2\",\"thread\":\"testing\",\"message\":\"test log2\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
        assertEquals(jsonArray.get(4).toString(), "{\"level\":\"INFO\",\"logger\":\"testlogger\",\"id\":\"id1\",\"thread\":\"testing\",\"message\":\"test log1\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
    }

    @Test
    public void testDebugReturnedValues() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("level","DEBUG");
        request.setParameter("limit","5");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest postrequest = new MockHttpServletRequest();
        MockHttpServletResponse postresponse = new MockHttpServletResponse();
        String json = "[";
        json += "{\"level\":\"INFO\",\"logger\":\"testlogger\",\"id\":\"id1\",\"thread\":\"testing\",\"message\":\"test log1\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"WARN\",\"logger\":\"testlogger\",\"id\":\"id2\",\"thread\":\"testing\",\"message\":\"test log2\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"ERROR\",\"logger\":\"testlogger\",\"id\":\"id3\",\"thread\":\"testing\",\"message\":\"test log3\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"FATAL\",\"logger\":\"testlogger\",\"id\":\"id4\",\"thread\":\"testing\",\"message\":\"test log4\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"DEBUG\",\"logger\":\"testlogger\",\"id\":\"id5\",\"thread\":\"testing\",\"message\":\"test log5\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}";
        json += "]";
        postrequest.setContentType("application/json");
        postrequest.setContent(json.getBytes());
        LogServlet service = new LogServlet();
        service.doPost(postrequest, postresponse);
        service.doGet(request,response);

        String result = response.getContentAsString();
        JSONArray jsonArray = new JSONArray(result);
        assertEquals(jsonArray.get(0).toString(), "{\"level\":\"DEBUG\",\"logger\":\"testlogger\",\"id\":\"id5\",\"thread\":\"testing\",\"message\":\"test log5\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
        assertEquals(jsonArray.get(1).toString(), "{\"level\":\"FATAL\",\"logger\":\"testlogger\",\"id\":\"id4\",\"thread\":\"testing\",\"message\":\"test log4\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
        assertEquals(jsonArray.get(2).toString(), "{\"level\":\"ERROR\",\"logger\":\"testlogger\",\"id\":\"id3\",\"thread\":\"testing\",\"message\":\"test log3\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
        assertEquals(jsonArray.get(3).toString(), "{\"level\":\"WARN\",\"logger\":\"testlogger\",\"id\":\"id2\",\"thread\":\"testing\",\"message\":\"test log2\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
        assertEquals(jsonArray.get(4).toString(), "{\"level\":\"INFO\",\"logger\":\"testlogger\",\"id\":\"id1\",\"thread\":\"testing\",\"message\":\"test log1\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
    }

    @Test
    public void testInfoReturnedValues() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("level","INFO");
        request.setParameter("limit","5");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest postrequest = new MockHttpServletRequest();
        MockHttpServletResponse postresponse = new MockHttpServletResponse();
        String json = "[";
        json += "{\"level\":\"INFO\",\"logger\":\"testlogger\",\"id\":\"id1\",\"thread\":\"testing\",\"message\":\"test log1\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"WARN\",\"logger\":\"testlogger\",\"id\":\"id2\",\"thread\":\"testing\",\"message\":\"test log2\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"ERROR\",\"logger\":\"testlogger\",\"id\":\"id3\",\"thread\":\"testing\",\"message\":\"test log3\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"FATAL\",\"logger\":\"testlogger\",\"id\":\"id4\",\"thread\":\"testing\",\"message\":\"test log4\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"DEBUG\",\"logger\":\"testlogger\",\"id\":\"id5\",\"thread\":\"testing\",\"message\":\"test log5\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}";
        json += "]";
        postrequest.setContentType("application/json");
        postrequest.setContent(json.getBytes());
        LogServlet service = new LogServlet();
        service.doPost(postrequest, postresponse);
        service.doGet(request,response);

        String result = response.getContentAsString();
        JSONArray jsonArray = new JSONArray(result);
        assertEquals(jsonArray.get(0).toString(),"{\"level\":\"FATAL\",\"logger\":\"testlogger\",\"id\":\"id4\",\"thread\":\"testing\",\"message\":\"test log4\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
        assertEquals(jsonArray.get(1).toString(),"{\"level\":\"ERROR\",\"logger\":\"testlogger\",\"id\":\"id3\",\"thread\":\"testing\",\"message\":\"test log3\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
        assertEquals(jsonArray.get(2).toString(),"{\"level\":\"WARN\",\"logger\":\"testlogger\",\"id\":\"id2\",\"thread\":\"testing\",\"message\":\"test log2\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
        assertEquals(jsonArray.get(3).toString(),"{\"level\":\"INFO\",\"logger\":\"testlogger\",\"id\":\"id1\",\"thread\":\"testing\",\"message\":\"test log1\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
    }

    @Test
    public void testWarnReturnedValues() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("level","WARN");
        request.setParameter("limit","5");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest postrequest = new MockHttpServletRequest();
        MockHttpServletResponse postresponse = new MockHttpServletResponse();
        String json = "[";
        json += "{\"level\":\"INFO\",\"logger\":\"testlogger\",\"id\":\"id1\",\"thread\":\"testing\",\"message\":\"test log1\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"WARN\",\"logger\":\"testlogger\",\"id\":\"id2\",\"thread\":\"testing\",\"message\":\"test log2\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"ERROR\",\"logger\":\"testlogger\",\"id\":\"id3\",\"thread\":\"testing\",\"message\":\"test log3\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"FATAL\",\"logger\":\"testlogger\",\"id\":\"id4\",\"thread\":\"testing\",\"message\":\"test log4\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"DEBUG\",\"logger\":\"testlogger\",\"id\":\"id5\",\"thread\":\"testing\",\"message\":\"test log5\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}";
        json += "]";
        postrequest.setContentType("application/json");
        postrequest.setContent(json.getBytes());
        LogServlet service = new LogServlet();
        service.doPost(postrequest, postresponse);
        service.doGet(request,response);

        String result = response.getContentAsString();
        JSONArray jsonArray = new JSONArray(result);
        assertEquals(jsonArray.get(0).toString(),"{\"level\":\"FATAL\",\"logger\":\"testlogger\",\"id\":\"id4\",\"thread\":\"testing\",\"message\":\"test log4\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
        assertEquals(jsonArray.get(1).toString(),"{\"level\":\"ERROR\",\"logger\":\"testlogger\",\"id\":\"id3\",\"thread\":\"testing\",\"message\":\"test log3\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
        assertEquals(jsonArray.get(2).toString(),"{\"level\":\"WARN\",\"logger\":\"testlogger\",\"id\":\"id2\",\"thread\":\"testing\",\"message\":\"test log2\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
    }

    @Test
    public void testErrorReturnedValues() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("level","ERROR");
        request.setParameter("limit","5");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest postrequest = new MockHttpServletRequest();
        MockHttpServletResponse postresponse = new MockHttpServletResponse();
        String json = "[";
        json += "{\"level\":\"INFO\",\"logger\":\"testlogger\",\"id\":\"id1\",\"thread\":\"testing\",\"message\":\"test log1\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"WARN\",\"logger\":\"testlogger\",\"id\":\"id2\",\"thread\":\"testing\",\"message\":\"test log2\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"ERROR\",\"logger\":\"testlogger\",\"id\":\"id3\",\"thread\":\"testing\",\"message\":\"test log3\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"FATAL\",\"logger\":\"testlogger\",\"id\":\"id4\",\"thread\":\"testing\",\"message\":\"test log4\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"DEBUG\",\"logger\":\"testlogger\",\"id\":\"id5\",\"thread\":\"testing\",\"message\":\"test log5\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}";
        json += "]";
        postrequest.setContentType("application/json");
        postrequest.setContent(json.getBytes());
        LogServlet service = new LogServlet();
        service.doPost(postrequest, postresponse);
        service.doGet(request,response);

        String result = response.getContentAsString();
        JSONArray jsonArray = new JSONArray(result);
        assertEquals(jsonArray.get(0).toString(),"{\"level\":\"FATAL\",\"logger\":\"testlogger\",\"id\":\"id4\",\"thread\":\"testing\",\"message\":\"test log4\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
        assertEquals(jsonArray.get(1).toString(),"{\"level\":\"ERROR\",\"logger\":\"testlogger\",\"id\":\"id3\",\"thread\":\"testing\",\"message\":\"test log3\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
    }

    @Test
    public void testFatalReturnedValues() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("level","FATAL");
        request.setParameter("limit","5");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest postrequest = new MockHttpServletRequest();
        MockHttpServletResponse postresponse = new MockHttpServletResponse();
        String json = "[";
        json += "{\"level\":\"INFO\",\"logger\":\"testlogger\",\"id\":\"id1\",\"thread\":\"testing\",\"message\":\"test log1\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"WARN\",\"logger\":\"testlogger\",\"id\":\"id2\",\"thread\":\"testing\",\"message\":\"test log2\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"ERROR\",\"logger\":\"testlogger\",\"id\":\"id3\",\"thread\":\"testing\",\"message\":\"test log3\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"FATAL\",\"logger\":\"testlogger\",\"id\":\"id4\",\"thread\":\"testing\",\"message\":\"test log4\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"DEBUG\",\"logger\":\"testlogger\",\"id\":\"id5\",\"thread\":\"testing\",\"message\":\"test log5\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}";
        json += "]";
        postrequest.setContentType("application/json");
        postrequest.setContent(json.getBytes());
        LogServlet service = new LogServlet();
        service.doPost(postrequest, postresponse);
        service.doGet(request,response);

        String result = response.getContentAsString();
        System.out.println(result);
        JSONArray jsonArray = new JSONArray(result);
        assertEquals(jsonArray.length(), 1);
        assertEquals(jsonArray.get(0).toString(),"{\"level\":\"FATAL\",\"logger\":\"testlogger\",\"id\":\"id4\",\"thread\":\"testing\",\"message\":\"test log4\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
    }

    @Test
    public void testLimitReturnedValues() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("level","TRACE");
        request.setParameter("limit","2");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest postrequest = new MockHttpServletRequest();
        MockHttpServletResponse postresponse = new MockHttpServletResponse();
        String json = "[";
        json += "{\"level\":\"INFO\",\"logger\":\"testlogger\",\"id\":\"id1\",\"thread\":\"testing\",\"message\":\"test log1\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"WARN\",\"logger\":\"testlogger\",\"id\":\"id2\",\"thread\":\"testing\",\"message\":\"test log2\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"ERROR\",\"logger\":\"testlogger\",\"id\":\"id3\",\"thread\":\"testing\",\"message\":\"test log3\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"FATAL\",\"logger\":\"testlogger\",\"id\":\"id4\",\"thread\":\"testing\",\"message\":\"test log4\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"DEBUG\",\"logger\":\"testlogger\",\"id\":\"id5\",\"thread\":\"testing\",\"message\":\"test log5\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}";
        json += "]";
        postrequest.setContentType("application/json");
        postrequest.setContent(json.getBytes());
        LogServlet service = new LogServlet();
        service.doPost(postrequest, postresponse);
        service.doGet(request,response);

        String result = response.getContentAsString();
        JSONArray jsonArray = new JSONArray(result);
        assertEquals(jsonArray.length(), 2);
        assertEquals(jsonArray.get(0).toString(),"{\"level\":\"DEBUG\",\"logger\":\"testlogger\",\"id\":\"id5\",\"thread\":\"testing\",\"message\":\"test log5\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
        assertEquals(jsonArray.get(1).toString(),"{\"level\":\"FATAL\",\"logger\":\"testlogger\",\"id\":\"id4\",\"thread\":\"testing\",\"message\":\"test log4\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}");
    }

    @Test
    public void testInvalidPostRequest() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("level","TRACE");
        request.setParameter("limit","2");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest postrequest = new MockHttpServletRequest();
        MockHttpServletResponse postresponse = new MockHttpServletResponse();
        String json = "";
        json += "{\"level\":\"INFO\",\"logger\":\"testlogger\",\"id\":\"id1\",\"thread\":\"testing\",\"message\":\"test log1\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"WARN\",\"logger\":\"testlogger\",\"id\":\"id2\",\"thread\":\"testing\",\"message\":\"test log2\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"ERROR\",\"logger\":\"testlogger\",\"id\":\"id3\",\"thread\":\"testing\",\"message\":\"test log3\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"FATAL\",\"logger\":\"testlogger\",\"id\":\"id4\",\"thread\":\"testing\",\"message\":\"test log4\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"DEBUG\",\"logger\":\"testlogger\",\"id\":\"id5\",\"thread\":\"testing\",\"message\":\"test log5\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}";
        json += "]";
        postrequest.setContentType("application/json");
        postrequest.setContent(json.getBytes());
        LogServlet service = new LogServlet();
        service.doPost(postrequest, postresponse);
        service.doGet(request,response);
        assertEquals(400,postresponse.getStatus());
    }

    @Test
    public void testInvalidPostRequest2() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("level","TRACE");
        request.setParameter("limit","2");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest postrequest = new MockHttpServletRequest();
        MockHttpServletResponse postresponse = new MockHttpServletResponse();
        String json = "[";
        json += "{\"level\":\"INFO\",\"logger\":\"testlogger\",\"id\":\"id1\",\"thread\":\"testing\",\"message\":\"test log1\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"WARN\",\"logger\":\"testlogger\",\"id\":\"id2\",\"thread\":\"testing\",\"message\":\"test log2\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"ERROR\",\"logger\":\"testlogger\",\"id\":\"id3\",\"thread\":\"testing\",\"message\":\"test log3\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"FATAL\",\"logger\":\"testlogger\",\"id\":\"id4\",\"thread\":\"testing\",\"message\":\"test log4\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"},";
        json += "{\"level\":\"DEBUG\",\"logger\":\"testlogger\",\"id\":\"id5\",\"thread\":\"testing\",\"message\":\"test log5\",\"timestamp\":\"2019-06-16\",\"errorDetails\":\"\"}";
        postrequest.setContentType("application/json");
        postrequest.setContent(json.getBytes());
        LogServlet service = new LogServlet();
        service.doPost(postrequest, postresponse);
        service.doGet(request,response);
        assertEquals(400,postresponse.getStatus());
    }

    @Test
    public void testInvalidPostRequest3() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("level","TRACE");
        request.setParameter("limit","2");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest postrequest = new MockHttpServletRequest();
        MockHttpServletResponse postresponse = new MockHttpServletResponse();
        postrequest.setContentType("application/json");
        LogServlet service = new LogServlet();
        service.doPost(postrequest, postresponse);
        service.doGet(request,response);
        assertEquals(400,postresponse.getStatus());
    }



}
