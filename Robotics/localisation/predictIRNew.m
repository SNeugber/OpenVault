function [ irVals ] = predictIRNew( pos, orient, map, centerMap )
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

irVals = ones(1,8) * 70; %in millimeters
rotMat = [cos(angle) -sin(angle); sin(angle) cos(angle)];
sensorNum = 8;
sensorAngles = [1.3963, 0.7854, 0.0873, -0.0873, -0.7854, -1.3963, -2.9671, 2.9671];
sensorPositions = [[10, 30];[20,25];[30,10];[30,-10];[20,-25];[10,-30];[-30,-10];[-30,10]];

sensorAngles = sensorAngles + angle;

centerMapSize = size(centerMap);
centerMapSize = centerMapSize(1);

newPolyLines = [map.polyline{1}];
for i=2:centerMapSize
   diff = centerMap(i,:) - pos;
   if (i == 4 || i == 10) && sqrt(diff(1)^2+diff(2)^2) < 400
       newPolyLines = [newPolyLines map.polyline{i}];
   elseif sqrt(diff(1)^2+diff(2)^2) < 200
       newPolyLines = [newPolyLines map.polyline{i}];
   end
end

for i=1:sensorNum
    
    % Transpose sensor positions %
    sensorPositions(i,:) = (rotMat * sensorPositions(i,:)')';
    sensorPositions(i,:) = sensorPositions(i,:) + pos;
    
    % Adjust sensor angles after we have added the robot angle before %
    if sensorAngles(i) > 2*pi
       sensorAngles(i) = sensorAngles(i) - 2*pi;
    elseif sensorAngles(i) < 0
       sensorAngles(i) = sensorAngles(i) + 2*pi;
    end
    
    [~, b]=size(newPolyLines);       %gets the number of polylines
    for j=1:b         %for all polylines
        for k=1:length(newPolyLines(j).p1)
            [ ~, distan ] = distance( sensorPositions(i,:)', [cos(sensorAngles(i)),sin(sensorAngles(i))]', newPolyLines(j).p1(k,:)', newPolyLines(j).p2(k,:)' );
            %saves the smaller distance (closest objec that is in line with the sensor)
            if distan<irVals(i)
                irVals(i)=distan;
            end
        end
    end
    
end

irVals = irVals';
end

