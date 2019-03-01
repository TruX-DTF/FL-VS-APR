package edu.lu.uni.serval.par.templates.fix;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import edu.lu.uni.serval.jdt.tree.ITree;
import edu.lu.uni.serval.par.templates.ChangeCondition;
import edu.lu.uni.serval.utils.Checker;

/**
 * For a conditional branch such as if() or ternary operator, this template
 * replaces its predicate by another expression collected in the same scope.
 * 
 * Context: IfStatement, WhileStatement, DoStatement, 
 * 			or InfixExpression with specific operator (i.e., ||, or &&).
 * 
 * 
 * @author kui.liu
 *
 */
public class ExpressionReplacer extends ChangeCondition {
	
	/*
	 * a || b -> a || c;
	 * a || b -> a && b;
	 */
	
	@Override
	public void generatePatches() {
		Map<ITree, Integer> allPredicateExpressions = this.identifyPredicateExpressions();
		ITree suspStmtAst = this.getSuspiciousCodeTree();
		Map<ITree, Integer> allSuspPredicateExps = this.readAllSuspiciousPredicateExpressions(suspStmtAst);
//		List<ITree> infixExps1 = new ArrayList<>();
//		List<ITree> infixExps2 = new ArrayList<>();
//		List<ITree> predicateExps = new ArrayList<>();
//		classifyAllSuspPredicatedExps(allSuspPredicateExps, infixExps1, infixExps2, predicateExps);
//		sortList(infixExps1);
		
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
		
		for (Map.Entry<ITree, Integer> entry : allSuspPredicateExps.entrySet()) {
			ITree suspExpAst = entry.getKey();
			int suspExpStartPos = suspExpAst.getPos();
			int suspExpEndPos = suspExpStartPos + suspExpAst.getLength();
			String suspCodePart1 = this.getSubSuspiciouCodeStr(suspCodeStartPos, suspExpStartPos);
			String suspCodePart2 = this.getSubSuspiciouCodeStr(suspExpEndPos,suspCodeEndPos);
			if (Checker.isInfixExpression(suspExpAst.getType())) {
				fixOperatore(suspExpAst);
				//TODO integrate the changes of several same operators.
			}
			
			String buggyExpStr = this.getSubSuspiciouCodeStr(suspExpStartPos, suspExpEndPos);
			for (Map.Entry<ITree, Integer> entry2 : allPredicateExpressions.entrySet()) {
				ITree expCandidate = entry2.getKey();
				int expCandidateStartPos = expCandidate.getPos();
				int expCandidateEndPos = expCandidateStartPos + expCandidate.getLength();
				String expCandidateStr = this.getPredicateExpCandidate(expCandidateStartPos, expCandidateEndPos);
				
				if (suspPredicateExpStr.contains(expCandidateStr) || expCandidateStr.equals(buggyExpStr)) continue;
				//FIXME: Whether the candidate expression is related to the suspicious code?
				
				this.generatePatch(suspCodePart1 + "(" + expCandidateStr + ")" + suspCodePart2);
			}
		}
	}

	// TODO
	@SuppressWarnings("unused")
	private void sortList(List<ITree> exps) {
		try {
			if (exps != null && exps.size() > 0) {
				Collections.sort(exps, new Comparator<ITree>() {
					@Override
					public int compare(ITree t1, ITree t2) {
						int startPos1 = t1.getPos();
						int endPos1 = startPos1 + t1.getLength();
						int startPos2 = t2.getPos();
						int endPos2 = startPos2 + t2.getLength();
						if (endPos1 <= startPos2) {
							// t1 before t2
						} else if (startPos1 >= endPos2) {
							// t1 after t2
						} else if (startPos1 == startPos2) {
							return endPos1 <= endPos2 ? 0 : 1;
						} else if (startPos1 > startPos2) {
							return endPos1 <= endPos2 ? 0 : 1;
						}
						return 0;
					}
					
				});
			}
		} catch (Exception e) {
		}
	}

	@SuppressWarnings("unused")
	private void classifyAllSuspPredicatedExps(Map<ITree, Integer> allSuspPredicateExps, List<ITree> infixExps1,
			List<ITree> infixExps2, List<ITree> predicateExps) {
		for (Map.Entry<ITree, Integer> entry : allSuspPredicateExps.entrySet()) {
			ITree suspExpAst = entry.getKey();
			if (Checker.isInfixExpression(suspExpAst.getType())) {
				String op = suspExpAst.getChild(1).getLabel();
				if ("&&".equals(op) || "||".equals(op)) 
					infixExps2.add(suspExpAst);
				else infixExps1.add(suspExpAst);
			} else predicateExps.add(suspExpAst);
		}
	}

	private void fixOperatore(ITree suspExpTree) {
		ITree operator = suspExpTree.getChild(1);
		int startPos = operator.getPos();
		int endPos = suspExpTree.getChild(2).getPos();
		String codePart1 = this.getSubSuspiciouCodeStr(suspCodeStartPos, startPos);
		String codePart2 = this.getSubSuspiciouCodeStr(endPos, suspCodeEndPos);
		
		String op = operator.getLabel();
		if ("&&".equals(op)) {
			String fixedCodeStr1 = codePart1 + " || " + codePart2;
			this.generatePatch(fixedCodeStr1);
		} else if ("||".equals(op)) {
			String fixedCodeStr1 = codePart1 + " && " + codePart2;
			this.generatePatch(fixedCodeStr1);
		} else if ("==".equals(op)) {
			String fixedCodeStr1 = codePart1 + " != " + codePart2;
			this.generatePatch(fixedCodeStr1);
		} else if ("!=".equals(op)) {
			String fixedCodeStr1 = codePart1 + " == " + codePart2;
			this.generatePatch(fixedCodeStr1);
		} else if (">".equals(op)) {
			String fixedCodeStr1 = codePart1 + " >= " + codePart2;
			this.generatePatch(fixedCodeStr1);
			fixedCodeStr1 = codePart1 + " <= " + codePart2;
			this.generatePatch(fixedCodeStr1);
			fixedCodeStr1 = codePart1 + " < " + codePart2;
			this.generatePatch(fixedCodeStr1);
		} else if (">=".equals(op)) {
			String fixedCodeStr1 = codePart1 + " > " + codePart2;
			this.generatePatch(fixedCodeStr1);
			fixedCodeStr1 = codePart1 + " < " + codePart2;
			this.generatePatch(fixedCodeStr1);
			fixedCodeStr1 = codePart1 + " <= " + codePart2;
			this.generatePatch(fixedCodeStr1);
		} else if ("<".equals(op)) {
			String fixedCodeStr1 = codePart1 + " <= " + codePart2;
			this.generatePatch(fixedCodeStr1);
			fixedCodeStr1 = codePart1 + " >= " + codePart2;
			this.generatePatch(fixedCodeStr1);
			fixedCodeStr1 = codePart1 + " > " + codePart2;
			this.generatePatch(fixedCodeStr1);
		} else if ("<=".equals(op)) {
			String fixedCodeStr1 = codePart1 + " < " + codePart2;
			this.generatePatch(fixedCodeStr1);
			fixedCodeStr1 = codePart1 + " > " + codePart2;
			this.generatePatch(fixedCodeStr1);
			fixedCodeStr1 = codePart1 + " >= " + codePart2;
			this.generatePatch(fixedCodeStr1);
		}
	}
	
}
