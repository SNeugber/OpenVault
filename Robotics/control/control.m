function [ oldLSpeed, oldRSpeed, foods, target, timers, isAtFood ] = control(s, robot, potFieldMap, sensor, foods, target, speed, turnspeed, oldLSpeed, oldRSpeed, timers, isAtFood )
%CONTROL Summary of this function goes here
%   Detailed explanation goes here

    home = [ 460 490 ];
    
    mapLimit = 600;
    
%     LEDtimer = timers(1);
%     ExploreTimer = timers(2);
%     FoodTimer = timers(3);
%     GiveUpTimer = timers(4);

    % Rename sensor values
    leftIR1 = sensor(1);
    leftIR2 = sensor(2);
    midLeftIR = sensor(3);
    midRightIR = sensor(4);
    rightIR2 = sensor(5);
    rightIR1 = sensor(6);
    %backRightIR = sensor(7);
    %backLeftIR = sensor(8);
    
    if (mydist(home, robot.position) < 35 && isequal(target,home))
        fprintf('Robot is close to home\n');
        if (size(foods,1) > 0)
            fprintf('New target set to primary food source\n');
            target = foods(1, :);
            timers(1) = tic;
            toggleLED(s, 0, 1);
        else
            fprintf('New target set randomly\n');
            target = [rand*940 rand*mapLimit];
            timers(2) = tic;
        end
    end
    
    if (toc(timers(1)) > 0.75)
        toggleLED(s, 0, 0)
    end
    
    if ((toc(timers(2)) > 15 || mydist(target, robot.position) < 50) && size(foods,1) == 0)
        fprintf('New target set randomly because reached target or timer expired.\n');
        target = [rand*940 rand*mapLimit];
        timers(2) = tic;
    end
    
    if (size(foods,1) > 0 && isequal(target,foods(1, :)) && mydist(target, robot.position) < 50)
        fprintf('Reached food source\n');
        timers(3) = tic;
        timers(4) = tic;
        target = target + 0.01;
        isAtFood = 1;
    end
    
    if (isAtFood && toc(timers(4)) > 35)
        fprintf('Giving up on food source, going off to explore\n');
        target = [rand*940 rand*mapLimit];
        isAtFood = 0;
    end
    
    if (isAtFood && toc(timers(3)) > 5)
        fprintf('Adjusting food target\n');
        target = [ target(1) + randn*20 , target(2) + randn*20 ];
        timers(3) = tic;
    end
    
    plot(target(1), target(2), 'og');
    
    % Check if we found food
    foundFood = 0;
    indeces = size(find(sensor > 30),1);
    if indeces == 0
       foundFood = 1;
    end
    
    % Do stuff when we found food.
    if (foundFood == 1 && (size(strmatch([ 1 1 ], ismember(foods,robot.position)), 1) == 0))
        fprintf('FOOOOOOOOOOOOD! Nom nom nom nomonomomomnnom....\n');
        stop(s);
        inGoals = 0;
        for i=1:size(foods,1)
            if (mydist(robot.position, foods(i,:)) < 50)
                inGoals = 1;
                target = home;
                break;
            end
        end
        
        if (~inGoals)
            foods = [robot.position; foods]; % Add the new food to the top. This will be our primary food source now.
            target = home;
        end
        isAtFood = 0;
        return
    end

    % Baseline obstacle avoidance code
    % =======================
    if((rightIR1 < 37.5 || rightIR2 < 28) && (leftIR1 < 37.5 || leftIR2 < 28) && ((midRightIR + midLeftIR)/2 > 55))
        fprintf('[Obstacle Avoid] Obstacle on BOTH SIDES -> Squeze through\n');
        go(s,speed);
        return
    % end
    
    % Adjust Left based on centre sensor
    elseif (midRightIR < 55 || rightIR1 < 37.5 || rightIR2 < 28)
        fprintf('[Obstacle Avoid] Obstacle on the RIGHT\n');
        stop(s)
        turn(s,-turnspeed,turnspeed)
        return
    % end

    % Adjust Left based on centre sensor
    elseif ((midLeftIR < 55 || leftIR1 < 37.5 || leftIR2 < 28))
        fprintf('[Obstacle Avoid] Obstacle on the LEFT\n');
        stop(s)
        turn(s,turnspeed,-turnspeed)
        return
    % end
    % ======================
    else
        % Get potential field direction
        %fprintf('[Potential Field] Calculate drive angle\n');
        [a,~] = getPotFieldVec(robot, potFieldMap, target);

        if(a > 0)
            rSpeed = speed;
            lSpeed = max(round(speed - (12 * abs(a) / 180) -1),-speed);
        else
            lSpeed = speed;
            rSpeed = max(round(speed - (12 * abs(a) / 180) -1),-speed);
        end

        if(oldLSpeed ~= lSpeed || oldRSpeed ~= rSpeed)
            setSpeeds(s,lSpeed,rSpeed);
            oldLSpeed = lSpeed;
            oldRSpeed = rSpeed;
        end
    end

end

