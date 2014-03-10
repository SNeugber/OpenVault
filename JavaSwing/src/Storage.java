import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * 
 * @author Samuel Neugber
 * 
 * Manages loading/storing image labels
 *
 */
public class Storage {
	static FileOutputStream f_out;
	FileInputStream f_in;
	static ObjectOutputStream obj_out;
	ObjectInputStream obj_in;

	public Storage() {

	}

	public void saveLabels(ArrayList<Label> labels, String imgHash) {
		try {
			f_out = new FileOutputStream(imgHash + ".data");
			obj_out = new ObjectOutputStream(f_out);
			obj_out.writeObject(labels);
			obj_out.close();
			f_out.close();
		} catch (FileNotFoundException e) {
			System.out.println("Could not create save file");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Could not write to save file");
			e.printStackTrace();
		}
	}

	public ArrayList<Label> loadLabels(String imgHash) {
		// Read from disk using FileInputStream
		try {
			f_in = new FileInputStream(imgHash + ".data");
			obj_in = new ObjectInputStream(f_in);
			Object obj = obj_in.readObject();
			ArrayList<Label> output = (ArrayList<Label>) obj;
			return output;
		} catch (FileNotFoundException e) {
			System.out.println("Could not find a save file for image: " + imgHash);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Found, but could not read save file for image: " + imgHash);
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;	
	}
	
	public boolean hasSaveFile (String imgHash){
		File file = new File(imgHash + ".data");
		if(file.exists()){
			return true;
		}
		return false;
	}
}

//import org.eclipse.swt.widgets.Dialog;
//import org.eclipse.swt.widgets.Display;
//import org.eclipse.swt.widgets.Shell;
//import org.eclipse.swt.SWT;
//
//
//public class OpenFilePopup extends Dialog {
//
//	protected Object result;
//	protected Shell shell;
//
//	/**
//	 * Create the dialog.
//	 * @param parent
//	 * @param style
//	 */
//	public OpenFilePopup(Shell parent, int style) {
//		super(parent, style);
//		setText("SWT Dialog");
//	}
//
//	/**
//	 * Open the dialog.
//	 * @return the result
//	 */
//	public Object open() {
//		createContents();
//		shell.open();
//		shell.layout();
//		Display display = getParent().getDisplay();
//		while (!shell.isDisposed()) {
//			if (!display.readAndDispatch()) {
//				display.sleep();
//			}
//		}
//		return result;
//	}
//
//	/**
//	 * Create contents of the dialog.
//	 */
//	private void createContents() {
//		shell = new Shell(getParent(), SWT.DIALOG_TRIM);
//		shell.setSize(450, 300);
//		shell.setText(getText());
//
//	}
//
//}
