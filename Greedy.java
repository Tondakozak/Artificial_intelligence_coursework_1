
import java.util.Comparator;
import java.util.LinkedList;

public class Greedy {
	private LinkedList<double[]> graph = new LinkedList<>(); // double[] {town_1, town_2, path_length}
	private LinkedList<Double> path = new LinkedList<>();
	private int[][] towns;
	
	private double bestLength = Double.MAX_VALUE;
	private int[] bestPath;

	public Greedy(int[][] towns) {
		this.towns = towns;
		initiateGraph();
	}

	/**
	 * Return best path
	 * @return
	 */
	public int[] getBestPath() {
		// convert the internal indexes of the towns to the indexes from the source file
		int[] realIndexes = new int[bestPath.length];
		for (int townsIndex = 0; townsIndex < bestPath.length; townsIndex++) {
			realIndexes[townsIndex] = towns[bestPath[townsIndex]][2];
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
	 * Solve TSP
	 */
	public void solve() {
		// To the result path add first pair with shortest distance between
		path.add(graph.getFirst()[0]);
		path.add(graph.getFirst()[1]);
		graph.removeFirst(); // remove the first pair from the source list
		
		// search in loop for next town until all towns are in result path
		while(true) {
			// find a town nearest to the first one
			double nearestFirst = findNearest(path.getFirst());
			if (nearestFirst == -1) {// there is no more towns 
				break;// terminate loop
			} else {
				path.addFirst(nearestFirst); // ad nearest town to the result path
			}
			
			
			// find a town nearest to the last one
			double nearestLast = findNearest(path.getLast());
			if (nearestLast == -1) { // there is no more towns 
				break; // terminate loop
			} else {
				path.addLast(nearestLast); // ad nearest town to the result path
			}
		}
		
		// Add last town to create a loop
		path.addLast(path.getFirst());
		
		
		// Convert the list into array
		int[] pathArray = new int[path.size()];
		for (int i = 0; i < pathArray.length; i++) {
			pathArray[i] = path.get(i).intValue();
					
		}
		
		
		bestPath = pathArray;
		bestLength = computeTotalPathLength();
	}
	
	/**
	 * Compute distances between all pairs of towns and sort the list
	 */
	private void initiateGraph() {
		
		// fill in the graph
		for (int firstTownIndex = 0; firstTownIndex < towns.length-1; firstTownIndex++) {
			for (int secondTownIndex = firstTownIndex+1; secondTownIndex < towns.length; secondTownIndex++) {
				double[] path = new double[3];
				path[0] = firstTownIndex;
				path[1] = secondTownIndex;
				path[2] = distance(towns[firstTownIndex], towns[secondTownIndex]);
				
				graph.add(path);				
			}
			
		}
		
		// sort the List
		graph.sort(new Comparator<double[]>() {
		     @Override
		     public int compare(double[] o1, double[] o2) {
		    	 return (int)(o1[2]*1000000) - (int)(o2[2]*1000000);
		     }
		 });
	}
	
	/**
	 * Compute distance between two points
	 * @param point1
	 * @param point2
	 * @return
	 */
	private double distance(int[] point1, int[] point2) {
		return Math.pow(Math.pow(point2[0]-point1[0], 2) + Math.pow(point2[1] - point1[1], 2), 1.0/2);
	}
	

	/**
	 * Find a town which is nearest to the given town and the new town is not in the result path yet
	 * @param town
	 * @return
	 */
	private double findNearest(double town) {
		for (int i = 0; i < graph.size(); i++) {
			if (graph.get(i)[0] == town || graph.get(i)[1] == town) { // if one town of the pair is the same as the given one
				double nextTown = (graph.get(i)[0] == town)?graph.get(i)[1]:graph.get(i)[0]; // new town will be the one from the pair, which is not the previous town
				if (!path.contains(nextTown)) { // if the new town is not in result path yet
					return nextTown;
				}
			}
		}
		return -1; // no town was found
	}
	
	/**
	 * Compute total length of the path
	 * @return
	 */
	private double computeTotalPathLength() {
		double total = 0; 
		
		for (int i = 0; i < path.size()-1; i++) {
			total += distance(towns[path.get(i).intValue()], towns[path.get(i+1).intValue()]);
		}
		return total;
	}
	
	
}


