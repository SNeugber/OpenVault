clf('reset')

hold on
[a b]=size(map.polyline);
for j=1:b
for i=1:length(map.polyline{j}.p1)
    segment=[map.polyline{j}.p1(i,:);map.polyline{j}.p2(i,:)];
    plot(segment(:,1),segment(:,2));
end
end
