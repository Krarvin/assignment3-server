package nz.ac.vuw.swen301.assignment3.server;

import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Log extends HttpServlet {
/*    public static ArrayList<String> database = new ArrayList<String>();*/
    public static HashMap<String, Integer> database = new HashMap<String, Integer>();
    public static int maxSize = 1000;

    public Log(){
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(request.getParameter("Limit").isEmpty() || request.getParameter("Level").isEmpty()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String limit = request.getParameter("Limit");
        String level = request.getParameter("Level");
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        int count = 0;
        for(String key: database.keySet()){
            if(count < Integer.parseInt(limit)) {
                if (database.get(key) < Integer.parseInt(level)) {
                    out.println(key);
                    count++;
                }
            }else{
                break;
            }
        }
        out.close();
    }

    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException{
        if(request.getParameter("Message").isEmpty() || request.getParameter("Message") == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String s = request.getParameter("Message");
        Layout layout = new LogLayout("Priority - ${Priority} , Category - ${Category}, Date - ${Date}, Message - ${Message}");
        LogAppender appender = new LogAppender(layout);
        Logger logger = Logger.getLogger("Server");
        logger.addAppender(appender);
        logger.info(s);
        PrintWriter out = response.getWriter();
        if(database.size() == this.maxSize) {
            database.remove(database.get(database.keySet().toArray()[0]));
            database.put(appender.getCurrentLogs().get(0), 4);
        }else{
            database.put(appender.getCurrentLogs().get(0), 4);
        }

        for(String key: database.keySet()){
            out.println(key);
        }
        out.close();

    }

}
