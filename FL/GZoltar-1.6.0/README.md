# Results of Fault Localization with GZoltar-1.6.0
Suspicious code positions of Defects4J bugs localized with GZoltar-1.6.0.

Seven ranking metrics used in fault localization are considered in this experiment. They are: `barinel`, `dstar2`, `jaccard`, `muse`, `ochiai`, `opt2`, and `tarantula`.

Results 
-------
1. Initial results (`crushMatrixOutput`) after running GZoltar-1.6.0 on buggy programs.

https://bitbucket.org/rjust/fault-localization-data

2. A ranked list of suspicious code statements for each buggy project.

> The suspicious code statements are decreasingly sorted by their suspiciousness values that are calculated with a ranking metric (e.g., `Ochiai`). 
All of them are formated as `packageName.className@lineNumber` and saved in a file named `Ranking_Metric_Name.txt`.
For example, the package name, class name and line number of bug `Chart_1` are `org.jfree.chart.renderer.category`, `AbstractCategoryItemRenderer` and `1797`, respectively. 
This position is formated as `org.jfree.chart.renderer.category.AbstractCategoryItemRenderer@1797`.
Eventually, all suspicious code statements in buggy project `Chart_1` located with GZoltar-0.1.1 and the ranking metric Ochiai are saved in the file [Chart_1/Ochiai.txt](https://github.com/flvsapr/FL-VS-APR/blob/master/FL/GZoltar-1.6.0/SuspiciousCodePositions/Chart_1/ochiai.txt). The same as others. 
Note that, If the suspicious value of a statement is 0.0, this statement is not contained in the list of suspicious code statements. 

3. Granularity of fault locality.(#granularity-fl)

> Granularity of fault locality consists of three levels: **file**, **method** and **line**.
> - **File**: At this level,  the bug is localized if any line from the buggy code file is contained in the list.
> - **Method**: At this level, the bug is localized if any line from the buggy code method is contained in the list. 
> - **Line**: At this level, the bug is localized if any the buggy code lines is contained in the list.

4. Bug positions in the ranked lists.

> At a given granularity level, if the bug can be localized, the associated position of the correct fault locality within the ranked list of suspicious code locations is recorded as the bug position.
Since a bug position could span over several lines, methods, and even over several files, the bug is considered to be correctly localized by an FL tool as long as any reported suspicious code line can match the ground truth bug locations with the corresponding granularity.
If the bug cannot be localized, the bug position is presented as `-`.
The localized bug positions are saved in folder [BugPostions](https://github.com/flvsapr/FL-VS-APR/tree/master/FL/GZoltar-1.6.0/BugPositions).
 