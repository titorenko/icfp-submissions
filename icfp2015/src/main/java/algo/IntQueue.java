package algo;

public interface IntQueue {
	/**
	 * Inserts the specified element into this queue if it is possible to do so
	 * immediately without violating capacity restrictions, returning
	 * {@code true} upon success and throwing an {@code IllegalStateException}
	 * if no space is currently available. 
	*/
	boolean add(int e);
	
	/**
     * Retrieves and removes the head of this queue,
     * or throws an {@code IllegalStateException} if this queue is empty.
     */
	int poll();
	
	/**
     * Retrieves, but does not remove, the head of this queue,
     * or returns {@code null} if this queue is empty.
     */
	int peek();
	
	
    /**
     * Returns <tt>true</tt> if this collection contains no elements.
     */
	boolean isEmpty();
}
