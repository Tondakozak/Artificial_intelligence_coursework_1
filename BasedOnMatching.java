import java.util.Arrays;
import java.util.LinkedList;


public class BasedOnMatching {
	private double[][] townDistances;
	
	private int[][] minimumSpanningTree;	
	private int[][] townPositions;
	private int dataLength;
	
	private double shortestLength = Double.MAX_VALUE;
	private int[] shortestPath;
	
	public BasedOnMatching(int[][] towns) {
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
		int[] realIndexes = new int[shortestPath.length];
		for (int townsIndex = 0; townsIndex < shortestPath.length; townsIndex++) {
			realIndexes[townsIndex] = townPositions[shortestPath[townsIndex]][2];
		}
		
		return realIndexes;
	}
	
	/**
	 * Get best length
	 * @return
	 */
	public double getBestLength() {
		return shortestLength;
	}
	
	/**
	 * Solve TSP
	 */
	public void solve() {
		this.minimumSpanningTree = minimumSpanningTree(townDistances);
		
		
		int[] oddNodes = findOddNodes(minimumSpanningTree);
		int[][] connectingPaths = findConnectingPaths(oddNodes);
		
		// Add new paths to the tree
		int[][] newTree = Arrays.copyOf(minimumSpanningTree, minimumSpanningTree.length+ connectingPaths.length);
		for (int newTreeIndex = minimumSpanningTree.length; newTreeIndex < newTree.length; newTreeIndex++) {
			newTree[newTreeIndex] = connectingPaths[newTreeIndex - minimumSpanningTree.length];
		}
		
		
		// find complete paths
		findCompletePaths(newTree);
	}
	
	/**
	 * Find complete path
	 * @param tree
	 * @return
	 */
	private LinkedList<int[]> findCompletePaths(int[][] tree) {
		LinkedList<int[]> paths = new LinkedList<>();

		for (int treeIndex = 0; treeIndex < tree.length; treeIndex++) {
			if (tree[treeIndex][0] == 0) { // there is a starting point
				LinkedList<Integer> result = new LinkedList<>();
				result.addFirst(tree[treeIndex][0]);
				result.addFirst(tree[treeIndex][1]);
				findCompletePathRecursion(result, arrayToLinkedList(tree), paths, 0);
				
			} else if (tree[treeIndex][1] == 0) { // there is a starting point
				LinkedList<Integer> result = new LinkedList<>();
				result.addFirst(tree[treeIndex][1]);
				result.addFirst(tree[treeIndex][0]);
				findCompletePathRecursion(result, arrayToLinkedList(tree), paths, 0);				
			}
		}
		
		return paths;
	}
	
	/**
	 * Find complete path in recursion
	 * @param result
	 * @param source
	 * @param output
	 * @param pathLength
	 */
	private void findCompletePathRecursion(LinkedList<Integer> result, LinkedList<int[]> source, LinkedList<int[]> output, double pathLength) {
		if (result.size() == dataLength) {
			// add result to output
			pathLength += townDistances[result.getLast()][0];
			if (pathLength < shortestLength) {
				shortestLength = pathLength;
				int[] resultArray = new int[dataLength+1];
				
				resultArray[dataLength] = 0;
				for (int resultIndex = 0; resultIndex < dataLength; resultIndex++) {
					resultArray[resultIndex+1] = result.get(resultIndex);
				}
				
				shortestPath = resultArray;
				//System.out.println(shortestLength);
			}
			
			
			return;
		} else {
			for (int sourceIndex = 0; sourceIndex < source.size(); sourceIndex++) {
				if (result.getFirst() == source.get(sourceIndex)[0] && !result.contains(source.get(sourceIndex)[1])) {
					LinkedList<Integer> newResult = new LinkedList<>(result);
					LinkedList<int[]> newSource = new LinkedList<>(source);
					
					double newPathLenght = pathLength + townDistances[newSource.get(sourceIndex)[1]][newResult.getFirst()];
					if (newPathLenght < shortestLength) {
						newResult.addFirst(newSource.get(sourceIndex)[1]);
						newSource.remove(sourceIndex);
						findCompletePathRecursion(newResult, newSource, output, newPathLenght);		
					}
					
					return;
				} else if (result.getFirst() == source.get(sourceIndex)[1] && !result.contains(source.get(sourceIndex)[0])) {
					LinkedList<Integer> newResult = new LinkedList<>(result);
					LinkedList<int[]> newSource = new LinkedList<>(source);
					
					double newPathLenght = pathLength + townDistances[newSource.get(sourceIndex)[0]][newResult.getFirst()];
						if (newPathLenght < shortestLength) {
						newResult.addFirst(newSource.get(sourceIndex)[0]);
						newSource.remove(sourceIndex);
						findCompletePathRecursion(newResult, newSource, output, newPathLenght);	
					}	
					return;
				}
			}
			
			// next node wasn't found
			// add all possible next nodes (which are not connected with the last one)
			for (int node = 0; node < dataLength; node++) {
				if (!result.contains(node)) { // this node is not in the result yet
					LinkedList<Integer> newResult = new LinkedList<>(result);
					double newPathLenght = pathLength + townDistances[node][newResult.getFirst()];
					if (newPathLenght < shortestLength) {
						newResult.addFirst(node);
						LinkedList<int[]> newSource = new LinkedList<>(source);
						findCompletePathRecursion(newResult, newSource, output, newPathLenght);	
					}
				}
			}
		}
		
	}
	
	/**
	 * Find all connecting path
	 * @param oddNodes
	 * @return
	 */
	private int[][]  findConnectingPaths(int[] oddNodes) {
		LinkedList<LinkedList<Integer>> paths = new LinkedList<>();
		
		
		// find possible pairs
		LinkedList<int[]> pairNodes = new LinkedList<>();
		for (int oddIndex = 0; oddIndex < oddNodes.length; oddIndex++) {
			for (int nextPairIndex = oddIndex+1; nextPairIndex < oddNodes.length; nextPairIndex++) {
				pairNodes.add(new int[] {oddNodes[oddIndex], oddNodes[nextPairIndex]});
			}
		}
		
		
		// find paths
		connectingPathsRecursion(new LinkedList<Integer>(), pairNodes, 0, paths, oddNodes.length);
		
		
		
		// find the best
		int[][] bestPaths = new int[oddNodes.length/2][2];
		double min = Double.MAX_VALUE;
		for (int pathsIndex = 0; pathsIndex < paths.size(); pathsIndex++) {
			double pathLenght = 0; 
			for (int thisPathIndex = 0; thisPathIndex < paths.get(pathsIndex).size()-1; thisPathIndex +=2) {
				pathLenght += townDistances[paths.get(pathsIndex).get(thisPathIndex)][paths.get(pathsIndex).get(thisPathIndex+1)];
				
			}
			if (min > pathLenght) {
				min = pathLenght;
				for (int thisPathIndex = 0; thisPathIndex < paths.get(pathsIndex).size()-1; thisPathIndex +=2) {
					bestPaths[thisPathIndex/2][0] = paths.get(pathsIndex).get(thisPathIndex);
					bestPaths[thisPathIndex/2][1] = paths.get(pathsIndex).get(thisPathIndex+1);
					
					
				}
			}
		}
		
		return bestPaths;
		
	}
	
	/**
	 * Find connectiong path in recursion
	 * @param result
	 * @param source
	 * @param lastIndex
	 * @param output
	 * @param numberOfNodes
	 */
	private void connectingPathsRecursion(LinkedList<Integer> result, LinkedList<int[]> source, int lastIndex, LinkedList<LinkedList<Integer>> output, int numberOfNodes) {
		if (result.size() == numberOfNodes) {
			output.add(result);
			return;
		} else {
			for (int sourceIndex = lastIndex; sourceIndex < source.size(); sourceIndex++) {
				if (!result.contains(source.get(sourceIndex)[0]) && !result.contains(source.get(sourceIndex)[1])) {
					LinkedList<Integer> newResult = new LinkedList<>(result);
					newResult.addFirst(source.get(sourceIndex)[0]);
					newResult.addFirst(source.get(sourceIndex)[1]);
					connectingPathsRecursion(newResult, source, sourceIndex, output, numberOfNodes);
				}
			}
		}
	}
	
	
	/**
	 * Convert array to LinkedList
	 * @param array
	 * @return
	 */
	private LinkedList<int[]> arrayToLinkedList(int[][] array) {
		LinkedList<int[]> newList = new LinkedList<>(); 
		for (int arrayIndex = 0; arrayIndex < array.length; arrayIndex++) {
			newList.add(array[arrayIndex]);
		}
		return newList;
	}
	
	/**
	 * Find nodes in the tree with odd degree
	 * @param tree
	 * @return
	 */
	private int[] findOddNodes(int[][] tree) {
		int[] nodes = new int[tree.length+1];
		int numberOfOddNodes = 0;
		for (int treeIndex = 0; treeIndex < tree.length; treeIndex++) {
			nodes[tree[treeIndex][0]]++;
			nodes[tree[treeIndex][1]]++;
			
			if (nodes[tree[treeIndex][1]] % 2 == 1) {
				numberOfOddNodes++;
			} else {
				numberOfOddNodes--;
			}
			if (nodes[tree[treeIndex][0]] % 2 == 1) {
				numberOfOddNodes++;
			} else {
				numberOfOddNodes--;
			}
		}
		
		int[] result = new int[numberOfOddNodes];
		int resultIndex = 0;
		for (int nodesIndex = 0; nodesIndex < nodes.length; nodesIndex++) {
			if (nodes[nodesIndex] % 2 == 1) {
				result[resultIndex] = nodesIndex;
				resultIndex++;
			}
		}
		
		return result;
		
	}
	
	/**
	 * Find minimum spanning tree
	 * @param distanceMatrix
	 * @return
	 */
	private int[][] minimumSpanningTree(double[][] distanceMatrix) {
		double[][] tempMetrix = deepCopyArray(distanceMatrix);
		
		LinkedList<int[]> spanningTree = new LinkedList<>();
		LinkedList<Integer> result = new LinkedList<>();
		LinkedList<Integer> source = new LinkedList<>();
		
		for (int index = 0; index < tempMetrix.length; source.add(index++)) {};
		
		// Find shortest distance between two towns
		double min = Double.MAX_VALUE;
		int[] minIndexes = new int[2];
		for (int row = 0; row < tempMetrix.length; row++) {
			for (int coll = 0; coll < tempMetrix[row].length; coll++) {
				if (min > tempMetrix[row][coll] && tempMetrix[row][coll] != -1) {
					min = tempMetrix[row][coll];
					minIndexes[0] = row;
					minIndexes[1] = coll;
				}
			}
		}
		
		// add towns to result and remove from source
		spanningTree.add(new int[] {minIndexes[0], minIndexes[1]});
		result.add(minIndexes[0]);
		result.add(minIndexes[1]);
		source.removeFirstOccurrence(minIndexes[0]);
		source.removeFirstOccurrence(minIndexes[1]);

		tempMetrix[minIndexes[0]][minIndexes[1]] = -1;
		tempMetrix[minIndexes[1]][minIndexes[0]] = -1;
		
		
		
		while (!source.isEmpty()) {
			//printMatrix(tempMetrix);
			//System.out.println();
			min = Double.MAX_VALUE;
			for (int sourceIndex = 0; sourceIndex < source.size(); sourceIndex++) {
				for (int resultIndex = 0; resultIndex < result.size(); resultIndex++) {
					if (min > tempMetrix[source.get(sourceIndex)][result.get(resultIndex)] 
							&& tempMetrix[source.get(sourceIndex)][result.get(resultIndex)] != -1) {
						min = tempMetrix[source.get(sourceIndex)][result.get(resultIndex)];
						minIndexes[0] = source.get(sourceIndex);
						minIndexes[1] = result.get(resultIndex);
					}
				}
			}
			
			spanningTree.add(new int[] {minIndexes[0], minIndexes[1]});
			result.add(minIndexes[0]);
			source.removeFirstOccurrence(minIndexes[0]);
			source.removeFirstOccurrence(minIndexes[1]);
			tempMetrix[minIndexes[0]][minIndexes[1]] = -1;
			tempMetrix[minIndexes[1]][minIndexes[0]] = -1;
		}
		
		int[][] resultArray = new int[spanningTree.size()][2];
		for (int spanningTreeIndex = 0; spanningTreeIndex < spanningTree.size(); spanningTreeIndex++) {
			resultArray[spanningTreeIndex] = spanningTree.get(spanningTreeIndex);
		}
		return resultArray;
		
	}
	
	/**
	 * Copy of two-dimensional array
	 * @param originalArray
	 * @return
	 */
	private double[][] deepCopyArray(double[][] originalArray) {
		double[][] newArray = new double[originalArray.length][originalArray[0].length];
		for (int row = 0; row < newArray.length; row++) {
			for (int coll = 0; coll < newArray[row].length; coll++) {
				newArray[row][coll] = originalArray[row][coll];
			}
		}
		
		return newArray;
 	}
	
	
	
	/**
	 * Fill in distances of the all pairs of towns
	 */
	private void fillInDistances() {
		townDistances = new double[townPositions.length][townPositions.length];
		for (int i = 0; i < townPositions.length; i++) {
			for (int k = i; k < townPositions.length; k++) {
				if (i == k) {
					townDistances[i][k] = -1;
				} else {
					townDistances[i][k] = distance(i, k);
					townDistances[k][i] = townDistances[i][k];
						
				}
			}
		}
	}
	
	/**
	 * Compute distance between two towns
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

