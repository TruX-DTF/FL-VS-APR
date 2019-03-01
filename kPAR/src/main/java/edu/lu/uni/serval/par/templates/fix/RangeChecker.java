package edu.lu.uni.serval.par.templates.fix;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.lu.uni.serval.jdt.tree.ITree;
import edu.lu.uni.serval.par.templates.AddChecker;
import edu.lu.uni.serval.utils.Checker;

/**
 * For a statement with array references, this template adds if() statements
 * that check whether an array index variable exceeds upper and lower bounds
 * before executing statements that access the array.
 * 
 * Context: ArrayAccess.
 * 
 * @author kui.liu
 *
 */
public class RangeChecker extends AddChecker {

	/*
	 * Template: if (arrayVar.length() <= index) {...}
	 */
	
	@Override
	public void generatePatches() {
		ITree suspCodeTree = this.getSuspiciousCodeTree();
		Map<ITree, ITree> allSuspiciousArrayVars = identifyAllSuspiciousArrayAccesses(suspCodeTree);
		String varName = null;
		if (Checker.isVariableDeclarationStatement(suspCodeTree.getType()) ||
				(Checker.isExpressionStatement(suspCodeTree.getType()) && Checker.isAssignment(suspCodeTree.getChild(0).getType()))) {
			varName = this.identifyVariableName(suspCodeTree);
		}
		if (varName != null) {
			suspCodeEndPos = this.identifyRelatedStatements(suspCodeTree, varName);
		}
		
		for (Map.Entry<ITree, ITree> entry : allSuspiciousArrayVars.entrySet()) {
			ITree suspArrayExp = entry.getKey();
			ITree indexExp = entry.getValue();
			int suspArrayExpStartPos = suspArrayExp.getPos();
			int suspArrayExpEndPos = suspArrayExpStartPos + suspArrayExp.getLength();
			int indexExpStartPos = indexExp.getPos();
			int indexExpEndPos = indexExpStartPos + indexExp.getLength();
			
			String suspArrayExpStr = this.getSubSuspiciouCodeStr(suspArrayExpStartPos, suspArrayExpEndPos);
			String indexExpStr = this.getSubSuspiciouCodeStr(indexExpStartPos, indexExpEndPos);
			
			String fixedCodeStr1 = "if (" + suspArrayExpStr + ".length() <= " + indexExpStr + ") {\n\t";
			String fixedCodeStr2 = "\n}\n";
			
			this.generatePatch(suspCodeStartPos, suspCodeEndPos, fixedCodeStr1, fixedCodeStr2);
		}
	}

	private Map<ITree, ITree> identifyAllSuspiciousArrayAccesses(ITree suspCodeTree) {
		Map<ITree, ITree> allSuspiciousArrayVars = new HashMap<>();
		List<ITree> children = suspCodeTree.getChildren();
		for (ITree child : children) {
			int type = child.getType();
			if (Checker.isStatement(type)) break;
			else if (Checker.isComplexExpression(type)) {
				if (Checker.isArrayAccess(type)) {
					ITree arrayExp = child.getChild(0);
					ITree indexExp = child.getChild(1);
					allSuspiciousArrayVars.put(arrayExp, indexExp);
				}
				allSuspiciousArrayVars.putAll(identifyAllSuspiciousArrayAccesses(child));
			}
		}
		return allSuspiciousArrayVars;
	}
	
}
