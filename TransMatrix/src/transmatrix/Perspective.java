package transmatrix;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IFolderLayout;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
		IFolderLayout folderLayout = layout.createFolder("folder",
				IPageLayout.TOP, 0.5f, IPageLayout.ID_EDITOR_AREA);
		folderLayout.addView("transmatrix.BatchView");
	}
}
