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
	static NaiveGaussian ng;
	static MultivariateGaussian mvg;
	static boolean splitIntoTwo = true;

	public static void main(String[] args) {
		loadTrainingDataFromCloud();
		loadTestDataFromCloud();
		buildAccFeatLibrary();
		buildTestAccFeatLibrary();

		ng = new NaiveGaussian(accFeatLibrary);
		mvg = new MultivariateGaussian(accFeatLibrary);

		while (true) {
			displayMenu();
			Scanner in = new Scanner(System.in);
			int selection = in.nextInt();

			if (selection == 99) {
				sendEntropyData();
			}
			if (selection == 77) {
				findMisclassifications();
			} else if (selection == 66) {
				WekaFileGenerator.generateFile(accFeatLibrary);
			} else if (selection != 66 && selection != 99) {
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
				System.out.println("Labeled as type: "
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

	public static void displayMenu() {
		System.out
				.print("\n\nº Select Action.\n (1) Identify activity with index = ");
	}

	private static List<Double> toDoubleList(BasicDBList list) {
		List<Double> result = new ArrayList<Double>();
		for (int i = 0; i < list.size(); i++) {
			result.add((Double) list.get(i));
		}
		return result;
	}

	public static void sendEntropyData() {
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
			ed = database.getCollection("entropy_data");
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
