package nz.ac.vuw.swen301.assignment3.server;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;

import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.util.ArrayList;

public class StatManager extends HttpServlet {

    public void doGet() throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Log Stats");
        ArrayList<LogEvent> database = LogServlet.database;
        int rownum = 0;
        for(LogEvent event: database){
            Row row = sheet.createRow(rownum++);
            int cellnum = 0;
        }
    }

    }

