import sys
import csv
import glob
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.ticker as tkr
from mpl_toolkits.mplot3d import Axes3D as A3D

def getData(pathAndFile,configName):
	allData=dict()
	print sys.argv
	data=list(csv.reader(open(pathAndFile),delimiter=',')) # get data
	#print(len(data[0]))
	#print(len(data[1]))
	# Get run number
	for i in range(len(data[0])):
		title=data[0][i]
		value=float(data[1][i])
		# Get run number
		runNum=title[title.find(configName + '-'):title.find('.')]
		runNum=int(runNum[runNum.find('-')+1:len(runNum)])
		# Insert into data
		if runNum not in allData: # Run not yet seen
			allData[runNum] = []
		allData[runNum].append(value)
	return np.array([v for k,v in sorted(allData.iteritems())])

thisData = getData('./totaltime-1.csv','collLearnTest3')
baseLine = getData('../findingTrafficJamPoint/3car/totaltime/totaltime-1.csv','testSpawnRateConfig7')

# Average travel time of normal cars over x repeated runs (5 in this case)
# Taken as a constant baseline
baseLine = [np.average(baseLine)]*len(thisData)

print baseLine[0]
print np.average(thisData[len(thisData)-1])


fig=plt.figure()
"""
ax2=fig.add_subplot(121,title='Comparison')
l1, = ax2.plot(range(len(thisData)),np.average(thisData,axis=1))
l2, = ax2.plot(range(len(baseLine)),baseLine)
ax2.set_ylim(ymin=0)#,ymax=(np.max(data)*1.05))
ax2.set_xlabel('Learning epochs')
ax2.set_ylabel('Average total travel time in seconds')
ax2.legend([l1,l2],['Learning cars','Human drivers'],loc=3) #,numpoints=1,bbox_to_anchor=(1.05, 1), loc=2, borderaxespad=0.
"""
#fig=plt.figure()
ax3=fig.add_subplot(111,title='Detailed view')
l1, = ax3.plot(range(len(thisData)),np.average(thisData,axis=1))
ax3.set_ylim(ymin=70,ymax=73)#,ymax=(np.max(data)*1.05))
ax3.set_xlabel('Learning epochs')
#ax2.set_ylabel('Average total travel time')
ax3.legend([l1],['Learning cars'],loc=0) #,numpoints=1,bbox_to_anchor=(1.05, 1), loc=2, borderaxespad=0.
#fig.set_xlabel('test')


#
#plt.subplots_adjust(top=0.86,left=0)

plt.show()
