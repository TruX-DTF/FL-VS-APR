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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.lu.uni.serval.dataprepare.DataPreparer;
import edu.lu.uni.serval.faultlocalization.GZoltarFaultLoclaization;
import edu.lu.uni.serval.faultlocalization.SuspiciousCode;
import edu.lu.uni.serval.faultlocalization.Metrics.Metric;
import edu.lu.uni.serval.utils.FileHelper;
import edu.lu.uni.serval.utils.MapSorter;
import edu.lu.uni.serval.utils.PathUtils;

public class TestFL {

	private static Logger log = LoggerFactory.getLogger(TestFL.class);
	
	List<SuspiciousCode> candidates = null;

	public static void main(String[] args) throws NumberFormatException, IOException {
		String path = "/Users/kui.liu/Public/Defects4J/";
//		String path = "/work/users/kliu/Defects4JData/";
		String projectName1 = "Chart_3";
		TestFL test1 = new TestFL();
		test1.testFL(path, projectName1);
		

//		Map<String, List<PatchDiff>> patchMap = new HashMap<>();
//		Map<String, Map<String, List<PatchDiff>>> patchMap2 = new HashMap<>();
////		parshPatches("../defects4j/", patchMap, patchMap2);
//		parshPatches("../../defects4j/", patchMap, patchMap2);
//		
//		/**
//		 * How many bugs can be detected?
//		 * How many files can be detected?
//		 * How many buggy lines can be detected?
//		 * Rankings of detected bugs?
//		 */
//		// BugId, FileName, Ranking.
//		Map<String, Map<String, List<Integer>>> buggyCodeRankingMaps = new HashMap<>(); // localized bug lines.
//		Map<String, Map<String, List<Integer>>> buggyCodeRankingMaps2 = new HashMap<>();// localized bug hunks.
//		Map<String, Map<String, List<Integer>>> buggyCodeRankingMaps3 = new HashMap<>();// localized with Fixed lines.
//		// BugId, FileName, BugLines. 
//		Map<String, Map<String, List<Integer>>> buggyCodeLinesMaps = new HashMap<>();
//		Map<String, Map<String, List<Integer>>> buggyCodeLinesMaps2 = new HashMap<>();
//		Map<String, Map<String, List<Integer>>> buggyCodeLinesMaps3 = new HashMap<>();
//		
//		List<String> bugIdsIdentifiedByBugLines = new ArrayList<>();
//		List<String> bugIds2 = new ArrayList<>();
//		
//		File[] projects = new File(path).listFiles();
//		for (File project : projects) {
//			if (project.isDirectory()) {
//				String projectName = project.getName();
////				if (!projectName.startsWith("Mockito"))
////					continue;
//				if (projectName.equals("Mockito_20") || projectName.equals("Mockito_19") || projectName.equals("Mockito_18")
//						 || projectName.equals("Mockito_1") || projectName.equals("Mockito_3"))
//					continue;
////				if (!projectName.equals("Chart_1")) continue;
//				TestFL test = new TestFL();
//				test.testFL(path, projectName);
//				List<SuspiciousCode> candidates = test.candidates;
//				
////				Map<String, List<PatchDiff>> filePDs = patchMap2.get(projectName);
//				List<PatchDiff> pds = patchMap.get(projectName);
//				Map<String, List<Integer>> suspCodeRankings = new HashMap<>();
//				Map<String, List<Integer>> suspCodeLines = new HashMap<>();
//				Map<String, List<Integer>> suspCodeRankings2 = new HashMap<>();
//				Map<String, List<Integer>> suspCodeLines2 = new HashMap<>();
//				Map<String, List<Integer>> suspCodeRankings3 = new HashMap<>();
//				Map<String, List<Integer>> suspCodeLines3 = new HashMap<>();
//				for (int index = 0, size = candidates.size(); index < size; index ++) {
//					SuspiciousCode candidate = candidates.get(index);
//					String className = candidate.getClassName();
//					int lineNumber = candidate.lineNumber;
//					int ranking = 0;
//					for (PatchDiff pd : pds) {
//						String bugFileName = pd.fileName.replace("/", ".");
//						bugFileName = bugFileName.substring(0, bugFileName.length() - 5);
//						List<Integer> bugLines = pd.buggyLines;
//						if (bugFileName.endsWith(className)) {
//							if (bugLines.size() == 0) {// Pure inserting.
//								int bugHunkStartLine = pd.buggyHunkStartLine;
//								int bugHunkRange = pd.buggyHunkRange;
//								if (bugHunkStartLine <= lineNumber && lineNumber <= bugHunkStartLine + bugHunkRange - 1) {
//									// Coarsely Identified
//									ranking = getRanking(index, candidates);
//									addToMap(suspCodeLines2, className, lineNumber);
//									addToMap(suspCodeRankings2, className, ranking);
//									
//									// little fine identified. -2, fixedLine, +2
//									int bugLine = pd.buggyHunkStartLine + pd.fixedLines.get(0) - pd.fixedHunkStartLine;
//									int bugStartLine = bugLine - 2;
//									int bugEndLine = bugLine + 2;
//									if (bugStartLine <= lineNumber && lineNumber <= bugEndLine) {
//										addToMap(suspCodeLines3, className, lineNumber);
//										addToMap(suspCodeRankings3, className, ranking);
//									}
//									if (!bugIds2.contains(projectName)) bugIds2.add(projectName);
//								}
//							} else {
//								if (bugLines.contains(lineNumber)) {
//									ranking = getRanking(index, candidates);
//									addToMap(suspCodeLines, className, lineNumber);
//									addToMap(suspCodeRankings, className, ranking);
//									if (!bugIdsIdentifiedByBugLines.contains(projectName)) bugIdsIdentifiedByBugLines.add(projectName);
//								}
//							}
////							break;
//						}
//					}
//				}
//				if (suspCodeRankings.size() > 0) buggyCodeRankingMaps.put(projectName, suspCodeRankings);
//				if (suspCodeLines.size() > 0) buggyCodeLinesMaps.put(projectName, suspCodeLines);
//				if (suspCodeRankings2.size() > 0) buggyCodeRankingMaps2.put(projectName, suspCodeRankings2);
//				if (suspCodeLines2.size() > 0) buggyCodeLinesMaps2.put(projectName, suspCodeLines2);
//				if (suspCodeRankings3.size() > 0) buggyCodeRankingMaps3.put(projectName, suspCodeRankings3);
//				if (suspCodeLines3.size() > 0) buggyCodeLinesMaps3.put(projectName, suspCodeLines3);
//			}
//		}
//		log.info("Matched bugs by bug lines: " + buggyCodeRankingMaps.size());// localized bug lines.
//		log.info("Matched bugs by bug hunks: " + buggyCodeRankingMaps2.size());// localized bug hunks.
//		log.info("Matched bugs by fixed lines: " + buggyCodeRankingMaps3.size());// localized with Fixed lines.
//
//		log.info("Matched bugs by bug lines: " + buggyCodeRankingMaps);// localized bug lines.
//		log.info("Matched bugs by bug hunks: " + buggyCodeRankingMaps2);// localized bug hunks.
//		log.info("Matched bugs by fixed lines: " + buggyCodeRankingMaps3);// localized with Fixed lines.
//
//		log.info("Original Bugs1: " + patchMap);
//		log.info("Original Bugs2: " + patchMap2);
//		
//		log.info("Detected Rankings:");
//		for (Map.Entry<Integer, List<Integer>> entry : rankingMap.entrySet()) {
//			log.info(entry.getKey() + " : " + entry.getValue());
//		}
//		log.info("Identified buggy projects1: \n" + bugIdsIdentifiedByBugLines.toString());
//		log.info("Identified buggy projects2: \n" + bugIds2.toString());
	}
	
	private static Map<Integer, List<Integer>> rankingMap = new HashMap<>();
	

	@SuppressWarnings("unused")
	private static int getRanking(int index, List<SuspiciousCode> candidates) {
		int ranking;
		Map<Double, Integer> map = new HashMap<>();
		for (int i = 0; i < index; i ++) {
			SuspiciousCode candidate = candidates.get(i);
			Double susp = candidate.getSuspiciousValue();
			if (map.containsKey(susp)) {
				map.put(susp, map.get(susp) + 1);
			} else {
				map.put(susp, 1);
			}
		}
		SuspiciousCode candidate = candidates.get(index);
		Double susp = candidate.getSuspiciousValue();
		if (map.containsKey(susp)) {
			ranking = map.size();
		} else {
			ranking = map.size() + 1;
		}
		if (rankingMap.containsKey(ranking)) {
			rankingMap.get(ranking).add(index);
		} else {
			List<Integer> indexes = new ArrayList<>();
			indexes.add(index);
			rankingMap.put(ranking, indexes);
		}
		return ranking;
	}


	@SuppressWarnings("unused")
	private static void addToMap(Map<String, List<Integer>> suspCodeMap, String className, int num) {
		if (suspCodeMap.containsKey(className)) {
			suspCodeMap.get(className).add(num);
		} else {
			List<Integer> lines = new ArrayList<>();
			lines.add(num);
			suspCodeMap.put(className, lines);
		}
	}

	public static void parshPatches(String defects4jPath, Map<String, List<PatchDiff>> patchMap, Map<String, Map<String, List<PatchDiff>>> patchMap2) throws NumberFormatException, IOException {
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
					String bugId = project + "_" + patchFileName.substring(0, patchFileName.indexOf("."));
//					if (!bugId.endsWith("Closure_103")) {
//						System.out.println();
//						continue;
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
							log.debug(bugId + "==" + fileName);
						} else if (line.startsWith("@@ ")) {
							if (hunk.length() > 0) {
								PatchDiff pd = new PatchDiff(buggyHunkStartLine, buggyHunkRange, fixedHunkStartLine, fixedHunkRange,
										deletedLines, addedLines, hunk.toString(), buggyLines, fixedLines);
								pd.setFileName(fileName);
								if (buggyFiles.containsKey(fileName)) {
									buggyFiles.get(fileName).add(pd);
								} else {
									List<PatchDiff> pds = new ArrayList<>();
									pds.add(pd);
									buggyFiles.put(fileName, pds);
								}
								patchDiffs.add(pd);
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

	@SuppressWarnings("unused")
	private void testFL(String path, String project) throws IOException {
		if (!project.contains("_")) {
			System.out.println("Main: cannot recognize project name \"" + project + "\"");
			return;
		}

		String[] elements = project.split("_");
		String projectType = elements[0];
		int projectNumber;
		try {
			projectNumber = Integer.valueOf(elements[1]);
		} catch (NumberFormatException e) {
			System.out.println("Main: cannot recognize project name \"" + project + "\"");
			return;
		}

		System.out.println(project);
		
		DataPreparer dp = new DataPreparer(path);
		dp.prepareData(project);
		if (!dp.validPaths) return;

//		TimeLine timeLine = new TimeLine(1800);
		GZoltarFaultLoclaization gzfl = new GZoltarFaultLoclaization();
		gzfl.threshold = 0.01;
		gzfl.maxSuspCandidates = 1000;
		gzfl.srcPath = path + project + PathUtils.getSrcPath(project).get(2);
		gzfl.localizeSuspiciousCodeWithGZoltar(dp.classPaths, checkNotNull(Arrays.asList("")), dp.testCases);
		Metric metric = null;//new Ochiai();
		gzfl.sortSuspiciousCode(metric);
		candidates = gzfl.candidates;
//		if (timeLine.isTimeout()){
//            return;
//        }
//		File suspiciousFile = new File(Configuration.LOCALIZATION_RESULT_CACHE + FileUtils.getMD5(StringUtils.join(dp.testCases, "")
//				+ dp.classPath + dp.testClassPath + dp.srcPath + dp.testSrcPath + "null") + ".sps");
//		suspiciousFile.getParentFile().mkdirs();
//		suspiciousFile.createNewFile();
//		ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(suspiciousFile));
//        objectOutputStream.writeObject(candidates);
//        objectOutputStream.close();
	}

}
