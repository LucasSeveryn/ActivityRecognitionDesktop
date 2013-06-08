import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;


public final class WekaFileGenerator {
	public final static WekaFileGenerator INSTANCE = new WekaFileGenerator();
	
	public static String intToAttributeName(int i){
		switch (i) {
	    case 0:
	      return "mean[0]";
	    case 1:
	      return "mean[1]";
	    case 2:
	      return "mean[2]";
	    case 3:
	      return "sd[0]";
	    case 4:
	      return "sd[1]";
	    case 5:
	      return "sd[2]";
	    case 6:
	      return "avPeakDistance[0]";
	    case 7:
	      return "avPeakDistance[1]";
	    case 8:
	      return "avPeakDistance[2]";
	    case 9:
	      return "crossingCount[0]";
	    case 10:
	      return "crossingCount[1]";
	    case 11:
	      return "crossingCount[2]";
	    case 12:
	      return "resultantAcc";
	    case 13:
	      return "maxDisplacement[0]";
	    case 14:
	      return "maxDisplacement[1]";
	    case 15:
	      return "maxDisplacement[2]";
	    case 16:
	      return "0";
	    case 17:
	      return "energy[0]";
	    case 18:
	      return "energy[1]";
	    case 19:
	      return "energy[2]";
	    case 20:
	      return "correlation[0]";
	    case 21:
	      return "correlation[1]";
	    case 22:
	      return "correlation[2]";
	    case 23:
	      return "SMA";

	    }
	    if (i > 23 && i < 54) {
	      return "histogram["+Integer.toString((int) Math.floor((i - 24) / 10)) +"][" + Integer.toString((i - 24) % 10)+"]";
	    }
	    if (i > 53 && i < 84) {
	      return "fftHistogram["+Integer.toString((int) Math.floor((i - 54) / 10))+"]["+Integer.toString((i - 54) % 10)+"]";
	    }
	    if (i > 83) {
	      int axis=(int) Math.floor((i - 84) / 4);
	      return "AR["+axis+"]["+Integer.toString((i - 84) % 4)+"]";
	    }
		
		return "ERROR,WTF";
		
	}
	
	public static void generateFile(ArrayList<AccFeat> library){
		    FastVector      atts;
		    FastVector      attVals;
		    Instances       data;
		    double[]        vals;
		    int             i;

		    // 1. set up attributes
		    atts = new FastVector();
		    // - nominal
		    attVals = new FastVector();
		    for (i = 0; i < 9; i++)
		      attVals.addElement((Integer.toString(i)));
		    atts.addElement(new Attribute("type", attVals));
		    // - numeric
		    for(int j=0;j<96;j++)
		    	
		    atts.addElement(new Attribute(intToAttributeName(j)));
		    
	    	 // 2. create Instances object
		    data = new Instances("Object", atts, 0);
		    
		    for(AccFeat a : library){


			    // 3. fill with data
			    // first instance
			    vals = new double[data.numAttributes()];
			    // - nominal
			    vals[0] = attVals.indexOf(Integer.toString(a.getType()));
			    // - numeric
			    double[] featVector = a.getFeatureVector();
			    for(int j=0;j<featVector.length;j++){
			    	vals[j+1]=featVector[j];
			    }
			    // add
			    data.add(new Instance(1.0, vals));
		    }

		    PrintStream out;
			try {
				PrintStream old = System.out;
				out = new PrintStream(new FileOutputStream("wekaData.arff"));
				System.setOut(out);
			    System.out.println(data);
			    System.out.flush();
			    System.setOut(old);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
	}

}
