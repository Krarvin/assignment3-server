package nz.ac.vuw.swen301.assignment3.server;


import com.google.gson.Gson;
import org.apache.commons.compress.utils.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

public class LogServlet extends HttpServlet {
    public static int maxSize = 50;
    public static ArrayList<LogEvent> database = new ArrayList<>();


//    public enum LEVEL
//    {
//        TRACE(5),DEBUG(4),INFO(3),WARN(2),ERROR(1),FATAL(0);
//        private int levVal;
//        LEVEL(int levVal){
//            this.levVal = levVal;
//        }
//
//        public int getlevVal(){
//            return this.getlevVal();
//        }
//    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(request.getParameter("Limit").isEmpty() || request.getParameter("Level").isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }if(Integer.parseInt(request.getParameter("Limit")) < 0 || Integer.parseInt(request.getParameter("Limit")) > maxSize ){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        int count = 0;
        PrintWriter out = response.getWriter();
        for(int i = database.size(); i <= database.size();i--){
            if(count > Integer.parseInt(request.getParameter("Limit"))){
                break;
            }else if(database.get(i).getLevel().getValue() <= LogEvent.LevelEnum.valueOf(request.getParameter("Level")).getValue()){
                String json = database.get(i).toString();
                out.write(json);
                count++;
            }
        }
        out.close();
    }

    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException{
        request.setCharacterEncoding("UTF-8");
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = request.getReader();
//        jsonString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            String jsonLine;
        while((jsonLine = br.readLine())!= null){
            sb.append(jsonLine);
        }
        PrintWriter out = response.getWriter();
        JSONObject json;
            JSONArray jsonArray = new JSONArray(sb.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                System.out.println(jsonArray.get(i).toString());
            }
        for(int i =0; i< jsonArray.length(); i++) {
            json = jsonArray.getJSONObject(i);
            String id = json.getString("id");
            String message = json.getString("message");
            String timestamp = json.getString("timestamp");
            String thread = json.getString("thread");
            String logger = json.getString("logger");
            String level = json.getString("level");
            LogEvent event = new LogEvent(id, message, timestamp, thread, logger, level);
            database.add(event);
        }
        out.close();
        }catch (JSONException e) {
            e.printStackTrace();
        }



    }



}


