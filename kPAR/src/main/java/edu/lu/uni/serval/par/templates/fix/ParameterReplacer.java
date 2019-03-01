package edu.lu.uni.serval.par.templates.fix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import edu.lu.uni.serval.jdt.tree.ITree;
import edu.lu.uni.serval.par.templates.AlterMethodParameter;
import edu.lu.uni.serval.utils.Checker;

/**
 * For a method call, this template seeks variables or expressions whose type is
 * compatible with a method parameter within the same scope. Then, it replaces
 * the selected parameter by a compatible variable or expression.
 * 
 * Context: MethodInvocation with at least one parameter.
 * 
 * @author kui.liu
 *
 */
public class ParameterReplacer extends AlterMethodParameter {

	/*
	 * Template: obj.method(v1,v2) → obj.method(v1,v3).
	 * TODO: changes of multiple parameters.
	 */
	
	@Override
	public void generatePatches() {
		this.identifySuspiciousMethodInvocations();
		Map<ITree, Integer> suspMethodInvocations = this.suspMethodInvocations;

		for (Map.Entry<ITree, Integer> entry : suspMethodInvocations.entrySet()) {
			ITree methodInvocationNode = entry.getKey();
			int methodInvType = entry.getValue();
			List<ITree> paraList;// = new ArrayList<>();
			if (methodInvType == 1) continue; // no parameters.
			else if (methodInvType == 3) 
				paraList = methodInvocationNode.getChildren();
			else {//if (methodInvType == 2)
				int suspCodeNodeType = this.getSuspiciousCodeTree().getType();
				if (Checker.isConstructorInvocation(suspCodeNodeType) || Checker.isSuperConstructorInvocation(suspCodeNodeType)) {
					paraList = methodInvocationNode.getChildren();
				} else {
					List<ITree> children = methodInvocationNode.getChildren();
					paraList = children.get(children.size() - 1).getChildren();
				}
			}
			if (paraList.isEmpty()) continue;
			
			Map<ITree, List<String>> literalsMap = new HashMap<>();
			List<ITree> emptyVarNameParas = new ArrayList<>();
			for (ITree paraTree : paraList) {
				List<String> varNames = new ArrayList<>();
				int paraTreeType = paraTree.getType();
				if (Checker.isSimpleName(paraTreeType)) {
					String varName = this.readVariableName2(paraTree);
					String dataType = varTypesMap.get(varName);
					
					if (dataType == null) {
						varName = "this." + varName;
						dataType = this.varTypesMap.get(varName);
					}
					if (dataType != null) {
						if (isNumberType(dataType)) {
							// FIXME: how about number literal values?
							List<String> varL = allVarNamesMap.get("int");
							if (varL != null) varNames.addAll(varL);
							varL = allVarNamesMap.get("Integer");
							if (varL != null) varNames.addAll(varL);
							varL = allVarNamesMap.get("long");
							if (varL != null) varNames.addAll(varL);
							varL = allVarNamesMap.get("Long");
							if (varL != null) varNames.addAll(varL);
							varL = allVarNamesMap.get("double");
							if (varL != null) varNames.addAll(varL);
							varL = allVarNamesMap.get("Double");
							if (varL != null) varNames.addAll(varL);
							varL = allVarNamesMap.get("float");
							if (varL != null) varNames.addAll(varL);
							varL = allVarNamesMap.get("Float");
							if (varL != null) varNames.addAll(varL);
							varL = allVarNamesMap.get("short");
							if (varL != null) varNames.addAll(varL);
							varL = allVarNamesMap.get("Short");
							if (varL != null) varNames.addAll(varL);
							varL = allVarNamesMap.get("byte");
							if (varL != null) varNames.addAll(varL);
							varL = allVarNamesMap.get("Byte");
							if (varL != null) varNames.addAll(varL);
						} else {
							varNames = allVarNamesMap.get(dataType);
						}
						varNames.remove(varName);
					} else {
						varNames = allVarNamesMap.get("Object");
					}
					
				} else if (Checker.isNumberLiteral(paraTreeType)) {
					// FIXME: how about number literal values?
					String num = paraTree.getLabel().toLowerCase(Locale.ROOT);
					if (num.endsWith("l")) {
						varNames.add("0l");
						List<String> varL = allVarNamesMap.get("long");
						if (varL != null) varNames.addAll(varL);
						varL = allVarNamesMap.get("Long");
						if (varL != null) varNames.addAll(varL);
					} else if (num.endsWith("d")) {
						varNames.add("0d");
						List<String> varL = allVarNamesMap.get("double");
						if (varL != null) varNames.addAll(varL);
						varL = allVarNamesMap.get("Double");
						if (varL != null) varNames.addAll(varL);
					} else if (num.endsWith("f")) {
						varNames.add("0f");
						List<String> varL = allVarNamesMap.get("float");
						if (varL != null) varNames.addAll(varL);
						varL = allVarNamesMap.get("Float");
						if (varL != null) varNames.addAll(varL);
					} else if (num.contains(".")) {
						varNames.add("0.0");
						List<String> varL = allVarNamesMap.get("double");
						if (varL != null) varNames.addAll(varL);
						varL = allVarNamesMap.get("Double");
						if (varL != null) varNames.addAll(varL);
						varL = allVarNamesMap.get("float");
						if (varL != null) varNames.addAll(varL);
						varL = allVarNamesMap.get("Float");
						if (varL != null) varNames.addAll(varL);
					} else {// int
						varNames.add("0");
						varNames.add("1");
						List<String> varL = allVarNamesMap.get("int");
						if (varL != null) varNames.addAll(varL);
						varL = allVarNamesMap.get("Integer");
						if (varL != null) varNames.addAll(varL);
						varL = allVarNamesMap.get("short");
						if (varL != null) varNames.addAll(varL);
						varL = allVarNamesMap.get("Short");
						if (varL != null) varNames.addAll(varL);
						varL = allVarNamesMap.get("byte");
						if (varL != null) varNames.addAll(varL);
						varL = allVarNamesMap.get("Byte");
						if (varL != null) varNames.addAll(varL);
					}
					varNames.remove(paraTree.getLabel());
					if (varNames != null && !varNames.isEmpty()) {
						literalsMap.put(paraTree, varNames);
					}
					continue;
				} else if (Checker.isStringLiteral(paraTreeType)) {
					varNames = allVarNamesMap.get("String");
					if (varNames != null && !varNames.isEmpty()) {
						literalsMap.put(paraTree, varNames);
					}
					continue;
				} else if (Checker.isCharacterLiteral(paraTreeType)) {
					List<String> varL = allVarNamesMap.get("char");
					if (varL != null) varNames.addAll(varL);
					varL = allVarNamesMap.get("Character");
					if (varL != null) varNames.addAll(varL);
					if (varNames != null && !varNames.isEmpty()) {
						literalsMap.put(paraTree, varNames);
					}
					continue;
				} else if (Checker.isBooleanLiteral(paraTreeType)) {
					boolean label = Boolean.valueOf(paraTree.getLabel());
					varNames.add(label ? "false" : "true");
					
					List<String> varL = allVarNamesMap.get("boolean");
					if (varL != null) varNames.addAll(varL);
					varL = allVarNamesMap.get("Boolean");
					if (varL != null) varNames.addAll(varL);
				} else if (Checker.isThisExpression(paraTreeType)
						|| Checker.isConstructorInvocation(paraTreeType)) {
					// Class name type vars TODO
				} else if (Checker.isQualifiedName(paraTreeType)) {
					String varName = paraTree.getLabel();
					String dataType = varTypesMap.get(varName);
					if (dataType == null) varNames = allVarNamesMap.get("Object");
					else {
						varNames = allVarNamesMap.get(dataType);
						varNames.remove(varName);
					}
				} else if (Checker.isFieldAccess(paraTreeType)) {
					// FieldAccess: this.var.get();
					List<ITree> subChildren = paraTree.getChildren();
					String varName = subChildren.get(subChildren.size() - 1).getLabel();
					String dataType = varTypesMap.get("this." + varName);
					if (dataType == null) dataType = varTypesMap.get(varName);
					if (dataType == null) varNames = allVarNamesMap.get("Object");
					else {
						varNames = allVarNamesMap.get(dataType);
						varNames.remove(varName);
					}
					// Without SuperFieldAccess.
				} else { // NullLiteral and others.
//				} else if (Checker.isFieldAccess(paraTreeType)) {
//					// Map<String, String> varTypesMap
//				} else if (Checker.isArrayAccess(paraTreeType)) {
//					// Map<String, String> varTypesMap
//				} else if (Checker.isClassInstanceCreation(paraTreeType)) {
//					// data type.
//						|| Checker.isSuperFieldAccess(paraTreeType)) {
//					// data type
//				} else if (Checker.isMethodInvocation(paraTreeType)
//						|| Checker.isSuperMethodInvocation(paraTreeType)) {
//					// return type.
//				} else if (Checker.isPrefixExpression(paraTreeType)) {
//					// !
//					|| (Checker.isInfixExpression(paraTreeType))
					varNames.addAll(allVarNamesList);
				}
				
				if (varNames != null && !varNames.isEmpty()) {
					int paraStartPos = paraTree.getPos();
					int paraEndPos = paraStartPos + paraTree.getLength();
					String codePart1 = this.getSubSuspiciouCodeStr(suspCodeStartPos, paraStartPos);
					String codePart2 = this.getSubSuspiciouCodeStr(paraEndPos, suspCodeEndPos);
					
					for (String varName : varNames) {
						String fixedCodeStr1 = codePart1 + varName + codePart2;
						generatePatch(fixedCodeStr1);
					}
				} else {
					emptyVarNameParas.add(paraTree);
				}
			}
			
			for (Map.Entry<ITree, List<String>> literalEntry : literalsMap.entrySet()) {
				ITree paraTree = literalEntry.getKey();
				List<String> varNames = literalEntry.getValue();
				
				int paraStartPos = paraTree.getPos();
				int paraEndPos = paraStartPos + paraTree.getLength();
				String codePart1 = this.getSubSuspiciouCodeStr(suspCodeStartPos, paraStartPos);
				String codePart2 = this.getSubSuspiciouCodeStr(paraEndPos, suspCodeEndPos);
				
				for (String varName : varNames) {
					String fixedCodeStr1 = codePart1 + varName + codePart2;
					generatePatch(fixedCodeStr1);
				}
			}
			
			for (ITree paraTree : emptyVarNameParas) {
				int paraStartPos = paraTree.getPos();
				int paraEndPos = paraStartPos + paraTree.getLength();
				String codePart1 = this.getSubSuspiciouCodeStr(suspCodeStartPos, paraStartPos);
				String codePart2 = this.getSubSuspiciouCodeStr(paraEndPos, suspCodeEndPos);
				
				for (String varName : allVarNamesList) {
					if (varName.equals(paraTree.getLabel())) continue;
					String fixedCodeStr1 = codePart1 + varName + codePart2;
					generatePatch(fixedCodeStr1);
				}
			}
		}
	}

	private boolean isNumberType(String dataType) {
		if ("int".equals(dataType)) return true;
		if ("Integer".equals(dataType)) return true;
		if ("long".equalsIgnoreCase(dataType)) return true;
		if ("float".equalsIgnoreCase(dataType)) return true;
		if ("double".equalsIgnoreCase(dataType)) return true;
		if ("short".equalsIgnoreCase(dataType)) return true;
		if ("byte".equalsIgnoreCase(dataType)) return true;
		return false;
	}
	
}
