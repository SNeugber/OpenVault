function [ avgs ] = makeCenterPoints(map)

avgs = [];

[a b]=size(map.polyline);
for j=1:b
    avg = [0 0];
    for i=1:length(map.polyline{j}.p1)
        avg = avg + map.polyline{j}.p1(i,:);
    end
    avg = avg ./ length(map.polyline{j}.p1);
    avgs = [avgs; avg];
end
end

