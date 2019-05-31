package nz.ac.vuw.swen301.assignment3.server;

import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Log extends HttpServlet {
    public static String[] database;
    public static int maxSize;

    public Log(){
        this.maxSize = 100;
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
        out.println("asdf");
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
        database = new String[appender.getCurrentLogs().size()];
        for(int i = 0; i<appender.getCurrentLogs().size(); i++){
            database[i] = appender.getCurrentLogs().get(i);
            out.println(database[i]);
        }
        out.close();

    }

}
