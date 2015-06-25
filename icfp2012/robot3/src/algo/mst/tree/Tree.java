package algo.mst.tree;

import java.util.Collections;
import java.util.List;

import model.Mine;


public class Tree {
	TreeNode root;
	
	public Tree(Mine initial) {
		this.root = new TreeNode(initial, 0, null);
	}
	
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		return toString(result, Collections.singletonList(root), 0).toString();
	}

	private StringBuffer toString(StringBuffer result, List<TreeNode> nodes, int depth) {
		if (nodes == null) return result;
		String prefix = "";
		for (int i=0; i<depth; i++) prefix+="\t";
		for (TreeNode treeNode : nodes) {
			result.append(prefix);
			result.append(treeNode.getPathDelta());
			result.append("\n");
			toString(result, treeNode.getChildren(), depth+1);
		}
		return result;
	}
	

	public TreeNode getRoot() {
		return root;
	}

	public void visit(TreeVisitor treeVisitor) {
		visit(root, treeVisitor);
	}

	private void visit(TreeNode node, TreeVisitor treeVisitor) {
		treeVisitor.visit(node);
		if (node.getChildren() == null) return;
		for (TreeNode child : node.getChildren()) {
			visit(child, treeVisitor);
		}
	}

}