package base;

import job.DistrictJob;
import job.ProvinceJob;
import job.WardJob;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class JobFactory {

    public static void buildAllJobs(StreamExecutionEnvironment env) {
        new WardJob().build(env);  // Method build() từ BaseJob được gọi
    }
    public static void buildWardJob(StreamExecutionEnvironment env) {
        new WardJob().build(env);
    }
}
