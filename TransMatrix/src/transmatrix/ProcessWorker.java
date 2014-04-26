package transmatrix;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

public class ProcessWorker implements Runnable {
	private BatchView receiver = null;
	private File workfile;
	private int type;
	private int normalizeN;
	private boolean sym;
	private boolean sepLines;
	private String outputDir;
	private String msg;
	private boolean errFlag = false;
	private boolean minus1;

	public ProcessWorker(BatchView receiver, File workFile, int type, boolean sym, boolean minus1, int normalizeN,
			boolean sepLines, String outputDir) {
		this.receiver = receiver;
		this.workfile = workFile;
		this.type = type;
		this.sym = sym;
		this.minus1 = minus1;
		this.sepLines = sepLines;
		this.outputDir = outputDir;
		this.normalizeN = normalizeN;
	}

	//
	@Override
	public void run() {
		msg = "...[" + workfile.getName() + "]开始计算…";
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				if (receiver != null) {
					receiver.appendLog(msg);
				}
			}
		});
		msg = "...[" + workfile.getName() + "]计算完成！";
		ExcelResultWriter wr = null;
		try {
			wr = new ExcelResultWriter(workfile.getName(), outputDir, sepLines, type, normalizeN);
			//
			StringMatrix[] strMatrix = Matrix.loadAll(workfile, type);
			Thread.yield();
			MatrixResults thisResult = null;
			for (int i = 0; i < strMatrix.length; i++) {
				Thread.yield();
				NumberMatrix transDataMatrix = null;
				switch (type) {
				case 1:
					for (int run = 1; run <= 12; run++) {
						transDataMatrix = MatrixPrepare.prepare1(run, strMatrix[i]);
						thisResult = MatrixCalculation.computeResults(transDataMatrix, type, sym, normalizeN);
						thisResult.rowResults[0].resultLable += "#" + run;
						wr.appendResult(thisResult.rowResults[0]); // 取第一行
						wr.appendCircle(thisResult.rowResults[0].resultLable, thisResult.circles,
								thisResult.circleDensity, thisResult.connectingNodes, thisResult.circlesMatrix,
								thisResult.circleMatrixEfficienty, thisResult.circleMatrixContraint);
					}
					//
					wr.appendResult(null);
					break;
				case 2:
					for (int run = 1; run <= 2; run++) {
						transDataMatrix = MatrixPrepare.prepare2(run, strMatrix[i]);
						thisResult = MatrixCalculation.computeResults(transDataMatrix, type, sym, normalizeN);
						thisResult.rowResults[0].resultLable += "#" + run;
						wr.appendResult(thisResult.rowResults[0]); // 取第一行
						wr.appendCircle(thisResult.rowResults[0].resultLable, thisResult.circles,
								thisResult.circleDensity, thisResult.connectingNodes, thisResult.circlesMatrix,
								thisResult.circleMatrixEfficienty, thisResult.circleMatrixContraint);
					}
					//
					wr.appendResult(null);
					break;
				case 3:
					transDataMatrix = MatrixPrepare.prepare3(strMatrix[i]);
					thisResult = MatrixCalculation.computeResults(transDataMatrix, type, sym, normalizeN);
					wr.appendResult(thisResult.rowResults[0]); // 取第一行
					wr.appendResult(null);
					//
					wr.appendCircle(thisResult.rowResults[0].resultLable, thisResult.circles, thisResult.circleDensity,
							thisResult.connectingNodes, thisResult.circlesMatrix, thisResult.circleMatrixEfficienty,
							thisResult.circleMatrixContraint);
					break;
				case 4:
					transDataMatrix = MatrixPrepare.prepare4(strMatrix[i], sym, minus1);
					wr.appendMatrix(transDataMatrix);
					thisResult = MatrixCalculation.computeResults(transDataMatrix, type, sym, normalizeN);
					for (int index = 0; index < thisResult.rowResults.length; index++) {
						wr.appendResult(thisResult.rowResults[index]); // 取所有行
					}
					//
					wr.appendCircle(thisResult.rowResults[0].resultLable, thisResult.circles, thisResult.circleDensity,
							thisResult.connectingNodes, thisResult.circlesMatrix, thisResult.circleMatrixEfficienty,
							thisResult.circleMatrixContraint);
					break;
				case 5:
					for (int run = 1; run <= 3; run++) {
						transDataMatrix = MatrixPrepare.prepare5(run, strMatrix[i]);
						thisResult = MatrixCalculation.computeResults(transDataMatrix, type, sym, normalizeN);
						thisResult.rowResults[0].resultLable += "#" + run;
						wr.appendResult(thisResult.rowResults[0]); // 取第一行
						//
						wr.appendCircle(thisResult.rowResults[0].resultLable, thisResult.circles,
								thisResult.circleDensity, thisResult.connectingNodes, thisResult.circlesMatrix,
								thisResult.circleMatrixEfficienty, thisResult.circleMatrixContraint);
					}
					wr.appendResult(null);
					break;
				default:
					break;
				}
			}
			wr.close();
		} catch (RuntimeException e) {
			errFlag = true;
			try {
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "严重错误",
						e.getMessage());
				if (wr != null)
					wr.close();
			} catch (Exception ee) {
			}
			msg = "ERR[" + workfile.getName() + "]处理错误！信息：" + e.toString();
			File resultFile = new File(outputDir + workfile.getName());
			if (resultFile.exists())
				resultFile.delete();
		} catch (Exception e) {
			errFlag = true;
			try {
				if (wr != null)
					wr.close();
			} catch (Exception ee) {
			}
			e.printStackTrace();
			msg = "ERR[" + workfile.getName() + "]处理错误！信息：" + e.toString();
			File resultFile = new File(outputDir + workfile.getName());
			if (resultFile.exists())
				resultFile.delete();
		}
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				if (receiver != null) {
					receiver.appendLog(msg);
					receiver.progress(errFlag);
				}
			}
		});
	}
}
