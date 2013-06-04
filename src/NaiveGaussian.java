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

import com.mongodb.util.Hash;

public class NaiveGaussian {
	ArrayList<AccFeat> lib;

	ArrayList<ArrayList<Double>> entropyMean = new ArrayList<ArrayList<Double>>();
	ArrayList<ArrayList<Double>> entropyVar = new ArrayList<ArrayList<Double>>();

	int[] attr = {
			40,
			 6,
			20,
			23,
			16,
			21,
			12,
			 7,
			51,
			50,
			 4,
			17,
			41,
			53,
			39,
			46,
			66,
			 5,
			19,
			49,
			15,
			22,
			25,
			56,
			14,
			37,
			44,
			76,
			43,
			38,
			 3,
			13,
			47,
			68,
			54,
			67,
			52,
			45,
			24,
			48,
			77,
			57,
			78,
			31,
			11,
			58,
			61,
			80,
			81,
			79,
			36,
			60,
			72,
			82,
			42,
			69,
			90,
			73,
			62,
			83,
			71,
			70,
			59,
			94,
			74,
			84
	};
	double[] weights = {
			1.91464,
			1.86173,
			1.85109,
			1.78223,
			1.715  ,
			1.71424,
			1.70875,
			1.69531,
			1.64979,
			1.58758,
			1.54313,
			1.51859,
			1.50909,
			1.5041 ,
			1.47753,
			1.4756 ,
			1.46557,
			1.44619,
			1.44179,
			1.42563,
			1.39821,
			1.39804,
			1.38488,
			1.38036,
			1.36769,
			1.35894,
			1.35703,
			1.35376,
			1.32802,
			1.31612,
			1.31367,
			1.311  ,
			1.29376,
			1.28537,
			1.28117,
			1.27821,
			1.26106,
			1.25526,
			1.24823,
			1.24485,
			1.21422,
			1.17971,
			1.17504,
			1.17398,
			1.17275,
			1.1581 ,
			1.1434 ,
			1.14247,
			1.12734,
			1.11912,
			1.11113,
			1.10561,
			1.10215,
			1.10036,
			1.09901,
			1.08603,
			1.07385,
			1.07358,
			1.06918,
			1.06747,
			1.04589,
			1.03798,
			1.03701,
			1.01956,
			1.01902,
			1.00377
	};

//    mean[1]
//    mean[2]
//    sd[1]
//    crossingCount[1]
//    resultantAcc
//    energy[1]
//    energy[2]
//    correlation[0]
//    correlation[1]
//    correlation[2]
//    SMA
//    histogram[0][6]
//    histogram[1][4]
//    histogram[2][0]
//    histogram[2][4]
//    histogram[2][5]
//    AR[2][0]
	
	public NaiveGaussian(ArrayList<AccFeat> lib) {
		this.lib = lib;
		for (int j = 0; j < attr.length; j++) {
			attr[j] = attr[j] - 2;
		}
		for(Integer i : attr){
			System.out.println(WekaFileGenerator.intToAttributeName(i));
		}
		ArrayList<Double> m0 = new ArrayList<Double>();
		ArrayList<Double> m1 = new ArrayList<Double>();
		ArrayList<Double> m2 = new ArrayList<Double>();
		ArrayList<Double> m3 = new ArrayList<Double>();
		ArrayList<Double> m4 = new ArrayList<Double>();
		ArrayList<Double> m5 = new ArrayList<Double>();
		ArrayList<Double> m6 = new ArrayList<Double>();
//		ArrayList<Double> m7 = new ArrayList<Double>();
//		ArrayList<Double> m8 = new ArrayList<Double>();

		entropyMean.add(0, m0);
		entropyMean.add(1, m1);
		entropyMean.add(2, m2);
		entropyMean.add(3, m3);
		entropyMean.add(4, m4);
		entropyMean.add(5, m5);
		entropyMean.add(6, m6);
//		entropyMean.add(7, m7);
//		entropyMean.add(8, m8);

		ArrayList<Double> v0 = new ArrayList<Double>();
		ArrayList<Double> v1 = new ArrayList<Double>();
		ArrayList<Double> v2 = new ArrayList<Double>();
		ArrayList<Double> v3 = new ArrayList<Double>();
		ArrayList<Double> v4 = new ArrayList<Double>();
		ArrayList<Double> v5 = new ArrayList<Double>();
		ArrayList<Double> v6 = new ArrayList<Double>();
//		ArrayList<Double> v7 = new ArrayList<Double>();
//		ArrayList<Double> v8 = new ArrayList<Double>();

		entropyVar.add(0, v0);
		entropyVar.add(1, v1);
		entropyVar.add(2, v2);
		entropyVar.add(3, v3);
		entropyVar.add(4, v4);
		entropyVar.add(5, v5);
		entropyVar.add(6, v6);
//		entropyVar.add(7, v7);
//		entropyVar.add(8, v8);

		entropy();
	}

	public void entropy() {
		for (int i = 0; i < 6; i++) {

			for (int k = 0; k < attr.length; k++) {
				double mean = getSampleMean(attr[k], i);
				entropyMean.get(i).add(mean);
				double var = getSampleVariance(attr[k], i, mean);
				entropyVar.get(i).add(var);
			}

			// if ( i != 6) {
			// for (int j = 0; j < 6; j++) {
			// double mean = getSampleMean(j, i);
			// entropyMean.get(i).add(mean);
			// double var = getSampleVariance(j, i, mean);
			// entropyVar.get(i).add(var);
			// }
			// for (int j = 17; j < 24; j++) {
			// double mean = getSampleMean(j, i);
			// entropyMean.get(i).add(mean);
			// double var = getSampleVariance(j, i, mean);
			// entropyVar.get(i).add(var);
			// }
			//
			// for (int j = 84; j < 95; j++) {
			// double mean = getSampleMean(j, i);
			// entropyMean.get(i).add(mean);
			// double var = getSampleVariance(j, i, mean);
			// entropyVar.get(i).add(var);
			// }
			//
			// }
		}

	}

	public void classify2(AccFeat q) {
		System.out.println("\n- Starting NBC Classification");
		double[] results = new double[6];
		double result;
		ArrayList<Double> qf = new ArrayList<>();

		// for (int j = 0; j < 6; j++) {
		// qf.add(q.getFeature(j));
		// }
		// for (int j = 17; j < 24; j++) {
		// qf.add(q.getFeature(j));
		// }
		// for (int j = 84; j < 95; j++) {
		// qf.add(q.getFeature(j));
		// }

		// for(int j=0;j<attr.length;j++){
		// attr[j]=attr[j]-1;
		// }

		for (int k = 0; k < attr.length; k++) {
			qf.add(q.getFeature(attr[k]));
		}
		double weight;
		for (int i = 0; i < 6; i++) {
			
			result = 0;
			for (int j = 0; j < entropyMean.get(i).size(); j++) {
				if(weights.length>0) weight=weights[j]; else weight=1;
				result += Math.log(weight*p(qf.get(j), entropyMean.get(i).get(j),
						entropyVar.get(i).get(j)));

			}
			results[i] = result;

		}

		int maxindex = 0;
		double maxvalue = results[0];

		for (int i = 0; i < 6; i++) {
			if (i != 6 && !Double.isNaN(results[i])) {
				maxvalue = results[i];
				maxindex = i;
				break;
			}
		}

		for (int i = 0; i < 6; i++) {

			if (!Double.isNaN(results[i])) {

				if (results[i] != 0.0) {
					System.out.println("[" + i + "] " + Math.exp(results[i])
							+ " log:" + results[i]);
				}
				if (results[i] > results[maxindex] && i != 6) {
					maxvalue = results[i];
					maxindex = i;
				}
			}

		}

		for (int i = 0; i < results.length; i++) {
			if (i != maxindex && i != 6) {
				System.out.println("    Type #" + i + " : "
						+ String.format("%.2f", results[i] / results[maxindex])
						+ " times less likely.");
			}
		}
		System.out.println("");

		System.out.println("- This is an activity of type #" + maxindex);

	}

	private double p(double v, double m, double var) {
		if (var == 0)
			return 1;
		double p = ((1 / (Math.sqrt(2 * Math.PI * var)) * Math.exp(-(Math.pow(v
				- m, 2))
				/ (2 * var))));
		if (p == 0)
			return 1;
		return p;
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

	public Attribute getMeanAndVariance(int feature, int type) {
		double mean = entropyMean.get(type).get(feature);
		double var = getSampleVariance(feature, type, mean);
		return new Attribute(mean, var);
	}

	public ArrayList<ArrayList<Double>> getEntropyMean() {
		return entropyMean;
	}

	public ArrayList<ArrayList<Double>> getEntropyVar() {
		return entropyVar;
	}
}
