package edu.lu.uni.serval.main;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.lu.uni.serval.AST.ASTGenerator;
import edu.lu.uni.serval.AST.ASTGenerator.TokenType;
import edu.lu.uni.serval.config.Configuration;
import edu.lu.uni.serval.dataprepare.DataPreparer;
import edu.lu.uni.serval.entity.Pair;
import edu.lu.uni.serval.faultlocalization.GZoltarFaultLoclaization;
import edu.lu.uni.serval.faultlocalization.Metrics;
import edu.lu.uni.serval.faultlocalization.SuspiciousCode;
import edu.lu.uni.serval.faultlocalization.Metrics.Metric;
import edu.lu.uni.serval.jdt.tree.ITree;
import edu.lu.uni.serval.parser.JavaFileParser;
import edu.lu.uni.serval.utils.Checker;
import edu.lu.uni.serval.utils.FileHelper;
import edu.lu.uni.serval.utils.MapSorter;
import edu.lu.uni.serval.utils.PathUtils;

public class Test {

	private static Logger log = LoggerFactory.getLogger(Test.class);
	
	static Map<String, List<PatchDiff>> patchMap = new HashMap<>();
	static Map<String, Map<String, List<PatchDiff>>> patchMap2 = new HashMap<>();
	static StringBuilder builder = new StringBuilder();
	static StringBuilder methodBuilder = new StringBuilder();
	static StringBuilder fileBuilder = new StringBuilder();
	
	public static void main(String[] args) throws IOException {
		
		for (String metricStr : Configuration.METRICS_0_1_1) {
			builder.append(metricStr).append(", ");
			methodBuilder.append(metricStr).append(", ");
			fileBuilder.append(metricStr).append(", ");
		}
		builder.append("\n");
		methodBuilder.append("\n");
		fileBuilder.append("\n");
		
		System.out.println("Test");
		// Match suspicious position with known bug fixes.
//		parshPatches("../defects4j/", patchMap, patchMap2);
		parshPatches("../../Public/git/defects4j/", patchMap, patchMap2);
		
		String outputPath = "bugPositions/";
//		FileHelper.deleteDirectory(outputPath);
//		String path = "../../Defects4JDataBackup/";//
		String path = "../../Public/Defects4JDataBackup/";//
//		String path = "/work/users/kliu/Defects4JData/";//
//		FileHelper.deleteFile(outputPath + "/bugs.csv");
		
//		String projectName_1 = args[0];//"Chart_8";
//		builder.append(projectName_1).append(", ");
//		Test test_1 = new Test();
//		test_1.locateSuspiciousCode(path, projectName_1, outputPath);
//		FileHelper.outputToFile(outputPath + "/bugs.csv", builder, true);
//		builder.setLength(0);
		
		for (int i = 1; i <= 26; i ++) {//Chart
			String projectName = "Chart_" + i;
			builder.append(projectName).append(", ");
			methodBuilder.append(projectName).append(", ");
			fileBuilder.append(projectName).append(", ");
			Test test = new Test();
			test.locateSuspiciousCode(path, projectName, outputPath);
			FileHelper.outputToFile(outputPath + "/bugs.csv", builder, true);
			builder.setLength(0);
			FileHelper.outputToFile(outputPath + "/bugsM.csv", methodBuilder, true);
			methodBuilder.setLength(0);
			FileHelper.outputToFile(outputPath + "/bugsF.csv", fileBuilder, true);
			fileBuilder.setLength(0);
		}
		for (int i = 1; i <= 65; i ++) {//Lang
			String projectName = "Lang_" + i;
			builder.append(projectName).append(", ");
			methodBuilder.append(projectName).append(", ");
			fileBuilder.append(projectName).append(", ");
			Test test = new Test();
			test.locateSuspiciousCode(path, projectName, outputPath);
			FileHelper.outputToFile(outputPath + "/bugs.csv", builder, true);
			builder.setLength(0);
			FileHelper.outputToFile(outputPath + "/bugsM.csv", methodBuilder, true);
			methodBuilder.setLength(0);
			FileHelper.outputToFile(outputPath + "/bugsF.csv", fileBuilder, true);
			fileBuilder.setLength(0);
		}
		for (int i = 1; i <= 106; i ++) {//Math
			String projectName = "Math_" + i;
			builder.append(projectName).append(", ");
			methodBuilder.append(projectName).append(", ");
			fileBuilder.append(projectName).append(", ");
			Test test = new Test();
			test.locateSuspiciousCode(path, projectName, outputPath);
			FileHelper.outputToFile(outputPath + "/bugs.csv", builder, true);
			builder.setLength(0);
			FileHelper.outputToFile(outputPath + "/bugsM.csv", methodBuilder, true);
			methodBuilder.setLength(0);
			FileHelper.outputToFile(outputPath + "/bugsF.csv", fileBuilder, true);
			fileBuilder.setLength(0);
		}
		for (int i = 1; i <= 133; i ++) {//Closure
			String projectName = "Closure_" + i;
			builder.append(projectName).append(", ");
			methodBuilder.append(projectName).append(", ");
			fileBuilder.append(projectName).append(", ");
			Test test = new Test();
			test.locateSuspiciousCode(path, projectName, outputPath);
			FileHelper.outputToFile(outputPath + "/bugs.csv", builder, true);
			builder.setLength(0);
			FileHelper.outputToFile(outputPath + "/bugsM.csv", methodBuilder, true);
			methodBuilder.setLength(0);
			FileHelper.outputToFile(outputPath + "/bugsF.csv", fileBuilder, true);
			fileBuilder.setLength(0);
		}
		for (int i = 1; i <= 38; i ++) {//Mockito_1,3,18,19,20
			String projectName = "Mockito_" + i;
			builder.append(projectName).append(", ");
			methodBuilder.append(projectName).append(", ");
			fileBuilder.append(projectName).append(", ");
			Test test = new Test();
			test.locateSuspiciousCode(path, projectName, outputPath);
			FileHelper.outputToFile(outputPath + "/bugs.csv", builder, true);
			builder.setLength(0);
			FileHelper.outputToFile(outputPath + "/bugsM.csv", methodBuilder, true);
			methodBuilder.setLength(0);
			FileHelper.outputToFile(outputPath + "/bugsF.csv", fileBuilder, true);
			fileBuilder.setLength(0);
		}
		for (int i = 1; i <= 27; i ++) {//Time
			String projectName = "Time_" + i;
			builder.append(projectName).append(", ");
			methodBuilder.append(projectName).append(", ");
			fileBuilder.append(projectName).append(", ");
			Test test = new Test();
			test.locateSuspiciousCode(path, projectName, outputPath);
			FileHelper.outputToFile(outputPath + "/bugs.csv", builder, true);
			builder.setLength(0);
			FileHelper.outputToFile(outputPath + "/bugsM.csv", methodBuilder, true);
			methodBuilder.setLength(0);
			FileHelper.outputToFile(outputPath + "/bugsF.csv", fileBuilder, true);
			fileBuilder.setLength(0);
		}
		
	}

	public void locateSuspiciousCode(String path, String buggyProject, String outputPath) throws IOException {
		System.out.println(buggyProject);
		
		DataPreparer dp = new DataPreparer(path);
		dp.prepareData(buggyProject);
		if (!dp.validPaths) return;

		GZoltarFaultLoclaization gzfl = new GZoltarFaultLoclaization();
		gzfl.threshold = 0.0;
		gzfl.maxSuspCandidates = -1;
		gzfl.srcPath = path + buggyProject + PathUtils.getSrcPath(buggyProject).get(2);
		try {
			gzfl.localizeSuspiciousCodeWithGZoltar(dp.classPaths, checkNotNull(Arrays.asList("")), dp.testCases);
		} catch (NullPointerException e) {
			for (String metricStr : Configuration.METRICS_0_1_1) {
				FileHelper.outputToFile(outputPath + buggyProject + "/" + metricStr + ".txt", "", false);
				builder.append("-,");
				methodBuilder.append("-,");
				fileBuilder.append("-,");
			}
			builder.append("\n");
			methodBuilder.append("\n");
			fileBuilder.append("\n");
			log.error(buggyProject + "\n" + e.getMessage());
			e.printStackTrace();
			return;
		}
		
		for (String metricStr : Configuration.METRICS_0_1_1) {
			Metric metric = new Metrics().generateMetric(metricStr);
			System.out.println(metricStr);
			
			gzfl.sortSuspiciousCode(metric);
			List<SuspiciousCode> candidates = new ArrayList<SuspiciousCode>(gzfl.candidates.size());
			candidates.addAll(gzfl.candidates);
			
			List<PatchDiff> pds = patchMap.get(buggyProject);
			
			if (candidates.size() == 0) {
				builder.append("-,");
				continue;
			}
			
			StringBuilder b = new StringBuilder();
			String detectedStr = "";
			String detectedMethodStr = "";
			String detectedFileStr = "";
			boolean detected = false;
			boolean detectedM = false;
			boolean detectedF = false;
			for (int index = 0, size = candidates.size(); index < size; index ++) {
				SuspiciousCode candidate = candidates.get(index);
				String className = candidate.getClassName();
				int lineNumber = candidate.lineNumber;
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
							if (bugLines.isEmpty()) {// Pure inserting.
								int bugHunkStartLine = pd.buggyHunkStartLine;
								int bugHunkRange = pd.buggyHunkRange;
								if (bugHunkStartLine <= lineNumber && lineNumber <= bugHunkStartLine + bugHunkRange - 1) {
									// Coarsely Identified
									if (detectedStr.isEmpty()) {
										detectedStr = "(" + (index + 1) + "),";
									}
								}
							} else {
								if (bugLines.contains(lineNumber)) {
									detected = true;
									builder.append(index + 1).append(",");
									break;
								}
							}
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
			
			gzfl.candidates = new ArrayList<SuspiciousCode>();
			FileHelper.outputToFile(Configuration.SUSPICIOUS_POSITIONS_FILE_APTH + buggyProject + "/" + metricStr + ".txt", b, false);
		}
		builder.append("\n");
	}
	
	public static void parshPatches(String defects4jPath, Map<String, List<PatchDiff>> patchMap, Map<String, Map<String, List<PatchDiff>>> patchMap2) throws NumberFormatException, IOException {
//		String buggyProjectsPath = "/Users/kui.liu/Public/Defects4JDataBackup/";
		String buggyFilesPath = "buggyFiles/";
		String path = defects4jPath + "framework/projects/";
		String[] projects = {"Chart", "Closure", "Lang", "Math", "Mockito", "Time"};
		String patches = "/patches/";
		// BugId
		int diffs = 0;
		for (String project : projects) {
			String inputPath = path + project + patches;
			File[] patchFiles = new File(inputPath).listFiles();
			for (File patchFile : patchFiles) {
				String patchFileName = patchFile.getName();
				if (patchFileName.endsWith("src.patch")) {
					bugId = project + "_" + patchFileName.substring(0, patchFileName.indexOf("."));
//					if (!bugId.endsWith("Lang_23")) {
//						System.out.println();
//						continue;
						// Lang-23, Mokito-17,23
//					}
					Map<String, List<PatchDiff>> buggyFiles = new HashMap<>();
					List<PatchDiff> patchDiffs = new ArrayList<>();
					String content = FileHelper.readFile(patchFile);
					BufferedReader reader = new BufferedReader(new StringReader(content));
					String line = null;
					String fileName = null;
					boolean isPatchCode = false;

					int fixedHunkStartLine = 0;
					int fixedHunkRange = 0;
					int buggyHunkStartLine = 0;
					int buggyHunkRange = 0;
					int addedLines = 0;
					int deletedLines = 0;
					int fixedLine = 0;
					int buggyLine = 0;
					StringBuilder hunk = new StringBuilder();
					List<Integer> fixedLines = new ArrayList<>();
					List<Integer> buggyLines = new ArrayList<>();
					
					while ((line = reader.readLine()) != null) {
						if (line.startsWith("diff") || line.startsWith("index")) {
							isPatchCode = false;
						} else if (line.startsWith("---")) {
							if (hunk.length() > 0) {
								PatchDiff pd = new PatchDiff(buggyHunkStartLine, buggyHunkRange, fixedHunkStartLine, fixedHunkRange,
										deletedLines, addedLines, hunk.toString(), buggyLines, fixedLines);
								pd.setFileName(fileName);
								identifyBuggyMethodPosition(buggyFilesPath + bugId + "/", pd);
								if (buggyFiles.containsKey(fileName)) {
									buggyFiles.get(fileName).add(pd);
								} else {
									List<PatchDiff> pds = new ArrayList<>();
									pds.add(pd);
									buggyFiles.put(fileName, pds);
								}
								patchDiffs.add(pd);
								hunk.setLength(0);
								fixedHunkStartLine = 0;
								fixedHunkRange = 0;
								buggyHunkStartLine = 0;
								buggyHunkRange = 0;
								addedLines = 0;
								deletedLines = 0;
								fixedLine = 0;
								buggyLine = 0;
								fixedLines = new ArrayList<>();
								buggyLines = new ArrayList<>();
								diffs ++;
							}
							
							fileName = line.substring(4);
							if (fileName.startsWith("a/")) {
								fileName = fileName.substring(2);
							}
							fileName = fileName.substring(0, fileName.indexOf(".java") + 5);
//							File buggyFile = new File(buggyFilesPath + bugId + "/" + fileName);
//							buggyFile.getParentFile().mkdirs();
//							buggyFile.createNewFile();
//							Files.copy(new File(buggyProjectsPath + bugId + "/" + fileName), buggyFile);
						} else if (line.startsWith("@@ ")) {
							if (hunk.length() > 0) {
								PatchDiff pd = new PatchDiff(buggyHunkStartLine, buggyHunkRange, fixedHunkStartLine, fixedHunkRange,
										deletedLines, addedLines, hunk.toString(), buggyLines, fixedLines);
								pd.setFileName(fileName);
								identifyBuggyMethodPosition(buggyFilesPath + bugId + "/", pd);
								if (buggyFiles.containsKey(fileName)) {
									buggyFiles.get(fileName).add(pd);
								} else {
									List<PatchDiff> pds = new ArrayList<>();
									pds.add(pd);
									buggyFiles.put(fileName, pds);
								}
								patchDiffs.add(pd);
								hunk.setLength(0);
								fixedHunkStartLine = 0;
								fixedHunkRange = 0;
								buggyHunkStartLine = 0;
								buggyHunkRange = 0;
								addedLines = 0;
								deletedLines = 0;
								fixedLine = 0;
								buggyLine = 0;
								fixedLines = new ArrayList<>();
								buggyLines = new ArrayList<>();
								
								diffs ++;
								patchMap.put(bugId, patchDiffs);
								patchMap2.put(bugId, buggyFiles);
							}
							isPatchCode = true;
							int plusIndex = line.indexOf("+");
							String lineNum = line.substring(4, plusIndex);
							String[] nums = lineNum.split(",");
							fixedHunkStartLine = Integer.parseInt(nums[0].trim());
							fixedLine = fixedHunkStartLine - 1;
							if (nums.length == 2) {
								fixedHunkRange = Integer.parseInt(nums[1].trim());
							}
							String lineNum2 = line.substring(plusIndex) .trim();
							lineNum2 = lineNum2.substring(1, lineNum2.indexOf("@@") - 1);
							String[] nums2 = lineNum2.split(",");
							buggyHunkStartLine = Integer.parseInt(nums2[0].trim());
							buggyLine = buggyHunkStartLine - 1;
							if (nums2.length == 2) {
								buggyHunkRange = Integer.parseInt(nums2[1].trim());
							}
							if (!line.startsWith("@@ -" + fixedHunkStartLine + "," + fixedHunkRange + " +" +
									buggyHunkStartLine + "," + buggyHunkRange + " @@")) {
								System.out.println(line);		
							}
						} else if (isPatchCode) {
							if (line.startsWith("-")) {
								addedLines ++;
								fixedLine ++;
								fixedLines.add(fixedLine);
							} else if (line.startsWith("+")) {
								deletedLines ++;
								buggyLine ++;
								buggyLines.add(buggyLine);
							} else {
								fixedLine ++;
								buggyLine ++;
							}
							hunk.append(line + "\n");
						}
					}
					if (hunk.length() > 0) {
						PatchDiff pd = new PatchDiff(buggyHunkStartLine, buggyHunkRange, fixedHunkStartLine, fixedHunkRange,
								deletedLines, addedLines, hunk.toString(), buggyLines, fixedLines);
						pd.setFileName(fileName);
						identifyBuggyMethodPosition(buggyFilesPath + bugId + "/", pd);
						if (buggyFiles.containsKey(fileName)) {
							buggyFiles.get(fileName).add(pd);
						} else {
							List<PatchDiff> pds = new ArrayList<>();
							pds.add(pd);
							buggyFiles.put(fileName, pds);
						}
						patchDiffs.add(pd);
						diffs ++;
//						log.info(bugId + "==" + buggyFiles.size());
						patchMap.put(bugId, patchDiffs);
						patchMap2.put(bugId, buggyFiles);
					}
					
//					for (Map.Entry<String, List<PatchDiff>> entry : buggyFiles.entrySet()) {
//						
//					}
				}
			}
		}
		
		System.out.println("Bugs: " + patchMap.size());
		System.out.println("Diffs: " + diffs);
		Map<Integer, Integer> map = new HashMap<>();
		// bug --> diffs
		// 1=232, 2=97, 3=39, 4=13, 5=3, 6=4, 7=2, 9=1, 10=2, 12=1, 20=1.
		for (Map.Entry<String, List<PatchDiff>> entry : patchMap.entrySet()) {
			int size = entry.getValue().size();
			Integer num = map.get(size);
			if (num == null) {
				map.put(size, 1);
			} else {
				map.put(size, num + 1);
			}
		}
		MapSorter<Integer, Integer> mapSorter = new MapSorter<>();
		map = mapSorter.sortByKeyAscending(map);
		System.out.println("Bug --> Diffs");
		System.out.println(map);
		
		Map<Integer, Integer> map2 = new HashMap<>();
		Map<String, Integer> map3 = new HashMap<>();
		
		// bug --> files
		// 1=365, 2=28, 5=1, 7=1.
		Map<Integer, Integer> bugSizes = new HashMap<>();
		Map<Integer, Integer> fixSizes = new HashMap<>();
		Map<String, Integer> bug_fixSizes = new HashMap<>();
		for (Map.Entry<String, Map<String, List<PatchDiff>>> entry : patchMap2.entrySet()) {
			Map<String, List<PatchDiff>> buggyFiles = entry.getValue();
			int size = buggyFiles.size();
			Integer num = map2.get(size);
			if (num == null) {
				map2.put(size, 1);
			} else {
				map2.put(size, num + 1);
			}
			for (Map.Entry<String, List<PatchDiff>> entry2 : buggyFiles.entrySet()) {
				List<PatchDiff> pds = entry2.getValue();
				int size2 = pds.size();
				String key = size + "_" + size2;
				Integer num2 = map3.get(key);
				if (num2 == null) {
					map3.put(key, 1);
				} else {
					map3.put(key, num2 + 1);
				}
				
				for (PatchDiff pd : pds) {
					List<Integer> buggyLines = pd.buggyLines;
					List<Integer> fixedLines = pd.fixedLines;
					int a = buggyLines.size();
					int b = fixedLines.size();
					String c = a + "_" + b + "";
					if (bugSizes.containsKey(a)) {
						bugSizes.put(a, bugSizes.get(a) + 1);
					} else {
						bugSizes.put(a, 1);
					}
					if (fixSizes.containsKey(b)) {
						fixSizes.put(b, fixSizes.get(b) + 1);
					} else {
						fixSizes.put(b, 1);
					}
					if (bug_fixSizes.containsKey(c)) {
						bug_fixSizes.put(c, bug_fixSizes.get(c) + 1);
					} else {
						bug_fixSizes.put(c, 1);
					}
				}
			}
		}
		map2 = mapSorter.sortByKeyAscending(map2);
		System.out.println("Bug --> Buggy Files");
		System.out.println(map2);
		MapSorter<String, Integer> mapSorter2 = new MapSorter<>();
		map3 = mapSorter2.sortByKeyAscending(map3);
		System.out.println("Files_Diffs:");
		System.out.println(map3);
		bugSizes = mapSorter.sortByKeyAscending(bugSizes);
		System.out.println("Deleted lines:");
		System.out.println(bugSizes);
		fixSizes = mapSorter.sortByKeyAscending(fixSizes);
		System.out.println("Added lines:");
		System.out.println(fixSizes);
		bug_fixSizes = mapSorter2.sortByKeyAscending(bug_fixSizes);
		System.out.println("Deleletd_Added lines:");
		System.out.println(bug_fixSizes);
	}

	private static String bugId = "";
	private static void identifyBuggyMethodPosition(String filePath, PatchDiff pd) {
		File buggyFile = new File(filePath + pd.fileName);
		int buggyHunkStartLine = pd.buggyHunkStartLine;
		int buggyHunkEndLine = buggyHunkStartLine + pd.buggyHunkRange;
		CompilationUnit unit = new JavaFileParser().new MyUnit().createCompilationUnit(buggyFile);
		
		ITree rootTree = new ASTGenerator().generateTreeForJavaFile(buggyFile, TokenType.EXP_JDT);
		List<Pair<Integer, Integer>> methodRanges = identifyMethod(rootTree, buggyHunkStartLine, buggyHunkEndLine, unit, pd);
		if (methodRanges.isEmpty()) {
			System.err.println(bugId + " --- null");
			System.out.println(pd.fileName + " --- " + pd.buggyHunkStartLine);
		}
		pd.methodRanges = methodRanges;
	}

	private static List<Pair<Integer, Integer>> identifyMethod(ITree tree, int buggyHunkStartLine, int buggyHunkEndLine, CompilationUnit unit, PatchDiff pd) {
		List<Pair<Integer, Integer>> methodRanges = new ArrayList<>();
		
		List<ITree> children = tree.getChildren();
		for (ITree child : children) {
			int astNodeType = child.getType();
			
			if (Checker.isMethodDeclaration(astNodeType)) {
				int startPosition = child.getPos();
				int endPosition = startPosition + child.getLength();
				int startLine = unit.getLineNumber(startPosition);
				int endLine = unit.getLineNumber(endPosition);
				
				if (startLine > buggyHunkEndLine) break;
				if (endLine < buggyHunkStartLine) continue;
				
				if (startLine <= buggyHunkStartLine && buggyHunkEndLine <= endLine) {
					Pair<Integer, Integer> methodRange = new Pair<Integer, Integer>(startLine, endLine);
					methodRanges.add(methodRange);
				} else {
					if ((buggyHunkStartLine <= endLine && buggyHunkEndLine > endLine)
							|| (buggyHunkStartLine < startLine && startLine <= buggyHunkEndLine)
							|| (buggyHunkStartLine < startLine && endLine < buggyHunkEndLine)) {
						List<Integer> buggyLines = pd.buggyLines;
						if (!buggyLines.isEmpty()) {
							for (int buggyLine : buggyLines) {
								if (startLine <= buggyLine && buggyLine <= endLine) {
									Pair<Integer, Integer> methodRange = new Pair<Integer, Integer>(startLine, endLine);
									methodRanges.add(methodRange);
									break;
								}
							}
							if (buggyLines.get(buggyLines.size() - 1) < startLine && !pd.fixedLines.isEmpty()) {
								List<Integer> fixedLines = pd.fixedLines;
								int lastLine = fixedLines.get(fixedLines.size() - 1);
								lastLine = lastLine - pd.fixedHunkStartLine + pd.buggyHunkStartLine - fixedLines.size() + buggyLines.size();
								if (lastLine < startLine) break;
								for (int fixedLine : fixedLines) {
									int buggyLine = fixedLine - pd.fixedHunkStartLine + pd.buggyHunkStartLine;
									if (startLine <= buggyLine && buggyLine <= endLine) {
										Pair<Integer, Integer> methodRange = new Pair<Integer, Integer>(startLine, endLine);
										methodRanges.add(methodRange);
										break;
									}
								}
							}
						} else {
							List<Integer> fixedLines = pd.fixedLines;
							int lastLine = fixedLines.get(fixedLines.size() - 1);
							lastLine = lastLine - pd.fixedHunkStartLine + pd.buggyHunkStartLine - fixedLines.size();
							if (lastLine < startLine) break;
							for (int fixedLine : fixedLines) {
								int buggyLine = fixedLine - pd.fixedHunkStartLine + pd.buggyHunkStartLine;
								if (startLine <= buggyLine && buggyLine <= endLine) {
									Pair<Integer, Integer> methodRange = new Pair<Integer, Integer>(startLine, endLine);
									methodRanges.add(methodRange);
									break;
								}
							}
						}
						
					}
				}
			} else {
				methodRanges.addAll(identifyMethod(child, buggyHunkStartLine, buggyHunkEndLine, unit, pd));
			}
 		}
		return methodRanges;
	}

}


