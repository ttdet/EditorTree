package editortrees;

/**
 * This is a container class that keeps tracks of the information needed during an addition or deletion operation.
 * The char stores the char deleted in a deletion operation. it will not be used for addition.
 * The boolean denotes whether further tracing up the tree, 
 * or in other words, further modifications of balance codes, is needed. 
 * The int keeps the total number of rotations happened in the operation.
 */
public class AdjustionInfo {
    public char ch;
    public boolean traceUp;
    public int rCount;
    public AdjustionInfo(char ch, boolean b, int i) {
        this.ch = ch;
        this.traceUp = b;
        this.rCount = i;
    }

}
