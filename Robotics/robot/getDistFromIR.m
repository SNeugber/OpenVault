function [ distances ] = getDistFromIR( irVals, map )
%GETDISTFROMIR Summary of this function goes here
%   Detailed explanation goes here

distances = ones(1,8) * 70;
sIR = size(irVals);
sIR = sIR(2);

for j=1:sIR
    [~,s] = size(map);
    for i=1:7
        if irVals(j) > map(j,i)
            % calculate distance
            if i == 1
                distances(j) = 10;
            else
                distances(j) = (map(j,i-1) - irVals(j))/(map(j,i-1) - map(j,i)) * 10 + (i-1)*10;
            end
            break
        end 
    end
end

end

