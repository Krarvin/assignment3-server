package nz.ac.vuw.swen301.assignment3.server;


import com.google.gson.GsonBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class LogServlet extends HttpServlet {
    public static int maxSize = 50;
    public static ArrayList<LogEvent> database = new ArrayList<>();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(isEnum(request.getParameter("level"))==false){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if(request.getParameter("limit") == null || request.getParameter("level") == null ||request.getParameter("level").isEmpty() || request.getParameter("limit").isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }if(Integer.parseInt(request.getParameter( "limit")) < 0 || Integer.parseInt(request.getParameter("limit")) > maxSize ){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        int limit = Integer.parseInt(request.getParameter("limit"));
        System.out.println(request.getParameter("level"));
        System.out.println(request.getParameter("limit"));
        System.out.println(database.size());
        int count = 0;
        PrintWriter out = response.getWriter();
        out.write("[");
        out.write("\n");
        System.out.println(database.size());
        for(int i = database.size() - 1;i >= 0 && count < limit;i--){
            if(count > Integer.parseInt(request.getParameter("limit"))){
                break;
            }else if(database.get(i).getLevel().getValue() <= LogEvent.LevelEnum.valueOf(request.getParameter("level")).getValue()){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", database.get(i).getId());
                jsonObject.put("message", database.get(i).getMessage());
                jsonObject.put("timestamp", database.get(i).getTimestamp());
                jsonObject.put("thread", database.get(i).getThread());
                jsonObject.put("logger", database.get(i).getLogger());
                jsonObject.put("level", database.get(i).getLevel().name());
                jsonObject.put("errorDetails", "");
                System.out.println(jsonObject.toString());
                out.write(jsonObject.toString());
                if(count + 1 < limit) {
                    out.write(",\n");
                }
                count++;
            }
        }
        out.write("\n]");
        out.close();
    }

    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException{
        request.setCharacterEncoding("UTF-8");
        StringBuilder sb = new StringBuilder();
        try {
            if(request.getReader() == null){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            BufferedReader br = request.getReader();
//        jsonString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            String jsonLine;
        while((jsonLine = br.readLine())!= null){
            sb.append(jsonLine);
        }
        System.out.println(sb.toString());
        JSONObject json;
        if(!sb.toString().startsWith("[") || !sb.toString().endsWith("]")){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        JSONArray jsonArray = new JSONArray(sb.toString());
        if(jsonArray.length() == 0){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
//            for (int i = 0; i < jsonArray.length(); i++) {
//                System.out.println(jsonArray.get(i).toString());
//            }
            System.out.println(jsonArray.length());
        for(int i =0; i< jsonArray.length(); i++) {
            json = jsonArray.getJSONObject(i);
            String id = json.getString("id");
            String message = json.getString("message");
            String timestamp = json.getString("timestamp");
            String thread = json.getString("thread");
            String logger = json.getString("logger");
            String level = json.getString("level");
            LogEvent event = new LogEvent(id, message, timestamp, thread, logger, level);
//            System.out.println(database.size());
            database.add(event);
        }
        System.out.println(database.size());

        }catch (JSONException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }



    }

    private boolean isEnum(String s){
        Set<String> enumNames = new HashSet<>();
        for(LogEvent.LevelEnum e : LogEvent.LevelEnum.values()){
            enumNames.add(e.name());
        }
        if(enumNames.contains(s)){
            return true;
        }else{
            return false;
        }
    }



}


