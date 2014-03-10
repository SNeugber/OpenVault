import sys
import numpy as np
import subprocess
import networkx as nx
import matplotlib.pyplot as plt

out=subprocess.check_output(['tail', '-4', sys.argv[1]])
lines=out.strip().split('\n')
node = int(sys.argv[2])

### Net structure ###
netstruct=map(int,(lines[0][lines[0].find('=')+1:len(lines[0])]).split())
#print "Neural Network Structure: [",
offset = 0
netOffsets=[]
for x in netstruct:
	#sys.stdout.write(str(offset)+".."+str(x+offset-1))
	netOffsets.append(x+offset-1)
	#if x != netstruct[len(netstruct)-1]:
	#	sys.stdout.write(",")
	offset+=x
#print "]"

biasNodes=[0]
for i in range(len(netOffsets)):
	if i < len(netOffsets)-2:
		biasNodes.append(netOffsets[i]+1)

### Neurons ###
neurons=lines[2].split(') (')
neurons[0]=neurons[0][neurons[0].find('=')+2:len(neurons[0])] # remove description at the beginning
neurons[len(neurons)-1]=neurons[len(neurons)-1].strip(') \r') # remove the last bracket
neurons = [map(float,x.split(', '))for x in neurons] # fonvert to pairs of floats

### Connections ###
connections=lines[3].split(') (')
connections[0]=connections[0][connections[0].find('=')+2:len(connections[0])] # remove description at the beginning
connections[len(connections)-1]=connections[len(connections)-1].strip(')') # remove the last bracket
connections = [map(float,x.split(', '))for x in connections] # fonvert to pairs of floats
numConnections = dict()

net=nx.Graph()
count=0
netstruct[0]-=1

net.add_nodes_from(range(17),bipartite=0)
net.add_nodes_from(range(17,34),bipartite=1)
net.add_nodes_from(range(34,42),bipartite=2)

currentNode = 18
lastEdge = -1
edgeCols=[]
minW = 0
maxW = 0
edges=[]

for c,w in connections:
	#We have wrapped around, need to move to next node
	if(c == 0 and lastEdge == 16) or (c == 17 and lastEdge == 33):
		currentNode+=1
	elif (c == 17 and lastEdge == 16):
		currentNode+=1
	# Connection between c & currentNod
	#if(c==5 or c > 16):
	edges.append([c,currentNode,{'weight':w}])
	#net.add_edge(c,currentNode,weight=w,width=8)
	if(w < minW): minW = w
	if(w > maxW): maxW = w
	lastEdge=c

darkblueinv=[1.0-(80.0/255.0),1.0-(80.0/255.0),1.0-(150.0/255.0)]
darkgreeninv=[1.0-(70.0/255.0),1.0-(120.0/255.0),1.0-(70.0/255.0)]
for edge in edges:
	c=0
	w=edge[2]['weight']
	width=5.0
	if w < 0:
		c=w/minW
		width*=c*c
		c=[1-x*c for x in darkgreeninv]
	else:
		c=w/maxW
		width*=c*c
		c=[1-x*c for x in darkblueinv]
	edge[2]['color']=c
	edge[2]['width']=width

net.add_edges_from(edges)

### Calculate values given the node to light up ###

level = 0
for i in range(len(netOffsets)):
	if node <= netOffsets[i]:
		level = i
		break

maxVal=-1
minVal=1
absMax=0
nodeVals=dict()
for layer in range(level,len(netOffsets)):
	if(layer == 0):
		# bla
		for n in range(netOffsets[layer]+1):
			if n == node:
				nodeVals[n]=1
			else:
				nodeVals[n]=0
			nodeVals[0]=1 # bias
	else:
		if layer == level:
			for n in range(netOffsets[layer-1]+1,netOffsets[layer]+1):
				if n == node:
					nodeVals[n]=1
				else:
					nodeVals[n]=0
				nodeVals[netOffsets[layer-1]+1]=1 #bias unit
		else:
			for n in range(netOffsets[layer-1]+1,netOffsets[layer]+1):
				if n < len(net.nodes()):
					thisVal = sum([v['weight']*nodeVals[k] for (k,v) in net[n].iteritems() if k <= netOffsets[layer-1]])
					#if(sum([1 for (k,v) in net[n].iteritems() if k <= netOffsets[layer-1]]) == 0):
					#	thisVal=1 # bias unit
					if(thisVal < minVal): minVal=thisVal
					if(thisVal > maxVal): maxVal=thisVal
					
					if(n in biasNodes): thisVal=1
					nodeVals[n]=thisVal

absMax=max(abs(minVal),abs(maxVal))
### PLOTTING ###

#print minVal, maxVal

fig=plt.figure("Node: " + str(node))
t=plt.title("Activation of node: "+str(node)+"\n")
pos=dict(zip(range(17),zip(range(17),[2]*17))) # upper nodes
pos.update(dict(zip(range(17,34),zip(range(17),[1]*17)))) # lower nodes
pos.update(dict(zip(range(34,42),zip(range(4,12),[0]*8)))) # lower nodes
#edge_color=['r']*len(net.edges())
#edge_color=colors
#edge_color=colors,width=widths
colors=[net[k][v]['color'] for k,v in net.edges()]
widths=[net[k][v]['width'] for k,v in net.edges()]
nodeCols=[]
for k in net.nodes():
	if k not in nodeVals:
		nodeCols.append([0,0,0])
	elif k == node or k in biasNodes:
		nodeCols.append([0,1,0])
	elif nodeVals[k] < 0: #red
		nodeCols.append([-nodeVals[k]/absMax,0,0])
	else: #blue
		nodeCols.append([0,nodeVals[k]/absMax,0])
nx.draw(net,pos,node_color=nodeCols)
plt.show()




"""
for c,w in connections:
	if c in numConnections:
		numConnections[c]+=1
	else: numConnections[c]=1
for c,v in sorted(numConnections.iteritems()):
	print c,v


#print connections
#for line in sys.stdin:
#	print line

nnFile=open(sys.argv[1])
nnFile[26]
num_lines = sum(1 for line in nnFile)
print num_lines
"""

"""
lostPacketsPerNodeAvgs=[]
lostPacketsPerNodeStds=[]
data=list(csv.reader(open(sys.argv[1]),delimiter=',')) # get data
data=np.array(data[1:len(data[0])])	# remove column titles
data=data[:,1:len(data[0])].astype('float')	# remove row titles
rows=np.shape(data)[0] # get number of rows
cols=np.shape(data)[1]/rows # get number of columns (have to divide by rows because of all the nans!)
data=data[~np.isnan(data)]	# remove NaNs
data=data.reshape(rows,cols).T # get it back into the original form

fig = plt.figure()
t=plt.title('Total number of lost packets for each node, throughout learning')
ax = fig.add_subplot(111)
ax.set_ylim(ymax=np.shape(data)[0])
ax.set_xlabel('Number of successive runs')
ax.set_ylabel('Car indeces (higher == created later in each run)')

ticks_at = [0, abs(data).max()]
cax = ax.imshow(data, interpolation='none')
cbar = fig.colorbar(cax,format=tkr.ScalarFormatter())

t.set_y(1.09)
plt.subplots_adjust(top=0.86,left=0)

plt.show()

"""