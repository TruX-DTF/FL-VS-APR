# Fix Bugs with kPAR automatically

kPAR: a straightforward fix pattern-based APR system. 

I. Requirement
----------------
 - Java 1.7
 - [Defects4J](https://github.com/rjust/defects4j)
 - GZoltar 0.1.1
 - kPAR
 
 II. How to run kPAR
--------------------

### A. Set up the Environment
Set up Defects4J bugs with this [instructions](https://github.com/SerVal-DTF/FL-VS-APR/tree/master/Defecst4JBugs).

### B. Run kPAR
See the parameter setting of `main` method in `edu.lu.uni.serval.main.Main.java` or `edu.lu.uni.serval.main.Main_Pos.java`.


III. Results 
------------
kPAR is evaluated on Defects4J benchmark with four different fault localization (FL) configurations.
- **Normal_FL**: it gives a ranked list of suspicious code locations identical as reported by a given FL tool (GZotarl 0.1.1 with Ochiai in kPAR).
- **File_Assumption**: it assumes that the faulty code files are known. Suspicious code locations from **Normal_FL** are then filtered accordingly. In other words, locations in the known buggy files are selected and locations in other files are ignored.
- **Method_Assumption**: it assumes that the faulty methods are known. Only suspicious code locations in the known methods are selected and locations in other methods are ignored.
- **Line_Assumption**: it assumes that the faulty code lines are known. No fault localization is then used.


We consider two kinds of correctly fixed bugs:
- **Fully-fixed bugs**, which are fixed with patches make the program pass all available test cases. 
- **Partially-fixed bugs**, which are fixed with patches that make the program pass not only all previously-passing test cases, but also part of the previously-failing test cases.
