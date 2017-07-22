package algo;

public class BoundedIntQueue implements IntQueue {

	private final int[] q;
	private final int capacity;
	
	private int head;
	private int tail;

	public BoundedIntQueue(int capacity) {
		this.q = new int[capacity];
		this.capacity = capacity; 
		this.head = 0;
		this.tail = 0;
	}

	@Override
	public boolean add(int e) {
		q[tail++] = e;
		if (tail == capacity) tail = 0;
		if (head == tail) 
			throw new IllegalStateException("Queue capacity of "+q.length+" reached");
		return true;
	}

	@Override
	public int poll() {
		if (isEmpty()) 
			throw new IllegalStateException("Queue is empty");
		int result = q[head++];
		if (head == capacity) head = 0;
		return result;
	}

	@Override
	public int peek() {
		return q[head];
	}

	@Override
	public boolean isEmpty() {
		return head == tail;
	}
}