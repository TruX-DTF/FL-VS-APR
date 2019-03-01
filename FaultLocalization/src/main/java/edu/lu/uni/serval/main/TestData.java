package edu.lu.uni.serval.main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.CompilationUnit;

import edu.lu.uni.serval.AST.ASTGenerator;
import edu.lu.uni.serval.AST.ASTGenerator.TokenType;
import edu.lu.uni.serval.entity.Pair;
import edu.lu.uni.serval.jdt.tree.ITree;
import edu.lu.uni.serval.parser.JavaFileParser;
import edu.lu.uni.serval.utils.Checker;
import edu.lu.uni.serval.utils.FileHelper;

/**
 * Test the data manually extracted from Defects4J.
 * 
 * @author kui.liu
 *
 */
@Deprecated
public class TestData {
	
	static Map<String, List<PatchDiff>> patchMap = new HashMap<>();

	public static void main(String[] args) {
		String buggyFilePath = "../Data/buggyFiles/";
		readPatches("../Data/bugPositions.txt", buggyFilePath);

		for (int i = 1; i <= 26; i ++) {//Chart
			String projectName = "Chart_" + i;
			matchBuggyFiles(projectName, buggyFilePath);
		}
		for (int i = 1; i <= 65; i ++) {//Lang
			String projectName = "Lang_" + i;
			matchBuggyFiles(projectName, buggyFilePath);
		}
		for (int i = 1; i <= 106; i ++) {//Math
			String projectName = "Math_" + i;
			matchBuggyFiles(projectName, buggyFilePath);
		}
		for (int i = 1; i <= 133; i ++) {//Closure
			String projectName = "Closure_" + i;
			matchBuggyFiles(projectName, buggyFilePath);
		}
		for (int i = 1; i <= 38; i ++) {//Mockito
			String projectName = "Mockito_" + i;
			matchBuggyFiles(projectName, buggyFilePath);
		}
		for (int i = 1; i <= 27; i ++) {//Time
			String projectName = "Time_" + i;
			matchBuggyFiles(projectName, buggyFilePath);
		}
	}

	private static void matchBuggyFiles(String projectName, String buggyFilePath) {
		List<File> javaFiles = FileHelper.getAllFiles(buggyFilePath + projectName + "/", ".java");
		List<PatchDiff> pds = patchMap.get(projectName);
		if (pds == null) {
			System.err.println(projectName + " =======null.");
			return;
		}
		
		for (File javaFile : javaFiles) {
			String javaFileStr = javaFile.getPath();
			boolean included = false;
			for (PatchDiff pd : pds) {
				String buggyFile = pd.fileName;
				if (javaFileStr.endsWith(buggyFile)) {
					included = true;
					break;
				}
			}
			if (!included) {
				System.err.println(javaFileStr + " ======= null.");
			}
		}
	}

	public static void readPatches(String positionsFile, String buggyFilePath) {
		String[] bugPositions = FileHelper.readFile(positionsFile).split("\n");
		for (String bugPos : bugPositions) {
			String[] elements = bugPos.split("@");
			String bugId = elements[0];
			String buggyFileStr = elements[1];
			String posStr = elements[2];
			String[] posStrArr = posStr.split(",");
			
			List<Integer> buggyLines = new ArrayList<>();
			for (String pos : posStrArr) {
				if (pos.contains("-")) {
					String[] subPos = pos.split("-");
					for (int line = Integer.valueOf(subPos[0]), endLine = Integer.valueOf(subPos[1]); line <= endLine; line ++) {
						buggyLines.add(line);
					}
				} else {
					buggyLines.add(Integer.valueOf(pos));
				}
			}
			
			File buggyFile = new File(buggyFilePath + bugId + "/" + buggyFileStr);
			if (!buggyFile.exists()) {
				System.err.println(bugId + "/" + buggyFileStr);
				continue;
			}

			CompilationUnit unit = new JavaFileParser().new MyUnit().createCompilationUnit(buggyFile);
			ITree rootTree = new ASTGenerator().generateTreeForJavaFile(buggyFile, TokenType.EXP_JDT);
			List<Pair<Integer, Integer>> methodRanges = identifyMethod(rootTree, buggyLines, unit);
			if (methodRanges.isEmpty()) {
//				System.err.println(bugId + " --- null");
			} else {
				for (int buggyLine : buggyLines) {
					boolean included = false;
					for (Pair<Integer, Integer> methodRange : methodRanges) {
						if (methodRange.getFirst() <= buggyLine && buggyLine <= methodRange.getSecond()) {
							included = true;
							break;
						}
					}
					if (!included) {
//						System.err.println(bugId + " --- null " + buggyLine);
					}
				}
			}
			
			PatchDiff pd = new PatchDiff(-1, -1, -1, -1, -1, -1, null, buggyLines, null);
			pd.methodRanges = methodRanges;
			pd.setFileName(buggyFileStr);
			
			List<PatchDiff> pds = patchMap.get(bugId);
			if (pds == null) {
				pds = new ArrayList<>();
				patchMap.put(bugId, pds);
			}
			pds.add(pd);
		}
	}

	private static List<Pair<Integer, Integer>> identifyMethod(ITree tree, List<Integer> buggyLines, CompilationUnit unit) {
		List<Pair<Integer, Integer>> methodRanges = new ArrayList<>();
		List<Integer> buggyLinesCopy = new ArrayList<>();
		buggyLinesCopy.addAll(buggyLines);
		
		List<ITree> children = tree.getChildren();
		for (ITree child : children) {
			int astNodeType = child.getType();
			
			if (Checker.isMethodDeclaration(astNodeType)) {
				int startPosition = child.getPos();
				int endPosition = startPosition + child.getLength();
				int startLine = unit.getLineNumber(startPosition);
				int endLine = unit.getLineNumber(endPosition);
				
				int firstPos = buggyLinesCopy.get(0);
				int lastPos = buggyLinesCopy.get(buggyLinesCopy.size() - 1);
				
				if (startLine > lastPos) break;
				if (endLine < firstPos) continue;
				
				if (startLine <= firstPos && lastPos <= endLine) {
					Pair<Integer, Integer> methodRange = new Pair<Integer, Integer>(startLine, endLine);
					methodRanges.add(methodRange);
					break;
				} else {
					if ((firstPos <= endLine && lastPos > endLine)
							|| (firstPos < startLine && startLine <= lastPos)
							|| (firstPos < startLine && endLine < lastPos)) {
						for (int index = 0, size = buggyLinesCopy.size(); index < size; index ++) {
							int buggyLine = buggyLinesCopy.get(index);
							if (startLine <= buggyLine && buggyLine <= endLine) {
								Pair<Integer, Integer> methodRange = new Pair<Integer, Integer>(startLine, endLine);
								methodRanges.add(methodRange);
//								buggyLinesCopy = buggyLinesCopy.subList(index + 1, size);
								break;
							}
						}
//						if (buggyLinesCopy.isEmpty()) break;
					}
				}
			} else {
				methodRanges.addAll(identifyMethod(child, buggyLinesCopy, unit));
			}
 		}
		return methodRanges;
	}

}
