# Data of Bugs
The detailed information of bug positions and failed test cases for each bug in Defects4J -- version 1.2.0.

I. Requirement
--------------
 - [Defects4J - version 1.2.0](https://github.com/rjust/defects4j)
 
II. Failed Test Cases
---------------------
Failed test cases are obtained by running command `defects4j test` for each buggy project, and are saved in distinct file formatted as `ProjectName_buggyID.txt`. 
For example, the failed test cases of buggy project `Chart_1` are saved in [FailedTestCases/Chart_1.txt](https://github.com/SerVal-DTF/FL-VS-APR/blob/master/data/FailedTestCases/Chart_1.txt). 
Note that, before running command `defects4j test` for each buggy project, please prepare bugs with this [instructions](https://github.com/SerVal-DTF/FL-VS-APR/tree/master/Defecst4JBugs).
 
 
III. Buggy Files
----------------
The Java code file(s) of each buggy project contain the buggy code that makes the program fail to pass some test cases.
For example, the buggy file of buggy project `Chart_1` is 

    source/org/jfree/chart/renderer/category/AbstractCategoryItemRenderer.java.


IV. Bug Positions 
-----------------
Bug positions are the line positions in Java code files, where the code is involved in the buggy code fragment. 
The bug positions are formated as '`ProjectName_buggyID@buggy_file@line_number`'.
For example, the bug position of buggy project `Chart_1` is line 1797 in file `source/org/jfree/chart/renderer/category/AbstractCategoryItemRenderer.java`.
Thus the bug position is formatted as 

    Chart_1@source/org/jfree/chart/renderer/category/AbstractCategoryItemRenderer.java@1797.

Bug positions of all Defects4J bugs are saved in [`BugPositions.txt`](https://github.com/SerVal-DTF/FL-VS-APR/blob/master/data/BugPositions.txt).
 