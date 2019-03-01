package edu.lu.uni.serval.par.templates.fix;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.lu.uni.serval.jdt.tree.ITree;
import edu.lu.uni.serval.par.templates.CallAnotherMethod;

/**
 * For a method call, this template replaces it to another method with
 * compatible parameters and return type.
 * 
 * Context: MethodInvocation.
 * 			Same parameters and return type.
 * 
 * @author kui.liu
 *
 */
public class MethodReplacer extends CallAnotherMethod {

	/*
	 * obj.method1(param) → obj.method2(param). 
	 */

	@Override
	public void generatePatches() {
		List<MethodInvocationExpression> suspMethodInvocations = this.identifySuspiciousMethodInvocations();
		for (MethodInvocationExpression suspM : suspMethodInvocations) {
			ITree methodNameNode = suspM.getCodeAst();
			String methodName = suspM.getMethodName();
			int startPos = methodNameNode.getPos();
			int endPos = startPos + methodName.length();
			String codePart1 = this.getSubSuspiciouCodeStr(suspCodeStartPos, startPos);
			String codePart2 = this.getSubSuspiciouCodeStr(endPos, suspCodeEndPos);
			
			Map<String, List<String>> couldBeReplacedMethods = suspM.getCouldBeReplacedMethods();
			if (couldBeReplacedMethods == null || couldBeReplacedMethods.isEmpty()) continue;
			
			List<String> triedMethodNames = new ArrayList<>();
			for (Map.Entry<String, List<String>> entry : couldBeReplacedMethods.entrySet()) {
				List<String> possibleNames = entry.getValue();
				for (String possibleName : possibleNames) {
					if (triedMethodNames.contains(possibleName)) continue;
					triedMethodNames.add(possibleName);
					String fixedCodeStr1 = codePart1 + possibleName + codePart2;
					this.generatePatch(fixedCodeStr1);
				}
			}
		}
	}

}
