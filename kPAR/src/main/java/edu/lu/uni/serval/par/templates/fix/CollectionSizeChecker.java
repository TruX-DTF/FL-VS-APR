package edu.lu.uni.serval.par.templates.fix;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.lu.uni.serval.jdt.tree.ITree;
import edu.lu.uni.serval.par.templates.AddChecker;
import edu.lu.uni.serval.utils.Checker;

/**
 * For a collection type variable, this template adds if() statements that check
 * whether an index variable exceeds the size of a given collection object.
 * 
 * Context: a collection type variable.
 * 			or, method invocation expression + method name "get" with a parameter.
 * 
 * @author kui.liu
 *
 */
public class CollectionSizeChecker extends AddChecker {

	/*
	 * if (colVar.size() < index) {...}
	 */
	
	@Override
	public void generatePatches() {
		ITree suspStmtTree = this.getSuspiciousCodeTree();
		Map<ITree, ITree> methodInvocations = identifyMethodInvocations(suspStmtTree);
		if (methodInvocations.isEmpty()) return;
		String varName = identifyVarName(suspStmtTree);
		int suspCodeEndPos = this.suspCodeEndPos;
		
		if (varName != null) {
			suspCodeEndPos = this.identifyRelatedStatements(suspStmtTree, varName);
		}
		
		for (Map.Entry<ITree, ITree> entry : methodInvocations.entrySet()) {
			ITree collectionExp = entry.getKey();
			ITree indexExp = entry.getValue();
			
			int collectionExpStartPos = collectionExp.getPos();
			int collectionExpEndPos = collectionExpStartPos + collectionExp.getLength();
			String collectionExpStr = this.getSubSuspiciouCodeStr(collectionExpStartPos, collectionExpEndPos);
			int indexExpStartPos = indexExp.getPos();
			int indexExpEndPos = indexExpStartPos + indexExp.getLength();
			String parameterExpStr = this.getSubSuspiciouCodeStr(indexExpStartPos, indexExpEndPos);
			String fixedCodeStr1 = "if (" + parameterExpStr + " < " + collectionExpStr + ".size()) {\n\t";
			String fixedCodeStr2 = "\n} else {\n\tthrow new IndexOutOfBoundsException(\"too big index: " + parameterExpStr + "\");\n}";
			this.generatePatch(suspCodeStartPos, suspCodeEndPos, fixedCodeStr1, fixedCodeStr2);
		}
	}

	private Map<ITree, ITree> identifyMethodInvocations(ITree codeAst) {
		Map<ITree, ITree> methodInvocations = new HashMap<>();
		
		List<ITree> children = codeAst.getChildren();
		if (children == null || children.isEmpty()) return methodInvocations;
		
		for (ITree child : children) {
			int childNodeType = child.getType();
			if (Checker.isMethodInvocation(childNodeType)) {
				List<ITree> subChildren = child.getChildren();
				for (int index = 0, size = subChildren.size(); index < size; index ++) {
					ITree subChild = subChildren.get(index);
					int subChildType = subChild.getType();
					String label = subChild.getLabel();
					if (Checker.isSimpleName(subChildType) && label.startsWith("MethodName:get:")) {
						// only one parameter.
						if (index + 2 == size) {
							ITree parameterAst = subChildren.get(index + 1);
							methodInvocations.put(subChild, parameterAst);
						}
					}
				}
			}
			if (Checker.isComplexExpression(childNodeType)) {
				methodInvocations.putAll(identifyMethodInvocations(child));
			}
		}
		
		return methodInvocations;
	}
	
	private String identifyVarName(ITree suspStmtTree) {
		List<ITree> children = suspStmtTree.getChildren();
		if (children == null || children.isEmpty()) return null;
		
		int suspStmtType = suspStmtTree.getType();
		if (Checker.isVariableDeclarationStatement(suspStmtType)) {
			boolean isType = true; // Identity data type
			for (ITree child : children) {
				int childNodeType = child.getType();
				if (Checker.isModifier(childNodeType)) {
					continue;
				}
				if (isType) { // Type Node.
					isType = false;
				} else { //VariableDeclarationFragment(s)
					return child.getChild(0).getLabel();
				}
			}
		} else if (Checker.isExpressionStatement(suspStmtType)) {
			ITree expAst = children.get(0);
			int expAstType = expAst.getType();
			if (Checker.isAssignment(expAstType)) {
				return expAst.getChild(0).getLabel();
			}
		}
		return null;
	}
	
}
