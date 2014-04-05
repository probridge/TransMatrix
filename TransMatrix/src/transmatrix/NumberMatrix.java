package transmatrix;

public class NumberMatrix extends Matrix {
	String[] description = null;

	public NumberMatrix(int row, int col) {
		data = new Double[row][col];
		this.row = row;
		this.col = col;
		description = new String[row];
		for (int i = 0; i < row; i++) {
			description[i] = "";
			for (int j = 0; j < col; j++)
				data[i][j] = 0.0d;
		}
	}

	public NumberMatrix makeCopy() {
		NumberMatrix copy = new NumberMatrix(row, col);
		for (int i = 0; i < row; i++) {
			copy.description[i] = description[i];
			for (int j = 0; j < col; j++)
				copy.data[i][j] = data[i][j];
		}
		//
		return copy;
	}

	public void make01() {
		for (int i = 0; i < row; i++)
			for (int j = 0; j < col; j++)
				if ((Double) data[i][j] > 1)
					data[i][j] = 1.0d;
	}

	public void flip() {
		for (int i = 0; i < row; i++)
			for (int j = 0; j < i; j++)
				swap(i, j);
	}

	private void swap(int i, int j) {
		double val1 = (Double) data[i][j];
		double val2 = (Double) data[j][i];
		data[j][i] = val1;
		data[i][j] = val2;
	}

	public void copySymmetric() {
		// ¶Ô³Æ»¯
		for (int i = 0; i < row; i++)
			for (int j = 0; j < i; j++) {
				double max = Math.max((Double) data[j][i], (Double) data[i][j]);
				data[j][i] = data[i][j] = max;
			}
	}
}
