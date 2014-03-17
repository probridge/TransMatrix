package transmatrix;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;

public class ExportFileAction extends Action {
	private IWorkbenchWindow window;

	public ExportFileAction(IWorkbenchWindow window) {
		this.window = window;
		this.setText("����ת������");
	}

	@Override
	public void run() {
		FileDialog transMatrixFileDialog = new FileDialog(window.getShell(), SWT.OPEN);
		transMatrixFileDialog.setText("ѡ����Ҫ������ļ���");
		transMatrixFileDialog.setFileName("ת������.xls");
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
