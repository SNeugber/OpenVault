import csv
import glob
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.ticker as tkr

def getDataInFolder(dir):
	outData=[]
	for fileName in glob.glob(dir+'/*.csv'):
		data=list(csv.reader(open(fileName),delimiter=','))
		data=np.array(data[1:len(data[0])-1]).astype('float')
		avgs=np.average(data,axis=0)
		stdev=np.std(data[:,4])
		#if(avgs[4]<100):
		outData.append([int(avgs[2]),avgs[4],stdev])
	return np.array(sorted(outData))

dataHuman=getDataInFolder('./naive')
dataLearnSafeOff=getDataInFolder('./nndistlearn/safeOff')
#dataLearnSafeOn=getDataInFolder('./nndistlearn/safeOn')

fig = plt.figure()
plt.title('Length of traffic jams measured as average stationary time')
plt.figtext(.05,.05,'Cars are inserted into every lane at the same time, at the frequency given on the x-axis.\nA total of 20 cars are inserted, all driving uniformly at 40km/h')
plt.gca().set_position((.1,.22,.65,.7))
ax = fig.add_subplot(111)
l1=ax.errorbar(dataHuman[:,0],dataHuman[:,1],dataHuman[:,2],fmt='bo')
l2=ax.errorbar(dataLearnSafeOff[:,0],dataLearnSafeOff[:,1],dataLearnSafeOff[:,2],fmt='ro')
#l3=ax.errorbar(dataLearnSafeOn[:,0],dataLearnSafeOn[:,1],dataLearnSafeOn[:,2],fmt='yo')
ax.set_ylim(ymin=-10)
ax.set_xlabel('Spawning frequency (s)')
ax.set_ylabel('Average time a car is stationary (s)')
ax.set_xscale('log')
#ax.legend([l1,l2,l3],['Human drivers','Learning cars, safety off','Learning cars, safety on'],numpoints=1,bbox_to_anchor=(1.05, 1), loc=2, borderaxespad=0.)
ax.legend([l1,l2],['Human drivers','Learning cars, safety off'],numpoints=1,bbox_to_anchor=(1.05, 1), loc=2, borderaxespad=0.)
ax.xaxis.set_major_formatter(tkr.ScalarFormatter())
plt.show()
