import csv
import glob
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.ticker as tkr
import pandas as pd

def getDataInFolder(dir):
	outData=dict()
	outData2=[]
	for fileName in glob.glob(dir+'/*.csv'):
		numruns=int(fileName[fileName.find('_')+1:fileName.find('epochs')])
		data=list(csv.reader(open(fileName),delimiter=','))
		data=np.array(data[1:len(data[0])-1]).astype('float')
		avgTravelTime=np.average(data[:,2])
		stdevTravTime=np.std(data[:,2])
		outData2.append([int(numruns),avgTravelTime])
	return np.array(outData2)

dataLearnSafeOff=getDataInFolder('./safeoffdata')
dataLearnSafeOn=getDataInFolder('./safeondata')

seriesSafeOff = pd.DataFrame(dataLearnSafeOff[:,1],columns=['Learned safe behaviour'])
seriesSafeOn = pd.DataFrame(dataLearnSafeOn[:,1],columns=['Enforced safe behaviour'])
seriesSafeOff['number of learning epochs'] = pd.Series(dataLearnSafeOff[:,0])
seriesSafeOn['number of learning epochs'] = pd.Series(dataLearnSafeOn[:,0])


ax = seriesSafeOff.boxplot(by='number of learning epochs')
ax.set_ylabel('Average travel time in seconds')

#plt.suptitle('Influence of learning time on learning performance in repeated experiments')
plt.suptitle('')
ax = seriesSafeOn.boxplot(by='number of learning epochs')
ax.set_ylabel('Average travel time in seconds')

plt.suptitle('')

#plt.suptitle('Influence of learning time on learning performance in repeated experiments')
plt.show()