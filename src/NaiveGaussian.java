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

	

	public NaiveGaussian(ArrayList<AccFeat> lib) {
		this.lib = lib;
        ArrayList<Double> m0 = new ArrayList<Double>();
        ArrayList<Double> m1 = new ArrayList<Double>();
        ArrayList<Double> m2 = new ArrayList<Double>();
        ArrayList<Double> m3 = new ArrayList<Double>();
        ArrayList<Double> m4 = new ArrayList<Double>();
        ArrayList<Double> m5 = new ArrayList<Double>();
        ArrayList<Double> m6 = new ArrayList<Double>();
        ArrayList<Double> m7 = new ArrayList<Double>();
        ArrayList<Double> m8 = new ArrayList<Double>();

        entropyMean.add(0, m0);
        entropyMean.add(1, m1);
        entropyMean.add(2, m2);
        entropyMean.add(3, m3);
        entropyMean.add(4, m4);
        entropyMean.add(5, m5);
        entropyMean.add(6, m6);
        entropyMean.add(7, m7);
        entropyMean.add(8, m8);
        
        ArrayList<Double> v0 = new ArrayList<Double>();
        ArrayList<Double> v1 = new ArrayList<Double>();
        ArrayList<Double> v2 = new ArrayList<Double>();
        ArrayList<Double> v3 = new ArrayList<Double>();
        ArrayList<Double> v4 = new ArrayList<Double>();
        ArrayList<Double> v5 = new ArrayList<Double>();
        ArrayList<Double> v6 = new ArrayList<Double>();
        ArrayList<Double> v7 = new ArrayList<Double>();
        ArrayList<Double> v8 = new ArrayList<Double>();

		entropyVar.add(0, v0);
		entropyVar.add(1, v1);
		entropyVar.add(2, v2);
		entropyVar.add(3, v3);
		entropyVar.add(4, v4);
		entropyVar.add(5, v5);
		entropyVar.add(6, v6);
		entropyVar.add(7, v7);
		entropyVar.add(8, v8);

		entropy();
	}

	public void entropy() {
		for (int i = 0; i < 9; i++) {
			if (i != 1 && i != 4 && i != 5 && i != 6) {
				for (int j = 0; j < 73; j++) {
					double mean = getSampleMean(j, i);
					entropyMean.get(i).add(mean);
					double var = getSampleVariance(j, i, mean);
					entropyVar.get(i).add(var);
				}

			}
		}

	}

	public void classify2(AccFeat q) {
		System.out.println("\n- Starting NBC Classification");
		double[] results = new double[9];
		double result;
		ArrayList<Double> qf = new ArrayList<>();

		for (int j = 0; j < 73; j++) {
			qf.add(q.getFeature(j));
		}

		for (int i = 0; i < 9; i++) {
			if (i != 1 && i != 4 && i != 5 && i != 6) { // debug
//				result = 1;
				result = 0;
				for (int j = 0; j < entropyMean.get(i).size(); j++) {
//					result = result * p(qf.get(j), entropyMean.get(i).get(j),entropyVar.get(i).get(j));
					//result = result - Math.log(p(qf.get(j), entropyMean.get(i).get(j),entropyVar.get(i).get(j)));
					result +=Math.log(p(qf.get(j), entropyMean.get(i).get(j),entropyVar.get(i).get(j)));
					
				}
				results[i] = result;
			}
		}

		int maxindex = 0;
		double maxvalue = results[0];

		for(int i=0;i<9;i++){
			if(i!= 1 && i != 4 && i != 5 && i != 6&&!Double.isNaN(results[i])){
				maxvalue=results[i];
				maxindex=i;
				break;
			}
		}
		
		for (int i = 0; i < 9; i++) {
			
			
			
			if (!Double.isNaN(results[i])){
				
				
				if(results[i]!=0.0){
					System.out.println("["+i+"] "+Math.exp(results[i]) + " log:" + results[i]);
				}
				if(results[i] > results[maxindex] && i != 1 && i != 4 && i != 5 && i != 6) {
					maxvalue = results[i];
					maxindex = i;
				}
			}
				
			}
//
//		for (int i = 0; i < results.length; i++) {
//			if (i != maxindex && i != 1 && i != 4 && i != 5 && i != 6) {
//				System.out.println("    Type #"
//						+ i
//						+ " : "   
//						+ String.format("%.2f", 
//								results[i] / results[maxindex]  )
//						+ " times less likely.");
//			}
//		}
		System.out.println("");

		System.out.println("- This is an activity of type #" + maxindex);

	}
	
	private double p(double v, double m, double var) {
		if (var == 0)
			return 1;
		double p = ((1 / (Math.sqrt(2 * Math.PI * var)) * Math.exp(-(Math.pow(v
				- m, 2))
				/ (2 * var))));

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
	
	public ArrayList<ArrayList<Double>> getEntropyMean(){
		return entropyMean;
	}
	
	public ArrayList<ArrayList<Double>> getEntropyVar(){
		return entropyVar;
	}
}
