package editortrees;

import java.util.Stack;

/**
 * A node in a height-balanced binary tree with rank. Except for the NULL_NODE,
 * one node cannot belong to two different trees.
 * 
 * @author Qingyuan Jiao and Yao Xiong
 */
public class Node {

	enum Code {
		SAME, LEFT, RIGHT;

		// Used in the displayer and debug string
		public String toString() {
			switch (this) {
			case LEFT:
				return "/";
			case SAME:
				return "=";
			case RIGHT:
				return "\\";
			default:
				throw new IllegalStateException();
			}
		}
	}

	char data;
	Node left, right; // subtrees
	int rank; // inorder position of this node within its own subtree.
	Code balance;

	static final Node NULL_NODE = new Node('\0', null, null, 0, Code.SAME);



	public Node(char data) {
		this.data = data;
		this.left = NULL_NODE;
		this.right = NULL_NODE;
		this.rank = 0;
		this.balance = Code.SAME;
	}

	public Node(char data, Node left, Node right, int rank, Code balance) {
		this.data = data;
		this.left = left;
		this.right = right;
		this.rank = rank;
		this.balance = balance;
	}


	/**
	 * Adds an char to the designated position.
	 * @param c The char to be added.
	 * @param index The index to be inserted at.
	 * @param toBeBalanced A container class. This is by default true. 
	 * 					   Once one rotation has been done and the tree is rebalanced, 
	 * 					   this will be changed to false. If no rebalancing is needed, 
	 * 					   Its value will remain true throughout.
	 * @param rCount A container class. This is used to count the rotations happened. 
	 * 				 A single rotation will change its value to one, while a double rotation
	 * 				 will change its value to two.
	 * @return The updated node. 
	 */
	public Node add(char c, int index, AdjustionInfo info) {
		if (this == NULL_NODE) {
			return new Node(c);
		} else {
			if (index <= this.rank) { //Recurses to the left subtree
				this.rank++;
				this.left = this.left.add(c, index, info);
				if (info.traceUp) { //Need further tracing up

					if(this.balance == Code.RIGHT) {
						this.balance = Code.SAME;
						info.traceUp = false;
					} else if (this.balance == Code.SAME) {
						this.balance = Code.LEFT;
					} else { //this.balance == Code.LEFT
						info.traceUp = false;

						if (this.left.balance == Code.LEFT) {
							info.rCount++;
							return this.SRRotate();
						} else if (this.left.balance == Code.RIGHT) {
							 info.rCount += 2;
							 return this.DRRotate();
						}

						
					}
				}
				
			} else { //Recurses to the right subtree
				this.right = this.right.add(c, index - this.rank - 1, info);
				if (info.traceUp) { //Needs further tracing up

					if(this.balance == Code.LEFT) {
						this.balance = Code.SAME;
						info.traceUp = false;
					} else if (this.balance == Code.SAME) {
						this.balance = Code.RIGHT;
					} else { //this.balance == Code.RIGHT
						info.traceUp = false;

						if (this.right.balance == Code.RIGHT) {
							info.rCount++;
							return this.SLRotate();
						} else if (this.right.balance == Code.LEFT) {
							 info.rCount += 2;
							 return this.DLRotate();
						}

					}
				}
				
			}
		}

		return this;

	}

	/**
	 * Gets the char at a specific position.
	 * @param index The position to visit.
	 * @return The char at the index. 
	 */
	public char get(int index) {
		if (this.rank == index) {
			return this.data;
		} else if (this.rank > index) {
			return this.left.get(index);
		} else {
			return this.right.get(index - this.rank - 1);
		}
	}

	/**
	 * Deletes a designated node (i.e. the char stored) at the given position.
	 * @param index Node at this position will be deleted.
	 * @param wrapper This is a container class that contains the infomation needed during
	 * 				  the deletion process. It keeps track of the number of rotations, whether
	 * 				  all balance codes have been properly adjusted, and also contains the char deleted.
	 * 					
	 * @return The updated node.
	 */
	public Node delete(int index, AdjustionInfo wrapper) {
		
		if (this.rank > index) { //Recurses to the left child
			this.rank--;
			this.left = this.left.delete(index, wrapper);

			if (wrapper.traceUp) {
				if (this.balance == Code.LEFT) {
					this.balance = Code.SAME;
					
				} else if (this.balance == Code.RIGHT) {
					if (this.right.balance == Code.RIGHT) {
						wrapper.rCount++;
						return this.SLRotate();
					} else if (this.right.balance == Code.LEFT) {
						wrapper.rCount += 2;
						return this.DLRotate();
					} else { //this.right.balance == Code.SAME
						wrapper.rCount++;
						Node n = this.SLRotate();
						n.balance = Code.LEFT;
						n.left.balance = Code.RIGHT;
						// Height remains unchanged after this rotation
						wrapper.traceUp = false;
						return n;
					}
				} else { //this.balance == Code.SAME, height remains unchanged
					this.balance = Code.RIGHT;
					wrapper.traceUp = false;
				}
			}
			
		} else if (this.rank < index) { //Recurses to the right child
			
			this.right = this.right.delete(index - this.rank - 1, wrapper);

			if (wrapper.traceUp) {
				if (this.balance == Code.RIGHT) {
					this.balance = Code.SAME;
					
				} else if (this.balance == Code.LEFT) {
					if (this.left.balance == Code.LEFT) {
						wrapper.rCount++;
						return this.SRRotate();
					} else if (this.left.balance == Code.RIGHT) {
						wrapper.rCount += 2;
						return this.DRRotate();
					} else { //this.left.balance == Code.SAME
						wrapper.rCount++;
						Node n = this.SRRotate();
						n.balance = Code.RIGHT;
						n.right.balance = Code.LEFT;
						// Height remains unchanged after this rotation
						wrapper.traceUp = false; 
						return n;
					}
				} else { //this.balance == Code.SAME
					this.balance = Code.LEFT;
					wrapper.traceUp = false;
				}
			}
		} else { //The current node is the target
			
			if (wrapper.ch == '\0') { 
				wrapper.ch = this.data;
			}
			if (this.right == NULL_NODE && this.left == NULL_NODE) {
				return NULL_NODE;
			} else if (this.left == NULL_NODE) { //Has only right child
				return this.right;
			} else if (this.right == NULL_NODE) { //Has only left child
				return this.left;
			} else { //Has both children
				
				return this.replaceWithSuccessor(wrapper);
			}
		}

		return this;
	}

	/**
	 * This method replaces the current node with its smallest in-order successor. This is used in deletion.
	 * @param wrapper A container class. Contains the infomation of a deletion.
	 * @return The updated node.
	 */
	public Node replaceWithSuccessor(AdjustionInfo wrapper) {
		Node n = this.right;
		while (n.left != NULL_NODE) {
			n = n.left;
		}
		this.data = n.data;
		//delete the redundant successor before returning
		return this.delete(this.rank + 1, wrapper);
	}

	/**
	 * Verifies that the rank of the current node and the ranks of
	 * all the nodes in its left sub-tree match the size of their left subtrees.
	 * @param res A container class. Its value is by default true but will be changed to false
	 * 			  once a node's rank is found incorrect.
	 * @return The rank of the current node if the ranks of all checked nodes are correct. 
	 * 		   -1 if a node has been verified to have incorrect rank. 
	 */
	public int verifySelfAndLeftSubRanks(Bool res) {
		if (!res.bool) {
			return -1;
		}
		if (this == NULL_NODE) {
			return 0;
		}
		int calculatedRank = 0;
		Node n = this.left;
		while (n != NULL_NODE) {
			calculatedRank += 1 + n.verifySelfAndLeftSubRanks(res);
			n = n.right;
		}
		
		if (calculatedRank != this.rank) {
			res.bool = false;
		}

		return this.rank;
	}

	/**
	 * Verifies that the balance codes of all the descendants of the current node are correct.
	 * @param res A container class. Its value is by default true but will be changed to false 
	 * 			  once a node's balance code is found incorrect.
	 * @return The height of the node if all checked nodes have correct balance codes. 
	 * 		   -2 if any node has been verified to have incorrect balance codes. 
	 */
	public int verifyBalance(Bool res) {
		if (this == NULL_NODE) {
			return -1;
		}

		if (!res.bool) {
			return -2;
		}

		int leftH = this.left.verifyBalance(res);
		int rightH = this.right.verifyBalance(res);

		if (this.balance == Code.LEFT) {
			if (leftH != rightH + 1) {
				res.bool = false;
			}
		} else if (this.balance == Code.RIGHT) {
			if (rightH != leftH + 1) {
				res.bool = false;
			}
		} else {
			if (leftH != rightH) {
				res.bool = false;
			}
		}

		return Math.max(leftH, rightH) + 1;
	}

	/**
	 * Performs a single left rotation at the current node.
	 * @return The updated node after the rotation. 
	 */
	public Node SLRotate() {
		Node newRoot = this.right;
		Node rightChildLeftSub = this.right.left;
		newRoot.left = this;
		newRoot.balance = Code.SAME;
		newRoot.left.right = rightChildLeftSub;
		newRoot.left.balance = Code.SAME;
		newRoot.rank += newRoot.left.rank + 1;
		return newRoot;
	}

	/**
	 * Performs a single right rotation at the current node.
	 * @return The updated node after the rotation.
	 */
	public Node SRRotate() {
		Node newRoot = this.left;
		Node leftChildRightSub = this.left.right;
		newRoot.right = this;
		newRoot.right.left = leftChildRightSub;
		newRoot.right.balance = Code.SAME;
		newRoot.balance = Code.SAME;
		newRoot.right.rank -= newRoot.rank + 1;
		return newRoot;
	}

	/**
	 * Performs a double left rotation, or right-left rotation, at the current node.
	 * @return The updated node after the rotation. 
	 */
	public Node DLRotate() {
		Node newRoot = this.right.left;
		Node leftSub = newRoot.left;
		Node rightSub = newRoot.right;
		newRoot.left = this;
		newRoot.right = this.right;
		newRoot.left.right = leftSub;
		newRoot.right.left = rightSub;
		newRoot.right.rank -= newRoot.rank + 1;
		newRoot.rank += newRoot.left.rank + 1;

		if (newRoot.balance == Code.RIGHT) {
			newRoot.right.balance = Code.SAME;
			newRoot.left.balance = Code.LEFT; 
		} else if (newRoot.balance == Code.LEFT) {
			newRoot.left.balance = Code.SAME;
			newRoot.right.balance = Code.RIGHT;
		} else {
			newRoot.left.balance = Code.SAME;
			newRoot.right.balance = Code.SAME;
		}
		newRoot.balance = Code.SAME;

		return newRoot;
	}

	/**
	 * Performs a double right rotation, or left-right rotation, at the current node.
	 * @return The updated node after the rotation. 
	 */
	public Node DRRotate() {
		Node newRoot = this.left.right;
		Node leftSub = newRoot.left;
		Node rightSub = newRoot.right;
		newRoot.left = this.left;
		newRoot.right = this;
		newRoot.left.right = leftSub;
		newRoot.right.left = rightSub;
		newRoot.right.rank -= newRoot.rank + newRoot.left.rank + 2;
		newRoot.rank += newRoot.left.rank + 1;

		if (newRoot.balance == Code.RIGHT) {
			newRoot.right.balance = Code.SAME;
			newRoot.left.balance = Code.LEFT; 
		} else if (newRoot.balance == Code.LEFT) {
			newRoot.left.balance = Code.SAME;
			newRoot.right.balance = Code.RIGHT;
		} else {
			newRoot.left.balance = Code.SAME;
			newRoot.right.balance = Code.SAME;
		}
		newRoot.balance = Code.SAME;

		return newRoot;
	}

	/**
	 * Builds a tree from a string, which is the inorder traversal of the tree. This tree's root will be the current node.
	 * @param str A tree will be built according to this string.
	 * @return The root node of the tree created from the string.
	 */
	public Node buildFromString(String str) {
		if (str.length() == 1) {
			return new Node(str.charAt(0));
		} 
		int mid = str.length() / 2;
		Node n = new Node(str.charAt(mid), NULL_NODE, NULL_NODE, mid, Code.SAME);
		n.left = n.left.buildFromString(str.substring(0, mid));
		if (str.length() == 2) {
			n.balance = Code.LEFT;
			return n;
		} else {
			n.right = n.right.buildFromString(str.substring(mid + 1, str.length()));
			if (n.left.balance == Code.LEFT && n.right.balance == Code.SAME) {
				n.balance = Code.LEFT;
			}

			return n;
		}
	}

	/**
	 * Gets a string containing the char and rank of the current node.
	 * @return A string containing the char and rank of the current node.
	 */
	public String toRankString() {
		return this.data + String.valueOf(this.rank);
	}

	/**
	 * Gets a string containing the char, the rank, and the balance code of the current node.
	 * @return A string containing the char, the rank, and the balance code of the current node.
	 */
	public String toDebugString() {
		return this.data + String.valueOf(this.rank) + this.balance.toString();
	}

	/**
	 * A fast way to get the height current tree. This method uses balance codes. 
	 * @return The height of current tree. 
	 */
	public int fastHeight() {
		if (this == NULL_NODE) {
			return -1;
		}

		if (this.balance == Code.LEFT) {
			return 1 + this.left.fastHeight();
		} else {
			return 1 + this.right.fastHeight();
		} 
	}

	/**
	 * Gets a substring of the entire tree. 
	 * @param index The start index of the substring.
	 * @param length The length of the substring.
	 * @return A substring that starts at the given index with the given length. 
	 */
	public String subString(int index, int length) {

		if (length <= 0) {
			return "";
		}

		if (index <= this.rank) {
			if (index + length <= this.rank) {
				return this.left.subString(index, length);
			} else {
				return this.left.subString(index, this.rank - index) + this.data + 
							this.right.subString(0, length - this.rank + index - 1);
			}
		} else {
			return this.right.subString(index - this.rank - 1, length);
		}
	}


	/**
	 * Outputs all chars stored in the current tree in-order. 
	 * @return A string containing all chars in the current tree. 
	 */
	public String toInOrderString() {
		String str = "";
		Stack<Node> inOrder = new Stack<Node>();
		Node n = this;
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


	// Provided to you to enable testing, please don't change.
	/**
	 * A slow way to get the height of the current tree .
	 * @return The height of the current tree.
	 */
	int slowHeight() {
		if (this == NULL_NODE) {
			return -1;
		}
		return Math.max(left.slowHeight(), right.slowHeight()) + 1;
	}


	// Provided to you to enable testing, please don't change.
	/**
	 * Gets the size of the current tree.
	 * @return The size of the current tree. 
	 */
	public int slowSize() {
		if (this == NULL_NODE) {
			return 0;
		}
		return left.slowSize() + right.slowSize() + 1;
	}

	
}