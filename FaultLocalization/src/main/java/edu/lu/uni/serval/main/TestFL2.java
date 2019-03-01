package edu.lu.uni.serval.main;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import com.gzoltar.core.components.Statement;
import com.gzoltar.core.instr.testing.TestResult;

import edu.lu.uni.serval.dataprepare.DataPreparer;
import edu.lu.uni.serval.faultlocalization.GZoltarFaultLoclaization;
import edu.lu.uni.serval.faultlocalization.Metrics;
import edu.lu.uni.serval.faultlocalization.Metrics.Metric;
import edu.lu.uni.serval.utils.FileHelper;
import edu.lu.uni.serval.utils.PathUtils;

/**
 * Output the suspicious code to a file.
 * 
 * @author kui.liu
 *
 */
public class TestFL2 {

	public static void main(String[] args) {
		String outputPath = "/Users/kui.liu/eclipse-workspace/SuspiciousCodeFiles/";
		String path = "../../Defects4JData/";//
		/**
		 * Bugs fixed by astor.
		 * C1, C3, C5, C7, C13, C15, C25, C26, 
		 * L27, 
		 * M 2, 5, 7, 8, 28, 32, 40, 44, 49, 50, 53, 57, 58, 60, 70, 71, 73, 74, 78, 80, 81, 82, 84, 85, 88, 95, 
		 * T4, T11
		 * 
		 * Prior metrics:
		 * Tarantula
		 * Ochiai2
		 * null
		 * Ochiai.
		 */
		String projectName = "Math_2";
		System.out.println(projectName);
		TestFL2 test = new TestFL2();
		try {
			test.locateSuspiciousCode(path, projectName, outputPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void locateSuspiciousCode(String path, String buggyProject, String outputPath) throws IOException {
		DataPreparer dp = new DataPreparer(path);
		dp.prepareData(buggyProject);
		if (!dp.validPaths) return;

		GZoltarFaultLoclaization gzfl = new GZoltarFaultLoclaization();
		gzfl.threshold = 0.0;
		gzfl.maxSuspCandidates = -1;
		gzfl.srcPath = path + buggyProject + PathUtils.getSrcPath(buggyProject).get(2);
		gzfl.localizeSuspiciousCodeWithGZoltar(dp.classPaths, checkNotNull(Arrays.asList("")), dp.testCases);
		
		String metricStr = "null";
		Metric metric = new Metrics().generateMetric(metricStr);
		
		gzfl.sortSuspiciousCode(metric);
		ArrayList<Statement> candidates1 = new ArrayList<Statement>(gzfl.selectedSuspiciousStatements.size());
		candidates1.addAll(gzfl.selectedSuspiciousStatements);
		ArrayList<String> failingTestCases = new ArrayList<String>(gzfl.failingTestCases.size());
		failingTestCases.addAll(gzfl.failingTestCases);
		ArrayList<TestResult> gzoltarTestResults = new ArrayList<TestResult>(gzfl.gzoltarTestResults.size());
		gzoltarTestResults.addAll(gzfl.gzoltarTestResults);
		
		File suspiciousFile1 = new File(outputPath + buggyProject + ".susp");
		File parentFile1 = suspiciousFile1.getParentFile();
		if (!parentFile1.exists()) parentFile1.mkdirs();
		if (suspiciousFile1.exists()) suspiciousFile1.delete();
		suspiciousFile1.createNewFile();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(suspiciousFile1));
        objectOutputStream.writeObject(candidates1);
        objectOutputStream.close();
        
        File failingTestCasesFile = new File(outputPath + buggyProject + ".ftcs");
        if (failingTestCasesFile.exists()) failingTestCasesFile.delete();
        failingTestCasesFile.createNewFile();
		objectOutputStream = new ObjectOutputStream(new FileOutputStream(failingTestCasesFile));
        objectOutputStream.writeObject(failingTestCases);
        objectOutputStream.close();
        
        File testResultsFile = new File(outputPath + buggyProject + ".tr");
        if (testResultsFile.exists()) testResultsFile.delete();
        testResultsFile.createNewFile();
		objectOutputStream = new ObjectOutputStream(new FileOutputStream(testResultsFile));
        objectOutputStream.writeObject(gzoltarTestResults);
        objectOutputStream.close();
        
        StringBuilder sb = new StringBuilder();
        for (Statement s : candidates1) {
			String rslt_s = "Suspicious line:" + s.getMethod().getParent().getLabel() + "," + s.getLineNumber() + ","
					+ s.getSuspiciousness();
			sb.append(rslt_s).append("\n");
		}
		
		File outputf = new File("/Users/kui.liu/eclipse-workspace/ssFix/output/" + buggyProject + "/gzoltar_fauloc");
		FileHelper.outputToFile(outputf, sb, false);
	}
}
