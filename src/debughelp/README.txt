Use of the displayable code is completely optional. It will take you a few minutes 
to integrate it into your code. 
But many past students have found it _very much_ worth their time. 
See the image in this folder for an example of the output it produces. To use it, simply:

1. Uncomment the whole DisplayableBinaryTree and DisplayableBinaryNode classes 
   (ctrl-a to select all, then ctrl-/)
 
2. Add/implement the required fields and methods to your EditTree class:
	
	-A DisplayableBinaryTree field called display:
		private DisplayableBinaryTree display;


	-The following show() method that you can call anywhere (unit tests, or main) to show your tree. 
	It will initialize the display field the first time it is called.
	
	public void show() {
		if (this.display == null) {
			this.display = new DisplayableBinaryTree(this, 960, 1080, true);
		} else {
			this.display.show(true);
		}
	}

	- A close() method (optional)
	/**
	 * closes the tree window, still keeps all the data, 
	   and you can still reshow the tree with the show() method
	 */
	public void close() {
		if (this.display != null) {
			this.display.close();
		}
	}

3. Add the required fields and methods to Node:
	- A non-private field called displayableNodeWrapper which is a DisplayableNodeWrapper object:
		DisplayableNodeWrapper displayableNodeWrapper;
	
	- In the Node constructor, initialize the DisplayableNodeWrapper field, like:
		displayableNodeWrapper = new DisplayableNodeWrapper(this);
	
	- The following methods:
	
	public boolean hasLeft() {
		return this.left != NULL_NODE;
	}

	public boolean hasRight() {
		return this.right != NULL_NODE;
	}

	public boolean hasParent() {
		return false;
	}

	public Node getParent() {
		return NULL_NODE;
	}
	- These assume you aren't using parents (which is generally easier).
	- if you were using parents, you'd need to return the right thing 
	  AND also do the TODO at the top of DisplayableBinaryTree to set the boolean to true
	  AND then push your changes to the server so we can run it.   

4. Call the show() method on your tree, like we did in the last unit test of Milestone 1:
	t.show();
	And since you'll want to pause the program after showing it, so that the display 
	doesn't close, add an infinite loop like: while (true) {}
	