package transmatrix;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.jblas.ComplexDouble;
import org.jblas.ComplexDoubleMatrix;
import org.jblas.DoubleMatrix;
import org.jblas.Eigen;

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
			for (int j = 0; j < matrix.col; j++) {
				if ((Double) matrix.data[i][j] > 0.0d && direction == OUT)
					n++;
				if ((Double) matrix.data[j][i] > 0.0d && direction == IN)
					n++;
			}
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

	public static double getDensity(NumberMatrix matrix) {
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

	// 1-5
	public static Double[] getNormalized4N(Double[] data, int N) {
		Double[] result = new Double[data.length];
		for (int i = 0; i < data.length; i++)
			result[i] = data[i] / (N * data.length - N);
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
			bottom += val;
		}
		return 3.0d * getTriangles(matrix) / bottom;
	}

	public static ArrayList<HashSet<Integer>> getCircleList(NumberMatrix matrix, int circleThreshold) {
		// pre-populate
		ArrayList<HashSet<Integer>> circleList = new ArrayList<HashSet<Integer>>();
		for (int i = 0; i < matrix.row; i++)
			for (int j = 0; j < matrix.col; j++) {
				if (j == i)
					continue;
				if ((Double) matrix.data[i][j] == 1.0d) {
					HashSet<Integer> thisCircle = new HashSet<Integer>();
					thisCircle.add(i);
					thisCircle.add(j);
					circleList.add(thisCircle);
				}
			}
		// 开始迭代
		do {
			reduce(circleList);
		} while (explore(circleList, matrix, circleThreshold));
		//
		// finishing up
		//
		Iterator<HashSet<Integer>> it = circleList.iterator();
		while (it.hasNext()) {
			HashSet<Integer> thisCircle = it.next();
			if (thisCircle.size() < circleThreshold + 1)
				it.remove();
		}
		return circleList;
	}

	private static boolean explore(ArrayList<HashSet<Integer>> circleList, NumberMatrix matrix, int circleThreshold) {
		boolean structuralChange = false;
		for (HashSet<Integer> eachCircle : circleList) {
			ArrayList<Integer> nodesToAdd = new ArrayList<Integer>();
			for (int eachNode = 0; eachNode < matrix.row; eachNode++) {
				if (eachCircle.contains(eachNode))
					continue;
				// any node outside the circle
				int counter = 0;
				for (Integer eachNodeInCircle : eachCircle) {
					if ((Double) matrix.data[eachNodeInCircle][eachNode] == 1.0d) {
						counter++;
					}
				}
				if (counter == eachCircle.size() || counter >= circleThreshold)
					nodesToAdd.add(eachNode);
			}
			if (nodesToAdd.size() > 0) {
				eachCircle.addAll(nodesToAdd);
				structuralChange = true;
			}
		}
		return structuralChange; // if anything changes return true
	}

	private static void reduce(ArrayList<HashSet<Integer>> circleList) {
		for (HashSet<Integer> eachCircle : circleList)
			for (HashSet<Integer> eachCircleToCheck : circleList) {
				if (eachCircleToCheck == eachCircle)
					continue;
				if (eachCircleToCheck.contains(-1))
					continue;
				if (eachCircleToCheck.equals(eachCircle))
					eachCircleToCheck.add(-1);
			}
		Iterator<HashSet<Integer>> it = circleList.iterator();
		while (it.hasNext())
			if (it.next().contains(-1))
				it.remove();
		return;
	}

	private static HashSet<Integer> getConnectionNodes(ArrayList<ArrayList<HashSet<Integer>>> nodesToCircleMap,
			NumberMatrix matrix) {
		int n = matrix.row;
		HashSet<Integer> connectionNodes = new HashSet<Integer>();
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				if ((Double) matrix.data[i][j] == 1.0d) {
					// i, j
					ArrayList<HashSet<Integer>> sets1 = nodesToCircleMap.get(i);
					ArrayList<HashSet<Integer>> sets2 = nodesToCircleMap.get(j);
					if (sets1.size() == 0 || sets2.size() == 0) // 非伙
						continue;
					// 同伙
					boolean skip = false;
					for (HashSet<Integer> eachCircle : sets2)
						if (eachCircle.contains(i))
							skip = true;
					//
					if (skip)
						continue;
					//
					connectionNodes.add(i);
					connectionNodes.add(j);
				}
		return connectionNodes;
	}

	private static ArrayList<ArrayList<HashSet<Integer>>> getReserveCircleList(ArrayList<HashSet<Integer>> circles,
			int row) {
		ArrayList<ArrayList<HashSet<Integer>>> reserveList = new ArrayList<ArrayList<HashSet<Integer>>>(row);
		for (int i = 0; i < row; i++) {
			ArrayList<HashSet<Integer>> containingCircle = new ArrayList<HashSet<Integer>>();
			// for each node
			for (HashSet<Integer> eachCircle : circles)
				if (eachCircle.contains(i))
					containingCircle.add(eachCircle);
			reserveList.add(containingCircle);
		}
		return reserveList;
	}

	private static NumberMatrix getCircleMatrix(NumberMatrix transDataMatrix, HashSet<Integer> eachCircle) {
		int n = eachCircle.size();
		NumberMatrix thisCircleMatrix = new NumberMatrix(n, n);
		int N = transDataMatrix.row;
		int x = 0, y = 0;
		for (int i = 0; i < N; i++) {
			if (eachCircle.contains(i)) {
				for (int j = 0; j < N; j++)
					if (eachCircle.contains(j))
						thisCircleMatrix.data[x++][y] = transDataMatrix.data[i][j];
				//
				thisCircleMatrix.description[y] = transDataMatrix.description[i];
				y++;
				x = 0;
			}
		}
		return thisCircleMatrix;
	}

	public static double getR(NumberMatrix matrix, int type) {
		int n = matrix.row;
		double a = 0.0d;
		double apos = 0.0d;
		int N = getN(matrix, type);
		for (int i = 0; i < n; i++)
			for (int j = i + 1; j < n; j++) {
				a += (Double) matrix.data[i][j];
				apos += (Double) matrix.data[j][i];
			}
		a = a / (N * (N - 1) * 2);
		apos = apos / (N * (N - 1) * 2);
		//
		double n1 = 0, n2 = 0, n3 = 0;
		//
		for (int i = 0; i < n; i++)
			for (int j = i + 1; j < n; j++) {
				n1 += ((Double) matrix.data[i][j] - a) * ((Double) matrix.data[j][i] - apos);
				n2 += Math.pow((Double) matrix.data[i][j] - a, 2);
				n3 += Math.pow((Double) matrix.data[j][i] - apos, 2);
			}
		return n1 / Math.sqrt(n2 * n3);
	}

	public static MatrixResults computeResults(NumberMatrix transDataMatrix, int type, boolean sym, int normalizeN,
			int circleThreshold) {
		// type 2,3 uses 01 matrix for calculation except strength
		NumberMatrix keepMatrix = transDataMatrix.makeCopy();
		if (type == 2 || type == 3)
			transDataMatrix.make01();
		//
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
		Double[] nInDC = MatrixCalculation.getNormalized4N(inDC, normalizeN);
		Double[] nOutDC = MatrixCalculation.getNormalized4N(outDC, normalizeN);
		//
		NumberMatrix bcMatrix = transDataMatrix.makeCopy();
		bcMatrix.copySymmetric();
		Double[] BC = MatrixCalculation.getBonacichCentrality(bcMatrix);
		//
		Double[] inStrength, outStrength, inOriginalStrength = null, outOriginalStrength = null;
		if (type == 2 || type == 3) {
			inStrength = MatrixCalculation.getStrength(keepMatrix, MatrixCalculation.IN, type);
			outStrength = MatrixCalculation.getStrength(keepMatrix, MatrixCalculation.OUT, type);
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
		// >0 ==> 1 (type 4 symmetric[div2] only)
		NumberMatrix matrix01 = transDataMatrix.makeCopy();
		matrix01.make01();
		// sym=max([x,y],[y,x]), >0==>1
		NumberMatrix symetric01Matrix = transDataMatrix.makeCopy();
		symetric01Matrix.copySymmetric();
		symetric01Matrix.make01();
		// Get circles
		ArrayList<HashSet<Integer>> circles = MatrixCalculation.getCircleList(symetric01Matrix, circleThreshold);
		// counter node --> containing circles
		ArrayList<ArrayList<HashSet<Integer>>> nodesToCircleMap = MatrixCalculation.getReserveCircleList(circles,
				symetric01Matrix.row);
		//
		NumberMatrix outTransMatrix = transDataMatrix.makeCopy();
		outTransMatrix.flip();
		// populate dis/clu into row result for easy handling
		double matrixDistribution = MatrixCalculation.getDistribution(symetric01Matrix);
		double matrixCluster = MatrixCalculation.getCluster(symetric01Matrix);
		double matrixDensity = MatrixCalculation.getDensity(transDataMatrix);
		double matrixR = MatrixCalculation.getR(transDataMatrix, type);
		//
		double matrixDensitySy = MatrixCalculation.getDensity(matrix01);
		//
		MatrixRowResult[] rowResults = new MatrixRowResult[n];
		for (int i = 0; i < n; i++) {
			rowResults[i] = new MatrixRowResult();
			rowResults[i].resultLable = transDataMatrix.description[i];
			rowResults[i].n = i;
			rowResults[i].N = N;
			rowResults[i].inDC = inDC[i];
			rowResults[i].outDC = outDC[i];
			rowResults[i].N_inDC = nInDC[i];
			rowResults[i].N_outDC = nOutDC[i];
			rowResults[i].between = betweenness[i];
			rowResults[i].N_between = nBetweenness[i];
			rowResults[i].N_inClose = nInCC[i];
			rowResults[i].N_outClose = nOutCC[i];
			rowResults[i].inClose2 = inCC2[i];
			rowResults[i].outClose2 = outCC2[i];
			rowResults[i].inEfficiency = MatrixCalculation.getEfficiency(transDataMatrix, i);
			rowResults[i].outEfficiency = MatrixCalculation.getEfficiency(outTransMatrix, i);
			//
			rowResults[i].inContraint = MatrixCalculation.getConstraint(transDataMatrix, i, type != 4);
			rowResults[i].outContraint = MatrixCalculation.getConstraint(outTransMatrix, i, type != 4);
			rowResults[i].egoDensity = MatrixCalculation.getEgoDensity(transDataMatrix, i);
			rowResults[i].egoDensitySy = MatrixCalculation.getEgoDensity(matrix01, i);
			rowResults[i].nonRedundancy = MatrixCalculation.getNonRedundancy(transDataMatrix, i, type);
			rowResults[i].density = matrixDensity;
			rowResults[i].densitySy = matrixDensitySy;
			rowResults[i].inStrength = inStrength[i];
			rowResults[i].outStrength = outStrength[i];
			rowResults[i].inOriginalStrength = (inOriginalStrength != null) ? inOriginalStrength[i] : Double.NaN;
			rowResults[i].outOriginalStrength = (outOriginalStrength != null) ? outOriginalStrength[i] : Double.NaN;
			rowResults[i].belongingCircle = nodesToCircleMap.get(i).size();
			rowResults[i].distribution = matrixDistribution;
			rowResults[i].cluster = matrixCluster;
			rowResults[i].R = matrixR;
			rowResults[i].BC = BC[i];
		}
		//
		MatrixResults result = new MatrixResults();
		result.rowResults = rowResults;
		// get connector point (ie. node that points to another circle)
		result.connectingNodes = MatrixCalculation.getConnectionNodes(nodesToCircleMap, symetric01Matrix);
		result.circles = circles;
		result.circleDensity = new double[circles.size()];
		result.circleThreshold = circleThreshold;
		//
		for (int i = 0; i < circles.size(); i++) {
			// extract sub matrix from original
			// calculate density for each row in the circle matrix
			NumberMatrix circleMatrix = getCircleMatrix(transDataMatrix, circles.get(i));
			result.circleDensity[i] = getDensity(circleMatrix);
		}
		//
		MatrixCalculation.computeCircleStructuralHole(result, nodesToCircleMap, transDataMatrix, type);
		return result;
	}

	private static void computeCircleStructuralHole(MatrixResults result,
			ArrayList<ArrayList<HashSet<Integer>>> nodesToCircleMap, NumberMatrix matrix, int type) {
		// deep clone
		result.circlesMatrixMemberList = new ArrayList<HashSet<Integer>>();
		for (HashSet<Integer> eachCircle : result.circles) {
			result.circlesMatrixMemberList.add(new HashSet<Integer>(eachCircle));
		}
		//
		ArrayList<Integer> brokenCircleMemberPair = new ArrayList<Integer>();
		//
		for (int i = 0; i < nodesToCircleMap.size(); i++) {
			// each node belong to multiple circle
			ArrayList<HashSet<Integer>> circlesList = nodesToCircleMap.get(i);
			if (circlesList.size() > 1) {
				// append new member
				HashSet<Integer> newCircleMember = new HashSet<Integer>();
				newCircleMember.add(i);
				int newMemberIndex = result.circlesMatrixMemberList.size();
				result.circlesMatrixMemberList.add(newCircleMember);
				//
				for (int j = 0; j < newMemberIndex; j++) {
					if (result.circlesMatrixMemberList.get(j).remove(i)) {
						// common node removed from the circle
						// remember (i,j), (j,i) - mark connectecd
						brokenCircleMemberPair.add(newMemberIndex);
						brokenCircleMemberPair.add(j);
					}
				}
			}
		}
		// remember circle number
		int nonCircleIndex = result.circlesMatrixMemberList.size();
		// process non circle nodes
		for (int i = 0; i < nodesToCircleMap.size(); i++) {
			// nodes that not belonging to any circle
			ArrayList<HashSet<Integer>> circlesList = nodesToCircleMap.get(i);
			if (circlesList.size() == 0) {
				// append new member
				HashSet<Integer> newCircleMember = new HashSet<Integer>();
				newCircleMember.add(i);
				result.circlesMatrixMemberList.add(newCircleMember);
			}
		}
		// generate new matrix
		int size = result.circlesMatrixMemberList.size();
		result.circlesMatrix = new NumberMatrix(size, size);
		for (int i = 0; i < size; i++) {
			result.circlesMatrix.description[i] = result.circlesMatrixMemberList.get(i).toString();
			if (i >= nonCircleIndex)
				result.circlesMatrix.description[i] = "-" + result.circlesMatrix.description[i] + "-";
		}
		// populate broken circles
		Iterator<Integer> it = brokenCircleMemberPair.iterator();
		while (it.hasNext()) {
			int a = it.next();
			int b = it.next();
			result.circlesMatrix.data[a][b] = 1.0d;
			result.circlesMatrix.data[b][a] = 1.0d;
		}
		// iterate connection points
		ArrayList<Integer> connectionPointList = new ArrayList<Integer>(result.connectingNodes);
		for (int i = 0; i < connectionPointList.size(); i++)
			for (int j = 0; j < connectionPointList.size(); j++) {
				if (i == j)
					continue;
				if ((Double) matrix.data[connectionPointList.get(i)][connectionPointList.get(j)] == 0.0d)
					continue;
				int iCircleNumber = findInCircleList(result.circlesMatrixMemberList, connectionPointList.get(i));
				int jCircleNumber = findInCircleList(result.circlesMatrixMemberList, connectionPointList.get(j));
				//
				if (iCircleNumber == jCircleNumber)
					continue;
				result.circlesMatrix.data[iCircleNumber][jCircleNumber] = 1.0d;
				result.circlesMatrix.data[jCircleNumber][iCircleNumber] = 1.0d;
			}
		// iterate non circle nodes
		for (int i = nonCircleIndex; i < result.circlesMatrixMemberList.size(); i++) {
			// thisNode is a single member hashset<Integer>
			HashSet<Integer> thisNode = result.circlesMatrixMemberList.get(i);
			int thisNodeNumber = (Integer) (thisNode.toArray()[0]);
			for (int j = 0; j < result.circlesMatrixMemberList.size(); j++) {
				HashSet<Integer> theNodeOrCircle = result.circlesMatrixMemberList.get(j);
				Iterator<Integer> itr = theNodeOrCircle.iterator();
				while (itr.hasNext()) {
					Integer eachMember = itr.next();
					if ((Double) matrix.data[thisNodeNumber][eachMember] > 0.0d) {
						result.circlesMatrix.data[i][j] = 1.0d;
						result.circlesMatrix.data[j][i] = 1.0d;
					}
				}
			}
		}
		// matrix gained - do efficiency & contraint
		result.circleMatrixEfficienty = new double[size];
		result.circleMatrixContraint = new double[size];
		for (int i = 0; i < size; i++) {
			result.circleMatrixEfficienty[i] = MatrixCalculation.getEfficiency(result.circlesMatrix, i);
			result.circleMatrixContraint[i] = MatrixCalculation.getConstraint(result.circlesMatrix, i, type != 4);
		}
	}

	private static int findInCircleList(ArrayList<HashSet<Integer>> circlesMatrixMemberList, int node) {
		for (int index = 0; index < circlesMatrixMemberList.size(); index++)
			if (circlesMatrixMemberList.get(index).contains(node))
				return index;
		// not found - generate exception
		return -1;
	}

	public static double getMaxEigenValue(NumberMatrix matrix) {
		double eps = 1e-7;
		int fuse = 10000;
		int itr = 0;
		boolean flag = true;
		int n = matrix.col;
		double[] v = new double[n];
		v[0] = 1.0d;
		double[] u = new double[n];
		double d, t = 0.0d, f = 0.0d, z = 0.0d;
		//
		do {
			itr++;
			for (int i = 0; i < n; i++) {
				double sum = 0.0d;
				for (int j = 0; j < n; j++)
					sum += ((Double) matrix.data[i][j]) * v[j];
				u[i] = sum;
			}
			d = 0.0d;
			for (int k = 0; k < n; k++)
				d += u[k] * u[k];
			d = Math.sqrt(d);
			for (int i = 0; i < n; i++)
				v[i] = u[i] / d;
			if (itr > 1) {
				double err = Math.abs((d - t) / d);
				f = 1;
				if (v[0] * z < 0)
					f = -1;
				if (err < eps)
					flag = false;
			}
			if (flag) {
				t = d;
				z = v[0];
			}
			if (itr > fuse) {
				flag = false;
				throw new RuntimeException("Trigger FUSE");
			}
		} while (flag);
		return f * d;
	}

	public static Double[] solveGauss(Double[][] A, Double[] b) {
		double EPSILON = 1e-10;
		int N = b.length;

		for (int p = 0; p < N; p++) {
			// find pivot row and swap
			int max = p;
			for (int i = p + 1; i < N; i++) {
				if (Math.abs(A[i][p]) > Math.abs(A[max][p])) {
					max = i;
				}
			}
			Double[] temp = A[p];
			A[p] = A[max];
			A[max] = temp;
			Double t = b[p];
			b[p] = b[max];
			b[max] = t;

			// singular or nearly singular
			if (Math.abs(A[p][p]) <= EPSILON) {
				throw new RuntimeException("Matrix is singular or nearly singular");
			}

			// pivot within A and b
			for (int i = p + 1; i < N; i++) {
				double alpha = A[i][p] / A[p][p];
				b[i] -= alpha * b[p];
				for (int j = p; j < N; j++) {
					A[i][j] -= alpha * A[p][j];
				}
			}
		}

		// back substitution
		Double[] x = new Double[N];
		for (int i = N - 1; i >= 0; i--) {
			double sum = 0.0;
			for (int j = i + 1; j < N; j++) {
				sum += A[i][j] * x[j];
			}
			x[i] = (b[i] - sum) / A[i][i];
		}
		return x;
	}

	public static Double[] getBonacichCentrality(NumberMatrix matrix) {
		Double[] principalEigenvector = getPrincipalEigenvector(convert(matrix));
		return principalEigenvector;
	}

	private static DoubleMatrix convert(NumberMatrix matrix) {
		DoubleMatrix dMatrix = new DoubleMatrix(matrix.row, matrix.col);
		for (int i = 0; i < matrix.row; i++)
			for (int j = 0; j < matrix.col; j++)
				dMatrix.put(i, j, ((Double) matrix.data[i][j]).doubleValue());
		return dMatrix;
	}

	private static Double[] getPrincipalEigenvector(DoubleMatrix matrix) {
		int maxIndex = getMaxIndex(matrix);
		ComplexDoubleMatrix eigenVectors = Eigen.eigenvectors(matrix)[0];
		return getEigenVector(eigenVectors, maxIndex);
	}

	private static Double[] getEigenVector(ComplexDoubleMatrix eigenVectors, int maxIndex) {
		Double[] ret = new Double[eigenVectors.rows];
		for (int k = 0; k < eigenVectors.rows; k++)
			ret[k] = eigenVectors.get(k, maxIndex).abs();
		return ret;
	}

	private static int getMaxIndex(DoubleMatrix matrix) {
		ComplexDouble[] doubleMatrix = Eigen.eigenvalues(matrix).toArray();
		int maxIndex = 0;
		for (int i = 0; i < doubleMatrix.length; i++) {
			double newnumber = doubleMatrix[i].abs();
			if ((newnumber > doubleMatrix[maxIndex].abs())) {
				maxIndex = i;
			}
		}
		return maxIndex;
	}

	public static void main(String[] args) throws Exception {
		NumberMatrix matrix = new NumberMatrix(4, 4);
		matrix.data[0][0] = 0.0d;
		matrix.data[0][1] = 1.0d;
		matrix.data[0][2] = 1.0d;
		matrix.data[0][3] = 1.0d;
		//
		matrix.data[1][0] = 1.0d;
		matrix.data[1][1] = 0.0d;
		matrix.data[1][2] = 1.0d;
		matrix.data[1][3] = 1.0d;
		//
		matrix.data[2][0] = 1.0d;
		matrix.data[2][1] = 1.0d;
		matrix.data[2][2] = 0.0d;
		matrix.data[2][3] = 0.0d;
		//
		matrix.data[3][0] = 1.0d;
		matrix.data[3][1] = 1.0d;
		matrix.data[3][2] = 0.0d;
		matrix.data[3][3] = 0.0d;
		//
		System.out.println("principalEigenvector = " + getBonacichCentrality(matrix));
		//
		System.out.println(1.0d / getMaxEigenValue(matrix));
		//
	}
}
