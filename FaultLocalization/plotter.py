import pandas as pd
import matplotlib.pyplot as plt
import matplotlib.dates as mdates
import matplotlib.ticker as mticker
from matplotlib.dates import MonthLocator
import numpy as np
from os.path import join,isfile
import pickle as p


def prepareDataFrame(frame,col):
    ys = frame[col].values.tolist()
    return ys

otherColors = ['#00A8F0','#C0D800','#CB4B4B','#4DA74D','#9440ED','#800080','#737CA1','#E4317F','#7D0541','#4EE2EC',
                       '#6698FF','#437C17','#7FE817','#FBB117']



def plotBoxInverted(res,fn,rotate=True):
    import matplotlib.pyplot as plt
    # labels = colNames
    labels = res.columns[1:].values
    # import seaborn as sns
    # xValues = []
    yList = []
    print('1. plot')
    for i in labels:
       print(np.mean(res[i]))
       yList.append(res[i])
    #         # res.apply(lambda x: yList.append(x[col]), axis=1)
    #     # yList_split = np.array_split(yList,10)
    #     #     labels_split = np.array_split(labels,10)
    #     #     y = yList_split[0]
    #     #     labels = labels_split[0]
    #     # ybox = [i.tolist() for i in yList]
    #     # xValues.append(ybox)

    fig = plt.figure()
    ax1 = fig.add_subplot(111)
    red_square = dict(markerfacecolor='r', marker='.')
    box = ax1.boxplot(yList, showmeans=True, vert=False,whis=1.5,flierprops=red_square,notch=False)

    if rotate:
        ax1.set_yticklabels(labels,rotation = 45, ha = 'right')
    else:
        ax1.set_yticklabels(labels)

    ax1.set_aspect('auto')
    ax1.set_xlim(left=0, right=1)
    for line in box['medians']:
        # get position data for median line
        x, y = line.get_xydata()[1]  # top of median line
        line.set(linewidth=4)
            # overlay median value
        ax1.text(x, y, '%.2f' % x, horizontalalignment='right', verticalalignment='bottom',
                    fontsize=11)  # draw above, centered
    for line in box['means']:
            # get position data for median line\n"
        x, y = line.get_xydata()[0]  # bottom of left line\n"
            # overlay median value\n"
        ax1.text(x + .25, y + .02, '%.2f' % x, horizontalalignment='right', verticalalignment='bottom',
                    fontsize=11)  # draw above, centered\n"

    #
    ax1.set_ylabel('FL Algorithm')
    ax1.set_xlabel('Position')

    plt.ion()

    plt.subplots_adjust(wspace=0, hspace=0)
    fig = plt.gcf()
    fig.savefig(join('plots',fn), dpi=100, bbox_inches='tight')



def plotBoxMulti(res):
    import matplotlib.pyplot as plt
    fig, axes = plt.subplots(nrows=1, ncols=3)
    colors = ['lightgreen', 'lightblue', 'pink']
    colors = ['whitesmoke','silver','gray']

    # labels = ['D&C','D&C_Base','Locus','BLIA','BRTracer','AmaLgam','BLUiR','BugLocator'  ]
    labels = res.columns[1:].values
    xValues = []
    for i in range(len(res)):
        yList = []
        for col in labels:
            yList.append(res.iloc[i][col])

        ybox = [i.tolist() for i in yList]
        xValues.append(ybox)

    ylabels = ['File Level', 'Method Level', 'Line Level']
    counter = 0
    for ax, y, l in zip(axes.flat, xValues, ylabels):
        if counter == 0:
            plt.setp(ax.get_yticklabels(), visible=True)
            ax.set_ylabel(r'Reciprocal position $^{*}$',fontsize=12)#,multialignment='left')
            # ax.set_ylabel('Reciprocal position of correct location \namong the ranked list of suspicious locations',fontsize=12)  # ,multialignment='left')

        else:
            plt.setp(ax.get_yticklabels(), visible=False)
        # ax.set_ylabel(label, rotation=45,ha='right')
        ax.set_xlabel(l, fontsize=12)
        ax.set_ylim(top=1.01, bottom=0)
        plt.setp(ax.get_xticklabels(), visible=True)

        box = ax.boxplot(y, 1, '', showmeans=True, vert=True, autorange=True,patch_artist=True,whis=[5, 95])  # widths=.08,
        # positions=[0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7])  # autorange=True  whis=2
        ax.set_xticklabels(['CF','OF','UF'],  ha='right')

        ax.set_aspect('auto')
        # ax.set_xlim(left=0.05, right=.75)
        for patch,color in zip(box['boxes'],colors):
            patch.set_facecolor(color)

        for line in box['medians']:
            # get position data for median line
            x,y=  line.get_xydata()[1]  # top of median line
            line.set(linewidth=5)
            line.set_color('black')
            # overlay median value
            # ax.text(x, y-0.01, '%.2f' % y, horizontalalignment='center', verticalalignment='top',
            #         fontsize=12)  # draw above, centered
        for line in box['means']:
            # get position data for median line\n"
            x,y = line.get_xydata()[0]
            line.set_color('black')
            line.set(linewidth=5)# bottom of left line\n"
            # overlay median value\n"
            # line.set_color('orange')
            # line.set(color='orange', linewidth=10)
        #     ax.text(x+.05 , y , '%.2f' % y, horizontalalignment='center', verticalalignment='top',
        #             fontsize=12)  # draw above, centered\n"
        counter += 1



    # ax.set_xlabel('Bug Report')

    plt.subplots_adjust(wspace=0.35, hspace=0)
    # ax1.set_yscale('log')
    # plt.subplots_adjust(left=0.18,bottom=0.15,right=0.98, top=0.95 , wspace= 0.1, hspace=0)
    # plt.legend(bbox_to_anchor=(1.05, 1), loc=2, borderaxespad=0.)
    plt.ion()

    fig = plt.gcf()
    fig.set_size_inches(5, 2, forward=True)
    fig.savefig('distOfAccuracy.pdf', dpi=100, bbox_inches='tight')
    plt.show()


def plotBox(res,fn,rotate=True):
    import matplotlib.pyplot as plt
    # labels = colNames
    labels = res.columns[1:].values
    # import seaborn as sns
    # xValues = []
    yList = []
    for i in labels:
       print(np.mean(res[i]))
       yList.append(res[i])
    #         # res.apply(lambda x: yList.append(x[col]), axis=1)
    #     # yList_split = np.array_split(yList,10)
    #     #     labels_split = np.array_split(labels,10)
    #     #     y = yList_split[0]
    #     #     labels = labels_split[0]
    #     # ybox = [i.tolist() for i in yList]
    #     # xValues.append(ybox)

    fig = plt.figure()
    ax1 = fig.add_subplot(111)
    box = ax1.boxplot(yList, 0, '', showmeans=True, vert=True,whis=1.5)
    # ax1.boxplot(yList,notch=False, sym='', vert=True, whis=1.5,
    #     positions=None, widths=None, patch_artist=True,
    #     bootstrap=None, usermedians=None, conf_intervals=None)
    if rotate:
        ax1.set_xticklabels(labels,rotation = 45, ha = 'right')
    else:
        ax1.set_xticklabels(labels)
    # sns.boxplot(yList, ax=ax1)

    # fig.set_size_inches(4, 4, forward=True)

    # fig, axes = plt.subplots(nrows=1, ncols=len(res))
    # ylabels = res['index'].values.tolist()
    # ylabels = ['MRR','MAP']
    # for ax, y, l in zip(axes.flat, xValues, ylabels):  # ,['Enum','StackTrace','CodeRegion','CodeEntity']):
    #     print(ax)
    #     plt.setp(ax.get_yticklabels(), visible=True)
    #     # ax.set_ylabel(label, rotation=45,ha='right')
    #     plt.setp(ax.get_xticklabels(), visible=True)
    #
    #     box = ax.boxplot(y, 0, '', showmeans=True, vert=False, autorange=True) #widths=.08,
    #                     #positions=[0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7])  # autorange=True  whis=2
    #     ax.set_yticklabels(labels, rotation=45, ha='right')
    #     ax.set_xlabel(l,  fontsize=12)
    #     # ax.set_yticks([0, 0.5, 1])
    #     # ax.set_xticks([0, 0.5, 1,1.5,2,2.5,3,3.5,4,4.5,5])
    #     # img = ax.imshow(im, vmin=0, vmax=1,cmap='binary')
    ax1.set_aspect('auto')
    ax1.set_ylim(bottom=0, top=1)
    for line in box['medians']:
        # get position data for median line
        x, y = line.get_xydata()[1]  # top of median line
        line.set(linewidth=4)
            # overlay median value
        ax1.text(x, y, '%.2f' % y, horizontalalignment='right', verticalalignment='bottom',
                    fontsize=11)  # draw above, centered
    for line in box['means']:
            # get position data for median line\n"
        x, y = line.get_xydata()[0]  # bottom of left line\n"
            # overlay median value\n"
        ax1.text(x + .25, y + .02, '%.2f' % y, horizontalalignment='right', verticalalignment='bottom',
                    fontsize=11)  # draw above, centered\n"
    #         # for box in box['boxes']:
    #         #     # change outline color
    #         #     box.set(linewidth=4)
    #         #     ## change color and linewidth of the medians
    #         # for whisker in box['whiskers']:
    #         #     whisker.set(linewidth=4)
    #         # ## change color and linewidth of the caps
    #         # for cap in box['caps']:
    #         #     cap.set(linewidth=4)
    # plt.setp(ax.get_yticklabels(), visible=False)
    #
    ax1.set_xlabel('FL Algorithm')
    ax1.set_ylabel('Position')
    # ax1.set_title(title)
    plt.ion()

    plt.subplots_adjust(wspace=0, hspace=0)
    fig = plt.gcf()
    fig.savefig(join('plots',fn), dpi=100, bbox_inches='tight')
    #
    # plt.subplots_adjust(wspace=0, hspace=0)
    # # ax1.set_yscale('log')
    # # plt.subplots_adjust(left=0.18,bottom=0.15,right=0.98, top=0.95 , wspace= 0.1, hspace=0)
    # # plt.legend(bbox_to_anchor=(1.05, 1), loc=2, borderaxespad=0.)
    # plt.ion()
    # fig = plt.gcf()
    # fig.set_size_inches(6, 6, forward=True)
    # fig.savefig('rq3_mrrNew.pdf', dpi=100)
    # plt.show()





def tops(typ,granularity,r):


    DATA_HOME = '/Users/anilkoyuncu/Downloads/Data/'
    if typ == 'GZoltar16':
        SUSP_HOME = 'BugPositionsGZoltar'
    else:
        SUSP_HOME = 'BugPositions'



    test =pd.read_csv(join(DATA_HOME,SUSP_HOME,granularity+".csv"))
    if typ=='GZoltar16':
        a = test[['Proj','tarantula','ochiai','dstar2','barinel','opt2','muse','jaccard']]
    else:
        # a = test[test.columns[:-2]]
        a = test[['Proj','Tarantula','Ochiai','DStar','Barinel','Naish2','Muse','Jaccard']]

    a.replace('-', 0,inplace=True)


    columns = a.columns[1:]
    for c in columns:
        a[c] = a[c].apply(pd.to_numeric, errors='coerce')
        if r == 'Top10':
            a[c] = a[c].apply(lambda x: (0 if x == 0 or x >10 else 1/x))
        elif r =='Top100':
            a[c] = a[c].apply(lambda x: (0 if x == 0 or x> 100 else 1 / x))
        elif r =='Top1':
            a[c] = a[c].apply(lambda x: (0 if x == 0 or x> 1 else 1 / x))
        elif r =='Top50':
            a[c] = a[c].apply(lambda x: (0 if x == 0 or x> 50 else 1 / x))
        elif r =='Top200':
            a[c] = a[c].apply(lambda x: (0 if x == 0 or x> 200 else 1 / x))

        else:
            a[c] = a[c].apply(lambda x: (0 if x ==0 else 1/x))
    a.fillna(0,inplace=True)
    # plotBox(a,typ+granularity+r+'.pdf')
    # plotBoxInverted(a,typ+granularity+r+'.pdf')
    return a
def getTops():
    typs = ['GZoltar','GZoltar16']

    for typ in typs:
        granularities = ['bugsM','bugsF','bugsL']
        rs = ['Top1','Top10','Top100','Any']

        for granularity in granularities:
            for r in rs:
                print(granularity,r)
                a = tops(typ,granularity,r)
                p.dump()

def checkTruthValue(x,granularity):
    x
    cs = [i for i in x.index if i.startswith(granularity)]
    tl = []
    for c in cs:
        tl.append(x[c])
    rtl = np.any(tl)
    return rtl
def printNotFound(typ,granularity,r):
    DATA_HOME = '/Users/anilkoyuncu/projects/fL/Data/'
    if typ == 'GZoltar16':
        SUSP_HOME = 'BugPositionsGZoltar'
    else:
        SUSP_HOME = 'BugPositions'



    test =pd.read_csv(join(DATA_HOME,SUSP_HOME,granularity+".csv"))
    if typ=='GZoltar16':
        a = test[['Proj','tarantula','ochiai','dstar2','barinel','opt2','muse','jaccard']]
        a.rename(columns={'tarantula': 'Tarantula', 'ochiai': 'Ochiai','dstar2':'DStar','barinel':'Barinel','opt2':'Naish2','muse':'Muse','jaccard':'Jaccard'},inplace=True)
    else:
        # a = test[test.columns[:-2]]
        a = test[['Proj','Tarantula','Ochiai','DStar','Barinel','Naish2','Muse','Jaccard']]


    a.replace('\s*-', 0,inplace=True)

    granularity = granularity.replace('bugs','')
    columns = a.columns[1:]
    for c in columns:
        a[c] = a[c].apply(pd.to_numeric, errors='coerce')
        a[c].fillna(0, inplace=True)
        # a['Top100'+granularity+c] = a[c].apply(lambda x:  False if(x == 0 or x > 100) else True)
        # a['Top1'+granularity+c] = a[c].apply(lambda x: False if (x == 0 or x > 1) else True)
        # a['Top10'+granularity+c] = a[c].apply(lambda x: False if (x == 0 or x > 10) else True)
        # a['Any' +granularity+ c] = a[c].apply(lambda x: False if (x == 0 ) else True)

    # a['Top100'+granularity] = a.apply(lambda x:checkTruthValue(x,'Top100' + granularity),axis=1)
    # a['Top10'+granularity] = a.apply(lambda x: checkTruthValue(x, 'Top10' +granularity), axis=1)
    # a['Top1'+granularity] = a.apply(lambda x: checkTruthValue(x,'Top1'+ granularity ), axis=1)
    # a['Any'+granularity] = a.apply(lambda x: checkTruthValue(x, 'Any'+ granularity ), axis=1)
    a['typ'] = typ
    a.rename(columns={'Tarantula': granularity+'Tarantula', 'Ochiai': granularity+'Ochiai', 'DStar': granularity+'DStar', 'Barinel': granularity+'Barinel',
                      'Naish2': granularity+'Naish2', 'Muse': granularity+'Muse', 'Jaccard': granularity+'Jaccard'}, inplace=True)
    # cols =[i for i in a.columns if not (i in ['Tarantula','Ochiai','DStar','Barinel','Naish2','Muse','Jaccard','tarantula','ochiai','dstar2','barinel','opt2','muse','jaccard'])]
    # return a[cols]
    return a
        # a['Top10'] = a.apply(lambda x: print(x['Proj']) if x[c] == 0 or x[c] > 10 else None, axis=1)


def calculateFL():
    # drawBoxPlots()
    typs = ['GZoltar','GZoltar16']

    t = []

    for typ in typs:
        l = []
        granularities = ['bugsM','bugsF','bugsL']
        rs =['Any'] #['Top1','Top10','Top100','Any']

        for granularity in granularities:
            for r in rs:
                print(granularity,r)
                a = printNotFound(typ,granularity,r)
                l.append(a)
                print('*****************')
        l
        res = pd.merge(l[0], l[1], on=['Proj', 'typ'])
        t1 = pd.merge(res,l[2], on=['Proj', 'typ'])
        p.dump(t1, open(typ+".pickle", "wb"))


flAlgo = ['Tarantula','Ochiai','DStar','Barinel','Naish2','Muse','Jaccard']
if __name__ == '__masin__':
    calculateFL()
    typs = ['GZoltar', 'GZoltar16']
    g1 = pd.read_pickle(typs[0]+".pickle")
    g2 = pd.read_pickle(typs[1]+".pickle")
    g1min = g1[[i for i in g1.columns if i.startswith('Any') or i == 'Proj']]
    g2min = g2[[i for i in g2.columns if i.startswith('Any') or i == 'Proj']]

    g1min.to_csv(typs[0]+'min.csv')
    g2min.to_csv(typs[1]+'min.csv')


    g1.to_csv(typs[0]+'.csv')
    g2.to_csv(typs[1] + '.csv')
    g2


import re

from natsort import natsorted, index_natsorted, order_by_index


# aprBugs = ['Chart_1','Chart_3','Chart_4','Chart_5','Chart_7','Chart_8','Chart_9','Chart_11','Chart_12','Chart_13','Chart_14','Chart_15','Chart_17','Chart_18','Chart_19','Chart_20','Chart_21','Chart_22','Chart_24','Chart_25','Chart_26','Closure_5','Closure_10','Closure_14','Closure_18','Closure_31','Closure_33','Closure_38','Closure_40','Closure_51','Closure_57','Closure_62','Closure_63','Closure_70','Closure_73','Closure_79','Closure_106','Closure_115','Closure_125','Closure_126','Lang_6','Lang_7','Lang_10','Lang_16','Lang_21','Lang_24','Lang_26','Lang_27','Lang_33','Lang_35','Lang_38','Lang_39','Lang_41','Lang_43','Lang_44','Lang_45','Lang_46','Lang_50','Lang_51','Lang_53','Lang_55','Lang_57','Lang_58','Lang_59','Lang_60','Lang_61','Lang_63','Math_1','Math_2','Math_3','Math_4','Math_5','Math_6','Math_8','Math_10','Math_20','Math_22','Math_25','Math_28','Math_30','Math_32','Math_33','Math_34','Math_35','Math_40','Math_41','Math_42','Math_49','Math_50','Math_53','Math_57','Math_58','Math_59','Math_61','Math_63','Math_65','Math_69','Math_70','Math_71','Math_72','Math_73','Math_75','Math_78','Math_79','Math_80','Math_81','Math_82','Math_84','Math_85','Math_87','Math_88','Math_89','Math_90','Math_93','Math_95','Math_97','Math_98','Math_99','Math_104','Math_105','Time_4','Time_7','Time_11','Time_15','Time_19']
# aprBugs = ['Chart_1','Chart_3','Chart_5','Chart_7','Chart_8','Chart_9','Chart_11','Chart_12','Chart_13','Chart_14','Chart_15','Chart_17','Chart_18','Chart_19','Chart_20','Chart_21','Chart_22','Chart_24','Chart_25','Chart_26','Closure_5','Closure_10','Closure_14','Closure_18','Closure_31','Closure_33','Closure_40','Closure_51','Closure_57','Closure_62','Closure_63','Closure_70','Closure_73','Closure_79','Closure_106','Closure_115','Closure_125','Closure_126','Lang_6','Lang_7','Lang_10','Lang_16','Lang_21','Lang_24','Lang_26','Lang_27','Lang_33','Lang_35','Lang_38','Lang_39','Lang_41','Lang_43','Lang_44','Lang_45','Lang_46','Lang_50','Lang_51','Lang_53','Lang_55','Lang_57','Lang_58','Lang_59','Lang_60','Lang_61','Lang_63','Math_1','Math_2','Math_3','Math_4','Math_5','Math_6','Math_8','Math_20','Math_22','Math_25','Math_28','Math_30','Math_32','Math_33','Math_34','Math_35','Math_40','Math_41','Math_42','Math_49','Math_50','Math_53','Math_57','Math_58','Math_59','Math_61','Math_63','Math_65','Math_69','Math_70','Math_71','Math_72','Math_73','Math_75','Math_78','Math_79','Math_80','Math_81','Math_82','Math_84','Math_85','Math_87','Math_88','Math_89','Math_90','Math_93','Math_95','Math_97','Math_98','Math_99','Math_104','Math_105','Time_4','Time_7','Time_11','Time_15','Time_19']


tools = ['Buggy Project','FixMiner','LSRepair','SimFix','CapGen','SketchFix','JAID','ssFix','ACS','ELIXIR','HDRepair','jGenProg','jKali','jMutRepair','Nopol']

correct = '✓'
plausible = '✗'
alsoPlausible= '(✓)'

def add2dict(aDict,k,v):
    if k in aDict:
        tmp = aDict[k]
        tmp.append(v)
        aDict[k] = tmp
    else:
        tmp = []
        tmp.append(v)
        aDict[k] = tmp
    return aDict

def patchType(x,c,correctPatches,plausiblePatches):
    p = x[c]
    id = x['Proj']
    if p == correct:
        # add2dict(correctPatches,c,id)
        correctPatches.add(id)
    elif p == plausible or p == alsoPlausible:
        # add2dict(plausiblePatches, c, id)
        plausiblePatches.add(id)
    # else:
    #     add2dict(wrong, c, id)
    id

# def exporterMain():
#
#     t = pd.read_excel('/Users/anilkoyuncu/bugStudy/documentation/semanticpattern/APR Tools.xlsx',sheet_name='Sheet2')
#     t = t[tools]
#     t.fillna('', inplace=True)
#     t.rename(columns={'Buggy Project':'Proj'},inplace=True)
#     #
#     #
#     correctPatches = set()
#     plausiblePatches = set()
#     # wrong = set()
#     for c in tools[1:]:
#         t.apply(lambda x: patchType(x,c,correctPatches,plausiblePatches),axis=1 )
#     #
#     #
#     #
#     cpPatches = correctPatches.union(plausiblePatches)
#     set(aprBugs).difference(cpPatches)
#     typs = ['GZoltar', 'GZoltar16']
#     g2 = pd.read_pickle(typs[0] + ".pickle")
#     #
#     #
#     # for idx,p in enumerate([correctPatches,plausiblePatches,wrong]):
#     #     for k,v in p.items():
#     #         aDf=  g2[g2['Proj'].isin(v)]
#     #
#     #         p.dump(aDf, open(k + str(idx)+".pickle", "wb"))
#
#
#
#
#
#     cols = ['Proj', 'F' + flAlgo[0], 'M' + flAlgo[0], 'L' + flAlgo[0],
#                     'F' + flAlgo[1], 'M' + flAlgo[1], 'L' + flAlgo[1],
#                     'F' + flAlgo[2], 'M' + flAlgo[2], 'L' + flAlgo[2],
#                     'F' + flAlgo[3], 'M' + flAlgo[3], 'L' + flAlgo[3],
#                     'F' + flAlgo[4], 'M' + flAlgo[4], 'L' + flAlgo[4],
#                     'F' + flAlgo[5], 'M' + flAlgo[5], 'L' + flAlgo[5],
#                     'F' + flAlgo[6], 'M' + flAlgo[6], 'L' + flAlgo[6]]
#
#
#     a = g2[cols]
#
#     t1 = pd.merge(t, a, on=['Proj'])
#
#     corrects = t1[t1['Proj'].isin(correctPatches)]
#     plausibles = t1[t1['Proj'].isin(plausiblePatches)]
#     wrongs = t1[~t1['Proj'].isin(cpPatches)]
#
#     p.dump(corrects, open("corrects.pickle", "wb"))
#     p.dump(plausibles, open("plausibles.pickle", "wb"))
#     p.dump(wrongs, open("wrongs.pickle", "wb"))
#
#     a = t1[t1['Proj'].isin(aprBugs)]
#
#     # a.replace(True,"\\cmark",inplace=True)
#     # a.replace(False, "\\xmark", inplace=True)
#     a = a.reindex(index=order_by_index(a.index, index_natsorted(a.Proj)))
#     # a.sort_values('Proj',inplace=True)
#     with open('mytable.tex', 'w') as tf:
#         tf.write(a.to_latex(index=False))
#     # g2[g2['AnyMtarantula'] == True].Proj


def plot4patch(a,fn):
    columns = a.columns[1:]
    for c in columns:
            a[c] = a[c].apply(lambda x: (0 if x ==0 else 1/x))

    plotBox(a, fn + '.pdf')


def normalizeResults(a):
    columns = a.columns[1:]
    for c in columns:
            a[c] = a[c].apply(lambda x: (0 if x ==0 else 1/x))
    return a

def plotFinal():
    # exporterMain()
    corrects = pd.read_pickle("corrects.pickle")
    plausibles = pd.read_pickle("plausibles.pickle")
    wrongs = pd.read_pickle("wrongs.pickle")

    print(len(corrects), len(plausibles), len(wrongs))

    cols = ['Proj','F' + flAlgo[1] + '_x', 'L' + flAlgo[1]+ '_x', 'M' + flAlgo[1]+ '_x']

    # plot4patch(corrects[cols],'PatchesCorrect')
    # plot4patch(plausibles[cols], 'PatchesPlausible')
    # plot4patch(wrongs[cols], 'PatchesWrong')

    corrects = normalizeResults(corrects[cols])
    plausibles = normalizeResults(plausibles[cols])
    wrongs = normalizeResults(wrongs[cols])




    colNames= ['T','Correct','Plausible','Wrong' ]

    bugs = ['F','M','L']

    res = pd.DataFrame(columns=colNames)
    idx = 0
    # rank = rank[rank.bugID.str.startswith('CAMEL')]
    for bug in bugs:
        valList = []

        for col in colNames[1:]:

            if col ==colNames[1]:
                valList.append(corrects[bug+ flAlgo[1]+ '_x'].values)

            elif col == colNames[2]:
                valList.append(plausibles[bug + flAlgo[1]+ '_x'].values)


            elif col == colNames[3]:
                valList.append(wrongs[bug + flAlgo[1]+ '_x'].values)
        valList.insert(0, bug)
            # print((valList))
        res.loc[idx] = valList
        idx += 1
    plotBoxMulti(res)

def exportTable():
    t = pd.read_excel('documentation/APR Tools.xlsx', sheet_name='Sheet2')
    t = t[tools]
    t.fillna('', inplace=True)
    t.rename(columns={'Buggy Project': 'Proj'}, inplace=True)
    #
    #
    correctPatches = set()
    plausiblePatches = set()
    # wrong = set()
    for c in tools[1:]:
        t.apply(lambda x: patchType(x, c, correctPatches, plausiblePatches), axis=1)

    cpPatches = correctPatches.union(plausiblePatches)

    print(len(cpPatches))

    # set(aprBugs).difference(cpPatches)
    typs = ['GZoltar', 'GZoltar16']
    g1 = pd.read_pickle(typs[0] + ".pickle")
    g2 = pd.read_pickle(typs[1] + ".pickle")

    cols = ['Proj', 'F' + flAlgo[1], 'M' + flAlgo[1], 'L' + flAlgo[1]]

    m = pd.merge(g1[cols], g2[cols], on=['Proj'])

    t1 = pd.merge(t, m, on=['Proj'])

    corrects = t1[t1['Proj'].isin(correctPatches)]

    plausibles = t1[t1['Proj'].isin(plausiblePatches)]
    wrongs = t1[~t1['Proj'].isin(cpPatches)]

    print(len(corrects),len(plausibles),len(wrongs))

    p.dump(corrects, open("corrects.pickle", "wb"))
    p.dump(plausibles, open("plausibles.pickle", "wb"))
    p.dump(wrongs, open("wrongs.pickle", "wb"))

    a = t1[t1['Proj'].isin(cpPatches)]

    # a.replace(True,"\\cmark",inplace=True)
    # a.replace(False, "\\xmark", inplace=True)
    a = a.reindex(index=order_by_index(a.index, index_natsorted(a.Proj)))
    # a.sort_values('Proj',inplace=True)
    with open('mytable.tex', 'w') as tf:
        tf.write(a.to_latex(index=False))

    localizable = a[a['LOchiai_x'] != 0]

    colList = localizable.columns.tolist()
    colList.remove('F' + flAlgo[1] + '_x')
    colList.remove('M' + flAlgo[1] + '_x')
    colList.remove('F' + flAlgo[1] + '_y')
    colList.remove('M' + flAlgo[1] + '_y')
    localizable = localizable[colList]
    with open('localizable.tex', 'w') as tf:
        tf.write(localizable.to_latex(index=False))

    nonlocalizable = a[a['LOchiai_x'] == 0]

    nonlocalizable = nonlocalizable[colList]
    with open('nonlocalizable.tex', 'w') as tf:
        tf.write(nonlocalizable.to_latex(index=False))

    localizable.replace(alsoPlausible, 0, inplace=True)
    localizable.replace(plausible, 0, inplace=True)
    localizable.replace(correct, 1, inplace=True)
    localizable.to_csv('localizable.csv')






if __name__ == '__main__':
    # #begin calculate tops
    # calculateFL()
    # typs = ['GZoltar', 'GZoltar16']
    # g1 = pd.read_pickle(typs[0] + ".pickle")
    # g2 = pd.read_pickle(typs[1] + ".pickle")
    #
    # g1.to_csv(typs[0]+'.csv')
    # g2.to_csv(typs[1] + '.csv')
    # #end tops
    # plotFinal()
    exportTable()
