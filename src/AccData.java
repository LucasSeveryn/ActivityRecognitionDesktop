
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class AccData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4027578149606001414L;
	private ArrayList<Double> xData;
	private ArrayList<Double> yData;
	private ArrayList<Double> zData;
	private ArrayList<Double> xgData;
	private ArrayList<Double> ygData;
	private ArrayList<Double> zgData;
	private int id; 
	private int type;


	public AccData(int id, int type, ArrayList<Double> xData, ArrayList<Double> yData,
			ArrayList<Double> zData, ArrayList<Double> xgData, ArrayList<Double> ygData, ArrayList<Double> zgData) {
		this.xData = xData;
		this.yData = yData;
		this.zData = zData;
		this.xgData = xgData;
		this.ygData = ygData;
		this.zgData = zgData;
		this.type = type;
	}
	
	public AccData(int id, int type, ArrayList<Double> xData, ArrayList<Double> yData,
			ArrayList<Double> zData) {
		this.xData = xData;
		this.yData = yData;
		this.zData = zData;

		this.type = type;
	}
	

	public ArrayList<Double> getxData() {
		return xData;
	}

	public ArrayList<Double> getyData() {
		return yData;
	}

	public ArrayList<Double> getzData() {
		return zData;
	}
	
	public ArrayList<Double> getxgData() {
		return xgData;
	}

	public ArrayList<Double> getygData() {
		return ygData;
	}

	public ArrayList<Double> getzgData() {
		return zgData;
	}

	public int getId() {
		return id;
	}




	public int getType() {
		return type;
	}
}