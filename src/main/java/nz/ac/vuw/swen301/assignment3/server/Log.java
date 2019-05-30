package nz.ac.vuw.swen301.assignment3.server;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Log extends HttpServlet {
    public static String[] database;

    public Log(){
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String n = request.getParameter("name");
        if(n == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        String names = Arrays.stream(database).filter(name ->  name.startsWith("K")).collect(Collectors.joining(" "));
        out.println(names);
        out.close();
    }
    public void doPost(String id){

    }
}
