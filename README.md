# Artificial intelligence coursework 1
Artificial Inteligence, Computer Science, Middlesex University, London

## Overview ##
In this project we have to find and implement in Java the best as posible algorithm for [Travelling salesman problem](https://en.wikipedia.org/wiki/Travelling_salesman_problem).

## Algorithms Description ##
In the project, I use 4 algorithms. The reason, why I use so many algorithms is, that I wrote them when I tried to find the best solution and I would regret if I would have to just throw them out.

### Heuristic algorithms ###
1)	Best First Search – (Class Greedy) Start with the first town and find the second one with the shortest distance to the previous one; in every iteration I look for one nearest town for the town on the end of the path but also one for the town on the first position in the path. I believe that it can give me better result than the approach of adding nearest town only to the end of the path. But obviously, it could be true only in certain category of test data.

    For better performance, I calculate the all distances in the beginning and I sort them into a list, so I can just go through the list and I don’t have to calculate all the distances all the time.

2)	Branch and bounds based on reduced matrix. (will be used for the fourth test if the number of towns is smaller than 15)

    This should be an exact algorithm, but I have there a bug (which I cannot find) which causes that I don’t get the best result. But I spent a lot of hours on this algorithm, so it would be a pity to just lost it. 

    The core of the algorithm is branch and bounds, so I examine only branches with lowest lower bound (the minimum path length, which must be in the branch) and cut branches with lower bound highest than the upper bound (the lowest known solution).

    For calculating lower bound I use reduced matrix approach. 
    -	I subtract matrix (matrix of distances between towns) so that in every line and column there is at least one zero.
    -	For every zero I calculate a penalty (sum of the next lowest number in the row and column).
    -	I take the zero with the highest penalty and create two sub-branches, 
         - In the path there will be a sub-path of the two town with the highest penalty
         - There will not be this sub-path
    -	Lower bound is sum of values I had to subtract from the matrix to get zeros and the lower bound of the parent path/node.
 
### Exact algorithms ###
1)	Breath first search – (Class Exhaustive) recursively explore every possible path from each node.
2)	Simple branch and bounds (will be used for optimal solution if the input has less than 8 towns)
    When I realized that I will not enough time to find a bug in the Branch and Bound algorithm with reduced matrix, I implemented the simpler version.
    
    For calculating lower bound it just sum the smallest distances for reachable towns (for every row in distance matrix finds smallest number, which are on reachable position – it would not create a circled sub-path.


	
Inspiration for the algorithms was taken from these videos:
•	https://www.youtube.com/watch?v=-cLsEHP0qt0
•	https://www.youtube.com/watch?v=nN4K8xA8ShM
