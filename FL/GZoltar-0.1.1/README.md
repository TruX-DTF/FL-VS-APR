# Results of Fault Localization with GZoltar-0.1.1
Suspicious code positions of Defects4J bugs localized with GZoltar-0.1.1.

Forty ranking metrics used in fault localization are considered in this experiment. They are: `Ample`, `Anderberg`, `ArithmeticMean`, `Barinel`, `Dice`, `DStar`, `Euclid`, `Fagge`, `Fleiss`, `GeometricMean`, `Goodman`, `Gp13`, `Hamann`, `Hamming`, `HarmonicMean`, `Jaccard`, `Kulczynski1`, `Kulczynski2`, `M1`, `M2`, `McCon`, `Minus`, `Muse`, `Naish1`, `Naish2`, `Ochiai`, `Ochiai2`, `Overlap`, `Qe`, `RogersTanimoto`, `Rogot1`, `Rogot2`, `RussellRao`, `Scott`, `SimpleMatching`, `Sokal`, `SorensenDice`, `Tarantula`, `Wong1`, `Wong2`, `Wong3`, `Zoltar`, and `null`, where `null` means the default ranking metric used in GZoltar-0.1.1.

Results 
-------
1. A ranked list of suspicious code statements for each buggy project.

The suspicious code statements are decreasingly sorted by their suspiciousness values that are calculated with a ranking metric (e.g., `Ochiai`). 
All of them are formated as `packageName.className@lineNumber` and saved in a file named `Ranking_Metric_Name.txt`.
For example, the package name, class name and line number of bug `Chart_1` are `org.jfree.chart.renderer.category`, `AbstractCategoryItemRenderer` and `1797`, respectively. 
This position is formated as `org.jfree.chart.renderer.category.AbstractCategoryItemRenderer@1797`.
Eventually, all suspicious code statements in buggy project `Chart_1` located with GZoltar-0.1.1 and the ranking metric Ochiai are saved in the file [Chart_1/Ochiai.txt](https://github.com/flvsapr/FL-VS-APR/blob/master/FL/GZoltar-0.1.1/SuspiciousCodePositions/Chart_1/Ochiai.txt). The same as others. 
Note that, If the suspicious value of a statement is 0.0, this statement is not contained in the list of suspicious code statements. 

2. Buggy code entity.

The buggy code entities are presented with three granularities of fault locality at the **file**, **method** and **line** levels:
- **File**: At this level,  the bug is localized if any line from the buggy code file is contained in the list.
- **Method**: At this level, the bug is localized if any line from the buggy code method is contained in the list. 
- **Line**: At this level, the bug is localized if any the buggy code lines is contained in the list.

3. Bug positions in the ranked lists.

At a given granularity level, if the bug can be localized, the associated position of the correct fault locality within the ranked list of suspicious code locations is recorded as the bug position.
Since a bug position could span over several lines, methods, and even over several files, the bug is considered to be correctly localized by an FL tool as long as any reported suspicious code line can match the ground truth bug locations with the corresponding granularity.
If the bug cannot be localized, the bug position is presented as `-`.
The localized bug positions are saved in folder [BugPostions](https://github.com/flvsapr/FL-VS-APR/tree/master/FL/GZoltar-0.1.1/BugPositions).
