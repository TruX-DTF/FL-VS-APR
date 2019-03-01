package edu.lu.uni.serval.bug.fixer;

/**
 * Store the information of generated patches.
 * 
 * @author kui.liu
 *
 */
public class Patch {
	
	private String buggyCodeStr = "";
	private String fixedCodeStr1 = "";
	private String fixedCodeStr2 = "";
	private int buggyCodeStartPos = -1;
	/*
	 * if (buggyCodeEndPos == buggyCodeStartPos) then
	 * 		replace buggyCodeStr with fixedCodeStr1;
	 * else if (buggyCodeEndPos > buggyCodeStartPos && buggyCodeStartPos == -1) then
	 * 		insert fixedCodeStr1 before buggyCodeStr;
	 * else if (buggyCodeEndPos > buggyCodeStartPos && buggyCodeStartPos > 0) then
	 * 		fixedCodeStr1 + buggCodeStr + fixedCodeStr2;
	 */
	private int buggyCodeEndPos = -1;

	public String getBuggyCodeStr() {
		return buggyCodeStr;
	}

	public void setBuggyCodeStr(String buggyCodeStr) {
		this.buggyCodeStr = buggyCodeStr;
	}

	public String getFixedCodeStr1() {
		return fixedCodeStr1;
	}

	public void setFixedCodeStr1(String fixedCodeStr1) {
		this.fixedCodeStr1 = fixedCodeStr1;
	}

	public String getFixedCodeStr2() {
		return fixedCodeStr2;
	}

	public void setFixedCodeStr2(String fixedCodeStr2) {
		this.fixedCodeStr2 = fixedCodeStr2;
	}

	public int getBuggyCodeStartPos() {
		return buggyCodeStartPos;
	}

	public void setBuggyCodeStartPos(int buggyCodeStartPos) {
		this.buggyCodeStartPos = buggyCodeStartPos;
	}

	public int getBuggyCodeEndPos() {
		return buggyCodeEndPos;
	}

	public void setBuggyCodeEndPos(int buggyCodeEndPos) {
		this.buggyCodeEndPos = buggyCodeEndPos;
	}

	@Override
	public String toString() {
		return this.fixedCodeStr1 + "\n" + this.fixedCodeStr2;
	}
}
