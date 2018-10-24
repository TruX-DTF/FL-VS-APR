dir=<D4J_Bugs_OutputPath> # Store the buggy projects.

proj=Chart
for bug in $(seq 1 26)
do
	cd ${proj}_${bug}
	defects4j compile
	cd ..
done

proj=Lang
for bug in $(seq 1 65)
do
	cd ${proj}_${bug}
	defects4j compile       
	cd ..
done

proj=Math
for bug in $(seq 1 106)
do
        cd ${proj}_${bug}
        defects4j compile
        cd ..
done

proj=Closure
for bug in $(seq 1 133)
do
        cd ${proj}_${bug}
        defects4j compile
        cd ..
done

proj=Mockito
for bug in $(seq 1 38)
do
        cd ${proj}_${bug}
        defects4j compile
        cd ..
done

proj=Time
for bug in $(seq 1 27)
do
	cd ${proj}_${bug}
	defects4j compile
	cd ..
done

