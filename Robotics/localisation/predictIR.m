function [ irVals ] = predictIR( pos, orient, map )
%PREDICTIR Summary of this function goes here
%   What this is supposed to do:
%   Calculate the positions and orientations of the ir sensors,
%   given the robot position and orientation.
%   Then use this information to get the distances

% TODO: Convert the ir values into distances

angle = atan2(orient(2), orient(1));

if angle < 0
    angle = 2*pi + angle;
end

irVals = [50,50,50,50,50,50,50,50]; %in millimeters
rotMat = [cos(angle) -sin(angle); sin(angle) cos(angle)];
sensorNum = 8;
sensorAngles = [1.3963, 0.7854, 0.0873, -0.0873, -0.7854, -1.3963, -2.9671, 2.9671];
sensorPositions = [[1, 3];[2,2.5];[3,1];[3,-1];[2,-2.5];[1,-3];[-3,-1];[-3,1]];

sensorAngles = sensorAngles + angle;
%avgs = [];

for i=1:sensorNum
    
    % Transpose sensor positions %
    sensorPositions(i,:) = (rotMat * sensorPositions(i,:)')';
    sensorPositions(i,:) = sensorPositions(i,:) + pos;
    
    % Adjust sensor angles after we have added the robot angle before %
    if sensorAngles(i) > 2*pi
       sensorAngles(i) = sensorAngles(i) - 2*pi;
    else if sensorAngles(i) < 0
       sensorAngles(i) = sensorAngles(i) + 2*pi;
    end
    
    [~, b]=size(map.polyline);       %gets the number of polylines
    for j=1:b         %for all polylines
        for k=1:length(map.polyline{j}.p1)
            [ ~, distan ] = distance( sensorPositions(i,:)', [cos(sensorAngles(i)),sin(sensorAngles(i))]', map.polyline{j}.p1(k,:)', map.polyline{j}.p2(k,:)' );
            %saves the smaller distance (closest objec that is in line with the sensor)
            if distan<irVals(i)
                irVals(i)=distan;
            end
            %avg = avg + map.polyline{j}.p1;
        end
        %avg = avg/length(map.polyline{j}.p1);
       % avgs = [avgs avg];
    end
    
end

end

