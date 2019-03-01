package edu.lu.uni.serval.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.lu.uni.serval.utils.FileHelper;
import edu.lu.uni.serval.utils.ListSorter;

public class GZoltar_1_6_0 {
	
	public static void main(String[] args) {
//		if (args.length != 4) {
//			System.err.println("Parameter Format: <> <> <> <>");
//			System.exit(0);
//		}
		String crushMatrixOutputDataPath = "GZoltar-1.6.0/crushMatrixOutput/";
		File inputFile = new File(crushMatrixOutputDataPath);
		File[] files = inputFile.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				String algorithmName = file.getName();
				// Chart 26, Closure 133, Lang 65, Math 106, Mockito 38, Time 27.
				for (int i = 1; i <= 26; i ++) {
					String projectName = "Chart_" + i;
					String filePath = file.getPath() + "/" + projectName;
					String outputFile = "GZoltar-1.6.0/SuspiciousCodePositions/" + projectName + "/" + algorithmName + ".txt";
					if (new File(filePath).exists()) {
						new GZoltar_1_6_0().parseResults(filePath, outputFile);
//					} else {
//						System.out.println(algorithmName + " : " + projectName);
					}
				}
				for (int i = 1; i <= 133; i ++) {
					String projectName = "Closure_" + i;
					String filePath = file.getPath() + "/" + projectName;
					String outputFile = "GZoltar-1.6.0/SuspiciousCodePositions/" + projectName + "/" + algorithmName + ".txt";
					if (new File(filePath).exists()) {
						new GZoltar_1_6_0().parseResults(filePath, outputFile);
//					} else {
//						System.out.println(algorithmName + " : " + projectName);
					}
				}
				for (int i = 1; i <= 65; i ++) {
					String projectName = "Lang_" + i;
					String filePath = file.getPath() + "/" + projectName;
					String outputFile = "GZoltar-1.6.0/SuspiciousCodePositions/" + projectName + "/" + algorithmName + ".txt";
					if (new File(filePath).exists()) {
						new GZoltar_1_6_0().parseResults(filePath, outputFile);
//					} else {
//						System.out.println(algorithmName + " : " + projectName);
					}
				}
				for (int i = 1; i <= 106; i ++) {
					String projectName = "Math_" + i;
					String filePath = file.getPath() + "/" + projectName;
					String outputFile = "GZoltar-1.6.0/SuspiciousCodePositions/" + projectName + "/" + algorithmName + ".txt";
					if (new File(filePath).exists()) {
						new GZoltar_1_6_0().parseResults(filePath, outputFile);
//					} else {
//						System.out.println(algorithmName + " : " + projectName);
					}
				}
				for (int i = 1; i <= 38; i ++) {
					String projectName = "Mockito_" + i;
					String filePath = file.getPath() + "/" + projectName;
					String outputFile = "GZoltar-1.6.0/SuspiciousCodePositions/" + projectName + "/" + algorithmName + ".txt";
					if (new File(filePath).exists()) {
						new GZoltar_1_6_0().parseResults(filePath, outputFile);
//					} else {
//						System.out.println(algorithmName + " : " + projectName);
					}
				}
				for (int i = 1; i <= 27; i ++) {
					String projectName = "Time_" + i;
					String filePath = file.getPath() + "/" + projectName;
					String outputFile = "GZoltar-1.6.0/SuspiciousCodePositions/" + projectName + "/" + algorithmName + ".txt";
					if (new File(filePath).exists()) {
						new GZoltar_1_6_0().parseResults(filePath, outputFile);
//					} else {
//						System.out.println(algorithmName + " : " + projectName);
					}
				}
			}
		}
	}
	
	public void parseResults(String intputFile, String outputPath) {
		String[] results = FileHelper.readFile(intputFile).split("\n");
		
		List<SuspiciousStatement> suspStmts = new ArrayList<>();
		
		for (int index = 1, length = results.length; index < length; index ++) {
			String[] subResults = results[index].split(",");
			String[] statement = subResults[0].trim().split("#");
			String suspValue = subResults[1].trim();
			if ("0.0".equals(suspValue)) continue;
			
			SuspiciousStatement suspStmt = new SuspiciousStatement(statement[0], Integer.valueOf(statement[1]), Double.valueOf(suspValue));
			suspStmts.add(suspStmt);
		}
		
		ListSorter<SuspiciousStatement> sorter = new ListSorter<SuspiciousStatement>(suspStmts);
		suspStmts = sorter.sortDescending();
		StringBuilder builder = new StringBuilder();
		for (SuspiciousStatement suspStmt : suspStmts) {
			builder.append(suspStmt.toString()).append("\n");
		}
		FileHelper.outputToFile(outputPath, builder, false);
	}
	
	class SuspiciousStatement implements Comparable<SuspiciousStatement> {
		String className;
		Integer lineNumber;
		Double suspiciousValue;
		
		public SuspiciousStatement(String className, Integer lineNumber, Double suspiciousValue) {
			super();
			this.className = className;
			this.lineNumber = lineNumber;
			this.suspiciousValue = suspiciousValue;
		}

		@Override
		public int compareTo(SuspiciousStatement suspStmt) {
			int compResult = this.suspiciousValue.compareTo(suspStmt.suspiciousValue);
			if (compResult == 0) {
				compResult = suspStmt.lineNumber.compareTo(this.lineNumber);
			}
			return compResult;
		}
		
		@Override
		public String toString() {
			return this.className + "@" + this.lineNumber + "@" + this.suspiciousValue;
		}
	}
}
