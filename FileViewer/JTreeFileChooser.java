//
// Name:        Kwok, Amanda
// Homework:    #3
// Due:         December 3, 2018
// Course:      CS-2450-01-F18
//
// Description: A program designed to list files and folders in the current 
// directory using a tree.
//
import java.io.File;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.tree.*;
import java.util.Comparator;

public class JTreeFileChooser {

    private File selectedFile;
    private boolean fileChosen;
    private DefaultMutableTreeNode root;
    
    // returns the selected file or null if none
    public File getSelectedFile() {
        return selectedFile;
    }
    
    // Pops up an "Open File" tree file chooser dialog
    public boolean showDialog(JFrame parent, String rootPath) {  
        selectedFile = null;
        JDialog dlg = new JDialog(parent,"Open File", true);
        dlg.setSize(500,350);
        dlg.setLayout(new BorderLayout());
        JLabel label = new JLabel("Select a file");

        // Create new tree
        root = new DefaultMutableTreeNode(rootPath);
        DefaultTreeModel model = new DefaultTreeModel(root);
        JTree tree = new JTree();
        tree.setModel(model);
        tree.setEditable(true);
        tree.setCellRenderer(new FileTreeCellRenderer());
        
        TreeSelectionModel tsm = tree.getSelectionModel();
        tsm.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tree);  
        loadNodes(new File(rootPath));
	tree.expandPath(new TreePath(root.getPath()));
	
        
        // Display the path of the selected file
        tree.addTreeSelectionListener(tsl -> {
            TreeNode node = (TreeNode) tree.getLastSelectedPathComponent();
            ArrayList<Object> path = new ArrayList<Object>();
            if (node != null) {
                path.add(node);
                node = node.getParent();
                while (node != null) {
                  path.add(0, node);
                  node = node.getParent();
                }
        }            
            String pathString = "";
            for (int i = 0; i<path.size(); i++){
                pathString += path.get(i); 
                if (i != path.size()-1) {
                    pathString += File.separator;
                }
            }
            label.setText(pathString);
        });
        
        // Configure buttons
        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton ok = new JButton ("Ok");
        south.add(ok);
        ok.addActionListener(ae -> {
            selectedFile = new File(label.getText());
            fileChosen = true;
            dlg.setVisible(false);
        });
        
        JButton cancel = new JButton ("Cancel");
        cancel.addActionListener(ae -> {
            fileChosen = false;
            dlg.setVisible(false);
        });
        
        south.add(cancel);
        dlg.setLocationRelativeTo(parent);
        dlg.add(south, BorderLayout.SOUTH);
        dlg.add(label, BorderLayout.NORTH);
        dlg.add(scrollPane);
        dlg.setVisible(true);
        return fileChosen;
    }
    
    private void loadNodes(File fileRoot) {
        File[] firstLevel = fileRoot.listFiles();
        loadNodesRecursive(firstLevel, root);
    }
    private void loadNodesRecursive (File[] arr, DefaultMutableTreeNode newRoot){
        if (arr == null)
            return;
       
        ArrayList<File> filteredList = new ArrayList<File>();
        for (int i = 0; i<arr.length; i++) {
            if (arr[i].isDirectory() || arr[i].getName().endsWith(".java")) {
                filteredList.add(arr[i]);
            } 
        }
        File[] filteredArray = filteredList.toArray(new File[filteredList.size()]);
        Comparator comp = new Comparator() { 
            public int compare(Object o1, Object o2) { 
                File f1 = (File) o1; 
                File f2 = (File) o2; 
                if (f1.isDirectory() && !f2.isDirectory())                                    
                    return -1; 
                else if (!f1.isDirectory() && f2.isDirectory()) 
                    return 1; 
                else 
                    return f1.compareTo(f2);
            }
        };
        Arrays.sort(filteredArray, comp);
        for (File file : filteredArray) {
            DefaultMutableTreeNode temp = new DefaultMutableTreeNode(file.getName());
            newRoot.add(temp);
            loadNodesRecursive(file.listFiles(), temp);
        }
}

// Replaced icon of empty subdirectory from leaf to folder
class FileTreeCellRenderer extends DefaultTreeCellRenderer {
    public Component getTreeCellRendererComponent(JTree tree,
            Object value, boolean sel, boolean expanded, boolean leaf,
            int row, boolean hasFocus)
    {
        JLabel renderer = (JLabel)super.getTreeCellRendererComponent(
                tree, value, sel, expanded, leaf, row, hasFocus);
        if (!(value.toString().endsWith(".java"))) {
            if (expanded)
                renderer.setIcon(openIcon);
            else
                renderer.setIcon(closedIcon);
        }
        else
            renderer.setIcon(leafIcon);
        return renderer;
    }
}
}
