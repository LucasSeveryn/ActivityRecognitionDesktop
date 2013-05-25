

import com.mongodb.*;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.bson.types.ObjectId;


public class ActRecognitionDesktop {
	static ArrayList<AccData> accDataLibrary = new ArrayList<AccData>();
	static ArrayList<AccFeat> accFeatLibrary = new ArrayList<AccFeat>();
	static ArrayList<AccFeat> accUnidentifiedFeatLibrary = new ArrayList<AccFeat>();
	static NaiveGaussian ng;

	public static void main(String[] args) {
		loadAccelerometerData();
		calculateFeatures();
		while (true) {
			displayMenu();
			Scanner in = new Scanner(System.in);
			int selection = in.nextInt();
			ng = new NaiveGaussian(accFeatLibrary);
			if(selection==99){
				sendEntropyData();	
			}
			else ng.classify2(accUnidentifiedFeatLibrary.get(selection));
			
		}

	}

	public static void printPeaks() {

		FeatureExtractors.peakdet(accDataLibrary.get(50).getyData());
	}

	public static void calculateFeatures() {
		System.out.println("º Building Feature Library");
		for (AccData a : accDataLibrary) {
			AccFeat temp = new AccFeat();
			temp.setId(a.getId());
			temp.setType(a.getType());
			ArrayList<Double> xData = a.getxData();
			ArrayList<Double> yData = a.getyData();
			ArrayList<Double> zData = a.getzData();

			ArrayList<Double> lpfxData = FeatureExtractors.lowPassFilter(xData);
			ArrayList<Double> lpfyData = FeatureExtractors.lowPassFilter(yData);
			ArrayList<Double> lpfzData = FeatureExtractors.lowPassFilter(zData);

			temp.setMean(0, FeatureExtractors.calculateMean(xData));
			temp.setMean(1, FeatureExtractors.calculateMean(yData));
			temp.setMean(2, FeatureExtractors.calculateMean(zData));

			temp.setSd(0, FeatureExtractors.standardDeviation(xData));
			temp.setSd(1, FeatureExtractors.standardDeviation(yData));
			temp.setSd(2, FeatureExtractors.standardDeviation(zData));

			//

			temp.setAvPeakDistance(0,
					FeatureExtractors.averageDistanceBetweenPeaks(lpfxData));
			temp.setAvPeakDistance(1,
					FeatureExtractors.averageDistanceBetweenPeaks(lpfyData));
			temp.setAvPeakDistance(2,
					FeatureExtractors.averageDistanceBetweenPeaks(lpfzData));

			temp.setResultantAcc(FeatureExtractors
					.averageResultantAcceleration(a.getxData(), a.getyData(),
							a.getzData()));

			temp.setFftHistogram(
					0,
					FeatureExtractors.calcHistogram(
							FeatureExtractors.fftest(xData), 0, 100, 10));
			temp.setFftHistogram(
					1,
					FeatureExtractors.calcHistogram(
							FeatureExtractors.fftest(yData), 0, 100, 10));
			temp.setFftHistogram(
					2,
					FeatureExtractors.calcHistogram(
							FeatureExtractors.fftest(zData), 0, 100, 10));

			temp.setHistogram(0,
					FeatureExtractors.calcHistogram(xData, -15, 15, 10));
			temp.setHistogram(1,
					FeatureExtractors.calcHistogram(yData, -15, 15, 10));
			temp.setHistogram(2,
					FeatureExtractors.calcHistogram(zData, -15, 15, 10));

			temp.setCrossingCount(0, FeatureExtractors
					.zeroCrossingCount(FeatureExtractors
							.highPassFilter(lpfxData)));
			temp.setCrossingCount(1, FeatureExtractors
					.zeroCrossingCount(FeatureExtractors
							.highPassFilter(lpfyData)));
			temp.setCrossingCount(2, FeatureExtractors
					.zeroCrossingCount(FeatureExtractors
							.highPassFilter(lpfzData)));
			if (a.getType() == 9)
				accUnidentifiedFeatLibrary.add(temp);
			else
				accFeatLibrary.add(temp);

		}
		System.out.println("º Feature library size: " + accFeatLibrary.size());
	}

	public static void displayMenu() {
		System.out
				.print("\n\nº Select Action.\n (1) Identify activity with index = ");
	}

	public static ArrayList<Double> toDoubleArrayList(BasicDBList list) {
		ArrayList<Double> result = new ArrayList<Double>();
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
//		    addElements(mean1, entropyMean.get(1));
		    addElements(mean2, entropyMean.get(2));
		    addElements(mean3, entropyMean.get(3));
//		    addElements(mean4, entropyMean.get(4));
//		    addElements(mean5, entropyMean.get(5));
//		    addElements(mean6, entropyMean.get(6));
		    addElements(mean7, entropyMean.get(7));
		    addElements(mean8, entropyMean.get(8));
		    
		    BasicDBList var0 = new BasicDBList();
		    BasicDBList var1 = new BasicDBList();
		    BasicDBList var2 = new BasicDBList();
		    BasicDBList var3 = new BasicDBList();
		    BasicDBList var4 = new BasicDBList();
		    BasicDBList var5 = new BasicDBList();
		    BasicDBList var6 = new BasicDBList();
		    BasicDBList var7 = new BasicDBList();
		    BasicDBList var8 = new BasicDBList();
		    
		    addElements(var0, entropyVar.get(0));
//		    addElements(var1, entropyVar.get(1));
		    addElements(var2, entropyVar.get(2));
		    addElements(var3, entropyVar.get(3));
//		    addElements(var4, entropyVar.get(4));
//		    addElements(var5, entropyVar.get(5));
//		    addElements(var6, entropyVar.get(6));
		    addElements(var7, entropyVar.get(7));
		    addElements(var8, entropyVar.get(8));
		    
		    BasicDBObject mean0obj = new BasicDBObject("mean0", mean0);
//		    BasicDBObject mean1obj = new BasicDBObject("mean1", mean1);
		    BasicDBObject mean2obj = new BasicDBObject("mean2", mean2);
		    BasicDBObject mean3obj = new BasicDBObject("mean3", mean3);
//		    BasicDBObject mean4obj = new BasicDBObject("mean4", mean4);
//		    BasicDBObject mean5obj = new BasicDBObject("mean5", mean5);
//		    BasicDBObject mean6obj = new BasicDBObject("mean6", mean6);
		    BasicDBObject mean7obj = new BasicDBObject("mean7", mean7);
		    BasicDBObject mean8obj = new BasicDBObject("mean8", mean8);
    
		    BasicDBObject var0obj = new BasicDBObject("var0", var0);
//		    BasicDBObject var1obj = new BasicDBObject("var1", var1);
		    BasicDBObject var2obj = new BasicDBObject("var2", var2);
		    BasicDBObject var3obj = new BasicDBObject("var3", var3);
//		    BasicDBObject var4obj = new BasicDBObject("var4", var4);
//		    BasicDBObject var5obj = new BasicDBObject("var5", var5);
//		    BasicDBObject var6obj = new BasicDBObject("var6", var6);
		    BasicDBObject var7obj = new BasicDBObject("var7", var7);
		    BasicDBObject var8obj = new BasicDBObject("var8", var8);

		    ed.insert(mean0obj);
//		    ed.insert(mean1obj);
		    ed.insert(mean2obj);
		    ed.insert(mean3obj);
//		    ed.insert(mean4obj);
//		    ed.insert(mean5obj);
//		    ed.insert(mean6obj);
		    ed.insert(mean7obj);
		    ed.insert(mean8obj);
		    
		    ed.insert(var0obj);
//		    ed.insert(var1obj);
		    ed.insert(var2obj);
		    ed.insert(var3obj);
//		    ed.insert(var4obj);
//		    ed.insert(var5obj);
//		    ed.insert(var6obj);
		    ed.insert(var7obj);
		    ed.insert(var8obj);
		    
		    
		    }
	}

	static public void addElements(BasicDBList bdblist, ArrayList<Double> list){
    	for(int i=0;i<list.size();i++){
    		bdblist.add(new BasicDBObject(Integer.toString(i), list.get(i)));
    	}    		
    }
	
	
	

	public static void loadAccelerometerData() {
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
			acc_db = database.getCollection("accelerometer_data");
			System.out
					.println("º Connection to Mongolab database established.");
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
				typeCounter[type]++;

				
				BasicDBList xData = (BasicDBList) result.get("xData");
				BasicDBList yData = (BasicDBList) result.get("yData");
				BasicDBList zData = (BasicDBList) result.get("zData");

				AccData temp = new AccData(id, type,
						toDoubleArrayList(xData), toDoubleArrayList(yData),
						toDoubleArrayList(zData));

				if (type == 9)
					accDataLibrary.add(temp);
				else
					accDataLibrary.add(temp);
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
			System.out.println("º Loading complete.\n");
		}
	}
}