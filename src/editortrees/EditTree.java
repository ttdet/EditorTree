package editortrees;

import java.util.Stack;

/**
 * A height-balanced binary tree with rank that could be the basis for a text
 * editor.
 * 
 * @author Qingyuan Jiao
 * @author Yao Xiong
 */
public class EditTree {

	Node root;
	private int size;
	private int rotationCount;
	private AdjustionInfo info = new AdjustionInfo('\0', true, 0);

	/**
	 * MILESTONE 1 Construct an empty tree
	 */
	public EditTree() {
		this.root = Node.NULL_NODE;
		this.size = 0;
		this.rotationCount = 0;
	}

	/**
	 * MILESTONE 1 Construct a single-node tree whose element is ch
	 * 
	 * @param ch
	 */
	public EditTree(char ch) {
		this.root = new Node(ch);
		this.size = 1;
		this.rotationCount = 0;
	}

	/**
	 * MILESTONE 2 Make this tree be a copy of e, with all new nodes, but the same
	 * shape and contents. You can write this one recursively, but you may not want
	 * your helper to be in the Node class.
	 * 
	 * @param e
	 */
	public EditTree(EditTree e) {
		this.root = e.copyNode(e.root);
		this.size = e.size;
	}

	public Node copyNode(Node n) {
		if (n == Node.NULL_NODE) {
			return Node.NULL_NODE;
		}
		Node newNode = new Node(n.data, n.left, n.right, n.rank, n.balance);
		newNode.left = copyNode(n.left);
		newNode.right = copyNode(n.right);
		return newNode;
	}

	/**
	 * MILESTONE 3 Create an EditTree whose toString is s. This can be done in O(N)
	 * time, where N is the size of the tree (note that repeatedly calling insert()
	 * would be O(N log N), so you need to find a more efficient way to do this.
	 * 
	 * @param s
	 */
	public EditTree(String s) {
		this.root = Node.NULL_NODE;
		this.root = this.root.buildFromString(s);
		this.size = s.length();
	}

	/**
	 * MILESTONE 1 return the string produced by an in-order traversal of this tree
	 */
	@Override
	public String toString() {
		String str = "";
		Stack<Node> inOrder = new Stack<Node>();
		Node n = this.root;
		while(n != Node.NULL_NODE) {
			inOrder.push(n);
			n = n.left;
		}
		while(!inOrder.isEmpty()) {
			Node curr = inOrder.pop();
			str += curr.data;
			if (curr.right != Node.NULL_NODE) {
				inOrder.push(curr.right);
				Node leftSub = curr.right.left;
				while(leftSub != Node.NULL_NODE) {
					inOrder.push(leftSub);
					leftSub = leftSub.left;
				}
			}
		}

		return str; 
	}

	/**
	 * MILESTONE 1 Just modify the value of this.size whenever adding or removing a
	 * node. This is O(1).
	 * 
	 * @return the number of nodes in this tree, not counting the NULL_NODE if you
	 *         have one.
	 */
	public int size() {
		return this.size; // nothing else to do here.
	}

	/**
	 * MILESTONE 1
	 * 
	 * @param ch character to add to the end of this tree.
	 */
	public void add(char ch) {
		this.add(ch, this.size);
	}

	/**
	 * MILESTONE 1
	 * 
	 * @param ch  character to add
	 * 
	 * @param pos character added in this in-order position Valid positions range
	 *            from 0 to the size of the tree, inclusive (if called with size, it
	 *            will append the character to the end of the tree).
	 * @throws IndexOutOfBoundsException if pos is negative or too large for this
	 *                                   tree.
	 */
	public void add(char ch, int pos) throws IndexOutOfBoundsException {
		if (pos > this.size || pos < 0) {
			throw new IndexOutOfBoundsException();
		} 
		info.traceUp = true;
		info.rCount = 0;
		this.root = this.root.add(ch, pos, info);
		this.size++; 
		this.rotationCount += info.rCount;
	}

	/**
	 * MILESTONE 1 This one asks for more info from each node. You can write it
	 * similar to the arraylist-based toString() method from the BinarySearchTree
	 * assignment. However, the output isn't just the elements, but the elements AND
	 * ranks. Former students recommended that this method, while making it a little
	 * harder to pass tests initially, saves them time later since it catches weird
	 * errors that occur when you don't update ranks correctly. For the tree with
	 * root b and children a and c, it should return the string: [b1, a0, c0] There
	 * are many more examples in the unit tests.
	 * 
	 * @return The string of elements and ranks, given in an PRE-ORDER traversal of
	 *         the tree.
	 */
	public String toRankString() {
		Stack<Node> preOrder = new Stack<Node>();
		String str = "[";
		preOrder.push(this.root);
		
		while (!preOrder.isEmpty()) {
			Node n = preOrder.pop();
			if (n == Node.NULL_NODE) continue;
			str += n.toRankString() + ", ";
			preOrder.push(n.right);
			preOrder.push(n.left);

		}
		return str.length() == 1 ? str + "]" : str.substring(0, str.length() - 2) + "]";
	}

	/**
	 * MILESTONE 1
	 * 
	 * @param pos position in the tree
	 * @return the character at that position
	 * @throws IndexOutOfBoundsException if pos is negative or too big. Note that
	 *                                   the pos is now EXclusive of the size of the
	 *                                   tree, since there is no character there.
	 *                                   But you can still use your size
	 *                                   field/method to determine this.
	 */
	public char get(int pos) throws IndexOutOfBoundsException {
		if (pos >= this.size || pos < 0) {
			throw new IndexOutOfBoundsException();
		}
		return this.root.get(pos);
	}

	// MILESTONE 1: They next two "slow" methods are useful for testing, debugging 
	// and the graphical debugger. They are each O(n) and don't make use of rank or 
	// size. In fact, they are the same as you used in an earlier assignment, so we 
	// are providing them for you.
	// Please do not modify them or their recursive helpers in the Node class.
	public int slowHeight() {
		return root.slowHeight();
	}

	public int slowSize() {
		return root.slowSize();
	}

	/**
	 * MILESTONE 1 Returns true iff (read as "if and only if") for every node in the
	 * tree, the node's rank equals the size of the left subtree. This will be used
	 * to check that your ranks are being updated correctly. So when you get a
	 * subtree's size, you should NOT refer to rank but find it brute-force, similar
	 * to slowSize(), and actually calling slowSize() might be a good first-pass.
	 * 
	 * For full credit, then refactor it to make it more efficient: do this in O(n)
	 * time, so in a single pass through the tree, and with only O(1) extra storage
	 * (so no temp collections).
	 * 
	 * Instead of using slowSize(), use the same pattern as the sum of heights
	 * problem in HW5. We put our helper class inside the Node class, but you can
	 * put it anywhere it's convenient.
	 * 
	 * PLEASE feel free to call this method (or its recursive helper) in your code
	 * while you are writing your add() method if rank isn't working correctly. You
	 * may also modify it to print WHERE it is failing. It may be most important to
	 * use in Milestone 2, when you are updating ranks during rotations. (We added
	 * some commented-out calls to this method there so show you how it can be
	 * used.)
	 * 
	 * @return True iff each node's rank correctly equals its left subtree's size.
	 */
	public boolean ranksMatchLeftSubtreeSize() {
		Bool rankCorrectness = new Bool(true);
		Node n = this.root;
		while (n != Node.NULL_NODE) {
			n.verifySelfAndLeftSubRanks(rankCorrectness);
			n = n.right;
			if (!rankCorrectness.bool) break;
		}
		return rankCorrectness.bool;
	}

	/**
	 * MILESTONE 2 Similar to toRankString(), but adding in balance codes too.
	 * 
	 * For the tree with root b and a left child a, it should return the string:
	 * [b1/, a0=] There are many more examples in the unit tests.
	 * 
	 * @return The string of elements and ranks, given in an pre-order traversal of
	 *         the tree.
	 */
	public String toDebugString() {
		Stack<Node> preOrder = new Stack<Node>();
		String str = "[";
		preOrder.push(this.root);
		
		while (!preOrder.isEmpty()) {
			Node n = preOrder.pop();
			if (n == Node.NULL_NODE) continue;
			str += n.toDebugString() + ", ";
			preOrder.push(n.right);
			preOrder.push(n.left);

		}
		return str.length() == 1 ? str + "]" : str.substring(0, str.length() - 2) + "]";
	}

	/**
	 * MILESTONE 2 returns the total number of rotations done in this tree since it
	 * was created. A double rotation counts as two.
	 *
	 * @return number of rotations since this tree was created.
	 */
	public int totalRotationCount() {
		return this.rotationCount; 
	}

	/**
	 * MILESTONE 2 Returns true iff (read as "if and only if") for every node in the
	 * tree, the node's balance code is correct based on its childrens' heights.
	 * Like ranksMatchLeftSubtreeSize() above, you'll need to compare your balance
	 * code to the actual brute-force height calculation. You may start with calling
	 * slowHeight(). But then, for full credit, do this in O(n) time, so in a single
	 * pass through the tree, and with only O(1) extra storage (so no temp
	 * collections). Instead of slowHeight(), use the same pattern as the sum of
	 * heights problem in HW5. We put our helper class inside the Node class, but
	 * you can put it anywhere it's convenient.
	 * 
	 * The notes for ranksMatchLeftSubtreeSize() above apply here - this method is
	 * to help YOU as the developer.
	 * 
	 * @return True iff each node's balance code is correct.
	 */
	public boolean balanceCodesAreCorrect() {
		Bool res = new Bool(true);
		this.root.verifyBalance(res);
		return res.bool; // replace by a real calculation.
	}

	/**
	 * MILESTONE 2 Only write this one once your balance codes are correct. It will
	 * rely on correct balance codes to find the height of the tree in O(log n)
	 * time.
	 * 
	 * @return the height of this tree
	 */
	public int fastHeight() {
		return this.root.fastHeight();
	}

	/**
	 * MILESTONE 3
	 * 
	 * @param pos position of character to delete from this tree
	 * @return the character that is deleted
	 * @throws IndexOutOfBoundsException
	 */
	public char delete(int pos) throws IndexOutOfBoundsException {
		if (pos >= this.size || pos < 0) {
			throw new IndexOutOfBoundsException();
		}
		info.ch = '\0';
		info.traceUp = true;
		info.rCount = 0;
		this.root = this.root.delete(pos, info);
		this.rotationCount += info.rCount;
		this.size--;
		return info.ch; // replace by a real calculation.
	}

	/**
	 * MILESTONE 3 This method operates in O(length), where length is the
	 * parameter provided. The way to do this is to recurse/iterate only
	 * over the nodes of the tree (and possibly their children) that
	 * contribute to the output string.
	 * 
	 * @param pos    location of the beginning of the string to retrieve
	 * @param length length of the string to retrieve
	 * @return string of length that starts in position pos
	 * @throws IndexOutOfBoundsException unless both pos and pos+length-1 are
	 *                                   legitimate indexes within this tree.
	 */
	public String get(int pos, int length) throws IndexOutOfBoundsException {
		if (pos + length > this.size) {
			throw new IndexOutOfBoundsException();
		}
		if (pos < 0 || length < 0) {
			throw new IndexOutOfBoundsException();
		}
		return this.root.subString(pos, length);

	}

}
