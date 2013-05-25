import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.util.MathUtils;

import Jama.CholeskyDecomposition;
import Jama.LUDecomposition;
import Jama.Matrix;
import org.apache.commons.math3.linear.*;

public class NBC {
	ArrayList<AccFeat> lib;
	HashMap<Integer, ArrayList<Attribute>> entropy = new HashMap<Integer, ArrayList<Attribute>>();
	HashMap<Integer, CMatrix> c = new HashMap<Integer, CMatrix>();
	HashMap<Integer, ArrayList<Double>> m = new HashMap<Integer, ArrayList<Double>>();
	HashMap<Integer, ArrayList<Double>> m2 = new HashMap<Integer, ArrayList<Double>>(); // stripped means
	HashMap<Integer, HashSet<Integer>> skippedFeatures = new HashMap<Integer, HashSet<Integer>>();
	double A[][];
	double mat[][];
	int N;

	class Attribute {
		double mean;
		double variance;

		Attribute(double mean, double variance) {
			this.mean = mean;
			this.variance = variance;
		}

		double getVar() {
			return variance;
		}

		double getM() {
			return mean;
		}

		void setMean(double mean) {
			this.mean = mean;
		}

		void setVariance(double variance) {
			this.variance = variance;
		}
	}

	class CMatrix {
		double[][] mat;
		int d;

		CMatrix(int d, int type) {
			mat = new double[d][d];
			this.d = d;
			int ii = 0;
			int jj = 0;
			for (int i = 0; i < 73; i++) {
				if (!skippedFeatures.get(type).contains(new Integer(i))) {
					for (int j = 0; j < 73; j++) {
						if (!skippedFeatures.get(type).contains(new Integer(j))) {
							double covariance = getCovariance(i, j, type);
//							if(covariance<0.10&&covariance>-0.10){
//								skippedFeatures.get(type).add(arg0)
//							}else{
//							this.set(ii, jj, (double)Math.round(covariance * 100000) / 100000);
//								this.set(ii, jj, (double)Math.round(covariance * 100) / 100);
								
								this.set(ii, jj, covariance);
								jj++;
//							}
						}
					}
					jj = 0;
					ii++;
				}

			}

		}

		double get(int i, int j) {
			return mat[i][j];
		}

		int getD() {
			return d;
		}

		void set(int i, int j, double x) {
			mat[i][j] = x;
		}

		double[][] getMatrix() {
			return mat;
		}

	}

	public NBC(ArrayList<AccFeat> lib) {
		this.lib = lib;
	    ArrayList<Attribute> type0Attributes = new ArrayList<Attribute>();
	    ArrayList<Attribute> type1Attributes = new ArrayList<Attribute>();
	    ArrayList<Attribute> type2Attributes = new ArrayList<Attribute>();
	    ArrayList<Attribute> type3Attributes = new ArrayList<Attribute>();
	    ArrayList<Attribute> type4Attributes = new ArrayList<Attribute>();
	    ArrayList<Attribute> type5Attributes = new ArrayList<Attribute>();
	    ArrayList<Attribute> type6Attributes = new ArrayList<Attribute>();
	    ArrayList<Attribute> type7Attributes = new ArrayList<Attribute>();
	    ArrayList<Attribute> type8Attributes = new ArrayList<Attribute>();

	    ArrayList<Double> t0mv = new ArrayList<Double>();
	    ArrayList<Double> t1mv = new ArrayList<Double>();
	    ArrayList<Double> t2mv = new ArrayList<Double>();
	    ArrayList<Double> t3mv = new ArrayList<Double>();
	    ArrayList<Double> t4mv = new ArrayList<Double>();
	    ArrayList<Double> t5mv = new ArrayList<Double>();
	    ArrayList<Double> t6mv = new ArrayList<Double>();
	    ArrayList<Double> t7mv = new ArrayList<Double>();
	    ArrayList<Double> t8mv = new ArrayList<Double>();

	    m.put(0, t0mv);
	    // m.put(1, t1mv);
	    m.put(2, t2mv);
	    m.put(3, t3mv);
	    // m.put(4, t4mv);
	    // m.put(5, t5mv);
	    // m.put(6, t6mv);
	    m.put(7, t7mv);
	    m.put(8, t8mv);

	    ArrayList<Double> t0mv2 = new ArrayList<Double>();
	    ArrayList<Double> t1mv2 = new ArrayList<Double>();
	    ArrayList<Double> t2mv2 = new ArrayList<Double>();
	    ArrayList<Double> t3mv2 = new ArrayList<Double>();
	    ArrayList<Double> t4mv2 = new ArrayList<Double>();
	    ArrayList<Double> t5mv2 = new ArrayList<Double>();
	    ArrayList<Double> t6mv2 = new ArrayList<Double>();
	    ArrayList<Double> t7mv2 = new ArrayList<Double>();
	    ArrayList<Double> t8mv2 = new ArrayList<Double>();

	    m2.put(0, t0mv2);
	    // m2.put(1, t1mv2);
	    m2.put(2, t2mv2);
	    m2.put(3, t3mv2);
	    // m2.put(4, t4mv2);
	    // m2.put(5, t5mv2);
	    // m2.put(6, t6mv2);
	    m2.put(7, t7mv2);
	    m2.put(8, t8mv2);

	    HashSet<Integer> skipped0 = new HashSet<Integer>();
	    HashSet<Integer> skipped1 = new HashSet<Integer>();
	    HashSet<Integer> skipped2 = new HashSet<Integer>();
	    HashSet<Integer> skipped3 = new HashSet<Integer>();
	    HashSet<Integer> skipped4 = new HashSet<Integer>();
	    HashSet<Integer> skipped5 = new HashSet<Integer>();
	    HashSet<Integer> skipped6 = new HashSet<Integer>();
	    HashSet<Integer> skipped7 = new HashSet<Integer>();
	    HashSet<Integer> skipped8 = new HashSet<Integer>();

		skippedFeatures.put(0, skipped0);
		skippedFeatures.put(1, skipped1);
		skippedFeatures.put(2, skipped2);
		skippedFeatures.put(3, skipped3);
		skippedFeatures.put(4, skipped4);
		skippedFeatures.put(5, skipped5);
		skippedFeatures.put(6, skipped6);
		skippedFeatures.put(7, skipped7);
		skippedFeatures.put(8, skipped8);

		entropy.put(0, type0Attributes);
		entropy.put(1, type1Attributes);
		entropy.put(2, type2Attributes);
		entropy.put(3, type3Attributes);
		entropy.put(4, type4Attributes);
		entropy.put(5, type5Attributes);
		entropy.put(6, type6Attributes);
		entropy.put(7, type7Attributes);
		entropy.put(8, type8Attributes);

		entropy();

		CMatrix t0 = new CMatrix(73 - skippedFeatures.get(0).size(), 0);
		// CMatrix t1 = new CMatrix(73-skippedFeatures.get(1).size(), 1);
		CMatrix t2 = new CMatrix(73 - skippedFeatures.get(2).size(), 2);
		CMatrix t3 = new CMatrix(73 - skippedFeatures.get(3).size(), 3);
		// CMatrix t4 = new CMatrix(73-skippedFeatures.get(4).size(), 4);
		// CMatrix t5 = new CMatrix(73-skippedFeatures.get(5).size(), 5);
		// CMatrix t6 = new CMatrix(73-skippedFeatures.get(6).size(), 6);
		CMatrix t7 = new CMatrix(73 - skippedFeatures.get(7).size(), 7);
		CMatrix t8 = new CMatrix(73 - skippedFeatures.get(8).size(), 8);
		c.put(0, t0);
		// c.put(1, t1);
		c.put(2, t2);
		c.put(3, t3);
		// c.put(4, t4);
		// c.put(5, t5);
		// c.put(6, t6);
		c.put(7, t7);
		c.put(8, t8);

		System.out.println("features number: " + entropy.get(0).size());

	}

	public void entropy() {
		for (int i = 0; i < 9; i++) {
			if (i != 1 && i != 4 && i != 5 && i != 6) {
				for (int j = 0; j < 73; j++) {
					double mean = getSampleMean(j, i);
//					if (mean<0.5&&mean>-0.05) {
					if (mean==0){
						skippedFeatures.get(i).add(j);
					} else {
						m2.get(i).add(mean);
					}
					m.get(i).add(mean);
					entropy.get(i).add(getMeanAndVariance(j, i));
				}

			}
		}

	}

	public void classifyMVG(AccFeat q) {

	}

	public double determinant(double[][] mat) {
		double result = 0;

		if (mat.length == 1) {
			result = mat[0][0];
			return result;
		}

		if (mat.length == 2) {
			result = mat[0][0] * mat[1][1] - mat[0][1] * mat[1][0];
			return result;
		}

		for (int i = 0; i < mat[0].length; i++) {
			double temp[][] = new double[mat.length - 1][mat[0].length - 1];

			for (int j = 1; j < mat.length; j++) {
				System.arraycopy(mat[j], 0, temp[j - 1], 0, i);
				System.arraycopy(mat[j], i + 1, temp[j - 1], i, mat[0].length
						- i - 1);
			}

			result += mat[0][i] * Math.pow(-1, i) * determinant(temp);
		}

		return result;

	}

	public static final double determinant2(final double A[][]) {
		int n = A.length;
		double D = 1.0; // determinant
		double B[][] = new double[n][n]; // working matrix
		int row[] = new int[n]; // row interchange indicies
		int hold, I_pivot; // pivot indicies
		double pivot; // pivot element value
		double abs_pivot;

		if (A[0].length != n) {
			System.out
					.println("Error in Matrix.determinant, inconsistent array sizes.");
		}
		// build working matrix
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				B[i][j] = A[i][j];
		// set up row interchange vectors
		for (int k = 0; k < n; k++) {
			row[k] = k;
		}
		// begin main reduction loop
		for (int k = 0; k < n - 1; k++) {
			// find largest element for pivot
			pivot = B[row[k]][k];
			abs_pivot = Math.abs(pivot);
			I_pivot = k;
			for (int i = k; i < n; i++) {
				if (Math.abs(B[row[i]][k]) > abs_pivot) {
					I_pivot = i;
					pivot = B[row[i]][k];
					abs_pivot = Math.abs(pivot);
				}
			}
			// have pivot, interchange row indicies
			if (I_pivot != k) {
				hold = row[k];
				row[k] = row[I_pivot];
				row[I_pivot] = hold;
				D = -D;
			}
			// check for near singular
			if (abs_pivot < 1.0E-10) {
				return 0.0;
			} else {
				D = D * pivot;
				// reduce about pivot
				for (int j = k + 1; j < n; j++) {
					B[row[k]][j] = B[row[k]][j] / B[row[k]][k];
				}
				// inner reduction loop
				for (int i = 0; i < n; i++) {
					if (i != k) {
						for (int j = k + 1; j < n; j++) {
							B[row[i]][j] = B[row[i]][j] - B[row[i]][k]
									* B[row[k]][j];
						}
					}
				}
			}
			// finished inner reduction
		}
		// end of main reduction loop
		return D * B[row[n - 1]][n - 1];
	} // end determinant

	public double determinant3(double A[][], int N) {
		double det = 0;
		double res;
		if (N == 1)
			res = A[0][0];
		else if (N == 2) {
			res = A[0][0] * A[1][1] - A[1][0] * A[0][1];
		} else {
			res = 0;
			for (int j1 = 0; j1 < N; j1++) {
				mat = new double[N - 1][];
				for (int k = 0; k < (N - 1); k++)
					mat[k] = new double[N - 1];
				for (int i = 1; i < N; i++) {
					int j2 = 0;
					for (int j = 0; j < N; j++) {
						if (j == j1)
							continue;
						mat[i - 1][j2] = A[i][j];
						j2++;
					}
				}
				res += Math.pow(-1.0, 1.0 + j1 + 1.0) * A[0][j1]
						* determinant3(mat, N - 1);
			}
		}
		return res;
	}

	double determinant4(double[][] a) {
		int n = a.length - 1;
		if (n < 0)
			return 0;
		double M[][][] = new double[n + 1][][];

		M[n] = a; // init first, largest, M to a

		// create working arrays
		for (int i = 0; i < n; i++)
			M[i] = new double[i + 1][i + 1];

		return getDecDet(M, n);
	} // end method getDecDet double [][] parameter

	private static double logAdd(double logX, double logY) {
		// 1. make X the max
		if (logY > logX) {
			double temp = logX;
			logX = logY;
			logY = temp;
		}
		// 2. now X is bigger
		if (logX == Double.NEGATIVE_INFINITY) {
			return logX;
		}
		// 3. how far "down" (think decibels) is logY from logX?
		// if it's really small (20 orders of magnitude smaller), then ignore
		double negDiff = logY - logX;
		if (negDiff < -20) {
			return logX;
		}
		// 4. otherwise use some nice algebra to stay in the log domain
		// (except for negDiff)
		return logX + java.lang.Math.log(1.0 + java.lang.Math.exp(negDiff));
	}

	public void classify(AccFeat q) {
		System.out.println("\n? Starting NBC Classification");
		double[] results = new double[9];
		double result;
		ArrayList<Double> qf = new ArrayList<Double>();

		for (int j = 0; j < 73; j++) {
			qf.add(q.getFeature(j));
		}

		for (int i = 0; i < 9; i++) {
			if (i != 1 && i != 4 && i != 5 && i != 6) { // debug
				result = 1;
				for (int j = 0; j < entropy.get(i).size(); j++) {
					result = result * p(qf.get(j), entropy.get(i).get(j));

				}
				// result = 1;
				// for (int j = 0; j < entropy.get(i).size(); j++) {
				// result = logAdd(result, Math.log(p(qf.get(j),
				// entropy.get(i).get(j))));
				//
				// }
				results[i] = result;
			}
		}

		int maxindex = 0;
		double maxvalue = results[0];
		for (int i = 0; i < 9; i++) {
			if (results[i] > results[maxindex]) {
				maxvalue = results[i];
				maxindex = i;
			}
		}
		for (int i = 0; i < results.length; i++) {
			if (i != maxindex && i != 1 && i != 4 && i != 5 && i != 6) {
				System.out.println("    Type #"
						+ i
						+ " : "
						+ String.format("%.2f",
								Math.log(results[i]) / Math.log(maxvalue))
						// (results[i])/(maxvalue))
						+ " times less likely.");
			}
		}
		System.out.println("");

		System.out.println("? This is an activity of type #" + maxindex);

	}

	public void classify2(AccFeat q) {
		System.out.println("\n? Starting MV Classification");
		double[] results = new double[9];
		double result;
		ArrayList<Double> qf = new ArrayList<Double>();
	
		for (int j = 0; j < 73; j++) {
			qf.add(q.getFeature(j));
		}
	
		for (int i = 0; i < 9; i++) {
			if (i != 1 && i != 4 && i != 5 && i != 6) {
				results[i] = p2(qf, i);
				System.out.println("   Type #" + i + " probability:"
						+ Math.log(results[i]));
			}
		}
	
		int maxindex = 0;
		double maxvalue = results[0];
		for (int i = 0; i < 9; i++) {
			if (results[i] > results[maxindex]) {
				maxvalue = results[i];
				maxindex = i;
			}
		}
		System.out.println("? This is an activity of type #" + maxindex);
		for (int i = 0; i < results.length; i++) {
			if (i != maxindex && i != 1 && i != 4 && i != 5 && i != 6) {
				System.out.println("    Type #"
						+ i
						+ " : "
						+ String.format("%.2f",
								Math.log(results[i]) / Math.log(maxvalue))
						+ " times less likely.");
			}
		}
		System.out.println("");
	
	}

	private double p(double v, Attribute a) {
		double var = a.getVar();
		if (var == 0)
			return 1;
		double m = a.getM();
		double p = ((1 / (Math.sqrt(2 * Math.PI * var)) * Math.exp(-(Math.pow(v
				- m, 2))
				/ (2 * var))));

		return p;
	}

	public double determinant5(double a[][], int n) {
		double det = 0;
		int sign = 1, p = 0, q = 0;

		if (n == 1) {
			det = a[0][0];
		} else {
			double b[][] = new double[n - 1][n - 1];
			for (int x = 0; x < n; x++) {
				p = 0;
				q = 0;
				for (int i = 1; i < n; i++) {
					for (int j = 0; j < n; j++) {
						if (j != x) {
							b[p][q++] = a[i][j];
							if (q % (n - 1) == 0) {
								p++;
								q = 0;
							}
						}
					}
				}
				det = det + a[0][x] * determinant5(b, n - 1) * sign;
				sign = -sign;
			}
		}
		return det;
	}

	
		
	
	private double p2(ArrayList<Double> qf, int type) {
		Matrix coeff = new Matrix(c.get(type).getMatrix());

		double det = coeff.det();
		Matrix coeff_inv = coeff.inverse();
		
//		double detinv= coeff_inv.det();
//		System.out.println("detinv: " + detinv + " det: " + det);
		coeff.print(50, 40);
		
//		double det = coeff.det();
//		 double det2 = determinant5(c.get(type).getMatrix(),56);
//		double det2 = 0;
		// double det3 =
		// determinant3(c.get(type).getMatrix(),73-skippedFeatures.get(type).size());
//		double det3 = 0;
		// double det4 = determinant4(c.get(type).getMatrix());
//		double det4 = 0;
//		System.out.println("      det:" + det + "  det2: " + det2 + "  det3: "
//				+ det3 + "  det4: " + det4);

		ArrayList<Double> qfStripped = new ArrayList<Double>(qf);

		for (int i = qfStripped.size() - 1; i > 0; i--) {
			if (skippedFeatures.get(type).contains(new Integer(i))) {
				qfStripped.remove(i);
			}
		}

		double result = Math.pow((2 * Math.PI), -coeff.getRowDimension() / 2);
		result *= Math.pow(coeff.det(), -0.5);

		double[][] xminusmt = new double[1][73 - skippedFeatures.get(type)
				.size()];
		double[][] xminusm = new double[73 - skippedFeatures.get(type).size()][1];
		double difference;
		for (int i = 0; i < (73 - skippedFeatures.get(type).size()); i++) {
//			difference = (double)Math.round(qfStripped.get(i) - m2.get(type).get(i) * 100) / 100;
			difference = qfStripped.get(i) - m2.get(type).get(i);
			xminusmt[0][i] = difference;
			xminusm[i][0] = difference;
		}

		Matrix x_minus_m = new Matrix(xminusm);
		Matrix x_minus_m_T = new Matrix(xminusmt);

		Matrix half_times_x_minus_m_T = x_minus_m_T.times(-0.5);

		Matrix x_minus_m_T_times_C_inv = half_times_x_minus_m_T
				.times(coeff_inv);
		double norml1 = x_minus_m_T_times_C_inv.times(x_minus_m).norm1();
		double n = Math.exp(norml1);
		result *= n;

		return result;

	}

	double getSampleMean(int feature, int type) {
		double sum = 0;
		int count = 0;
		for (AccFeat a : lib) {
			if (a.getType() == type) {
				sum += a.getFeature(feature);
				count++;
			}
		}
		return sum / count;
	}

	double getSampleVariance(int feature, int type, double mean) {
		double sum = 0;
		int count = 0;
		for (AccFeat a : lib) {
			if (a.getType() == type) {
				sum += Math.pow((a.getFeature(feature) - mean), 2);
				count++;
			}
		}
		return sum / count;
	}

	double getDecDet(double[][][] M, int m) {
		if (m == 0)
			return M[0][0][0];
		int e = 1;

		// init subarray to upper left mxm submatrix
		for (int i = 0; i < m; i++)
			for (int j = 0; j < m; j++)
				M[m - 1][i][j] = M[m][i][j];
		double sum = M[m][m][m] * getDecDet(M, m - 1);

		// walk through rest of rows of M
		for (int i = m - 1; i >= 0; i--) {
			for (int j = 0; j < m; j++)
				M[m - 1][i][j] = M[m][i + 1][j];
			e = -e;
			sum += e * M[m][i][m] * getDecDet(M, m - 1);
		} // end for each row of matrix

		return sum;
	} // end getDecDet double [][][], int

	double getCovariance(int featureX, int featureY, int type) {
		//if(featureX == featureY) return 1;
		
		double meanX = m.get(type).get(featureX);
		double meanY = m.get(type).get(featureY);

		
		double sum = 0;
		int count = 0;
		for (AccFeat a : lib) {
			if (a.getType() == type) {
				sum += (a.getFeature(featureX) - meanX)
						* (a.getFeature(featureY) - meanY);
				count++;
			}
		}
		double result = sum / (count - 1);
		return result;

	}

	public Attribute getMeanAndVariance(int feature, int type) {
		// double mean = m.get(type).get(feature);
		double mean = getSampleMean(feature, type);
		double var = getSampleVariance(feature, type, mean);
		return new Attribute(mean, var);
	}

}
