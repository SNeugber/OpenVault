import static org.math.array.LinearAlgebra.eigen;
import java.util.Arrays;
import org.math.array.StatisticSample;
import Jama.EigenvalueDecomposition;

/**
 * This class holds all the functions to perform Principle Component Analysis
 */
public class PCA {

	/**
	 * Get the principle components of a set of points
	 */
	public EigenvalueDecomposition getPCs (float[][] input) {

		// normalize the points, get the covariance matrix and then the eigenvalues/vectors
		double[][] norm = normalize(input);
		double[][] cov = getCov(norm);
		EigenvalueDecomposition eig = eigen(cov);

		return eig;
	}

	/**
	 * PCA works on normalized data, so get the centroid and move the character to (0,0) with regard to it
	 * Also "converts" the input from float[][] to double[][]
	 */
	private double[][] normalize (float[][] in) {

		double[][] temp = new double[in.length][2];

		// Get centroid
		float[] centroid = getCentroid(in);

		// Normalize
		for(int i = 0; i < in.length; i++) {
			temp[i][0] = in[i][0] - centroid[0];
			temp[i][1] = in[i][1] - centroid[1];
		}

		return temp;
	}

	private float[] getCentroid (float[][] inArray) {

		// When we encounter a non-zero value, we add it to the overall sum of values
		// and add the x/y/position of it to a counter (weighted by the value we encounter!)

		float ones = inArray.length;
		float xSum = 0;
		float ySum = 0;

		for (int i = 0; i < inArray.length; i++) {
			xSum += inArray[i][0];
			ySum += inArray[i][1];
		}

		// Compute x and y coordinates of centroid

		float xCen = (1/ones)*(xSum);
		float yCen = (1/ones)*(ySum);

		float[] tempArr = new float[2];
		tempArr[0] = xCen;
		tempArr[1] = yCen;

		return tempArr;
	}

	/**
	 * Returns the covariance matrix
	 */
	private double[][] getCov (double[][] in) {
		return StatisticSample.covariance(in);
	}

	/**
	 * Finds the 20 least variant points as the reference points for a given class
	 */
	public int[] getPCARefIndeces(float[][][] classRef) {
		
		// Assuming 30 points for now:
		// Reshape the input array so that we have all examples for each of the 30 points
		// in one array
		float[][][] temp = new float[30][classRef.length][3];
		for(int i = 0; i < classRef.length; i++) { // For each writer
			for(int j = 0; j < classRef[i].length; j++) { // For all of their points
				temp[j][i][0] = classRef[i][j][0];
				temp[j][i][1] = classRef[i][j][1];
				temp[j][i][2] = classRef[i][j][2];
			}
		}
		
		// Find the eigenvalues of each set of points (hardcoded again)
		double[] maxEVals = new double[30];
		for(int i = 0; i < temp.length; i++) {
			EigenvalueDecomposition eig = getPCs(temp[i]);
			double[] tempEVals = eig.getRealEigenvalues();
			double variance = Math.abs(tempEVals[0]) + Math.abs(tempEVals[1]);
			maxEVals[i] = variance;
		}

		// Sort the array and take the 20 first points = the ones with the lowest combined eigenvalues
		double[] sortMaxVals = maxEVals.clone();
		Arrays.sort(sortMaxVals);
		double[] importantEigVals = Arrays.copyOfRange(sortMaxVals, 0, 20);
		
		// Find the 20 points in the original array so that we know which indices to return
		int[] importantIndeces = new int[importantEigVals.length];
		for(int j = 0; j < importantEigVals.length; j++) {
			for(int i = 0; i < maxEVals.length; i++) {
				if(importantEigVals[j] == maxEVals[i]) {
					importantIndeces[j] = i;
				}
			}
		}
		// Sort the indices again to be able to use them properly
		Arrays.sort(importantIndeces);

		return importantIndeces;
	}
	
	/**
	 * Given a character and the principle components of a class,
	 * return the reduces character which now only consists of the 20
	 * characters which match the principle components
	 */
	public float[][] getImportantPoints(float[][] pattern, int[] importantIndeces) {
		
		float[][] newPat = new float[importantIndeces.length][];
		for(int i = 0; i < importantIndeces.length; i++) {
			newPat[i] = pattern[importantIndeces[i]];
		}

		return newPat;
	}
	
}

