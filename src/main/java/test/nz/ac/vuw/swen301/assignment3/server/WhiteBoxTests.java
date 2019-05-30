package test.nz.ac.vuw.swen301.assignment3.server;
import nz.ac.vuw.swen301.assignment3.server.Log;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class WhiteBoxTests {

    @Test
    public void test1() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Log service = new Log();
        service.doGet(request, response);
        assertEquals(400, response.getStatus());
    }
}
