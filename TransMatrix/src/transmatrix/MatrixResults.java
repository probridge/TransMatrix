package transmatrix;

import java.util.ArrayList;
import java.util.HashSet;

public class MatrixResults {
	public MatrixRowResult[] rowResults;
	public HashSet<Integer> connectingNodes;
	public ArrayList<HashSet<Integer>> circles;
	public double[] circleDensity;
	//
	public ArrayList<HashSet<Integer>> circlesMatrixMemberList; //������Ա
	public NumberMatrix circlesMatrix; //�����
	public double[] circleMatrixEfficienty, circleMatrixContraint; //�����ṹ��
	public int circleThreshold; //���׼
}
