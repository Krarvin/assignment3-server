package nz.ac.vuw.swen301.assignment3.server;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class StatManager extends HttpServlet {

    public void doGet(HttpServletResponse request, HttpServletResponse response) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("student Details");
        ArrayList<JSONObject> database = LogServlet.database;
        int rownum = 0;
        for(int i = 0; i <database.size();i++){
            Row row = sheet.createRow(rownum++);
            int cellnum = 0;
            for(JSONObject o:database) {
                Cell cell = row.createCell(cellnum++);
            }

        }
    }

    }

