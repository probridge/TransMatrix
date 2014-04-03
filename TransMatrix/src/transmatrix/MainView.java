package transmatrix;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

public class MainView extends ViewPart {
	public MainView() {
	}

	public static final String ID = "transmatrix.MainView";
	private StringMatrix originalDataMatrix = null;
	private NumberMatrix transDataMatrix = null;
	private ArrayList<TableViewerColumn> originalColumns = new ArrayList<TableViewerColumn>();
	private ArrayList<TableViewerColumn> transColumns = new ArrayList<TableViewerColumn>();
	private InputTableContentProvider originalMatrixCP = new InputTableContentProvider();
	private InputTableLabelProvider originalMatrixLP = new InputTableLabelProvider();
	private InputTableContentProvider transMatrixCP = new InputTableContentProvider();
	private InputTableLabelProvider transMatrixLP = new InputTableLabelProvider();
	private Table originalTable;
	private TableViewer originalTableViewer;
	private Table transTable;
	private TableViewer transTableViewer;
	private TabItem originalTab;
	private TabItem transTab;
	private TabFolder tabFolder;
	private Table resultTable;
	private TableViewer resultTableViewer;
	private TabItem resultTab;
	private MatrixCalculationResult[] thisResult = new MatrixCalculationResult[0];
	protected NumberMatrix transDataMatrix1;
	protected boolean sym;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		parent.setLayout(new FormLayout());
		tabFolder = new TabFolder(parent, SWT.NONE);
		FormData fd_tabFolder = new FormData();
		fd_tabFolder.bottom = new FormAttachment(0, 415);
		fd_tabFolder.right = new FormAttachment(0, 1007);
		fd_tabFolder.top = new FormAttachment(0, 15);
		fd_tabFolder.left = new FormAttachment(0, 11);
		tabFolder.setLayoutData(fd_tabFolder);

		Button btnTrans1 = new Button(parent, SWT.NONE);
		btnTrans1.setText("\u8F6C\u6362\u4E00");
		Button btnTrans2 = new Button(parent, SWT.NONE);
		btnTrans2.setText("\u8F6C\u6362\u4E8C");
		Button btnTrans3 = new Button(parent, SWT.NONE);
		btnTrans3.setText("\u8F6C\u6362\u4E09");
		Button btnTrans4 = new Button(parent, SWT.NONE);
		btnTrans4.setText("\u8F6C\u6362\u56DB");
		Button btnTrans5 = new Button(parent, SWT.NONE);
		btnTrans5.setText("\u8F6C\u6362\u4E94");
		Button btnTrans6 = new Button(parent, SWT.NONE);
		btnTrans6.setText("\u8F6C\u6362\u516D");

		FormData fd_btnTrans1 = new FormData();
		fd_btnTrans1.top = new FormAttachment(tabFolder, 6);
		fd_btnTrans1.left = new FormAttachment(tabFolder, 10, SWT.LEFT);
		// fd_btnTrans1.right = new FormAttachment(0, 103);

		FormData fd_btnTrans2 = new FormData();
		fd_btnTrans2.top = new FormAttachment(tabFolder, 6);
		fd_btnTrans2.left = new FormAttachment(btnTrans1, 25);
		// fd_btnTrans2.right = new FormAttachment(100, -725);

		FormData fd_btnTrans3 = new FormData();
		fd_btnTrans3.top = new FormAttachment(tabFolder, 6);
		fd_btnTrans3.left = new FormAttachment(btnTrans2, 25);
		// fd_btnTrans3.right = new FormAttachment(btnTrans4, -24);

		FormData fd_btnTrans4 = new FormData();
		fd_btnTrans4.top = new FormAttachment(tabFolder, 6);
		fd_btnTrans4.left = new FormAttachment(btnTrans3, 25);
		// fd_btnTrans4.right = new FormAttachment(btnTrans5, -21);

		FormData fd_btnTrans5 = new FormData();
		fd_btnTrans5.top = new FormAttachment(tabFolder, 6);
		fd_btnTrans5.left = new FormAttachment(btnTrans4, 25);
		// fd_btnTrans5.right = new FormAttachment(100, -384);

		FormData fd_btnTrans6 = new FormData();
		fd_btnTrans6.top = new FormAttachment(tabFolder, 6);
		fd_btnTrans6.left = new FormAttachment(btnTrans5, 25);
		// fd_btnTrans6.right = new FormAttachment(100, -278);

		btnTrans1.setLayoutData(fd_btnTrans1);
		btnTrans2.setLayoutData(fd_btnTrans2);
		btnTrans3.setLayoutData(fd_btnTrans3);
		btnTrans4.setLayoutData(fd_btnTrans4);
		btnTrans5.setLayoutData(fd_btnTrans5);
		btnTrans6.setLayoutData(fd_btnTrans6);

		originalTab = new TabItem(tabFolder, SWT.NONE);
		originalTab.setText("\u539F\u59CB\u6570\u636E");

		originalTableViewer = new TableViewer(tabFolder, SWT.BORDER);
		originalTable = originalTableViewer.getTable();
		originalTab.setControl(originalTable);
		originalTable.setLinesVisible(true);
		originalTable.setHeaderVisible(true);
		originalTableViewer.setContentProvider(originalMatrixCP);
		originalTableViewer.setLabelProvider(originalMatrixLP);
		//
		transTab = new TabItem(tabFolder, SWT.NONE);
		transTab.setText("\u8F6C\u6362\u77E9\u9635");

		transTableViewer = new TableViewer(tabFolder, SWT.BORDER);
		transTable = transTableViewer.getTable();
		transTable.setLinesVisible(true);
		transTable.setHeaderVisible(true);
		transTab.setControl(transTable);

		resultTab = new TabItem(tabFolder, SWT.NONE);
		resultTab.setText("\u8F93\u51FA\u7ED3\u679C");

		resultTableViewer = new TableViewer(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		resultTable = resultTableViewer.getTable();
		resultTable.setLinesVisible(true);
		resultTable.setHeaderVisible(true);
		resultTab.setControl(resultTable);

		TableViewerColumn tableViewerColumn_11 = new TableViewerColumn(resultTableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn_11 = tableViewerColumn_11.getColumn();
		tblclmnNewColumn_11.setWidth(30);
		tblclmnNewColumn_11.setText("No");

		TableViewerColumn tableViewerColumn = new TableViewerColumn(resultTableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn = tableViewerColumn.getColumn();
		tblclmnNewColumn.setWidth(70);
		tblclmnNewColumn.setText("O-Degree");

		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(resultTableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn_1 = tableViewerColumn_1.getColumn();
		tblclmnNewColumn_1.setWidth(70);
		tblclmnNewColumn_1.setText("I-Degree");

		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(resultTableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn_2 = tableViewerColumn_2.getColumn();
		tblclmnNewColumn_2.setWidth(85);
		tblclmnNewColumn_2.setText("N-O-Degree");

		TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(resultTableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn_3 = tableViewerColumn_3.getColumn();
		tblclmnNewColumn_3.setWidth(85);
		tblclmnNewColumn_3.setText("N-I-Degree");

		TableViewerColumn tableViewerColumn_4 = new TableViewerColumn(resultTableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn_4 = tableViewerColumn_4.getColumn();
		tblclmnNewColumn_4.setWidth(70);
		tblclmnNewColumn_4.setText("Between");

		TableViewerColumn tableViewerColumn_5 = new TableViewerColumn(resultTableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn_5 = tableViewerColumn_5.getColumn();
		tblclmnNewColumn_5.setWidth(85);
		tblclmnNewColumn_5.setText("N-Between");

		TableViewerColumn tableViewerColumn_6 = new TableViewerColumn(resultTableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn_6 = tableViewerColumn_6.getColumn();
		tblclmnNewColumn_6.setWidth(85);
		tblclmnNewColumn_6.setText("N-O-Close");

		TableViewerColumn tableViewerColumn_7 = new TableViewerColumn(resultTableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn_7 = tableViewerColumn_7.getColumn();
		tblclmnNewColumn_7.setWidth(85);
		tblclmnNewColumn_7.setText("N-I-Close");

		TableViewerColumn tableViewerColumn_12 = new TableViewerColumn(resultTableViewer, SWT.NONE);
		TableColumn tblclmnOclose = tableViewerColumn_12.getColumn();
		tblclmnOclose.setWidth(70);
		tblclmnOclose.setText("O-Close2");

		TableViewerColumn tableViewerColumn_13 = new TableViewerColumn(resultTableViewer, SWT.NONE);
		TableColumn tblclmnIclose = tableViewerColumn_13.getColumn();
		tblclmnIclose.setWidth(70);
		tblclmnIclose.setText("I-Close2");

		TableViewerColumn tableViewerColumn_8 = new TableViewerColumn(resultTableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn_8 = tableViewerColumn_8.getColumn();
		tblclmnNewColumn_8.setWidth(80);
		tblclmnNewColumn_8.setText("O-Efficiency");

		TableViewerColumn tableViewerColumn_18 = new TableViewerColumn(resultTableViewer, SWT.NONE);
		TableColumn tblclmnIefficiency = tableViewerColumn_18.getColumn();
		tblclmnIefficiency.setWidth(80);
		tblclmnIefficiency.setText("I-Efficiency");

		TableViewerColumn tableViewerColumn_9 = new TableViewerColumn(resultTableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn_9 = tableViewerColumn_9.getColumn();
		tblclmnNewColumn_9.setWidth(80);
		tblclmnNewColumn_9.setText("O-Constraint");

		TableViewerColumn tableViewerColumn_19 = new TableViewerColumn(resultTableViewer, SWT.NONE);
		TableColumn tblclmnIconstraint = tableViewerColumn_19.getColumn();
		tblclmnIconstraint.setWidth(80);
		tblclmnIconstraint.setText("I-Constraint");

		TableViewerColumn tableViewerColumn_10 = new TableViewerColumn(resultTableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn_10 = tableViewerColumn_10.getColumn();
		tblclmnNewColumn_10.setWidth(85);
		tblclmnNewColumn_10.setText("EgoDensity");

		TableViewerColumn tableViewerColumn_20 = new TableViewerColumn(resultTableViewer, SWT.NONE);
		TableColumn tblclmnNonredundancy = tableViewerColumn_20.getColumn();
		tblclmnNonredundancy.setWidth(110);
		tblclmnNonredundancy.setText("NonRedundancy");

		TableViewerColumn tableViewerColumn_14 = new TableViewerColumn(resultTableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn_12 = tableViewerColumn_14.getColumn();
		tblclmnNewColumn_12.setWidth(70);
		tblclmnNewColumn_12.setText("Density");

		TableViewerColumn tableViewerColumn_15 = new TableViewerColumn(resultTableViewer, SWT.NONE);
		TableColumn tblclmnN = tableViewerColumn_15.getColumn();
		tblclmnN.setWidth(30);
		tblclmnN.setText("N");

		TableViewerColumn tableViewerColumn_16 = new TableViewerColumn(resultTableViewer, SWT.NONE);
		TableColumn tblclmnStrength = tableViewerColumn_16.getColumn();
		tblclmnStrength.setWidth(80);
		tblclmnStrength.setText("I-Strength");

		TableViewerColumn tableViewerColumn_17 = new TableViewerColumn(resultTableViewer, SWT.NONE);
		TableColumn tblclmnOstrength = tableViewerColumn_17.getColumn();
		tblclmnOstrength.setWidth(80);
		tblclmnOstrength.setText("O-Strength");

		TableViewerColumn tableViewerColumn_21 = new TableViewerColumn(resultTableViewer, SWT.NONE);
		TableColumn tblclmnIorigstrength = tableViewerColumn_21.getColumn();
		tblclmnIorigstrength.setWidth(100);
		tblclmnIorigstrength.setText("I-Orig-Strength");

		TableViewerColumn tableViewerColumn_22 = new TableViewerColumn(resultTableViewer, SWT.NONE);
		TableColumn tblclmnOorigstrength = tableViewerColumn_22.getColumn();
		tblclmnOorigstrength.setWidth(100);
		tblclmnOorigstrength.setText("O-Orig-Strength");

		TableViewerColumn tableViewerColumn_23 = new TableViewerColumn(resultTableViewer, SWT.NONE);
		TableColumn tblclmnDistribution = tableViewerColumn_23.getColumn();
		tblclmnDistribution.setWidth(100);
		tblclmnDistribution.setText("Distribution");

		TableViewerColumn tableViewerColumn_24 = new TableViewerColumn(resultTableViewer, SWT.NONE);
		TableColumn tblclmnCluster = tableViewerColumn_24.getColumn();
		tblclmnCluster.setWidth(100);
		tblclmnCluster.setText("Cluster");
		//
		resultTableViewer.setLabelProvider(new ResultLabelProvider());
		resultTableViewer.setContentProvider(new ResultContentProvider());
		transTableViewer.setContentProvider(transMatrixCP);
		transTableViewer.setLabelProvider(transMatrixLP);

		btnTrans1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				InputDialog inputRunDialog = new InputDialog(getSite().getShell(), "输入Type1的Run", "Run Number", "1",
						null);
				inputRunDialog.open();
				String runNumber = inputRunDialog.getValue();
				int zz = inputRunDialog.getReturnCode();
				if (runNumber == null || runNumber.length() == 0 || zz > 0) {
					MessageDialog.openInformation(getSite().getShell(), "Input Run Pls", "Run: 1-12");
					return;
				}
				int run = 0;
				try {
					run = Integer.parseInt(runNumber);
					if (run < 1 || run > 12)
						throw new NumberFormatException();
				} catch (NumberFormatException ee) {
					MessageDialog.openInformation(getSite().getShell(), "Input Run Pls", "Run: 1-12");
					return;
				}
				transDataMatrix = MatrixPrepare.prepare1(run, originalDataMatrix);
				// populate table columns
				displayTransMatrix();
				// calculation
				thisResult = MatrixCalculation.computeResults(transDataMatrix, 1, true);
				//
				resultTableViewer.setInput(thisResult);
				//
				tabFolder.setSelection(resultTab);
			}
		});
		btnTrans2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				InputDialog inputRunDialog = new InputDialog(getSite().getShell(), "输入Type2的Run", "Run Number", "1",
						null);
				inputRunDialog.open();
				String runNumber = inputRunDialog.getValue();
				int zz = inputRunDialog.getReturnCode();
				if (runNumber == null || runNumber.length() == 0 || zz > 0) {
					MessageDialog.openInformation(getSite().getShell(), "Input Run Pls", "Run: 1, 2");
					return;
				}
				int run = 0;
				try {
					run = Integer.parseInt(runNumber);
					if (run < 1 || run > 2)
						throw new NumberFormatException();
				} catch (NumberFormatException ee) {
					MessageDialog.openInformation(getSite().getShell(), "Input Run Pls", "Run: 1, 2");
					return;
				}
				NumberMatrix transDataMatrix = MatrixPrepare.prepare2(run, originalDataMatrix);
				// populate table columns
				displayTransMatrix();
				// calculation
				thisResult = MatrixCalculation.computeResults(transDataMatrix, 2, true);
				//
				resultTableViewer.setInput(thisResult);
				//
				tabFolder.setSelection(resultTab);
			}
		});
		btnTrans3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				NumberMatrix transDataMatrix = MatrixPrepare.prepare3(originalDataMatrix);
				// populate table columns
				displayTransMatrix();
				// calculation
				thisResult = MatrixCalculation.computeResults(transDataMatrix, 3, true);
				//
				resultTableViewer.setInput(thisResult);
				//
				tabFolder.setSelection(resultTab);
			}
		});
		btnTrans4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				InputDialog inputRunDialog = new InputDialog(getSite().getShell(), "输入Type4是否需要对称化", "对称化", "N", null);
				inputRunDialog.open();
				String runNumber = inputRunDialog.getValue();
				int zz = inputRunDialog.getReturnCode();
				if (runNumber == null || runNumber.length() == 0 || zz > 0
						|| (!runNumber.toUpperCase().equals("Y") && !runNumber.toUpperCase().equals("N"))) {
					MessageDialog.openInformation(getSite().getShell(), "Input Y/N for symmetric?", "Type Y/N");
					return;
				}
				sym = "Y".equals(runNumber.toUpperCase());
				//

				InputDialog inputMinus1Dialog = new InputDialog(getSite().getShell(), "输入Type4是否需要减一处理", "减一处理", "N",
						null);
				inputMinus1Dialog.open();
				String strMinus1 = inputMinus1Dialog.getValue();
				int zzz = inputMinus1Dialog.getReturnCode();
				if (strMinus1 == null || strMinus1.length() == 0 || zzz > 0
						|| (!strMinus1.toUpperCase().equals("Y") && !strMinus1.toUpperCase().equals("N"))) {
					MessageDialog.openInformation(getSite().getShell(), "Input Y/N for Minus1?", "Type Y/N");
					return;
				}
				boolean minus1 = "Y".equals(strMinus1.toUpperCase());
				//
				transDataMatrix = MatrixPrepare.prepare4(originalDataMatrix, sym, minus1);
				// populate table columns
				displayTransMatrix();
				// calculation
				thisResult = MatrixCalculation.computeResults(transDataMatrix, 4, sym);
				//
				resultTableViewer.setInput(thisResult);
				//
				tabFolder.setSelection(resultTab);
			}
		});
		btnTrans5.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				InputDialog inputRunDialog = new InputDialog(getSite().getShell(), "输入Type5的Run", "Run Number", "1",
						null);
				inputRunDialog.open();
				String runNumber = inputRunDialog.getValue();
				int zz = inputRunDialog.getReturnCode();
				if (runNumber == null || runNumber.length() == 0 || zz > 0) {
					MessageDialog.openInformation(getSite().getShell(), "Input Run Pls", "Run: 1-3");
					return;
				}
				int run = 0;
				try {
					run = Integer.parseInt(runNumber);
					if (run < 1 || run > 3)
						throw new NumberFormatException();
				} catch (NumberFormatException ee) {
					MessageDialog.openInformation(getSite().getShell(), "Input Run Pls", "Run: 1-3");
					return;
				}
				transDataMatrix = MatrixPrepare.prepare5(run, originalDataMatrix);
				// populate table columns
				displayTransMatrix();
				// calculation
				thisResult = MatrixCalculation.computeResults(transDataMatrix, 5, true);
				//
				resultTableViewer.setInput(thisResult);
				//
				tabFolder.setSelection(resultTab);

			}
		});
		btnTrans6.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				transDataMatrix = MatrixPrepare.prepare6(originalDataMatrix);
				// populate table columns
				displayTransMatrix();
				// calculation
				thisResult = MatrixCalculation.computeResults(transDataMatrix, 6, false);
				//
				resultTableViewer.setInput(thisResult);
				//
				tabFolder.setSelection(resultTab);
			}
		});
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}

	public void populateOriginal(String fileName) {
		try {
			for (TableViewerColumn eachColumn : originalColumns) {
				eachColumn.getColumn().dispose();
			}
			originalColumns.clear();
			originalDataMatrix = Matrix.load(fileName);
			if (originalDataMatrix.col + originalDataMatrix.row > 0) {
				// populate table columns
				for (int c = 0; c < originalDataMatrix.col; c++) {
					TableViewerColumn thisTVCol = new TableViewerColumn(originalTableViewer, SWT.NONE);
					TableColumn thisCol = thisTVCol.getColumn();
					thisCol.setWidth(75);
					thisCol.setText(String.valueOf(c));
					originalColumns.add(thisTVCol);
				}
				//
				originalTableViewer.setLabelProvider(originalMatrixLP);
				// set input
				originalTableViewer.setInput(originalDataMatrix);
				//
				originalTableViewer.setContentProvider(originalMatrixCP);
				//
				tabFolder.setSelection(originalTab);
			}
		} catch (Exception e) {
			MessageBox dialog = new MessageBox(getSite().getShell(), SWT.ICON_ERROR | SWT.OK);
			dialog.setText("文件打开失败");
			dialog.setMessage(e.getMessage());
			dialog.open();
		}
	}

	public void exportTransformed(String fileName) {
		try {
			Matrix.export(transDataMatrix, fileName);
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox dialog = new MessageBox(getSite().getShell(), SWT.ICON_ERROR | SWT.OK);
			dialog.setText("文件导出失败");
			dialog.setMessage("信息" + e.getMessage());
			dialog.open();
		}
	}

	private void displayTransMatrix() {
		// populate table columns
		for (TableViewerColumn eachColumn : transColumns) {
			eachColumn.getColumn().dispose();
		}
		transColumns.clear();
		//
		for (int c = 0; c < transDataMatrix.col; c++) {
			TableViewerColumn thisTVCol = new TableViewerColumn(transTableViewer, SWT.NONE);
			TableColumn thisCol = thisTVCol.getColumn();
			thisCol.setWidth(75);
			thisCol.setText(String.valueOf(c));
			transColumns.add(thisTVCol);
		}
		transTableViewer.setLabelProvider(transMatrixLP);
		// set input
		transTableViewer.setInput(transDataMatrix);
		//
		transTableViewer.setContentProvider(transMatrixCP);
	}
}