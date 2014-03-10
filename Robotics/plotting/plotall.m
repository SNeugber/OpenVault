function [ ] = plotall( robot, particles, map, bestPose )
%PLOTALL Summary of this function goes here
%   Detailed explanation goes here

x = robot.position(1);
y = robot.position(2);
robotR = 30;
sensorR = robotR + 70;
ang=0:0.01:2*pi; 
xpSensor = sensorR*cos(ang);
ypSensor = sensorR*sin(ang);
xpRobot = robotR*cos(ang);
ypRobot = robotR*sin(ang);

plotparticles     % plots particles
plot(robot.position(1), robot.position(2) ,'ro');    % plots actual position of the robot
plot(x+xpRobot,y+ypRobot, 'r');
plot(x+xpSensor,y+ypSensor, 'g');
direc = robot.position + robot.direction * 30;
plot([robot.position(1) direc(1)], [robot.position(2) direc(2)], 'k');
plot(bestPose.position(1), bestPose.position(2), 'r*');
bestDirec = bestPose.position+bestPose.direction*50;
plot([bestPose.position(1) bestDirec(1)], [bestPose.position(2) bestDirec(2)], 'r');

end