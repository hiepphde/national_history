package job;

import config.AppConfig;
import model.Province;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import sink.OracleSinkFactory;
import deserialize.JsonDeserializationSchema;


import java.util.Objects;

import static util.DatabaseUtils.*;

public class ProvinceJob {

    // SQL Constants
    private static final String insertSql = "insert into PROVINCES (ID, PROVINCE, CODE, ENG_NAME, LEVEL_NAME, VERSION, DELETED_FLAG, ISSUE_DATE, NOTE, IS_ACTIVE) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String updateSql = "update PROVINCES set PROVINCE = ?,CODE = ?,ENG_NAME = ?,LEVEL_NAME = ?,VERSION = ?,DELETED_FLAG = ?,ISSUE_DATE = ?,NOTE = ?,IS_ACTIVE = ? where ID = ?";
    private static final String deleteSql = "delete from PROVINCES where ID = ?";

    public static void build(StreamExecutionEnvironment env) {
        // Create Province objects
        JsonDeserializationSchema<Province> provinceDeserializer = new JsonDeserializationSchema<>(Province.class);

        // Create source from Kafka
        KafkaSource<Province> provinceSource = KafkaSource.<Province>builder()
                .setBootstrapServers(AppConfig.get("kafka.bootstrap.servers"))
                .setTopics(AppConfig.get("kafka.province.topic"))
                .setGroupId(AppConfig.get("kafka.province.group"))
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(provinceDeserializer)
                .build();

        DataStream<Province> jsonStream = env.fromSource(provinceSource, WatermarkStrategy.noWatermarks(),"Province Source");

        // Add null filtering at the beginning to prevent NPE
        DataStream<Province> provinceStream = jsonStream.filter(Objects::nonNull);

        DataStream<Province> insertStream = provinceStream
                .filter(p -> p.getAfter() != null)
                .filter(p -> p.getOp().equals("c") || p.getOp().equals("r"));

        DataStream<Province> updateStream = provinceStream
                .filter(p -> p.getAfter() != null)
                .filter(p -> p.getOp().equals("u"));

        DataStream<Province> deleteStream = provinceStream
                .filter(p -> p.getBefore() != null)
                .filter(p -> p.getOp().equals("d"));

        // Sink to Oracle
        OracleSinkFactory.addOracleSink(
                insertStream,
                insertSql,
                (ps, record) -> {
                    try {
                        Province.After a = record.getAfter();
                        // Add null check for After object
                        if (a == null) {
                            throw new IllegalArgumentException("Province.after is null on insert record: " + record);
                        }
                        safeSetString(ps, 1, a.getId());
                        safeSetString(ps, 2, a.getProvince());
                        safeSetString(ps, 3, a.getCode());
                        safeSetString(ps, 4, a.getEng_name());
                        safeSetString(ps, 5, a.getLevel());
                        safeSetString(ps, 6, a.getVersion());
                        safeSetString(ps, 7, a.getDeleted_flag());
                        ps.setTimestamp(8, a.getIssue_date());
                        safeSetString(ps, 9, a.getNote());
                        safeSetBool(ps, 10, a.getIs_active());
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to process insert record: " + record, e);
                    }
                },
                AppConfig.get("oracle.url"),
                AppConfig.get("oracle.user"),
                AppConfig.get("oracle.password"),
                "insert." + AppConfig.get("province.job")
        );

        OracleSinkFactory.addOracleSink(
                updateStream,
                updateSql,
                (ps, p) -> {
                    try {
                        Province.After a = p.getAfter();
                        // Add null check for After object
                        if (a == null) {
                            throw new IllegalArgumentException("Province.after is null on update record: " + p);
                        }
                        safeSetString(ps, 1, a.getProvince());
                        safeSetString(ps, 2, a.getCode());
                        safeSetString(ps, 3, a.getEng_name());
                        safeSetString(ps, 4, a.getLevel());
                        safeSetString(ps, 5, a.getVersion());
                        safeSetString(ps, 6, a.getDeleted_flag());
                        ps.setTimestamp(7, a.getIssue_date());
                        safeSetString(ps, 8, a.getNote());
                        safeSetBool(ps, 9, a.getIs_active());
                        safeSetString(ps, 10, a.getId());
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to process update record: " + p, e);
                    }
                },
                AppConfig.get("oracle.url"),
                AppConfig.get("oracle.user"),
                AppConfig.get("oracle.password"),
                "update." + AppConfig.get("province.job")
        );

        OracleSinkFactory.addOracleSink(
                deleteStream,
                deleteSql,
                (ps, p) -> {
                    try {
                        // Since we already filtered, these should never be null, but add safety check
                        if (p == null || p.getBefore() == null) {
                            throw new IllegalArgumentException("Province or Province.before is null on delete record: " + p);
                        }
                        safeSetString(ps, 1, p.getBefore().getId());
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to process delete record: " + p, e);
                    }
                },
                AppConfig.get("oracle.url"),
                AppConfig.get("oracle.user"),
                AppConfig.get("oracle.password"),
                "delete." + AppConfig.get("province.job")
        );
    }
}

