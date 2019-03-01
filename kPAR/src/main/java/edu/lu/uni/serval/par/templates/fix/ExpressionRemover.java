package edu.lu.uni.serval.par.templates.fix;

import java.util.Map;

import edu.lu.uni.serval.jdt.tree.ITree;
import edu.lu.uni.serval.par.templates.ChangeCondition;

/**
 * For a conditional branch, this template removes a term of its predicate. 
 * 
 * Context: IfStatement, WhileStatement, DoStatement, 
 * 			or InfixExpression with specific operator (i.e., ||, or &&).
 * 
 * @author kui.liu
 *
 */
public class ExpressionRemover extends ChangeCondition {

	/*
	 * a || b -> a;
	 * a && b -> a;
	 */
	
	@Override
	public void generatePatches() {
		ITree suspStmtAst = this.getSuspiciousCodeTree();
		Map<ITree, Integer> allPredicateExps = readAllSuspiciousPredicateExpressions(suspStmtAst);
		
		for (Map.Entry<ITree, Integer> entry : allPredicateExps.entrySet()) {
			ITree predicateExp = entry.getKey();
			int pos = entry.getValue();
			int predicateExpStartPos = predicateExp.getPos();
			
			String fixedCodeStr1;
			if (pos == 0) {
				continue;
			} else if (pos > predicateExpStartPos) {
				fixedCodeStr1 = this.getSubSuspiciouCodeStr(suspCodeStartPos, predicateExpStartPos);
				fixedCodeStr1 += this.getSubSuspiciouCodeStr(pos, suspCodeEndPos);
			} else {
				fixedCodeStr1 = this.getSubSuspiciouCodeStr(suspCodeStartPos, pos);
				fixedCodeStr1 += this.getSubSuspiciouCodeStr(predicateExpStartPos + predicateExp.getLength(), suspCodeEndPos);
			}
			this.generatePatch(fixedCodeStr1);
		}
	}

}
