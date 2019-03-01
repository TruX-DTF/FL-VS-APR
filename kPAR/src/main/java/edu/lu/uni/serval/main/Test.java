package edu.lu.uni.serval.main;

import edu.lu.uni.serval.config.Configuration;

public class Test {

	public static void main(String[] args) {
		Configuration.suspPositionsFilePath = "../Data/SuspiciousCodePositions/";
		Configuration.failedTestCasesFilePath = "../Data/FailedTestCases/";
		String buggyProjectsPath = "/Users/kui.liu/Public/Defects4JData/";
		String defects4jPath = "/Users/kui.liu/Public/git/defects4j/";
		String projectName = "Chart_1";
		System.out.println(projectName);
		
//		Main.fixBug(buggyProjectsPath, defects4jPath, projectName);
		
		Configuration.knownBugPositions = "../../eclipse-fault-localization/Data/BugPositions.txt";
		Main_Pos.fixBug(buggyProjectsPath, defects4jPath, projectName);
	}
	
}
