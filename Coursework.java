import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Coursework {
	/*
	 * https://www.youtube.com/watch?v=-cLsEHP0qt0
	 * https://www.youtube.com/watch?v=nN4K8xA8ShM
	 */

	public static void main(String[] args) {
		
		String fourthTestFile = "finalTest4.txt"; // file for the test with the best length*time
		
		// file names for optimal result
		String[] files = {
				"test1tsp.txt",
				"x-test.txt",
				"test2atsp.txt",
				"test3atsp.txt",
				"finalTest1.txt",
				"finalTest2.txt",
				//"finalTest3.txt",
		};
		
		// solve forth test file with heuristic method
		solveForth(fourthTestFile);
		
		// solve test files for optimal result
		for (int filesIndex = 0; filesIndex < files.length; filesIndex++) {
			solveOptimal(files[filesIndex]);
		}
		
		
	}
	
	/**
	 * Solve TSP with optimal result
	 * @param fileName
	 */
	public static void solveOptimal(String fileName) {
		long startTime = System.nanoTime();
		File townFile = new File(fileName);
		int[][] towns = readFile(townFile);
		
		int[] bestPath;
		double bestLength;
		
		if (towns.length < 8) {
			// simple branch and bounds algorithm
			BranchAndBoundsSimple branchAndBounds = new BranchAndBoundsSimple(towns);
			branchAndBounds.solve();
			bestLength = branchAndBounds.getBestLength();
			bestPath = branchAndBounds.getBestPath();
		} else {
			// breath first search 
			Exhaustive breathFirst = new Exhaustive(towns);
			breathFirst.solve();
			bestLength = breathFirst.getBestLength();
			bestPath = breathFirst.getBestPath();
		}
		
		System.out.println();
		System.out.println("Test file: "+townFile.getName());
		System.out.println("Length: "+bestLength);
		System.out.println("Path: "+Arrays.toString(bestPath));
		
		System.out.println("Time to solve: "+(System.nanoTime() - startTime));
	}
	
	/**
	 * Solve TSP with heuristic algorithm with respect to pathLength * solvingTime
	 * @param fileName
	 */
	public static void solveForth(String fileName) {
		long startTime = System.nanoTime();
		File townFile = new File(fileName);
		int[][] towns = readFile(townFile);
		
		int[] bestPath;
		double bestLength;
		
		if (towns.length < 15) {
			// branch and bounds algorithm
			BranchAndBounds branchAndBounds = new BranchAndBounds(towns);
			branchAndBounds.solve();
			bestLength = branchAndBounds.getBestLength();
			bestPath = branchAndBounds.getBestPath();
		} else {
			// best first search algorithm
			Greedy greedy = new Greedy(towns);
			greedy.solve();
			bestLength = greedy.getBestLength();
			bestPath = greedy.getBestPath();
		}
		System.out.println();
		System.out.println("Test file (heuristic method): "+townFile.getName());
		System.out.println("Length: "+bestLength);
		System.out.println("Path: "+Arrays.toString(bestPath));		
		System.out.println("Time to solve: "+(System.nanoTime() - startTime));
	}
		
	/**
	 * Reads the input file
	 * @param dataFile
	 * @return
	 */
	public static int[][] readFile(File dataFile) {
		ArrayList<int[]> content = new ArrayList<>();
	    Scanner input = null;
	    try {
	        input = new Scanner(dataFile);
	    } catch (FileNotFoundException ex) {
	        System.out.println("Problem with reading a file.");
	        System.exit(0);
	    }
	    
	    // reading the file
	    while (input.hasNextLine() && input.hasNextInt()) {
	    	int[] contentLine = new int[3];
	    	Scanner line = new Scanner(input.nextLine());
	    	try {
		    	contentLine[2] = line.nextInt();
		    	contentLine[0] = line.nextInt();
		    	contentLine[1] = line.nextInt();
	    	} catch (InputMismatchException e) {
				System.out.println("Error: The file is not in the proper format.");
				System.exit(1);
	    	}
	        content.add(contentLine);
	        line.close();
	    }	    
	    input.close(); // close the file
	    
	    // Convert the list into array
	    int[][] resultArray = new int[content.size()][3];
	    for(int contentLine = 0; contentLine < content.size(); contentLine++) {
	    	resultArray[contentLine] = content.get(contentLine);
	    }
	    return resultArray;
	}

	
}
