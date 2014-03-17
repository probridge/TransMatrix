package transmatrix;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class ResultLabelProvider implements ITableLabelProvider {

	@Override
	public void addListener(ILabelProviderListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public Image getColumnImage(Object arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnText(Object rawData, int index) {
		String label = "N/A";
		if (rawData instanceof MatrixCalculationResult) {
			MatrixCalculationResult thisResult = (MatrixCalculationResult) rawData;
			switch (index) {
			case 0:
				label = String.valueOf(thisResult.n);
				break;
			case 1:
				label = Matrix.formatDouble(thisResult.outDC);
				break;
			case 2:
				label = Matrix.formatDouble(thisResult.inDC);
				break;
			case 3:
				label = Matrix.formatDouble(thisResult.N_outDC);
				break;
			case 4:
				label = Matrix.formatDouble(thisResult.N_inDC);
				break;
			case 5:
				label = Matrix.formatDouble(thisResult.between);
				break;
			case 6:
				label = Matrix.formatDouble(thisResult.N_between);
				break;
			case 7:
				label = Matrix.formatDouble(thisResult.N_outClose);
				break;
			case 8:
				label = Matrix.formatDouble(thisResult.N_inClose);
				break;
			case 9:
				label = Matrix.formatDouble(thisResult.outClose2);
				break;
			case 10:
				label = Matrix.formatDouble(thisResult.inClose2);
				break;
			case 11:
				label = Matrix.formatDouble(thisResult.outEfficiency);
				break;
			case 12:
				label = Matrix.formatDouble(thisResult.inEfficiency);
				break;
			case 13:
				label = Matrix.formatDouble(thisResult.outContraint);
				break;
			case 14:
				label = Matrix.formatDouble(thisResult.inContraint);
				break;
			case 15:
				label = Matrix.formatDouble(thisResult.egoDensity);
				break;
			case 16:
				label = Matrix.formatDouble(thisResult.nonRedundancy);
				break;
			case 17:
				label = Matrix.formatDouble(thisResult.density);
				break;
			case 18:
				label = String.valueOf(thisResult.N);
				break;
			case 19:
				label = Matrix.formatDouble(thisResult.inStrength);
				break;
			case 20:
				label = Matrix.formatDouble(thisResult.outStrength);
				break;
			case 21:
				label = Matrix.formatDouble(thisResult.inOriginalStrength);
				break;
			case 22:
				label = Matrix.formatDouble(thisResult.outOriginalStrength);
				break;
			case 23:
				label = Matrix.formatDouble(thisResult.distribution);
				break;
			case 24:
				label = Matrix.formatDouble(thisResult.cluster);
				break;
			default:
				break;
			}
		}
		return label;
	}
}
