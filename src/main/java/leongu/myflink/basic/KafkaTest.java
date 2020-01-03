package leongu.myflink.basic;

import leongu.myflink.util.Utils;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010;
import org.apache.flink.streaming.connectors.kafka.Kafka010TableSource;
import org.apache.flink.streaming.connectors.wikiedits.WikipediaEditEvent;
import org.apache.flink.streaming.connectors.wikiedits.WikipediaEditsSource;
import org.apache.flink.streaming.kafka.test.base.CustomWatermarkExtractor;
import org.apache.flink.streaming.kafka.test.base.KafkaEvent;
import org.apache.flink.streaming.kafka.test.base.KafkaEventSchema;
import org.apache.flink.streaming.kafka.test.base.RollingAdditionMapper;

public class KafkaTest {

	public static void main(String[] args) throws Exception {
		// 我们可以自己制定 --input-topic 这样的自定义参数，也可以制定  bootstrap.servers 的 kafka properties
		// --input-topic topic1
		// --output-topic topic2
		// --bootstrap.servers kafka0:9092
		// --zookeeper.connect zookeeper0:2181
		// --group.id myconsumer

		final ParameterTool parameterTool = ParameterTool.fromArgs(args);
		StreamExecutionEnvironment env = Utils.localEnv();
		env.getConfig().disableSysoutLogging();
		env.getConfig().setRestartStrategy(
			RestartStrategies.fixedDelayRestart(4, 10000));
		env.enableCheckpointing(5000); // create a checkpoint every 5 seconds
		env.getConfig().setGlobalJobParameters(parameterTool); // make parameters available in the web interface
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

		// KAFKA EVENT:
		// word1,1,0
		// word2,2,5000
		DataStream<KafkaEvent> input = env
			.addSource(
				new FlinkKafkaConsumer010<>(
					parameterTool.getRequired("input-topic"),
					new KafkaEventSchema(),
					parameterTool.getProperties())
					.assignTimestampsAndWatermarks(new CustomWatermarkExtractor()))
			.keyBy("word")
			.map(new RollingAdditionMapper());

		input.addSink(
			new FlinkKafkaProducer010<>(
				parameterTool.getRequired("output-topic"),
				new KafkaEventSchema(),
				parameterTool.getProperties()));

		env.execute("Kafka 0.10 Example");
	}

}
