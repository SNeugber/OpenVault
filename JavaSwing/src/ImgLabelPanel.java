import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * 
 * @author Samuel Neugber
 * 
 * Displays the image and handles user input for tagging
 *
 */
public class ImgLabelPanel extends JPanel implements MouseMotionListener {

	// Image related vars
	private String imgFilePath; // File path of the current image which is
	// displayed
	private String imgName; // File name of the current image which is displayed
	private BufferedImage image; // Current image
	private String md5Sum; // md5-hash of the image

	// Label related vars
	private Label currentLabel; // Current label the user is working on
	private ArrayList<Label> imgLabels; // All labels associated to the current
	// image
	private int mouseSelectedCornerPoint; // Which of the corner points of the
	// current label is selected (if at
	// all)
	private Label mouseSelectedLabel; // Which of the label is currently
	// underneath the mouse
	private static final int cornerSize = 10; // Area around each corner point
	// where it should be selectable
	private static final int maxCorners = 100; // Max number of corners/label
	private Random random; // Used for random color regions
	private GeneralPath shape; // Used to fill the selected region of a label
	Storage labelStorage;
	private ArrayList<Point> redoList;

	// TreeView related vars
	TreeViewPanel tvp;

	// Option Related variables
	private boolean autoSaveEnabled;
	private boolean showLabelNames;
	private boolean tagMode;
	private boolean imageOpened;

	private Color selectLineColor;
	private Color otherLineColor;
	
	private final Color currentLabelLineColor = Color.GREEN;
	private final Color defaultLineColor = Color.BLUE;

	/**
	 * Constructor
	 */
	public ImgLabelPanel() {

		// Init vars
		random = new Random();
		labelStorage = new Storage();
		imgFilePath = "";

		selectLineColor = Color.GREEN;
		otherLineColor= Color.blue;

		autoSaveEnabled = false;
		showLabelNames = false;
		tagMode = false;
		imageOpened = false;

		imgLabels = new ArrayList<Label>();
		redoList = new ArrayList<Point>();
		currentLabel = new Label("", "", new Color(random.nextInt(256), random
				.nextInt(256), random.nextInt(256), 60), defaultLineColor, maxCorners);
		initMouseControl();
	}

	public void setTreeViewPanelReference(TreeViewPanel tvp) {
		this.tvp = tvp;
	}

	/* IMAGE RELATED FUNCTIONS */

	/**
	 * Set the fileName of the image
	 * 
	 * @param fileName
	 */
	public void setFileName(String fileName) {
		File temp = new File(fileName);
		if (temp.exists()) {

			this.imgFilePath = fileName;
			this.imgName = temp.getName();

			// Default label
			newLabel();

		} else {
			this.imgFilePath = "";
			this.imgName = "";
		}
	}

	public String getFileName() {
		return this.imgFilePath;
	}

	/**
	 * Computes the md5 hash of the currently loaded image and stores it in
	 * md5Sum
	 * 
	 * @throws Exception
	 */
	public void computeImageHash() {
		byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer())
		.getData();
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Could not compute md5");
			this.md5Sum = "";
			e.printStackTrace();
			return;
		}
		md.update(data);
		byte[] hash = md.digest();
		String md5Sum = "";
		for (int i = 0; i < hash.length; i++) { // for loop ID:1
			md5Sum += Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(
					1);
		}
		this.md5Sum = md5Sum;
	}

	/**
	 * Call this from wherever to load a new image & display it Requires file
	 * path to have been set using setFileName fileName is used to add a note in
	 * Tree
	 */
	public void loadAndDisplayImage(String fileName) {
		if (imgFilePath == "")
			return;
		File file = new File(this.imgFilePath);
		try {
			image = ImageIO.read(file); // Read image
			computeImageHash(); // Generate md5
			loadLabels(); // Attempt to load labels
			imageOpened = true;
			addLabelsToTreeView();

		} catch (IOException e) {
			System.err.println("Could not read image");
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Could not generate md5 checksum");
			e.printStackTrace();
		}

		ImageLabeller.enableButtons();
		repaint();
	}

	/* LABELLING RELATED FUNCTIONS */
	/**
	 * Add all current labels (if there are any) to a tree view panel
	 */
	public void addLabelsToTreeView() {
		tvp.clearLabelsForImage(this.imgName);
		if (imgLabels != null && imgLabels.size() > 0)
			tvp.addLabelNode(this.imgName, imgLabels);
	}

	/**
	 * Removes all labels of the picture.
	 */
	public void removeLabels() {
		imgLabels = new ArrayList<Label>();
		repaint();
	}

	/**
	 * Attempts to load the labels from the save file Doesn't load anything if
	 * no save file exists
	 */
	public void loadLabels() {
		if (labelStorage.hasSaveFile(this.md5Sum)) {
			imgLabels = labelStorage.loadLabels(this.md5Sum);
			if (imgLabels.size() > 0)
				switchCurrentLabel(imgLabels.get(0));
		} else {
			imgLabels = new ArrayList<Label>();
			newLabel();
		}
		repaint();
	}

	/**
	 * Attempts to write all labels to a file, named <image md5 hash>.data
	 */
	public void saveLabels() {
		if (imgLabels.size() > 0)
			System.out.println("Saving");
		labelStorage.saveLabels(imgLabels, this.md5Sum);
	}

	/**
	 * a function to show the names of defined labels
	 */
	public void displayLabelNames(boolean input) {
		showLabelNames = input;
		repaint();
	}

	/**
	 * Add a point to the currently selected label
	 * 
	 * @param x
	 * @param y
	 */
	public void addPointToCurrentLabel(int x, int y) {
		if (currentLabel.getPointCount() < maxCorners) {
			currentLabel.addPoint(new Point(x, y));
			repaint();
		}
	}

	/**
	 * Remove the last point which was added
	 */
	public void removeLast() {
		if(currentLabel.getLastPoint() == null) return;
		redoList.add(currentLabel.getLastPoint());
		currentLabel.removeLastPoint();

		System.out.println("undo");
		repaint();
	}

	public void redo() {
		if (redoList.size() > 0) {
			currentLabel.addPoint(redoList.remove(redoList.size() - 1));
			repaint();
		}
	}

	/**
	 * This clears all points of the current label the user is working on.
	 */
	public void clear() {
		currentLabel.removeAllPoints();
		repaint();
	}

	/**
	 * Adds a new label to the current list of labels. If the last label has not
	 * been finished it will be deleted.
	 */
	public void newLabel() {
		if (!currentLabel.isFinished())
			imgLabels.remove(currentLabel);
		Label newLabel = new Label(imgName, md5Sum, new Color(random
				.nextInt(256), random.nextInt(256), random.nextInt(256), 60), defaultLineColor,
				maxCorners);
		imgLabels.add(newLabel);
		currentLabel = newLabel;
		redoList = new ArrayList<Point>();
	}

	/**
	 * Used to update the GUI. Actions: - Draws all labels, their cornerpoints &
	 * lines around them
	 */
	public void paint(Graphics g) {

		// We have an image -> draw it
		if (!imgFilePath.equals("")) {

			Graphics2D g2d = (Graphics2D) g;
			Dimension d = getSize();
			g2d.clearRect(0, 0, d.width, d.height);
			g2d.drawImage(image, 0, 0, d.width, d.height, this);

			// Iterate over each label we have, draw all cornerpoints as
			// rectangles, draw lines between them
			for (Label l : imgLabels) {
				Point[] points = l.getPoints();

				if (l == currentLabel) {
					if(l.getLineColor().equals(defaultLineColor)) {
						drawCornerPoints(g2d, points, currentLabelLineColor);
						drawLines(g2d, points, currentLabelLineColor, l.isFinished());
					} else {
						drawCornerPoints(g2d, points, currentLabelLineColor);
						drawLines(g2d, points, l.getLineColor(), l.isFinished());
					}
				} else {
					drawCornerPoints(g2d, points, l.getLineColor());
					drawLines(g2d, points, l.getLineColor(), l.isFinished());
				}

				if (l.isFinished()) {
					fillShape(l, g2d, points);
					if (showLabelNames == true) {
						drawLabelName(g2d, l);
					}
				}

			}
		} else { // No image -> Just gray background
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}

	/**
	 * Draws lines between all points in an array
	 * 
	 * @param g2d
	 * @param points
	 */
	private void drawLines(Graphics2D g2d, Point[] points, Color drawColor,
			boolean closed) {
		g2d.setColor(drawColor);
		// Draws the lines from the start point to the last point
		for (int i = 0; i < points.length - 1; i++) {
			g2d.drawLine(points[i].x, points[i].y, points[i + 1].x,
					points[i + 1].y);
		}

		// Draws the line from the last point back to the start point
		if (points.length > 2 && closed) {
			g2d.drawLine(points[points.length - 1].x,
					points[points.length - 1].y, points[0].x, points[0].y);
		}

	}

	private void drawLabelName(Graphics2D gr, Label inputLB) {
		if (inputLB.getPointCount() > 0) {
			int sumX = 0;
			int sumY = 0;
			Point[] points = inputLB.getPoints();
			for (Point point : points) {
				sumX = sumX + point.x;
				sumY = sumY + point.y;
			}

			Font f = new Font("Arial", Font.BOLD | Font.ITALIC, 14);
			gr.setColor(new Color(
					255-inputLB.getBgColor().getRed(),
					255-inputLB.getBgColor().getGreen(),
					255-inputLB.getBgColor().getBlue()));

			gr.setFont(f);

			int stringLength = (int)gr.getFontMetrics().getStringBounds(inputLB.getName(), gr).getWidth();
			int start = (sumX / inputLB.getPointCount()) - stringLength/2;
			gr.drawString(inputLB.getName(), start,
					sumY / inputLB.getPointCount());
		}

	}

	/**
	 * Draws a square around each corner point to indicate where they can be
	 * moved
	 * 
	 * @param g2d
	 * @param points
	 */
	private void drawCornerPoints(Graphics2D g2d, Point[] points,
			Color colorToDraw) {
		g2d.setColor(colorToDraw);
		for (Point p : points) {
			g2d.fillOval(p.x - (cornerSize / 2), p.y - (cornerSize / 2),
					cornerSize, cornerSize);
		}
	}

	/**
	 * Uses a set of points as corners to an arbitrary shape. This shape is then
	 * filled with some translucent color.
	 * 
	 * @param g2d
	 * @param points
	 */
	private void fillShape(Label label, Graphics2D g2d, Point[] points) {

		if (points.length == 0)
			return;
		// checking if color has not been set

		g2d.setColor(label.getBgColor());

		// Init shape
		shape = new GeneralPath();

		// Setup first point
		shape.moveTo(points[0].x, points[0].y);

		// Connect points
		for (int i = 1; i < points.length; i++) {
			shape.lineTo(points[i].x, points[i].y);
		}
		shape.lineTo(points[0].x, points[0].y);

		// Finish shape
		shape.closePath(); // close the shape

		// draw filled shape
		g2d.fill(shape);
	}

	/**
	 * Opens a popup to name the label. If a name is entered the label is seen
	 * as "finished".
	 */
	private void openLabelNamePopup() {
		String response = JOptionPane.showInputDialog(null,
				"Enter a name for the area", "Finish Label",
				JOptionPane.QUESTION_MESSAGE);
		/*
		 * If the user has entered a name, pass the md5Sum of the image to the
		 * label and tag the label as finished.
		 */
		if (response != null) {
			for (Label l : imgLabels) {
				if (l.getName().equals(response)) {
					JOptionPane.showMessageDialog(null,
					"Please enter a unique name for this label");
					openLabelNamePopup();
					return;
				}
			}
			currentLabel.setImageReference(md5Sum);
			currentLabel.setName(response);
			currentLabel.finishLabel();
			addLabelsToTreeView();
		}
	}

	/**
	 * This function checks if the point which is passed as a parameter is close
	 * to one of the cornerpoints of the current label the user is working with.
	 * 
	 * @param x
	 * @param y
	 * @return If a cornerpoint is close enough: The index of that point Else:
	 *         -1
	 */
	private void inCornerPoint(int x, int y) {

		// First: check the current label as this has priority
		Point[] points = currentLabel.getPoints();
		Point clickPoint = new Point(x, y);

		for (int i = 0; i < points.length; i++) {
			if (clickPoint.distance(points[i]) < 10) {
				mouseSelectedLabel = currentLabel;
				mouseSelectedCornerPoint = i;
				return;
			}
		}

		// If we did not select a point in the current label, check all other
		// labels
		for (Label l : imgLabels) {
			points = l.getPoints();

			for (int i = 0; i < points.length; i++) {
				if (clickPoint.distance(points[i]) < 10) {
					mouseSelectedLabel = l;
					mouseSelectedCornerPoint = i;
					return;
				}
			}
		}

		mouseSelectedCornerPoint = -1;
	}

	public void setCurrentLabel(String labelName) {
		if (currentLabel.getName().equals(labelName))
			return;

		for (Label l : imgLabels) {
			if (l.getName().equals(labelName)) {
				switchCurrentLabel(l);
				break;
			}
		}
		repaint();
	}

	public void setAutoSave(boolean input) {
		if (input == true) {
			autoSaveEnabled = true;
			System.out.println("autosave enabled");
			saveLabels();
		} else {
			autoSaveEnabled = false;
			System.out.println("autosave disabled");
		}
	}

	private void switchCurrentLabel(Label newLabel) {
		redoList = new ArrayList<Point>();
		currentLabel = newLabel;
	}

	/* MOUSE INPUT RELATED CODE */
	private void initMouseControl() {
		addMouseListener(new MouseAdapter() {

			/**
			 * If we get a button press we want to check whether the user wants
			 * to create a cornerpoint or if he wants to finish the label by
			 * clicking the first cornerpoint
			 */
			public void mousePressed(MouseEvent event) {
				int x = event.getX();
				int y = event.getY();
				inCornerPoint(x, y);
				if (mouseSelectedCornerPoint > -1) {
					if (!mouseSelectedLabel.equals(currentLabel)) {
						if (!currentLabel.isFinished()) {
							imgLabels.remove(currentLabel);
						}
						switchCurrentLabel(mouseSelectedLabel);
						//currentLabel = mouseSelectedLabel;
					}
				}
				/*
				 * If we are not inside a square and and the current label
				 * is not finished, add another point
				 * 
				 * Else If we are inside a square, it is the FIRST
				 * cornerpoint AND the label has at least 3 corners, open
				 * the popup to name the label.
				 */
				if(tagMode) {
					if (mouseSelectedCornerPoint == -1) { // not inside a
						// square
						if (currentLabel.isFinished())
							newLabel();
						currentLabel.addPoint(new Point(x, y));
					} else if (mouseSelectedCornerPoint == 0
							&& mouseSelectedLabel.equals(currentLabel)
							&& !currentLabel.isFinished()
							&& currentLabel.getPointCount() > 2) {
						openLabelNamePopup();
					}
					if (autoSaveEnabled == true) {
						saveLabels();
					}
				}
				repaint();
			}

			/**
			 * Reset the currently selected cornerpoint in mouse release
			 */
			public void mouseReleased(MouseEvent event) {
				mouseSelectedCornerPoint = -1;
			}

			/**
			 * This function is used to check if we have doubleclicked on a
			 * cornerpoint to delete it if this is the case.
			 */
			public void mouseClicked(MouseEvent evt) {
				int x = evt.getX();
				int y = evt.getY();
				if (tagMode == true) {
					inCornerPoint(x, y);

					if (evt.getButton() == MouseEvent.BUTTON3) {
						if (mouseSelectedCornerPoint > -1) {
							currentLabel.removePoint(mouseSelectedCornerPoint);
							if (currentLabel.isEmpty()) {
								imgLabels.remove(currentLabel);
								newLabel();
								if (autoSaveEnabled == true) {
									saveLabels();
								}

							}
							repaint();
						}
					}
				}

			}
		});
		addMouseMotionListener(this);
		mouseSelectedCornerPoint = -1;
	}

	/**
	 * If the mouse is hovering over one of the cornerpoints -> change cursor
	 */
	public void mouseMoved(MouseEvent evt) {
		int x = evt.getX();
		int y = evt.getY();

		inCornerPoint(x, y);

		if (mouseSelectedCornerPoint > -1)
			setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		else if (getTagMode()) {
			setTagCursor();
		} else
			setCursor(Cursor.getDefaultCursor());

	}

	/**
	 * If the mouse is dragged (mouse1 button held down + movement), check if we
	 * want to reposition a cornerpoint
	 */
	public void mouseDragged(MouseEvent evt) {
		int x = evt.getX();
		int y = evt.getY();

		if (mouseSelectedCornerPoint > -1) {
			currentLabel.setPointAtIndex(mouseSelectedCornerPoint, x, y);
			if (autoSaveEnabled == true) {
				saveLabels();
			}
			repaint();
		}
	}

	public void setTagMode(boolean input) {
		tagMode = input;
		if (tagMode == true) {
			/**
			 * Change cursor
			 */
			setTagCursor();

		}
	}

	private void setTagCursor() {
		if (imageOpened == true){
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Image imagesc = toolkit.getImage(this.getClass().getClassLoader().getResource("tagMouse.png"));

			Cursor cursor = toolkit.createCustomCursor(imagesc, new Point(5, 5),
			"Tag");
			setCursor(cursor);
		}
	}

	public boolean getTagMode() {
		return tagMode;
	}

//	public void setSelectLineColor(Color color){
//		selectLineColor = color;
//		repaint();
//	}
//
//	public void setOtherLineColor(Color color){
//		otherLineColor = color;
//		repaint();
//	}

	public void setCurrentLabelLineColor(Color c) {
		if(currentLabel == null) return;
		currentLabel.setLineColor(c);
		repaint();
	}
	
	public void setCurrentLabelBGColor(Color c) {
		if(currentLabel == null) return;
		currentLabel.setBgColor(new Color(c.getRed(),c.getGreen(),c.getBlue(),60));
		repaint();
	}
}
