package transmatrix;

import org.eclipse.jface.action.StatusLineContributionItem;
import org.eclipse.ui.PlatformUI;

public class HeapMonitor implements Runnable {

	private StatusLineContributionItem receiver;

	public HeapMonitor(StatusLineContributionItem receiver) {
		this.receiver = receiver;
	}

	@Override
	public void run() {
		try {

			while (true) {
				Thread.sleep(1000);
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
					@Override
					public void run() {
						long heapSize = Runtime.getRuntime().totalMemory() / 1048576;
						long heapMaxSize = Runtime.getRuntime().maxMemory() / 1048576;
						long heapFreeSize = Runtime.getRuntime().freeMemory() / 1048576;
						receiver.setText("Heap(Free/Total/Max)£º" + heapFreeSize
								+ "M/" + heapSize + "M/" + heapMaxSize + "M");
					}
				});
			}
		} catch (Exception e) {

		}
	}
}
