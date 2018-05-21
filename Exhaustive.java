
import java.util.LinkedList;


public class Exhaustive {
	private static int DATA_LENGTH;
	private double[][] townDistances;
	private int[] townIndexes;
	
	private double bestLength = Double.MAX_VALUE;
	private int[] bestPath;
	
	
	private int[][] townPositions;
	
	
	
	public Exhaustive(int[][] towns) {
		DATA_LENGTH = towns.length;
		townDistances = new double[DATA_LENGTH][DATA_LENGTH];
		townIndexes = new int[DATA_LENGTH];
		
		for(int i = 0; i < towns.length; i++) {
			townIndexes[i] = i;
		}
		townPositions = towns;		
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
		return bestLength;
	}
	
	/**
	 * Solve the TSP
	 */
	public void solve() {		
		fillInDistances();	// compute distances for all pairs of towns
		
		// prepare data for breath first search
		LinkedList<Integer> result = new LinkedList<>();
		LinkedList<Integer> data = new LinkedList<>();
		result.add(townIndexes[0]);
		
		for (int i = 1; i < DATA_LENGTH; i++) {
			data.add(townIndexes[i]);
		}
		
		breathFirstSearch(result, data);
	}
	
	
	/**
	 * Compute length of given path (given path should be a loop)
	 * @param data
	 * @return double
	 */
	public double evaluate(int[] data) {		
			double sum = 0; 			
			int end = DATA_LENGTH-1;
			for (int dataIndex = 0; dataIndex < end; dataIndex++) {
				sum += townDistances[data[dataIndex]][data[dataIndex+1]];				
			}
			return sum;		
	}
	
	
	/**
	 * Compute distances between towns and insert them into townPosition variable
	 */
	private void fillInDistances() {
		for (int i = 0; i < townPositions.length; i++) {
			for (int k = i; k < townPositions.length; k++) {
				townDistances[i][k] = distance(i, k);
				townDistances[k][i] = townDistances[i][k];
			}
		}
	}
	
	/**
	 * Recursive breath first search; best result is added to global variable bestLength and bestPath
	 * @param result
	 * @param data
	 */
	private void breathFirstSearch(LinkedList<Integer> result, LinkedList<Integer> data) {
		
		if (data.size() == 0) {	// If the new path contains all the towns		
			// create a array from a list
			int[] resultInt = new int[result.size()+1];
			for (int i = 0; i < result.size(); i++) {
				resultInt[i] = result.get(i);
			}
			resultInt[result.size()] = resultInt[0]; // create a loop
			
			// get length of the path
			double pathLength = evaluate(resultInt);
			
			// if the length is best, add it to the global variables (for saving time)
			if (pathLength < bestLength) {
				bestLength = pathLength;
				bestPath = resultInt;
			}
			
			return;
		}
		
		// if the path is not complete, for every sub-node create new path and call this function recursively 
		for (int dataIndex = 0; dataIndex < data.size(); dataIndex++) {
			LinkedList<Integer> newResult = new LinkedList<>(result);
			LinkedList<Integer> newData = new LinkedList<>(data);
			newResult.add(newData.get(dataIndex));
			newData.remove(dataIndex);
			breathFirstSearch(newResult, newData);			
		}
	}
	
	/**
	 * Return distance between two points
	 * @param p1 int point 1
	 * @param p2 int point 2
	 * @return double distance
	 */
	private double distance(int p1, int p2) {		
			int[] point1 = townPositions[p1];
			int[] point2 = townPositions[p2];
			
			return Math.pow(Math.pow(point2[0]-point1[0], 2) + Math.pow(point2[1] - point1[1], 2), 1.0/2);		
	}
	
	
}
