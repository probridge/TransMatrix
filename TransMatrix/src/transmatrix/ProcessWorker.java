package transmatrix;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

public class ProcessWorker implements Runnable {
	private BatchView receiver = null;
	private File workfile;
	private int type;
	private boolean sym;
	private boolean sepLines;
	private String outputDir;
	private String msg;
	private boolean errFlag = false;
	private boolean minus1;

	public ProcessWorker(BatchView receiver, File workFile, int type,
			boolean sym, boolean minus1, boolean sepLines, String outputDir) {
		this.receiver = receiver;
		this.workfile = workFile;
		this.type = type;
		this.sym = sym;
		this.minus1 = minus1;
		this.sepLines = sepLines;
		this.outputDir = outputDir;
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
			wr = new ExcelResultWriter(workfile.getName(), outputDir, sepLines,
					type);
			//
			StringMatrix[] strMatrix = Matrix.loadAll(workfile, type);
			Thread.yield();
			MatrixCalculationResult[] thisResult = null;
			for (int i = 0; i < strMatrix.length; i++) {
				Thread.yield();
				NumberMatrix transDataMatrix = null;
				NumberMatrix transDataMatrix1 = null;
				switch (type) {
				case 1:
					for (int run = 1; run <= 12; run++) {
						transDataMatrix = MatrixPrepare.prepare1(run,
								strMatrix[i]);
						thisResult = MatrixCalculation.computeResults(
								transDataMatrix, null, type, sym);
						thisResult[0].resultLable += "_" + run;
						wr.appendResult(thisResult[0]);
					}
					wr.appendResult(null);
					break;
				case 2:
					for (int run = 1; run <= 2; run++) {
						NumberMatrix[] rr = MatrixPrepare.prepare2(run,
								strMatrix[i]);
						transDataMatrix = rr[0];
						transDataMatrix1 = rr[1];
						thisResult = MatrixCalculation.computeResults(
								transDataMatrix, transDataMatrix1, type, sym);
						thisResult[0].resultLable += "_" + run;
						wr.appendResult(thisResult[0]);
					}
					wr.appendResult(null);
					break;
				case 3:
					NumberMatrix[] rr = MatrixPrepare.prepare3(strMatrix[i]);
					transDataMatrix = rr[0];
					transDataMatrix1 = rr[1];
					thisResult = MatrixCalculation.computeResults(
							transDataMatrix, transDataMatrix1, type, sym);
					wr.appendResult(thisResult[0]);
					wr.appendResult(null);
					break;
				case 4:
					transDataMatrix = MatrixPrepare.prepare4(strMatrix[i], sym,
							minus1);
					wr.appendMatrix(transDataMatrix);
					thisResult = MatrixCalculation.computeResults(
							transDataMatrix, null, type, sym);
					for (int index = 0; index < strMatrix[i].row; index++) {
						wr.appendResult(thisResult[index]);
					}
					break;
				case 5:
					for (int run = 1; run <= 3; run++) {
						transDataMatrix = MatrixPrepare.prepare5(run,
								strMatrix[i]);
						thisResult = MatrixCalculation.computeResults(
								transDataMatrix, null, type, sym);
						thisResult[0].resultLable += "_" + run;
						wr.appendResult(thisResult[0]);
					}
					wr.appendResult(null);
					break;
				case 6:
					transDataMatrix = MatrixPrepare.prepare6(strMatrix[i]);
					wr.appendMatrix(transDataMatrix);
					thisResult = MatrixCalculation.computeResults(
							transDataMatrix, null, type, sym);
					wr.appendResult(thisResult[0]);
					break;
				default:
					break;
				}
			}
			wr.close();
		} catch (RuntimeException e) {
			errFlag = true;
			try {
				MessageDialog.openError(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell(), "严重错误",
						e.toString());
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
