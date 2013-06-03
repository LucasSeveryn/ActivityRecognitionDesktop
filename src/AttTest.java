import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Generates a little ARFF file with different attribute types.
 *
 * @author FracPete
 */
public class AttTest {
  public static void main(String[] args) throws Exception {
    FastVector      atts;
    FastVector      attVals;
    Instances       data;
    double[]        vals;
    int             i;

    // 1. set up attributes
    atts = new FastVector();
    // - numeric
    
    
    atts.addElement(new Attribute("att1"));
    // - nominal
    attVals = new FastVector();
    for (i = 0; i < 9; i++)
      attVals.addElement(Integer.toString(i));
    atts.addElement(new Attribute("att2", attVals));
    
    // 2. create Instances object
    data = new Instances("MyRelation", atts, 0);

    // 3. fill with data
    // first instance
    vals = new double[data.numAttributes()];
    // - numeric
    vals[0] = Math.PI;
    // - nominal
    vals[1] = attVals.indexOf("2");
    
    // add
    data.add(new Instance(1.0, vals));

    // second instance
    vals = new double[data.numAttributes()];  // important: needs NEW array!
    // - numeric
    vals[0] = Math.E;
    // - nominal
    vals[1] = attVals.indexOf("1");
    data.add(new Instance(1.0, vals));

    // 4. output data
    System.out.println(data);
  }
}
