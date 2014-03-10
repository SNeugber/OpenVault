import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * 
 * @author Samuel Neugber
 *
 * Label object: holds the shape, colour and name of an image label
 *
 */
public class Label implements Serializable {
	
	private String name;				// Name of the label
	private String imgHash;				// Image Hash which the label belongs to
	private Color bgColor;				// Background color for the label 
	private Color lineColor;			// Color for the lines around the label
	private ArrayList<Point> points;	// Set of cornerpoints for the label
	private int maxPoints;				// Maximum number of corner points per label
	private boolean closed;				// True if the label has been finished (i.e. the first & last points are connected)
	

	
	public Label(String name, String imgHash, Color bgColor, Color lineColor, int maxPoints) {
		this.name = name;
		this.imgHash = imgHash;
		this.bgColor = bgColor;
		this.lineColor = lineColor;
		this.maxPoints = maxPoints;
		this.closed = false;
		
		points = new ArrayList<Point>(maxPoints);	
	}
	
	
	public boolean isEmpty() {
		return points.size() == 0;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getPointCount() {
		return points.size();		
	}
	
	public Point getStartingPoint() {
		if(points.size() > 0) return points.get(0);
		else return null;
	}
	
	public Point[] getPoints() {
		Point[] out = new Point[points.size()];
		for(int i = 0; i < out.length; i++) {
			out[i] = points.get(i);			
		}
		return out;
	}
	
	public void addPoint(Point p) {
		if(!points.contains(p) && points.size() < maxPoints) points.add(p);
	}
	
	public void removePoint(int index) {
		points.remove(index);
	}
	
	public void removeLastPoint() {
		if(points.size() > 0) points.remove(points.size()-1);
	}
	
	public Point getLastPoint(){
		if(points.size() > 0) return points.get(points.size()-1);
		

		return null;
	}
	
	public int hasPoint(Point p) {
		return points.indexOf(p);	
	}
	
	public void setPointAtIndex(int index, int x, int y) {
		points.get(index).x = x;
		points.get(index).y = y;
	}
	
	public void removeAllPoints(){
		points.clear();
	}
	
	public void setImageReference(String imgMD5Sum){
		this.imgHash = imgMD5Sum;
	}
	
	public boolean isFinished() {
		return closed;
	}
	
	public void finishLabel() {
		closed = true;
	}
	
	public void unFinishLabel() {
		closed = false;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean hasImageReference() {
		if(this.imgHash != "") return true;
		return false;
	}
	
	public Color getBgColor(){
		return bgColor;
	}
	
	public void setBgColor(Color color){
		 bgColor = color;
	}


	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}


	public Color getLineColor() {
		return lineColor;
	}
}
