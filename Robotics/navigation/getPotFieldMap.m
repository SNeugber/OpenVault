function  potFieldMap  = getPotFieldMap(resolution)
%GETPOTFIELDMAP Summary of this function goes here
%   Detailed explanation goes here

realMap = load('realmap');
realMap = realMap.map;

potFieldMap = [];

lines = realMap.polyline;

for line = lines
    
    tempPotPoints = [];
    s = size(line{1}.p1);
    s = s(1);
    
    for j = 1:s-1
        d1 = sqrt(sum(((line{1}.p1(j,:) - line{1}.p1(j+1,:))).^2,2));
        
        n = floor(d1/resolution);
        
        if n == 0
            outPoint = line{1}.p1(j,:);
            potFieldMap = [potFieldMap ; outPoint];
        else
            for k = 0:n
                outPoint = line{1}.p1(j,:) + (k/n) * (line{1}.p1(j+1,:) - line{1}.p1(j,:));
                potFieldMap = [potFieldMap ; outPoint];
            end 
        end
    end
    
    d1 = sqrt(sum(((line{1}.p1(1,:) - line{1}.p1(s,:))).^2,2));
        
    n = floor(d1/resolution);
    
    if n == 0
        outPoint = line{1}.p1(s,:);
        potFieldMap = [potFieldMap ; outPoint];
    else
        for k = 0:n
             outPoint = line{1}.p1(s,:) + (k/n) * (line{1}.p1(1,:) - line{1}.p1(s,:));
             potFieldMap = [potFieldMap ; outPoint];
        end
    end

end

    
end

