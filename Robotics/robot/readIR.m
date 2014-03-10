function sensorVals = readIR(s)
fprintf(s,'N');
pause(0.001);
sensorString = fscanf(s);
splitString = regexp(sensorString,',','split');
sensorVals = cellfun(@str2num,splitString(2:end));
end
