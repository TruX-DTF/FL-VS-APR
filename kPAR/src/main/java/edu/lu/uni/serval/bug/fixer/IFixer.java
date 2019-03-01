package edu.lu.uni.serval.bug.fixer;

import java.util.List;

import edu.lu.uni.serval.bug.fixer.AbstractFixer.SuspCodeNode;
import edu.lu.uni.serval.dataprepare.DataPreparer;
import edu.lu.uni.serval.utils.SuspiciousPosition;

/**
 * Fixer Interface.
 * 
 * @author kui.liu
 *
 */
public interface IFixer {

	public List<SuspiciousPosition> readSuspiciousCodeFromFile(String metric, String buggyProject, DataPreparer dp);
	
	public SuspCodeNode parseSuspiciousCode(SuspiciousPosition suspiciousCode);

	public void fixProcess();
	
}
