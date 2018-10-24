dir=<D4J_Bugs_OutputPath> # Store the buggy projects.

proj=Chart
for bug in $(seq 1 26)
do
	defects4j checkout -p $proj -v ${bug}b -w ${dir}${proj}_${bug}
done

proj=Lang
for bug in $(seq 1 65)
do
        defects4j checkout -p $proj -v ${bug}b -w ${dir}${proj}_${bug}
done

proj=Math
for bug in $(seq 1 106)
do
        defects4j checkout -p $proj -v ${bug}b -w ${dir}${proj}_${bug}
done

proj=Closure
for bug in $(seq 1 133)
do
        defects4j checkout -p $proj -v ${bug}b -w ${dir}${proj}_${bug}
done

proj=Mockito
for bug in $(seq 1 38)
do
        defects4j checkout -p $proj -v ${bug}b -w ${dir}${proj}_${bug}
done

proj=Time
for bug in $(seq 1 27)
do
	defects4j checkout -p $proj -v ${bug}b -w ${dir}${proj}_${bug}
done