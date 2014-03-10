function [ distance ] = mydist( p1, p2 )
%DIST Euclidean distance between two points
%   Detailed explanation goes here

distance = sqrt((p1(1) - p2(1))^2 + (p1(2) - p2(2))^2);

end

