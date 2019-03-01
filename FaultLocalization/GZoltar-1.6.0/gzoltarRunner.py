

from subprocess import Popen, PIPE
import os
from multiprocessing.pool import ThreadPool, Pool
from os.path import join,isfile

projects = {'Lang':'65','Chart':'26','Time':'27','Mockito':'38','Math':'106','Closure':'133'}

formulas = ['tarantula','ochiai','dstar2','barinel','opt2','muse','jaccard']

FLHOME = '/Users/anilkoyuncu/projects/fault-localization-data/'  # what is this path? Input or output?

GZOLTAR_OUTPUT = FLHOME + 'gzoltars'
SCRIPTS = 'analysis/pipeline-scripts'

LAUNCHER_PATH = '/Users/anilkoyuncu/projects/faultLocalization'  # what is this path? Input or output?

def prepareFiles(t):

    try:
        project,bug = t
        # if (isfile(join(FLHOME, 'gzoltars', project, bug, 'matrix')) and isfile(
        #         join(FLHOME, 'gzoltars', project, bug, 'spectra'))):
        #     print('Skipping '+ project +' '+bug)
        # else:
        os.chdir(FLHOME)

        cmd = 'gzoltar/run_gzoltar.sh '+project+''+' '+bug+' ./ developer'
        print(cmd)

        # cmd = 'git -C ' + gitrepo + ' show ' + sha + ':' + filePath + '> spdiff/' + sha +':' +f+ '.original'
        # lines = subprocess.check_output(cmd, shell=True)
        with Popen(cmd, stdout=PIPE, stderr=PIPE, shell=True) as p:
            # stdin={'file': PIPE, 'encoding': 'iso-8859-1', 'newline': False},
            # stdout={'file': PIPE, 'encoding': 'utf-8', 'buffering': 0, 'line_buffering': False}) as p:
            output, errors = p.communicate()
        print(errors)
        lines = output.decode('latin-1')
        # print lines
        # original

    except Exception as e:
        print(e)
        return ''


def runGzoltar():
    tuples = []
    for k, v in projects.items():
        for i in range(1, int(v)+1):
            t = k,str(i)
            tuples.append(t)

    coreNumber = 4
    print('Core number %s' % coreNumber)

    pool = ThreadPool(coreNumber)

    data = pool.map(prepareFiles, [link for link in tuples])

def runCrushMatrix(t):
    try:
        project, bug = t
        matrixFile = join(FLHOME, 'gzoltars', project, bug, 'matrix')
        spectraFile = join(FLHOME, 'gzoltars', project, bug, 'spectra')
        if (isfile(matrixFile) and isfile(
                spectraFile)):

            scriptPath = join(FLHOME,SCRIPTS,'crush-matrix')

            for formula in formulas:
                outputFile = join(FLHOME, 'crushMatrixOutput', formula, project + '_' + bug)
                launcherPath= join(LAUNCHER_PATH,'launchCrushMatrix.sh')
                cmd = '%s %s %s %s %s %s ' % (launcherPath,scriptPath,formula,matrixFile,spectraFile,outputFile)

                if(not isfile(outputFile)):
                    print(cmd)
                    with Popen(cmd, stdout=PIPE, stderr=PIPE, shell=True) as p:
                        # stdin={'file': PIPE, 'encoding': 'iso-8859-1', 'newline': False},
                        # stdout={'file': PIPE, 'encoding': 'utf-8', 'buffering': 0, 'line_buffering': False}) as p:
                        output, errors = p.communicate()
                    print(errors)



    except Exception as e:
        print(e)
        return ''



def crushM():
    tuples = []
    for k, v in projects.items():
        for i in range(1, int(v) + 1):
            t = k, str(i)
            tuples.append(t)
    pool = ThreadPool(8)

    data = pool.map(runCrushMatrix, [link for link in tuples])

def launchStmt2Line():

    scriptPath = join(FLHOME,SCRIPTS,'stmt-susps-to-line-susps')
    launcherPath = join(LAUNCHER_PATH, 'launchStmt2Line.sh')
    stmt_susps =join(FLHOME,'crushMatrixOutput/tarantula/Lang_37')
    sourceCodeLines =join(FLHOME,SCRIPTS,'source-code-lines/Lang-37b.source-code.lines')
    outputFile = join(FLHOME,'lineNumber','tarantula/Lang_37')
    cmd = '%s %s %s %s %s' % (launcherPath, scriptPath, stmt_susps, sourceCodeLines, outputFile)
    with Popen(cmd, stdout=PIPE, stderr=PIPE, shell=True) as p:
        # stdin={'file': PIPE, 'encoding': 'iso-8859-1', 'newline': False},
        # stdout={'file': PIPE, 'encoding': 'utf-8', 'buffering': 0, 'line_buffering': False}) as p:
        output, errors = p.communicate()
    print(errors)

pj = [('Closure','110'),('Closure','117'),('Closure','118'),('Closure','129'),('Mockito','1'),('Mockito','2'),('Mockito','3'),('Mockito','4'),('Mockito','5'),('Mockito','6'),('Mockito','7'),('Mockito','8'),('Mockito','18'),('Mockito','19'),('Mockito','20')]
if __name__ == '__main__':
    launchStmt2Line()
    # for t in pj:
    #     # prepareFiles(t)
    #     runCrushMatrix(t)
