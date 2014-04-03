package transmatrix;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
	private WritableWorkbook workbook;
	private WritableSheet worksheet;
	private int currentRow = 0, currentCol = 0;
	private WritableFont fmtFont;
	private WritableCellFormat fmtCell;
	private NumberFormat fmtNumber;
	private WritableCellFormat fmtHeaderCell;
	private String outputFileName;
	private ArrayList<MatrixCalculationResult> resultToWrite = new ArrayList<MatrixCalculationResult>();
	private ArrayList<NumberMatrix> matrixToDump = new ArrayList<NumberMatrix>();
	private WritableCellFormat fmtCellMatrix;

	public ExcelResultWriter(String fileName, String outputPath, boolean sepLines, int type) throws IOException,
			WriteException {
		this.fileName = fileName;
		this.outputFileName = fileName.substring(0, fileName.lastIndexOf('.')) + "_Result.xls";
		this.outputPath = outputPath;
		this.sepLines = sepLines;
		this.type = type;
	}

	public void appendResult(MatrixCalculationResult thisResult) {
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
		worksheet = workbook.createSheet("½á¹û", 0);
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
			writeCell("±àºÅ", fmtHeaderCell);
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
				writeCell("Distribution", fmtHeaderCell);
				writeCell("Cluster", fmtHeaderCell);
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
					writeCell("Distribution[" + index + "]", fmtHeaderCell);
					writeCell("Cluster[" + index + "]", fmtHeaderCell);

					index++;
				}
			}
		} else if (type == 4) {
			writeCell("±àºÅ", fmtHeaderCell);
			writeCell("N", fmtHeaderCell);
			writeCell("O-Strength", fmtHeaderCell);
			writeCell("I-Strength", fmtHeaderCell);
			writeCell("O-Orig-Strength", fmtHeaderCell);
			writeCell("I-Orig-Strength", fmtHeaderCell);
			writeCell("O-Degree", fmtHeaderCell);
			writeCell("I-Degree", fmtHeaderCell);
			writeCell("N-O-Degree", fmtHeaderCell);
			writeCell("N-I-Degree", fmtHeaderCell);
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
			writeCell("NonRedundancy", fmtHeaderCell);
			writeCell("Distribution", fmtHeaderCell);
			writeCell("Cluster", fmtHeaderCell);
		} else if (type == 6) {
			writeCell("±àºÅ", fmtHeaderCell);
			writeCell("N", fmtHeaderCell);
			writeCell("Distribution", fmtHeaderCell);
			writeCell("Cluster", fmtHeaderCell);
		}
		currentRow++;
		currentCol = 0;
		// Write Cells
		if (type == 1 || type == 2 || type == 3 || type == 5) {
			boolean headerWritten = false;
			for (MatrixCalculationResult thisResult : resultToWrite) {
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
				writeCell(thisResult.distribution, fmtCell);
				writeCell(thisResult.cluster, fmtCell);
				if (sepLines) {
					currentRow++;
					currentCol = 0;
				}
			}
		} else if (type == 4) {
			for (MatrixCalculationResult thisResult : resultToWrite) {
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
				writeCell(thisResult.nonRedundancy, fmtCell);
				writeCell(thisResult.distribution, fmtCell);
				writeCell(thisResult.cluster, fmtCell);
				currentRow++;
				currentCol = 0;
			}
		} else if (type == 6) {
			for (MatrixCalculationResult thisResult : resultToWrite) {
				writeCell(thisResult.resultLable, fmtCell);
				writeCell(thisResult.N, fmtCell);
				writeCell(thisResult.distribution, fmtCell);
				writeCell(thisResult.cluster, fmtCell);
				currentRow++;
				currentCol = 0;
			}
		}
		// Dump Matrix
		if (type == 4 || type == 6) {
			WritableSheet sheet = workbook.createSheet("ÖÐ¼ä¾ØÕó", Integer.MAX_VALUE);
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
