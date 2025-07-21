package job;

import config.AppConfig;
import deserialize.JsonDeserializationSchema;
import model.District;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import sink.OracleSinkFactory;

import java.util.Objects;

import static util.DatabaseUtils.*;

public class DistrictJob{

    private static final String insertSql = "insert into DISTRICTS (ID, DISTRICT, CODE, ENG_NAME, LEVEL_NAME, PROVINCE_ID, DELETED_FLAG, VALID_DATE) values (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String updateSql = "update DISTRICTS set DISTRICT = ?,CODE = ?,ENG_NAME = ?,LEVEL_NAME = ?,PROVINCE_ID = ?,DELETED_FLAG = ?,VALID_DATE = ? where ID = ?";
    private static final String deleteSql = "delete from DISTRICTS where ID = ?";

    public static void build(StreamExecutionEnvironment env) {

        try {
            // Create District objects
            JsonDeserializationSchema<District> districtDeserializer = new JsonDeserializationSchema<>(District.class);

            // Create source from Kafka
            KafkaSource<District> districtSource = KafkaSource.<District>builder()
                    .setBootstrapServers(AppConfig.get("kafka.bootstrap.servers"))
                    .setTopics(AppConfig.get("kafka.district.topic"))
                    .setGroupId(AppConfig.get("kafka.district.group"))
                    .setStartingOffsets(OffsetsInitializer.earliest())
                    .setValueOnlyDeserializer(districtDeserializer)
                    .build();

            DataStream<District> jsonStream = env.fromSource(districtSource, WatermarkStrategy.noWatermarks(),"District Source");

            // Add null filtering at the beginning to prevent NPE
            DataStream<District> districtStream = jsonStream.filter(Objects::nonNull);

            DataStream<District> insertStream = districtStream
                    .filter(p -> p.getAfter() != null)
                    .filter(p -> p.getOp().equals("c") || p.getOp().equals("r"));

            DataStream<District> updateStream = districtStream
                    .filter(p -> p.getAfter() != null)
                    .filter(p -> p.getOp().equals("u"));

            DataStream<District> deleteStream = districtStream
                    .filter(p -> p.getBefore() != null)
                    .filter(p -> p.getOp().equals("d"));

            insertStream.print();
            OracleSinkFactory.addOracleSink(
                    insertStream,
                    insertSql,
                    (ps, record) -> {
                        try {
                            District.After after = record.getAfter();
                            if (after == null) {
                                throw new IllegalArgumentException("District.after is null on insert record: " + record);
                            }
                            safeSetString(ps, 1, after.getId());
                            safeSetString(ps, 2, after.getDistrict());
                            safeSetString(ps, 3, after.getCode());
                            safeSetString(ps, 4, after.getEng_name());
                            safeSetString(ps, 5, after.getLevel());
                            safeSetString(ps, 6, after.getProvince_id());
                            safeSetString(ps, 7, after.getDeleted_flag());
                            safeSetTimestamp(ps, 8, after.getValid_date());
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to process insert record: " + record, e);
                        }
                    },
                    AppConfig.get("oracle.url"),
                    AppConfig.get("oracle.user"),
                    AppConfig.get("oracle.password"),
                    "insert." + AppConfig.get("district.job")
            );

            updateStream.print();
            OracleSinkFactory.addOracleSink(
                    updateStream,
                    updateSql,
                    (ps, record) -> {
                        try {
                            District.After after = record.getAfter();
                            if (after == null) {
                                throw new IllegalArgumentException("District.after is null on update record: " + record);
                            }
                            safeSetString(ps, 1, after.getDistrict());
                            safeSetString(ps, 2, after.getCode());
                            safeSetString(ps, 3, after.getEng_name());
                            safeSetString(ps, 4, after.getLevel());
                            safeSetString(ps, 5, after.getProvince_id());
                            safeSetString(ps, 6, after.getDeleted_flag());
                            safeSetTimestamp(ps, 7, after.getValid_date());
                            safeSetString(ps, 8, after.getId()); // WHERE clause
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to process update record: " + record, e);
                        }
                    },
                    AppConfig.get("oracle.url"),
                    AppConfig.get("oracle.user"),
                    AppConfig.get("oracle.password"),
                    "update." + AppConfig.get("province.job")
            );

            deleteStream.print();
            OracleSinkFactory.addOracleSink(
                    deleteStream,
                    deleteSql,
                    (ps, p) -> {
                        try {
                            // Since we already filtered, these should never be null, but add safety check
                            if (p == null || p.getBefore() == null) {
                                throw new IllegalArgumentException("District or District.before is null on delete record: " + p);
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

        } catch (Exception e) {
            System.err.println("✗ Error configuring District Job: " + e.getMessage());
        }
    }
}
