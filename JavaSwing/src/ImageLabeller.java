import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * 
 * @author Samuel Neugber
 * 
 * Java Swing example (originally for one of my courses)
 * 
 * Can load images and tag them with labels.
 * 
 *
 */

@SuppressWarnings("serial")
public class ImageLabeller extends JFrame {

	// General Private Vars
	private final JFileChooser fc = new JFileChooser(); // Used to restrict the
	// files to choose when
	// opening images
	private final ImageLabeller ref = this; // File-chooser need to know in what
	// scope to open

	// Toolbar Private Vars
	private static JMenuItem undoMenuItem;
	private static JMenuItem redoMenuItem;

	// Panels

	private ImgLabelPanel imgLabelPanel;
	private TreeViewPanel sideTreeViewPanel;

	// icon for menu display
	private Icon menuIcon;

	// Buttons for customization
	JRadioButtonMenuItem OpenImageMenuItem, openDirectoryMenuItem,
			saveLabelsMenuItem, undoMenuBoxItem, redoMenuBoxItem,
			removeLabelItem;
	JToolBar toolbar = new JToolBar();

	/**
	 * checks if the toolbar has not been modified
	 */

	// Constructor
	public ImageLabeller() {

		initVariables();
		initMenus();
		initGrids();

		// final GUI setups
		Image image = null;
		try {
			image = ImageIO.read(new File(this.getClass().getClassLoader().getResource("main.png").getPath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		setIconImage(image);
		setSize(800, 600);
		setLocationRelativeTo(null);
		setTitle("Image Labeller");
		setResizable(false);
		setVisible(true);

	}

	/**
	 * Set all initial parameters here
	 */
	private final void initVariables() {
		//set initial toolbar;
		openDirectoryMenuItem = new JRadioButtonMenuItem(
				"import images from directory");
		undoMenuBoxItem = new JRadioButtonMenuItem("Undo");
		redoMenuBoxItem = new JRadioButtonMenuItem("Redo");
		removeLabelItem = new JRadioButtonMenuItem("Remove Label");
		saveLabelsMenuItem = new JRadioButtonMenuItem("Save Labels");
		OpenImageMenuItem = new JRadioButtonMenuItem("Open Image");
		OpenImageMenuItem.setSelected(true);
		openDirectoryMenuItem.setSelected(true);
		saveLabelsMenuItem.setSelected(true);
		undoMenuBoxItem.setSelected(true);
		redoMenuBoxItem.setSelected(true);
		removeLabelItem.setSelected(true);
		
		
		sideTreeViewPanel = new TreeViewPanel();
		// Init Panels
		imgLabelPanel = new ImgLabelPanel();
		imgLabelPanel.setTreeViewPanelReference(sideTreeViewPanel);
		sideTreeViewPanel.setImgLabelPanelReference(imgLabelPanel);

		// Init File Chooser
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Image Files", "png", "jpg", "jpeg");
		fc.addChoosableFileFilter(filter);
		fc.setFileFilter(filter);
		fc.setAcceptAllFileFilterUsed(false);
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	    this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
    				int dialogResult = JOptionPane.showConfirmDialog (null, "Do you want to save your labels before exiting?");
    				if(dialogResult == JOptionPane.YES_OPTION){
    					imgLabelPanel.saveLabels();
    					JOptionPane.showMessageDialog(null, "Labels have been saved");
    					System.exit(0);
    				} else if(dialogResult == JOptionPane.NO_OPTION) {
    					System.exit(0);
    				}
            }
	    });

	}

	/**
	 * All menu action goes here
	 */
	private void initMenus() {

		// Init standard menu buttons
		JMenuBar menubar = new JMenuBar();
		JMenu fileMenu = new JMenu("File"); // File button
		menuIcon = new ImageIcon(this.getClass().getClassLoader().getResource("file.png"));
		fileMenu.setIcon(menuIcon);
		fileMenu.setMnemonic(KeyEvent.VK_F);

		JMenu editMenu = new JMenu("Edit"); // Edit button
		menuIcon = new ImageIcon(this.getClass().getClassLoader().getResource("reply.png"));
		editMenu.setIcon(menuIcon);
		editMenu.setMnemonic(KeyEvent.VK_E);

		JMenu helpMenu = new JMenu("Help"); // Help button
		helpMenu.setMnemonic(KeyEvent.VK_H);
		menuIcon = new ImageIcon(this.getClass().getClassLoader().getResource("comments.png"));
		helpMenu.setIcon(menuIcon);

		JMenu optionMenu = new JMenu("Options");
		menuIcon = new ImageIcon(this.getClass().getClassLoader().getResource("options.png"));
		optionMenu.setIcon(menuIcon);
		optionMenu.setMnemonic(KeyEvent.VK_O);

		JMenu tagMenu = new JMenu("Tagging");
		tagMenu.setMnemonic(KeyEvent.VK_T);
		menuIcon = new ImageIcon(this.getClass().getClassLoader().getResource("tag2.png"));
		tagMenu.setIcon(menuIcon);

		// Exit menu button + actionlistener
		menuIcon = new ImageIcon(this.getClass().getClassLoader().getResource("action_delete.png"));
		JMenuItem exitMenuItem = new JMenuItem("Exit", menuIcon);
		exitMenuItem.setMnemonic(KeyEvent.VK_E);
		exitMenuItem.setToolTipText("Exit application");
		exitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int dialogResult = JOptionPane.showConfirmDialog (null, "Do you want to save your labels before exiting?");
				if(dialogResult == JOptionPane.YES_OPTION){
					imgLabelPanel.saveLabels();
					JOptionPane.showMessageDialog(null, "Labels have been saved");
					System.exit(0);
				} else if(dialogResult == JOptionPane.NO_OPTION) {
					System.exit(0);
				}
			}
		});

		// Open button + actionlistener to open file selection popup
		menuIcon = new ImageIcon(this.getClass().getClassLoader().getResource("folder_open.png"));
		JMenuItem openImgItem = new JMenuItem("Open Image", menuIcon);
		openImgItem.setMnemonic(KeyEvent.VK_O);
		openImgItem.setToolTipText("Open an image");
		openImgItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (fc.showOpenDialog(ref) == JFileChooser.APPROVE_OPTION) {

					imgLabelPanel.setFileName(fc.getSelectedFile()
							.getAbsolutePath());
					imgLabelPanel.loadAndDisplayImage(fc.getSelectedFile()
							.getName());
					sideTreeViewPanel.addImageNode(fc.getSelectedFile()
							.getName(), fc.getSelectedFile().getAbsolutePath());
					imgLabelPanel.addLabelsToTreeView();
				}
			}
		});

		menuIcon = new ImageIcon(this.getClass().getClassLoader().getResource("folder_files.png"));
		JMenuItem importImagesItem = new JMenuItem("Import Images", menuIcon);
		importImagesItem.setMnemonic(KeyEvent.VK_I);
		importImagesItem
				.setToolTipText("Open a directory to import all images in it");
		importImagesItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				fc.setCurrentDirectory(new java.io.File("."));
				fc.setDialogTitle("Select Directory to import images");
				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

					importFilesFromDirectory();

				}
			}
		});

		// Open button + actionlistener to open file selection popup
		menuIcon = new ImageIcon(this.getClass().getClassLoader().getResource("tag.png"));
		JMenuItem loadLabels = new JMenuItem("Load Labels", menuIcon);
		loadLabels.setMnemonic(KeyEvent.VK_L);
		loadLabels.setToolTipText("Load labels for the image");
		loadLabels.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				imgLabelPanel.loadLabels();
				imgLabelPanel.addLabelsToTreeView();
			}
		});

		// Save button: save the current data
		menuIcon = new ImageIcon(this.getClass().getClassLoader().getResource("save.png"));
		JMenuItem saveTxtItem = new JMenuItem("Save Labels", menuIcon);
		saveTxtItem.setMnemonic(KeyEvent.VK_S);
		saveTxtItem.setToolTipText("Save labels for this image");
		saveTxtItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				imgLabelPanel.saveLabels();
				JOptionPane.showMessageDialog(null, "Labels have been saved");

			}
		});

		// Undo button: undo last added label point
		menuIcon = new ImageIcon(this.getClass().getClassLoader().getResource("arrow_back.png"));
		undoMenuItem = new JMenuItem("Undo", menuIcon);
		undoMenuItem.setMnemonic(KeyEvent.VK_B);
		undoMenuItem.setEnabled(false);
		undoMenuItem.setToolTipText("Undo last point");
		undoMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				imgLabelPanel.removeLast();
			}
		});

		menuIcon = new ImageIcon(this.getClass().getClassLoader().getResource("arrow_next.png"));
		redoMenuItem = new JMenuItem("Redo", menuIcon);
		redoMenuItem.setMnemonic(KeyEvent.VK_N);
		redoMenuItem.setEnabled(true);
		redoMenuItem.setToolTipText("Redo last removed point");
		redoMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				imgLabelPanel.redo();
			}
		});

		// Clear button: delete the entire current label
		menuIcon = new ImageIcon(this.getClass().getClassLoader().getResource("action_remove.png"));
		JMenuItem clearMenuItem = new JMenuItem("Remove Label", menuIcon);
		clearMenuItem.setMnemonic(KeyEvent.VK_C);
		clearMenuItem.setToolTipText("Remove current label");
		clearMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				imgLabelPanel.clear();
			}
		});

		menuIcon = new ImageIcon(this.getClass().getClassLoader().getResource("stop.png"));
		JMenuItem removeLabelsItem = new JMenuItem("Clear Labels", menuIcon);
		removeLabelsItem.setMnemonic(KeyEvent.VK_R);
		removeLabelsItem.setToolTipText("Clear all labels");
		removeLabelsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				imgLabelPanel.removeLabels();
			}
		});

		JCheckBoxMenuItem SavecheckItem = new JCheckBoxMenuItem(
				"Enable Autosave");
		SavecheckItem.setSelected(false);

		SavecheckItem.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if (e.getStateChange() == ItemEvent.SELECTED) {
					imgLabelPanel.setAutoSave(true);
				} else {
					imgLabelPanel.setAutoSave(false);
				}
			}

		});

		JCheckBoxMenuItem showNameCheckItem = new JCheckBoxMenuItem(
				"Display Label Names");
		showNameCheckItem.setSelected(false);

		showNameCheckItem.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if (e.getStateChange() == ItemEvent.SELECTED) {
					imgLabelPanel.displayLabelNames(true);
				} else {
					imgLabelPanel.displayLabelNames(false);
				}
			}

		});
		/**
		 * TODO: add better description
		 */

		menuIcon = new ImageIcon(this.getClass().getClassLoader().getResource("blog.png"));
		JMenuItem welcomeItem = new JMenuItem("Welcome", menuIcon);
		welcomeItem.setMnemonic(KeyEvent.VK_W);
		welcomeItem.setToolTipText("Welcome text");
		welcomeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(
						null,
						"Welcome to the image labelling application by Samuel Neugber and Jaunius Pinelis.\n"
						
						);
			}
		});
		
		menuIcon = new ImageIcon(this.getClass().getClassLoader().getResource("user.png"));
		JMenuItem manualItem = new JMenuItem("Manual", menuIcon);
		manualItem.setMnemonic(KeyEvent.VK_M);
		manualItem.setToolTipText("Short instructions");
		manualItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(
						null,
						"- To Select an image to label, click on File->Open Image, or File->Import Images,\n"+
						"  or click the large button with the image symbol.\n"+
						"- To create a label click anywhere onto the image to add cornerpoints,\n" +
						"  and connect the last to the first point to finish the label.\n"+
						"- To select a label press on any Point connected to label or choose the label\n"+
						" in side panel."
						
						
						);
			}
		});

		final JCheckBoxMenuItem tagModeItem = new JCheckBoxMenuItem("On/Off");
		tagModeItem.setSelected(true);
		imgLabelPanel.setTagMode(true);
		tagModeItem.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if (e.getStateChange() == ItemEvent.SELECTED) {
					imgLabelPanel.setTagMode(true);

				} else {
					imgLabelPanel.setTagMode(false);

				}
			}

		});

		menuIcon = new ImageIcon(this.getClass().getClassLoader().getResource("colors.jpg"));
		JMenu colorsItem = new JMenu("Set Colors");
		colorsItem.setIcon(menuIcon);
		colorsItem.setToolTipText("Set Colors of the lines");
		JMenuItem lineColorSelectItem = new JMenuItem(
				"Set Line Color of Selected Label");
		lineColorSelectItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				Color c;
				c = JColorChooser.showDialog(((Component) arg0.getSource())
						.getParent(), "Demo", Color.GREEN);
				imgLabelPanel.setCurrentLabelLineColor(c);
			}

		});
		JMenuItem bgColorSelectItem = new JMenuItem(
				"Set Fill Color of Selected Label");

		bgColorSelectItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				Color c;
				c = JColorChooser.showDialog(((Component) arg0.getSource())
						.getParent(), "Demo", Color.GREEN);
				imgLabelPanel.setCurrentLabelBGColor(c);
			}

		});

		//-------------------------------------------------------------
		JMenu customizeToolbarItem = new JMenu("Customize toolbar");

		OpenImageMenuItem.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub

				initToolbar();
			}
		}

		);
		customizeToolbarItem.add(OpenImageMenuItem);

		customizeToolbarItem.add(openDirectoryMenuItem);

		openDirectoryMenuItem.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub

				initToolbar();
			}
		}

		);

		customizeToolbarItem.add(saveLabelsMenuItem);
		saveLabelsMenuItem.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub

				initToolbar();
			}
		}

		);

		customizeToolbarItem.add(undoMenuBoxItem);
		undoMenuBoxItem.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub

				initToolbar();
			}
		}

		);

		customizeToolbarItem.add(redoMenuBoxItem);
		redoMenuBoxItem.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub

				initToolbar();
			}
		}

		);

		customizeToolbarItem.add(removeLabelItem);
		removeLabelItem.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub

				initToolbar();
			}
		}

		);

		//---------------------------------------------------------

		colorsItem.add(lineColorSelectItem);
		colorsItem.add(bgColorSelectItem);

		// TODO: New label button

		// Add buttons to menu
		fileMenu.add(openImgItem);
		fileMenu.add(importImagesItem);
		fileMenu.add(saveTxtItem);
		fileMenu.add(loadLabels);
		fileMenu.add(new JSeparator());
		fileMenu.add(exitMenuItem);

		editMenu.add(undoMenuItem);
		editMenu.add(redoMenuItem);
		editMenu.add(new JSeparator());
		editMenu.add(removeLabelsItem);
		editMenu.add(clearMenuItem);
		editMenu.add(colorsItem);

		tagMenu.add(tagModeItem);

		optionMenu.add(SavecheckItem);
		optionMenu.add(showNameCheckItem);
		optionMenu.add(new JSeparator());
		optionMenu.add(customizeToolbarItem);

		helpMenu.add(welcomeItem);
		helpMenu.add(manualItem);

		menubar.add(fileMenu);
		menubar.add(editMenu);
		menubar.add(tagMenu);
		menubar.add(optionMenu);
		menubar.add(helpMenu);
		setJMenuBar(menubar);
	}

	private final void initGrids() {

		Container container = getContentPane();
		container.setLayout(new GridBagLayout());

		// -------------------------------
		toolbar = new JToolBar();
		initToolbar();

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.gridwidth = 2;
		gbc.weighty = 0.03;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.BOTH;

		container.add(toolbar, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0.9;
		gbc.weighty = 0.97;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.BOTH;

		container.add(imgLabelPanel, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 0.1;
		gbc.weighty = 0.97;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.BOTH;

		// Testing

		container.add(sideTreeViewPanel, gbc);

	}

	/**
	 * inits Toolbars
	 */
	private void initToolbar() {

		toolbar.removeAll();

		URL iconPath = this.getClass().getClassLoader().getResource("tExit.png");
		ImageIcon icon = new ImageIcon(iconPath);
		icon = getResizedIcon(icon, 30, 30);
		JButton removeLabelButton = new JButton(icon);
		removeLabelButton.setToolTipText("Remove the current label");

		removeLabelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				imgLabelPanel.clear();
			}

		});

		iconPath = this.getClass().getClassLoader().getResource("folders.png");
		icon = new ImageIcon(iconPath);
		icon = getResizedIcon(icon, 30, 30);
		JButton importDirectory = new JButton(icon);
		importDirectory.setToolTipText("Import images from directory");

		importDirectory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				fc.setCurrentDirectory(new java.io.File("."));
				fc.setDialogTitle("Select Directory to import images");
				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

					importFilesFromDirectory();

				}
			}

		});

		iconPath = this.getClass().getClassLoader().getResource("tSave.png");
		icon = new ImageIcon(iconPath);
		icon = getResizedIcon(icon, 30, 30);
		JButton saveProgressButton = new JButton(icon);
		saveProgressButton.setToolTipText("Save labels");

		saveProgressButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				imgLabelPanel.saveLabels();
			}

		});

		iconPath = this.getClass().getClassLoader().getResource("main.png");
		icon = new ImageIcon(iconPath);
		icon = getResizedIcon(icon, 30, 30);
		JButton openImageButton = new JButton(icon);
		openImageButton.setToolTipText("Open a new image");

		openImageButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (fc.showOpenDialog(ref) == JFileChooser.APPROVE_OPTION) {

					imgLabelPanel.setFileName(fc.getSelectedFile()
							.getAbsolutePath());
					imgLabelPanel.loadAndDisplayImage(fc.getSelectedFile()
							.getName());
					sideTreeViewPanel.addImageNode(fc.getSelectedFile()
							.getName(), fc.getSelectedFile().getAbsolutePath());
					imgLabelPanel.addLabelsToTreeView();
				}
			}

		});

		iconPath = this.getClass().getClassLoader().getResource("tUndo.png");
		icon = new ImageIcon(iconPath);
		icon = getResizedIcon(icon, 30, 30);
		JButton undoButton = new JButton(icon);
		undoButton.setToolTipText("Remove last added point");

		undoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				imgLabelPanel.removeLast();
			}

		});

		iconPath = this.getClass().getClassLoader().getResource("tRedo.png");
		icon = new ImageIcon(iconPath);
		icon = getResizedIcon(icon, 30, 30);
		JButton redoButton = new JButton(icon);
		redoButton.setToolTipText("Add last removed point");

		redoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				imgLabelPanel.redo();
			}

		});

		iconPath = this.getClass().getClassLoader().getResource("tTag.png");
		icon = new ImageIcon(iconPath);
		icon = getResizedIcon(icon, 30, 30);
		JButton tagButton = new JButton(icon);
		tagButton.setToolTipText("Turn on/off tag mode");

		tagButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				imgLabelPanel.setTagMode(!imgLabelPanel.getTagMode());
			}

		});

		if (OpenImageMenuItem.isSelected())
			toolbar.add(openImageButton);
		if (openDirectoryMenuItem.isSelected())
			toolbar.add(importDirectory);
		if (saveLabelsMenuItem.isSelected())
			toolbar.add(saveProgressButton);

		toolbar.add(tagButton);

		if (undoMenuBoxItem.isSelected())
			toolbar.add(undoButton);
		if (redoMenuBoxItem.isSelected())
			toolbar.add(redoButton);
		if (removeLabelItem.isSelected()) {
			toolbar.add(removeLabelButton);
		}

	}

	public static void main(String[] args) {
		new ImageLabeller();
	}

	public static void enableButtons() {
		undoMenuItem.setEnabled(true);
	}

	private static ImageIcon getResizedIcon(ImageIcon icon, int width,
			int height) {
		Image img = icon.getImage();

		Image newimg = img.getScaledInstance(width, height,
				java.awt.Image.SCALE_SMOOTH);
		ImageIcon newIcon = new ImageIcon(newimg); // we now have new icon -
		// scaled by 120x120
		return newIcon;

	}

	private void importFilesFromDirectory() {
		File dir = new File(fc.getSelectedFile().getAbsolutePath().toString());

		// array of supported extensions (use a List if you prefer)
		final String[] EXTENSIONS = new String[] { "gif", "png", "bmp", "jpg" // and
		// other
		// formats
		// you
		// need
		};
		// filter to identify images based on their extensions
		FilenameFilter IMAGE_FILTER = new FilenameFilter() {

			@Override
			public boolean accept(final File dir, final String name) {
				for (final String ext : EXTENSIONS) {
					if (name.endsWith("." + ext)) {
						return (true);
					}
				}
				return (false);
			}
		};
		System.out.println(dir);

		if (dir.isDirectory()) { // make sure it's a directory
			for (final File f : dir.listFiles(IMAGE_FILTER)) {
				System.out.println(f.getName());
				sideTreeViewPanel
						.addImageNode(f.getName(), f.getAbsolutePath());
			}
		}
	}
}
