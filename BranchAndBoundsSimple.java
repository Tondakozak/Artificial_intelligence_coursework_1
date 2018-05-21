import java.util.Arrays;
import java.util.LinkedList;

public class BranchAndBoundsSimple {
	private double[][] townDistances;
	private int[][] townPositions;
	private LinkedList<BaBNodeSimple> stack = new LinkedList<>();
	private int dataLength;

	
	private boolean chooseLowestBound = false;
	private double maxLowerBound = Double.MAX_VALUE;
	private int[] bestPath;
	
	public BranchAndBoundsSimple(int[][] towns) {
		townPositions = towns;
		dataLength = towns.length;
		fillInDistances();		
	}
	
	/**
	 * Solve TSP
	 */
	public void solve() {	
		// first element		
		stack.add(new BaBNodeSimple(townDistances, new int[0], 0, 0));
		
		
		while (!stack.isEmpty()) {
			// take element
			BaBNodeSimple node;			
			
			// choose the node
			 node = stack.getFirst();
			 
			// find node with lowest Lower-bound
			int nodeIndex = 0;
			if (chooseLowestBound) {
				for (int i = 1; i < stack.size(); i++) {
					if (node.lowerBound > stack.get(i).lowerBound) {
						node = stack.get(i);
						nodeIndex = i;
					}
				}
			}			
			stack.remove(nodeIndex); // remove node from the stack
				
			// if current lower bound is greater or equal as maxLowerBound -> go to the next node
			if (node.lowerBound >= maxLowerBound) {
				continue;
			}
			
			
			// if the path is complete
			if (node.nextPossibleNodes.length == 0) {
				chooseLowestBound = true;
				int[] completePath = Arrays.copyOf(node.path, node.path.length+1);
				completePath[completePath.length-1] = node.path[0];
				
				double pathLength = evaluate(completePath); // length of the complete path

				// if the path is shortest
				if (pathLength < maxLowerBound) {
					maxLowerBound = pathLength;
					bestPath = completePath;
					
					// remove all paths which have lower bound greater or equal as the path length
					for (int i = 0; i < stack.size(); i++) {
						if (stack.get(i).lowerBound >= maxLowerBound) {
							stack.remove(i);
						}
					}
				}
				 
			} else { // path is not complete; generate new nodes
				double pathCost = evaluate(node.path);
				for (int nextNodeIndex = 0; nextNodeIndex < node.nextPossibleNodes.length; nextNodeIndex++) {
					int[] nextPath = {node.newNode, node.nextPossibleNodes[nextNodeIndex]};
					BaBNodeSimple newNode = new BaBNodeSimple(prepareMatrix(node.matrix, nextPath), node.path, node.nextPossibleNodes[nextNodeIndex], pathCost);
					if (newNode.lowerBound < maxLowerBound) {
						stack.add(newNode);
					}
				}
			}
		}
	}
	
	/**
	 * Return best path
	 * @return
	 */
	public int[] getBestPath() {
		// convert the internal indexes of the towns to the indexes from the source file
		int[] realIndexes = new int[bestPath.length];
		for (int townsIndex = 0; townsIndex < bestPath.length; townsIndex++) {
			realIndexes[townsIndex] = townPositions[bestPath[townsIndex]][2];
		}
		
		return realIndexes;
	}
	
	/**
	 * Get best length
	 * @return
	 */
	public double getBestLength() {
		return maxLowerBound;
	}
	
	
	/**
	 * Create new matrix, add -1 to the not-reachable pairs of towns
	 * @param originalMatrix
	 * @param nextPath
	 * @return
	 */
	private double[][] prepareMatrix(double[][] originalMatrix, int[] nextPath) {
		double[][] newMatrix = new double[dataLength][dataLength];
		
		for (int row = 0; row < dataLength; row++) {
			for (int coll = 0; coll < dataLength; coll++) {
				if (row == nextPath[0] || coll == nextPath[1] || (row == nextPath[1] && coll == nextPath[0])) {
					newMatrix[row][coll] = -1;
				} else {
					newMatrix[row][coll] = originalMatrix[row][coll];
				}
			}
		}
		return newMatrix;
	}
	
	/**
	 * Compute total length of the given path
	 * @param path
	 * @return
	 */
	private double evaluate(int[] path) {
		double pathLength = 0;
		for (int i = 1; i < path.length; i++) {
			pathLength += townDistances[path[i-1]][path[i]];
		}
		return pathLength;
	}
	
	/**
	 * Fill in distances of the all pairs of towns
	 */
	private void fillInDistances() {
		townDistances = new double[townPositions.length][townPositions.length];
		for (int firstTownIndex = 0; firstTownIndex < townPositions.length; firstTownIndex++) {
			for (int secondTownIndex = firstTownIndex; secondTownIndex < townPositions.length; secondTownIndex++) {
				if (firstTownIndex == secondTownIndex) { // the town are the same
					townDistances[firstTownIndex][secondTownIndex] = -1;
				} else {
					townDistances[firstTownIndex][secondTownIndex] = distance(firstTownIndex, secondTownIndex);
					townDistances[secondTownIndex][firstTownIndex] = townDistances[firstTownIndex][secondTownIndex];
						
				}
			}
		}
	}
	
	/**
	 * Return distance between two points
	 * @param p1
	 * @param p2
	 * @return
	 */
	private double distance(int p1, int p2) {
		// Get position of the towns
		int[] point1 = townPositions[p1];
		int[] point2 = townPositions[p2];
		
		return Math.pow(Math.pow(point2[0]-point1[0], 2) + Math.pow(point2[1] - point1[1], 2), 1.0/2);
		
	}
	
}

