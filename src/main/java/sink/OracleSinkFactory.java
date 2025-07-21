package sink;

import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.io.Serializable;
import java.sql.PreparedStatement;

public class OracleSinkFactory {

    private static final String DRIVER = "oracle.jdbc.OracleDriver";

    public static <T> void addOracleSink(
            DataStream<T> stream,
            String insertSql,
            BiSetter<T> setter,
            String url,
            String user,
            String pwd,
            String job
    ) {
        stream.addSink(
                JdbcSink.sink(
                        insertSql,
                        (PreparedStatement ps, T record) -> {
                            try {
                                setter.set(ps, record);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        },
                        JdbcExecutionOptions.builder()
                                .withBatchSize(1)
                                .withBatchIntervalMs(100)
                                .withMaxRetries(3)
                                .build(),
                        new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                                .withUrl(url)
                                .withDriverName(DRIVER)
                                .withUsername(user)
                                .withPassword(pwd)
                                .build()
                )
        ).name(job);
    }

    public interface BiSetter<T> extends Serializable {
        void set(PreparedStatement ps, T record) throws Exception;
    }
}
