function [ position, direction ] = moveParticles( position, direction, stepmove ,map, centerMap)
%MOVEMENT moves the robot or the particle following the 'stepmove' one step plan
%   'stepmove' is a one step plan with speed and turn angle
%   the function considers stochasticity affecting the turn angle and speed
%   and constraints the particle movements based on map data

theta=stepmove.turn+randn*stepmove.turn/14;      % the turn angle is afected by stochasticity
trans=[cos(theta) sin(theta);     % defines the rotation operator
      -sin(theta) cos(theta)];
speed=stepmove.speed+randn*stepmove.speed/10;    %the speed is afected by stochasticity

direction= trans*direction;         %rotates the direction
direction=direction/norm(direction); %normalizes the direction

centerMapSize = size(centerMap);
centerMapSize = centerMapSize(1);

newPolyLines = [map.polyline{1}];
for i=2:centerMapSize
   diff = centerMap(i,:) - position';
   if (i == 4 || i == 10) && sqrt(diff(1)^2+diff(2)^2) < 400
       newPolyLines = [newPolyLines map.polyline{i}];
   elseif sqrt(diff(1)^2+diff(2)^2) < 200
       newPolyLines = [newPolyLines map.polyline{i}];
   end
end

mindistance=inf;    % initilizes the minimum distance with a high value
[~, b]=size(newPolyLines);   % gets the number of polylines in the map
for j=1:b       % for all polylines
    for i=1:length(newPolyLines(j).p1)        %for all segments
        % searches for possible intersection, representing objects that
        % could block the movement
        [ ~, distan ] = distance( position, direction, newPolyLines(j).p1(i,:)', newPolyLines(j).p2(i,:)' );
        if distan<mindistance
            mindistance=distan;
        end
    end
end

%mindistance = stepmove.speed;

% if the intersection is close enought, the stops before the
% intersection
if  mindistance<stepmove.speed
    position=position+direction*(mindistance-5);
else
    position=position+direction*speed;  %if there is no intersection close enought, it moves the whole step
end
    
end

