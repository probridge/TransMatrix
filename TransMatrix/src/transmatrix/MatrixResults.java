package transmatrix;

import java.util.ArrayList;
import java.util.HashSet;

public class MatrixResults {
	public MatrixRowResult[] rowResults;
	public HashSet<Integer> connectingNodes;
	public ArrayList<HashSet<Integer>> circles;
	public double[] circleDensity;
	//
	public ArrayList<HashSet<Integer>> circlesMatrixMemberList; //»ï¾ØÕó³ÉÔ±
	public NumberMatrix circlesMatrix; //»ï¾ØÕó
	public double[] circleMatrixEfficienty, circleMatrixContraint; //»ï¾ØÕó½á¹¹¶´
}
