function [ probdens ] = probdensity(  position , direction, map, sensor, sigma, centerPoints )
%PROBDENSITY returns the probability density of a N-dimensional normal
%distribuition
%   the measurement is a N-dimensional vector.  the function calculates the
%   probability density of the 'real measurement' of a particle (the value 
%   that the sensor would measure if there were no error) within a N-d normal
%   distribuition with mean=sensor measure

[ realdistance ] = predictIRNew( position', direction, map, centerPoints );   %calculates the real values of distaces
%[ realdistance ] = realmeasurement( position, direction, map, 8);

%realdistance'

mu = sensor; % makes  mean = measured value 

probdens = mvnpdf(realdistance, mu, sigma);    % calculates the multi variative probability density

end