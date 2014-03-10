import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * 
 * @author Samuel
 *
 * Makes an expandable tree of clickable elements.
 * 
 * Used for creating a hierarchy of images and their associated labels
 * 
 */

public class TreeViewPanel extends JPanel {
	
	private JTree tree;
	private JScrollPane scrollPane;
	DefaultMutableTreeNode nNode;
	DefaultMutableTreeNode imagesNode;
	private DefaultTreeModel model;
	
	private ImgLabelPanel ilp;
	
	public static TreePath path;
	public static MutableTreeNode node;
	
	private HashMap<String, String> images;
	
	public TreeViewPanel(){
		
		buildTree();
		
		scrollPane = new JScrollPane();
		scrollPane.getViewport().add( tree );
		add( scrollPane, BorderLayout.CENTER );
		images = new HashMap<String, String>();
		
		initMouseControl();
		
	}
	
	public void setImgLabelPanelReference (ImgLabelPanel ilp) {
		this.ilp = ilp;
	}
	
	private void initMouseControl() {
		tree.addMouseListener(new MouseAdapter() {
			
			/**
			 * If we get a button press we want to check whether the user wants to create a cornerpoint
			 * or if he wants to finish the label by clicking the first cornerpoint
			 */
			public void mousePressed(MouseEvent event) {
				String item = getSelectedTreeItem();
				if(item.endsWith(".png") || item.endsWith(".gif") || item.endsWith(".bmp") || item.endsWith(".jpg")) {
					if(ilp.getFileName() != images.get(item)) {
						ilp.setFileName(images.get(item));
						ilp.loadAndDisplayImage(item);
					}
				}else 
					ilp.setCurrentLabel(getSelectedTreeItem());
			}
		});
	}
	
	private String getSelectedTreeItem() {
		DefaultMutableTreeNode tempNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if(tempNode != null)
			return tempNode.toString();
		else return "";
	}
	
	private void buildTree(){
		setLayout( new BorderLayout() );
		imagesNode = new DefaultMutableTreeNode("Images");
		tree = new JTree(imagesNode);
	}
	
	public void addImageNode(String input, String path){
		model = (DefaultTreeModel) tree.getModel();
		
		// Check if the node already exists
		Enumeration en = imagesNode.breadthFirstEnumeration();
		DefaultMutableTreeNode tempNode;
		while (en.hasMoreElements()) {
		  tempNode = (DefaultMutableTreeNode) en.nextElement();
		  if(((String) tempNode.getUserObject()).equals(input)) {
			  return;
		  }
		}
		
		DefaultMutableTreeNode imgNode = new DefaultMutableTreeNode(input);
		imagesNode.add(imgNode);
		images.put(input, path);
		model.reload();
		repaint();
	}
	
	public void clearLabelsForImage(String image) {
		model = (DefaultTreeModel) tree.getModel();
		
		Enumeration en = imagesNode.breadthFirstEnumeration();
		DefaultMutableTreeNode tempNode;
		while (en.hasMoreElements()) {
		  tempNode = (DefaultMutableTreeNode) en.nextElement();
		  if(((String) tempNode.getUserObject()).equals(image)) {
			  tempNode.removeAllChildren();
			  break;
		  }
		}
	}
	
	public void addLabelNode(String image, ArrayList<Label> labels) {
		model = (DefaultTreeModel) tree.getModel();
	
		Enumeration en = imagesNode.breadthFirstEnumeration();
		DefaultMutableTreeNode imgNode = null;
		DefaultMutableTreeNode tempNode;
		while (en.hasMoreElements()) {
		  tempNode = (DefaultMutableTreeNode) en.nextElement();
		  if(((String) tempNode.getUserObject()).equals(image)) {
			  imgNode = tempNode;
			  break;
		  }
		}
		if(imgNode == null) return;

		
		for(Label l : labels) {
			boolean hasLabel = false;
			en = imgNode.breadthFirstEnumeration();
			while (en.hasMoreElements()) {
			  tempNode = (DefaultMutableTreeNode) en.nextElement();
			  if(((String) tempNode.getUserObject()).equals(l.getName())) {
				  hasLabel = true;
				  break;
			  }
			}
			if(!hasLabel) imgNode.add(new DefaultMutableTreeNode(l.getName()));
		}
		model.reload();
		repaint();
	}
	
	public void refresh() {
		((DefaultTreeModel) tree.getModel()).reload();
		repaint();
	}
	
	public TreePath findByName(JTree tree, String[] names) {
	    TreeNode root = (TreeNode)tree.getModel().getRoot();
	    return find2(tree, new TreePath(root), names, 0, true);
	}
	private TreePath find2(JTree tree, TreePath parent, Object[] nodes, int depth, boolean byName) {
	    TreeNode node = (TreeNode)parent.getLastPathComponent();
	    Object o = node;

	    // If by name, convert node to a string
	    if (byName) {
	        o = o.toString();
	    }

	    // If equal, go down the branch
	    if (o.equals(nodes[depth])) {
	        // If at end, return match
	        if (depth == nodes.length-1) {
	            return parent;
	        }

	        // Traverse children
	        if (node.getChildCount() >= 0) {
	            for (Enumeration e=node.children(); e.hasMoreElements(); ) {
	                TreeNode n = (TreeNode)e.nextElement();
	                TreePath path = parent.pathByAddingChild(n);
	                TreePath result = find2(tree, path, nodes, depth+1, byName);
	                // Found a match
	                if (result != null) {
	                    return result;
	                }
	            }
	        }
	    }

	    // No match at this branch
	    return null;
	}

}
