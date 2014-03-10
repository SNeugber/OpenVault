import subprocess as sp

for i in range(1,17):
	sp.Popen("python lightNode.py savedNNNoSpeedVar.fann "+str(i),shell=True)