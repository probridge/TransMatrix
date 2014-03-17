package transmatrix;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;

public class OpenFileAction extends Action {
	private IWorkbenchWindow window;

	public OpenFileAction(IWorkbenchWindow window) {
		this.window = window;
		this.setText("���ļ�");
	}

	@Override
	public void run() {
		FileDialog imageFileDialog = new FileDialog(window.getShell(), SWT.OPEN);
		imageFileDialog.setText("ѡ����Ҫ�򿪵�Excel 97/2000�ļ�");
		imageFileDialog.open();
		String localFileName = imageFileDialog.getFileName();
		String prefixPath = imageFileDialog.getFilterPath();
		MainView mainView = ((MainView) window.getActivePage().findView(
				MainView.ID));
		if (mainView != null)
			mainView.populateOriginal(prefixPath.concat(
					System.getProperty("file.separator")).concat(localFileName));
	}
}
