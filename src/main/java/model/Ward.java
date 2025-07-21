package model;

import lombok.Data;

import java.sql.Timestamp;

public class Ward {
    private Before before;
    private After after;
    private Source source;
    private String op;
    private long ts_ms;
    private String transaction;

    @Data
    private static class Before {
        private String id;
        private String ward;
        private String code;
        private String eng_name;
        private String level;
        private String district_id;
        private String deleted_flag;
        private Timestamp valid_date;
    }

    @Data
    private static class After {
        private String id;
        private String ward;
        private String code;
        private String eng_name;
        private String level;
        private String district_id;
        private String deleted_flag;
        private Timestamp valid_date;
    }

    @Data
    private static class Source {
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
