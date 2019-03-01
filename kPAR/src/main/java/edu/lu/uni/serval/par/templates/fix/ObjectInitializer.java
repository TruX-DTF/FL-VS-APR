package edu.lu.uni.serval.par.templates.fix;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.lu.uni.serval.jdt.tree.ITree;
import edu.lu.uni.serval.par.templates.InitializeAnObject;
import edu.lu.uni.serval.utils.Checker;

/**
 * For a variable in a method call, this template inserts an initialization
 * statement before the call. The statement uses the basic constructor which has
 * no parameter.
 * 
 * Context: variable and MethodInvocatio --> VariableDeclaration?.
 * 
 * @author kui.liu
 *
 */
public class ObjectInitializer extends InitializeAnObject {

	/*
	 * T var = new T(); or T var = 0 ...;
	 */
	
	@Override
	public void generatePatches() {
		ITree suspCodeAst = this.getSuspiciousCodeTree();
		List<String> allSuspVariables = identifySuspiciousVariables(suspCodeAst);
		Map<String, String> varTypesMap = readVariableTypes(suspCodeAst, allSuspVariables);
		
		for (Map.Entry<String, String> entry : varTypesMap.entrySet()) {
			String varName = entry.getKey();
			String varType = entry.getValue();
			String fixedCodeStr1 = varName;
			if ("char".equals(varType)) {
				fixedCodeStr1 += " = ' ';\n";
			} else if ("Character".equals(varType)) {
				fixedCodeStr1 += " = ' ';\n";
//				this.generatePatch(suspCodeStartPos, fixedCodeStr1);
//				fixedCodeStr1 = varName + " = null;\n";
			} else if ("int".equals(varType)) {
				fixedCodeStr1 += " = 0;\n";
				this.generatePatch(suspCodeStartPos, fixedCodeStr1);
				fixedCodeStr1 = varName + " = 1;\n";
			} else if ("Integer".equals(varType)) {
				fixedCodeStr1 += " = 0;\n";
//				this.generatePatch(suspCodeStartPos, fixedCodeStr1);
//				fixedCodeStr1 = varName + " = null;\n";
			} else if ("double".equals(varType)) {
				fixedCodeStr1 += " = 0d;\n";
			} else if ("Double".equals(varType)) {
				fixedCodeStr1 += " = 0d;\n";
//				this.generatePatch(suspCodeStartPos, fixedCodeStr1);
//				fixedCodeStr1 = varName + " = null;\n";
			} else if ("float".equals(varType)) {
				fixedCodeStr1 += " = 0f;\n";
			} else if ("Float".equals(varType)) {
				fixedCodeStr1 += " = 0f;\n";
//				this.generatePatch(suspCodeStartPos, fixedCodeStr1);
//				fixedCodeStr1 = varName + " = null;\n";
			} else if ("short".equals(varType)) {
				fixedCodeStr1 += " = 0;\n";
				this.generatePatch(suspCodeStartPos, fixedCodeStr1);
				fixedCodeStr1 = varName + " = 1;\n";
			} else if ("Short".equals(varType)) {
				fixedCodeStr1 += " = 0;\n";
//				this.generatePatch(suspCodeStartPos, fixedCodeStr1);
//				fixedCodeStr1 = varName + " = null;\n";
			} else if ("byte".equals(varType)) {
				fixedCodeStr1 += " = 0;\n";
				this.generatePatch(suspCodeStartPos, fixedCodeStr1);
				fixedCodeStr1 = varName + " = 1;\n";
			} else if ("Byte".equals(varType)) {
				fixedCodeStr1 += " = 0;\n";
//				this.generatePatch(suspCodeStartPos, fixedCodeStr1);
//				fixedCodeStr1 = varName + " = null;\n";
			} else if ("long".equals(varType)) {
				fixedCodeStr1 += " = 0l;\n";
				this.generatePatch(suspCodeStartPos, fixedCodeStr1);
				fixedCodeStr1 = varName + " = 1l;\n";
			} else if ("Long".equals(varType)) {
				fixedCodeStr1 += " = 0l;\n";
//				this.generatePatch(suspCodeStartPos, fixedCodeStr1);
//				fixedCodeStr1 = varName + " = null;\n";
			} else if ("boolean".equals(varType)) {
				fixedCodeStr1 += " = true;\n";
				this.generatePatch(suspCodeStartPos, fixedCodeStr1);
				fixedCodeStr1 = varName + " = false;\n";
			} else if ("Boolean".equals(varType)) {
				fixedCodeStr1 += " = true;\n";
				this.generatePatch(suspCodeStartPos, fixedCodeStr1);
				fixedCodeStr1 = varName + " = false;\n";
//				this.generatePatch(suspCodeStartPos, fixedCodeStr1);
//				fixedCodeStr1 = varName + " = null;\n";
			} else if ("String".equals(varType)) {
				fixedCodeStr1 += " = \"\";\n";
//				this.generatePatch(suspCodeStartPos, fixedCodeStr1);
//				fixedCodeStr1 = varName + " = null;\n";
			} else if ("List".equals(varType)) {
				fixedCodeStr1 += " = new ArrayList<>();\n";
//				this.generatePatch(suspCodeStartPos, fixedCodeStr1);
//				fixedCodeStr1 = varName + " = null;\n";
			} else if ("Map".equals(varType)) {
				fixedCodeStr1 += " = new HashMap<>();\n";
//				this.generatePatch(suspCodeStartPos, fixedCodeStr1);
//				fixedCodeStr1 = varName + " = null;\n";
			} else if (varType.endsWith("[]")) {
				int size = 0;
				varType = varType.trim();
				while (!varType.endsWith("[]")) {
					size ++;
					varType = varType.substring(0, varType.length() - 2).trim();
				}
				fixedCodeStr1 += " = new " + varType;
				if (size == 0) {
					return;
				} else {
					for (int i = 0; i < size; i ++) {
						fixedCodeStr1 += "[0]";
					}
					fixedCodeStr1 += ";\n";
				}
			} else {
				fixedCodeStr1 += " = new " + varType + "();\n";
//				this.generatePatch(suspCodeStartPos, fixedCodeStr1);
//				fixedCodeStr1 = varName + " = null;\n";
			}
			this.generatePatch(suspCodeStartPos, fixedCodeStr1);
		}
	}

	private Map<String, String> readVariableTypes(ITree suspCodeAst, List<String> allSuspVariables) {
		Map<String, String> varNamesMap = new HashMap<>();
		while (true) {
			int parentTreeType = suspCodeAst.getType();
			if (Checker.isStatement(parentTreeType)) {// variable
				readVariableDeclaration(suspCodeAst, parentTreeType, varNamesMap, allSuspVariables);
				parentTreeType = suspCodeAst.getParent().getType();
				if (Checker.isStatement(parentTreeType) || Checker.isMethodDeclaration(parentTreeType)) {
					List<ITree> children = suspCodeAst.getParent().getChildren();
					int index = children.indexOf(suspCodeAst) - 1;
					for (; index >= 0; index --) {
						ITree child = children.get(index);
						int childType = child.getType();
						if (!Checker.isStatement(childType)) break;
						readVariableDeclaration(child, childType, varNamesMap, allSuspVariables);
					}
				}
			} else if (Checker.isMethodDeclaration(parentTreeType)) { // parameter type.
				List<ITree> children = suspCodeAst.getChildren();
				for (ITree child : children) {
					int childType = child.getType();
					if (Checker.isStatement(childType)) break;
					readSingleVariableDeclaration(child, childType, varNamesMap, allSuspVariables);
				}
			} else if (Checker.isTypeDeclaration(parentTreeType)) {// Field
				List<ITree> children = suspCodeAst.getChildren();
				for (ITree child : children) {
					int childType = child.getType();
					if (Checker.isFieldDeclaration(childType)) {
						List<ITree> subChildren = child.getChildren();
						boolean readVar = false;
						boolean isStatic = false;
						String varType = null;
						for (ITree subChild : subChildren) {
							if (readVar) {
								String varName = subChild.getChild(0).getLabel();
								if (allSuspVariables.contains(varName)) {
									varNamesMap.put(varName, varType);
								} else if (!isStatic){
									varName = "this." + varName;
									if (allSuspVariables.contains(varName)) {
										varNamesMap.put(varName, varType);
									}
								}
							} else if (!Checker.isModifier(subChild.getType())) {
								varType = this.readType(subChild.getLabel());
								readVar = true;
							} else {
								if (subChild.getLabel().equals("static")) isStatic = true;
							}
						}
					}
				}
				// TODO: fields in the super class.
				break;
			}
			
			suspCodeAst = suspCodeAst.getParent();
			if (suspCodeAst == null) break;
		}
		return varNamesMap;
	}
	
	/**
	 * Read the information of a variable in the variable declaration nodes.
	 * @param stmtTree
	 * @param stmtType
	 * @param varNamesMap
	 * @param varTypesMap
	 * @param allVarNamesList
	 */
	private void readVariableDeclaration(ITree stmtTree, int stmtType, Map<String, String> varNamesMap, List<String> allSuspVariables) {
		String varType = null;
		if (Checker.isVariableDeclarationStatement(stmtType)) {
			List<ITree> children = stmtTree.getChildren();
			boolean readVar = false;
			for (ITree child : children) {
				if (readVar) {
					String varName = child.getChild(0).getLabel();
					if (allSuspVariables.contains(varName)) {
						varNamesMap.put(varName, varType);
					}
				} else if (!Checker.isModifier(child.getType())) {
					varType = this.readType(child.getLabel());
					readVar = true;
				}
			}
		} else if (Checker.isForStatement(stmtType)) {
			ITree varDecFrag = stmtTree.getChild(0);
			if (Checker.isVariableDeclarationExpression(varDecFrag.getType())) {
				List<ITree> children = varDecFrag.getChildren();
				varType = this.readType(children.get(0).getLabel());
				for (int i = 1, size = children.size(); i < size; i ++) {
					ITree child = children.get(i);
					String varName = child.getChild(0).getLabel();
					if (allSuspVariables.contains(varName)) {
						varNamesMap.put(varName, varType);
					}
				}
			}
		} else if (Checker.isEnhancedForStatement(stmtType)) {
			ITree singleVarDec = stmtTree.getChild(0);
			readSingleVariableDeclaration(singleVarDec, singleVarDec.getType(), varNamesMap, allSuspVariables);
		}
	}
	
	private void readSingleVariableDeclaration(ITree codeTree, int treeType, Map<String, String> varNamesMap, List<String> allSuspVariables) {
		if (Checker.isSingleVariableDeclaration(treeType)) {
			List<ITree> children = codeTree.getChildren();
			int size = children.size();
			String varType = this.readType(children.get(size - 2).getLabel());
			String varName = children.get(size - 1).getLabel();
			if (allSuspVariables.contains(varName)) {
				varNamesMap.put(varName, varType);
			}
		}
	}
	
}
