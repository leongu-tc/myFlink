package leongu.myflink.util;

import java.io.File;

public class Constants {
	public static String projPath = new File(
		Thread.currentThread().getContextClassLoader().getResource("")
			.getPath()).getParentFile().getParentFile()
		.getPath();
	public static String res = projPath + "/src/main/resources/";
	public static String target = projPath + "/target/";
}
