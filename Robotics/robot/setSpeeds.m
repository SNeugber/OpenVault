function setSpeeds(s,leftSpeed,rightSpeed)
fprintf(s,['D,' num2str(leftSpeed) ',' num2str(rightSpeed)]);
pause(0.001);
fscanf(s);
end

