function [ realdistance ] = realmeasurement( position , direction, map, N )
%MEASUREMENT given the actual position and direction of the robot or the particle returns
%the array of real distances. the same code as measurement, but whitout adding noise.

theta=2*pi/N;
trans=[cos(theta) sin(theta);
       -sin(theta) cos(theta)];
   
realdistance= zeros(N,1);

for n=1:N
    realdistance(n)=10000;
    [a b]=size(map.polyline);
    for j=1:b
    for i=1:length(map.polyline{j}.p1)
        [ dummy, distan ] = distance( position, direction, map.polyline{j}.p1(i,:)', map.polyline{j}.p2(i,:)' );
        if distan<realdistance(n)
            realdistance(n)=distan;            
        end
    end
    end
direction=trans*direction;
end


end

