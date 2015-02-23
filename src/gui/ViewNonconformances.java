package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.ToolTipManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import utils.commons.Constants;
import utils.commons.FileUtil;
import utils.datastructure.Nonconformance;
import controller.Controller;

/**
 * Shown an Screen for Categorization info of the program.
 *
 * @author Alysson Milanez and Dennis Sousa.
 * @version 1.0
 */
public class ViewNonconformances extends JFrame {
	/**
*
*/
	private static final long serialVersionUID = 1L;
	private List<Nonconformance> nc;
	private String[] namesNC;
	private JPanel contentPane;
	private JList<String> listNonconformances;
	private JTextArea textAreaTestCases;
	private Highlighter highLit;
	private Highlighter.HighlightPainter painter;
	private JFileChooser dirLibs;
	// Constants defining window size.
	private static final int WIDTH = 790;
	private static final int HEIGHT = 410;
	private JTree tree;
	private Controller controller;

	/**
	 * Create the frame.
	 * 
	 * @param controller
	 */
	public ViewNonconformances(
			final List<Nonconformance> nonconformance, Controller controller) {
		this.controller = controller;
		initializingStringForSelectionList(nonconformance);
		// Initialize Screen Options
		FileUtil.setUIFont(new javax.swing.plaf.FontUIResource(
				Constants.MAIN_FONT));
		dirLibs = new JFileChooser();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 790, 456);
		setMinimumSize(new Dimension(WIDTH, HEIGHT));
		// Initialize Icons
		List<Image> icons = new ArrayList<Image>();
		icons.add((Image) FileUtil.createImageIcon("images/logo(16x16).jpg")
				.getImage());
		icons.add((Image) FileUtil.createImageIcon("images/logo(32x32).jpg")
				.getImage());
		icons.add((Image) FileUtil.createImageIcon("images/logo(64x64).jpg")
				.getImage());
		icons.add((Image) FileUtil.createImageIcon("images/logo(128x128).jpg")
				.getImage());
		setIconImages(icons);
		// Initialize Window
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout springLayout = new SpringLayout();
		contentPane.setLayout(springLayout);
		// Nonconformance info
		JLabel lblNumberNonconformances = new JLabel("nonconformances.");
		lblNumberNonconformances.setFont(new Font("Verdana", Font.BOLD, 18));
		contentPane.add(lblNumberNonconformances);
		JLabel lblNumberNonconformancesToSet = new JLabel(nc.size() + "");
		springLayout.putConstraint(SpringLayout.NORTH,
				lblNumberNonconformances, 0, SpringLayout.NORTH,
				lblNumberNonconformancesToSet);
		springLayout.putConstraint(SpringLayout.WEST, lblNumberNonconformances,
				6, SpringLayout.EAST, lblNumberNonconformancesToSet);
		springLayout.putConstraint(SpringLayout.NORTH,
				lblNumberNonconformancesToSet, 0, SpringLayout.NORTH,
				contentPane);
		lblNumberNonconformancesToSet
				.setFont(new Font("Verdana", Font.BOLD, 18));
		contentPane.add(lblNumberNonconformancesToSet);
		JLabel lblNumberNonconformances1 = new JLabel("Were detected");
		springLayout.putConstraint(SpringLayout.NORTH,
				lblNumberNonconformances1, 0, SpringLayout.NORTH, contentPane);
		springLayout.putConstraint(SpringLayout.WEST,
				lblNumberNonconformances1, 10, SpringLayout.WEST, contentPane);
		springLayout.putConstraint(SpringLayout.WEST,
				lblNumberNonconformancesToSet, 10, SpringLayout.EAST,
				lblNumberNonconformances1);
		lblNumberNonconformances1.setFont(new Font("Verdana", Font.BOLD, 18));
		contentPane.add(lblNumberNonconformances1);
		JLabel lblNonconformances = new JLabel("Nonconformances");
		springLayout.putConstraint(SpringLayout.NORTH, lblNonconformances, 6, SpringLayout.SOUTH, lblNumberNonconformances);
		springLayout.putConstraint(SpringLayout.WEST, lblNonconformances, 10, SpringLayout.WEST, contentPane);
		lblNonconformances.setFont(new Font("Verdana", Font.BOLD, 18));
		contentPane.add(lblNonconformances);
		// Location Label
		JLabel lblLocation = new JLabel("Location");
		springLayout.putConstraint(SpringLayout.NORTH, lblLocation, 0, SpringLayout.NORTH, lblNonconformances);
		lblLocation.setFont(new Font("Verdana", Font.BOLD, 18));
		contentPane.add(lblLocation);
		// Initialize List
		listNonconformances = new JList<String>();
		listNonconformances.setSize(225,  310);
		listNonconformances.setListData(namesNC);
		listNonconformances
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listNonconformances
				.addListSelectionListener(new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						setChangesFromSelectionOnTheList();
					}
				});
		// Initialize Highlighter
		highLit = new DefaultHighlighter();
		painter = new DefaultHighlighter.DefaultHighlightPainter(
				Constants.HILIT_COLOR);
		// Set List Area
		JScrollPane scrollPaneListNC = new JScrollPane();
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPaneListNC, 311, SpringLayout.SOUTH, lblNonconformances);
		scrollPaneListNC.setBounds(10, 20, 225, 310);
		scrollPaneListNC.setSize(225,  310);
		springLayout.putConstraint(SpringLayout.NORTH, scrollPaneListNC, 2, SpringLayout.SOUTH, lblNonconformances);
		springLayout.putConstraint(SpringLayout.WEST, scrollPaneListNC, 10,
				SpringLayout.WEST, contentPane);
		springLayout.putConstraint(SpringLayout.EAST, scrollPaneListNC, 240,
				SpringLayout.WEST, contentPane);
		scrollPaneListNC.setViewportView(listNonconformances);
		contentPane.add(scrollPaneListNC);
		// Set Test Cases Area
		textAreaTestCases = new JTextArea();
		textAreaTestCases.setSize(225,  310);
		textAreaTestCases.setEditable(false);
		JScrollPane scrollPaneTestCase = new JScrollPane();
		springLayout.putConstraint(SpringLayout.EAST, scrollPaneTestCase, -5, SpringLayout.EAST, contentPane);
		scrollPaneTestCase.setBounds(10, 20, 225, 310);
		scrollPaneTestCase.setSize(225, 310);
		scrollPaneTestCase.setViewportView(textAreaTestCases);
		contentPane.add(scrollPaneTestCase);
		// create the root node
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Location");
		// create the child nodes
		DefaultMutableTreeNode packageNode = new DefaultMutableTreeNode("");
		root.add(packageNode);
		DefaultMutableTreeNode classNode = new DefaultMutableTreeNode("");
		packageNode.add(classNode);
		DefaultMutableTreeNode methodNode = new DefaultMutableTreeNode("");
		classNode.add(methodNode);
		tree = new JTree(root);
		tree.setBounds(10, 20, 225, 310);
		tree.setSize(225,  310);
		expandAllJTree();
		
		// Create Tree
		JScrollPane scrollPaneTree = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollPaneTree, 2, SpringLayout.SOUTH, lblLocation);
		springLayout.putConstraint(SpringLayout.WEST, lblLocation, 0, SpringLayout.WEST, scrollPaneTree);
		springLayout.putConstraint(SpringLayout.WEST, scrollPaneTestCase, 10, SpringLayout.EAST, scrollPaneTree);
		springLayout.putConstraint(SpringLayout.WEST, scrollPaneTree, 10, SpringLayout.EAST, scrollPaneListNC);
		springLayout.putConstraint(SpringLayout.EAST, scrollPaneTree, -264, SpringLayout.EAST, contentPane);
		scrollPaneTree.setSize(225, 310);
		// Set Renderer options to Tree
		
		ToolTipManager.sharedInstance().registerComponent(tree);
		tree.setCellRenderer(new MyRenderer());
		tree.setRootVisible(false);
		scrollPaneTree.setColumnHeaderView(tree);
		contentPane.add(scrollPaneTree);
		JLabel lblTestCase = new JLabel("Test Case");
		springLayout.putConstraint(SpringLayout.WEST, lblTestCase, 175, SpringLayout.EAST, lblLocation);
		springLayout.putConstraint(SpringLayout.NORTH, scrollPaneTestCase, 2, SpringLayout.SOUTH, lblTestCase);
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPaneTestCase, 311, SpringLayout.SOUTH, lblTestCase);
		springLayout.putConstraint(SpringLayout.NORTH, lblTestCase, 0, SpringLayout.NORTH, lblNonconformances);
		springLayout.putConstraint(SpringLayout.SOUTH, lblTestCase, -356, SpringLayout.SOUTH, contentPane);
		lblTestCase.setFont(new Font("Verdana", Font.BOLD, 18));
		contentPane.add(lblTestCase);
		// Save Results Button
		JButton btnSaveResults = new JButton("Save Results");
		springLayout.putConstraint(SpringLayout.EAST, btnSaveResults, 212, SpringLayout.WEST, contentPane);
		springLayout.putConstraint(SpringLayout.NORTH, btnSaveResults, 369,
				SpringLayout.NORTH, contentPane);
		springLayout.putConstraint(SpringLayout.WEST, btnSaveResults, 37, SpringLayout.WEST, contentPane);
		springLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER,
				btnSaveResults, 0, SpringLayout.HORIZONTAL_CENTER,
				scrollPaneListNC);
		btnSaveResults.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveResults();
			}
		});
		btnSaveResults.setFont(new Font("Verdana", Font.BOLD, 18));
		contentPane.add(btnSaveResults);
		// Stack Trace Button
		JButton btnStackTrace = new JButton("Stack Trace");
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPaneTree, -6, SpringLayout.NORTH, btnStackTrace);
		springLayout.putConstraint(SpringLayout.NORTH, btnStackTrace, 0, SpringLayout.NORTH, btnSaveResults);
		springLayout.putConstraint(SpringLayout.WEST, btnStackTrace, 71, SpringLayout.EAST, btnSaveResults);
		btnStackTrace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (listNonconformances.isSelectionEmpty())
					JOptionPane
							.showMessageDialog(
									ViewNonconformances.this,
									"Please select one of the nonconformances to display its stack trace.");
				else
					showStackTrace(nonconformance.get(
							listNonconformances.getSelectedIndex())
							.getStackTraceOrder());
			}
		});
		btnStackTrace.setFont(new Font("Verdana", Font.BOLD, 18));
		contentPane.add(btnStackTrace);
		
		JButton btnCategorize = new JButton("Categorize");
		springLayout.putConstraint(SpringLayout.EAST, btnStackTrace, -90, SpringLayout.WEST, btnCategorize);
		springLayout.putConstraint(SpringLayout.NORTH, btnCategorize, 6, SpringLayout.SOUTH, scrollPaneTestCase);
		springLayout.putConstraint(SpringLayout.WEST, btnCategorize, -214, SpringLayout.EAST, contentPane);
		springLayout.putConstraint(SpringLayout.EAST, btnCategorize, -44, SpringLayout.EAST, contentPane);
		btnCategorize.setFont(new Font("Verdana", Font.BOLD, 18));
		btnCategorize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		contentPane.add(btnCategorize);
		// Select one option of list
		if (nc.size() > 0) {
			listNonconformances.setSelectedIndex(0);
			setChangesFromSelectionOnTheList();
		}
	}

	/**
	 * Initialize array of names to put on JList from list of nonconformances.
	 * 
	 * @param nonconformance
	 *            List of nonconformances to be put on JList.
	 */
	private void initializingStringForSelectionList(
			List<Nonconformance> nonconformance) {
		nc = nonconformance;
		namesNC = new String[nc.size()];
		for (int i = 0; i < nc.size(); i++) {
			namesNC[i] = (i + 1) + " - " + nc.get(i).getType().getName();
		}
	}

	/**
	 * Show stack trace frame.
	 * 
	 * @param list
	 *            List of stack trace order.
	 */
	private void showStackTrace(final List<String> list) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PopupStackTraceSwowerFrame frame = new PopupStackTraceSwowerFrame(
							list);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Operation when button save results are pressed, copy the file results.xml
	 * to another directory specified by user.
	 */
	protected void saveResults() {
		String path = "";
		dirLibs.setApproveButtonText("Select");
		dirLibs.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		dirLibs.setCurrentDirectory(new File(jarPath()));
		if (dirLibs.showOpenDialog(ViewNonconformances.this) == JFileChooser.APPROVE_OPTION) {
			path = dirLibs.getSelectedFile().getAbsolutePath();
		}
		try {
			this.controller.saveResultsInXML(path);
			JOptionPane.showMessageDialog(this, "Results saved.");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Invalid Directory.");
		}
	}

	/**
	 * Return path of where jar had run.
	 * 
	 * @return path of where jar had run.
	 */
	private String jarPath() {
		Path path = null;
		try {
			path = Paths.get(Main.class.getProtectionDomain().getCodeSource()
					.getLocation().toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return path.getParent().toString();
	}

	/**
	 * Make changes on frame depending on selected option from JList.
	 */
	private void setChangesFromSelectionOnTheList() {
		// Set Principal Info
		changeNode(1, nc.get(listNonconformances.getSelectedIndex())
				.getClassName());
		changeNode(2, nc.get(listNonconformances.getSelectedIndex())
				.getMethodName());
		if (nc.get(listNonconformances.getSelectedIndex()).getPackageName() == "") {
			changeNode(0, "<default>");
		} else {
			changeNode(0, nc.get(listNonconformances.getSelectedIndex())
					.getPackageName());
		}
		// Test Case and Highlight
		textAreaTestCases.setText(nc
				.get(listNonconformances.getSelectedIndex()).getTestCaseCode());
		textAreaTestCases.setHighlighter(highLit);
		String stringToHighlight = nc
				.get(listNonconformances.getSelectedIndex())
				.getSampleLineOfError().trim();
		int counter = nc.get(listNonconformances.getSelectedIndex())
				.getCountOcurrencesLineOfError();
		int beginIndex = textAreaTestCases.getText().indexOf(stringToHighlight);
		while (counter-- > 0)
			beginIndex = textAreaTestCases.getText().indexOf(stringToHighlight,
					beginIndex + stringToHighlight.length());
		int endIndex = beginIndex + stringToHighlight.length();
		try {
			highLit.addHighlight(beginIndex, endIndex, painter);
		} catch (BadLocationException e1) {
			try {
				throw new Exception("beginIndex = " + beginIndex
						+ " endIndex = " + endIndex + " stringToHighlight = "
						+ stringToHighlight + " \n" + e1.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Change information for selected nodes.
	 * 
	 * @param index
	 *            indicating depth of node desired.
	 * @param text
	 *            indicating new information to node.
	 */
	private void changeNode(int index, String text) {
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		DefaultMutableTreeNode node = null;
		for (int i = 0; i <= index; i++) {
			node = (DefaultMutableTreeNode) root.getChildAt(0);
			root = node;
		}
		node.setUserObject(text);
		model.nodeChanged(node);
	}

	/**
	 * Expand all JTree nodes.
	 */
	public void expandAllJTree() {
		int row = 0;
		while (row < tree.getRowCount()) {
			tree.expandRow(row);
			row++;
		}
	}

	/**
	 * Overrides DefaultTreeCellRenderer to puts more functionalities.
	 */
	private class MyRenderer extends DefaultTreeCellRenderer {
		private static final long serialVersionUID = 1L;
		Icon packageIcon;
		Icon classIcon;
		Icon methodIcon;

		/**
		 * Prepare options of images to frame.
		 */
		public MyRenderer() {
			packageIcon = FileUtil.createImageIcon("images/packageIcon.png");
			classIcon = FileUtil.createImageIcon("images/classIcon.png");
			methodIcon = FileUtil.createImageIcon("images/methodIcon.png");
		}

		/**
		 * Override cellRenderer to permit another functionalities for cells of
		 * JTree.
		 */
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value, sel, expanded,
					leaf, row, hasFocus);
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			setToolTipText(node.getUserObject().toString());
			if (node.getDepth() == 2) {
				setIcon(packageIcon);
			} else if (node.getDepth() == 1) {
				setIcon(classIcon);
			} else if (node.getDepth() == 0) {
				setIcon(methodIcon);
			}
			return this;
		}
	}
}
