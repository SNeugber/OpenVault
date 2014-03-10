function [particles] = initializeRandom( numpartic, map )
%INITIALIZE initilizes random particles and the position and direction fo
%the robot

[~, b]=size(map.polyline);
points=[];
for j=1:b
   points=[points; map.polyline{j}.p1; map.polyline{j}.p2];   % gets all vertex of the map
end

Xmax=max(points(:,1));   % define map bounderies
Ymax=max(points(:,2));
Xmin=min(points(:,1));
Ymin=min(points(:,2));

startX = (rand*(Xmax-Xmin)+Xmin);
startY = (rand*(Ymax-Ymin)+Ymin);
startA = rand(2,1)-[0.5 0.5]';

%rng('shuffle');
particles.position=[(repmat(startX, numpartic, 1)) (repmat(startY, numpartic, 1))]; % puts particles at random in map bounderies

particles.direction = repmat(startA', numpartic, 1);

end