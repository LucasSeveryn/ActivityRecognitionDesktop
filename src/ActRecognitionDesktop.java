import com.mongodb.*;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.plaf.ListUI;

public class ActRecognitionDesktop {
	static ArrayList<AccData> accDataLibrary = new ArrayList<AccData>();
	static ArrayList<AccFeat> accFeatLibrary = new ArrayList<AccFeat>();
	static ArrayList<AccFeat> accUnidentifiedFeatLibrary = new ArrayList<AccFeat>();

	public static void main(String[] args) {
		loadAccelerometerData();
		calculateFeatures();
		while(true){displayMenu();
		Scanner in = new Scanner(System.in);
		int selection = in.nextInt();
			NBC n = new NBC(accFeatLibrary);
			n.classify(accUnidentifiedFeatLibrary.get(selection));
		}

	}

	public static void printPeaks() {

		FeatureExtractors.peakdet(accDataLibrary.get(50).getyData());
	}

	public static void calculateFeatures() {
		System.out.println("� Building Feature Library");
		for (AccData a : accDataLibrary) {
			AccFeat temp = new AccFeat();
			temp.setId(a.getId());
			temp.setType(a.getType());
			ArrayList<Double> xData = a.getxData();
			ArrayList<Double> yData = a.getyData();
			ArrayList<Double> zData = a.getzData();
//			ArrayList<Double> xgData = a.getxgData();
//			ArrayList<Double> ygData = a.getygData();
//			ArrayList<Double> zgData = a.getzgData();
			
			ArrayList<Double> lpfxData = FeatureExtractors.lowPassFilter(xData);
			ArrayList<Double> lpfyData = FeatureExtractors.lowPassFilter(yData);
			ArrayList<Double> lpfzData = FeatureExtractors.lowPassFilter(zData);

			temp.setMean(0, FeatureExtractors.calculateMean(xData));
			temp.setMean(1, FeatureExtractors.calculateMean(yData));
			temp.setMean(2, FeatureExtractors.calculateMean(zData));
			
			temp.setSd(0, FeatureExtractors.standardDeviation(xData));
			temp.setSd(1, FeatureExtractors.standardDeviation(yData));
			temp.setSd(2, FeatureExtractors.standardDeviation(zData));
			
//			temp.setgMean(0, FeatureExtractors.calculateMean(xgData));
//			temp.setgMean(1, FeatureExtractors.calculateMean(ygData));
//			temp.setgMean(2, FeatureExtractors.calculateMean(zgData));
//			
//			temp.setgSd(0, FeatureExtractors.standardDeviation(xgData));
//			temp.setgSd(1, FeatureExtractors.standardDeviation(ygData));
//			temp.setgSd(2, FeatureExtractors.standardDeviation(zgData));
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
			
//			FeatureExtractors.fftest(lpfxData);
//			FeatureExtractors.fftest(lpfyData);
//			FeatureExtractors.fftest(lpfzData);
			
			temp.setFftHistogram(0,FeatureExtractors.calcHistogram(FeatureExtractors.fftest(xData), 0, 100, 10));
			temp.setFftHistogram(1,FeatureExtractors.calcHistogram(FeatureExtractors.fftest(yData), 0, 100, 10));
			temp.setFftHistogram(2,FeatureExtractors.calcHistogram(FeatureExtractors.fftest(zData), 0, 100, 10));
			
			temp.setHistogram(0,FeatureExtractors.calcHistogram(xData, -15, 15, 10));
			temp.setHistogram(1,FeatureExtractors.calcHistogram(yData, -15, 15, 10));
			temp.setHistogram(2,FeatureExtractors.calcHistogram(zData, -15, 15, 10));
			
//			if(temp.getType()==0)System.out.println("Type #" + temp.getType() + " bin 0: " + temp.getFftHistogram(0, 1));
			
			
//			temp.setgResultantAcc(FeatureExtractors
//					.averageResultantAcceleration(a.getxgData(), a.getygData(),
//							a.getzgData()));
			
			temp.setCrossingCount(0, FeatureExtractors.zeroCrossingCount(FeatureExtractors.highPassFilter(lpfxData)));
			temp.setCrossingCount(1, FeatureExtractors.zeroCrossingCount(FeatureExtractors.highPassFilter(lpfyData)));
			temp.setCrossingCount(2, FeatureExtractors.zeroCrossingCount(FeatureExtractors.highPassFilter(lpfzData)));
			if(a.getType()==9) accUnidentifiedFeatLibrary.add(temp); else accFeatLibrary.add(temp);

		}
		System.out.println("� Feature library size: " + accFeatLibrary.size());
	}

	public static void displayMenu() {
		System.out.print("\n\n� Select Action.\n (1) Identify activity with index = ");
	}

	public static ArrayList<Double> toDoubleArrayList(BasicDBList list) {
		ArrayList<Double> result = new ArrayList<Double>();
		for (int i = 0; i < list.size(); i++) {
			result.add((Double) list.get(i));
		}
		return result;
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
					.println("� Connection to Mongolab database established.");
			// System.out.println("  Total records:" + acc_db.count());

			DBCursor results = acc_db.find();
			int resultCounter = 0;
			int[] typeCounter = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
			while (results.hasNext()) {

				DBObject result = results.next();
				resultCounter++;

				int type = (int) result.get("type");
				typeCounter[type]++;

				BasicDBObject gyroData = (BasicDBObject) result.get("gyro");
				
				BasicDBList xgData = (BasicDBList) gyroData.get("xData");
				BasicDBList ygData = (BasicDBList) gyroData.get("yData");
				BasicDBList zgData = (BasicDBList) gyroData.get("zData");
				
				
				BasicDBList xData = (BasicDBList) result.get("xData");
				BasicDBList yData = (BasicDBList) result.get("yData");
				BasicDBList zData = (BasicDBList) result.get("zData");

				AccData temp = new AccData(resultCounter, type,
						toDoubleArrayList(xData), toDoubleArrayList(yData),
						toDoubleArrayList(zData), toDoubleArrayList(xgData), toDoubleArrayList(ygData),toDoubleArrayList(zgData));

				if(type==9) accDataLibrary.add(temp);
				else accDataLibrary.add(temp);
			}
			System.out.println("� " + resultCounter
					+ " records loaded. Type breakdown:");
			for (int i = 0; i < 9; i++) {
				if (typeCounter[i] > 0)
					System.out.println("   type #" + i + " count: "
							+ typeCounter[i]);
			}
			if(typeCounter[9]>0){
				System.out.println("   Unidentified elements: " + typeCounter[9]);
			}
			System.out.println("� Loading complete.\n");
		}
	}
}