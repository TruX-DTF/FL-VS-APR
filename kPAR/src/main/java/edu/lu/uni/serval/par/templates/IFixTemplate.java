package edu.lu.uni.serval.par.templates;

import java.util.List;

import edu.lu.uni.serval.bug.fixer.Patch;
import edu.lu.uni.serval.jdt.tree.ITree;

/**
 * FixTemplate interface.
 * 
 * @author kui.liu
 *
 */
public interface IFixTemplate {
	/**
	 * 1. Altering method parameters. 
	 * 		obj.method(v1,v2) → obj.method(v1,v3)
	 * 2. Calling another method with the same parameters.
	 * 		obj.method1(param) → obj.method2(param)
	 * 3. Calling another overloaded method with one more parameter.
	 * 		obj.method(v1) → obj.method(v1,v2)
	 * 4. Changing a branch condition.
	 * 		if(a == b) → if(a == b && c != 0)
	 * 		This pattern modifies a branch condition in conditional statements or in ternary operators. 
	 * 		Patches in this pattern often just add a term to a predicate or remove a term from a predicate.
	 * 5. Initializing an object.
	 * 		Type obj; → Type obj = new Type()
	 * 		This pattern inserts an additional initialization for an object. This prevents an object being null.
	 * 6. Adding a “null”, “array-out-of-bound”, and “classcast” checker.
	 * 		obj.m1() → if(obj!=null){obj.m1()}
	 * 		These three patterns insert a new control flow in a program. 
	 * 		They often add a new “if(...)” statement to avoid throwing exceptions due to an unexpected state of the program. 
	 */
	
	/*
	 * 1. Method invocation:
	 * 		a. change a parameter with node distance in AST.
	 * 			For a method call, this template seeks variables or expressions whose type is compatible with a method parameter within the same scope. 
	 * 			Then, it replaces the selected parameter by a compatible variable or expression.
	 * 		b. change the method name.
	 * 			For a method call, this template replaces it to another method with compatible parameters and return type.
	 * 		c. insert a new parameter.
	 * 		d. remove a parameter.
	 * 			For a method call, this template adds or removes parameters if the method has overloaded methods. 
	 * 			When it adds a parameter, this template search for compatible variables and expressions in the same scope. 
	 * 			Then, it adds one of them to the place of the new parameter.
	 * 2. If statement or ternary operator.
	 * 		a. Expression Replacer.
	 * 			For a conditional branch such as if() or ternary operator, 
	 * 			this template replaces its predicate by another expression collected in the same scope.
	 * 		b. Expression Adder and Remover.
	 * 			For a conditional branch, this template inserts or removes a term of its predicate. 
	 * 			When adding a term, the template collects predicates from the same scope.
	 * 3. Inserting a new if-checker.
	 * 		a. null checker.
	 * 			For a statement in a program, this template adds if() statements checking whether an object is null 
	 * 			only if the statement has any object reference.
	 * 		b. Range checker.
	 * 			For a statement with array references, this template adds if() statements 
	 * 			that check whether an array index variable exceeds upper and lower bounds 
	 * 			before executing statements that access the array.
	 * 		c. Collection Size Checker
	 * 			For a collection type variable, this template adds if() statements that check 
	 * 			whether an index variable exceeds the size of a given collection object.
	 * 		d. class-cast checker.
	 * 			For a class-casting statement, 
	 * 			this template inserts an if() statement checking that the castee is an object of the casting type 
	 * 			(using instanceof operator).
	 * 4. Initializing an object.
	 * 		a. inserts an additional initialization for an object
	 * 			For a variable in a method call, this template inserts an initialization statement before the call. 
	 * 			The statement uses the basic constructor which has no parameter.
	 * 			
	 */
	public void setSuspiciousCodeStr(String suspiciousCodeStr);
	
	public String getSuspiciousCodeStr();
	
	public void setSuspiciousCodeTree(ITree suspiciousCodeTree);
	
	public ITree getSuspiciousCodeTree();
	
	public void generatePatches();
	
	public List<Patch> getPatches();
	
	public String getSubSuspiciouCodeStr(int startPos, int endPos);
}
