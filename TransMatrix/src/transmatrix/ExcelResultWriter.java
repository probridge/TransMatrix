package transmatrix;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import jxl.CellView;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExcelResultWriter {
	String fileName, outputPath;

	boolean sepLines = false;
	int type = 0;
	private int currentRow = 0, currentCol = 0;
	private String outputFileName;

	private WritableSheet worksheet;
	private WritableWorkbook workbook;
	private WritableFont fmtFont;
	private WritableCellFormat fmtCell;
	private NumberFormat fmtNumber;
	private WritableCellFormat fmtHeaderCell;
	private WritableCellFormat fmtCellMatrix;
	//
	private ArrayList<MatrixRowResult> resultToWrite = new ArrayList<MatrixRowResult>();
	private ArrayList<NumberMatrix> matrixToDump = new ArrayList<NumberMatrix>();
	//
	private ArrayList<String> circleNames = new ArrayList<String>();
	private ArrayList<ArrayList<HashSet<Integer>>> eachCircles = new ArrayList<ArrayList<HashSet<Integer>>>();
	private ArrayList<HashSet<Integer>> connectionNodes = new ArrayList<HashSet<Integer>>();
	private ArrayList<double[]> eachCircleDensity = new ArrayList<double[]>();

	private int NDC;

	public ExcelResultWriter(String fileName, String outputPath, boolean sepLines, int type, int NDC)
			throws IOException, WriteException {
		this.fileName = fileName;
		this.outputFileName = fileName.substring(0, fileName.lastIndexOf('.')) + "_Result.xls";
		this.outputPath = outputPath;
		this.sepLines = sepLines;
		this.type = type;
		this.NDC = NDC;
	}

	public void appendCircle(String name, ArrayList<HashSet<Integer>> circles, double[] circleDensity,
			HashSet<Integer> connectionNodes) {
		this.circleNames.add(name);
		this.eachCircles.add(circles);
		this.connectionNodes.add(connectionNodes);
		this.eachCircleDensity.add(circleDensity);
	}

	public void appendResult(MatrixRowResult thisResult) {
		resultToWrite.add(thisResult);
	}

	public void appendMatrix(NumberMatrix thisMatrix) {
		matrixToDump.add(thisMatrix);
	}

	public void close() throws IOException, WriteException {
		workbook = Workbook.createWorkbook(new File(outputPath + outputFileName));
		//
		fmtFont = new WritableFont(WritableFont.COURIER, 12);
		fmtNumber = new NumberFormat("###0.000");
		fmtHeaderCell = new WritableCellFormat(fmtFont, fmtNumber);
		fmtHeaderCell.setAlignment(Alignment.CENTRE);
		fmtCell = new WritableCellFormat(fmtFont, fmtNumber);
		fmtCell.setAlignment(Alignment.RIGHT);
		fmtCellMatrix = new WritableCellFormat(fmtFont);
		fmtCellMatrix.setAlignment(Alignment.CENTRE);
		// Create Sheet
		worksheet = workbook.createSheet("结果", 0);
		currentRow = 0;
		worksheet.getSettings().setHorizontalFreeze(1);
		worksheet.getSettings().setVerticalFreeze(1);
		//
		for (int i = 0; i < 200; i++) {
			CellView cellView = new CellView();
			cellView.setSize(20 * 256);
			if (i == 1)
				cellView.setSize(10 * 256);
			worksheet.setColumnView(i, cellView);
		}
		// Write headers
		if (type == 1 || type == 2 || type == 3 || type == 5) {
			writeCell("编号", fmtHeaderCell);
			writeCell("N", fmtHeaderCell);
			if (sepLines) {
				writeCell("Strength", fmtHeaderCell);
				writeCell("Degree", fmtHeaderCell);
				writeCell("EgoBetween", fmtHeaderCell);
				writeCell("EgoClose", fmtHeaderCell);
				writeCell("Efficiency", fmtHeaderCell);
				writeCell("Constraint", fmtHeaderCell);
				writeCell("EgoDensity", fmtHeaderCell);
				writeCell("NonRedundancy", fmtHeaderCell);
				writeCell("R", fmtHeaderCell);
				writeCell("Distribution", fmtHeaderCell);
				writeCell("Cluster", fmtHeaderCell);
				writeCell("NumCircles", fmtHeaderCell);
			} else {
				int index = 1;
				for (int i = 0; i < resultToWrite.size(); i++) {
					if (resultToWrite.get(i) == null)
						break;
					writeCell("Strength[" + index + "]", fmtHeaderCell);
					writeCell("Degree[" + index + "]", fmtHeaderCell);
					writeCell("EgoBetween[" + index + "]", fmtHeaderCell);
					writeCell("EgoClose[" + index + "]", fmtHeaderCell);
					writeCell("Efficiency[" + index + "]", fmtHeaderCell);
					writeCell("Constraint[" + index + "]", fmtHeaderCell);
					writeCell("EgoDensity[" + index + "]", fmtHeaderCell);
					writeCell("NonRedundancy[" + index + "]", fmtHeaderCell);
					writeCell("R[" + index + "]", fmtHeaderCell);
					writeCell("Distribution[" + index + "]", fmtHeaderCell);
					writeCell("Cluster[" + index + "]", fmtHeaderCell);
					writeCell("NumCircles[" + index + "]", fmtHeaderCell);
					index++;
				}
			}
		} else if (type == 4) {
			writeCell("编号", fmtHeaderCell);
			writeCell("N", fmtHeaderCell);
			writeCell("O-Strength", fmtHeaderCell);
			writeCell("I-Strength", fmtHeaderCell);
			writeCell("O-Orig-Strength", fmtHeaderCell);
			writeCell("I-Orig-Strength", fmtHeaderCell);
			writeCell("O-Degree", fmtHeaderCell);
			writeCell("I-Degree", fmtHeaderCell);
			writeCell("N-O-Degree(" + NDC + ")", fmtHeaderCell);
			writeCell("N-I-Degree(" + NDC + ")", fmtHeaderCell);
			writeCell("Between", fmtHeaderCell);
			writeCell("N-Between", fmtHeaderCell);
			writeCell("N-O-Close", fmtHeaderCell);
			writeCell("N-I-Close", fmtHeaderCell);
			writeCell("O-Close2", fmtHeaderCell);
			writeCell("I-Close2", fmtHeaderCell);
			writeCell("O-Efficiency", fmtHeaderCell);
			writeCell("I-Efficiency", fmtHeaderCell);
			writeCell("O-Constraint", fmtHeaderCell);
			writeCell("I-Constraint", fmtHeaderCell);
			writeCell("Density", fmtHeaderCell);
			writeCell("EgoDensity", fmtHeaderCell);
			writeCell("Density-Sy", fmtHeaderCell);
			writeCell("EgoDensity-Sy", fmtHeaderCell);
			writeCell("NonRedundancy", fmtHeaderCell);
			writeCell("R", fmtHeaderCell);
			writeCell("Distribution", fmtHeaderCell);
			writeCell("Cluster", fmtHeaderCell);
			writeCell("NumCircles", fmtHeaderCell);
		}
		currentRow++;
		currentCol = 0;
		// Write Cells
		if (type == 1 || type == 2 || type == 3 || type == 5) {
			boolean headerWritten = false;
			for (MatrixRowResult thisResult : resultToWrite) {
				if (thisResult == null) {
					if (!sepLines) {
						currentRow++;
						currentCol = 0;
						headerWritten = false;
					}
					continue;
				}
				if (sepLines || !headerWritten) {
					writeCell(thisResult.resultLable, fmtCell);
					writeCell(thisResult.N, fmtCell);
					headerWritten = true;
				}
				//
				writeCell(thisResult.inStrength, fmtCell);
				writeCell(thisResult.inDC, fmtCell);
				writeCell(thisResult.between, fmtCell);
				writeCell(thisResult.inClose2, fmtCell);
				writeCell(thisResult.inEfficiency, fmtCell);
				writeCell(thisResult.inContraint, fmtCell);
				writeCell(thisResult.egoDensity, fmtCell);
				writeCell(thisResult.nonRedundancy, fmtCell);
				writeCell(thisResult.R, fmtCell);
				writeCell(thisResult.distribution, fmtCell);
				writeCell(thisResult.cluster, fmtCell);
				writeCell(thisResult.belongingCircle, fmtCell);
				if (sepLines) {
					currentRow++;
					currentCol = 0;
				}
			}
		} else if (type == 4) {
			for (MatrixRowResult thisResult : resultToWrite) {
				writeCell(thisResult.resultLable, fmtCell);
				writeCell(thisResult.N, fmtCell);
				writeCell(thisResult.outStrength, fmtCell);
				writeCell(thisResult.inStrength, fmtCell);
				writeCell(thisResult.outOriginalStrength, fmtCell);
				writeCell(thisResult.inOriginalStrength, fmtCell);
				writeCell(thisResult.outDC, fmtCell);
				writeCell(thisResult.inDC, fmtCell);
				writeCell(thisResult.N_outDC, fmtCell);
				writeCell(thisResult.N_inDC, fmtCell);
				writeCell(thisResult.between, fmtCell);
				writeCell(thisResult.N_between, fmtCell);
				writeCell(thisResult.N_outClose, fmtCell);
				writeCell(thisResult.N_inClose, fmtCell);
				writeCell(thisResult.outClose2, fmtCell);
				writeCell(thisResult.inClose2, fmtCell);
				writeCell(thisResult.outEfficiency, fmtCell);
				writeCell(thisResult.inEfficiency, fmtCell);
				writeCell(thisResult.outContraint, fmtCell);
				writeCell(thisResult.inContraint, fmtCell);
				writeCell(thisResult.density, fmtCell);
				writeCell(thisResult.egoDensity, fmtCell);
				writeCell(thisResult.densitySy, fmtCell);
				writeCell(thisResult.egoDensitySy, fmtCell);
				writeCell(thisResult.nonRedundancy, fmtCell);
				writeCell(thisResult.R, fmtCell);
				writeCell(thisResult.distribution, fmtCell);
				writeCell(thisResult.cluster, fmtCell);
				writeCell(thisResult.belongingCircle, fmtCell);
				currentRow++;
				currentCol = 0;
			}
		}
		// Print Circles
		if (circleNames.size() > 0) {
			worksheet = workbook.createSheet("伙", Integer.MAX_VALUE - 1);
			currentRow = 0;
			currentCol = 0;
			//
			for (int i = 0; i < 200; i++) {
				CellView cellView = new CellView();
				cellView.setSize(30 * 256);
				// cellView.setAutosize(true);
				if (i == 0)
					cellView.setSize(15 * 256);
				worksheet.setColumnView(i, cellView);
			}
			//
			boolean circleNotice = false;
			for (int i = 0; i < circleNames.size(); i++) {
				// write header
				writeCell("矩阵", fmtHeaderCell);
				writeCell(circleNames.get(i), fmtHeaderCell);
				if (!circleNotice) {
					writeCell("伙成员(N)为连接点", fmtHeaderCell);
					circleNotice = true;
				}
				currentRow++;
				currentCol = 0;
				//
				writeCell("伙成员", fmtHeaderCell);
				//
				ArrayList<HashSet<Integer>> thisCircleLists = eachCircles.get(i);
				HashSet<Integer> thisConnectionNodes = connectionNodes.get(i);
				double[] thisCircleDensity = eachCircleDensity.get(i);
				//
				for (HashSet<Integer> eachCircle : thisCircleLists) {
					StringBuffer sb = new StringBuffer();
					sb.append("{");
					for (Integer eachNodeInCircle : eachCircle) {
						if (thisConnectionNodes.contains(eachNodeInCircle))
							sb.append("(");
						sb.append(eachNodeInCircle);
						if (thisConnectionNodes.contains(eachNodeInCircle))
							sb.append(")");
						sb.append(",");
					}
					if (sb.length() > 1)
						sb.deleteCharAt(sb.length() - 1);
					sb.append("}");
					writeCell(sb.toString(), fmtHeaderCell);
				}
				currentRow++;
				currentCol = 0;
				//
				writeCell("Density", fmtHeaderCell);
				//
				for (double eachCircleDensity : thisCircleDensity) {
					writeCell(eachCircleDensity, fmtHeaderCell);
				}
				currentRow += 2;
				currentCol = 0;
			}
		}
		// Dump Matrix
		if (type == 4) {
			WritableSheet sheet = workbook.createSheet("中间矩阵", Integer.MAX_VALUE);
			//
			CellView cellView = new CellView();
			cellView.setSize(20 * 256);
			sheet.setColumnView(0, cellView);
			// fill
			int cr = 0;
			for (NumberMatrix eachMatrix : matrixToDump) {
				int r = 0;
				for (r = 0; r < eachMatrix.row; r++) {
					sheet.addCell(new Label(0, r + cr, (String) eachMatrix.description[r], fmtHeaderCell));
					for (int c = 0; c < eachMatrix.col; c++) {
						if (eachMatrix.data[r][c] instanceof String)
							sheet.addCell(new Label(c + 1, r + cr, (String) eachMatrix.data[r][c], fmtCellMatrix));
						else if (eachMatrix.data[r][c] instanceof Double)
							sheet.addCell(new Number(c + 1, r + cr, (Double) eachMatrix.data[r][c], fmtCellMatrix));
					}
				}
				cr += (r + 1);
			}
		}
		// Save
		workbook.write();
		workbook.close();
	}

	public void writeCell(Object val, WritableCellFormat fmt) throws RowsExceededException, WriteException {
		if (val instanceof String)
			worksheet.addCell(new Label(currentCol, currentRow, (String) val, fmt));
		else if (val instanceof Integer)
			worksheet.addCell(new Number(currentCol, currentRow, (Integer) val, fmt));
		else if (val instanceof Double) {
			if (((Double) val).isInfinite())
				worksheet.addCell(new Label(currentCol, currentRow, "Inf.", fmt));
			else if (((Double) val).isNaN())
				worksheet.addCell(new Label(currentCol, currentRow, "NaN.", fmt));
			else
				worksheet.addCell(new Number(currentCol, currentRow, (Double) val, fmt));
		}
		currentCol++;
	}
}
