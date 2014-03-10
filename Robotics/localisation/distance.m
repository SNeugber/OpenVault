function [ intersec, distan ] = distance( position, direction, p1, p2 )
%DISTANCE calculates the distance to the intersection of 2 straight lines
%   position and direction define one straight line that is ment to be the
%   sensor of the robot. p1 and p2 defines a line segment that is one wall,
%   and p1 and p2 are the end points of the wall.
%   line1: position + t*direction
%   line2: p2 + r*(p1-p2)
%   intersection:  position + t*direction = p2 + r*(p1-p2)
%                  t*direction -r*(p1-p2)  = p2-position
%
%   matrix form:
%                  [ direction  ;  (p2-p1)]*[ t ] = p2-position
%                                           [ r ]
% 
%   if this problem has a solution then there will be a intersection

param = linsolve([direction (p2-p1)],(p2-position));   % solves the linear problem

intersecpoint = position + param(1)*direction;   % get the intersection point
segmentlength = norm(p1-p2);                     % calculates the length of the segment to know if the 
                                                % intersection point is out
                                                % limits of the segment

if ((param(1)<0) || (norm(p1-intersecpoint)>segmentlength) || (norm(p2-intersecpoint)> segmentlength))
    % if t<0 the wall is at the oposit direction of the sensor
    % if the distance of the intercetion point were greater then the length
    % of the segment then the intersection occurs outside of the segment.
    
    intersec =0;    % no intersection
    distan = inf;
else
    intersec=1;     % is intersection
    distan = norm(position-intersecpoint);    % the distance is the measurement
end

end

