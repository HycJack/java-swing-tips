package example;
//-*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import java.awt.event.*;
import java.util.Optional;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

public final class MainPanel extends JPanel {
    private MainPanel() {
        super(new BorderLayout());
        JTree tree = new JTree();
        tree.setComponentPopupMenu(new TreePopupMenu());
        add(new JScrollPane(tree));
        setPreferredSize(new Dimension(320, 240));
    }
    public static void main(String... args) {
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                createAndShowGUI();
            }
        });
    }
    public static void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException
               | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        JFrame frame = new JFrame("@title@");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(new MainPanel());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

class TreePopupMenu extends JPopupMenu {
    private TreePath path;
    private final Action addChildNodeAction = new AbstractAction("add child node") {
        @Override public void actionPerformed(ActionEvent e) {
            JTree tree = (JTree) getInvoker();
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            DefaultMutableTreeNode self  = (DefaultMutableTreeNode) path.getLastPathComponent();
            DefaultMutableTreeNode child = new DefaultMutableTreeNode("New child node");
            self.add(child);
            model.reload(self);
            //or: model.insertNodeInto(child, self, self.getChildCount());
        }
    };
    private final Action addAboveAction = new AbstractAction("insert preceding sibling node") {
        @Override public void actionPerformed(ActionEvent e) {
            JTree tree = (JTree) getInvoker();
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            MutableTreeNode self   = (MutableTreeNode) path.getLastPathComponent();
            MutableTreeNode parent = (MutableTreeNode) self.getParent();
            MutableTreeNode child  = new DefaultMutableTreeNode("New preceding sibling");
            int index = model.getIndexOfChild(parent, self);
            parent.insert(child, index);
            model.reload(parent);
            //or: model.insertNodeInto(child, parent, index);
        }
    };
    private final Action addBelowAction = new AbstractAction("insert following sibling node") {
        @Override public void actionPerformed(ActionEvent e) {
            JTree tree = (JTree) getInvoker();
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            MutableTreeNode self   = (MutableTreeNode) path.getLastPathComponent();
            MutableTreeNode parent = (MutableTreeNode) self.getParent();
            MutableTreeNode child  = new DefaultMutableTreeNode("New following sibling");
            int index = model.getIndexOfChild(parent, self);
            parent.insert(child, index + 1);
            model.reload(parent);
            //or: model.insertNodeInto(child, parent, index + 1);
        }
    };
    protected TreePopupMenu() {
        super();
        add(addChildNodeAction);
        addSeparator();
        add(addAboveAction);
        add(addBelowAction);
    }
    @Override public void show(Component c, int x, int y) {
        if (c instanceof JTree) {
            JTree tree = (JTree) c;
            path = tree.getPathForLocation(x, y);
            Optional.ofNullable(path).ifPresent(tp -> super.show(c, x, y));
        }
    }
}
