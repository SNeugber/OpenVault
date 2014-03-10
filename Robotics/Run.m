addpath(genpath('.'));

numparticles=200;    % number of particles
N=8;                 % number of sensors
stepmove.speed=0;    % speed of movement (distance to move at every iteration)
stepmove.turn=0;     % angle to turn at every iteration
t = 0;
load realmap
load distToIRMap
centerPoints = makeCenterPoints(map);
potFieldMap = getPotFieldMap(10);

foods = [];
target = [460 490];
LEDtimer = tic;
ExploreTimer = tic;
FoodTimer = tic;
GiveUpTimer = tic;
isAtFood = 0;

[particles, robot]=initialize(numparticles);    % initializes particles and the robot

oldWeights = ones(numparticles,1);

oldrobot.position = robot.position;
oldrobot.direction = robot.direction;

stddeviation=10;  % defines stddeviation. they are the same for every dimension.
ro=.5;
sigma=[]; sigma(1:N,1:N)=ro*stddeviation^2; sigma=sigma-sigma.*eye(N)+eye(N).*stddeviation^2; % defines the covariance matrix 

% Initial plotting
plotall(robot, particles, map, robot);

fprintf('Initial state loaded. Is robot pose correct?\nPress any key to continue or CTRL+C to abort\n');
pause

% ===============
setCounts(s,0,0);
oldWheelCounts = [0 0];

%go(s, 2);
% ===============

speed = 6;
turnspeed = speed/2;
lSpeed = speed;
rSpeed = speed;
oldLSpeed = speed;
oldRSpeed = speed;

while true
    t = t + 1;
    
    % Generate new robot position and stepmove from odometry
    [ robot, stepmove, oldWheelCounts ] = odometry(s, oldrobot, oldWheelCounts);
    
    % Move particles and generate new predicted position
    bestPose = robot;
%     bestPose.position = bestPose.position';
%     bestPose.direction = bestPose.direction';
    if ((stepmove.speed > 0 || stepmove.turn ~= 0))
        [numpart ~]= size(particles.position);
        for i=1:numpart
            [ pos, dir ] = moveParticles( particles.position(i,:)', particles.direction(i,:)', stepmove, map, centerPoints);
            particles.position(i,:)  = pos';   % move particles following the stepmove plan
            particles.direction(i,:) = dir';
        end
        if (mod(t, 10) == 0)
            stop(s);
			% Read sensor data
			[ sensor ] = getDistFromIR(readIR(s), distToIRMap)';
            [ particles, bestPose, oldWeights ] = ressample(particles, sensor, stepmove, map ,sigma, oldWeights, centerPoints);  % ressample
            % setSpeeds(s, lSpeed, rSpeed);
            
            % Update robot position with the predicted position
            if (size(bestPose.position, 1) == 1 || size(bestPose.position, 2) == 1)
                %fprintf('here\n');
                robot.position = bestPose.position;
                robot.direction = bestPose.direction;
            else
                %fprintf('there\n');
                index = 1;
                prevd = inf;
                d = inf;
                for i=1:size(bestPose.position,1)
                    prevd = d;
                    d = min(mydist(robot.position, bestPose.position(i,:)), d);
                    if (~isequal(d,prevd))
                        index = i;
                    end
                end
                robot.position = bestPose.position(index,:);
                robot.direction = bestPose.direction(index,:);
            end
        end
    end
    
    % Fix for annoying one-case situation.
    if (size(robot.position, 1) == 2 && size(robot.position, 2) == 1)
        robot.position = robot.position';
        robot.direction = robot.direction';
    end

    % Save position for next iteration
    oldrobot.position = robot.position;
    oldrobot.direction = robot.direction;

    % Plot everything
    if (mod(t, 3) == 0)
        plotall(robot, particles, map, bestPose);
    end
    
    % Do navigation & control.
    timers = [ LEDtimer ; ExploreTimer ; FoodTimer ; GiveUpTimer ];
	if (mod(t, 10) ~= 0)
		% Read sensor data
		[ sensor ] = getDistFromIR(readIR(s), distToIRMap)';
	end
    [ oldLSpeed, oldRSpeed, foods, target, timers, isAtFood ] = control(s, robot, potFieldMap, sensor, foods, target, speed, turnspeed, oldLSpeed, oldRSpeed, timers, isAtFood );
    LEDtimer = timers(1);
    ExploreTimer = timers(2);
    FoodTimer = timers(3);
    GiveUpTimer = timers(4);
    % fprintf('Paused. press any key for next iteration.\nPress ctrl+C to stop \n')
    % pause(0.001);
end