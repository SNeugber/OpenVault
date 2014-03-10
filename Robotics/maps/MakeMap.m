
resp=input('Do you want to edit or erase current map? \n Answer 1 to edit and 2 to erase. \n Answer 0 to cancel \n');

if resp==2
    map=[];
    map.polyline={};
end


if resp==1 | resp==2
   [a b]=size(map.polyline);
   if b~=0
       resp=input('do you want to erase a poly-line (answer 1)\n  or input a poly-line (answer 2)?\n');
       if resp==1
           fprintf('there are %i poly-lines \n',b)
           J=input('input the index of the poly-line you want to erase \n');
           if J<=b
               
           plotmap
           for i=1:length(map.polyline{J}.p1)
            segment=[map.polyline{J}.p1(i,:);map.polyline{J}.p2(i,:)];
            plot(segment(:,1),segment(:,2),'r');
           end
           
           resp=input('are you sure? answer 1 to erase or 0 to cancel \n');
           if resp==1
               map.polyline(J)=[];
               fprintf('erased \n')
               plotmap
           end
           else
               fprintf('invalid input')
           end
       elseif resp==2

            points=input('input points of the poly-line. ex.: [3 5; 3 10; 7 10; 7 5] \n')
            [m,n]=size(points);
    
            if n~=2
                 fprintf('invalid input');
            else
                [a b]=size(map.polyline);
                map.polyline{b+1}.p1=[points(1:m-1,:)];
                map.polyline{b+1}.p2=[points(2:m,:)];
                plotmap
            end
       end 
   else
       points=input('input points of the poly-line. ex.: [3 5; 3 10; 7 10; 7 5] \n')
       [m,n]=size(points);
    
    
       if n~=2
          fprintf('invalid input');
       else
          [a b]=size(map.polyline);
          map.polyline{b+1}.p1=[points(1:m-1,:)];
          map.polyline{b+1}.p2=[points(2:m,:)];
          plotmap
       end
   end
else
    fprintf('operation canceled');
end