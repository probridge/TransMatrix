package transmatrix;

import java.util.ArrayList;
import java.util.HashSet;

public class MatrixResults {
	public MatrixRowResult[] rowResults;
	public HashSet<Integer> connectingNodes;
	public ArrayList<HashSet<Integer>> circles;
	public double[] circleDensity;
	//
	public ArrayList<HashSet<Integer>> circlesMatrixMemberList; //伙矩阵成员
	public NumberMatrix circlesMatrix; //伙矩阵
	public double[] circleMatrixEfficienty, circleMatrixContraint; //伙矩阵结构洞
	public int circleThreshold; //伙标准
}
