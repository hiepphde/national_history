package job;

import base.JobFactory;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class DataStreamJob {

	public static void main(String[] args) throws Exception {
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

//		ProvinceJob.build(env);
//		DistrictJob.build(env);
		JobFactory.buildWardJob(env);

		// Execute program, beginning computation.
		env.execute("National Management");
	}
}
