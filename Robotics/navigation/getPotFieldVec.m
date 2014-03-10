function [ angle, intensity ] = getPotFieldVec(robot,map,food )
%GETPOTFIELDVEC Summary of this function goes here
%   Detailed explanation goes here

outVector = [0 0];

obsFactor = 400;
foodFactor = 2000;

% go through entire map of objects, calculate distance to objects,
% check if distance within 10cm, calculate vector from object to robot,
% add to overall vector
s = size(map);
for x = map'
    d = sqrt(sum((x'-robot.position).^2,2));
    if d < 100
        temp = (robot.position - x')/norm((robot.position - x'));
        factor = obsFactor/(d^2);
        outVector = outVector + (factor .* temp);
    end
end

% go through entire map of food sources, calculate distance,
% check if distance within 100cm, calculate vector from robot to food,
% add to overall vector
s = size(food);
for y = food'
    d = sqrt(sum((y'-robot.position).^2,2));
    if(d < foodFactor)
        temp = (y' - robot.position)/norm((y' - robot.position));
        outVector = outVector + ((foodFactor - d)/(foodFactor*s(1)).* temp);
    end
end

intensity = norm(outVector);
outVector = outVector/norm(outVector);

costheta = dot(outVector, robot.direction)/(norm(outVector)*norm(robot.direction));
angle = rad2deg(acos(costheta));

if robot.direction(1)*outVector(2) - robot.direction(2)*outVector(1)< 0
    angle = -angle;
end

end

