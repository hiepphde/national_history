package base;

import config.AppConfig;
import deserialize.JsonDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import sink.OracleSinkFactory;

import java.sql.PreparedStatement;
import java.util.Objects;

public abstract class BaseJob<T> {
    protected static final String INSERT_OP = "c";
    protected static final String UPDATE_OP = "u";
    protected static final String DELETE_OP = "d";
    protected static final String READ_OP = "r";

    public void build(StreamExecutionEnvironment env) {
        try {
            // Create deserializer
            JsonDeserializationSchema<T> deserializer = new JsonDeserializationSchema<>(getEntityClass());

            // Create Kafka source
            KafkaSource<T> source = KafkaSource.<T>builder()
                    .setBootstrapServers(AppConfig.get("kafka.bootstrap.servers"))
                    .setTopics(getTopicConfigKey())
                    .setGroupId(getGroupConfigKey())
                    .setStartingOffsets(OffsetsInitializer.earliest())
                    .setValueOnlyDeserializer(deserializer)
                    .build();

            DataStream<T> jsonStream = env.fromSource(source, WatermarkStrategy.noWatermarks(), getSourceName());

            // Filter null records
            DataStream<T> filteredStream = jsonStream.filter(Objects::nonNull);

            // Create operation streams
            DataStream<T> insertStream = createInsertStream(filteredStream);
            DataStream<T> updateStream = createUpdateStream(filteredStream);
            DataStream<T> deleteStream = createDeleteStream(filteredStream);

            // Add sinks
            addInsertSink(insertStream);
            addUpdateSink(updateStream);
            addDeleteSink(deleteStream);

        } catch (Exception e) {
            System.err.println("✗ Error configuring " + getJobName() + ": " + e.getMessage());
            throw new RuntimeException("Failed to build job: " + getJobName(), e);
        }
    }

    protected DataStream<T> createInsertStream(DataStream<T> stream) {
        return stream
                .filter(this::hasAfterData)
                .filter(record -> isInsertOrReadOperation(getOperation(record)));
    }

    protected DataStream<T> createUpdateStream(DataStream<T> stream) {
        return stream
                .filter(this::hasAfterData)
                .filter(record -> UPDATE_OP.equals(getOperation(record)));
    }

    protected DataStream<T> createDeleteStream(DataStream<T> stream) {
        return stream
                .filter(this::hasBeforeData)
                .filter(record -> DELETE_OP.equals(getOperation(record)));
    }

    private boolean isInsertOrReadOperation(String op) {
        return INSERT_OP.equals(op) || READ_OP.equals(op);
    }

    protected void addInsertSink(DataStream<T> stream) {
        OracleSinkFactory.addOracleSink(
                stream,
                getInsertSql(),
                this::setInsertParameters,
                AppConfig.get("oracle.url"),
                AppConfig.get("oracle.user"),
                AppConfig.get("oracle.password"),
                "insert." + getJobConfigKey()
        );
    }

    protected void addUpdateSink(DataStream<T> stream) {
        OracleSinkFactory.addOracleSink(
                stream,
                getUpdateSql(),
                this::setUpdateParameters,
                AppConfig.get("oracle.url"),
                AppConfig.get("oracle.user"),
                AppConfig.get("oracle.password"),
                "update." + getJobConfigKey()
        );
    }

    protected void addDeleteSink(DataStream<T> stream) {
        OracleSinkFactory.addOracleSink(
                stream,
                getDeleteSql(),
                this::setDeleteParameters,
                AppConfig.get("oracle.url"),
                AppConfig.get("oracle.user"),
                AppConfig.get("oracle.password"),
                "delete." + getJobConfigKey()
        );
    }

    protected abstract Class<T> getEntityClass();
    protected abstract String getTopicConfigKey();
    protected abstract String getGroupConfigKey();
    protected abstract String getSourceName();
    protected abstract String getJobName();
    protected abstract String getJobConfigKey();
    protected abstract String getInsertSql();
    protected abstract String getUpdateSql();
    protected abstract String getDeleteSql();
    protected abstract boolean hasAfterData(T record);
    protected abstract boolean hasBeforeData(T record);
    protected abstract String getOperation(T record);
    protected abstract void setInsertParameters(PreparedStatement ps, T record) throws Exception;
    protected abstract void setUpdateParameters(PreparedStatement ps, T record) throws Exception;
    protected abstract void setDeleteParameters(PreparedStatement ps, T record) throws Exception;
}
