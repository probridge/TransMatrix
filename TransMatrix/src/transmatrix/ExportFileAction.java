package transmatrix;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;

public class ExportFileAction extends Action {
	private IWorkbenchWindow window;

	public ExportFileAction(IWorkbenchWindow window) {
		this.window = window;
		this.setText("保存转换矩阵");
	}

	@Override
	public void run() {
		FileDialog transMatrixFileDialog = new FileDialog(window.getShell(), SWT.OPEN);
		transMatrixFileDialog.setText("选择需要保存的文件名");
		transMatrixFileDialog.setFileName("转换矩阵.xls");
		transMatrixFileDialog.open();
		String localFileName = transMatrixFileDialog.getFileName();
		String prefixPath = transMatrixFileDialog.getFilterPath();
		MainView mainView = ((MainView) window.getActivePage().findView(
				MainView.ID));
		if (mainView != null)
			mainView.exportTransformed(prefixPath.concat(
					System.getProperty("file.separator")).concat(localFileName));
	}
}
