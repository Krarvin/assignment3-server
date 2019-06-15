package nz.ac.vuw.swen301.assignment3.server;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import nz.ac.vuw.swen301.assignment3.server.Pair;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class StatManager extends HttpServlet {
    public static HashMap<Pair,Integer> stats = new HashMap<>();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Log Stats");
        ArrayList<LogEvent> database = LogServlet.database;
        Set<String> loggerSet = getLoggers(database);
        Set<String> levelSet = getLevels(database);
        Set<String> threadSet = getThreads(database);
        Set<String> dateSet = getDates(database);
        int rownum = 0;
        int cellnum = 1;
        Row row = sheet.createRow(rownum++);
        for(String date: dateSet){
            Cell cell = row.createCell(cellnum++);
            cell.setCellValue(date);
        }
        for(String logger: loggerSet){
            cellnum = 0;
            Row loggerRow = sheet.createRow(rownum++);
            Cell loggerCell = loggerRow.createCell(cellnum);
            loggerCell.setCellValue(logger);
        }
        for(String thread: threadSet){
            Row threadRow = sheet.createRow(rownum++);
            Cell threadCell = threadRow.createCell(cellnum);
            threadCell.setCellValue(thread);
        }
        for(String level:levelSet){
            Row levelRow = sheet.createRow(rownum++);
            Cell levelCell = levelRow.createCell(cellnum);
            System.out.println(level);
            levelCell.setCellValue(level);
        }

        for(LogEvent event: database) {
            Pair levelpair = new Pair(event.getLevel().name(), event.getTimestamp().substring(0,10));
            if (stats.containsKey(levelpair)) {
                stats.put(levelpair, stats.get(levelpair) + 1);
            } else {
                stats.put(levelpair, 1);
            }
            Pair threadpair = new Pair(event.getThread(), event.getTimestamp().substring(0,10));
            if (stats.containsKey(threadpair)) {
                stats.put(threadpair, stats.get(threadpair) + 1);
            } else {
                stats.put(threadpair, 1);
            }
            Pair loggerpair = new Pair(event.getLogger(), event.getTimestamp().substring(0,10));
            if (stats.containsKey(loggerpair)) {
                stats.put(loggerpair, stats.get(loggerpair) + 1);
            } else {
                stats.put(loggerpair, 1);
            }
        }
        int count = 0;
        for(Pair key: stats.keySet()) {
        for(int i = 1; i < sheet.getLastRowNum() + 1; i++){
                if (sheet.getRow(i).getCell(0).toString().equals(key.getFirst())){
                    if(sheet.getRow(0).getCell(1) != null) {
                        if (sheet.getRow(0).getCell(1).toString().equals(key.getSecond()) && sheet.getRow(i)!= null) {
                            if(sheet.getRow(i).getCell(1)!= null) {
                                sheet.getRow(i).getCell(1).setCellValue(stats.get(key));
                            }else{
                                sheet.getRow(i).createCell(1);
                                sheet.getRow(i).getCell(1).setCellValue(stats.get(key));
                            }
                        }
                    }
                }
            }
        }
        try{
            FileOutputStream fileOut = new FileOutputStream("log-statistics.xlsx");
            workbook.write(fileOut);
            fileOut.close();
        }
        catch(Exception e){

        }
    }

    public Set<String> getLoggers(ArrayList<LogEvent> database){
        Set<String> loggerSet = new HashSet<>();
        for(LogEvent event: database){
            loggerSet.add(event.getLogger());
        }
        return loggerSet;
    }

    public Set<String> getLevels(ArrayList<LogEvent> database){
        Set<String> levelSet = new HashSet<>();
        for(LogEvent event: database){
            levelSet.add(event.getLevel().name());
        }
        return levelSet;
    }

    public Set<String> getThreads(ArrayList<LogEvent> database){
        Set<String> threadSet = new HashSet<>();
        for(LogEvent event: database){
            threadSet.add(event.getThread());
        }
        return threadSet;
    }

    public Set<String> getDates(ArrayList<LogEvent> database){
        Set<String> DateSet = new HashSet<>();
        for(LogEvent event: database){
            DateSet.add(event.getTimestamp().substring(0,10));
        }
        return DateSet;
    }

    }