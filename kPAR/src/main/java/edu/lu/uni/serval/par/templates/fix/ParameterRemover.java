package edu.lu.uni.serval.par.templates.fix;

import java.util.List;

import edu.lu.uni.serval.jdt.tree.ITree;
import edu.lu.uni.serval.par.templates.AddMethodParameter;

/**
 * For a method call, this template removes parameters if the method has overloaded methods. 
 * 
 * Context: MethodInvocation with at least one parameter.
 * 
 * @author kui.liu
 *
 */
public class ParameterRemover extends AddMethodParameter {
	
	/*
	 * Template: obj.method(v1,v2) → obj.method(v1).
	 */

	@Override
	public void generatePatches() {
		List<MethodInvocationExpression> suspMethodInvocations = this.identifySuspiciousMethodInvocations();
		for (MethodInvocationExpression suspM : suspMethodInvocations) {
			ITree methodNameNode = suspM.getCodeAst();
			List<ITree> paraAsts = methodNameNode.getChildren();
			if (!paraAsts.isEmpty()) {
				int size = paraAsts.size();
				if (size == 1) {
					ITree paraAst = paraAsts.get(0);
					int startPos = paraAst.getPos();
					int endPos = startPos + paraAst.getLength();
					String fixedCodeStr1 = this.getSubSuspiciouCodeStr(suspCodeStartPos, startPos) + 
							this.getSubSuspiciouCodeStr(endPos, suspCodeEndPos);
					this.generatePatch(fixedCodeStr1);
				} else {
					size = size - 1;
					for (int index = 0; index < size; index ++) {
						int startPos = paraAsts.get(index).getPos();
						int endPos = paraAsts.get(index + 1).getPos();
						String fixedCodeStr1 = this.getSubSuspiciouCodeStr(suspCodeStartPos, startPos) + 
								this.getSubSuspiciouCodeStr(endPos, suspCodeEndPos);
						this.generatePatch(fixedCodeStr1);
					}
					ITree paraAst = paraAsts.get(size - 1);
					int startPos = paraAst.getPos() + paraAst.getLength();
					paraAst = paraAsts.get(size);
					int endPos = paraAst.getPos() + paraAst.getLength();
					String fixedCodeStr1 = this.getSubSuspiciouCodeStr(suspCodeStartPos, startPos) + 
							this.getSubSuspiciouCodeStr(endPos, suspCodeEndPos);
					this.generatePatch(fixedCodeStr1);
				}
			}
		}
	}
	
}
