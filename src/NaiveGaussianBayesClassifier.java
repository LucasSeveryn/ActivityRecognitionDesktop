import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.*;
import org.apache.commons.math3.util.Pair;

public class NaiveGaussianBayesClassifier {
	List<AccFeat> lib;

	List<ArrayList<Double>> entropyMean = new ArrayList<ArrayList<Double>>();
	ArrayList<ArrayList<Double>> entropyVar = new ArrayList<ArrayList<Double>>();

	int[] attr = { 6, 20, 40, 23, 16, 21, 7, 17, 12, 51, 41, 53, 50, 4, 39, 5,
			66, 19, 22, 56, 49, 76, 15, 46, 14, 3, 67, 13, 54, 25, 44, 38, 77,
			9, 24, 52, 43, 45, 37, 68, 31, 11, 78, 81, 57, 47, 80, 82, 48, 58,
			60, 36, 30, 79, 61, 42, 71, 72, 73, 59, 69, 62, 70, 83, 32, 2, 74,
			84, 55, 90 };
//	double[] weights = { 2.1178, 2.1049, 2.1011, 2.0322, 1.9589, 1.8879,
//			1.8677, 1.7185, 1.7182, 1.6857, 1.6677, 1.666, 1.6497, 1.6487,
//			1.6168, 1.6081, 1.6052, 1.5946, 1.564, 1.4989, 1.4961, 1.4956,
//			1.495, 1.4859, 1.4538, 1.4516, 1.4501, 1.435, 1.4058, 1.405,
//			1.3833, 1.3733, 1.3604, 1.3571, 1.353, 1.3406, 1.3099, 1.298,
//			1.2734, 1.2666, 1.2559, 1.2543, 1.229, 1.2253, 1.2159, 1.2077,
//			1.1925, 1.1649, 1.1628, 1.1434, 1.1417, 1.1379, 1.1368, 1.1366,
//			1.1313, 1.122, 1.1182, 1.1149, 1.1138, 1.1059, 1.0957, 1.0844,
//			1.079, 1.0745, 1.052, 1.0485, 1.0353, 1.0226, 1.0161, 1.0105 };
//	int[] attr = { 
//			2,3,4,5,6,7};
	int[] weights={};
	int[] types = {0,1,2,3,4,5,6,7};

	
	
	public NaiveGaussianBayesClassifier(List<AccFeat> lib) {
		this.lib = lib;
		for (int j = 0; j < attr.length; j++) {
			attr[j] = attr[j] - 2;
		}
		// for(Integer i : attr){
		// System.out.println(WekaFileGenerator.intToAttributeName(i));
		// }
		ArrayList<Double> m0 = new ArrayList<Double>();
		ArrayList<Double> m1 = new ArrayList<Double>();
		ArrayList<Double> m2 = new ArrayList<Double>();
		ArrayList<Double> m3 = new ArrayList<Double>();
		ArrayList<Double> m4 = new ArrayList<Double>();
		ArrayList<Double> m5 = new ArrayList<Double>();
		ArrayList<Double> m6 = new ArrayList<Double>();
		 ArrayList<Double> m7 = new ArrayList<Double>();
		// ArrayList<Double> m8 = new ArrayList<Double>();

		entropyMean.add(0, m0);
		entropyMean.add(1, m1);
		entropyMean.add(2, m2);
		entropyMean.add(3, m3);
		entropyMean.add(4, m4);
		entropyMean.add(5, m5);
		entropyMean.add(6, m6);
		 entropyMean.add(7, m7);
		// entropyMean.add(8, m8);

		ArrayList<Double> v0 = new ArrayList<Double>();
		ArrayList<Double> v1 = new ArrayList<Double>();
		ArrayList<Double> v2 = new ArrayList<Double>();
		ArrayList<Double> v3 = new ArrayList<Double>();
		ArrayList<Double> v4 = new ArrayList<Double>();
		ArrayList<Double> v5 = new ArrayList<Double>();
		ArrayList<Double> v6 = new ArrayList<Double>();
		 ArrayList<Double> v7 = new ArrayList<Double>();
		// ArrayList<Double> v8 = new ArrayList<Double>();

		entropyVar.add(0, v0);
		entropyVar.add(1, v1);
		entropyVar.add(2, v2);
		entropyVar.add(3, v3);
		entropyVar.add(4, v4);
		entropyVar.add(5, v5);
		entropyVar.add(6, v6);
		 entropyVar.add(7, v7);
		// entropyVar.add(8, v8);

		entropy();
	}

	public void entropy() {
		for (int i = 0; i < types.length; i++) {
			for (int k = 0; k < attr.length; k++) {
				double mean = getSampleMean(attr[k], types[i]);
				entropyMean.get(types[i]).add(mean);
				double var = getSampleVariance(attr[k], types[i], mean);
				entropyVar.get(types[i]).add(var);
			}

		}

	}

	public Pair<ArrayList<Double>, String> classify(AccFeat q) {
		String txt = "";
		txt += ("Gaussian Naive Bayes Classification");
		double[] results = new double[types.length];
		double result;
		ArrayList<Double> qf = new ArrayList<Double>();

		for (int k = 0; k < attr.length; k++) {
			qf.add(q.getFeature(attr[k]));
		}

		double weight;
		for (int i = 0; i < types.length; i++) {
			result = 0;
			for (int j = 0; j < entropyMean.get(types[i]).size(); j++) {
				if(weights.length>0) weight=weights[j]; else weight=1;
				result += Math.log(weight*p(qf.get(j), entropyMean.get(types[i]).get(j),
						entropyVar.get(types[i]).get(j)));

			}
			results[i] = result;
		
	}

		int maxindex = 0;
		double maxvalue = results[0];

		for (int i = 0; i < results.length; i++) {
			if (!Double.isNaN(results[i])) {
				maxvalue = results[i];
				maxindex = i;
				break;
			}
		}

		for (int i = 0; i < types.length; i++) {
			if (!Double.isNaN(results[i])) {
				DecimalFormat df = new DecimalFormat("0.00E000");
				txt += ("\n[" + types[i] + "] " 
//				+ df.format(Math.exp(results[i]))
						+ " log:" + String.format("%.5f", results[i]));
				// }
				if (results[i] > results[maxindex]) {
					maxvalue = results[i];
					maxindex = i;
				}
			}

		}

		// for (int i = 0; i < results.length; i++) {
		// if (i != maxindex) {
		// String value = String.format("%.2f", results[i] / results[maxindex]);
		// if(value.equals("Infinity")) value = "inf.";
		// txt += ("\n   Type #" + i + " : "
		// + value + " times less likely.");
		// }
		// }
		// txt += ("\n");

		txt += ("\nClassified as type: " + FeatureExtractors.getType(types[maxindex]));

		ArrayList<Double> resultsArrayList = new ArrayList<Double>();
		for (Double d : results) {
			resultsArrayList.add(d);
		}

		org.apache.commons.math3.util.Pair<ArrayList<Double>, String> pair = new org.apache.commons.math3.util.Pair<ArrayList<Double>, String>(
				resultsArrayList, txt);
		return pair;

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

	
	public List<ArrayList<Double>> getEntropyMean() {
		return entropyMean;
	}

	public ArrayList<ArrayList<Double>> getEntropyVar() {
		return entropyVar;
	}

	public int[] getTypes() {
		return types;
	}
}
