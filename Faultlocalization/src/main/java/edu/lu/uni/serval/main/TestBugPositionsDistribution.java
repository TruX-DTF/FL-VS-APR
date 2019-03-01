package edu.lu.uni.serval.main;

import java.util.ArrayList;
import java.util.List;

import edu.lu.uni.serval.config.Configuration;
import edu.lu.uni.serval.utils.FileHelper;

/**
 * Boxplot Distribution of identified bug positions with each FL metric.
 * 
 * @author kui.liu
 *
 */
public class TestBugPositionsDistribution {
	
	public static void main(String[] args) {
		String dataPath = "GZoltar-0.1.1/BugPositions/";
		String linePosFile = dataPath + "bugsL.csv";
		String methodPosFile = dataPath + "bugsM.csv";
		String filePosFile = dataPath + "bugsF.csv";
		
		new TestBugPositionsDistribution().parseResults(linePosFile, dataPath + "bugsL_boxplot.csv");
		new TestBugPositionsDistribution().parseResults(methodPosFile, dataPath + "bugsM_boxplot.csv");
		new TestBugPositionsDistribution().parseResults(filePosFile, dataPath + "bugsF_boxplot.csv");
	}

	static List<String> badMetrics = new ArrayList<>();
	
	static List<String> badMetrics2 = new ArrayList<>();
	static {
		badMetrics.add("Euclid");
		badMetrics.add("Fleiss");
		badMetrics.add("Hamann");
		badMetrics.add("Hamming");
		badMetrics.add("M1");
		badMetrics.add("Naish1");
		badMetrics.add("RogersTanimoto");
		badMetrics.add("Scott");
		badMetrics.add("SimpleMatching");
		badMetrics.add("Sokal");
		

		badMetrics2.add("Ample");
		badMetrics2.add("Fagge");
		badMetrics2.add("Gp13");
		badMetrics2.add("M2");
		badMetrics2.add("Naish2");
		badMetrics2.add("Rogot1");
		badMetrics2.add("Rogot2");
		badMetrics2.add("RussellRao");
		badMetrics2.add("Wong1");
	}
	private void parseResults(String posFile, String outputFile) {
		String[] posArr = FileHelper.readFile(posFile).split("\n");
		StringBuilder builder = new StringBuilder("Algo,Pos\n");
		
		for (int i = 1; i <= 395; i ++) {
			String pos = posArr[i];
			String[] posSubArr = pos.split(",");
			String proj = posSubArr[0];
			proj = proj.substring(0, proj.indexOf("_"));
			
			
			List<Integer> indexes = new ArrayList<>();
			for (int j = 1; j <= Configuration.METRICS_0_1_1.length; j ++) {
				String metricStr = Configuration.METRICS_0_1_1[j - 1];
				if (badMetrics.contains(metricStr) || badMetrics2.contains(metricStr)) continue;
				if ("null".equals(metricStr)) metricStr = "Default";
				String idxStr = posSubArr[j].trim();
				if ("-".equals(idxStr)) {
					indexes.add(-1);
//					continue;
					idxStr = "100000";
				}
				builder.append(metricStr).append(",");
				builder.append(idxStr).append("\n");
			}
		}
		
		FileHelper.outputToFile(outputFile, builder, false);
		
	}

}
