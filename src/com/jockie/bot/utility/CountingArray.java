package com.jockie.bot.utility;

import java.util.Arrays;

@SuppressWarnings("unchecked")
public class CountingArray<E> {
	
	private int elements = 0;
	private int size = 10;
	
	private transient Object[] object_array = new Object[this.size];
	private transient long[] count_array = new long[this.size];
	
	public int size() {
		return this.elements;
	}
	
	public E getMostPopularEntry() {
		int most_popular_index = -1;
		int most_popular_count = 0;
		
		for(int i = 0; i < this.count_array.length; i++) {
			if(this.count_array[i] > most_popular_count) {
				most_popular_index = i;
				most_popular_count = (int) this.count_array[i];
			}
		}
		
		if(most_popular_index != -1)
			return this.get(most_popular_index);
		else throw new IndexOutOfBoundsException("Array is empty");
	}
	
	public long getCount(E element) {
		int index = this.indexOf(element);
		if(index != -1)
			return this.count_array[index];
		return 0;
	}
	
	public long getCount(int index) {
		return this.count_array[index];
	}
	
	public E get(int index) {
		return (E) this.object_array[index];
	}
	
	public void add(E element) {
		if(this.contains(element)) {
			int index = this.indexOf(element);
			this.count_array[index] = this.count_array[index] + 1;
		}else{
			this.ensureCapacity();
			
			this.object_array[this.elements] = element;
			this.count_array[this.elements] = 1;
			
			this.elements = this.elements + 1;
		}
	}
	
    public void clear() {
        for(int i = 0; i < size; i++) {
        	this.object_array[i] = null;
            this.count_array[i] = -1;
        }
        
        this.elements = 0;
    }
	
	private boolean contains(Object object) {
		if(this.indexOf(object) >= 0) {
			return true;
		}
		return false;
	}
	
	private int indexOf(Object object) {
		if(object == null)
			return -1;
		
		for(int i = 0; i < this.object_array.length; i++) {
			if(object.equals(this.object_array[i])) {
				return i;
			}
		}
		
		return -1;
	}
	
	private void ensureCapacity() {
		if(this.elements + 1 >= this.size) {
			Object[] oldData = this.object_array;
			long[] oldCount = this.count_array;
			
			this.size = (this.size * 3)/2 + 1;
			
			this.object_array = Arrays.copyOf(oldData, this.size);
			this.count_array = Arrays.copyOf(oldCount, this.size);
		}
	}
}