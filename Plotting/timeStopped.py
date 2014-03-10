import sys
import glob
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.ticker as tkr
from mpl_toolkits.mplot3d import Axes3D as a3
from matplotlib.collections import PolyCollection

# Functs
do_avg = np.vectorize(np.average)
total_stopped = np.vectorize(lambda x : sum([1 for i in x if i < 0.1])/10.0) # Number of 0.1s timesteps (hence the division) at zero speed


def getData(path,filename):
	# Read data from vector csvs
	vec_files = glob.glob(path+'/'+filename+'*.vec') # List of all vector files
	runs = sorted([int(f[f.find('-')+1:f.find('.')]) for f in vec_files]) # Run indeces of available vector files
	allData=[]
	for run in runs:
		nodeSpeedVectors=dict()
		nodeSpeeds=dict()
		f=open(path+'/'+filename+'-'+str(run)+'.vec')
		for line in f:
			if line.strip() == "": continue
			line=line.split()
			if len(line) < 4: continue
			if line[0]=='vector' and line[3]=='speed':
				node=int(line[2][line[2].find('[')+1:line[2].find(']')])
				vector=int(line[1])
				nodeSpeedVectors[vector]=node
				nodeSpeeds[node]=[]
			else:
				try:
					vector=int(line[0])
					if vector in nodeSpeedVectors:
						nodeSpeeds[nodeSpeedVectors[vector]].append(float(line[3]))
				except ValueError:
					pass
		allData.append([v for (k,v) in sorted(nodeSpeeds.iteritems())])
	return total_stopped(np.array(allData,dtype=object))

c1Data=getData('1car','testSpawnRateConfig5')
c2Data=getData('2car','testSpawnRateConfig6')
c3Data=getData('3car','testSpawnRateConfig7')
c3unevenData=getData('3car_uneven','testSpawnRateConfig7')
c4Data=getData('4car','testSpawnRateConfig8')
c5Data=getData('5cars','testSpawnRateConfig1')
c10Data=getData('10cars','testSpawnRateConfig2')
c50Data=getData('50cars','testSpawnRateConfig3')
c100Data=getData('100cars','testSpawnRateConfig4')

#print c5Data
avgs=[]
avgs.append(np.average(c1Data))
avgs.append(np.average(c2Data))
avgs.append(np.average(c3Data))
avgs.append(np.average(c3unevenData))
avgs.append(np.average(c4Data))
avgs.append(np.average(c5Data))
avgs.append(np.average(c10Data))
avgs.append(np.average(c50Data))
avgs.append(np.average(c100Data))

xs=[1,2,3,3.5,4,5,10,50,100]
#print xs
#print avgs

fig = plt.figure()
plt.suptitle('Length of traffic jams measured as average stationary time')
ax=fig.add_subplot(111)
l1=ax.scatter(xs,avgs)
ax.plot(xs,avgs)
ax.set_xscale('log')
ax.set_xlim(xmin=0.9,xmax=110)
ax.xaxis.set_major_formatter(tkr.ScalarFormatter())
ax.set_xlabel('Insertion frequency (s)')
ax.set_ylabel('Average stationary time (s)')


fig = plt.figure()
t=plt.title('Progression of stopping times over all cars')
ax = fig.add_subplot(111)
l1=ax.plot(np.average(c1Data,axis=1),label='1')
l2=ax.plot(np.average(c2Data,axis=1),label='2')
l3=ax.plot(np.average(c3Data,axis=1),label='3')
l9=ax.plot(np.average(c3unevenData,axis=1),label='3.5')
l4=ax.plot(np.average(c4Data,axis=1),label='4')
l5=ax.plot(np.average(c5Data,axis=1),label='5')
l6=ax.plot(np.average(c10Data,axis=1),label='10')
l7=ax.plot(np.average(c50Data,axis=1),label='50')
l8=ax.plot(np.average(c100Data,axis=1),label='100')
ax.set_xticks(range(0,np.shape(c5Data)[0]+1,10))
handles, labels = ax.get_legend_handles_labels()
ax.legend(handles, labels)
#ax.legend([l1,l2,l3,l4,l5,l6,l7,l8,l9],['1','2','3','4','5','10','50','100''3.5'],numpoints=1, loc=0, prop={'size':10})
#ax.set_yticks(range(0,200,10))
#ax.set_ylim(ymin=-10)


plt.show()