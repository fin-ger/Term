import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Term {
	private static Termconf	CONF;
	private static Class<?>	CLASS;
	
	public static void init (String class_name, String... args) {
		try {
			CLASS = Class.forName (class_name);
		} catch (ClassNotFoundException e) {
			System.err.println ("ERROR: No class named \"" + class_name
					+ "\" availabe in this project!");
		}
		CONF = (Termconf) CLASS.getAnnotation (Termconf.class);
		String       methodName;
		String       option;
		int          methodIdx;
		Method       method;
		Class<?>[]   mpt; // method parameter types
		Class<?>     mrt; // method return type
		Object[]     param_obj;
		String[]     methodArgs;
		Method[]     methods     = CLASS.getMethods ();
		List<String> methodNames = new ArrayList<String> ();
		
		if (args.length == 0 || args[0].equals ("-h")
				|| args[0].equals ("--help")) {
			help ();
			return;
		}
		
		option = args[0];
		
		if (option.length () == 2)
			methodName = methBySP (option);
		else
			methodName = option.substring (2);
		
		for (Method meth : methods) {
			methodNames.add (meth.getName ());
		}
		
		methodIdx = methodNames.indexOf (methodName);
		
		if (methodIdx == -1) {
			System.err.println ("ERROR: Method " + methodName
					+ " does not exist!");
			System.exit (1);
		}
		
		method     = methods[methodIdx];
		mpt        = methods[methodIdx].getParameterTypes ();
		methodArgs = Arrays.copyOfRange (args, 1, args.length);
		
		if (mpt.length > methodArgs.length) {
			System.err.println ("ERROR: Not enough arguments!");
			System.exit (1);
		} else if (mpt.length < methodArgs.length) {
			System.err.println ("ERROR: Too many arguments!");
			System.exit (1);
		}
		
		mrt       = method.getReturnType ();
		param_obj = new Object[mpt.length];
		
		for (int i = 0; i < mpt.length; i++) {
			String type = mpt[i].getSimpleName ();
			if (type.equals ("int")) {
				param_obj[i] = Integer.valueOf (methodArgs[i]);
			} else if (type.equals ("long")) {
				param_obj[i] = Long.valueOf (methodArgs[i]);
			} else if (type.equals ("double")) {
				param_obj[i] = Double.valueOf (methodArgs[i]);
			} else if (type.equals ("String")) {
				param_obj[i] = String.valueOf (methodArgs[i]);
			} else {
				System.err.println ("ERROR: Variable type \""
						+ Arrays.toString (mpt) + "\" is not supported!");
			}
		}
		
		if (mrt.getSimpleName ().equals ("void")) {
			exec (method, param_obj);
		} else if (mrt.getSimpleName ().equals ("double[]")) {
			System.out.println (Arrays.toString ((double[]) exec (
					method, param_obj)));
		} else {
			System.out.println (exec (method, param_obj));
		}
		
		/*
		 * INFO
		 *
		 * currently supported method parameter types:
		 *   - int
		 *   - long
		 *   - double
		 *   - String
		 * currently supported method return types:
		 *   - int
		 *   - long
		 *   - double
		 *   - String
		 *   - double[]
		 *   - void
		 */
	}
	
	public static void help () {
		Method[] methods = CLASS.getMethods ();
		
		System.out.println ("Usage: " + CONF.usage () + "\n");
		System.out.println ("Available options:");
		prOps ("-h, --help", "Print this help page");
		
		for (int i = 0; i < methods.length - 9; i++) {
			if (!Arrays.toString (CONF.ignored_methods ()).contains (
					methods[i].getName ())) {
				Termmeth meta =
						(Termmeth) methods[i].getAnnotation (Termmeth.class);
				
				if (!methods[i].isAnnotationPresent (Termmeth.class)) {
					System.err
					.println ("ERROR: No Termmeth present for method \""
							+ methods[i].getName () + "\"!");
					System.exit (1);
				}
				
				String arg_des = ""; // argument description
				for (int k=0; k < methods[i].getParameterTypes ().length; k++) {
					arg_des = arg_des + " " + meta.arguments ()[k];
				}
				
				String ops = "-" + meta.short_param () + ", --"
						+ methods[i].getName () + arg_des;
				
				prOps (ops, meta.description (), meta.des_form ());
				
			}
		}
		
		System.out.println ();
		
		if (!CONF.example ().isEmpty ()) {
			System.out.println ("Example:");
			System.out.println ("  " + CONF.example ());
		}
	}
	
	private static void prOps (String ops, String des, boolean formatting) {
		String       pfstr; // printf string
		String       wrap_des;
		int          wrap_idx;
		String       format  = "%s%n%" + CONF.ops_width () + "s%s";
		List<String> des_lns = new ArrayList<String> ();
		
		if (ops.length () >= CONF.ops_width ())
			pfstr = "  %s%n%" + CONF.ops_width () + "s%s%n";
		else
			pfstr =
			"  %-" + (CONF.ops_width () - 3) + "."
					+ (CONF.ops_width () - 3) + "s%s%s%n";
		
		if (!formatting) {
			System.out.printf (pfstr, ops, " ", des);
		} else {
			while (des.length () > CONF.term_width () - CONF.ops_width ()) {
				String new_des =
						des.substring (
								0, CONF.term_width () - CONF.ops_width ());
				
				wrap_idx = new_des.lastIndexOf (" ");
				
				if (des.charAt (new_des.length ()) == 32)
					wrap_idx = new_des.length ();
				
				wrap_idx = (wrap_idx == -1) ? new_des.length () : wrap_idx;
				des_lns.add (des.substring (0, wrap_idx));
				des = des.substring (wrap_idx + 1);
			}
			des_lns.add (des);
			
			wrap_des = des_lns.get (0);
			for (String line : des_lns.subList (1, des_lns.size ())) {
				System.out.println ("[" + line + "]");
				wrap_des = String.format (format, wrap_des, " ", line);
			}
			
			System.out.printf (pfstr, ops, " ", wrap_des);
		}
	}
	
	private static void prOps (String ops, String des) {
		prOps (ops, des, true);
	}
	
	private static String methBySP (String param) {
		Method[] methods    = CLASS.getMethods ();
		String   methodName = new String ();
		
		param = param.substring (1);
		
		for (int i = 0; i < methods.length - 9; i++) {
			if (!Arrays.toString (CONF.ignored_methods ()).contains (
					methods[i].getName ())) {
				Termmeth meta =
						(Termmeth) methods[i].getAnnotation (Termmeth.class);
				
				if (!methods[i].isAnnotationPresent (Termmeth.class)) {
					System.err
					.println ("ERROR: No Termmeth present for method \""
							+ methods[i].getName () + "\"!");
					System.exit (1);
				}
				
				if (meta.short_param ().equals (param)) {
					methodName = methods[i].getName ();
					break;
				}
			}
		}
		
		if (methodName.length () == 0) {
			System.err.println ("ERROR: Option \"-" + param
					+ "\" is not available!");
			System.exit (1);
		}
		
		return methodName;
	}
	
	private static Object exec (Method m, Object[] o) {
		Object res = null;
		try {
			res = m.invoke (CLASS, o);
		} catch (Exception e) {
			System.err.println ("ERROR: Failed to execute \"" + m.getName ()
					+ "\"!");
			e.printStackTrace ();
			System.exit (1);
		}
		return res;
	}
}
