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
		noise=float(fileName[fileName.find('epochs_')+len('epochs_'):fileName.find('noise')])
		data=list(csv.reader(open(fileName),delimiter=','))
		data=np.array(data[1:len(data[0])-1]).astype('float')
		avgTravelTime=np.average(data[:,2])
		stdevTravTime=np.std(data[:,2])
		#if(avgs[4]<100):
		outData2.append([noise*100,avgTravelTime])
		#if noise not in outData:
		#	outData[noise]=[]
		#outData[noise].append(avgTravelTime)
	#newoutdata=[]
	"""
	for runs,data in sorted(outData.iteritems()):
		avgs=np.median(np.array(data),axis=0)
		stds=np.std(np.array(data),axis=0)
		newoutdata.append(np.concatenate(([runs],avgs,stds),axis=0))
	return np.array(newoutdata)
	"""
	#return outData
	return np.array(outData2)

#dataHuman=getDataInFolder('./naive')
dataLearnSafeOff=getDataInFolder('./safeoffdata')
dataLearnSafeOn=getDataInFolder('./safeondata')

seriesSafeOff = pd.DataFrame(np.array([dataLearnSafeOff[:,1],dataLearnSafeOn[:,1]]).T,columns=['Learned safe behaviour','Enforced safe behaviour'])
seriesSafeOff['minimal amount of random actions (%)'] = pd.Series(dataLearnSafeOff[:,0])
ax = seriesSafeOff.boxplot(by='minimal amount of random actions (%)')[0]
ax.set_ylabel('Average travel time in seconds')

plt.suptitle('Influence of noise in repeated experiments')
plt.show()