package edu.lu.uni.serval.par.templates;

/**
 * Pattern: Adding a “null”, “array-out-of-bound”, and “class-cast” checker. 
 * Example: obj.m1() → if(obj!=null){obj.m1()}.
 * Description: These three patterns insert a new control flow in a program. 
 * 				They often add a new “if(...)” statement to avoid throwing exceptions due to an unexpected state of the program.
 * 
 * @author kui.liu
 *
 */
public abstract class AddChecker extends FixTemplate {
	
}
