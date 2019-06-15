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


public class LogServlet extends HttpServlet {
    public static int maxSize = 50;
    public static ArrayList<LogEvent> database = new ArrayList<>();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(request.getParameter("Limit") == null || request.getParameter("Level") == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }if(Integer.parseInt(request.getParameter( "Limit")) < 0 || Integer.parseInt(request.getParameter("Limit")) > maxSize ){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        int limit = Integer.parseInt(request.getParameter("Limit"));
        System.out.println(request.getParameter("Level"));
        System.out.println(request.getParameter("Limit"));
        int count = 0;
        PrintWriter out = response.getWriter();
        out.write("[");
        out.write("\n");
        System.out.println(database.size());
        for(int i = database.size() - 1;i >= 0 && count < limit;i--){
            if(count > Integer.parseInt(request.getParameter("Limit"))){
                break;
            }else if(database.get(i).getLevel().getValue() <= LogEvent.LevelEnum.valueOf(request.getParameter("Level")).getValue()){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", database.get(i).getId());
                jsonObject.put("message", database.get(i).getMessage());
                jsonObject.put("timestamp", database.get(i).getTimestamp());
                jsonObject.put("thread", database.get(i).getThread());
                jsonObject.put("logger", database.get(i).getLogger());
                jsonObject.put("level", database.get(i).getLevel().name());
                jsonObject.put("errorDetails", "");
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
            BufferedReader br = request.getReader();
//        jsonString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            String jsonLine;
        while((jsonLine = br.readLine())!= null){
            sb.append(jsonLine);
        }
        System.out.println(sb.toString());
        JSONObject json;
        JSONArray jsonArray = new JSONArray(sb.toString());
//            for (int i = 0; i < jsonArray.length(); i++) {
//                System.out.println(jsonArray.get(i).toString());
//            }
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
            e.printStackTrace();
        }



    }



}


