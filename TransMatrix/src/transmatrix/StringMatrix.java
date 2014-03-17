package transmatrix;

public class StringMatrix extends Matrix {
	public StringMatrix(int row, int col) {
		data = new String[row][col];
		this.row = row;
		this.col = col;
	}
}
