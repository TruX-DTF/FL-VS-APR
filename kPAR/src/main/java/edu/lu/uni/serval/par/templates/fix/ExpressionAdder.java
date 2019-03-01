package edu.lu.uni.serval.par.templates.fix;

import java.util.List;
import java.util.Map;

import edu.lu.uni.serval.jdt.tree.ITree;
import edu.lu.uni.serval.par.templates.ChangeCondition;
import edu.lu.uni.serval.utils.Checker;

/**
 * For a conditional branch, this template inserts a term of its predicate. 
 * When adding a term, the template collects predicates from the same scope.
 * 
 * Context: IfStatement, WhileStatement, DoStatement, or PredicateExpression.
 * 
 * @author kui.liu
 *
 */
public class ExpressionAdder extends ChangeCondition {

	/*
	 * a -> a || c;
	 * a -> a && c;
	 */
	
	@Override
	public void generatePatches() {
		//TODO: this expression adder is just simply adding expression candidates. To be improved.
		
		Map<ITree, Integer> allPredicateExpressions = this.identifyPredicateExpressions();
		ITree suspStmtAst = this.getSuspiciousCodeTree();
		ITree suspPredicateExp = null;
		if (Checker.isDoStatement(suspStmtAst.getType())) {
			List<ITree> children = suspStmtAst.getChildren();
			suspPredicateExp = children.get(children.size() - 1);
		} else {
			suspPredicateExp = suspStmtAst.getChild(0);
		}
		int suspPredicateExpStartPos = suspPredicateExp.getPos();
		int suspPredicateExpEndPos = suspPredicateExpStartPos + suspPredicateExp.getLength();
		String suspPredicateExpStr = this.getSubSuspiciouCodeStr(suspPredicateExpStartPos, suspPredicateExpEndPos);
		
		String suspCodeStr = this.getSuspiciousCodeStr();
		int suspStmtStartPos = suspStmtAst.getPos();
		String codePart1 = suspCodeStr.substring(0, suspPredicateExpStartPos - suspStmtStartPos);
		String codePart2 = suspCodeStr.substring(suspPredicateExpEndPos - suspStmtStartPos);
		
		for (Map.Entry<ITree, Integer> entry : allPredicateExpressions.entrySet()) {
			// same expression problem.
			ITree predicateExpCandidate = entry.getKey();
			int predicateExpStartPos = predicateExpCandidate.getPos();
			int predicateExpEndPos = predicateExpStartPos + predicateExpCandidate.getLength();
			
			String predicateExpCandidateStr = getPredicateExpCandidate(predicateExpStartPos, predicateExpEndPos);
			if (suspPredicateExpStr.contains(predicateExpCandidateStr) || predicateExpCandidateStr.equals(suspPredicateExpStr)) continue;
			
			/*
			 * TODO: use the context information to limit the search space of predicate expression candidates. 
			 */
			String fixedCodeStr1 = codePart1 + "(" + suspPredicateExpStr + ") || (" + predicateExpCandidateStr + ")" + codePart2;
			this.generatePatch(fixedCodeStr1);
			
			fixedCodeStr1 = codePart1 + "(" + suspPredicateExpStr + ") && (" + predicateExpCandidateStr + ")" + codePart2;
			this.generatePatch(fixedCodeStr1);
			
			fixedCodeStr1 = codePart1 + "(" + suspPredicateExpStr + ") || !(" + predicateExpCandidateStr + ")" + codePart2;
			this.generatePatch(fixedCodeStr1);
			
			fixedCodeStr1 = codePart1 + "(" + suspPredicateExpStr + ") && !(" + predicateExpCandidateStr + ")" + codePart2;
			this.generatePatch(fixedCodeStr1);
		}
	}

}
