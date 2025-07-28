package job;

import base.BaseJob;
import config.AppConfig;
import model.Ward;
import org.apache.flink.streaming.api.datastream.DataStream;
import sink.OracleSinkFactory;
import transformer.WardTransformer;
import util.SqlUtils.*;

public class WardJob extends BaseJob<Ward> {

    @Override
    protected Class<Ward> getEntityClass() {
        return Ward.class;
    }

    @Override
    protected String getTopicConfigKey() {
        return AppConfig.get("kafka.ward.topic");
    }

    @Override
    protected String getGroupConfigKey() {
        return AppConfig.get("kafka.ward.group");
    }

    @Override
    protected String getSourceName() {
        return "Ward Source";
    }

    @Override
    protected String getJobName() {
        return "Ward Job";
    }

    @Override
    protected String getJobConfigKey() {
        return AppConfig.get("ward.job");
    }

    @Override
    protected String getInsertSql() {
        return SqlWard.INSERT_SQL;
    }

    @Override
    protected String getUpdateSql() {
        return SqlWard.UPDATE_SQL;
    }

    @Override
    protected String getDeleteSql() {
        return SqlWard.DELETE_SQL;
    }

    @Override
    protected void addInsertSink(DataStream<Ward> stream) {
        // Filter stream to include only records with after data and insert/read operations
        DataStream<Ward> filteredStream = stream
                .filter(WardTransformer::hasAfterData)
                .filter(WardTransformer::isInsertOrReadOperation);

        // Use OracleSinkFactory with filtered stream
        OracleSinkFactory.addOracleSink(
                filteredStream,
                getInsertSql(),
                (ps, record) -> {
                    try {
                        var insert = new WardTransformer();
                        insert.setInsertParameters(ps, record);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to process insert record: " + record, e);
                    }
                },
                AppConfig.get("oracle.url"),
                AppConfig.get("oracle.user"),
                AppConfig.get("oracle.password"),
                "insert." + getJobConfigKey()
        );
    }

    @Override
    protected void addUpdateSink(DataStream<Ward> stream) {
        DataStream<Ward> filteredStream = stream
                .filter(WardTransformer::hasAfterData)
                .filter(WardTransformer::isUpdateOperation);

        OracleSinkFactory.addOracleSink(
                filteredStream,
                getUpdateSql(),
                (ps, record) -> {
                    try {
                        var update = new WardTransformer();
                        update.setUpdateParameters(ps, record);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to process update record: " + record, e);
                    }
                },
                AppConfig.get("oracle.url"),
                AppConfig.get("oracle.user"),
                AppConfig.get("oracle.password"),
                "update." + getJobConfigKey()
        );
    }

    @Override
    protected void addDeleteSink(DataStream<Ward> stream) {
        DataStream<Ward> filteredStream = stream
                .filter(WardTransformer::hasAfterData)
                .filter(WardTransformer::isDeleteOperation);

        OracleSinkFactory.addOracleSink(
                filteredStream,
                getDeleteSql(),
                (ps, record) -> {
                    try {
                        var delete = new WardTransformer();
                        delete.setDeleteParameters(ps, record);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to process delete record: " + record, e);
                    }
                },
                AppConfig.get("oracle.url"),
                AppConfig.get("oracle.user"),
                AppConfig.get("oracle.password"),
                "delete." + getJobConfigKey()
        );
    }
}
