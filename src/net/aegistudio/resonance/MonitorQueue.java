package net.aegistudio.resonance;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * A monitor which serves as a blocked queue. When the queue is empty, the thread
 * will be blocked by calling the next method.
 * @author aegistudio
 *
 * @param <T>
 */

public class MonitorQueue<T>
{
	private final Queue<T> queue;
	
	public MonitorQueue()
	{
		this(new ArrayDeque<T>());
	}
	
	public MonitorQueue(Queue<T> queue)
	{
		this.queue = queue;
	}
	
	public synchronized T next()
	{
		if(this.queue.isEmpty())
			try
			{
				wait();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		return this.queue.remove();
	}
	
	public synchronized void push(T object)
	{
		this.queue.add(object);
		notify();
	}
	
	public synchronized void clear()
	{
		this.queue.clear();
	}
}
