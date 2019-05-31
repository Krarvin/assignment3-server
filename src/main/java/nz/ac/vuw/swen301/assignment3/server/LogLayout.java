package nz.ac.vuw.swen301.assignment3.server;

import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

import org.apache.log4j.Category;
import org.apache.log4j.Layout;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.ParseException;

public class LogLayout extends Layout {
    private String pattern;
    public LogLayout(String pattern){
        this.pattern = pattern;
    }
    public void activateOptions(){
    }

    @Override
    public String format(LoggingEvent event) {
        try {
            RuntimeServices runtimeServices = RuntimeSingleton.getRuntimeServices();
            StringReader reader = new StringReader(pattern);
            Template t = new Template();
            t.setRuntimeServices(runtimeServices);
            t.setData(runtimeServices.parse(reader, "template"));
            t.initDocument();
            VelocityContext ce = new VelocityContext();
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(event.getTimeStamp());
            ce.put("Category", event.getLoggerName());
            ce.put("Date", timeStamp);
            ce.put("Message", event.getMessage());
            ce.put("Priority", event.getLevel());
            StringWriter writer = new StringWriter();
            t.merge(ce, writer);
            return writer.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public boolean ignoresThrowable() {
        return false;
    }
}
