import sys
import csv
import glob
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.ticker as tkr
from mpl_toolkits.mplot3d import Axes3D as A3D

allData=dict()
print sys.argv
data=list(csv.reader(open(sys.argv[1]),delimiter=',')) # get data
name=sys.argv[2]
# Get run number
for i in range(len(data[0])):
	title=data[0][i]
	value=float(data[1][i])
	# Get run number
	runNum=title[title.find(name+'-'):title.find('.')]
	runNum=int(runNum[runNum.find('-')+1:len(runNum)])
	
	# Get node number (unecessary)
	#nodNum=int(title[title.find('[')+1:title.find(']')])
	
	# Insert into data
	if runNum not in allData: # Run not yet seen
		allData[runNum] = []
	allData[runNum].append(value)

allData = [v for k,v in sorted(allData.iteritems())]
data=np.array(allData)
data2=data.T
max=np.max(data)

"""
data=np.array(data[1:len(data[0])])	# remove column titles
data=data[:,1:len(data[0])].astype('float')	# remove row titles
rows=np.shape(data)[0] # get number of rows
cols=np.shape(data)[1]/rows # get number of columns (have to divide by rows because of all the nans!)
data=data[~np.isnan(data)]	# remove NaNs
data=data.reshape(rows,cols).T # get it back into the original form
"""
fig = plt.figure()
t=plt.title('Traveltimes throughout learning')
ax = fig.add_subplot(111)
ax.set_ylim(ymax=np.shape(data2)[0])
ax.set_xlabel('Number of successive runs')
ax.set_ylabel('Car indeces (higher == created later in each run)')
ticks_at = [0, max]
cax = ax.imshow(data2, interpolation='none')
cbar = fig.colorbar(cax,format=tkr.ScalarFormatter())
t.set_y(1.75)

fig2=plt.figure()
ax2=fig2.add_subplot(111)
ax2.plot(range(len(data)),np.average(data,axis=1))
ax2.set_ylim(ymin=0,ymax=(max*1.05))
ax2.set_xlabel('Number of successive runs')
ax2.set_ylabel('Average total travel time')

#
#plt.subplots_adjust(top=0.86,left=0)

plt.show()
