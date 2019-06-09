package nz.ac.vuw.swen301.assignment3.server;


import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

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
        String jsonString = "";
        String jsonLine;
        BufferedReader br = request.getReader();
        while((jsonLine = br.readLine())!= null){
            jsonString += jsonLine;
        }
        JSONObject json;
        JSONArray jsonArray = new JSONArray(jsonString);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        Date timestamp = null;
        for(int i =0; i< jsonArray.length(); i++){
           json = jsonArray.getJSONObject(i);
           String message = json.getString("message");
            try {
                timestamp = dateFormat.parse(json.getString("timestamp"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String thread = json.getString("thread");
            String logger = json.getString("logger");
            String level = json.getString("level");
            LogEvent event = new LogEvent(UUID.randomUUID(), message,timestamp,thread,logger,level);
            database.add(event);
        }


    }

}


//           if(json.has("timestamp")){
//               try {
//                   SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
//                   Date date = dateFormat.parse(database.get(i).getString("timestamp"));
//                   json.remove("timestamp");
//                   json.put("timestamp", date);
//               } catch (ParseException e) {
//                   e.printStackTrace();
//               }
//           }