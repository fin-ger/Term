package github.fin_ger.term;

import java.lang.annotation.*;

@Retention (RetentionPolicy.RUNTIME)
@Target    (ElementType.TYPE)
public @interface Termconf {
	String usage () default "java <classname> [option] [argument]";
	
	String example ();
	
	String[] ignored_methods () default {"main"};
	
	int ops_width () default 24;
	
	int term_width () default 80; 
}
