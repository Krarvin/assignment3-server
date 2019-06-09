/*
 * Resthome for Logs
 * This is the log service API for ecs.vuw.ac.nz SWEN301 assignment 3
 *
 * OpenAPI spec version: 1.0.2
 * Contact: jens.dietrich@vuw.ac.nz
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package nz.ac.vuw.swen301.assignment3.server;

import java.util.Date;
import java.util.Objects;
import java.util.Arrays;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.IOException;
import java.util.UUID;
import org.threeten.bp.OffsetDateTime;

/**
 * LogEvent
 */
public class LogEvent {
    public LogEvent(UUID id, String message, Date timestamp, String thread, String logger, String level){
        this.id = id;
        this.message = message;
        this.timestamp = timestamp;
        this.thread = thread;
        this.logger = logger;
        this.level = LevelEnum.valueOf(level);

    }

    @SerializedName("id")
    private UUID id = null;

    @SerializedName("message")
    private String message = null;

    @SerializedName("timestamp")
    private Date timestamp = null;

    @SerializedName("thread")
    private String thread = null;

    @SerializedName("logger")
    private String logger = null;

    /**
     * Gets or Sets level
     */
    @JsonAdapter(LevelEnum.Adapter.class)
    public enum LevelEnum {

        DEBUG(0),

        INFO(1),

        WARN(2),

        ERROR(3),

        FATAL(4),

        TRACE(5);

        private int value;

        LevelEnum(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }


        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static LevelEnum fromValue(String text) {
            for (LevelEnum b : LevelEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

        public static class Adapter extends TypeAdapter<LevelEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final LevelEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public LevelEnum read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return LevelEnum.fromValue(String.valueOf(value));
            }
        }
    }

    @SerializedName("level")
    private LevelEnum level = null;

    @SerializedName("errorDetails")
    private String errorDetails = null;

    public LogEvent id(UUID id) {
        this.id = id;
        return this;
    }

    /**
     * Get id
     * @return id
     **/
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LogEvent message(String message) {
        this.message = message;
        return this;
    }

    /**
     * Get message
     * @return message
     **/
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LogEvent timestamp(Date timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    /**
     * see https://stackoverflow.com/questions/8405087/what-is-this-date-format-2011-08-12t201746-384z how to interface with this date format
     * @return timestamp
     **/
    @ApiModelProperty(example = "2019-07-29T09:12:33.001Z", required = true, value = "see https://stackoverflow.com/questions/8405087/what-is-this-date-format-2011-08-12t201746-384z how to interface with this date format")
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public LogEvent thread(String thread) {
        this.thread = thread;
        return this;
    }

    /**
     * Get thread
     * @return thread
     **/
    @ApiModelProperty(example = "main", required = true, value = "")
    public String getThread() {
        return thread;
    }

    public void setThread(String thread) {
        this.thread = thread;
    }

    public LogEvent logger(String logger) {
        this.logger = logger;
        return this;
    }

    /**
     * Get logger
     * @return logger
     **/
    @ApiModelProperty(example = "com.example.Foo", required = true, value = "")
    public String getLogger() {
        return logger;
    }

    public void setLogger(String logger) {
        this.logger = logger;
    }

    public LogEvent level(LevelEnum level) {
        this.level = level;
        return this;
    }

    /**
     * Get level
     * @return level
     **/
    @ApiModelProperty(example = "DEBUG", required = true, value = "")
    public LevelEnum getLevel() {
        return level;
    }

    public void setLevel(LevelEnum level) {
        this.level = level;
    }

    public LogEvent errorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
        return this;
    }

    /**
     * Get errorDetails
     * @return errorDetails
     **/
    @ApiModelProperty(value = "")
    public String getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LogEvent logEvent = (LogEvent) o;
        return Objects.equals(this.id, logEvent.id) &&
                Objects.equals(this.message, logEvent.message) &&
                Objects.equals(this.timestamp, logEvent.timestamp) &&
                Objects.equals(this.thread, logEvent.thread) &&
                Objects.equals(this.logger, logEvent.logger) &&
                Objects.equals(this.level, logEvent.level) &&
                Objects.equals(this.errorDetails, logEvent.errorDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, message, timestamp, thread, logger, level, errorDetails);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class LogEvent {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    message: ").append(toIndentedString(message)).append("\n");
        sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
        sb.append("    thread: ").append(toIndentedString(thread)).append("\n");
        sb.append("    logger: ").append(toIndentedString(logger)).append("\n");
        sb.append("    level: ").append(toIndentedString(level)).append("\n");
        sb.append("    errorDetails: ").append(toIndentedString(errorDetails)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}