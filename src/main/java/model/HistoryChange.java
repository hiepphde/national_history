package model;

import lombok.Data;

import java.sql.Timestamp;

public class HistoryChange {
    private Before before;
    private After after;
    private Source source;
    private String op;
    private long ts_ms;
    private String transaction;

    @Data
    private static class Before {
        private String id;
        private String province_id;
        private String label;
        private String old_code;
        private String old_name;
        private String action;
        private String new_code;
        private String new_name;
        private String district_name;
        private Timestamp date_change;
        private String deleted_flag;
        private String district_code;
        private String note;
    }

    @Data
    private static class After {
        private String id;
        private String province_id;
        private String label;
        private String old_code;
        private String old_name;
        private String action;
        private String new_code;
        private String new_name;
        private String district_name;
        private Timestamp date_change;
        private String deleted_flag;
        private String district_code;
        private String note;
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
