[numpart unused]= size(particles.position);
plotmap

for j=1:numpart
    plot(particles.position(j,1),particles.position(j,2),'k+')
    direc=[particles.position(j,:);particles.position(j,:)+particles.direction(j,:)*50];
    plot(direc(:,1),direc(:,2),'c');
end