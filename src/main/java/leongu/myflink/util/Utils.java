package leongu.myflink.util;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.File;

public class Utils {

	/**
	 * local run with web ui localhost:18081
	 * @return
	 */
	public static StreamExecutionEnvironment localEnv() {
		Configuration flink_conf = new Configuration();
		flink_conf.setLong("rest.port", 18081);
		StreamExecutionEnvironment env = StreamExecutionEnvironment
			.createLocalEnvironmentWithWebUI(flink_conf);
		return env;
	}

	/**
	 * mac standalone cluster localhost:8081 , with myflink.jar
	 * @return
	 */
	public static StreamExecutionEnvironment remoteEnv() {
		String jarPath = Constants.target + "myflink-1.0.0.jar";
		System.out.println(jarPath);
		StreamExecutionEnvironment env = StreamExecutionEnvironment
			.createRemoteEnvironment("localhost", 8081, jarPath);
		return env;
	}

	public static void deleteDir(File dir) {
		if (!dir.exists()) {
			return;
		}
		File[] files = dir.listFiles();
		for (File f : files) {
			if (f.isDirectory()) {
				deleteDir(f);
			} else {
				f.delete();
				System.out.println("delete file " + f.getAbsolutePath());
			}
		}
		dir.delete();
		System.out.println("delete dir " + dir.getAbsolutePath());
	}

	public static void deleteDir(String dir) {
		deleteDir(new File(dir));
	}
}
