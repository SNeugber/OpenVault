// 
// Dear SDP 2012 student,
// 
// Once you are done trying to 'optimize' this class,
// and have realized what a terrible mistake that was,
// please increment the following counter as a warning
// to the next guy:
// 
// total_hours_wasted_here = 29
// 

package Vision;

import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

import Planning.Main;
import Shared.ComponentPoint;
import Shared.Tools;

/** The vision class responsible for doing the bulk of the image processing
 */
public class StandardImageProcessor extends ImageProcessor {

	public static int DEBUG_LEVEL = 2;

	public static int backgrndThresh = 75;
	public static int searchdistance = 200;

	// --- Barrel Distortion Correction
	private static final double barrelCorrectionX = -0.016;
	private static final double barrelCorrectionY = -0.06;
	public static boolean useBarrelDistortion = false;

	// --- Coordinates for ball and teams
	Point lastBallPos = new Point(-1, -1);
	Point btPos = new Point(-1, -1);
	Point ytPos = new Point(-1, -1);
	Point ourPos = new Point(-1, -1);

	// --- Arrays for blue and yellow pixels for detecting the T's
	ArrayList<Point> bluePixels;
	ArrayList<Point> yellowPixels;

	// --- Initialise the background image (if none can be found this will throw IOException)
	static Raster background = getBackground().getData();

	public boolean backgroundSubtraction(int[] image, int[] bg){
		return (Colours.getColourDifference(image, bg) > backgrndThresh ? true : false); 
	}

	public BufferedImage process(BufferedImage image) {

		bluePixels = new ArrayList<Point>();
		yellowPixels = new ArrayList<Point>();

		if (Main.weAreBlueTeam)
			ourPos = btPos;
		else
			ourPos = ytPos;

		// Create raster from given image
		Raster data = null;
		try {
			data = image.getData();
		} catch (NullPointerException e) {
			System.out.println(e.toString());
			return null;
		}

		Point ballPos = new Point(-1, -1);

		// Create a new blank raster of the same size
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		ColorModel cm = new ComponentColorModel(cs, 
				false, 
				false,
				Transparency.OPAQUE, 
				DataBuffer.TYPE_BYTE);
		WritableRaster wraster = data.createCompatibleWritableRaster();

		// This will be used to find red pixel (one!)
		int closestRedFound = 255 * 3;

		// This will be used to find blue robot centroid
		int btCentroidCount = 0;
		Point btCentroid = new Point(0, 0);

		// This for yellow
		int ytCentroidCount = 0;
		Point ytCentroid = new Point(0, 0);

		for (int i = xlowerlimit; i < xupperlimit; i = i + 1) {
			for (int j = ylowerlimit; j < yupperlimit; j = j + 1) {
				
				// Get RGB values for a pixel
				int[] pixelColour = new int[3];
				data.getPixel(i, j, pixelColour);

				int[] backgroundColour = new int[3];
				background.getPixel(i, j, backgroundColour);

				// Barrel distortion
				Point out = convertToBarrelCorrected(new Point(i, j));

				if (DEBUG_LEVEL < 4)
					drawPixel(wraster, out, pixelColour);

				if (!backgroundSubtraction(pixelColour, backgroundColour)) {
					continue;
				}

				if (DEBUG_LEVEL < 5)
					drawPixel(wraster, out, pixelColour);


				// Finds how red a pixel is
				int ballDifference = Colours.getColourDifference(Colours.red, pixelColour);


				// This will try to find the "reddest" point. This works better
				// for the ball than centroid.
				if (ballDifference < closestRedFound && pixelColour[Colours.RED] > 150
						&& pixelColour[Colours.GREEN] < 150) {
					closestRedFound = ballDifference;
					ballPos = out;
				}

				// Check which colour the current pixel has
				if (Colours.isColourHSB(pixelColour, "blue")){
					if (DEBUG_LEVEL > 2)
						drawPixel(wraster, out, Colours.lightBlue);
					bluePixels.add(out);
				}
				else if (Colours.isColourHSB(pixelColour, "yellow")){
					if (DEBUG_LEVEL > 2)
						drawPixel(wraster, out, Colours.orange);
					yellowPixels.add(out);
				}
				else if (Colours.isColour(pixelColour, "green")) {
					if (DEBUG_LEVEL > 1)
						drawPixel(wraster, convertToBarrelCorrected(new Point(
								i, j)), new int[] { 0, 255, 0 });
				}
			}
		}

		// Get the blue "T" (hopefully)
		if(bluePixels.size() > 0) bluePixels = getLargestConnectedComponent(bluePixels,10);
		for(Point p : bluePixels) {
			btCentroidCount++;
			btCentroid.x += p.x;
			btCentroid.y += p.y;
			if (DEBUG_LEVEL > 1)
				drawPixel(wraster, p, Colours.blue);
		}

		// Get the yellow "T" (hopefully)
		if(yellowPixels.size() > 0) yellowPixels = getLargestConnectedComponent(yellowPixels,10);
		for(Point p : yellowPixels) {
			ytCentroidCount++;
			ytCentroid.x += p.x;
			ytCentroid.y += p.y; 
			if (DEBUG_LEVEL > 1)
				drawPixel(wraster, p, Colours.yell);
		}

		// Where 5 is just some minimal number of pixels found
		if (btCentroidCount > 5) {
			btPos = new Point(btCentroid.x / btCentroidCount, btCentroid.y
					/ btCentroidCount);
			//int btAngle = findAngle(data, wraster, btPos, blue);
			//int btRanAngle = Angles.findRansacAngle(bluePixels, wraster, btPos, Colours.blue, this);
			int btAngle = Angles.findRansacAngle(bluePixels, btPos, Colours.blue);
			//int btOldAngle = (int)(objectInfos.getBlueBot().getAngle() * 180 / Math.PI);
			//btRanAngle = Angles.smoothAngle(btOldAngle, btRanAngle);
			if(DEBUG_LEVEL > 1)
				drawOrientation(btPos, Math.toRadians(btAngle), wraster);
			objectInfos.updateBlueBot(btPos, btAngle);
		}

		if (ytCentroidCount > 5) {
			ytPos = new Point(ytCentroid.x / ytCentroidCount, ytCentroid.y
					/ ytCentroidCount);
			//int ytAngle = findAngle(data, wraster, ytPos, yell);
			//int ytRanAngle = Angles.findRansacAngle(yellowPixels, wraster, ytPos, Colours.yell, this);
			int ytAngle = Angles.findRansacAngle(yellowPixels, ytPos, Colours.yell);
			//int ytOldAngle = (int)(objectInfos.getYellowBot().getAngle() * 180 / Math.PI);
			//ytRanAngle = Angles.smoothAngle(ytOldAngle, ytRanAngle);
			if(DEBUG_LEVEL > 1)
				drawOrientation(ytPos, Math.toRadians(ytAngle), wraster);
			//GDebug.debug("Yellow Angle", ytRanAngle);
			objectInfos.updateYellowBot(ytPos, ytAngle);
		}

		if (useMouse) {
			Point mouse = MouseInfo.getPointerInfo().getLocation();
			ballPos = new Point(mouse.x - 5 - displX, mouse.y - 50 - displY);
			drawCross(wraster, ballPos, Colours.red);
			//System.out.println("Mouse: " + ballPos.x + ", " + ballPos.y);
		} else {
			findBall(wraster, ballPos);
		}

		objectInfos.updateBall(ballPos);

		// Draw box around walls of pitch
		if (DEBUG_LEVEL > 2) {
			Point topleft = new Point(Main.WALL_LEFT,Main.WALL_TOP);
			Point bottomright = new Point(Main.WALL_RIGHT,Main.WALL_BOTTOM);
			drawBox(wraster, topleft, bottomright, Colours.blue);
		}

		int lineColors = 0;
		if (DEBUG_LEVEL > 0)
			for (int i = 0; i < lines.size(); i += 2) {
				try {
					Color c = new Color(lineColor.get(lineColors));
					lineColors++;
					int color[] = new int[] { c.getRed(), c.getGreen(),
							c.getBlue() };
					try {
						drawLine(wraster, (Point) lines.get(i), (Point) lines
								.get(i + 1), color);
					} catch (NullPointerException e) {

					}
				} catch (IndexOutOfBoundsException e) {
					
				}
			}

		BufferedImage img = new BufferedImage(cm, wraster, false, null);
		return img;
	}

	public static BufferedImage getBackground() {

		BufferedImage img = null;
		String imgLocation;

		// Save to the correct location
		if (InitialSetup.usingPitch1 && InitialSetup.wallMounted)
			imgLocation = "data/backgroundP1WallMounted.jpg";
		else if (InitialSetup.usingPitch1 && !InitialSetup.wallMounted) 
			imgLocation = "data/backgroundP1.jpg";
		else if (!InitialSetup.usingPitch1 && InitialSetup.wallMounted) 
			imgLocation = "data/backgroundP2WallMounted.jpg";
		else
			imgLocation = "data/backgroundP2.jpg";

		try {
			img = ImageIO.read(new File(imgLocation));
		} catch (IOException e) {
			System.err.println("Couldn't find background image file at: "+imgLocation);
		}

		return img;
	}

	private void findBall(WritableRaster raster, Point ballPos) {
		if (ballPos != null) { // If we can see the ball
			if (lastBallPos.x == -1) {
				lastBallPos = ballPos;
			}

			double ballmovement = Tools.getDistanceBetweenPoint(ballPos,
					lastBallPos);

			// Ignores the movement when it is higher than expected or very
			// small
			if (ballmovement < searchdistance && ballmovement > 3) {
				lastBallPos = ballPos;
			} else {
				ballPos = lastBallPos;
			}

			if (DEBUG_LEVEL > 0)
				drawCross(raster, ballPos, Colours.red);
		} else { // If we can't see the ball
			if (lastBallPos.x != -1) {
				// Try last values
				ballPos = lastBallPos;

				if (DEBUG_LEVEL > 0)
					drawCross(raster, ballPos, Colours.red);
			}
		}
	}
	/**
	 * This function recursively assigns component-labels to pixels, given a seed p
	 *
	 * @param p   		 seed point which needs to have a valid component-label
	 * @param points    arraylist of all other points to be checked
	 */
	private void assignComponents(ComponentPoint p, ArrayList<ComponentPoint> points, int[] compCounters) {
		if(points.size() == 0) return; // Failsafe
		if(compCounters[p.getComponent()] > 350) return; // Break if we are trying to find a connected component within something larger than the T
		// Check the four points around the current point: +/-2x, +/-2y
		for(int j = -1; j < 2; j+=2) {
			for (int k = -1; k < 2; k+=2) {
				ComponentPoint q = new ComponentPoint(p.getPoint().x+j,p.getPoint().y+k);
				int index = points.indexOf(q);
				if(index != -1) { // Does this point even exist?
					if(!points.get(index).isConnected()) { // Check that the point is not already assigned to a component
						points.get(index).setComponent(p.getComponent());
						compCounters[p.getComponent()]++;
						assignComponents(points.get(index), points, compCounters);
					}
				}
			}
		}
	}

	/**
	 * This function gets the largest connected component from a set of points,
	 * by picking a random "seed"-point and recursively finding the connected points to it.
	 * The number of random seeds is given by numOfComps
	 *
	 * @param pixels   	 the arraylist of pixels
	 * @param numOfComps    the maximum number of seeds to be used
	 * @return
	 */
	private ArrayList<Point> getLargestConnectedComponent(ArrayList<Point> pixels, int numOfComps) {

		if(pixels.size() == 0) {
			return new ArrayList<Point>(); // Return empty array
		}

		// Make a new list of unconnected components, given the list of points
		ArrayList<ComponentPoint> compPoints = new ArrayList<ComponentPoint>();
		for(Point p : pixels) {
			compPoints.add(new ComponentPoint(p.x,p.y));
		}

		// Make an array with a counter entry for each component
		int[] compCounters = new int[numOfComps];

		// Make at most numOfComps seeds, and stop if we can't find any more unassigned ones
		int seed = 0;
		int comboBreaker = 0;
		while(seed < numOfComps && comboBreaker < 30) {
			ComponentPoint p = compPoints.get((int)(Math.random()*compPoints.size()));
			if(!p.isConnected()) {
				p.setComponent(seed);
				compCounters[seed]++;
				assignComponents(p, compPoints, compCounters);
				seed++;
			}
			else comboBreaker++;
		}

		// Get the largest connected component
		int max = 0;
		int maxPoint = 0;
		for(int i = 0; i < compCounters.length; i++) {
			if(compCounters[i] > max) {
				max = compCounters[i];
				maxPoint = i;
			}
		}

		// Produce an arraylist with the pixels for the largest connected component
		ArrayList<Point> out = new ArrayList<Point>();
		for(ComponentPoint p : compPoints) {
			if(p.getComponent() == maxPoint) {
				out.add(new Point(p.getPoint()));
			}
		}
		return out;
	}


	/**
	 * Barrel Distortion Correction 'straightens' the distorted image.
	 * But doesn't quite work properly. But it is not really needed anyway.
	 * 
	 * @param p1
	 *            point coordinates
	 * @return if useBarrelDistorion is false returns p1, otherwise, returns
	 *         adjusted coordinates
	 */
	public Point convertToBarrelCorrected(Point p1) {
		if (!useBarrelDistortion)
			return p1;
		// First normalise pixel
		double px = (2 * p1.x - width) / (double) width;
		double py = (2 * p1.y - height) / (double) height;

		// Then compute the radius of the pixel you are working with
		double rad = px * px + py * py;

		// Then compute new pixel'
		double px1 = px * (1 - barrelCorrectionX * rad);
		double py1 = py * (1 - barrelCorrectionY * rad);

		// Then convert back
		int pixi = (int) ((px1 + 1) * width / 2);
		int pixj = (int) ((py1 + 1) * height / 2);
		// System.out.println("New Pixel: (" + pixi + ", " + pixj + ")");
		return new Point(pixi, pixj);
	}

	public static Point barrelCorrected(Point p1) {
		// System.out.println("Pixel: (" + x + ", " + y + ")");
		// First normalise pixel
		double px = (2 * p1.x - width) / (double) width;
		double py = (2 * p1.y - height) / (double) height;

		// System.out.println("Norm Pixel: (" + px + ", " + py + ")");
		// Then compute the radius of the pixel you are working with
		double rad = px * px + py * py;

		// Then compute new pixel'
		double px1 = px * (1 - barrelCorrectionX * rad);
		double py1 = py * (1 - barrelCorrectionY * rad);

		// Then convert back
		int pixi = (int) ((px1 + 1) * width / 2);
		int pixj = (int) ((py1 + 1) * height / 2);
		// System.out.println("New Pixel: (" + pixi + ", " + pixj + ")");
		return new Point(pixi, pixj);
	}

	/**
	 * Draw line from p1 to p2 of specified colour on given raster.
	 * 
	 * @param raster
	 *            draw on writable raster
	 * @param p1
	 *            start point
	 * @param p2
	 *            end point
	 * @param colour
	 *            - colour as three integers
	 */
	public void drawLine(WritableRaster raster, Point p1, Point p2, int[] colour) {
		int x0 = p1.x;
		int y0 = p1.y;
		int x1 = p2.x;
		int y1 = p2.y;
		int dx = x1 - x0;
		int dy = y1 - y0;
		drawPixel(raster, new Point(x0, y0), colour);
		if (dx != 0) {
			float m = (float) dy / (float) dx;
			float b = y0 - m * x0;
			dx = (x1 > x0) ? 1 : -1;
			while (x0 != x1) {
				x0 += dx;
				y0 = Math.round(m * x0 + b);
				drawPixel(raster, new Point(x0, y0), colour);
			}
		}
	}

	public void addLineToBeDrawn(Point p1, Point p2, int color) {
		lines.add(p1);
		lines.add(p2);
		lineColor.add(color);
	}

	public static void setBackgrndThresh(int backgrndThresh) {
		StandardImageProcessor.backgrndThresh = backgrndThresh;
	}
	

	private void drawOrientation(Point roboPos, double angle, WritableRaster r) {
		double dy = -Math.sin(angle);
		double dx = Math.cos(angle);
		Point p = new Point((int)(roboPos.x + (dx * 50)),(int)(roboPos.y + (dy * 50)));
		drawLine(r,roboPos,p,Colours.red);
		drawLine(r,new Point(roboPos.x+1,roboPos.y-1),new Point(p.x+1,p.y-1),Colours.red);
		drawLine(r,new Point(roboPos.x-1,roboPos.y-1),new Point(p.x-1,p.y-1),Colours.red);
		drawLine(r,new Point(roboPos.x+1,roboPos.y+1),new Point(p.x+1,p.y+1),Colours.red);
		drawLine(r,new Point(roboPos.x-1,roboPos.y+1),new Point(p.x-1,p.y+1),Colours.red);
	}
}