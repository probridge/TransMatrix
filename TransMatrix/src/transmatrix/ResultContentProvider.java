package transmatrix;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ResultContentProvider implements IStructuredContentProvider {

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] getElements(Object input) {
		MatrixCalculationResult[] results = (MatrixCalculationResult[]) input;
		if (results != null)
			return (MatrixCalculationResult[]) input;
		else
			return new MatrixCalculationResult[] {};
	}
}
