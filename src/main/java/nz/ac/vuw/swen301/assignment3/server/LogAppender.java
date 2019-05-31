package nz.ac.vuw.swen301.assignment3.server;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

import java.util.ArrayList;
import java.util.List;

public class LogAppender extends AppenderSkeleton {
    private ArrayList<String> currentLogs;
    private Layout layout;

    public LogAppender(Layout layout){
        this.currentLogs = new ArrayList<String>();
        this.layout = layout;
    }
    @Override
    protected void append(LoggingEvent event) {
        String s = layout.format(event);
        this.currentLogs.add(s);
    }

    public void close(){
        this.closed = true;
    }

    public boolean requiresLayout(){
        return false;
    }

    public ArrayList<String> getCurrentLogs(){
        return currentLogs;
    }
}
