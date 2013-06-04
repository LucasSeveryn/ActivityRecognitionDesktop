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
			21,
			23,
			 7,
			12,
			51,
			16,
			41,
			50,
			53,
			46,
			39,
			17,
			 4,
			13,
			66,
			19,
			76,
			47,
			44,
			49,
			37,
			56,
			 5,
			22,
			15,
			43,
			54,
			25,
			68,
			14,
			45,
			52,
			 3,
			69,
			78,
			38,
			67,
			48,
			58,
			24,
			75,
			63,
			62,
			85,
			79,
			84,
			82,
			55,
			81,
			77,
			59,
			83,
			36,
			11,
			57,
			31,
			70,
			94,
			42,
			64,
			61,
			74,
			60,
			65,
			73,
			90,
			80,
			72,
			32
	};
	double[] weights = {
			2.00842,
			1.90362,
			1.90362,
			1.85225,
			1.83463,
			1.82784,
			1.76513,
			1.75496,
			1.7364 ,
			1.65055,
			1.62761,
			1.61655,
			1.59692,
			1.58928,
			1.56968,
			1.54069,
			1.5376 ,
			1.53315,
			1.51196,
			1.50264,
			1.49833,
			1.4974 ,
			1.49706,
			1.49527,
			1.48138,
			1.48037,
			1.47657,
			1.46071,
			1.45906,
			1.45426,
			1.44838,
			1.4355 ,
			1.40742,
			1.40619,
			1.4024 ,
			1.39379,
			1.33425,
			1.31976,
			1.31704,
			1.3015 ,
			1.29358,
			1.28328,
			1.26973,
			1.26196,
			1.25153,
			1.24595,
			1.24158,
			1.23882,
			1.23825,
			1.23424,
			1.23406,
			1.23196,
			1.22484,
			1.21866,
			1.21471,
			1.21452,
			1.20573,
			1.20324,
			1.17514,
			1.1733 ,
			1.1729 ,
			1.1667 ,
			1.14785,
			1.14756,
			1.14645,
			1.14544,
			1.135  ,
			1.13376,
			1.12697,
			1.12648,
			1.07333,
			1.05079
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
