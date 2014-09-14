import java.util.*;
import java.lang.ref.WeakReference;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class AVL<T extends Comparable<T>> {
    private class Node {
        // public members are useful internally
        // and won't be seen outside of AVL
        public T data;
        public Node left, right;
        public int height;
        public WeakReference<Node> parent;
        
        public Node(T x) {
            data = x;
            left = right = null;
            height = 1;
            parent = new WeakReference<>(null);
        }

        // never actually used because I need to update height :P
        private void rebalance() {
            if (left == null) {
                if (right == null) {
                    return;
                }
                if (right.height > 1)
                    rebalanceRightHeavy();
            } else {
                if (right == null) {
                    if (left.height > 1)
                        rebalanceLeftHeavy();
                } else {
                    if (left.height > right.height) {
                        if (left.height - right.height > 1)
                            rebalanceLeftHeavy();
                    } else {
                        if (right.height - left.height > 1)
                            rebalanceRightHeavy();
                    }
                }
            }
        }

        private void rebalanceLeftHeavy() {
            if (left.left != null) {
                if (left.right != null)
                    if (left.right.height > left.left.height) // left subtree is right heavy
                        left.leftRotate();
            } else {
                if (left.right != null)
                    left.leftRotate();
            }
            rightRotate();
        }

        private void rebalanceRightHeavy() {
            if (right.right != null) {
                if (right.left != null)
                    if (right.left.height > right.right.height) // right subtree is left heavy
                        right.rightRotate();
            } else {
                if (right.left != null)
                    right.rightRotate();
            }
            leftRotate();
        }
        
        public void insert(T x) {
            if (x.compareTo(data) <= 0) {
                if (left == null) {
                    left = new Node(x);
                    left.parent = new WeakReference<>(this);
                    if (height < 2)
                        height = 2;
                } else {
                    left.insert(x);
                    if (right == null || left.height > right.height)
                        height = left.height + 1;
                    if ((right == null && left.height > 1) || left.height - right.height > 1) {
                        rebalanceLeftHeavy();
                    }
                }
            } else {
                if (right == null) {
                    right = new Node(x);
                    right.parent = new WeakReference<>(this);
                    if (height < 2)
                        height = 2;
                } else {
                    right.insert(x);
                    if (left == null || right.height > left.height)
                        height = right.height + 1;
                    if ((left == null && right.height > 1) || right.height - left.height > 1) {
                        rebalanceRightHeavy();
                    }
                }
            }
        }

        // delete self
        private void delete() {
            replace(null);
        }

        // replace self
        private void replace(Node x) {
            if (parent.get() == null) {
                head = x;
            } else {
                if (data.compareTo(parent.get().data) <= 0) {
                    parent.get().left = x;
                } else {
                    parent.get().right = x;
                }
            }
            if (x != null)
                x.parent = new WeakReference<>(parent.get());
        }
        
        // note that this is actually not recursive
        // because a node may be swapped and the
        // call stack invalidated
        public void delete(T x) {
            Node n = this; // node to delete or current node
            boolean found = false;
            while (true) {
                if (n == null)
                    throw new IndexOutOfBoundsException("no such node");
                int c = x.compareTo(n.data);
                if (c < 0 && !found) {
                    n = n.left;
                } else if (c > 0 && !found) {
                    n = n.right;
                } else {
                    if (n.left == null) {
                        if (n.right == null) {
                            // case 1: no children
                            Node t = n; // node to delete
                            n = n.parent.get(); // rebalance parent
                            // note: pointless to rebalance deleted node
                            t.delete();
                            break;
                        } else {
                            // case 2: one child
                            Node t = n; // node to delete
                            n = n.right;
                            t.replace(t.right);
                            n = n.parent.get(); // rebalance parent
                            // note: pointless to rebalance right node
                            // which is guaranteed to be an AVL tree
                            break;
                        }
                    } else {
                        if (n.right == null) {
                            // case 2: one child
                            Node t = n;
                            n = n.left;
                            t.replace(t.left);
                            n = n.parent.get(); // rebalance parent
                            // note: pointless to rebalance left node
                            // which is guaranteed to be an AVL tree
                            break;
                        } else {
                            System.out.println("2 children");
                            // case 3: two children
                            // find in-order successor
                            // aka leftmost node in right subtree
                            Node t = n;
                            n = n.right;
                            while (n.left != null)
                                n = n.left;
                            t.data = n.data;
                            System.out.println(n.data);
                            // no break; will delete with case 1 or 2
                            found = true;
                        }
                    }
                }
            }
            while (n != null) {
                Node next = n.parent.get();
                if (n.left == null) {
                    if (n.right == null) {
                        n.height = 1;
                    } else {
                        n.height = n.right.height + 1;
                        if (n.right.height > 1)
                            n.rebalanceRightHeavy();
                    }
                } else {
                    if (n.right == null) {
                        n.height = n.left.height + 1;
                        if (n.left.height > 1)
                            n.rebalanceLeftHeavy();
                    } else {
                        if (n.left.height > n.right.height) {
                            n.height = n.left.height + 1;
                            if (n.left.height - n.right.height > 1) {
                                n.rebalanceLeftHeavy();
                            }
                        } else {
                            n.height = n.right.height + 1;
                            if (n.right.height - n.left.height > 1) {
                                n.rebalanceRightHeavy();
                            }
                        }
                    }
                }
                n = next;
            }
        }
        
        private void rightRotate() {
            // rotate right
            System.out.println("RIGHT ROTATE on " + data.toString());
            for (StackTraceElement e : new Throwable().getStackTrace())
                System.out.println(e);
            if (parent.get() != null) {
                if (data.compareTo(parent.get().data) <= 0) { // we're the left node
                    parent.get().left = left;
                } else {
                    parent.get().right = left;
                }
            } else {
                head = left;
                left.parent = new WeakReference<>(null);
            }
            left.parent = new WeakReference<>(parent.get());
            parent = new WeakReference<>(left);
            Node oldlr = left.right;
            left.right = this;
            left = oldlr;
            if (oldlr != null)
                oldlr.parent = new WeakReference<>(this);
            // update heights
            if (right != null)
                if (left != null)
                    height = (left.height > right.height ? left.height : right.height) + 1;
                else
                    height = right.height + 1;
            else
                if (left != null)
                    height = left.height + 1;
                else
                    height = 1;
            if (parent.get().left != null)
                parent.get().height = (parent.get().left.height > height ? parent.get().left.height : height) + 1;
            else
                parent.get().height = height + 1;
        }
        
        private void leftRotate() {
            // rotate left
            System.out.println("LEFT ROTATE on " + data.toString());
            for (StackTraceElement e : new Throwable().getStackTrace())
                System.out.println(e);
            if (parent.get() != null) {
                if (data.compareTo(parent.get().data) <= 0) { // we're the left node
                    parent.get().left = right;
                } else {
                    parent.get().right = right;
                }
            } else {
                head = right;
                right.parent = new WeakReference<>(null);
            }
            right.parent = new WeakReference<>(parent.get());
            parent = new WeakReference<>(right);
            Node oldrl = right.left;
            right.left = this;
            right = oldrl;
            if (oldrl != null)
                oldrl.parent = new WeakReference<>(this);
            // update heights
            if (right != null)
                if (left != null)
                    height = (left.height > right.height ? left.height : right.height) + 1;
                else
                    height = right.height + 1;
            else
                if (left != null)
                    height = left.height + 1;
                else
                    height = 1;
            if (parent.get().right != null)
                parent.get().height = (parent.get().right.height > height ? parent.get().right.height : height) + 1;
            else
                parent.get().height = height + 1;
        }
        
        public void print() {
            if (left != null) {
                // verify parents
                if (left.parent.get() != this)
                    System.out.println("Parent mismatch");
                left.print();
            }
            System.out.println(data);
            if (right != null) {
                if (right.parent.get() != this)
                    System.out.println("Parent mismatch");
                right.print();
            }
        }
        
        public void structure(int depth) { // old structure
            for (int i = 0; i < depth; i++)
                System.out.print(" ");
            System.out.println(data);
            if (left != null)
                left.structure(depth + 1);
            else {
                for (int i = 0; i < depth; i++)
                    System.out.print(" ");
                System.out.println("NULL");
            }
            if (right != null)
                right.structure(depth + 1);
            else {
                for (int i = 0; i < depth; i++)
                    System.out.print(" ");
                System.out.println("NULL");
            }
        }
        
        // thanks to a user on stack overflow
        public void structure() {
            int maxLevel = maxLevel();
            printNodeInternal(Collections.singletonList(this), 1, maxLevel);
        }
    
        private void printNodeInternal(List<Node> nodes, int level, int maxLevel) {
            if (nodes.isEmpty() || isAllElementsNull(nodes))
                return;
            int floor = maxLevel - level;
            int endgeLines = (int) Math.pow(2, (Math.max(floor - 1, 0)));
            int firstSpaces = (int) Math.pow(2, (floor)) - 1;
            int betweenSpaces = (int) Math.pow(2, (floor + 1)) - 1;
            printWhitespaces(firstSpaces);
            List<Node> newNodes = new ArrayList<Node>();
            for (Node node : nodes) {
                if (node != null) {
                    System.out.print(node.data);
                    newNodes.add(node.left);
                    newNodes.add(node.right);
                } else {
                    newNodes.add(null);
                    newNodes.add(null);
                    System.out.print(" ");
                }
    
                printWhitespaces(betweenSpaces);
            }
            System.out.println("");
            for (int i = 1; i <= endgeLines; i++) {
                for (int j = 0; j < nodes.size(); j++) {
                    printWhitespaces(firstSpaces - i);
                    if (nodes.get(j) == null) {
                        printWhitespaces(endgeLines + endgeLines + i + 1);
                        continue;
                    }
    
                    if (nodes.get(j).left != null)
                        System.out.print("/");
                    else
                        printWhitespaces(1);
    
                    printWhitespaces(i + i - 1);
    
                    if (nodes.get(j).right != null)
                        System.out.print("\\");
                    else
                        printWhitespaces(1);
    
                    printWhitespaces(endgeLines + endgeLines - i);
                }
    
                System.out.println("");
            }
    
            printNodeInternal(newNodes, level + 1, maxLevel);
        }
    
        private void printWhitespaces(int count) {
            for (int i = 0; i < count; i++)
                System.out.print(" ");
        }
    
        private int maxLevel(Node node) {
            if (node == null)
                return 0;
            int h = Math.max(maxLevel(node.left), maxLevel(node.right)) + 1;
            if (h != node.height)
                System.out.println("Height mismatch");
            return h;
        }
        
        private int maxLevel() {
            return maxLevel(this);
        }
    
        private <T> boolean isAllElementsNull(List<T> list) {
            for (Object object : list) {
                if (object != null)
                    return false;
            }
    
            return true;
        }
    }
    
    Node head;
    
    public AVL() {
        head = null;
    }
    
    public void insert(T x) {
        if (head == null) {
            head = new Node(x);
        } else {
            head.insert(x);
        }
    }

    public void delete(T x) {
        if (head == null) {
            throw new IndexOutOfBoundsException("no such node");
        } else {
            head.delete(x);
        }
    }
    
    public void print() {
        head.print();
    }
    
    public void structure() {
        head.structure();
    }
    
    public void structure(int depth) {
        head.structure(depth);
    }
    
    public void clear() {
        head = null;
    }
    
    public static void test() {
        AVL<Integer> a = new AVL<Integer>();
        // decreasing
        for (int i = 10; i > 0; i--) {
            a.insert(i);
        }
        a.print();
        System.out.println();
        a.structure();
        a.clear();
        // increasing
        for (int i = 1; i <= 10; i++) {
            a.insert(i);
        }
        a.print();
        System.out.println();
        a.structure();
    }

    public static void main(String argv[]) {
        AVL<Integer> a = new AVL<Integer>();
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("> ");
            String line;
            try {
                line = r.readLine();
            } catch (IOException e) {
                // probably eof
                break;
            }
            Scanner s = new Scanner(line);
            String cmd = s.next();
            if (cmd.equals("q"))
                break;
            if (cmd.equals("i"))
                while (s.hasNextInt())
                    a.insert(s.nextInt());
            if (cmd.equals("d"))
                while (s.hasNextInt())
                    a.delete(s.nextInt());
            if (cmd.equals("c"))
                a.clear();
            if (cmd.equals("p"))
                a.print();
            if (cmd.equals("s"))
                a.structure();
        }
    }
}
