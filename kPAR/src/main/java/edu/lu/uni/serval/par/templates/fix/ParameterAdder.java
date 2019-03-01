package edu.lu.uni.serval.par.templates.fix;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.lu.uni.serval.jdt.tree.ITree;
import edu.lu.uni.serval.par.templates.AddMethodParameter;
import edu.lu.uni.serval.utils.Checker;

/**
 * For a method call, this template adds parameters if the method has overloaded
 * methods. When it adds a parameter, this template search for compatible
 * variables and expressions in the same scope. Then, it adds one of them to the
 * place of the new parameter.
 * 
 * Context: MethodInvocation.
 * 
 * @author kui.liu
 *
 */
public class ParameterAdder extends AddMethodParameter {

	/*
	 * Template: obj.method(v1,v2) → obj.method(v1,v2,v3).
	 */
	
	@Override
	public void generatePatches() {
		List<MethodInvocationExpression> suspMethodInvocations = this.identifySuspiciousMethodInvocations();
		Map<String, List<String>> allVarNamesMap = this.allVarNamesMap;
		for (MethodInvocationExpression suspM : suspMethodInvocations) {
			List<String> returnTypes = suspM.getPossibleReturnTypes();
			List<List<String>> parameterTypesList = suspM.getParameterTypes();
			ITree methodNameNode = suspM.getCodeAst();
			List<ITree> paraAsts = methodNameNode.getChildren();
			int paraNum = paraAsts.size();
			
			List<ITree> differentParaMethods = suspM.getDifferentParaMethods();
			if (differentParaMethods == null || differentParaMethods.isEmpty()) {
//				if (Checker.isClassInstanceCreation(methodNameNode.getType())) {
//					insertParameter(methodNameNode, paraAsts);
//				}
				continue;
			}
			
			for (ITree method : differentParaMethods) {
				String label = method.getLabel();
				int indexOfMethodName = label.indexOf("MethodName:");
				int indexOrPara = label.indexOf("@@Argus:");
				
				// Match parameter data types.
				String paraStr = label.substring(indexOrPara + 8);
				if (paraStr.startsWith("null")) {
					continue;
				} else {
					int indexExp = paraStr.indexOf("@@Exp:");
					if (indexExp > 0) paraStr = paraStr.substring(0, indexExp);
				}
				
				// Read return type.
				String returnType = label.substring(label.indexOf("@@") + 2, indexOfMethodName - 2);
				int index = returnType.indexOf("@@tp:");
				if (index > 0) returnType = returnType.substring(0, index - 2);
				returnType = this.readType(returnType);
				
				index = returnTypes.indexOf(returnType);
				if (index >= 0) {
					List<String> paraList = parseMethodParameterTypes(paraStr, "\\+");
					int paraListSize = paraList.size();
					List<String> buggyParaList = parameterTypesList.get(index);
					int remainParaNum = paraListSize - paraNum;
					if (remainParaNum <= 0) continue;
					if (remainParaNum != 1) continue;// FIXME: other conditions.
//					int lcsValue = paraListSize + paraNum - 2 * lcs(paraList, buggyParaList);
//					if (lcsValue != remainParaNum) continue;
					for (int i = 0; i < paraListSize; i ++) {
						List<String> subParaList = new ArrayList<>();
						subParaList.addAll(paraList.subList(0, i));
						subParaList.addAll(paraList.subList(i + 1, paraListSize));
						if (sameParaList(subParaList, buggyParaList)) {
							String paraType = paraList.get(i);
							List<String> varNames = allVarNamesMap.get(paraType);
							
							if (varNames != null && !varNames.isEmpty()) {
								String codePart1;
								String codePart2;
								if (i == paraNum) {
									if (i == 0) {
										String methodName = methodNameNode.getLabel().substring(11);
										methodName = methodName.substring(0, methodName.indexOf(":"));
										int subEndPos1 = methodNameNode.getPos() + methodName.length();
										int subEndPos2 = methodNameNode.getPos() + methodNameNode.getLength();
										codePart1 = this.getSubSuspiciouCodeStr(suspCodeStartPos, subEndPos1) + "(";
										codePart2 = ")" + this.getSubSuspiciouCodeStr(subEndPos2, suspCodeEndPos);
									} else {
										ITree paraTree = paraAsts.get(i - 1);
										int subStartPos = paraTree.getPos();
										int subEndPos = subStartPos + paraTree.getLength();
										codePart1 = this.getSubSuspiciouCodeStr(this.suspCodeStartPos, subEndPos) + ", ";
										codePart2 = this.getSubSuspiciouCodeStr(subEndPos, this.suspCodeEndPos);
									}
								} else {
									ITree paraTree = paraAsts.get(i);
									int subStartPos = paraTree.getPos();
									codePart1 = this.getSubSuspiciouCodeStr(suspCodeStartPos, subStartPos);
									codePart2 = ", " + this.getSubSuspiciouCodeStr(subStartPos, suspCodeEndPos);
								}
								for (String varName : varNames) {
									String fixedCodeStr1 = codePart1 + varName + codePart2;
									this.generatePatch(fixedCodeStr1);
								}
							}
						}
					}
					
					// some default values. FIXME: it could be removed.
					for (int i = 0; i < paraListSize; i++) {
						List<String> subParaList = new ArrayList<>();
						subParaList.addAll(paraList.subList(0, i));
						subParaList.addAll(paraList.subList(i + 1, paraListSize));
						if (sameParaList(subParaList, buggyParaList)) {
							String paraType = paraList.get(i);
							String codePart1;
							String codePart2;
							if (i == paraNum) {
								if (i == 0) {
									String methodName = methodNameNode.getLabel().substring(11);
									methodName = methodName.substring(0, methodName.indexOf(":"));
									int subEndPos1 = methodNameNode.getPos() + methodName.length();
									int subEndPos2 = methodNameNode.getPos() + methodNameNode.getLength();
									codePart1 = this.getSubSuspiciouCodeStr(suspCodeStartPos, subEndPos1) + "(";
									codePart2 = ")" + this.getSubSuspiciouCodeStr(subEndPos2, suspCodeEndPos);
								} else {
									ITree paraTree = paraAsts.get(i - 1);
									int subStartPos = paraTree.getPos();
									int subEndPos = subStartPos + paraTree.getLength();
									codePart1 = this.getSubSuspiciouCodeStr(suspCodeStartPos, subEndPos) + ", ";
									codePart2 = this.getSubSuspiciouCodeStr(subEndPos, suspCodeEndPos);
								}
							} else {
								ITree paraTree = paraAsts.get(i);
								int subStartPos = paraTree.getPos();
								codePart1 = this.getSubSuspiciouCodeStr(suspCodeStartPos, subStartPos);
								codePart2 = ", " + this.getSubSuspiciouCodeStr(subStartPos, suspCodeEndPos);
							}

							String fixedCodeStr1 = codePart1;
							if (paraType.equals("char")) {
								fixedCodeStr1 += "' '";
							} else if (paraType.equals("Character")) {
								fixedCodeStr1 += "' '" + codePart2;
								this.generatePatch(fixedCodeStr1);
								fixedCodeStr1 = codePart1 + "null";
							} else if (paraType.equals("byte") || paraType.equals("short") || paraType.equals("int")
									|| paraType.equals("long") || paraType.equals("double")
									|| paraType.equals("float")) {
								fixedCodeStr1 += "0" + codePart2;
								this.generatePatch(fixedCodeStr1);
								fixedCodeStr1 = codePart1 + "1";
							} else if (paraType.equals("Byte") || paraType.equals("Short") || paraType.equals("Integer")
									|| paraType.equals("Long") || paraType.equals("Double")
									|| paraType.equals("Float")) {
								fixedCodeStr1 += "0" + codePart2;
								this.generatePatch(fixedCodeStr1);
								fixedCodeStr1 = codePart1 + "1" + codePart2;
								this.generatePatch(fixedCodeStr1);
								fixedCodeStr1 = codePart1 + "null";
							} else if (paraType.equals("String")) {
								fixedCodeStr1 += "\"\"" + codePart2;
								this.generatePatch(fixedCodeStr1);
								fixedCodeStr1 = codePart1 + "null";
							} else if (paraType.equalsIgnoreCase("boolean")) {
								fixedCodeStr1 += "true" + codePart2;
								this.generatePatch(fixedCodeStr1);
								fixedCodeStr1 = codePart1 + "false";
							} else {
								fixedCodeStr1 += "null";
							}
							fixedCodeStr1 += codePart2;
							this.generatePatch(fixedCodeStr1);
						}
					}
				}
			}
//			JavaFileParser jfp = new JavaFileParser();
//			jfp.parseJavaFile("CodeDonor", new File(codePath));
//			List<Method> methods = jfp.getMethods();
//			for (Method method : methods) {
//				String returnType = this.readType(method.getReturnTypeString());
//				int indexReturnType = returnTypes.indexOf(returnType);
//				if (methodName.equals(method.getName()) && indexReturnType >= 0) {
//					String paraStr = method.getArgumentsStr();
//					List<String> paraTypes = this.parseMethodParameterTypes(paraStr, "#");
//					List<String> parameterTypes = parameterTypesList.get(indexReturnType);
//					int parameterTypesSize = parameterTypes.size();
//					int paraTypesSize = paraTypes.size();
//					if (parameterTypesSize + 1 == paraTypesSize) {// FIXME more parameters?
//						boolean matched = true;
//						int insertIndex = 0;
//						if (parameterTypesSize > 0) {
//							for (int index = 0; index < paraTypesSize; index ++) {
//								List<String> tempParaTypes = new ArrayList<>();
//								tempParaTypes.addAll(paraTypes);
//								tempParaTypes.remove(index);
//								for (int i = 0; i < parameterTypesSize; i ++) {
//									String paraType = tempParaTypes.get(i);
//									String targetType = parameterTypes.get(i);
//									if ((paraType.equals("char") || paraType.equals("Character"))
//											&& targetType.equals("char") || targetType.equals("Character")) {
//										
//									} else if ((paraType.equals("int") || paraType.equals("Integer"))
//											&& targetType.equals("int") || targetType.equals("Integer")) {
//									} else if (paraType.equalsIgnoreCase("double/float")
//											&& (targetType.equalsIgnoreCase("double") || targetType.equalsIgnoreCase("float"))) {
//										
//									} else if (paraType.equalsIgnoreCase(targetType)) {
//										// boolean, long, float, double
//									} else if (paraType.equals("Object")) {
//										// fuzzing matching.
//									} else {
//										matched = false;
//										break;
//									}
//								}
//								if (matched) {
//									insertIndex = index;
//									break;
//								}
//							}
//						}
//					}
//				}
//			}
		}
		
	}
	
	@SuppressWarnings("unused")
	private void insertParameter(ITree methodNameNode, List<ITree> paraAsts) {
		List<ITree> parameters = new ArrayList<>();
		boolean isParameter = false;
		for (ITree paraAst : paraAsts) {
			if (isParameter) {
				parameters.add(paraAst);
			} else if (Checker.isSimpleType(paraAst.getType())) {
				isParameter = true;
			}
		}
	}

//	private List<Integer> getSubParaList(int initIndex, int lcsValue, List<String> paraList, List<String> buggyParaList) {
//		for (int i = 0, size1 = paraList.size(), size2 = buggyParaList.size(); i < lcsValue; i ++) {
//			if (paraList.get(i).equals(buggyParaList.get(0))) {
//				List<Integer> a = new ArrayList<>();
//				a.add(i + initIndex);
//				if (size2 != 1) {
//					List<String> subParaList = new ArrayList<>();
//					subParaList.addAll(paraList.subList(i + 1, size1));
//					initIndex += i + 1;
//					a.addAll(getSubParaList(initIndex, lcsValue - 1, subParaList, buggyParaList.subList(1, size2)));
//				}
//				
//			}
//		}
//		return null;
//	}

	private boolean sameParaList(List<String> subParaList, List<String> buggyParaList) {
		if (buggyParaList.size() != subParaList.size()) return false;
		for (int i = 0, size = subParaList.size(); i < size; i ++) {
			if (!subParaList.get(i).equals(buggyParaList.get(i))) return false;
		}
		return true;
	}

	@SuppressWarnings("unused")
	private int lcs(List<String> l1, List<String> l2) {
        int sizeOfL1 = l1.size();
        int sizeOfL2 = l2.size();

        int[][] c = new int[sizeOfL1 + 1][sizeOfL2 + 1];

        for (int i = 1; i <= sizeOfL1; i++) {
            for (int j = 1; j <= sizeOfL2; j++) {
                if (l1.get(i - 1).equals(l2.get(j - 1))) {
                    c[i][j] = c[i - 1][j - 1] + 1;

                } else {
                    c[i][j] = Math.max(c[i][j - 1], c[i - 1][j]);
                }
            }
        }

        return c[sizeOfL1][sizeOfL2];
	}

	@SuppressWarnings("unused")
	private List<String> findVariableNames(ITree codeAst, String varType) {
		List<String> varNames = new ArrayList<>();
		String varName = null;
		while (true) {
			int codeAstType = codeAst.getType();
			if (Checker.isStatement(codeAstType)) {// variable
				varName = readVariableName(codeAst, codeAstType, varType);
				if (varName != null) varNames.add(varName);
				if (Checker.isStatement(codeAst.getParent().getType())) {
					List<ITree> children = codeAst.getParent().getChildren();
					for (ITree child : children) {
						if (child == codeAst) break;
						varName = readVariableName(child, child.getType(), varType);
						if (varName != null) varNames.add(varName);
					}
				}
			} else if (Checker.isMethodDeclaration(codeAstType)) { // parameter type.
				List<ITree> children = codeAst.getChildren();
				for (ITree child : children) {
					int childType = child.getType();
					if (Checker.isStatement(childType)) break;
					varName = readVariableName(child, childType, varType);
					if (varName != null) varNames.add(varName);
				}
			} else if (Checker.isTypeDeclaration(codeAstType)) {// Field
				List<ITree> children = codeAst.getChildren();
				for (ITree child : children) {
					int childType = child.getType();
					if (Checker.isFieldDeclaration(childType)) {
						List<ITree> subChildren = child.getChildren();
						for (int i = 0, size = subChildren.size(); i < size; i ++) {
							ITree subChild = subChildren.get(i);
							if (!Checker.isModifier(subChild.getType())) {
								if (varType.equals(subChild.getLabel())) {
									varName = subChildren.get(i + 1).getChild(0).getLabel();
								}
								break;
							}
						}
					}
					if (varName != null) varNames.add(varName);
				}
				break;
			}
			
			codeAst = codeAst.getParent();
			if (codeAst == null) break;
		}
		
		return varNames;
	}
	
	private String readVariableName(ITree stmtTree, int stmtType, String varType) {
		String varName = null;
		if (Checker.isVariableDeclarationStatement(stmtType) || Checker.isSingleVariableDeclaration(stmtType)) {
			List<ITree> children = stmtTree.getChildren();
			for (int index = 0, size = children.size(); index < size; index ++) {
				ITree child = children.get(index);
				if (!Checker.isModifier(child.getType())) {
					if (varType.equals(child.getLabel())) {
						if (Checker.isSingleVariableDeclaration(stmtType)) {
							varName = children.get(index + 1).getLabel();
						} else {
							varName = children.get(index + 1).getChild(0).getLabel();
						}
					}
					break;
				}
			}
		}
		
		return varName;
	}
}
