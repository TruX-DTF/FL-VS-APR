package edu.lu.uni.serval.par.templates;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.lu.uni.serval.AST.ASTGenerator;
import edu.lu.uni.serval.AST.ASTGenerator.TokenType;
import edu.lu.uni.serval.jdt.tree.ITree;
import edu.lu.uni.serval.utils.Checker;

/**
 * The method invocations are limited to all methods defined in the current program.
 * 
 * @author kui.liu
 *
 */
public abstract class AlterMethodInvocation extends FixTemplate {
	
	/*
	 * 1. method1() -> method1
	 * 2. a.method1().method2() -> method2
	 * 3. a.method1().method2() -> method1
	 */
	protected Map<ITree, Integer> suspMethodInvocations = new HashMap<>();
	ITree classDeclarationAst = null;
	String packageName = null;
	String className = null;
	
	protected List<MethodInvocationExpression> identifySuspiciousMethodInvocations() {
		ITree suspCodeTree = this.getSuspiciousCodeTree();
		allVarNamesMap = readAllVariableNames(suspCodeTree, varTypesMap, allVarNamesList);
		
		if (this.classDeclarationAst == null) {
			readClassDeclaration(suspCodeTree);
		}
		if (this.classDeclarationAst == null) {
			return new ArrayList<MethodInvocationExpression>();
		}
		if (className == null) {
			readClassName(suspCodeTree);
		}
		if (className == null) {
			return new ArrayList<MethodInvocationExpression>();
		}
		readPackageName();
		if (packageName == null) return new ArrayList<MethodInvocationExpression>();
		
		identifySuspiciousMethodInvocationExps(suspCodeTree);
		return identifySuspiciousMethodInvocations1();
	}

	private void identifySuspiciousMethodInvocationExps(ITree suspCodeTree) {
		int suspCodeTreeType = suspCodeTree.getType();
		if (Checker.isConstructorInvocation(suspCodeTreeType) || Checker.isSuperConstructorInvocation(suspCodeTreeType)) {
			suspMethodInvocations.put(suspCodeTree, 2);
		}
		
		List<ITree> children = suspCodeTree.getChildren();
		for (ITree child : children) {
			int type = child.getType();
			if (Checker.isMethodInvocation(type)) {
				if (Checker.isMethodInvocation(suspCodeTree.getType())) {
					int childIndex = suspCodeTree.getChildPosition(child);
					if (childIndex < (children.size() - 1)) {
						int startPos = child.getPos() + child.getLength();
						String tempCode = this.getSubSuspiciouCodeStr(startPos, this.suspCodeEndPos).trim();
						if (tempCode.startsWith(".")) {
							suspMethodInvocations.put(child, 3);
						} else if (tempCode.startsWith("//") ||tempCode.startsWith("/*")) {
							String[] tempCodeArr = tempCode.split("\n");
							for (String tempC : tempCodeArr) {
								tempC = tempC.trim();
								if (tempC.startsWith("//") || tempC.startsWith("/*")) continue;
								if (tempC.startsWith(".")) {
									suspMethodInvocations.put(child, 3);
								} else {
									suspMethodInvocations.put(child, 2);
								}
								break;
							}
						} else {
							// Parameter of a method invocation.
							suspMethodInvocations.put(child, 2);
						}
					} else {
						if (child.getChildren().isEmpty()){
							suspMethodInvocations.put(child, 1);
						} else {
							suspMethodInvocations.put(child, 2);
						}
					}
				} else if (child.getChildren().isEmpty()){
					suspMethodInvocations.put(child, 1);
				} else {
					suspMethodInvocations.put(child, 2);
				}
				identifySuspiciousMethodInvocationExps(child);
			} else if (Checker.isSimpleName(type)) {
				if (child.getLabel().startsWith("MethodName:")) {
					boolean contained = false;
					for (Map.Entry<ITree, Integer> entry : suspMethodInvocations.entrySet()) {
						int size = entry.getKey().getChildren().size();
						if (size == 0) continue;
						if (entry.getKey().getChild(size - 1).equals(child)) {
							contained = true;
							break;
						}
					}
					if (!contained) {
						suspMethodInvocations.put(child, 2);
					}
					identifySuspiciousMethodInvocationExps(child);
				}
			} else if (Checker.isConstructorInvocation(type) || Checker.isSuperConstructorInvocation(type) || Checker.isClassInstanceCreation(type)) {
				suspMethodInvocations.put(child, 2);
				identifySuspiciousMethodInvocationExps(child);
			} else if (Checker.isSuperMethodInvocation(type)) {
				List<ITree> subChildren = child.getChildren();
				ITree subChild = subChildren.get(subChildren.size() - 1);
				suspMethodInvocations.put(child, 2);
				identifySuspiciousMethodInvocationExps(subChild);
			} else if (Checker.isComplexExpression(type)) {
				identifySuspiciousMethodInvocationExps(child);
			} else if (Checker.isStatement(type)) break;
		}
	}
	
	/**
	 * Read the information of suspicious method invocations.
	 * @return
	 */
	private List<MethodInvocationExpression> identifySuspiciousMethodInvocations1() {
		List<MethodInvocationExpression> suspMethodInvocations = new ArrayList<>();
		
		for (Map.Entry<ITree, Integer> entry : this.suspMethodInvocations.entrySet()) {
			ITree suspMethodInv = entry.getKey();
			ITree methodNameNode = suspMethodInv;
			int type = suspMethodInv.getType();
			
			// Read method name and parameters.
			// Read the possible return types of the method.
			ITree rootTree = null;
			String varType = null;
			List<ITree> parameters = methodNameNode.getChildren();
			String methodName = null;
			if (Checker.isConstructorInvocation(type)) {
				methodName = "=CONSTRUCTOR=";
				varType = "this=CONSTRUCTOR=";
				rootTree = this.classDeclarationAst;
			} else if (Checker.isSuperConstructorInvocation(type)) {
				methodName = "super=CONSTRUCTOR=";
				varType = "this+Super=CONSTRUCTOR=";
				rootTree = this.classDeclarationAst;
			} else if (Checker.isClassInstanceCreation(type)) {
				int size = parameters.size();
				boolean isClassName = false;
				int i = 0;
				for (; i < size; i ++) {
					if (isClassName) {
						methodName = methodNameNode.getChild(i).getLabel(); 
						break;
					} else if ("new".equals(methodNameNode.getChild(i).getLabel())) {
						isClassName = true;
					}
				}
				if (Checker.isAnonymousClassDeclaration(parameters.get(size - 1).getType())) continue;
				parameters = parameters.subList(i + 1, size);
				varType = methodNameNode.getChild(1).getLabel() + "=CONSTRUCTOR=";
				rootTree = this.classDeclarationAst;
			} else {
				int methodType = entry.getValue();
				if (methodType == 2) {
					List<ITree> children = suspMethodInv.getChildren();
					methodNameNode = children.get(children.size() - 1);
					parameters = methodNameNode.getChildren();
				}
				methodName = methodNameNode.getLabel().substring(11);
				methodName = methodName.substring(0, methodName.indexOf(":"));
				
				if (Checker.isSuperMethodInvocation(methodNameNode.getParent().getType())) {
					varType = "this+Super";
					rootTree = this.classDeclarationAst;
				} else {
					ITree parentCodeAst = methodNameNode.getParent();
					int indexPos = parentCodeAst.getChildPosition(methodNameNode);
					if (indexPos == 0) { // the method belongs to the current class or its ancestral classes.
						rootTree = this.classDeclarationAst;
						varType = "this";
					} else { // the method belongs to the class of the return data type of its previous peer AST node..
						ITree prePeerCodeAst = parentCodeAst.getChild(indexPos - 1);
						/*
						 * The previous peer AST node can be:
						 * 		a. field
						 * 		b. qualified name.
						 * 		c. method invocation.
						 * 		d. other complex expressions.
						 */
						int prePeerCodeAstType = prePeerCodeAst.getType();
						if (Checker.isSimpleName(prePeerCodeAstType)) { // a variable.
							String varName = this.readVariableName2(prePeerCodeAst);
							varType = this.varTypesMap.get(varName);
							if (varType == null) varType = this.varTypesMap.get("this." + varName);
							if (varType == null) varType = varName;
							rootTree = this.classDeclarationAst;
						} else if (Checker.isQualifiedName(prePeerCodeAstType)) { 
							// QualifiedName: T.var.get()
							String dataType = prePeerCodeAst.getLabel(); 
							if (this.varTypesMap.containsKey(dataType)) {
								varType = this.varTypesMap.get(dataType);
								rootTree = this.classDeclarationAst;
							} else {
								int firstPointIndex = dataType.indexOf(".");
								String fieldName = dataType.substring(firstPointIndex + 1); // Class name.
								dataType = dataType.substring(0, firstPointIndex);
								String dataTypeFile = this.identifyJavaFilePath(this.classDeclarationAst, dataType);
								if (dataTypeFile != null) { // field data type.
									rootTree = new ASTGenerator().generateTreeForJavaFile(dataTypeFile, TokenType.EXP_JDT);
									List<ITree> children = rootTree.getChildren();
									children = children.get(children.size() - 1).getChildren();
									for (ITree child : children) {
										if (Checker.isFieldDeclaration(child.getType())) { // Field declaration
											List<ITree> subChildren = child.getChildren();
											boolean isFound = false;
											for (int i = 1, size = subChildren.size(); i < size; i ++) {
												ITree varDeclaration = subChildren.get(i);
												if (Checker.isVariableDeclarationFragment(varDeclaration.getType())) {
													if (varType == null) varType = subChildren.get(i - 1).getLabel();
													if (varDeclaration.getChild(0).getLabel().equals(fieldName)) {
														isFound = true;
														break;
													}
												}
											}
											if (isFound) break;
											varType = null;
										}
									}
								}
							}
						} else if (Checker.isFieldAccess(prePeerCodeAstType)
								|| Checker.isSuperFieldAccess(prePeerCodeAstType)) {
							// FieldAccess: this.var.get();
							// SuperFieldAccess: super.var.get();
							List<ITree> subChildren = prePeerCodeAst.getChildren();
							String dataType = subChildren.get(subChildren.size() - 1).getLabel();
							varType = this.varTypesMap.get(dataType);
							if (varType == null) varType = this.varTypesMap.get("this." + dataType);
							rootTree = this.classDeclarationAst;
							if (varType == null) {
								// TODO: is it possible to be null?
								continue;
							}
						} else if (Checker.isMethodInvocation(prePeerCodeAstType)) {
							// TODO the return type of the previous peer method invocation.
							continue;
						} else {
							// FIXME: other possible expressions.
							continue;
						}
					}
				}
			}
			if (rootTree == null || varType == null) continue;
			
			// Read parameter data types.
			List<String> paraTypeStrs = readMethodParameterTypes(parameters);
			if (paraTypeStrs == null) continue; // Generate ERROR when reading its parameter types.
			
			// Identify possible return types of the method invocations.
			List<String> possibleReturnTypes = null;
			String methodClassPath = null;
			Map<List<String>, String> map = identifyPossibleReturnTypes(rootTree, varType, methodName, paraTypeStrs);
			if (map != null) {
				for (Map.Entry<List<String>, String> subEntry : map.entrySet()) {
					possibleReturnTypes = subEntry.getKey();
					methodClassPath = subEntry.getValue();
					break;
				}
			}
			
			if (possibleReturnTypes != null && !possibleReturnTypes.isEmpty()) {
				MethodInvocationExpression mi = new MethodInvocationExpression();
				mi.setCodePath(methodClassPath);
				mi.setMethodName(methodName);
				mi.setCodeAst(methodNameNode);
				mi.setCouldBeReplacedMethods(this.couldBeReplacedMethods);
				mi.setDifferentParaMethods(this.differentParaMethods);
				for (String possibleReturnType : possibleReturnTypes) {
					String[] elements = possibleReturnType.split("\\+");
					mi.getPossibleReturnTypes().add(elements[0]);
					paraTypeStrs = new ArrayList<>();
					for (int i = 1, length = elements.length; i < length; i = i + 2) {
						paraTypeStrs.add(elements[i]);
					}
					mi.getParameterTypes().add(paraTypeStrs);
				}
				suspMethodInvocations.add(mi);
			} else {
				// TODO
//				if (Checker.isClassInstanceCreation(type)) {
//					MethodInvocationExpression mi = new MethodInvocationExpression();
//					mi.setCodePath(methodClassPath);
//					mi.setMethodName(methodName);
//					mi.setCodeAst(methodNameNode);
//					mi.setCouldBeReplacedMethods(this.couldBeReplacedMethods);
//					mi.setDifferentParaMethods(this.differentParaMethods);
//					if (possibleReturnTypes != null) {
//						for (String possibleReturnType : possibleReturnTypes) {
//							String[] elements = possibleReturnType.split("\\+");
//							mi.getPossibleReturnTypes().add(elements[0]);
//							paraTypeStrs = new ArrayList<>();
//							for (int i = 1, length = elements.length; i < length; i = i + 2) {
//								paraTypeStrs.add(elements[i]);
//							}
//							mi.getParameterTypes().add(paraTypeStrs);
//						}
//					}
//					suspMethodInvocations.add(mi);
//				}
			}
		}
		return suspMethodInvocations;
	}

	/**
	 * Identify the java file path of a data type.
	 * 
	 * @param classDeclarationAst
	 * @param varType
	 * @return
	 */
	private String identifyJavaFilePath(ITree classDeclarationAst, String varType) {
		List<ITree> rootTreeChildren = classDeclarationAst.getParent().getChildren();
		String path = null;
		for (ITree child : rootTreeChildren) {
			int childType = child.getType();
			String childLabel = child.getLabel();
			if (Checker.isImportDeclaration(childType)) { // import declarations.
				if (childLabel.endsWith(varType)) {
					path = this.sourceCodePath + child.getLabel().replace(".", "/") + ".java";
					if (!new File(path).exists()) path = null;
					break;
				}
			} else if (Checker.isPackageDeclaration(childType)) { // package name.
				path = this.sourceCodePath + child.getLabel().replace(".", "/") + "/" + varType + ".java";
				if (new File(path).exists()) break;
				else path = null;
			} else {
				break;
			}
		}
		return path;
	}

	/**
	 * 
	 * @param classDeclarationAst
	 * @param varType
	 * @param methodName
	 * @param paraTypeStrs
	 * @return
	 */
	private Map<List<String>, String> identifyPossibleReturnTypes(ITree classDeclarationAst, String varType, String methodName, List<String> paraTypeStrs) {
		
		if (varType == null) return null;
		
		String path = null;
		int constructorIndex = varType.indexOf("=CONSTRUCTOR=");
		boolean isConstructor = false;
		boolean isSuperClass = false;
		if (constructorIndex > 0) {
			isConstructor = true;
			varType = varType.substring(0, constructorIndex);
		}
		int superIndex = varType.indexOf("+Super");
		if (superIndex > 0) {
			varType = varType.substring(0, superIndex);
			isSuperClass = true;
		}
		if ("this".equals(varType)) {
			path = this.sourceCodePath + this.packageName.replace(".", "/") + "/" + this.className + ".java";
		} else {
			path = identifyJavaFilePath(classDeclarationAst, varType);
			if (path == null) return null;
			classDeclarationAst = new ASTGenerator().generateTreeForJavaFile(path, TokenType.EXP_JDT);
			List<ITree> children = classDeclarationAst.getChildren();
			classDeclarationAst = children.get(children.size() - 1);
		}
		
		return identifyPossibleReturnTypes(classDeclarationAst, methodName, paraTypeStrs, path, isConstructor, isSuperClass);
	}

	Map<String, List<String>> couldBeReplacedMethods; // replace method name.
	List<ITree> differentParaMethods;   // add or delete parameter(s);
	
	/**
	 * 
	 * @param classDeclarationAst
	 * @param methodName
	 * @param paraTypeStrs
	 * @return
	 */
	private Map<List<String>, String> identifyPossibleReturnTypes(ITree classDeclarationAst, String methodName, List<String> paraTypeStrs, 
			String path, boolean isConstructor, boolean isSuperClass) {
		couldBeReplacedMethods = new HashMap<>(); // replace method name.
		differentParaMethods = new ArrayList<>();   // add or delete parameter(s);
		List<String> possibleReturnTypes = new ArrayList<>();
		String superConstructor = "";
		if (!isSuperClass) {
			List<ITree> children = classDeclarationAst.getChildren();
			for (ITree child : children) {
				if (Checker.isMethodDeclaration(child.getType())) {
					// Match method name.
					String label = child.getLabel();
					int indexOfMethodName = label.indexOf("MethodName:");
					int indexOrPara = label.indexOf("@@Argus:");
					String currentMethodName = label.substring(indexOfMethodName + 11, indexOrPara - 2);
					
					// Match parameter data types.
					String paraStr = label.substring(indexOrPara + 8);
					if (paraStr.startsWith("null")) {
						paraStr = null;
					} else {
						int indexExp = paraStr.indexOf("@@Exp:");
						if (indexExp > 0) paraStr = paraStr.substring(0, indexExp);
					}
					
					// Read return type.
					String returnType = label.substring(label.indexOf("@@") + 2, indexOfMethodName - 2);
					int index = returnType.indexOf("@@tp:");
					if (index > 0) returnType = returnType.substring(0, index - 2);
					
					if (isConstructor) {
						if (!"=CONSTRUCTOR=".equals(returnType)) {// Constructor.
							continue;
						}
//					} else if (!currentMethodName.equals(methodName)) continue;
					} else if ("=CONSTRUCTOR=".equals(returnType)) continue;
					else returnType = this.readType(returnType);
		
					// Match possible return types.
					if (paraTypeStrs.isEmpty()) {
						if (paraStr == null) {
							if (currentMethodName.equals(methodName)) {
								possibleReturnTypes.add(returnType);
							} else { // methods with same parameter and same(possible) return type
								List<String> methodNamesList = couldBeReplacedMethods.get(returnType);
								if (methodNamesList == null) methodNamesList = new ArrayList<String>();
								methodNamesList.add(currentMethodName);
								couldBeReplacedMethods.put(returnType, methodNamesList);
							}
						} else if (currentMethodName.equals(methodName)) {
							differentParaMethods.add(child);
						}
					} else if (paraStr != null) {
						List<String> paraList = parseMethodParameterTypes(paraStr, "\\+");
						if (paraList.size() == paraTypeStrs.size()) {
							boolean matched = true;
							for (int i = 0, size = paraTypeStrs.size(); i < size; i ++) {
								String paraType = paraTypeStrs.get(i);
								String targetType = paraList.get(i);
								if ((paraType.equals("char") || paraType.equals("Character"))
										&& targetType.equals("char") || targetType.equals("Character")) {
									
								} else if ((paraType.equals("int") || paraType.equals("Integer"))
										&& targetType.equals("int") || targetType.equals("Integer")) {
								} else if (paraType.equalsIgnoreCase("double/float")
										&& (targetType.equalsIgnoreCase("double") || targetType.equalsIgnoreCase("float"))) {
									
								} else if (paraType.equalsIgnoreCase(targetType)) {
								} else if (paraType.endsWith(targetType) || targetType.endsWith(paraType)) {
									// boolean, long, float, double
								} else if (paraType.equals("Object")) {
									// fuzzing matching.
								} else {
									matched = false;
									break;
								}
							}
							if (matched) {
								if (currentMethodName.equals(methodName)) {
									possibleReturnTypes.add(returnType + "+" + paraStr);
								} else { // methods with same parameter and same(possible) return type
									List<String> methodNamesList = couldBeReplacedMethods.get(returnType);
									if (methodNamesList == null) methodNamesList = new ArrayList<String>();
									methodNamesList.add(currentMethodName);
									couldBeReplacedMethods.put(returnType, methodNamesList);
								}
							}
						} else if (currentMethodName.equals(methodName)) {
							differentParaMethods.add(child);
						}
					} else if (currentMethodName.equals(methodName)) {
						differentParaMethods.add(child);
					}
				}
			}
		} else if (isConstructor) {
			superConstructor = "=CONSTRUCTOR=";
		}
		
		
		if (possibleReturnTypes.isEmpty()) {
			String label = classDeclarationAst.getLabel();
			int index = label.indexOf("@@SuperClass:");
			if (index > 0) {
				String superClassName = label.substring(index + 13);
				index = superClassName.indexOf("@@Interface:");
				if (index < 0) index = superClassName.length();
				superClassName = superClassName.substring(0, index - 2) + superConstructor;
				Map<List<String>, String> map = identifyPossibleReturnTypes(classDeclarationAst, superClassName, methodName, paraTypeStrs);
				if (map != null) {
					possibleReturnTypes = map.entrySet().iterator().next().getKey();
				}
			}
		}
		Map<List<String>, String> map = new HashMap<>();
		map.put(possibleReturnTypes, path);
		return map;
	}

	/**
	 * Parse the parameter data types of a method declaration.
	 * 
	 * @param paraStr
	 * @return
	 */
	protected List<String> parseMethodParameterTypes(String paraStr, String splitStr) {
		List<String> parameterTypes = new ArrayList<>();
		String[] paraArray = paraStr.split(splitStr);
		for (int i = 0, length = paraArray.length; i < length; i = i + 2) {
			parameterTypes.add(readType(paraArray[i]));
		}
		return parameterTypes;
	}
	
	/**
	 * Read the parameter types of the suspicious method invocation expression.
	 * 
	 * @param parameters
	 * @return null - ERROR.
	 * 		   Empty_ArrayList - zero parameter.
	 * 		   other - parameter types.
	 */
	private List<String> readMethodParameterTypes(List<ITree> parameters) {
		List<String> paraTypeStrs = new ArrayList<>();
		if (parameters == null || parameters.isEmpty()) {
			// no parameter.
		} else {
			for (ITree para : parameters) {
				int paraAstType = para.getType();
				String paraLabel = para.getLabel();
				if (Checker.isSimpleName(paraAstType)) {
					paraLabel = this.readVariableName2(para);
					if (paraLabel == null) {
						paraTypeStrs.add("Object");
					} else {
						String dataType = varTypesMap.get(paraLabel);
						if (dataType == null) dataType = this.varTypesMap.get("this." + paraLabel);
						paraTypeStrs.add(dataType == null ? "Object" : dataType);
					}
				} else if (Checker.isBooleanLiteral(paraAstType)){  // BooleanLiteral
					paraTypeStrs.add("boolean");
				} else if (Checker.isCharacterLiteral(paraAstType)){ // CharacterLiteral
					paraTypeStrs.add("char");
				} else if (Checker.isNullLiteral(paraAstType)){ // NullLiteral
					paraTypeStrs.add("Object");
				} else if (Checker.isNumberLiteral(paraAstType)){ // NumberLiteral
					String lastChar = paraLabel.substring(paraLabel.length() - 1, paraLabel.length());
					if ("l".equalsIgnoreCase(lastChar)) {
						paraTypeStrs.add("long");
					} else if ("f".equalsIgnoreCase(lastChar)) {
						paraTypeStrs.add("float");
					} else if ("d".equalsIgnoreCase(lastChar)) {
						paraTypeStrs.add("double");
					} else if (paraLabel.contains(".")) {
						paraTypeStrs.add("double/float");
					} else {
						paraTypeStrs.add("int");
					}
				} else if (Checker.isStringLiteral(paraAstType)){ // ThisExpression
					paraTypeStrs.add("String");
				} else if (Checker.isThisExpression(paraAstType)){ // ThisExpression
					if (className == null) return null;//paraTypeStrs.add("Object");
					else paraTypeStrs.add(className);
				} else if (Checker.isFieldAccess(paraAstType)) {
					paraLabel = para.getChild(1).getLabel();
					String dataType = varTypesMap.get(paraLabel);//readVarialbeType(para, paraLabel);
					if (dataType == null) dataType = this.varTypesMap.get("this." + paraLabel);
					if(dataType!= null) paraTypeStrs.add(dataType);
					else paraTypeStrs.add("Object");
					// without QualifiedName and SuperFieldAccess.
				} else {// Complex Expressions.
					// FIXME: Is it possible to get the data type of the return value of the complex expression?
					paraTypeStrs.add("Object");
				}
			}
		}
		return paraTypeStrs;
	}
	
	/**
	 * Read the class declaration AST of the suspicious code.
	 * 
	 * @param suspCodeTree
	 */
	private void readClassDeclaration(ITree suspCodeTree) {
		ITree parent = suspCodeTree.getParent();
		while (true) {
			if (Checker.isTypeDeclaration(parent.getType())) {
				this.classDeclarationAst = parent;
			}
			parent = parent.getParent();
			if (parent == null) break;
		}
	}
	
	/**
	 * Read the class name of the suspicious code.
	 * @param suspCodeTree
	 */
	private void readClassName(ITree suspCodeTree) {
		if (this.classDeclarationAst == null) {
			readClassDeclaration(suspCodeTree);
		}
		if (this.classDeclarationAst == null) {
			// FIXME non-type declaration file.
			className = null;
			return;
		}
		List<ITree> classChildren = this.classDeclarationAst.getChildren();
		for (ITree classChild : classChildren) {
			if (classChild.getType() == 42)  // SimpleName
				className = classChild.getLabel().substring(10);
		}
	}
	
	/**
	 * Read the package name of the suspicious code.
	 */
	private void readPackageName() {
		ITree parent = this.classDeclarationAst.getParent();
		while (true) {
			ITree packageDeclaration = parent.getChild(0);
			if (Checker.isPackageDeclaration(packageDeclaration.getType())) {
				this.packageName = packageDeclaration.getLabel();
				break;
			}
			parent = parent.getParent();
			if (parent == null) break;
		}
	}
	
	public class MethodInvocationExpression {
		
		/*
		 * PackageName,
		 * ClassName,
		 * ReturnType,
		 * MethodName,
		 * Parameter Types.
		 */
		
		private String codePath;
		private List<String> possibleReturnTypes = new ArrayList<>(); //"=CONSTRUCTOR="
		private String methodName;
		private List<List<String>> parameterTypes = new ArrayList<>();
		private ITree codeAst;
		private Map<String, List<String>> couldBeReplacedMethods; // replace method name.
		private List<ITree> differentParaMethods; // add or delete parameter(s);

		public String getCodePath() {
			return codePath;
		}

		public void setCodePath(String codePath) {
			this.codePath = codePath;
		}

		public List<String> getPossibleReturnTypes() {
			return possibleReturnTypes;
		}

		public void setPossibleReturnTypes(List<String> possibleReturnTypes) {
			this.possibleReturnTypes = possibleReturnTypes;
		}

		public String getMethodName() {
			return methodName;
		}

		public void setMethodName(String methodName) {
			this.methodName = methodName;
		}

		public List<List<String>> getParameterTypes() {
			return parameterTypes;
		}

		public ITree getCodeAst() {
			return codeAst;
		}

		public void setCodeAst(ITree codeAst) {
			this.codeAst = codeAst;
		}

		public Map<String, List<String>> getCouldBeReplacedMethods() {
			return couldBeReplacedMethods;
		}

		public void setCouldBeReplacedMethods(Map<String, List<String>> couldBeReplacedMethods) {
			this.couldBeReplacedMethods = couldBeReplacedMethods;
		}

		public List<ITree> getDifferentParaMethods() {
			return differentParaMethods;
		}

		public void setDifferentParaMethods(List<ITree> differentParaMethods) {
			this.differentParaMethods = differentParaMethods;
		}
	}

}
