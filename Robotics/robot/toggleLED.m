function toggleLED(s,LED,onoff)

command = 2;

if (nargin == 3)
    command = onoff;
end

fprintf(s,['L,' num2str(LED) ',' num2str(command)]);
pause(0.001);
fscanf(s);

end