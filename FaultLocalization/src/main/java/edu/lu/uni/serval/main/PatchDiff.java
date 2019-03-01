package edu.lu.uni.serval.main;

import java.util.List;

import edu.lu.uni.serval.entity.Pair;

public class PatchDiff {
	public String fileName;
	public int buggyHunkStartLine;
	public int buggyHunkRange;
	public int fixedHunkStartLine;
	public int fixedHunkRange;
	public int deletedLines;
	public int addedLines;
	public String hunk;

	public String className;
	public List<Pair<Integer, Integer>> methodRanges; //<Start_Line, End_Line> of buggy method declaration.
	public List<Integer> buggyLines;
	public List<Integer> fixedLines;
	
	public PatchDiff(int buggyHunkStartLine, int buggyHunkRange, int fixedHunkStartLine, int fixedHunkRange,
			int deletedLines, int addedLines, String hunk, List<Integer> buggyLines, List<Integer> fixedLines) {
		super();
		this.buggyHunkStartLine = buggyHunkStartLine;
		this.buggyHunkRange = buggyHunkRange;
		this.fixedHunkStartLine = fixedHunkStartLine;
		this.fixedHunkRange = fixedHunkRange;
		this.deletedLines = deletedLines;
		this.addedLines = addedLines;
		this.hunk = hunk;
		this.buggyLines = buggyLines;
		this.fixedLines = fixedLines;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	@Override
	public String toString() {
		return "\n(" + buggyHunkStartLine + " - " + buggyLines + " - " + (buggyHunkStartLine + buggyHunkRange - 1) + ")\n";
	}
}
