import com.mongodb.*;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.math3.util.Pair;
import org.bson.types.ObjectId;

public class ActRecognitionDesktop {
	static ArrayList<AccData> accDataLibrary = new ArrayList<AccData>();
	static ArrayList<AccData> accTestDataLibrary = new ArrayList<AccData>();
	static ArrayList<AccFeat> accFeatLibrary = new ArrayList<AccFeat>();
	static ArrayList<AccFeat> accTestFeatLibrary = new ArrayList<AccFeat>();
	static NaiveGaussianBayesClassifier ng;
	static MultivariateNormalBayesClassifier mvg;
	static MultivariateNormalBayesClassifier mvg2;

	static boolean splitIntoTwo = true;


	
	public static void main(String[] args) {
		loadTrainingDataFromCloud();
		loadTestDataFromCloud();
		buildAccFeatLibrary();
		buildTestAccFeatLibrary();

		ng = new NaiveGaussianBayesClassifier(accFeatLibrary);
		mvg = new MultivariateNormalBayesClassifier(accFeatLibrary);

		while (true) {
			displayMenu();
			Scanner in = new Scanner(System.in);
			int selection = in.nextInt();

			if (selection == 2) {
				findMisclassifications();
			} else if (selection == 3) {
				WekaFileGenerator.generateFile(accFeatLibrary);
			}
				else if (selection == 4) {
				System.out.println("sending..");
				sendMeanVarVectors();
			}	else if (selection == 5) {
				sendMatrices();
				sendMvcMeanVarVectors();
			} else if (selection==1) {
				System.out.println("Classify sample with index: ");
				selection = in.nextInt();
				System.out.println(mvg.classify(
						accTestFeatLibrary.get(selection)).getSecond());
				System.out.println(ng.classify(
						accTestFeatLibrary.get(selection)).getSecond());
				System.out.println("Labeled as type: "
						+ FeatureExtractors.getType(accTestFeatLibrary.get(
								selection).getType()));

			}
		}

	}
	
	private static BasicDBList addRows(double[][] mvgMatrix){
		BasicDBList matrix = new BasicDBList();
		for(int i=0;i<6;i++){
			BasicDBList row = new BasicDBList();
			for(int j=0;j<6;j++){
				row.add(mvgMatrix[i][j]);
			}
			matrix.add(row);
		}
		return matrix;
	}

	private static void sendMatrices() {
		String username = "acclibrary";
		String password = "acclibrary";
		String uriString = "mongodb://" + username + ":" + password
				+ "@ds059957.mongolab.com:59957/activity_recognition";

		MongoURI uri = new MongoURI(uriString);
		DB database = null;
		DBCollection ed = null;

		try {
			database = uri.connectDB();
		} catch (UnknownHostException uhe) {
			System.out.println("UnknownHostException: " + uhe);
		} catch (MongoException me) {
			System.out.println("MongoException: " + me);
		}

		if (database != null) {
			ed = database.getCollection("entropy_data");
			ed.drop();
			ed = database.getCollection("mvc_matrices");
			System.out
					.println("º Connection to Mongolab database established.");

			double[][] m0 = mvg.cMatrices.get(0).getMatrix();
			double[][] m1 = mvg.cMatrices.get(1).getMatrix();
			double[][] m2 = mvg.cMatrices.get(2).getMatrix();
			double[][] m3 = mvg.cMatrices.get(3).getMatrix();
			double[][] m4 = mvg.cMatrices.get(4).getMatrix();
			double[][] m5 = mvg.cMatrices.get(5).getMatrix();
			double[][] m6 = mvg.cMatrices.get(6).getMatrix();
			double[][] m7 = mvg.cMatrices.get(7).getMatrix();
			
			BasicDBList matrix0 = addRows(m0);
			BasicDBList matrix1 = addRows(m1);
			BasicDBList matrix2 = addRows(m2);
			BasicDBList matrix3 = addRows(m3);
			BasicDBList matrix4 = addRows(m4);
			BasicDBList matrix5 = addRows(m5);
			BasicDBList matrix6 = addRows(m6);
			BasicDBList matrix7 = addRows(m7);
		
			BasicDBObject matrix0obj = new BasicDBObject("matrix0", matrix0);
			BasicDBObject matrix1obj = new BasicDBObject("matrix1", matrix1);
			BasicDBObject matrix2obj = new BasicDBObject("matrix2", matrix2);
			BasicDBObject matrix3obj = new BasicDBObject("matrix3", matrix3);
			BasicDBObject matrix4obj = new BasicDBObject("matrix4", matrix4);
			BasicDBObject matrix5obj = new BasicDBObject("matrix5", matrix5);
			BasicDBObject matrix6obj = new BasicDBObject("matrix6", matrix6);
			BasicDBObject matrix7obj = new BasicDBObject("matrix7", matrix7);

			ed.insert(matrix0obj);
			ed.insert(matrix1obj);
			ed.insert(matrix2obj);
			ed.insert(matrix3obj);
			ed.insert(matrix4obj);
			ed.insert(matrix5obj);
			ed.insert(matrix6obj);
			ed.insert(matrix7obj);


		}
		
	}

	public static void displayMenu() {
		System.out
				.println("\n\nº Select Action.\n(1) Classify single sample");
		System.out
				.println("(2) Classify all samples");
		System.out
				.println("(3) Export to .arff file");
		System.out
				.println("(4) Send NGBC data to the cloud");
		System.out
				.println("(5) Send MVC matrices to the cloud");
		System.out
				.println("    Selection: ");
	}

	public static void findMisclassifications() {
		ArrayList<String> maxProbabilities = new ArrayList<String>();
		ArrayList<String> maxMVCProbabilities = new ArrayList<String>();

		int counter = 0;
		int mvgbcWrong = 0;
		int ngbcWrong = 0;
		int[] mvcWrong = new int[8]; 
		int[] nvcWrong = new int[8]; 
		for (AccFeat a : accTestFeatLibrary) {
			Pair<ArrayList<Double>, String> p = ng.classify(a);
			// System.out.println(ng.classify(a).getSecond());
			ClassificationResult c = new ClassificationResult(p.getFirst(),
					null);

			Pair<ArrayList<Double>, String> p2 = mvg.classify(a);
			ClassificationResult c2 = new ClassificationResult(p2.getFirst(),
					null);
			if (c.getResult() != a.getType() || c2.getResult() != a.getType()) {
				System.out.println("-Classification\nLabeled as type: "
						+ FeatureExtractors.getType(a.getType()));
			}
			if (c.getResult() != a.getType()) {
				ngbcWrong++;
				System.out.println("NGBC:");
				System.out.println("Classified as type: "
						+ FeatureExtractors.getType(c.getResult()) + " | p: "
						+ c.getMaxProbabilityValue());
				nvcWrong[a.getType()]++;
			}
			if (c2.getResult() != a.getType()) {
				mvgbcWrong++;
				System.out.println("MVGBC:");
				System.out.println("Classified as type: "
						+ FeatureExtractors.getType(c2.getResult()) + " | p: "
						+ c2.getMaxProbabilityValue());
				mvcWrong[a.getType()]++;
			}

			maxProbabilities.add(c.getMaxProbabilityValue() + "," + c.getSecondMaxProbabilityValue());
			maxMVCProbabilities.add(c2.getMaxProbabilityValue() + "," + c2.getSecondMaxProbabilityValue());

		}

		Collections.sort(maxProbabilities);
		System.out.println("NBC");
		for (String d : maxProbabilities) {
			System.out.println(counter + " : " + d);
			counter++;
		}
		System.out.println("MVC");
		for (String d : maxMVCProbabilities) {
			System.out.println(counter + " : " + d);
			counter++;
		}
		for(int i=0;i<mvcWrong.length;i++){
			System.out.println("type #" +i+ " wrong: " + mvcWrong[i]); 
		}

		System.out.println("NGBC: " + ngbcWrong + "/" + counter);
		System.out.println("MVGBC: " + mvgbcWrong + "/" + counter);
	}

	public static void buildAccFeatLibrary() {
		System.out.println("• Building Training Feature Library");
		for (AccData a : accDataLibrary) {
			AccFeat temp = FeatureExtractors.buildFeatureObject(a);
			temp.setType(a.getType());
			// if (a.getType() == 9)
			// accTestFeatLibrary.add(temp);
			// else
			if (a.getType() < 8)
				accFeatLibrary.add(temp);

		}
		System.out.println("  Feature library size: " + accFeatLibrary.size());
	}

	public static void buildTestAccFeatLibrary() {
		System.out.println("• Building Test Feature Library");
		for (AccData a : accTestDataLibrary) {
			AccFeat temp = FeatureExtractors.buildFeatureObject(a);
			temp.setType(a.getType());
			accTestFeatLibrary.add(temp);
		}
		System.out.println("  Test Feature library size: "
				+ accTestDataLibrary.size());
	}

	private static List<Double> toDoubleList(BasicDBList list) {
		List<Double> result = new ArrayList<Double>();
		for (int i = 0; i < list.size(); i++) {
			result.add((Double) list.get(i));
		}
		return result;
	}

	public static void sendMeanVarVectors() {
		String username = "acclibrary";
		String password = "acclibrary";
		String uriString = "mongodb://" + username + ":" + password
				+ "@ds059957.mongolab.com:59957/activity_recognition";

		MongoURI uri = new MongoURI(uriString);
		DB database = null;
		DBCollection ed = null;

		try {
			database = uri.connectDB();
		} catch (UnknownHostException uhe) {
			System.out.println("UnknownHostException: " + uhe);
		} catch (MongoException me) {
			System.out.println("MongoException: " + me);
		}

		if (database != null) {
			ed = database.getCollection("m_v_vectors");
			ed.drop();
			ed = database.getCollection("m_v_vectors");
			System.out
					.println("º Connection to Mongolab database established.");

			List<ArrayList<Double>> entropyMean = ng.getEntropyMean();
			List<ArrayList<Double>> entropyVar = ng.getEntropyVar();

			BasicDBList mean0 = new BasicDBList();
			BasicDBList mean1 = new BasicDBList();
			BasicDBList mean2 = new BasicDBList();
			BasicDBList mean3 = new BasicDBList();
			BasicDBList mean4 = new BasicDBList();
			BasicDBList mean5 = new BasicDBList();
			BasicDBList mean6 = new BasicDBList();
			BasicDBList mean7 = new BasicDBList();
			BasicDBList mean8 = new BasicDBList();

			addElements(mean0, entropyMean.get(0));
			addElements(mean1, entropyMean.get(1));
			addElements(mean2, entropyMean.get(2));
			addElements(mean3, entropyMean.get(3));
			addElements(mean4, entropyMean.get(4));
			addElements(mean5, entropyMean.get(5));
			addElements(mean6, entropyMean.get(6));
			addElements(mean7, entropyMean.get(7));
			// addElements(mean8, entropyMean.get(8));

			BasicDBList var0 = new BasicDBList();
			BasicDBList var1 = new BasicDBList();
			BasicDBList var2 = new BasicDBList();
			BasicDBList var3 = new BasicDBList();
			BasicDBList var4 = new BasicDBList();
			BasicDBList var5 = new BasicDBList();
			BasicDBList var6 = new BasicDBList();
			BasicDBList var7 = new BasicDBList();
			// BasicDBList var8 = new BasicDBList();

			addElements(var0, entropyVar.get(0));
			addElements(var1, entropyVar.get(1));
			addElements(var2, entropyVar.get(2));
			addElements(var3, entropyVar.get(3));
			addElements(var4, entropyVar.get(4));
			addElements(var5, entropyVar.get(5));
			addElements(var6, entropyVar.get(6));
			addElements(var7, entropyVar.get(7));
			// addElements(var8, entropyVar.get(8));

			BasicDBObject mean0obj = new BasicDBObject("mean0", mean0);
			BasicDBObject mean1obj = new BasicDBObject("mean1", mean1);
			BasicDBObject mean2obj = new BasicDBObject("mean2", mean2);
			BasicDBObject mean3obj = new BasicDBObject("mean3", mean3);
			BasicDBObject mean4obj = new BasicDBObject("mean4", mean4);
			BasicDBObject mean5obj = new BasicDBObject("mean5", mean5);
			BasicDBObject mean6obj = new BasicDBObject("mean6", mean6);
			BasicDBObject mean7obj = new BasicDBObject("mean7", mean7);
			// BasicDBObject mean8obj = new BasicDBObject("mean8", mean8);

			BasicDBObject var0obj = new BasicDBObject("var0", var0);
			BasicDBObject var1obj = new BasicDBObject("var1", var1);
			BasicDBObject var2obj = new BasicDBObject("var2", var2);
			BasicDBObject var3obj = new BasicDBObject("var3", var3);
			BasicDBObject var4obj = new BasicDBObject("var4", var4);
			BasicDBObject var5obj = new BasicDBObject("var5", var5);
			BasicDBObject var6obj = new BasicDBObject("var6", var6);
			BasicDBObject var7obj = new BasicDBObject("var7", var7);
			// BasicDBObject var8obj = new BasicDBObject("var8", var8);

			ed.insert(mean0obj);
			ed.insert(mean1obj);
			ed.insert(mean2obj);
			ed.insert(mean3obj);
			ed.insert(mean4obj);
			ed.insert(mean5obj);
			ed.insert(mean6obj);
			ed.insert(mean7obj);
			// ed.insert(mean8obj);

			ed.insert(var0obj);
			ed.insert(var1obj);
			ed.insert(var2obj);
			ed.insert(var3obj);
			ed.insert(var4obj);
			ed.insert(var5obj);
			ed.insert(var6obj);
			ed.insert(var7obj);
			// ed.insert(var8obj);
			System.out.println("data sent");
		}
	}

	public static void sendMvcMeanVarVectors() {
		String username = "acclibrary";
		String password = "acclibrary";
		String uriString = "mongodb://" + username + ":" + password
				+ "@ds059957.mongolab.com:59957/activity_recognition";

		MongoURI uri = new MongoURI(uriString);
		DB database = null;
		DBCollection ed = null;

		try {
			database = uri.connectDB();
		} catch (UnknownHostException uhe) {
			System.out.println("UnknownHostException: " + uhe);
		} catch (MongoException me) {
			System.out.println("MongoException: " + me);
		}

		if (database != null) {
			ed = database.getCollection("mvc_m_v_vectors");
			ed.drop();
			ed = database.getCollection("mvc_m_v_vectors");
			System.out
					.println("º Connection to Mongolab database established.");

			List<ArrayList<Double>> entropyMean = mvg.getEntropyMean();
			List<ArrayList<Double>> entropyVar = mvg.getEntropyVar();

			BasicDBList mean0 = new BasicDBList();
			BasicDBList mean1 = new BasicDBList();
			BasicDBList mean2 = new BasicDBList();
			BasicDBList mean3 = new BasicDBList();
			BasicDBList mean4 = new BasicDBList();
			BasicDBList mean5 = new BasicDBList();
			BasicDBList mean6 = new BasicDBList();
			BasicDBList mean7 = new BasicDBList();
			BasicDBList mean8 = new BasicDBList();

			addElements(mean0, entropyMean.get(0));
			addElements(mean1, entropyMean.get(1));
			addElements(mean2, entropyMean.get(2));
			addElements(mean3, entropyMean.get(3));
			addElements(mean4, entropyMean.get(4));
			addElements(mean5, entropyMean.get(5));
			addElements(mean6, entropyMean.get(6));
			addElements(mean7, entropyMean.get(7));
			// addElements(mean8, entropyMean.get(8));

			BasicDBList var0 = new BasicDBList();
			BasicDBList var1 = new BasicDBList();
			BasicDBList var2 = new BasicDBList();
			BasicDBList var3 = new BasicDBList();
			BasicDBList var4 = new BasicDBList();
			BasicDBList var5 = new BasicDBList();
			BasicDBList var6 = new BasicDBList();
			BasicDBList var7 = new BasicDBList();
			// BasicDBList var8 = new BasicDBList();

			addElements(var0, entropyVar.get(0));
			addElements(var1, entropyVar.get(1));
			addElements(var2, entropyVar.get(2));
			addElements(var3, entropyVar.get(3));
			addElements(var4, entropyVar.get(4));
			addElements(var5, entropyVar.get(5));
			addElements(var6, entropyVar.get(6));
			addElements(var7, entropyVar.get(7));
			// addElements(var8, entropyVar.get(8));

			BasicDBObject mean0obj = new BasicDBObject("mean0", mean0);
			BasicDBObject mean1obj = new BasicDBObject("mean1", mean1);
			BasicDBObject mean2obj = new BasicDBObject("mean2", mean2);
			BasicDBObject mean3obj = new BasicDBObject("mean3", mean3);
			BasicDBObject mean4obj = new BasicDBObject("mean4", mean4);
			BasicDBObject mean5obj = new BasicDBObject("mean5", mean5);
			BasicDBObject mean6obj = new BasicDBObject("mean6", mean6);
			BasicDBObject mean7obj = new BasicDBObject("mean7", mean7);
			// BasicDBObject mean8obj = new BasicDBObject("mean8", mean8);

			BasicDBObject var0obj = new BasicDBObject("var0", var0);
			BasicDBObject var1obj = new BasicDBObject("var1", var1);
			BasicDBObject var2obj = new BasicDBObject("var2", var2);
			BasicDBObject var3obj = new BasicDBObject("var3", var3);
			BasicDBObject var4obj = new BasicDBObject("var4", var4);
			BasicDBObject var5obj = new BasicDBObject("var5", var5);
			BasicDBObject var6obj = new BasicDBObject("var6", var6);
			BasicDBObject var7obj = new BasicDBObject("var7", var7);
			// BasicDBObject var8obj = new BasicDBObject("var8", var8);

			ed.insert(mean0obj);
			ed.insert(mean1obj);
			ed.insert(mean2obj);
			ed.insert(mean3obj);
			ed.insert(mean4obj);
			ed.insert(mean5obj);
			ed.insert(mean6obj);
			ed.insert(mean7obj);
			// ed.insert(mean8obj);

			ed.insert(var0obj);
			ed.insert(var1obj);
			ed.insert(var2obj);
			ed.insert(var3obj);
			ed.insert(var4obj);
			ed.insert(var5obj);
			ed.insert(var6obj);
			ed.insert(var7obj);
			// ed.insert(var8obj);
			System.out.println("data sent");
		}
	}
	
	static public void addElements(BasicDBList bdblist, ArrayList<Double> list) {
		for (int i = 0; i < list.size(); i++) {
			bdblist.add(new BasicDBObject(Integer.toString(i), list.get(i)));
		}
	}

	public static void loadTestDataFromCloud() {
		String username = "acclibrary";
		String password = "acclibrary";
		String uriString = "mongodb://" + username + ":" + password
				+ "@ds059957.mongolab.com:59957/activity_recognition";

		MongoURI uri = new MongoURI(uriString);
		DB database = null;
		DBCollection acc_db = null;

		try {
			database = uri.connectDB();
		} catch (UnknownHostException uhe) {
			System.out.println("UnknownHostException: " + uhe);
		} catch (MongoException me) {
			System.out.println("MongoException: " + me);
		}

		if (database != null) {
			acc_db = database.getCollection("accelerometer_data_test_samples");
			System.out
					.println("º Connection to Mongolab database established.");
			System.out.println("   Loading test samples.");
			DBCursor results = acc_db.find();
			int resultCounter = 0;
			int[] typeCounter = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			while (results.hasNext()) {

				DBObject result = results.next();
				resultCounter++;

				ObjectId objid = (ObjectId) result.get("_id");
				int id = objid.hashCode();
				int type = (int) result.get("type");

				BasicDBList xData = (BasicDBList) result.get("xData");
				BasicDBList yData = (BasicDBList) result.get("yData");
				BasicDBList zData = (BasicDBList) result.get("zData");

				AccData temp = new AccData(type, toDoubleList(xData),
						toDoubleList(yData), toDoubleList(zData));

				if (splitIntoTwo && temp.size() > 300) {
					accTestDataLibrary.add(temp.getFirstHalfOfElements());
					typeCounter[type]++;
					accTestDataLibrary.add(temp.getSecondHalfOfElements());
					typeCounter[type]++;
				} else {
					accTestDataLibrary.add(temp);
					typeCounter[type]++;
				}

			}
			System.out.println("º " + resultCounter
					+ " records loaded. Type breakdown:");
			for (int i = 0; i < 9; i++) {
				if (typeCounter[i] > 0)
					System.out.println("   type #" + i + " count: "
							+ typeCounter[i]);
			}
			if (typeCounter[9] > 0) {
				System.out.println("   Unidentified elements: "
						+ typeCounter[9]);
			}
			System.out.println("º Test Sample loading complete.\n");
		}
	}

	public static void loadTrainingDataFromCloud() {
		String username = "acclibrary";
		String password = "acclibrary";
		String uriString = "mongodb://" + username + ":" + password
				+ "@ds059957.mongolab.com:59957/activity_recognition";

		MongoURI uri = new MongoURI(uriString);
		DB database = null;
		DBCollection acc_db = null;

		try {
			database = uri.connectDB();
		} catch (UnknownHostException uhe) {
			System.out.println("UnknownHostException: " + uhe);
		} catch (MongoException me) {
			System.out.println("MongoException: " + me);
		}

		if (database != null) {
			// acc_db = database.getCollection("accelerometer_data");
			acc_db = database.getCollection("accelerometer_data_new");
			System.out
					.println("º Connection to Mongolab database established.");
			System.out.println("   Loading training samples.");
			// System.out.println("  Total records:" + acc_db.count());

			DBCursor results = acc_db.find();
			int resultCounter = 0;
			int[] typeCounter = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			while (results.hasNext()) {

				DBObject result = results.next();
				resultCounter++;

				ObjectId objid = (ObjectId) result.get("_id");
				int id = objid.hashCode();
				int type = (int) result.get("type");

				BasicDBList xData = (BasicDBList) result.get("xData");
				BasicDBList yData = (BasicDBList) result.get("yData");
				BasicDBList zData = (BasicDBList) result.get("zData");

				AccData temp = new AccData(type, toDoubleList(xData),
						toDoubleList(yData), toDoubleList(zData));

				// if (type == 9)
				// accDataLibrary.add(temp);
				// else
				if (splitIntoTwo && temp.size() > 300) {
					accDataLibrary.add(temp.getFirstHalfOfElements());
					typeCounter[type]++;
					accDataLibrary.add(temp.getSecondHalfOfElements());
					typeCounter[type]++;
				} else {
					accDataLibrary.add(temp);
					typeCounter[type]++;
				}

			}
			System.out.println("º " + resultCounter
					+ " records loaded. Type breakdown:");
			for (int i = 0; i < 9; i++) {
				if (typeCounter[i] > 0)
					System.out.println("   type #" + i + " count: "
							+ typeCounter[i]);
			}
			if (typeCounter[9] > 0) {
				System.out.println("   Unidentified elements: "
						+ typeCounter[9]);
			}
			System.out.println("º Training Sample loading complete.\n");
		}
	}
}
