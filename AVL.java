import java.util.*;
import java.lang.ref.WeakReference;

public class AVL<T extends Comparable<T>> {
	private class Node {
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
						rightRotate();
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
						leftRotate();
					}
				}
			}
		}
		
		private void rightRotate() {
			// rotate right
			System.out.println("RIGHT ROTATE");
			if (parent.get() != null) {
				if (data.compareTo(parent.get().data) <= 0) { // we're the left node
					parent.get().left = left;
				} else {
					parent.get().right = left;
				}
			} else {
				head = left;
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
			System.out.println("LEFT ROTATE");
			if (parent.get() != null) {
				if (data.compareTo(parent.get().data) <= 0) { // we're the left node
					parent.get().left = right;
				} else {
					parent.get().right = right;
				}
			} else {
				head = right;
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
			int	maxLevel = maxLevel();
			printNodeInternal(Collections.singletonList(this), 1, maxLevel);
		}
	
		private void printNodeInternal(List<Node> nodes, int level,	int	maxLevel) {
			if (nodes.isEmpty()	|| isAllElementsNull(nodes))
				return;
			int	floor =	maxLevel - level;
			int	endgeLines = (int) Math.pow(2, (Math.max(floor - 1,	0)));
			int	firstSpaces	= (int)	Math.pow(2,	(floor)) - 1;
			int	betweenSpaces =	(int) Math.pow(2, (floor + 1)) - 1;
			printWhitespaces(firstSpaces);
			List<Node> newNodes = new ArrayList<Node>();
			for	(Node node :	nodes) {
				if (node !=	null) {
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
			for	(int i = 1;	i <= endgeLines; i++) {
				for	(int j = 0;	j <	nodes.size(); j++) {
					printWhitespaces(firstSpaces -	i);
					if (nodes.get(j) ==	null) {
						printWhitespaces(endgeLines + endgeLines +	i +	1);
						continue;
					}
	
					if (nodes.get(j).left != null)
						System.out.print("/");
					else
						printWhitespaces(1);
	
					printWhitespaces(i	+ i	- 1);
	
					if (nodes.get(j).right != null)
						System.out.print("\\");
					else
						printWhitespaces(1);
	
					printWhitespaces(endgeLines + endgeLines -	i);
				}
	
				System.out.println("");
			}
	
			printNodeInternal(newNodes,	level +	1, maxLevel);
		}
	
		private void printWhitespaces(int count)	{
			for	(int i = 0;	i <	count; i++)
				System.out.print(" ");
		}
	
		private int maxLevel(Node node)	{
			if (node ==	null)
				return 0;
			int h = Math.max(maxLevel(node.left), maxLevel(node.right)) + 1;
			if (h != node.height)
				System.out.println("Height mismatch");
			return h;
		}
		
		private int maxLevel() {
			return maxLevel(this);
		}
	
		private <T>	boolean isAllElementsNull(List<T> list) {
			for	(Object	object : list) {
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
	
	public static void main(String argv[]) {
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
}
