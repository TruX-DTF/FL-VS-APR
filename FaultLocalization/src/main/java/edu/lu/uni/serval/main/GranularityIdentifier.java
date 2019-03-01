package edu.lu.uni.serval.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.CompilationUnit;

import edu.lu.uni.serval.AST.ASTGenerator;
import edu.lu.uni.serval.AST.ASTGenerator.TokenType;
import edu.lu.uni.serval.config.Configuration;
import edu.lu.uni.serval.entity.Pair;
import edu.lu.uni.serval.jdt.tree.ITree;
import edu.lu.uni.serval.parser.JavaFileParser;
import edu.lu.uni.serval.utils.Checker;
import edu.lu.uni.serval.utils.FileHelper;

/**
 * Identify the fault locality in terms of three-level granularities: line, method, file.
 * 
 * @author kui.liu
 *
 */
public class GranularityIdentifier {
	
	static Map<String, List<PatchDiff>> patchMap = new HashMap<>();
	static StringBuilder builder = new StringBuilder("Proj,");
	static StringBuilder methodBuilder = new StringBuilder("Proj,");
	static StringBuilder fileBuilder = new StringBuilder("Proj,");
	static String suspiciousPath = "GZoltar-1.6.0/BugPositions/"; //  or "GZoltar-0.1.1/BugPositions/";

	public static void main(String[] args) {
		String buggyFilePath = "../Data/BuggyFiles/";
		readPatches("../Data/BugPositions.txt", buggyFilePath);

		String outputPath = suspiciousPath;
		FileHelper.deleteDirectory(outputPath);
		
//		for (String metricStr : Configuration.METRICS_0_1_1) {
		for (String metricStr : Configuration.METRICS_1_6_0) {
			builder.append(metricStr).append(",");
			methodBuilder.append(metricStr).append(",");
			fileBuilder.append(metricStr).append(",");
		}
		builder.append("\n");
		methodBuilder.append("\n");
		fileBuilder.append("\n");
		
		for (int i = 1; i <= 26; i ++) {//Chart
			String projectName = "Chart_" + i;
			builder.append(projectName).append(", ");
			methodBuilder.append(projectName).append(", ");
			fileBuilder.append(projectName).append(", ");
			locateSuspiciousCode(projectName);
			FileHelper.outputToFile(outputPath + "/LineGranularity.csv", builder, true);
			builder.setLength(0);
			FileHelper.outputToFile(outputPath + "/MethodGranularity.csv", methodBuilder, true);
			methodBuilder.setLength(0);
			FileHelper.outputToFile(outputPath + "/FileGranularity.csv", fileBuilder, true);
			fileBuilder.setLength(0);
		}
		for (int i = 1; i <= 65; i ++) {//Lang
			String projectName = "Lang_" + i;
			builder.append(projectName).append(", ");
			methodBuilder.append(projectName).append(", ");
			fileBuilder.append(projectName).append(", ");
			locateSuspiciousCode(projectName);
			FileHelper.outputToFile(outputPath + "/LineGranularity.csv", builder, true);
			builder.setLength(0);
			FileHelper.outputToFile(outputPath + "/MethodGranularity.csv", methodBuilder, true);
			methodBuilder.setLength(0);
			FileHelper.outputToFile(outputPath + "/FileGranularity.csv", fileBuilder, true);
			fileBuilder.setLength(0);
		}
		for (int i = 1; i <= 106; i ++) {//Math
			String projectName = "Math_" + i;
			builder.append(projectName).append(", ");
			methodBuilder.append(projectName).append(", ");
			fileBuilder.append(projectName).append(", ");
			locateSuspiciousCode(projectName);
			FileHelper.outputToFile(outputPath + "/LineGranularity.csv", builder, true);
			builder.setLength(0);
			FileHelper.outputToFile(outputPath + "/MethodGranularity.csv", methodBuilder, true);
			methodBuilder.setLength(0);
			FileHelper.outputToFile(outputPath + "/FileGranularity.csv", fileBuilder, true);
			fileBuilder.setLength(0);
		}
		for (int i = 1; i <= 133; i ++) {//Closure
			String projectName = "Closure_" + i;
			builder.append(projectName).append(", ");
			methodBuilder.append(projectName).append(", ");
			fileBuilder.append(projectName).append(", ");
			locateSuspiciousCode(projectName);
			FileHelper.outputToFile(outputPath + "/LineGranularity.csv", builder, true);
			builder.setLength(0);
			FileHelper.outputToFile(outputPath + "/MethodGranularity.csv", methodBuilder, true);
			methodBuilder.setLength(0);
			FileHelper.outputToFile(outputPath + "/FileGranularity.csv", fileBuilder, true);
			fileBuilder.setLength(0);
		}
		for (int i = 1; i <= 38; i ++) {//Mockito_1,3,18,19,20
			String projectName = "Mockito_" + i;
			builder.append(projectName).append(", ");
			methodBuilder.append(projectName).append(", ");
			fileBuilder.append(projectName).append(", ");
			locateSuspiciousCode(projectName);
			FileHelper.outputToFile(outputPath + "/LineGranularity.csv", builder, true);
			builder.setLength(0);
			FileHelper.outputToFile(outputPath + "/MethodGranularity.csv", methodBuilder, true);
			methodBuilder.setLength(0);
			FileHelper.outputToFile(outputPath + "/FileGranularity.csv", fileBuilder, true);
			fileBuilder.setLength(0);
		}
		for (int i = 1; i <= 27; i ++) {//Time
			String projectName = "Time_" + i;
			builder.append(projectName).append(", ");
			methodBuilder.append(projectName).append(", ");
			fileBuilder.append(projectName).append(", ");
			locateSuspiciousCode(projectName);
			FileHelper.outputToFile(outputPath + "/LineGranularity.csv", builder, true);
			builder.setLength(0);
			FileHelper.outputToFile(outputPath + "/MethodGranularity.csv", methodBuilder, true);
			methodBuilder.setLength(0);
			FileHelper.outputToFile(outputPath + "/FileGranularity.csv", fileBuilder, true);
			fileBuilder.setLength(0);
		}
	}

	private static void readPatches(String positionsFile, String buggyFilePath) {
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

	public static void locateSuspiciousCode(String buggyProject) {
		System.out.println(buggyProject);
		
		File dataPathFile = new File(suspiciousPath + buggyProject + "/");
		if (!dataPathFile.exists()) {
//			for (int i = 0, length = Configuration.METRICS_0_1_1.length; i < length; i ++) {
//				builder.append("-,");
//				methodBuilder.append("-,");
//				fileBuilder.append("-,");
//			}
//			builder.append("\n");
//			methodBuilder.append("\n");
//			fileBuilder.append("\n");
//			System.err.println("null suspicious code positions.");
			return;
		}
		
		for (String metricStr : Configuration.METRICS_0_1_1) {
//			System.out.println(metricStr);
			File dataFile = new File(suspiciousPath + buggyProject + "/" + metricStr + ".txt");
			if (!dataFile.exists()) {
				builder.append("-,");
				methodBuilder.append("-,");
				fileBuilder.append("-,");
//				System.err.println("null suspicious code positions. ===== " + buggyProject + " === " + metricStr);
				continue;
			}
			String dataContent = FileHelper.readFile(dataFile).trim();
			if (dataContent.isEmpty()) {
				builder.append("-,");
				methodBuilder.append("-,");
				fileBuilder.append("-,");
				continue;
			}
			List<String> suspCodeList = Arrays.asList(dataContent.split("\n"));
			List<PatchDiff> pds = patchMap.get(buggyProject);
			StringBuilder b = new StringBuilder();
			String detectedStr = "";
			String detectedMethodStr = "";
			String detectedFileStr = "";
			boolean detected = false;
			boolean detectedM = false;
			boolean detectedF = false;
			for (int index = 0, size = suspCodeList.size(); index < size; index ++) {
				String candidate = suspCodeList.get(index);
				int atIndex = candidate.indexOf("@");
				String className = candidate.substring(0, atIndex);
				int lineNumber = Integer.valueOf(candidate.substring(atIndex + 1, candidate.lastIndexOf("@")));
				b.append(className).append("@").append(lineNumber).append("\n");
				
				if (!detected)  {
					for (PatchDiff pd : pds) {
						String bugFileName = pd.fileName.replace("/", ".");
						bugFileName = bugFileName.substring(0, bugFileName.length() - 5);
						List<Integer> bugLines = pd.buggyLines;
						if (bugFileName.endsWith(className)) {
							// File level
							if (!detectedF) {
								detectedF = true;
								detectedFileStr = (index + 1) + ",";
							}
							
							// method level
							if (!detectedM && !pd.methodRanges.isEmpty()) {
								List<Pair<Integer, Integer>> methodRanges = pd.methodRanges;
								for (Pair<Integer, Integer> methodRange : methodRanges) {
									if (methodRange.getFirst() <= lineNumber && lineNumber <= methodRange.getSecond()) {
										detectedM = true;
										detectedMethodStr = (index + 1) + ",";
									}
								}
							}
							
							// line level
//							if (bugLines.isEmpty()) {// Pure inserting.
//								int bugHunkStartLine = pd.buggyHunkStartLine;
//								int bugHunkRange = pd.buggyHunkRange;
//								if (bugHunkStartLine <= lineNumber && lineNumber <= bugHunkStartLine + bugHunkRange - 1) {
//									// Coarsely Identified
//									if (detectedStr.isEmpty()) {
//										detectedStr = "(" + (index + 1) + "),";
//									}
//								}
//							} else {
								if (bugLines.contains(lineNumber)) {
									detected = true;
									builder.append(index + 1).append(",");
									break;
								}
//							}
						}
					}
					if (detected) break;
				}
			}
			if (!detected) {
				if (detectedStr.isEmpty()) builder.append("-,");
				else builder.append(detectedStr);
			}
			if (detectedM) methodBuilder.append(detectedMethodStr);
			else methodBuilder.append("-,");
			if (detectedF) fileBuilder.append(detectedFileStr);
			else fileBuilder.append("-,");
			
		}
		builder.append("\n");
		methodBuilder.append("\n");
		fileBuilder.append("\n");
	}
}
