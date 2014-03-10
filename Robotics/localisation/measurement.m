function [ sensor ] = measurement( position , direction, map ,N)
%MEASUREMENT given the actual position and direction of the robot and the number of sensors returns
%the array of measurements. 
%   there are N sensors equaly angle-spaced, the angle between to adjacent
%   sensors is theta=2*pi/N
%   it uses the rotation operator 'trans' to rotate the direction of the
%   sensors.

%rng('shuffle');
theta=2*pi/N;     % defines the angle
trans=[cos(theta) sin(theta);     % defines the rotation operator
       -sin(theta) cos(theta)];
         

for n=1:N     % for all sensors
    sensor(n)=100000;            % initializes the measurement with a high value
    [a b]=size(map.polyline);       %gets the number of polylines
    for j=1:b         %for all polylines
    for i=1:length(map.polyline{j}.p1)     % for all segments of polylines
        %check to see if is there a intersection point
        [ intersec, distan ] = distance( position, direction, map.polyline{j}.p1(i,:)', map.polyline{j}.p2(i,:)' );
        
        %saves the smaller distance (closest objec that is in line with the sensor)
        if distan<sensor(n)
            sensor(n)=distan;            
        end
    end
    end
direction=trans*direction;     % rotates the direction to match the direction of the sensor
end



sensor=sensor'+randn(N,1)/3;   % adds noise to the measure with a normal distribuition

end