package base;

import config.AppConfig;
import deserialize.JsonDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Objects;

public abstract class BaseJob<T> {

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

            // Add sinks
            addInsertSink(filteredStream);
            addUpdateSink(filteredStream);
            addDeleteSink(filteredStream);

        } catch (Exception e) {
            System.err.println("✗ Error configuring " + getJobName() + ": " + e.getMessage());
            throw new RuntimeException("Failed to build job: " + getJobName(), e);
        }
    }

    protected void addInsertSink(DataStream<T> stream) {}

    protected void addUpdateSink(DataStream<T> stream) {}

    protected void addDeleteSink(DataStream<T> stream) {}

    protected abstract Class<T> getEntityClass();
    protected abstract String getTopicConfigKey();
    protected abstract String getGroupConfigKey();
    protected abstract String getSourceName();
    protected abstract String getJobName();
    protected abstract String getJobConfigKey();
    protected abstract String getInsertSql();
    protected abstract String getUpdateSql();
    protected abstract String getDeleteSql();
}
