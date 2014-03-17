package transmatrix;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;

public class MatrixCalculation {
	private static double NOT_CONNECTED = 99999.0d;
	public static boolean IN = true;
	public static boolean OUT = false;

	public static int getN(NumberMatrix matrix, int type) {
		int count = 0;
		if (matrix.row > 0) {
			for (int i = 0; i < matrix.col; i++)
				if (((type == 1 || type == 2 || type == 3 || type == 5) && (Double) matrix.data[0][i] > 0) || type == 4)
					count++;
		}
		return (type == 4) ? count : (count + 1);
	}

	public static Double[] getStrength(NumberMatrix matrix, boolean direction, int type) {
		Double[] s = new Double[matrix.row];
		Double[] dc = getDC(matrix, direction);
		int n = getN(matrix, type);
		//
		for (int i = 0; i < matrix.row; i++)
			s[i] = dc[i] / (n - 1);
		return s;
	}

	public static Double[] getStrengthOriginal(NumberMatrix matrix, boolean direction, int type) {
		Double[] s = new Double[matrix.row];
		Double[] dc = getDC(matrix, direction);
		//
		for (int i = 0; i < matrix.row; i++) {
			int n = 0;
			for (int j = 0; j < matrix.col; j++)
				if ((Double) matrix.data[i][j] > 0.0d)
					n++;
			s[i] = dc[i] / n;
		}
		return s;
	}

	public static Double[] getDC(NumberMatrix matrix, boolean direction) {
		Double[] dc = new Double[matrix.row];
		for (int i = 0; i < dc.length; i++)
			dc[i] = 0.0d;
		//
		for (int k = 0; k < matrix.col; k++)
			for (int i = 0; i < matrix.col; i++)
				if (direction == OUT)
					dc[k] += (Double) matrix.data[k][i];
				else
					dc[k] += (Double) matrix.data[i][k];
		return dc;
	}

	public static double getDensity(NumberMatrix matrix, int k) {
		double d = 0.0d;
		for (int i = 0; i < matrix.row; i++) {
			for (int j = 0; j < matrix.col; j++) {
				d += (Double) matrix.data[i][j];
			}
		}
		return d / (matrix.row * (matrix.row - 1));
	}

	public static double getEgoDensity(NumberMatrix matrix, int k) {
		double d = 0.0d;
		int skipped = 0;
		for (int i = 0; i < matrix.row; i++) {
			if (((Double) matrix.data[i][k]).equals(0.0d) && ((Double) matrix.data[k][i]).equals(0.0d) && i != k) {
				skipped++;
				continue;
			}
			for (int j = 0; j < matrix.col; j++) {
				if (i == k || j == k)
					continue;
				if (((Double) matrix.data[k][j]).equals(0.0d))
					continue;
				d += (Double) matrix.data[i][j];
			}
		}
		return d / ((matrix.row - 1 - skipped) * (matrix.row - 2 - skipped));
	}

	public static double getNonRedundancy(NumberMatrix matrix, int k, int type) {
		double d = 0.0d;
		int skipped = 0;
		for (int i = 0; i < matrix.row; i++) {
			if (((Double) matrix.data[i][k]).equals(0.0d) && ((Double) matrix.data[k][i]).equals(0.0d) && i != k) {
				skipped++;
				continue;
			}
			for (int j = 0; j < matrix.col; j++) {
				if (i == k || j == k)
					continue;
				if (((Double) matrix.data[k][j]).equals(0.0d))
					continue;
				d += (Double) matrix.data[i][j];
			}
		}
		double e = (matrix.row - 1 - skipped) * (matrix.row - 2 - skipped);
		return (e - d) / (getN(matrix, type) - 1);
	}

	public static double getConstraint(NumberMatrix matrix, int k, boolean excludeZeros) {
		double con = 0.0d;
		//
		if (matrix.col <= 1)
			con = Double.NaN;
		//
		double tk = 0.0d;
		for (int i = 0; i < matrix.col; i++)
			tk += (Double) matrix.data[k][i];
		//
		for (int j = 0; j < matrix.col; j++) {
			if (j == k)
				continue;
			// if ((Double) matrix.data[k][j] == 0.0d && excludeZeros)
			if ((Double) matrix.data[k][j] == 0.0d)
				continue;
			double t1 = 0.0d;
			for (int q = 0; q < matrix.row; q++) {
				if (q == k || q == j)
					continue;
				//
				// if (((Double) matrix.data[q][k] == 0.0d || (Double)
				// matrix.data[k][q] == 0.0d)
				// && excludeZeros)
				if (((Double) matrix.data[q][k] == 0.0d || (Double) matrix.data[k][q] == 0.0d))
					continue;

				double tq = 0.0d;
				for (int z = 0; z < matrix.col; z++) {
					if ((Double) matrix.data[k][z] == 0.0d && k != z && excludeZeros)
						continue;
					tq += (Double) matrix.data[q][z];
				}
				//
				double inDirect = ((((Double) matrix.data[k][q]) / tk) * (((Double) matrix.data[q][j]) / tq));
				if (Double.isNaN(inDirect))
					inDirect = 0.0d;
				t1 += inDirect;
			}
			double direct = ((Double) matrix.data[k][j]) / tk;
			if (Double.isNaN(direct))
				direct = 0.0d;
			//
			t1 += direct;
			con += Math.pow(t1, 2);
		}
		return con;
	}

	public static double getEfficiency(NumberMatrix matrix, int k) {
		//
		double t1 = 0.0d;
		for (int j = 0; j < matrix.col; j++) {
			t1 += (Double) matrix.data[k][j];
		}
		//
		double eff = 0.0d;
		for (int j = 0; j < matrix.col; j++) {
			if (j == k)
				continue;
			if (((Double) matrix.data[j][k]).equals(0.0d))
				continue;

			double max = getRowMax((Double[]) matrix.data[j]);
			//
			double t2 = 0.0d;
			for (int q = 0; q < matrix.col; q++) {
				if (((Double) matrix.data[k][q]).equals(0.0d))
					continue;
				t2 += (((Double) matrix.data[k][q] / t1) * ((Double) matrix.data[j][q] / max));
			}
			eff += (1 - t2);
		}
		//
		double t2 = 0.0d;
		for (int j = 0; j < matrix.col; j++) {
			if ((j != k) && ((Double) matrix.data[k][j] != 0))
				t2++;
		}
		//
		eff = eff / t2;
		if (Double.isInfinite(eff))
			eff = 0.0d;
		return eff;
	}

	private static double getRowMax(Double[] row) {
		double maxVal = 0.0d;
		for (int i = 0; i < row.length; i++)
			maxVal = Math.max(maxVal, row[i]);
		return maxVal;
	}

	public static double getCloseness(NumberMatrix disMatrix, int k, boolean direction) {
		Double result = 0.0d;
		for (int i = 0; i < disMatrix.col; i++) {
			double val = ((direction == OUT) ? (Double) disMatrix.data[k][i] : (Double) disMatrix.data[i][k]);
			result += val;
		}
		return result;
	}

	public static double getCloseness2(NumberMatrix disMatrix, int k, boolean direction) {
		Double result = 0.0d;
		for (int i = 0; i < disMatrix.col; i++) {
			double val = ((direction == OUT) ? (Double) disMatrix.data[k][i] : (Double) disMatrix.data[i][k]);
			if (val != 0)
				result += 1 / val;
		}
		return result;
	}

	public static double getBetweenness(NumberMatrix original, int k, boolean div2, boolean exludingZeros) {
		NumberMatrix original2 = original.makeCopy();
		//
		if (exludingZeros) {
			// prep matrix removing rows J and col J if a[k][j]==0 && j<>k;
			for (int j = 0; j < original2.col; j++) {
				if (j == k)
					continue;
				if ((Double) original2.data[k][j] == 0.0d) {
					for (int z = 0; z < original2.col; z++) {
						original2.data[j][z] = 0.0d;
						original2.data[z][j] = 0.0d;
					}
				}
			}
		}
		// init
		Double[] betweenness = new Double[original2.row];
		getDistanceMatrix(original2, betweenness);
		//
		// Type 4 symmetric special processing
		if (div2)
			return betweenness[k] / 2;
		else
			return betweenness[k];
	}

	public static NumberMatrix getDistanceMatrix(NumberMatrix original, Double[] betweenness) {
		int n = original.row;
		// 转0,1
		NumberMatrix original2 = original.makeCopy();
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				if ((Double) original2.data[i][j] > 0)
					original2.data[i][j] = 1.0d;
				else
					original2.data[i][j] = NOT_CONNECTED;
		//
		NumberMatrix result = new NumberMatrix(n, n);
		//
		@SuppressWarnings("unchecked")
		ArrayList<String>[] path = new ArrayList[n];
		int[][] numOfSRs = new int[n][n]; // x到y的最短路径条数
		int[][][] numOfHit = new int[n][n][n]; // 从i出发的到j点的所有路径经过k点的次数

		for (int i = 0; i < n; i++) {
			// 初始化i到其他节点的路径
			for (int j = 0; j < n; j++) {
				path[j] = new ArrayList<String>();
				if (((Double) original2.data[i][j]).equals(1.0d)) {
					path[j].add(i + "," + j + ",");
				}
			}
			// 计算最短路径阵
			Double[] dis = Dijsktra((Double[][]) original2.makeCopy().data, i, path, numOfSRs);
			System.arraycopy(dis, 0, result.data[i], 0, n);
			// 统计从起始点出发的到J点最短路径上每个节点的经过次数
			for (int j = 0; j < n; j++) {
				for (String eachString : path[j]) {
					StringTokenizer st = new StringTokenizer(eachString, ",");
					while (st.hasMoreTokens()) {
						int val = Integer.parseInt(st.nextToken());
						if (val != i && val != j)
							numOfHit[i][j][val]++;
					}
				}
			}
		}
		// 计算betweenness
		if (betweenness != null)
			for (int k = 0; k < n; k++) {
				betweenness[k] = 0.0d;
				for (int i = 0; i < n; i++)
					for (int j = 0; j < n; j++)
						if (numOfSRs[i][j] != 0)
							betweenness[k] += (numOfHit[i][j][k] * 1.0d / numOfSRs[i][j]);
			}
		return result;
	}

	private static Double[] Dijsktra(Double[][] matrix, int start, ArrayList<String>[] path, int[][] numOfSRs) {
		int n = matrix.length;
		Double[] shortPath = new Double[n];
		int[] visited = new int[n];
		Double[] distance = new Double[n];
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				if (matrix[i][j] < NOT_CONNECTED)
					numOfSRs[i][j] = 1;
		//
		shortPath[start] = 0.0d;
		visited[start] = 1;
		//
		for (int count = 1; count <= n - 1; count++) {
			int k = -1;
			Double dmin = Double.MAX_VALUE;
			for (int i = 0; i < n; i++)
				if (visited[i] == 0 && matrix[start][i] < dmin) {
					dmin = matrix[start][i];
					k = i;
				}
			shortPath[k] = dmin;
			visited[k] = 1;
			//
			for (int i = 0; i < n; i++) {
				if (visited[i] == 0 && matrix[start][k] + matrix[k][i] == matrix[start][i]) {
					matrix[start][i] = matrix[start][k] + matrix[k][i];
					numOfSRs[start][i] += numOfSRs[start][k] * numOfSRs[k][i];
					for (String eachPreviousPath : path[k])
						path[i].add(eachPreviousPath + i + ",");
				} else if (visited[i] == 0 && matrix[start][k] + matrix[k][i] < matrix[start][i]) {
					matrix[start][i] = matrix[start][k] + matrix[k][i];
					path[i].clear();
					for (String eachPreviousPath : path[k])
						path[i].add(eachPreviousPath + i + ",");
					numOfSRs[start][i] = numOfSRs[start][k] * numOfSRs[k][i];
				}
			}
		}
		//
		for (int i = 0; i < n; i++)
			if (shortPath[i] >= NOT_CONNECTED)
				distance[i] = 0.0d;
			else
				distance[i] = shortPath[i];
		return distance;
	}

	public static Double[] getNormalized(Double[] data) {
		Double[] result = new Double[data.length];
		Double maxVal = 0.0d;
		for (int i = 0; i < data.length; i++)
			maxVal = Math.max(maxVal, data[i]);
		for (int i = 0; i < data.length; i++)
			result[i] = data[i] / maxVal;
		return result;
	}

	public static Double[] getNormalizedMin(Double[] data) {
		Double[] result = new Double[data.length];
		Double minVal = Double.MAX_VALUE;
		for (int i = 0; i < data.length; i++)
			minVal = Math.min(minVal, data[i]);

		for (int i = 0; i < data.length; i++)
			if (data[i] != 0)
				result[i] = minVal / data[i];
			else
				result[i] = 0.0d;
		return result;
	}

	public static Double[] getNormalizedMin2(Double[] data) {
		Double[] result = new Double[data.length];

		for (int i = 0; i < data.length; i++)
			if (data[i] != 0)
				result[i] = (data.length - 1) / data[i];
			else
				result[i] = 0.0d;
		return result;
	}

	public static Double[] getNormalized4N(Double[] data) {
		Double[] result = new Double[data.length];
		for (int i = 0; i < data.length; i++)
			result[i] = data[i] / (4 * data.length - 4);
		return result;
	}

	public static Double[] getNormalizedN1N2(Double[] data, boolean div2) {
		Double[] result = new Double[data.length];
		for (int i = 0; i < data.length; i++) {
			result[i] = data[i] / ((data.length - 1) * (data.length - 2));
			if (div2) {
				result[i] *= 2;
			}
		}
		return result;
	}

	public static int getTriangles(NumberMatrix matrix) {
		int n = matrix.row;
		int result = 0;
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				if ((Double) matrix.data[i][j] == 1.0d)
					for (int k = 0; k < n; k++)
						if ((Double) matrix.data[j][k] == 1.0d)
							if ((Double) matrix.data[k][i] == 1.0d)
								result++;
		return result / 6;
	}

	public static int getTriangleNodes(NumberMatrix matrix) {
		int n = matrix.row;
		HashSet<Integer> result = new HashSet<Integer>(n);
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				if ((Double) matrix.data[i][j] == 1.0d)
					for (int k = 0; k < n; k++)
						if ((Double) matrix.data[j][k] == 1.0d)
							if ((Double) matrix.data[k][i] == 1.0d) {
								result.add(i);
								result.add(j);
								result.add(k);
							}
		return result.size();
	}

	public static int getDegree(NumberMatrix matrix, int k) {
		int result = 0;
		int n = matrix.row;
		for (int i = 0; i < n; i++)
			if ((Double) matrix.data[k][i] > 0.0d)
				result++;
		return result;
	}

	public static double getDistribution(NumberMatrix matrix) {
		return getTriangleNodes(matrix) * 1.0d / matrix.row;
	}

	public static double getCluster(NumberMatrix matrix) {
		int bottom = 0;
		int n = matrix.row;
		for (int i = 0; i < n; i++) {
			int degree = getDegree(matrix, i);
			int val = degree * (degree - 1) / 2;
			// BigInteger val = getFactorial(degree).divide(getFactorial(degree
			// - 2).multiply(new BigInteger("2")));
			bottom += val;
		}
		return 3.0d * getTriangles(matrix) / bottom;
	}

	public static BigInteger getFactorial(int val) {
		BigInteger fact = BigInteger.ONE;
		for (int i = 2; i <= val; i++) {
			fact = fact.multiply(new BigInteger(String.valueOf(i)));
		}
		// System.out.println("fact(" + val + ")=" + fact.toString());
		return fact;
	}

	public static MatrixCalculationResult[] computeResults(NumberMatrix transDataMatrix, NumberMatrix transDataMatrix1,
			int type, boolean sym) {
		int n = transDataMatrix.row;
		int N = MatrixCalculation.getN(transDataMatrix, type);
		//
		Double[] betweenness = new Double[n];
		for (int i = 0; i < betweenness.length; i++)
			betweenness[i] = getBetweenness(transDataMatrix, i, (type == 4 && sym), (type != 4));
		Double[] nBetweenness = MatrixCalculation.getNormalizedN1N2(betweenness, (type == 4 && sym));
		//
		Double[] inDC = MatrixCalculation.getDC(transDataMatrix, MatrixCalculation.IN);
		Double[] outDC = MatrixCalculation.getDC(transDataMatrix, MatrixCalculation.OUT);
		Double[] nInDC = MatrixCalculation.getNormalized4N(inDC);
		Double[] nOutDC = MatrixCalculation.getNormalized4N(outDC);
		//
		Double[] inStrength, outStrength, inOriginalStrength = null, outOriginalStrength = null;
		if (type == 2 || type == 3) {
			inStrength = MatrixCalculation.getStrength(transDataMatrix1, MatrixCalculation.IN, type);
			outStrength = MatrixCalculation.getStrength(transDataMatrix1, MatrixCalculation.OUT, type);
		} else {
			inStrength = MatrixCalculation.getStrength(transDataMatrix, MatrixCalculation.IN, type);
			outStrength = MatrixCalculation.getStrength(transDataMatrix, MatrixCalculation.OUT, type);
		}
		//
		if (type == 4) {
			inOriginalStrength = MatrixCalculation.getStrengthOriginal(transDataMatrix, MatrixCalculation.IN, type);
			outOriginalStrength = MatrixCalculation.getStrengthOriginal(transDataMatrix, MatrixCalculation.OUT, type);
		}
		//
		NumberMatrix disMatrix = MatrixCalculation.getDistanceMatrix(transDataMatrix, null);
		//
		Double[] inCC = new Double[n];
		Double[] outCC = new Double[n];
		for (int i = 0; i < n; i++) {
			inCC[i] = MatrixCalculation.getCloseness(disMatrix, i, MatrixCalculation.IN);
			outCC[i] = MatrixCalculation.getCloseness(disMatrix, i, MatrixCalculation.OUT);
		}
		Double[] nInCC = MatrixCalculation.getNormalizedMin2(inCC);
		Double[] nOutCC = MatrixCalculation.getNormalizedMin2(outCC);
		//
		Double[] inCC2 = new Double[n];
		Double[] outCC2 = new Double[n];
		for (int i = 0; i < n; i++) {
			inCC2[i] = MatrixCalculation.getCloseness2(disMatrix, i, MatrixCalculation.IN);
			outCC2[i] = MatrixCalculation.getCloseness2(disMatrix, i, MatrixCalculation.OUT);
		}
		//
		MatrixCalculationResult[] result = new MatrixCalculationResult[n];
		//
		for (int i = 0; i < n; i++) {
			result[i] = new MatrixCalculationResult();
			result[i].resultLable = transDataMatrix.description[i];
			result[i].n = i;
			result[i].N = N;
			result[i].inDC = inDC[i];
			result[i].outDC = outDC[i];
			result[i].N_inDC = nInDC[i];
			result[i].N_outDC = nOutDC[i];
			result[i].between = betweenness[i];
			result[i].N_between = nBetweenness[i];
			result[i].N_inClose = nInCC[i];
			result[i].N_outClose = nOutCC[i];
			result[i].inClose2 = inCC2[i];
			result[i].outClose2 = outCC2[i];
			result[i].inEfficiency = MatrixCalculation.getEfficiency(transDataMatrix, i);
			NumberMatrix mm = transDataMatrix.makeCopy();
			mm.flip();
			result[i].outEfficiency = MatrixCalculation.getEfficiency(mm, i);
			//
			result[i].inContraint = MatrixCalculation.getConstraint(transDataMatrix, i, type != 4);
			result[i].outContraint = MatrixCalculation.getConstraint(mm, i, type != 4);
			result[i].egoDensity = MatrixCalculation.getEgoDensity(transDataMatrix, i);
			result[i].nonRedundancy = MatrixCalculation.getNonRedundancy(transDataMatrix, i, type);
			result[i].density = MatrixCalculation.getDensity(transDataMatrix, i);
			result[i].inStrength = inStrength[i];
			result[i].outStrength = outStrength[i];
			result[i].inOriginalStrength = (inOriginalStrength != null) ? inOriginalStrength[i] : Double.NaN;
			result[i].outOriginalStrength = (outOriginalStrength != null) ? outOriginalStrength[i] : Double.NaN;
			result[i].distribution = MatrixCalculation.getDistribution(transDataMatrix);
			result[i].cluster = MatrixCalculation.getCluster(transDataMatrix);
		}
		return result;
	}
}
