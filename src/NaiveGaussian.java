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
			21,
			 7,
			16,
			12,
			51,
			41,
			50,
			53,
			39,
			17,
			13,
			46,
			66,
			44,
			47,
			76,
			49,
			37,
			22,
			 5,
			 4,
			56,
			19,
			43,
			68,
			15,
			54,
			45,
			14,
			52,
			25,
			69,
			67,
			 3,
			38,
			78,
			48,
			58,
			24,
			63,
			77,
			75,
			79,
			62,
			55,
			85,
			82,
			84,
			81,
			57,
			83,
			11,
			59,
			90,
			36,
			74,
			94,
			70,
			42,
			31,
			64,
			61,
			60,
			73,
			65,
			80,
			32,
			72,
			30
	};
	double[] weights = {
			 2.00348,
			 1.95511,
			 1.95511,
			 1.87402,
			 1.86996,
			 1.83862,
			 1.8181,
			 1.81198,
			 1.76002,
			 1.68145,
			 1.63202,
			 1.62493,
			 1.60145,
			 1.59761,
			 1.568,  
			 1.5631, 
			 1.56102,
			 1.52887,
			 1.5253, 
			 1.52467,
			 1.52263,
			 1.52116,
			 1.51781,
			 1.51497,
			 1.50329,
			 1.50318,
			 1.50162,
			 1.4822 ,
			 1.48164,
			 1.47304,
			 1.4708 ,
			 1.43623,
			 1.42213,
			 1.40987,
			 1.4051 ,
			 1.34989,
			 1.3476 ,
			 1.34702,
			 1.33112,
			 1.33041,
			 1.30703,
			 1.29392,
			 1.28322,
			 1.27532,
			 1.27209,
			 1.26361,
			 1.25536,
			 1.2515 ,
			 1.24914,
			 1.24594,
			 1.24491,
			 1.24148,
			 1.23432,
			 1.21793,
			 1.21774,
			 1.21271,
			 1.20254,
			 1.18993,
			 1.18865,
			 1.18744,
			 1.18312,
			 1.17821,
			 1.17119,
			 1.16637,
			 1.15158,
			 1.15127,
			 1.14181,
			 1.13615,
			 1.13572,
			 1.12988,
			 1.10058,
			 1.07186,
			 1.01885
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
