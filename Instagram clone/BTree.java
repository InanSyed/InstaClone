import java.util.ArrayList;

public class BTree {
    private BNode root;

    // constructor, initializes the root of the tree to be null
    public BTree() {
        root = null;
    }

    // public method to insert a value into the tree
    public void insert(String val) {
        root = insert(root, val);
    }

    // private helper method to insert a value into a specific branch of the tree
    private BNode insert(BNode branch, String val) {
        if (branch == null) {
            // if the branch is null, create a new node with the given value and return it
            return new BNode(val);
        }
        if (val.compareTo(branch.val) < 0) {
            // if the value to insert is less than the value of the current node, recursively insert it on the left
            branch.left = insert(branch.left, val);
        } else if (val.compareTo(branch.val) > 0) {
            // if the value to insert is greater than the value of the current node, recursively insert it on the right
            branch.right = insert(branch.right, val);
        }
        // return the current node
        return branch;
    }

    // public method to search for values in the tree with a given prefix
    public ArrayList<String> search(String prefix) {
        ArrayList<String> result = new ArrayList<>();
        search(root, prefix, result);
        return result;
    }

    // private helper method to search for values in a specific branch of the tree with a given prefix
    private void search(BNode node, String prefix, ArrayList<String> result) {
        if (node == null || prefix == "") {
            // if the node is null or the prefix is empty, return
            return;
        }
        if (node.val.startsWith(prefix)) {
            // if the value of the node starts with the given prefix, add it to the result list
            result.add(node.val);
        }
        search(node.left, prefix, result);
        search(node.right, prefix, result);
    }

    // private class representing a node in the tree
    private class BNode {
        private String val;
        private BNode left;
        private BNode right;

        // constructor, initializes the value and left and right children of the node
        public BNode(String val) {
            this.val = val;
            left = null;
            right = null;
        }
    }
}
