import java.util.Arrays;

/**
 * This class is used to combine the distance measurements
 * And make a decision as to which class has been identified (for the trained case)
 */
public class Decision {

	/**
	 * Return the index of the class which is picked by at least 2/3 distance measurements
	 * If all disagree, fall back to DTW (the first distance measure)
	 */
	public int majorityClassify(float[][] values) {
		
		// Find the closest distance for each measurement
		int[] mins = findClosest(values);
		
		// Add 1 to the count of the classes which have been found to be the closest
		int[] count = new int[values[0].length];
		for(int i = 0; i < mins.length; i++) {
			count[mins[i]] += 1;
		}
		
		// Find majority
		int choice = 0;
		int max = 0;
		for(int i = 0; i < count.length; i++) {
			if(count[i] > max) {
				max = count[i];
				choice = i;
			}
		}
		// No majority? Default: DTW
		if(max == 1) {
			return mins[0];
		}
		else return choice;
	}
	
	/**
	 * Weights the three distance measures and adds them to form a combined one.
	 * Also finds the smallest of the combined distance measures and returns 
	 * the integer number of the class it belongs to.
	 */
	public int weightedClassify(float[][] values, float dtwWeight, float pcaWeight, float pixelWeight) {
		
		// Get normalized data
		float[][] normVals = normalize(values);
		float minDist = Float.MAX_VALUE;
		float tempDist = 0;
		int minPos = 0;
		
		// Combine distances and find smallest
		for(int i = 0; i < normVals[0].length; i++) {
			tempDist = 0;
			tempDist += dtwWeight * normVals[0][i];
			tempDist += pcaWeight * normVals[1][i];
			tempDist += pixelWeight * normVals[2][i];
			if(tempDist < minDist) {
				minDist = tempDist;
				minPos = i;
			}
		}
		
		return minPos;
	}
	
	// Normalizes the data: dist = (dist - mean) / standard_deviation
	public float[][] normalize(float[][] values) {
		
		for(int i = 0; i < values.length; i++) {
			
			// Find mean
			float avg = 0;
			for(float val : values[i]) {
				avg += val;
			}
			avg /= values[i].length;
			
			// Find stand.dev.
			float stdDev = 0;
			for(float val : values[i]) {
				stdDev += (val - avg) * (val - avg);
			}
			stdDev = (float) Math.sqrt(stdDev/values[i].length);
			
			// Normalize
			for(int j = 0; j < values[i].length; j++) {
				values[i][j] = (values[i][j] - avg)/stdDev;
			}			
		}
		
		return values;
	}
	
	
	/**
	 * Finds the minimum distances in a set of values for each distance measurements
	 * (So it returns 3 values)
	 */
	private int[] findClosest(float[][] values) {
		
		int[] mins = new int[values.length];
		
		for(int j = 0; j < values.length; j++) {
			float minVal = Float.MAX_VALUE;
			int minPos = 0;
			for(int i = 0; i < values[j].length; i++) {
				if(values[j][i] < minVal) {
					minVal = values[j][i];
					minPos = i;
				}
			}
			mins[j] = minPos;
		}
		return mins;
	}
	
	/**
	 * Normalize the data and get the minimum distance for each example
	 * Return either the weighted minimum, or simply the smallest of the three
	 * distance measures
	 */
	public double[][] normalizeAndGetMinimum(double[][][] distances, float dtwWeight, float pcaWeight, float pixelWeight, boolean useWeights) {
		
		// If we are not using weights, set up array to find the minimum value (fill it with the largest ones)
		double[][] out = new double[distances[0].length][distances[0][0].length];
		if (!useWeights) {			
			for(int i = 0; i < out.length; i++) {
				Arrays.fill(out[i], Double.MAX_VALUE);
			}
		}
		
		// Normalize data : dist = (dist - avg) / std_dev
		double numberCount = distances[0].length * distances[0][0].length;
		for(int i = 0; i < distances.length; i++) {
			double avg = 0;
			double stddev = 0;
			
			// Find average
			for(double[] x : distances[i]) {
				for(double z : x) {
					avg += z;
				}
			}
			avg /= numberCount;
			
			// Find std.dev.
			for(double[] x : distances[i]) {
				for(double z : x) {
					stddev += (z - avg) * (z - avg);
				}
			}
			stddev = Math.sqrt(stddev/numberCount);
			
			// Normalize the distances
			for(int j = 0; j < distances[i].length; j++) {
				for(int k = 0; k < distances[i][j].length; k++) {
					distances[i][j][k] = ((distances[i][j][k] - avg)/stddev);
				}
			}
		}
		
		// Not using weights? Find the smallest distance
		if(!useWeights) {
			for(int i = 0; i < distances.length; i++) {
				for(int j = 0; j < distances[i].length; j++) {
					for(int k = 0; k < distances[i][j].length; k++) {
						if(distances[i][j][k] < out[j][k]) out[j][k] = distances[i][j][k];				
					}
				}
			}
		}
		// Otherwise compute the weighted distance
		else {
			for(int j = 0; j < distances[0].length; j++) {
				for(int k = 0; k < distances[0][j].length; k++) {
					out[j][k] += dtwWeight * distances[0][j][k];
					out[j][k] += pcaWeight * distances[1][j][k];
					out[j][k] += pixelWeight * distances[2][j][k];
				}
			}
			
		}
		return out;
	}

}
