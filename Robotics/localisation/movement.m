function [ position, direction ] = movement( position, direction, stepmove ,map)
%MOVEMENT Moves the robot or the particle following the 'stepmove' given by
%odometry.
%   'stepmove' is a one step plan with speed and turn angle as given by the
%   robots odometry calculations.

theta=stepmove.turn;  
trans=[cos(theta) sin(theta);       % defines the rotation operator
      -sin(theta) cos(theta)];
speed=stepmove.speed; 
  
direction= trans*direction;         %rotates the direction
direction=direction/norm(direction); %normalizes the direction

mindistance=inf;    % initilizes the minimum distance with a high value
[~, b]=size(map.polyline);   % gets the number of polylines in the map
for j=1:b       % for all polylines
    for i=1:length(map.polyline{j}.p1)        %for all segments
        % searches for possible intersection, representing objects that
        % could block the movement
        [ ~, distan ] = distance( position, direction, map.polyline{j}.p1(i,:)', map.polyline{j}.p2(i,:)' );
        if distan<mindistance
            mindistance=distan;            
        end
    end
end

% if the intersection is close enought, the stops before the
% intersection
if  mindistance<stepmove.speed
    position=position+direction*(mindistance-5);
else
    position=position+direction*speed;  %if there is no intersection close enought, it moves the whole step
end
    
end

