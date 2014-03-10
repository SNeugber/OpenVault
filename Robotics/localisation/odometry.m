function [ robot, stepmove, oldWheelCounts ] = odometry(s, oldrobot, oldWheelCounts)
%ODOMETRY Summary of this function goes here
%   Detailed explanation goes here

    xPos = oldrobot.position(1);
    yPos = oldrobot.position(2);
    angle = atan2(oldrobot.direction(2), oldrobot.direction(1));
    
    if angle < 0
        angle = 2*pi + angle;
    end
    
    % update Odometry
    % odo 1: Get wheel measurements
    wheelCounts = readCounts(s);
    wheelDeltaL = wheelCounts(1) - oldWheelCounts(1);
    wheelDeltaR = wheelCounts(2) - oldWheelCounts(2);
    oldWheelCounts(1) = wheelCounts(1);
    oldWheelCounts(2) = wheelCounts(2);

    % odo 2: calculate angle
    % φ ← φ + Δφ = φ - 0.5*(vleft - vright)/(widthfactor)
    % 2 x 40mm radius * 0.08mm per wheelcount != 'width factor' of 330, which I just found through trial and error
    angle = angle - 0.5*(wheelDeltaL - wheelDeltaR)/330;
    
    if(angle > 2*pi) 
        angle = angle - 2*pi; end
    if(angle < 0) 
        angle = angle + 2*pi; end
    
    % odo 3: calculate position
    % x ← x + Δx =x + 0.5*(vleft + vright) cos(φ)
    % y ← y + Δy =y + 0.5*(vleft + vright) sin(φ)
    xPos = xPos + 0.5*(wheelDeltaL + wheelDeltaR)*0.08 * cos(angle);
    yPos = yPos + 0.5*(wheelDeltaL + wheelDeltaR)*0.08 * sin(angle);
    
    
    % Save the new odometry
    robot.position = [xPos, yPos]';
    robot.direction = [cos(angle), sin(angle)]';
    
    % Calculate turnangle
    turnangle = 0.5*(wheelDeltaL - wheelDeltaR)/330;
    stepmove.speed = mydist(robot.position, oldrobot.position);
    stepmove.turn = turnangle;

end

