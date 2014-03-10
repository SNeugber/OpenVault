function [ particles, bestPose, oldWeights ] = ressample( particles, sensor, stepmove, map, sigma, oldWeights, centerPoints)
%RESSAMPLE Particle filter reassampling code. 
%   Detailed explanation goes here


reInitAmt = 0; % Percentage of samples that should be reinitialised.

[numpart ~]= size(particles.position);

weight = weights( particles, sensor, map, sigma, oldWeights, centerPoints );    % get the weights for every particle
j = randsample(numpart,numpart,true,weight);    % draw particle index with replacement

if (find(weight == max(weight)) > 0)
    bestPose.position = particles.position(weight == max(weight), :);
    bestPose.direction = particles.direction(weight == max(weight), :);
end

%alocates position and direction matrices
posit=zeros(numpart,2);
direc=zeros(numpart,2);

for i=1:(numpart - floor(reInitAmt*numpart))
    posit(i,:) = particles.position(j(i),:);
    direc(i,:) = particles.direction(j(i),:);
end

if (reInitAmt > 0)
    [reinitpart] = initializeRandom(floor(reInitAmt*numpart),map); % reinitializes 'reInitAmt' percent of the particles
    posit((numpart - floor(reInitAmt*numpart)+1):numpart,:)=reinitpart.position;
    direc((numpart - floor(reInitAmt*numpart)+1):numpart,:)=reinitpart.direction;
end


particles.position=posit;
particles.direction=direc;

oldWeights = weight;

end
