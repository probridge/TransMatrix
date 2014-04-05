package transmatrix;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineContributionItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private IWorkbenchAction quitAction;
	private IWorkbenchAction aboutAction;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	@Override
	protected void makeActions(IWorkbenchWindow window) {
		super.makeActions(window);
		quitAction = ActionFactory.QUIT.create(window);
		quitAction.setText("�˳�����");
		aboutAction = ActionFactory.ABOUT.create(window);
		aboutAction.setText("���ڱ�����");
		ActionFactory.ABOUT.create(window);
	}

	@Override
	protected void fillMenuBar(IMenuManager menuBar) {
		super.fillMenuBar(menuBar);
		MenuManager systemMenu = new MenuManager("���ܲ˵�(&S)", "sysMenu");
		systemMenu.add(aboutAction);
		systemMenu.add(quitAction);
		menuBar.add(systemMenu);
	}

	@Override
	protected void fillStatusLine(IStatusLineManager statusLine) {
		StatusLineContributionItem heapUsageIndicator = new StatusLineContributionItem(
				"HeapMontior", 50);
		heapUsageIndicator.setText("�ڴ�ָʾ��");
		statusLine.add(heapUsageIndicator);
		new Thread(new HeapMonitor(heapUsageIndicator)).start();
		super.fillStatusLine(statusLine);
	}
}
