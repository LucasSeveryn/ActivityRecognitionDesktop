import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class NBC {
	ArrayList<AccFeat> lib;
	HashMap<Integer, ArrayList<Attribute>> entropy = new HashMap<>();

	public NBC(ArrayList<AccFeat> lib) {
		this.lib = lib;
		ArrayList<Attribute> type0Attributes = new ArrayList<>();
		ArrayList<Attribute> type1Attributes = new ArrayList<>();
		ArrayList<Attribute> type2Attributes = new ArrayList<>();
		ArrayList<Attribute> type3Attributes = new ArrayList<>();
		ArrayList<Attribute> type4Attributes = new ArrayList<>();
		ArrayList<Attribute> type5Attributes = new ArrayList<>();
		ArrayList<Attribute> type6Attributes = new ArrayList<>();
		ArrayList<Attribute> type7Attributes = new ArrayList<>();
		ArrayList<Attribute> type8Attributes = new ArrayList<>();
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

	}

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

	public void entropy() {
		/*
		 * int id; Double[] mean = new Double[3]; Double[] sd = new Double[3];
		 * Double[] avPeakDistance = new Double[3]; int[] crossingCount = new
		 * int[3]; double resultantAcc;
		 */

		// 1. We first segment data by class. And then compute mean
		// and variance of attribute in each class
		// mean
		for (int i = 0; i < 9; i++) {
			if (i != 1 && i != 4 && i != 5 && i != 6) {
				// Mean
				entropy.get(i).add(getMeanMeanAndVariance(i, 0));
				entropy.get(i).add(getMeanMeanAndVariance(i, 1));
				entropy.get(i).add(getMeanMeanAndVariance(i, 2));

				// SD
				entropy.get(i).add(getSdMeanAndVariance(i, 0));
				entropy.get(i).add(getSdMeanAndVariance(i, 1));
				entropy.get(i).add(getSdMeanAndVariance(i, 2));

				// //Mean
				// entropy.get(i).add(getMeanMeanAndVariance(i, 0));
				// entropy.get(i).add(getMeanMeanAndVariance(i, 1));
				// entropy.get(i).add(getMeanMeanAndVariance(i, 2));
				//
				// //SD
				// entropy.get(i).add(getSdMeanAndVariance(i, 0));
				// entropy.get(i).add(getSdMeanAndVariance(i, 1));
				// entropy.get(i).add(getSdMeanAndVariance(i, 2));

				// //gMean
				// entropy.get(i).add(getgMeanMeanAndVariance(i, 0));
				// entropy.get(i).add(getgMeanMeanAndVariance(i, 1));
				// entropy.get(i).add(getgMeanMeanAndVariance(i, 2));
				//
				// //gSD
				// entropy.get(i).add(getgSdMeanAndVariance(i, 0));
				// entropy.get(i).add(getgSdMeanAndVariance(i, 1));
				// entropy.get(i).add(getgSdMeanAndVariance(i, 2));

				// avPeakDistance
				entropy.get(i).add(getAvPeakMeanAndVariance(i, 0));
				entropy.get(i).add(getAvPeakMeanAndVariance(i, 1));
				entropy.get(i).add(getAvPeakMeanAndVariance(i, 2));

				// crossingCount
				entropy.get(i).add(getCrossingMeanAndVariance(i, 0));
				entropy.get(i).add(getCrossingMeanAndVariance(i, 1));
				entropy.get(i).add(getCrossingMeanAndVariance(i, 2));

				// resultantAcc
				entropy.get(i).add(getAvResAccMeanAndVariance(i));
				// entropy.get(i).add(getgAvResAccMeanAndVariance(i));

				// FFThistogramComparison
				for (int j = 0; j < 3; j++) {
					for (int k = 0; k < 10; k++) {
						entropy.get(i).add(
								getAvFFTHistogramIndexAccMeanAndVariance(i, j,
										k));
						// System.out.println("Type #" + i + " axis: " + j +
						// " i: " + k + " | m: " +
						// getAvHistogramIndexAccMeanAndVariance(i,j,k).getM() +
						// " v: " +
						// getAvHistogramIndexAccMeanAndVariance(i,j,k).getVar());
					}

				}

				// histogramComparison
				for (int j = 0; j < 3; j++) {
					for (int k = 0; k < 10; k++) {
						entropy.get(i).add(
								getAvHistogramIndexAccMeanAndVariance(i, j, k));
						// System.out.println("Type #" + i + " axis: " + j +
						// " i: " + k + " | m: " +
						// getAvHistogramIndexAccMeanAndVariance(i,j,k).getM() +
						// " v: " +
						// getAvHistogramIndexAccMeanAndVariance(i,j,k).getVar());
					}

				}

			}
		}

	}

	public void classify(AccFeat q) {
		System.out.println("\n• Starting Classification");
		double[] results = new double[9];
		double result;
		ArrayList<Double> qf = new ArrayList<>();

		qf.add(q.getMean(0));
		qf.add(q.getMean(1));
		qf.add(q.getMean(2));

		qf.add(q.getSd(0));
		qf.add(q.getSd(1));
		qf.add(q.getSd(2));

		// qf.add(q.getMean(0));
		// qf.add(q.getMean(1));
		// qf.add(q.getMean(2));
		//
		// qf.add(q.getSd(0));
		// qf.add(q.getSd(1));
		// qf.add(q.getSd(2));
		//
		// qf.add(q.getgMean(0));
		// qf.add(q.getgMean(1));
		// qf.add(q.getgMean(2));
		//
		// qf.add(q.getgSd(0));
		// qf.add(q.getgSd(1));
		// qf.add(q.getgSd(2));

		qf.add(q.getAvPeakDistance(0));
		qf.add(q.getAvPeakDistance(1));
		qf.add(q.getAvPeakDistance(2));

		qf.add(q.getCrossingCount(0));
		qf.add(q.getCrossingCount(1));
		qf.add(q.getCrossingCount(2));

		qf.add(q.getResultantAcc());

		for (int axis = 0; axis < 3; axis++) {
			for (int index = 0; index < 10; index++) {
				qf.add((double) q.getFftHistogram(axis, index));
			}
		}

		for (int axis = 0; axis < 3; axis++) {
			for (int index = 0; index < 10; index++) {
				qf.add((double) q.getHistogram(axis, index));
			}
		}

		// qf.add(q.getgResultantAcc());

		for (int i = 0; i < 9; i++) {
			if (i != 1 && i != 4 && i != 5 && i != 6) { // debug
				result = 1;
				for (int j = 0; j < entropy.get(i).size(); j++) {
					result = result * p(qf.get(j), entropy.get(i).get(j));
					// if (Double.isNaN(result)) {
					// System.out.println("nan happens here");
					// }
				}
				results[i] = result;
				// System.out.println("            before log:" + result);
				// System.out.println("   Type #" + i + " probability:" +
				// Math.log(result));
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

		System.out.println("• This is an activity of type #" + maxindex);
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

	public double p(double v, Attribute a) {
		double var = a.getVar();
		if (var == 0)
			return 1;
		double m = a.getM();
		// if(m==0){System.out.println("here we get m 0, and v:" + v); return
		// 0;}
		double p = ((1 / (Math.sqrt(2 * Math.PI * var)) * Math.exp(-(Math.pow(v
				- m, 2))
				/ (2 * var))));

		return p;
	}

	public Attribute getMeanMeanAndVariance(int type, int index) {
		double sum = 0;
		int count = 0;
		for (AccFeat a : lib) {
			if (a.getType() == type) {
				sum += a.getMean(index);
				count++;
			}
		}
		double mean = sum / count;
		sum = 0;
		count = 0;
		for (AccFeat a : lib) {
			if (a.getType() == type) {
				sum += Math.pow((a.getMean(index) - mean), 2);
				count++;
			}
		}
		double var = sum / count;
		return new Attribute(mean, var);
	}

	public Attribute getgMeanMeanAndVariance(int type, int index) {
		double sum = 0;
		int count = 0;
		for (AccFeat a : lib) {
			if (a.getType() == type) {
				sum += a.getgMean(index);
				count++;
			}
		}
		double mean = sum / count;
		sum = 0;
		count = 0;
		for (AccFeat a : lib) {
			if (a.getType() == type) {
				sum += Math.pow((a.getgMean(index) - mean), 2);
				count++;
			}
		}
		double var = sum / count;
		return new Attribute(mean, var);
	}

	public Attribute getSdMeanAndVariance(int type, int index) {
		double sum = 0;
		int count = 0;
		for (AccFeat a : lib) {
			if (a.getType() == type) {
				sum += a.getSd(index);
				count++;
			}
		}
		double mean = sum / count;
		sum = 0;
		count = 0;
		for (AccFeat a : lib) {
			if (a.getType() == type) {
				sum += Math.pow((a.getSd(index) - mean), 2);
				count++;
			}
		}
		double var = sum / count;
		return new Attribute(mean, var);
	}

	public Attribute getgSdMeanAndVariance(int type, int index) {
		double sum = 0;
		int count = 0;
		for (AccFeat a : lib) {
			if (a.getType() == type) {
				sum += a.getgSd(index);
				count++;
			}
		}
		double mean = sum / count;
		sum = 0;
		count = 0;
		for (AccFeat a : lib) {
			if (a.getType() == type) {
				sum += Math.pow((a.getgSd(index) - mean), 2);
				count++;
			}
		}
		double var = sum / count;
		return new Attribute(mean, var);
	}

	public Attribute getAvPeakMeanAndVariance(int type, int index) {
		double sum = 0.0;
		int count = 0;

		for (AccFeat a : lib) {
			if (a.getType() == type) {
				sum += a.getAvPeakDistance(index);
				count++;
			}
		}
		double mean = sum / count;
		sum = 0;
		count = 0;
		for (AccFeat a : lib) {
			if (a.getType() == type) {
				sum += Math.pow((a.getAvPeakDistance(index) - mean), 2);
				count++;
			}
		}
		double var = sum / count;
		// System.out.println("Av. Peak Dist for type #" + type + " is: " + mean
		// + " var: " + var);
		return new Attribute(mean, var);
	}

	public Attribute getCrossingMeanAndVariance(int type, int index) {
		double sum = 0;
		int count = 0;
		for (AccFeat a : lib) {
			if (a.getType() == type) {
				sum += a.getCrossingCount(index);
				count++;
			}
		}
		double mean = sum / count;
		sum = 0;
		count = 0;
		for (AccFeat a : lib) {
			if (a.getType() == type) {
				sum += Math.pow((a.getCrossingCount(index) - mean), 2);
				count++;
			}
		}
		double var = sum / count;
		return new Attribute(mean, var);
	}

	public Attribute getAvResAccMeanAndVariance(int type) {
		double sum = 0;
		int count = 0;
		for (AccFeat a : lib) {
			if (a.getType() == type) {
				sum += a.getResultantAcc();
				count++;
			}
		}
		double mean = sum / count;
		sum = 0;
		count = 0;
		for (AccFeat a : lib) {
			if (a.getType() == type) {
				sum += Math.pow((a.getResultantAcc() - mean), 2);
				count++;
			}
		}
		double var = sum / count;
		return new Attribute(mean, var);
	}

	public Attribute getAvHistogramIndexAccMeanAndVariance(int type, int axis,
			int index) {
		double sum = 0;
		int count = 0;
		for (AccFeat a : lib) {
			if (a.getType() == type) {
				sum += a.getHistogram(axis, index);
				count++;
			}
		}
		double mean = sum / count;
		sum = 0;
		count = 0;
		for (AccFeat a : lib) {
			if (a.getType() == type) {
				sum += Math.pow((a.getHistogram(axis, index) - mean), 2);
				count++;
			}
		}
		double var = sum / count;
		return new Attribute(mean, var);
	}

	public Attribute getAvFFTHistogramIndexAccMeanAndVariance(int type,
			int axis, int index) {
		double sum = 0;
		int count = 0;
		for (AccFeat a : lib) {
			if (a.getType() == type) {
				sum += a.getFftHistogram(axis, index);
				count++;
			}
		}
		double mean = sum / count;
		sum = 0;
		count = 0;
		for (AccFeat a : lib) {
			if (a.getType() == type) {
				sum += Math.pow((a.getFftHistogram(axis, index) - mean), 2);
				count++;
			}
		}
		double var = sum / count;
		return new Attribute(mean, var);
	}

	public Attribute getgAvResAccMeanAndVariance(int type) {
		double sum = 0;
		int count = 0;
		for (AccFeat a : lib) {
			if (a.getType() == type) {
				sum += a.getgResultantAcc();
				count++;
			}
		}
		double mean = sum / count;
		sum = 0;
		count = 0;
		for (AccFeat a : lib) {
			if (a.getType() == type) {
				sum += Math.pow((a.getgResultantAcc() - mean), 2);
				count++;
			}
		}
		double var = sum / count;
		return new Attribute(mean, var);
	}

}
