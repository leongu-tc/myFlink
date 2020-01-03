package leongu.myflink.basic;

public class App {
	public static void main(String[] args) {
		try {
			System.out.println(Class.forName("string"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
