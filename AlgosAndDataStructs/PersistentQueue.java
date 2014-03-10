import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * 
 * @author Samuel Neugber
 * 
 * Toy example about mutability.
 *
 * This queue improves performance by only copying the underlying list of data ON DEMAND.
 * 
 * Each persistent queue object stores whether some data has been appended to it in the past.
 * If this is the case and a new enqueue() happens, it copies the array and creates a new
 * PersistentQueue object with it.
 * Otherwise it simply appends the new data element to the end of the list,
 * and passes on a reference to the list of data to a new PersistentQueue object.
 * 
 * => This way enqueuing should be a constant operation in most cases, and is at worst just as "slow" as previously.
 * 
 * Since we share a datastructure now, we can just dequeue by creating a new
 * PersistentQueue which points to the next element up in the list as its first element.
 * 
 * 
 * @param <E> The type of value stored in this queue
 */

public class PersistentQueue<E> {

	private boolean hasNext;
	private ArrayList<E> queue;
	private int myFirst;

	public PersistentQueue() {
		queue = new ArrayList<E>();
		myFirst = 0;
	}

	private PersistentQueue(ArrayList<E> queue, int first) {
		this.queue = queue;
		this.hasNext = false;
		this.myFirst = first;
	}


	// Runtime often constant, otherwise no worse than before (i.e. O(n))
	public PersistentQueue<E> enqueue(E e) {

		if(e == null) throw new IllegalArgumentException();

		PersistentQueue<E> out;
		if(hasNext) {
			ArrayList<E> copy = new ArrayList<E>(queue.subList(myFirst, queue.size()));
			out = new PersistentQueue<E>(copy,0);
		} else {
			queue.add(e);
			hasNext = true;
			out = new PersistentQueue<E>(queue,myFirst);
		}
		return out;
	}


	public E peek() {
		if(myFirst == queue.size()) throw new NoSuchElementException();
		return queue.get(myFirst);
	}

	// Runtime is constant! O(1)
	public PersistentQueue<E> dequeue() {
		if(myFirst == queue.size()) throw new NoSuchElementException();

		return new PersistentQueue<E>(queue,myFirst+1);
	}

	public int size() {
		return queue.size()-myFirst;
	}
}

