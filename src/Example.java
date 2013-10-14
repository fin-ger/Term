@Termconf (usage = "java Example [option] [arguments]",
		example = "java Example -t ha 5")
public class Example {
	public static void main (String[] args) {
		Term.init ("Example", args);
	}
	
	@Termmeth (short_param = "t", description = "This is a test method",
			arguments = { "TEXT", "REPETITIONS" })
	public static void test (String a, int b) {
		String res = "";
		for (int i = 0; i < b; i++) {
			res += a;
		}
		System.out.println (res);
	}
	
	@Termmeth (short_param = "c", description = "Concatenate two strings",
			arguments = { "TEXT", "TEXT" })
	public static String cat (String a, String b) {
		return a + b;
	}
	
	@Termmeth (short_param = "m",
			description = "Multiplicate two integer values", des_form = false,
			arguments = { "INT", "INT" })
	public static int mul (int a, int b) {
		return a * b;
	}
}
