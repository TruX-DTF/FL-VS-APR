package edu.lu.uni.serval.par.templates;

/**
 * Pattern: Calling another overloaded method with one more parameter.
 * Example: obj.method(v1) → obj.method(v1,v2).
 * Description: This pattern adds one more parameter to the existing method call, 
 * 				but it actually replaces the callee by another overloaded method.
 * 
 * @author kui.liu
 *
 */
public abstract class AddMethodParameter extends AlterMethodInvocation {

}
