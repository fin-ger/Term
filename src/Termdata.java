import java.lang.annotation.*;

@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.METHOD)
public @interface Termdata {
	String short_param ();
	
	String description ();
	
	boolean des_form () default true;
	
	String[] arguments ();
}
