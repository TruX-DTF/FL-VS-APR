package edu.lu.uni.serval.main;

import java.io.FileNotFoundException;
import java.io.IOException;

public class TestFL3 {

	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		// The path of suspicious code file must be the absolute path.
//		List<File> files = FileHelper.getAllFilesInCurrentDiectory("/Users/kui.liu/eclipse-workspace/SuspiciousCodeFiles/", "susp");
		
//		for (File file : files) {
//			if (!file.getName().startsWith("Time_11.")) continue;
//			String suspiciousCodeFileName = file.getPath();
//			System.out.println(file.getName());
//			String failingTestCasesFileName = suspiciousCodeFileName.substring(0, suspiciousCodeFileName.length() - 4) + "ftcs";
//			String testResultsFileName = suspiciousCodeFileName.substring(0, suspiciousCodeFileName.length() - 4) + "tr";
//			
//			ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(failingTestCasesFileName));
//			Object object = objectInputStream.readObject();
//			@SuppressWarnings("unchecked")
//			ArrayList<String> failingTestCases = (ArrayList<String>) object;
//			System.out.println("failingTestCases: " + failingTestCases.size());
//			objectInputStream.close();
//
//			objectInputStream = new ObjectInputStream(new FileInputStream(testResultsFileName));
//			Object object1 = objectInputStream.readObject();
//			@SuppressWarnings("unchecked")
//			ArrayList<TestResult> testResults = (ArrayList<TestResult>) object1;
//			System.out.println("testResults: " + testResults.size());
//			objectInputStream.close();
//			
//			objectInputStream = new ObjectInputStream(new FileInputStream(suspiciousCodeFileName));
//			Object object2 = objectInputStream.readObject();
//			@SuppressWarnings("unchecked")
//			ArrayList<Statement> results = (ArrayList<Statement>) object2;
//			System.out.println("Statements: " + results.size());
//	        objectInputStream.close();
//		}
	}

}
