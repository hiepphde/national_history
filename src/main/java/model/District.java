package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class District {
    private Before before;
    private After after;
    private Source source;
    private String op;
    private long ts_ms;
    private String transaction;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Before {
        private String id;
        private String district;
        private String code;
        private String eng_name;
        private String level;
        private String province_id;
        private String deleted_flag;
        private Timestamp valid_date;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class After {
        private String id;
        private String district;
        private String code;
        private String eng_name;
        private String level;
        private String province_id;
        private String deleted_flag;
        private Timestamp valid_date;
    }

    @Data
    public static class Source {
        private String version;
        private String connector;
        private String name;
        private Timestamp ts_ms;
        private String snapshot;
        private String db;
        private String sequence;
        private String schema;
        private String table;
        private long txId;
        private long lsn;
        private Object xmin;
    }
}
