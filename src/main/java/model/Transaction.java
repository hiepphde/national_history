package model;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;

public class Transaction {
    private Long id;
    private String document;
    private String type;
    private LocalDateTime date;
    private Long materialId;
    private Double beginning;
    private Double inbound;
    private Double outbound;
    private Double hold;
    private Double ending;

    public Transaction() {
        this.document = document;
        this.type = type;
        this.date = date;
        this.materialId = materialId;
        this.beginning = beginning;
        this.inbound = inbound;
        this.outbound = outbound;
        this.hold = hold;
        this.ending = ending;
    }

    public static Transaction from(String doc, String type, String date, JsonNode batch) {
        Transaction t = new Transaction();
        return t;
    }
}
