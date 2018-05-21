import java.util.Arrays;
import java.util.LinkedList;

public class BaBNodeSimple {
	double[][] matrix;
	double lowerBound = 0;
	int[] nextPossibleNodes;
	int[] path;
	int newNode;
	
	double pathCost;
	
	public BaBNodeSimple(double[][] matrix, int[] path, int nextNode, double pathCost) {
		this.matrix = matrix;
		this.path = Arrays.copyOf(path, path.length+1);
		this.path[path.length] = nextNode;
		this.newNode = nextNode;
		this.pathCost = pathCost;
		
		computeLowerBound();
		findNextPossibleNodes();
	}
	
	/**
	 * Compute lower bound
	 */
	private void computeLowerBound() {
		for (int row = 0; row < matrix.length; row++) {
			double rowMinimum = Double.MAX_VALUE;
			for (int coll = 0; coll < matrix.length; coll++) {
				if (matrix[row][coll] != -1 && matrix[row][coll] < rowMinimum) {
					rowMinimum = matrix[row][coll];
				}
			}
			if (rowMinimum < Double.MAX_VALUE) {
				lowerBound += rowMinimum;
			}
			
		}
		lowerBound += pathCost;
	}
	
	/**
	 * Finds towns which are reachable from the last one
	 */
	private void findNextPossibleNodes() {
		LinkedList<Integer> newNodes = new LinkedList<>();
		for (int coll = 0; coll < matrix.length; coll++) {
			if (matrix[newNode][coll] != 0) {
				boolean isInPath = false;
				for (int pathIndex = 0; pathIndex < path.length; pathIndex++) {
					if (coll == path[pathIndex]) {
						isInPath = true;
					}
				}
				if (!isInPath) {
					newNodes.add(coll);	
				}				
			}			
		} 

		nextPossibleNodes = new int[newNodes.size()];
		for (int element = 0; element < newNodes.size(); element++) {
			nextPossibleNodes[element] = newNodes.get(element);
		}
	}
}
