import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class NBC {
	ArrayList<AccFeat> lib;
	HashMap<Integer, ArrayList<Attribute>> entropy = new HashMap<>();
	HashMap<Integer, CMatrix> c = new HashMap<>();

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
		CMatrix t0 = new CMatrix(73);
		CMatrix t1 = new CMatrix(73);
		CMatrix t2 = new CMatrix(73);
		CMatrix t3 = new CMatrix(73);
		CMatrix t4 = new CMatrix(73);
		CMatrix t5 = new CMatrix(73);
		CMatrix t6 = new CMatrix(73);
		CMatrix t7 = new CMatrix(73);
		CMatrix t8 = new CMatrix(73);
		c.put(0, t0);
		c.put(1, t0);
		c.put(2, t0);
		c.put(3, t0);
		c.put(4, t0);
		c.put(5, t0);
		c.put(6, t0);
		c.put(7, t0);
		c.put(8, t0);
		
		
		
		System.out.println("features number: " + entropy.get(0).size());

	}

	static void generateCMatrix(CMatrix c){
		int d=c.getD();
		for(int i=0;i<d;i++){
			for(int j=0;j<d;j++){
				double x=0;
				
				c.set(i, j, x);
			}
		}
	}
	
	class CMatrix {
		double[][] matrix;
		int d;
		
		CMatrix(int d) {
			matrix = new double[d][d];
			this.d=d;
		}

		double get(int i,int j){
			return matrix[i][j];
		}
		
		int getD(){
			return d;
		}
		
		void set(int i,int j,double x){
			 matrix[i][j]=x;
		}
	}
	
	
	static double covariance(){return 0;}
	
	
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
		for (int i = 0; i < 9; i++) {
			if (i != 1 && i != 4 && i != 5 && i != 6) {
				for(int j=0;j<73;j++){
					entropy.get(i).add(getMeanAndVariance(j, i));
				}

			}
		}

	}

	public void classify(AccFeat q) {
		System.out.println("\n• Starting Classification");
		double[] results = new double[9];
		double result;
		ArrayList<Double> qf = new ArrayList<>();

		for(int j=0;j<73;j++){
			qf.add(q.getFeature(j));
		}

		for (int i = 0; i < 9; i++) {
			if (i != 1 && i != 4 && i != 5 && i != 6) { // debug
				result = 1;
				for (int j = 0; j < entropy.get(i).size(); j++) {
					result = result * p(qf.get(j), entropy.get(i).get(j));

				}
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
		double p = ((1 / (Math.sqrt(2 * Math.PI * var)) * Math.exp(-(Math.pow(v
				- m, 2))
				/ (2 * var))));

		return p;
	}

	double getSampleMean(int feature, int type){
		double sum = 0;
		int count = 0;
		for (AccFeat a : lib) {
			if (a.getType() == type) {
				sum += a.getFeature(feature);
				count++;
			}
		}
		return sum/count;
	}
	
	double getSampleVariance(int feature, int type, double mean){
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
		double mean = getSampleMean(feature, type);
		double var = getSampleVariance(feature, type, mean);
		return new Attribute(mean, var);
	}


}
