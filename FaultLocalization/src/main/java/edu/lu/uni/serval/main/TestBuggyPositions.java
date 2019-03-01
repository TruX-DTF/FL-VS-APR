package edu.lu.uni.serval.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.lu.uni.serval.config.Configuration;
import edu.lu.uni.serval.utils.FileHelper;

/**
 * Ranks of identified bugs.
 * 
 * @author kui.liu
 *
 */
public class TestBuggyPositions {
	
	public static void main(String[] args) {
		String outputPath = "GZoltar-0.1.1/BugPositions/";
		String linePosFile = outputPath + "bugsL.csv";
		String methodPosFile = outputPath + "bugsM.csv";
		String filePosFile = outputPath + "bugsF.csv";
		
		new TestBuggyPositions().parseResults(linePosFile);
		new TestBuggyPositions().parseResults(methodPosFile);
		new TestBuggyPositions().parseResults(filePosFile);
	}

	
	private void parseResults(String posFile) {
		Map<String, Result> results = new HashMap<String, TestBuggyPositions.Result>();
		String[] posArr = FileHelper.readFile(posFile).split("\n");
		for (int i = 1; i <= 395; i ++) {
			String pos = posArr[i];
			String[] posSubArr = pos.split(",");
			String proj = posSubArr[0];
			proj = proj.substring(0, proj.indexOf("_"));
			int rankIdx = -1;
			
			
			List<Integer> indexes = new ArrayList<>();
			for (int j = 1; j <= Configuration.METRICS_0_1_1.length; j ++) {
				String idxStr = posSubArr[j].trim();
				if ("-".equals(idxStr)) {
					indexes.add(-1);
					continue;
				}
				int index = Integer.valueOf(idxStr);
				indexes.add(index);
				if (rankIdx == -1) rankIdx = index;
				else if (rankIdx > index) rankIdx = index;
			}
			
			System.out.println(rankIdx);
			
			Result result = results.get(proj);
			if (result == null) {
				result = new Result();
				results.put(proj, result);
			}
			result.rankIndexes.add(rankIdx);
			if (rankIdx != -1) {
				for (int j = 0; j < indexes.size(); j ++) {
					int index = indexes.get(j);
					if (index == rankIdx) {
						String metircStr = Configuration.METRICS_0_1_1[j];
						Integer times = result.metricIndexes.get(metircStr);
						if (times == null) {
							times = 1;
						}
						times ++;
						result.metricIndexes.put(metircStr, times);
					}
				}
			}
		}
		
		StringBuilder b = new StringBuilder("Proj.,");
		for (String metricStr : Configuration.METRICS_0_1_1) {
			b.append(metricStr).append(",");
		}
		b.append("\n");
		
		for (Map.Entry<String, Result> entry : results.entrySet()) {
			String proj = entry.getKey();
			b.append(proj).append(",");
			Result result = entry.getValue();
			for (String metricStr : Configuration.METRICS_0_1_1) {
				Integer times = result.metricIndexes.get(metricStr);
				if (times == null) {
					b.append("0,");
				} else {
					b.append(times).append(",");
				}
			}
			b.append("\n");
		}
		System.out.println(b);
	}

	class Result {
		public List<Integer> rankIndexes = new ArrayList<>();
		public Map<String, Integer> metricIndexes = new HashMap<>();
		
		public String toString() {
			return rankIndexes.toString() + "\n" +
					metricIndexes.toString();
		}
	}
}
