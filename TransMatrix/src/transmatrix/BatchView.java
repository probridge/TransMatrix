package transmatrix;

import java.io.File;
import java.io.FilenameFilter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.SWTResourceManager;

public class BatchView extends ViewPart {

	public static final String ID = "transmatrix.BatchView"; //$NON-NLS-1$
	public Text txtLog;
	private Text txtDirectory;
	private Table fileListTable;
	private Text txtOutputDir;
	public CheckboxTableViewer fileListTableViewer;
	private String initPath = "C:\\"; // 1st time default
	public File[] files = null;
	private Button btnType1;
	private Button btnType2;
	private Button btnType3;
	private Button btnType4;
	private Button btnType5;
	private Button btn4Symmetric;
	private Button btnSingleLine;
	private Button btnSeparateLines;
	private BatchView self = this;
	public ProgressBar progressBar;
	private Button btnStart;
	private Group grpProgress;
	private long startTime = 0l;
	private Button btnStop;
	private BlockingQueue<Runnable> workloadQueue;
	private ThreadPoolExecutor executor;
	private Label lblSelectedFile;
	private Button btn4Minus1;
	private Button btnType6;

	public BatchView() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String val = store.getString(PreferenceInitializer.RECENT_DIRECTORY);
		if (val != null && val.length() > 0)
			initPath = val;

	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FormLayout());

		Group grpFiles = new Group(container, SWT.NONE);
		grpFiles.setText("\u8F93\u5165\u6587\u4EF6");
		grpFiles.setLayout(new FormLayout());
		FormData fd_grpFiles = new FormData();
		fd_grpFiles.left = new FormAttachment(0, 10);
		fd_grpFiles.top = new FormAttachment(0, 10);
		fd_grpFiles.bottom = new FormAttachment(0, 578);
		grpFiles.setLayoutData(fd_grpFiles);

		Group grpOptions = new Group(container, SWT.NONE);
		fd_grpFiles.right = new FormAttachment(100, -462);
		grpOptions.setText("\u5904\u7406\u65B9\u5F0F");
		FormData fd_grpOptions = new FormData();
		fd_grpOptions.left = new FormAttachment(grpFiles, 6);
		fd_grpOptions.right = new FormAttachment(100, -10);
		fd_grpOptions.top = new FormAttachment(0, 10);
		fd_grpOptions.bottom = new FormAttachment(0, 381);
		grpOptions.setLayoutData(fd_grpOptions);

		Group grpOutput = new Group(container, SWT.NONE);
		grpOutput.setText("\u8F93\u51FA\u9009\u9879");
		FormData fd_grpOutput = new FormData();
		fd_grpOutput.right = new FormAttachment(grpOptions, 0, SWT.RIGHT);
		fd_grpOutput.left = new FormAttachment(grpFiles, 6);
		fd_grpOutput.top = new FormAttachment(grpOptions, 6);
		grpOutput.setLayoutData(fd_grpOutput);

		btnStart = new Button(grpOutput, SWT.NONE);
		btnStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Create Output Directory
				File outputPath = new File(txtOutputDir.getText());
				if (!outputPath.exists())
					outputPath.mkdir();

				// Read Options
				int type = 1;
				if (btnType1.getSelection())
					type = 1;
				if (btnType2.getSelection())
					type = 2;
				if (btnType3.getSelection())
					type = 3;
				if (btnType4.getSelection())
					type = 4;
				if (btnType5.getSelection())
					type = 5;
				if (btnType6.getSelection())
					type = 6;

				Object[] fileToWork = fileListTableViewer.getCheckedElements();
				if (fileToWork != null && fileToWork.length > 0) {
					// Remember directory
					IPreferenceStore store = Activator.getDefault().getPreferenceStore();
					store.setValue(PreferenceInitializer.RECENT_DIRECTORY, txtDirectory.getText());
					//
					StructuredSelection sel = new StructuredSelection(fileToWork);
					fileListTableViewer.setSelection(sel, true);
					// Disable controls
					setControlStatus(false);
					progressBar.setSelection(progressBar.getMinimum());
					progressBar.setMinimum(0);
					progressBar.setMaximum(fileToWork.length);
					progressBar.setState(SWT.NORMAL);
					txtLog.setText("");
				}
				workloadQueue = new LinkedBlockingQueue<Runnable>();
				executor = new ThreadPoolExecutor(10, 10, 60, TimeUnit.SECONDS, workloadQueue);
				//
				startTime = System.currentTimeMillis();
				// Get each file & start thread
				for (Object eachFile : fileToWork) {
					if (eachFile instanceof File) {
						ProcessWorker workunit = new ProcessWorker(self, (File) eachFile, type, btn4Symmetric
								.getSelection(), btn4Minus1.getSelection(), btnSeparateLines.getSelection(),
								txtOutputDir.getText());
						executor.submit(workunit);
					}
				}
				executor.shutdown();
			}
		});
		btnStart.setBounds(27, 132, 139, 36);
		btnStart.setText("\u5F00\u59CB\u5904\u7406");

		btnSeparateLines = new Button(grpOutput, SWT.RADIO);
		btnSeparateLines.setBounds(278, 32, 152, 17);
		btnSeparateLines.setText("\u6BCF\u4E2A\u7ED3\u679C\u8F93\u51FA\u4E3A\u5355\u72EC\u7684\u884C");

		btnSingleLine = new Button(grpOutput, SWT.RADIO);
		btnSingleLine.setSelection(true);
		btnSingleLine.setBounds(110, 32, 162, 17);
		btnSingleLine.setText("\u6BCF\u4E2A\u77E9\u9635\u5408\u5E76\u8F93\u51FA\u4E3A\u4E00\u884C");

		Label label_1 = new Label(grpOutput, SWT.NONE);
		label_1.setBounds(27, 66, 269, 17);
		label_1.setText("\u8F93\u51FA\u76EE\u5F55\uFF1A\uFF08\u6CE8\u610F\uFF1A\u540C\u540D\u6587\u4EF6\u5C06\u4F1A\u88AB\u8986\u76D6\uFF01\uFF09");

		txtOutputDir = new Text(grpOutput, SWT.BORDER);
		txtOutputDir.setBounds(27, 89, 403, 23);

		btnStop = new Button(grpOutput, SWT.NONE);
		btnStop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				executor.purge();
				executor.shutdown();
				executor.getQueue().clear();
				progressBar.setSelection(progressBar.getMaximum());
				progress(true);
				txtLog.append("用户中断，请等待所有线程完成！供耗时：" + (System.currentTimeMillis() - startTime) / 1000 + "秒\n");
			}
		});
		btnStop.setEnabled(false);
		btnStop.setBounds(179, 132, 139, 36);
		btnStop.setText("\u505C\u6B62");

		Label label_2 = new Label(grpOutput, SWT.NONE);
		label_2.setText("\u7C7B\u578B1,2,3,5\uFF1A");
		label_2.setBounds(27, 32, 77, 17);

		btnType1 = new Button(grpOptions, SWT.RADIO);
		btnType1.setSelection(true);
		btnType1.setBounds(28, 38, 97, 17);
		btnType1.setText("\u7C7B\u578B\u4E00");

		btnType2 = new Button(grpOptions, SWT.RADIO);
		btnType2.setText("\u7C7B\u578B\u4E8C");
		btnType2.setBounds(28, 93, 97, 17);

		btnType3 = new Button(grpOptions, SWT.RADIO);
		btnType3.setText("\u7C7B\u578B\u4E09");
		btnType3.setBounds(28, 148, 97, 17);

		btnType4 = new Button(grpOptions, SWT.RADIO);
		btnType4.setText("\u7C7B\u578B\u56DB");
		btnType4.setBounds(28, 203, 97, 17);

		btnType5 = new Button(grpOptions, SWT.RADIO);
		btnType5.setText("\u7C7B\u578B\u4E94");
		btnType5.setBounds(28, 258, 97, 17);

		btn4Symmetric = new Button(grpOptions, SWT.CHECK);
		btn4Symmetric.setBounds(131, 203, 98, 17);
		btn4Symmetric.setText("\u5BF9\u79F0\u5316\u5904\u7406");

		btn4Minus1 = new Button(grpOptions, SWT.CHECK);
		btn4Minus1.setText("\u51CF\u4E00\u5904\u7406");
		btn4Minus1.setBounds(235, 203, 98, 17);

		btnType6 = new Button(grpOptions, SWT.RADIO);
		btnType6.setText("\u7C7B\u578B\u516D\uFF1ADistribution - Cluster");
		btnType6.setBounds(28, 313, 201, 17);

		Label label = new Label(grpFiles, SWT.NONE);
		FormData fd_label = new FormData();
		fd_label.bottom = new FormAttachment(0, 48);
		fd_label.right = new FormAttachment(0, 45);
		fd_label.top = new FormAttachment(0, 25);
		fd_label.left = new FormAttachment(0);
		label.setLayoutData(fd_label);
		label.setAlignment(SWT.RIGHT);
		label.setText("\u8DEF\u5F84\uFF1A");

		txtDirectory = new Text(grpFiles, SWT.BORDER);
		FormData fd_txtDirectory = new FormData();
		fd_txtDirectory.left = new FormAttachment(0, 51);
		fd_txtDirectory.right = new FormAttachment(0, 400);
		fd_txtDirectory.bottom = new FormAttachment(0, 51);
		fd_txtDirectory.top = new FormAttachment(0, 24);
		txtDirectory.setLayoutData(fd_txtDirectory);
		txtDirectory.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == 13) {
					listFiles();
					fileListTableViewer.setInput(files);
				}

			}
		});
		txtDirectory.setText(initPath);

		Button btnSelectDir = new Button(grpFiles, SWT.NONE);
		FormData fd_btnSelectDir = new FormData();
		fd_btnSelectDir.bottom = new FormAttachment(txtDirectory, 0, SWT.BOTTOM);
		fd_btnSelectDir.left = new FormAttachment(txtDirectory, 6);
		fd_btnSelectDir.right = new FormAttachment(0, 453);
		btnSelectDir.setLayoutData(fd_btnSelectDir);
		btnSelectDir.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dd = new DirectoryDialog(getSite().getShell());
				dd.setText("选择样本文件所在目录");
				dd.setMessage("选择样本文件所在目录");
				String dir = dd.open();
				if (dir != null && dir.length() > 0)
					txtDirectory.setText(dir);
				listFiles();
				fileListTableViewer.setInput(files);
			}
		});
		btnSelectDir.setText("\u6D4F\u89C8");

		fileListTableViewer = CheckboxTableViewer.newCheckList(grpFiles, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		fileListTable = fileListTableViewer.getTable();
		FormData fd_fileListTable = new FormData();
		fd_fileListTable.left = new FormAttachment(0, 51);
		fd_fileListTable.right = new FormAttachment(0, 400);
		fd_fileListTable.bottom = new FormAttachment(0, 519);
		fd_fileListTable.top = new FormAttachment(0, 60);
		fileListTable.setLayoutData(fd_fileListTable);

		lblSelectedFile = new Label(grpFiles, SWT.NONE);
		FormData fd_lblSelectedFile = new FormData();
		fd_lblSelectedFile.right = new FormAttachment(btnSelectDir, 0, SWT.RIGHT);
		fd_lblSelectedFile.top = new FormAttachment(0, 528);
		fd_lblSelectedFile.left = new FormAttachment(0, 51);
		lblSelectedFile.setLayoutData(fd_lblSelectedFile);
		lblSelectedFile.setText("\u8BF7\u9009\u62E9\u4E00\u4E2A\u6216\u8005\u591A\u4E2A\u6587\u4EF6\uFF01");

		Button btnSelectAll = new Button(grpFiles, SWT.NONE);
		FormData fd_btnSelectAll = new FormData();
		fd_btnSelectAll.left = new FormAttachment(btnSelectDir, 0, SWT.LEFT);
		fd_btnSelectAll.right = new FormAttachment(0, 453);
		btnSelectAll.setLayoutData(fd_btnSelectAll);
		btnSelectAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fileListTableViewer.setAllChecked(true);
				updateSelection();
			}
		});
		btnSelectAll.setText("\u5168\u9009");

		Button btnSelectNone = new Button(grpFiles, SWT.NONE);
		fd_btnSelectAll.bottom = new FormAttachment(100, -386);
		FormData fd_btnSelectNone = new FormData();
		fd_btnSelectNone.left = new FormAttachment(btnSelectDir, 0, SWT.LEFT);
		fd_btnSelectNone.right = new FormAttachment(0, 453);
		fd_btnSelectNone.top = new FormAttachment(btnSelectAll, 7);
		btnSelectNone.setLayoutData(fd_btnSelectNone);
		btnSelectNone.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fileListTableViewer.setAllChecked(false);
				updateSelection();
			}
		});
		btnSelectNone.setText("\u4E0D\u9009");

		Button btnSelectInv = new Button(grpFiles, SWT.NONE);
		FormData fd_btnSelectInv = new FormData();
		fd_btnSelectInv.top = new FormAttachment(btnSelectAll, 40);
		fd_btnSelectInv.left = new FormAttachment(btnSelectDir, 0, SWT.LEFT);
		fd_btnSelectInv.right = new FormAttachment(0, 453);
		btnSelectInv.setLayoutData(fd_btnSelectInv);
		btnSelectInv.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (File eachFile : files)
					fileListTableViewer.setChecked(eachFile, !fileListTableViewer.getChecked(eachFile));
				updateSelection();
			}
		});
		btnSelectInv.setText("\u53CD\u9009");

		grpProgress = new Group(container, SWT.NONE);
		fd_grpOutput.bottom = new FormAttachment(grpProgress, -6);
		grpProgress.setLayout(new FormLayout());
		FormData fd_group = new FormData();
		fd_group.bottom = new FormAttachment(100, -10);
		fd_group.top = new FormAttachment(grpFiles, 6);
		fd_group.left = new FormAttachment(0, 10);
		fd_group.right = new FormAttachment(100, -10);
		grpProgress.setLayoutData(fd_group);

		progressBar = new ProgressBar(grpProgress, SWT.SMOOTH);
		FormData fd_progressBar = new FormData();
		fd_progressBar.right = new FormAttachment(100, -13);
		fd_progressBar.left = new FormAttachment(0, 7);
		fd_progressBar.top = new FormAttachment(0);
		fd_progressBar.bottom = new FormAttachment(0, 17);
		progressBar.setLayoutData(fd_progressBar);

		txtLog = new Text(grpProgress, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		txtLog.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtLog.setFont(SWTResourceManager.getFont("Courier New", 10, SWT.NORMAL));
		FormData fd_txtLog = new FormData();
		fd_txtLog.top = new FormAttachment(0, 35);
		fd_txtLog.right = new FormAttachment(progressBar, 0, SWT.RIGHT);
		fd_txtLog.left = new FormAttachment(progressBar, 0, SWT.LEFT);
		fd_txtLog.bottom = new FormAttachment(100, -10);
		txtLog.setLayoutData(fd_txtLog);

		fileListTableViewer.addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				updateSelection();
			}
		});

		listFiles();
		fileListTableViewer.setLabelProvider(new FileListLabelProvider());
		fileListTableViewer.setContentProvider(new FileListContentProvider());
		fileListTableViewer.setInput(files);
	}

	protected void updateSelection() {
		if (fileListTableViewer.getCheckedElements().length > 0)
			lblSelectedFile.setText("选择了" + fileListTableViewer.getCheckedElements().length + "个文件...");
		else
			lblSelectedFile.setText("请选择一个或者多个文件！");
	}

	protected void setControlStatus(boolean status) {
		txtDirectory.setEnabled(status);
		fileListTable.setEnabled(status);
		txtOutputDir.setEnabled(status);
		btnStart.setEnabled(status);
		btnStop.setEnabled(!status);
		btnType1.setEnabled(status);
		btnType2.setEnabled(status);
		btnType3.setEnabled(status);
		btnType4.setEnabled(status);
		btnType5.setEnabled(status);
		btnType6.setEnabled(status);
		btn4Symmetric.setEnabled(status);
		btn4Minus1.setEnabled(status);
		btnSingleLine.setEnabled(status);
		btnSeparateLines.setEnabled(status);
	}

	private void listFiles() {
		File p = new File(txtDirectory.getText());
		if (p.exists() && p.isDirectory()) {
			files = p.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File f, String fn) {
					return fn.toLowerCase().endsWith(".xls");
				}
			});
			txtOutputDir.setText(p.getAbsolutePath() + System.getProperty("file.separator") + "result"
					+ System.getProperty("file.separator"));
			progressBar.setMinimum(0);
			progressBar.setSelection(0);
		}
	}

	public void appendLog(String msg) {
		txtLog.append(msg + "\n");
	}

	public void progress(boolean errFlag) {
		if (errFlag)
			progressBar.setState(SWT.ERROR);
		progressBar.setSelection(progressBar.getSelection() + 1);
		grpProgress.setText("正在处理...已完成" + (progressBar.getSelection() * 100 / progressBar.getMaximum()) + "%.."
				+ (System.currentTimeMillis() - startTime) / 1000 + "s");
		btnStart.setText("已完成: " + progressBar.getSelection() + " / " + progressBar.getMaximum());
		if (progressBar.getSelection() >= progressBar.getMaximum()) {
			setControlStatus(true);
			grpProgress.setText("处理结束！供耗时：" + (System.currentTimeMillis() - startTime) / 1000 + "秒");
			btnStart.setText("开始");
		}
	}

	@Override
	public void setFocus() {
		// Set the focus
	}
}
