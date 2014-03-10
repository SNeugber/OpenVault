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
configName = sys.argv[2]
#print(len(data[0]))
#print(len(data[1]))
# Get run number
for i in range(len(data[0])):
	title=data[0][i]
	value=float(data[1][i])
	# Get run number
	runNum=title[title.find(configName + '-'):title.find('.')]
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
t1=plt.title('Collisions throughout learning by vehicle')
ax = fig.add_subplot(111)
ax.set_ylim(ymax=np.shape(data2)[0])
ax.set_xlabel('Epoch')
ax.set_ylabel('Car indeces (higher == created later in each run)')
ticks_at = [0, max]
cax = ax.imshow(data2, interpolation='none')
cbar = fig.colorbar(cax,format=tkr.ScalarFormatter(),ticks=range(0,3))
#t.set_y(1.75)

fig2=plt.figure()
ax2=fig2.add_subplot(111)
#plt.gca().set_position((.1,.22,.65,.7))
t=plt.title('Number of collisions throughout learning')
ax2.plot(range(len(data)),np.sum(data,axis=1))
ax2.set_ylim(ymin=0,ymax=(np.max(np.sum(data,axis=1))*1.05))
ax2.set_xlabel('Epoch')
ax2.set_ylabel('Number of vehicles that have collided')
#plt.figtext(.05,.05,'(Odd numbers due to vehicles potentially involved in multiple collisions)')


plt.show()
