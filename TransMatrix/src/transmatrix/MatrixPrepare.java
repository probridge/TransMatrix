package transmatrix;

import java.util.ArrayList;

public class MatrixPrepare {
	/**
	 * 准备第一类型矩阵
	 * 
	 * @param run
	 *            计算次数1-12
	 * @param original
	 *            原始Excel阵，必须为10行x18列，不含表头，包含问卷编号和序号
	 * @return 11x11的方阵
	 */
	public static NumberMatrix prepare1(int run, StringMatrix original) {
		int n = original.row + 1;
		NumberMatrix result = new NumberMatrix(n, n);
		// Fill
		for (int i = 1; i < n; i++) {
			if (run <= 3) {
				double val = Matrix.toDouble(original.data[i - 1][11 + run]) - 1;
				result.data[i][0] = val < 0 ? 0 : val;
			} else {
				double val = Matrix.toDouble(original.data[i - 1][12 + ((run - 1) / 3 - 1)]) - 1;
				if (val < 0)
					val = 0;
				result.data[i][0] = val * Matrix.toDouble(original.data[i - 1][15 + ((run - 1) % 3)]) / 5;
			}
			for (int j = 1; j < n; j++) {
				double val = Matrix.toDouble(original.data[i - 1][j + 1]) - 1;
				result.data[i][j] = val < 0 ? 0 : val;
			}
		}
		// 对称化
		for (int i = 0; i < n; i++)
			for (int j = i + 1; j < n; j++)
				result.data[i][j] = result.data[j][i];
		// 填名称
		for (int i = 0; i < original.row; i++)
			result.description[i + 1] = (String) original.data[i][0];
		result.description[0] = (String) original.data[0][0];
		//
		result = reorg(result);
		return result;
	}

	/**
	 * 准备第二类型矩阵
	 * 
	 * @param run
	 *            计算次数1-2
	 * @param original
	 *            原始Excel阵，必须为n行x9列，不含表头，包含问卷编号和序号
	 * @return n+1的方阵
	 */

	public static NumberMatrix[] prepare2(int run, StringMatrix original) {
		int rows = original.row + 1;
		NumberMatrix result = new NumberMatrix(rows, rows);
		// Fill
		for (int i = 1; i < rows; i++) {
			result.data[i][0] = Matrix.toDouble(original.data[i - 1][6 + run]);
			for (int j = 1; j < rows; j++)
				result.data[i][j] = Matrix.toDouble(original.data[i - 1][j + 1]);
		}
		// 对称化
		for (int i = 0; i < rows; i++)
			for (int j = i + 1; j < rows; j++)
				result.data[i][j] = result.data[j][i];
		// 填名称
		for (int i = 0; i < original.row; i++)
			result.description[i + 1] = (String) original.data[i][0];
		result.description[0] = (String) original.data[0][0];
		//
		result = reorg(result);
		//
		NumberMatrix result1 = result.makeCopy();
		// 01化
		result.make01();
		return new NumberMatrix[] { result, result1 };
	}

	/**
	 * 准备第三类型矩阵
	 * 
	 * @param original
	 *            原始Excel阵，必须为n行x9列，不含表头，包含问卷编号和序号
	 * @return n+1的方阵
	 */

	public static NumberMatrix[] prepare3(StringMatrix original) {
		int rows = original.row + 1;
		NumberMatrix result = new NumberMatrix(rows, rows);
		// Fill
		for (int i = 1; i < rows; i++) {
			result.data[i][0] = Matrix.toDouble(original.data[i - 1][8]);
			for (int j = 1; j < rows; j++)
				result.data[i][j] = Matrix.toDouble(original.data[i - 1][j + 1]);
		}
		// 对称化
		for (int i = 0; i < rows; i++)
			for (int j = i + 1; j < rows; j++)
				result.data[i][j] = result.data[j][i];
		// 填名称
		for (int i = 0; i < original.row; i++)
			result.description[i + 1] = (String) original.data[i][0];
		result.description[0] = (String) original.data[0][0];
		//
		result = reorg(result);
		//
		NumberMatrix result1 = result.makeCopy();
		// 01化
		result.make01();
		return new NumberMatrix[] { result, result1 };
	}

	public static NumberMatrix prepare4(StringMatrix original, boolean symmetric, boolean minus1) {
		int n = Math.max(original.row, original.col - 2);
		NumberMatrix result = new NumberMatrix(n, n);
		for (int i = 0; i < n; i++) {
			if (i < original.row)
				for (int j = 0; j < n; j++)
					result.data[i][j] = Matrix.toDouble(original.data[i][j + 2]);
			else
				for (int j = 0; j < original.row; j++)
					result.data[i][j] = Matrix.toDouble(original.data[j][i + 2]);
		}
		//
		if (minus1)
			for (int i = 0; i < n; i++)
				for (int j = 0; j < n; j++)
					if ((Double) result.data[i][j] > 0)
						result.data[i][j] = (Double) result.data[i][j] - 1;
		//
		if (symmetric)
			for (int i = 0; i < n; i++)
				for (int j = 0; j < i; j++) {
					double avg = ((Double) result.data[i][j] + (Double) result.data[j][i]) / 2;
					result.data[i][j] = avg;
					result.data[j][i] = avg;
				}
		// 填名称
		for (int i = 0; i < original.row; i++)
			result.description[i] = (String) original.data[i][0];
		//
		return result;
	}

	public static NumberMatrix prepare5(int run, StringMatrix original) {
		int n = original.row + 1;
		NumberMatrix result = new NumberMatrix(n, n);
		// Fill
		for (int i = 1; i < n; i++) {
			if (run == 1) {
				double val = Matrix.toDouble(original.data[i - 1][7]) - 1;
				result.data[i][0] = val < 0 ? 0 : val;
			}
			if (run == 2) {
				double val = Matrix.toDouble(original.data[i - 1][7]) - 1;
				result.data[i][0] = (val < 0 ? 0 : val) * Matrix.toDouble(original.data[i - 1][8]) / 5;
			}
			if (run == 3) {
				double val = Matrix.toDouble(original.data[i - 1][7]) - 1;
				result.data[i][0] = (val < 0 ? 0 : val) * Matrix.toDouble(original.data[i - 1][9]) / 5;
			}

			for (int j = 1; j < n; j++) {
				double val = Matrix.toDouble(original.data[i - 1][j + 1]) - 1;
				result.data[i][j] = val < 0 ? 0 : val;
			}
		}
		// 对称化
		for (int i = 0; i < n; i++)
			for (int j = i + 1; j < n; j++)
				result.data[i][j] = result.data[j][i];
		//
		// 填名称
		for (int i = 0; i < original.row; i++)
			result.description[i + 1] = (String) original.data[i][0];
		result.description[0] = (String) original.data[0][0];
		//
		result = reorg(result);
		//
		return result;
	}

	public static NumberMatrix prepare6(StringMatrix original) {
		int n = Math.max(original.row, original.col - 2);
		NumberMatrix result = new NumberMatrix(n, n);
		for (int i = 0; i < n; i++) {
			if (i < original.row)
				for (int j = 0; j < n; j++)
					result.data[i][j] = Matrix.toDouble(original.data[i][j + 2]);
			else
				for (int j = 0; j < original.row; j++)
					result.data[i][j] = Matrix.toDouble(original.data[j][i + 2]);
		}
		// 对称化
		for (int i = 0; i < n; i++)
			for (int j = 0; j < i; j++) {
				double max = Math.max((Double) (result.data[j][i]), (Double) (result.data[i][j]));
				result.data[j][i] = result.data[i][j] = max;
			}
		// 填名称
		for (int i = 0; i < original.row; i++)
			result.description[i] = (String) original.data[i][0];
		//
		result.make01();
		return result;
	}

	private static NumberMatrix reorg(NumberMatrix matrix) {
		int n = matrix.row;
		ArrayList<Integer> skipRows = new ArrayList<Integer>();
		for (int i = 0; i < n; i++) {
			boolean rowAllZero = true;
			for (int j = 0; j < n; j++) {
				if ((Double) matrix.data[i][j] > 0) {
					rowAllZero = false;
					break;
				}
			}
			if (rowAllZero)
				skipRows.add(i);
		}
		int nn = n - skipRows.size();
		NumberMatrix reorgMatrix = new NumberMatrix(nn, nn);
		int ii = 0, jj = 0;
		for (int i = 0; i < n; i++) {
			if (skipRows.contains(i))
				continue;
			reorgMatrix.description[ii] = matrix.description[i];
			for (int j = 0; j < n; j++) {
				if (skipRows.contains(j))
					continue;
				reorgMatrix.data[ii][jj] = matrix.data[i][j];
				jj++;
			}
			ii++;
			jj = 0;
		}
		if (nn == 0 || skipRows.contains(0)) {
			reorgMatrix = new NumberMatrix(1, 1);
			reorgMatrix.description[0] = matrix.description[0];
			reorgMatrix.data[0][0] = 0.0d;
		}
		return reorgMatrix;
	}
}
