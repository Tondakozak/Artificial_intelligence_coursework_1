import java.util.Arrays;
import java.util.LinkedList;

public class BranchAndBounds {
	private double[][] townDistances;
	private int[][] townPositions;
	private LinkedList<BaBNode> stack = new LinkedList<>();
	private int dataLength;
	private int counter = 0;
	private boolean chooseLongestPath = true;
	
	private double maxLowerBound = Double.MAX_VALUE;
	private int[][] shortestPath;
	
	public BranchAndBounds(int[][] towns) {
		townPositions = towns;
		dataLength = towns.length;
		fillInDistances();
		
	}
	
	/**
	 * Return best path
	 * @return
	 */
	public int[] getBestPath() {
		// convert the internal indexes of the towns to the indexes from the source file
		int[] realIndexes = new int[dataLength+1];
		realIndexes[0] = getRealTownIndex(shortestPath[0][0]);
		realIndexes[1] = getRealTownIndex(shortestPath[0][1]);
		int lastPairIndex = 0;
		for (int townsIndex = 2; townsIndex < dataLength; townsIndex++) {
			// Find next town
			for (int bestPathIndex = 1; bestPathIndex < shortestPath.length; bestPathIndex++) {
				if (lastPairIndex != bestPathIndex && 
						(getRealTownIndex(shortestPath[bestPathIndex][0]) == realIndexes[townsIndex-1]) ||
						(getRealTownIndex(shortestPath[bestPathIndex][1]) == realIndexes[townsIndex-1])) {
					int nextTown = (getRealTownIndex(shortestPath[bestPathIndex][1]) == realIndexes[townsIndex-1])?shortestPath[bestPathIndex][0]:shortestPath[bestPathIndex][1];
					realIndexes[townsIndex] = getRealTownIndex(nextTown);
				}
			}
		}
		realIndexes[dataLength] = realIndexes[0];
		
		return realIndexes;
	}
	
	/**
	 * Get best length
	 * @return
	 */
	public double getBestLength() {
		return maxLowerBound;
	}
	public void solve() {
		// first node
		BaBNode firstNode = new BaBNode(townDistances, 0, new int[0][0]);
		stack.add(firstNode);
		while (!stack.isEmpty()) {
			counter++;
			BaBNode node;
			
			if (counter > 1000) {
				chooseLongestPath = true;				
			}
			
			// choose the node
			if (maxLowerBound == Double.MAX_VALUE || counter > 1000) {
				node = stack.getFirst();
				stack.removeFirst();
			} else {
				 node = stack.getFirst();
				// find node with lowest Lower-bound
				int nodeIndex = 0;
				for (int i = 1; i < stack.size(); i++) {
					if ((chooseLongestPath && node.path.length < stack.get(i).path.length) || node.lowerBound > stack.get(i).lowerBound) {
						node = stack.get(i);
						nodeIndex = i;
					}
				}
				
				stack.remove(nodeIndex); // remove node from the stack
						
			}
						
			
			if (node.maxPenaltyCoor[0] == 0 && node.maxPenaltyCoor[1] == 0) { // if there is no penalty -> new node
				continue;
			}
			
			// create two new nodes -> 1) no create tour of max-penalty towns pair; 2) visit that
			double[][] newMatrixNoVisit = new double[dataLength][dataLength];
			for (int i = 0; i < dataLength; i++) {
				for (int k = 0; k <dataLength; k++) {
					if ((i == node.maxPenaltyCoor[0] && k == node.maxPenaltyCoor[1]) || (i == node.maxPenaltyCoor[1] && k == node.maxPenaltyCoor[0])) { // set this path as unavailable
						newMatrixNoVisit[i][k] = -1;
					} else {
						newMatrixNoVisit[i][k] = node.matrix[i][k];
					}
				}
			}
			
			BaBNode newNoVisit = new BaBNode(newMatrixNoVisit, node.lowerBound, node.path);
			// if the penalty are of impossible possition - remove node
			if (!(newNoVisit.maxPenaltyCoor[0] == 0 && newNoVisit.maxPenaltyCoor[1] == 0)) {
				stack.add(newNoVisit);
			}
			
			
			double[][] newMatrixVisit = new double[dataLength][dataLength];
			for (int i = 0; i < dataLength; i++) {
				for (int k = 0; k <dataLength; k++) {
					if ((i == node.maxPenaltyCoor[1] && k == node.maxPenaltyCoor[0]) ||
							i == node.maxPenaltyCoor[0] ||
							k == node.maxPenaltyCoor[1]) { // set this path as unavailable
						newMatrixVisit[i][k] = -1;
					} else {
						newMatrixVisit[i][k] = node.matrix[i][k];
					}
				}
			}
			
						
			
			int[][] newPath = new int[node.path.length+1][2];
			for (int i = 0; i < node.path.length; i++) {
				newPath[i] = node.path[i];
			}
			newPath[newPath.length-1] = node.maxPenaltyCoor;
			
			
			// prevent creating sub-tours
			for (int i = 0; i < newPath.length; i++) {
				for (int k = 0; k < newPath.length; k++) {
					newMatrixVisit[newPath[i][0]][newPath[k][1]] = -1;
					newMatrixVisit[newPath[k][1]][newPath[i][0]] = -1;
					
				}
			}
			
			
			if (newPath.length == dataLength-1) { // we reached the end, evaluate
				// Find last town to the path to create a loop
				int townsIndexesSum = ((newPath.length+1) * (newPath.length)) / 2;
				int lastPartSum[] = new int[2];
				for (int i = 0; i < newPath.length; i++) {
					lastPartSum[0] += newPath[i][0];
					lastPartSum[1] += newPath[i][1];
				}
				
				int[][] newPathWithEnd = Arrays.copyOf(newPath, newPath.length+1);
				newPathWithEnd[newPath.length] = new int[2];
				newPathWithEnd[newPath.length][0] = townsIndexesSum -lastPartSum[0];
				newPathWithEnd[newPath.length][1] = townsIndexesSum -lastPartSum[1];
				
				double pathLength = evaluate(newPathWithEnd);
				if (pathLength < maxLowerBound) {
					maxLowerBound = pathLength;
					shortestPath = newPathWithEnd;
				}
					// delete nodes with higher lowerBound than is the maxLowerBound					
				for (int i = 0; i < stack.size(); i++) {
					if (stack.get(i).lowerBound >= maxLowerBound) {
						stack.remove(i);
						i--;
					}
				}
				counter = 0;
				chooseLongestPath = false;
				
			} else {
				stack.addFirst(new BaBNode(newMatrixVisit, node.lowerBound, newPath));
			}
		}	
	}
	
	private int getRealTownIndex(int index) {
		return townPositions[index][2];
	}
	
	/**
	 * Compute total length of the path
	 * @param path
	 * @return
	 */
	private double evaluate(int[][] path) {
		double pathLength = 0;
		for (int i = 0; i < path.length; i++) {
			pathLength += townDistances[path[i][0]][path[i][1]];
		}
		
		return pathLength;
	}
	
	/**
	 * Fill in distances of the all pairs of towns
	 */
	private void fillInDistances() {
		townDistances = new double[townPositions.length][townPositions.length];
		for (int row = 0; row < townPositions.length; row++) {
			for (int col = row; col < townPositions.length; col++) {
				if (row == col) {
					townDistances[row][col] = -1;
				} else {
					townDistances[row][col] = distance(row, col);
					townDistances[col][row] = townDistances[row][col]; // the matrix is symmetrical
						
				}
			}
		}
	}
	
	/**
	 * return distances between two towns
	 * @param p1
	 * @param p2
	 * @return
	 */
	private double distance(int p1, int p2) {
		int[] point1 = townPositions[p1];
		int[] point2 = townPositions[p2];
		
		return Math.pow(Math.pow(point2[0]-point1[0], 2) + Math.pow(point2[1] - point1[1], 2), 1.0/2);

	}
}

