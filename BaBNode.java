

public class BaBNode {
	public int dataLength;

	double[][] matrix;
	double[][] originalMatrix;
	double lowerBound;
	public int[][] path;
	
	// penalty
	double maxPenalty = 0;
	int[] maxPenaltyCoor = new int[2];
	
	
	
	public BaBNode(double[][] matrix, double lowerBound, int[][] path) {
		this.originalMatrix = matrix;
		this.lowerBound = lowerBound;
		this.path = path;
		
		
		dataLength = originalMatrix.length;
		subtractMatrix();
		computeMaxPenalty();
	}
	
	/**
	 * Subtract matrix for getting at least one zero in each row and column
	 */
	private void subtractMatrix() {
		// first matrix
		double[][] sourceMatrix = originalMatrix;
		// row subtraction
		double[][] rowSubtractionMatrix = new double[originalMatrix.length][originalMatrix.length];
		
		for (int rowIndex = 0; rowIndex < originalMatrix.length; rowIndex++) {
			double rowMin = Double.MAX_VALUE;
			// find min
			for (int colIndex = 0; colIndex < originalMatrix.length; colIndex++) {
				if (sourceMatrix[rowIndex][colIndex] != -1 && sourceMatrix[rowIndex][colIndex] < rowMin) {
					rowMin = sourceMatrix[rowIndex][colIndex];
				}
			}
			if (rowMin != Double.MAX_VALUE) { // row is not empty
				// subtract row and add subtraction to lowerBound
				lowerBound += rowMin;
				for (int colIndex = 0; colIndex < originalMatrix.length; colIndex++) {
					if (sourceMatrix[rowIndex][colIndex] != -1) {
						rowSubtractionMatrix[rowIndex][colIndex] = sourceMatrix[rowIndex][colIndex]-rowMin;
					} else {
						rowSubtractionMatrix[rowIndex][colIndex] = -1;
					}
				}
			} else {
				// row is empty
				rowSubtractionMatrix[rowIndex] = sourceMatrix[rowIndex];
			}
		}
		
		//Column subtraction
		double[][] colSubtractionMatrix = new double[originalMatrix.length][originalMatrix.length];
		for (int colIndex = 0; colIndex < originalMatrix.length; colIndex++) {
			double colMin = Double.MAX_VALUE;
			// find min
			for (int rowIndex = 0; rowIndex < originalMatrix.length; rowIndex++) {
				if (rowSubtractionMatrix[rowIndex][colIndex] != -1 && rowSubtractionMatrix[rowIndex][colIndex] < colMin) {
					colMin = rowSubtractionMatrix[rowIndex][colIndex];
				}
			}
			
				// subtract column and add subtraction to lowerBound
			if (colMin != Double.MAX_VALUE) {
				lowerBound += colMin;
			}
				
			for (int rowIndex = 0; rowIndex < originalMatrix.length; rowIndex++) {
				if (rowSubtractionMatrix[rowIndex][colIndex] != -1 && colMin != Double.MAX_VALUE) {
					colSubtractionMatrix[rowIndex][colIndex] = rowSubtractionMatrix[rowIndex][colIndex]-colMin;
				} else {
					colSubtractionMatrix[rowIndex][colIndex] = -1;
				}
			}			
		}
		
		matrix = colSubtractionMatrix;
	}
	
	
	/**
	 * Compute max penalty (for each zero in matrix find the lowest number in the column and row and sum the two numbers
	 * Max penalty is the highest sum
	 */
	private void computeMaxPenalty() {		
		for (int rowIndex = 0; rowIndex < dataLength; rowIndex++) {
			for (int colIndex = 0; colIndex < dataLength; colIndex++) {
				if (matrix[rowIndex][colIndex] == 0) { // find zero
					if (maxPenaltyCoor[0] == 0 && maxPenaltyCoor[1] == 0) { // if it is the first zero, add it to store the coordinates
						maxPenaltyCoor[0] = rowIndex;
						maxPenaltyCoor[1] = colIndex;
					}
					
					// compute penalty (the next lowest number in the row and the column
					double thisPenalty = 0;
					// Penalty for row
					double minRow = Double.MAX_VALUE;
					for (int thisRow = 0; thisRow < dataLength; thisRow++) {
						if (thisRow != colIndex && matrix[rowIndex][thisRow] != -1 && matrix[rowIndex][thisRow] < minRow) { 
							minRow = matrix[rowIndex][thisRow];
						}
					}
					if (minRow != Double.MAX_VALUE) { // if the row is not empty
						thisPenalty += minRow;
					}
					
					// penalty for column
					double minCol = Double.MAX_VALUE;
					for (int row = 0; row < dataLength; row++) {
						if (row != rowIndex && matrix[row][colIndex] != -1 && matrix[row][colIndex] < minCol) {
							minCol = matrix[row][colIndex];
						}
					}
					if (minCol != Double.MAX_VALUE) { // if the column is not empty
						thisPenalty += minCol;
					}
					
					// if the current penalty is greatest
					if (thisPenalty > maxPenalty) {
						maxPenalty = thisPenalty;
						maxPenaltyCoor[0] = rowIndex;
						maxPenaltyCoor[1] = colIndex;
					}
				}
			}
		}
		
	}
}
