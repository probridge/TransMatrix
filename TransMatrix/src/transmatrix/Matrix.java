package transmatrix;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class Matrix {
	public Object[][] data;

	public int col = 0;
	public int row = 0;

	public void print() {
		System.out.println("Matrix has " + row + " rows and " + col + " columns.");
		for (int r = 0; r < row; r++) {
			for (int c = 0; c < col; c++)
				System.out.print(data[r][c] + ",");
			System.out.println();
		}
	}

	public static StringMatrix load(String fileName) throws BiffException, IOException {
		Workbook workbook = Workbook.getWorkbook(new File(fileName));
		// only support 1st sheet
		Sheet sheet = workbook.getSheet(0);
		// get info
		int row = sheet.getRows();
		int col = sheet.getColumns();
		// allocate
		StringMatrix matrix = new StringMatrix(row, col);
		// fill
		for (int r = 0; r < row; r++)
			for (int c = 0; c < col; c++)
				matrix.data[r][c] = sheet.getCell(c, r).getContents().trim();
		// close
		workbook.close();
		return matrix;
	}

	public static StringMatrix getStringMatrix(ArrayList<String> buf, int lrecl) {
		StringMatrix m = new StringMatrix(buf.size() / lrecl, lrecl);
		Iterator<String> it = buf.iterator();
		for (int i = 0; i < buf.size() / lrecl; i++)
			for (int j = 0; j < lrecl; j++)
				m.data[i][j] = it.next();
		return m;
	}

	public static StringMatrix[] loadAll(File workFile, int type) {
		StringMatrix[] s = new StringMatrix[0];
		try {
			Workbook workbook = Workbook.getWorkbook(workFile);
			// only support 1st sheet
			Sheet sheet = workbook.getSheet(0);
			// get info
			int row = sheet.getRows();
			int col = sheet.getColumns();
			ArrayList<StringMatrix> matrixs = new ArrayList<StringMatrix>();
			String lastVal = "";
			int currentLRECL = 0;
			ArrayList<String> buf = new ArrayList<String>();
			for (int r = 0; r < row; r++) {
				String val = sheet.getCell(0, r).getContents().trim();
				// skip empty line if detected unless second col is not empty
				if (val.isEmpty())
					if (!sheet.getCell(1, r).getContents().trim().isEmpty())
						val = lastVal;
					else
						continue;
				// 中文表头处理
				if (val != null && val.length() > 0 && !isAlphaNumberic(val.charAt(0))) {
					if (currentLRECL > 0 && buf.size() > 0)
						matrixs.add(getStringMatrix(buf, currentLRECL));
					//
					buf.clear();
					//
					int c = 0;
					for (c = 0; c < col; c++)
						if (sheet.getCell(c, r).getContents().trim().isEmpty())
							break;
					currentLRECL = c;
					//
					continue;
				}
				// 英文数字： 1,2,3,5用相同的问卷编号识别
				if (type == 1 || type == 2 || type == 3 || type == 5) {
					if (!val.equals(lastVal)) { // New matrix
						if (currentLRECL > 0 && buf.size() > 0)
							matrixs.add(getStringMatrix(buf, currentLRECL));
						//
						buf.clear();
						lastVal = val;
					}
				}
				for (int c = 0; c < currentLRECL; c++)
					buf.add(sheet.getCell(c, r).getContents().trim());
			}
			if (currentLRECL > 0 && buf.size() > 0)
				matrixs.add(getStringMatrix(buf, currentLRECL));
			// close
			workbook.close();
			//
			s = new StringMatrix[matrixs.size()];
			matrixs.toArray(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	private static boolean isAlphaNumberic(char c) {
		if (c >= 'A' && c <= 'Z')
			return true;
		if (c >= 'a' && c <= 'z')
			return true;
		if (c >= '0' && c <= '9')
			return true;
		return false;
	}

	public static void export(Matrix matrix, String fileName) throws IOException, RowsExceededException, WriteException {
		WritableWorkbook workbook = Workbook.createWorkbook(new File(fileName));
		WritableSheet sheet = workbook.createSheet("转换结果", 0);
		//

		// fill
		for (int r = 0; r < matrix.row; r++)
			for (int c = 0; c < matrix.col; c++) {
				if (matrix.data[r][c] instanceof String)
					sheet.addCell(new Label(c, r, (String) matrix.data[r][c]));
				else if (matrix.data[r][c] instanceof Double)
					sheet.addCell(new Number(c, r, (Double) matrix.data[r][c]));
			}
		// close
		workbook.write();
		workbook.close();
	}

	public static Double toDouble(Object obj) {
		try {
			if (obj instanceof String) {
				String str = String.valueOf(obj);
				if ("".equals(str) || str == null)
					return 0.0d;
				else
					return Double.valueOf(str);
			} else
				return 0.0d;
		} catch (NumberFormatException e) {
			System.err.println("Error converting " + obj.toString() + " to double, 0.0d assumed.");
			return 0.0d;
		}
	}

	public static String formatDouble(Double number) {
		if (number == null)
			return "NULL";
		if (number.isNaN())
			return "NaN";
		NumberFormat nbf = NumberFormat.getInstance();
		nbf.setMinimumFractionDigits(3);
		nbf.setMaximumFractionDigits(3);
		return nbf.format(number);
	}

	public static void main(String[] args) {
		System.out.println(Double.NaN + 3.0d);
	}
}
