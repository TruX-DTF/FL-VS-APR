package edu.lu.uni.serval.main;

import edu.lu.uni.serval.config.Configuration;
import edu.lu.uni.serval.faultlocalization.FL;

public class Main {
	
	/**
	 * Input: 
	 * 		1. Defects4J project path. e.g., ../Defects4JData/
	 * 		2. Output path: e.g., SuspiciousCodePositions/
	 * 
	 * Output:
	 * 		1. ranked lists of suspicious statements for all buggy projects in Defects4J.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
//		if (args.length != 2 || args.length != 3) {
//			System.err.println("Parameter Format: <Defects4J_Project_Path> <Output_Path>");
//			System.exit(0);
//		}
		
		String outputPath = "GZoltar-0.1.1/" + Configuration.SUSPICIOUS_POSITIONS_FILE_APTH;
//		FileHelper.deleteDirectory(outputPath);
		String path = Configuration.BUGGY_PROJECTS_PATH;
		
		for (int i = 1; i <= 106; i ++) {
			String projectName = "Math_" + i;
			FL fl = new FL();
			fl.locateSuspiciousCode(path, projectName, outputPath);
		}
		for (int i = 1; i <= 65; i ++) {
			String projectName = "Lang_" + i;
			FL fl = new FL();
			fl.locateSuspiciousCode(path, projectName, outputPath);
		}
		for (int i = 1; i <= 26; i ++) {
			String projectName = "Chart_" + i;
			FL fl = new FL();
			fl.locateSuspiciousCode(path, projectName, outputPath);
		}
		for (int i = 1; i <= 27; i ++) {
			String projectName = "Time_" + i;
			FL fl = new FL();
			fl.locateSuspiciousCode(path, projectName, outputPath);
		}
		for (int i = 1; i <= 38; i ++) {
			String projectName = "Mockito_" + i;
			FL fl = new FL();
			fl.locateSuspiciousCode(path, projectName, outputPath);
		}
		for (int i = 1; i <= 8; i ++) {
			String projectName = "Closure_" + i;
			FL fl = new FL();
			fl.locateSuspiciousCode(path, projectName, outputPath);
		}

		for (int i = 10; i <= 133; i ++) {
			String projectName = "Closure_" + i;
			FL fl = new FL();
			fl.locateSuspiciousCode(path, projectName, outputPath);
		}
	}

}
