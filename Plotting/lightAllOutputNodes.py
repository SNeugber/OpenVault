import subprocess as sp

for i in range(34,42):
	sp.Popen("python lightOutputNode.py savedNNNoSpeedVar.fann "+str(i),shell=True)