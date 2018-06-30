package com.dr.test.entity;

import java.util.HashMap;
import java.util.Map;

//Least Recently Used (LRU) cache
public class LRUCache<K, V> {
	class Node {
		private K key;
		private V value;
		private Node pre;
		private Node next;
		public Node(K key, V value) {
			this.key = key;
			this.value = value;
		}
	}
	class MyDeque {
		private Node head;
		private Node tail;

		public Node remove(Node node) {
			//O(1) time
			if (node == null) return null;
			if (node.pre != null) {
				node.pre.next = node.next;
			} else {
				//head
				head = node.next;
			}

			if (node.next != null) {
				node.next.pre = node.pre;
			} else {
				//tail
				tail = node.pre;
			}
			//cut the link! very important for deleting!
			node.next = null;
			node.pre = null;
			return node;
		}

		//add to the tail
		public void offer(Node node) {
			//O(1) time
			if (tail == null) {
				tail = node;
				head = tail;
			} else {
				tail.next = node;
				node.pre = tail;
				tail = node;
			}
		}

		//poll from the head
		public Node poll() {
			//O(1) time
			return remove(head);
		}

		public void clear() {
			head = null;
			tail = null;
		}
	}

	private int capacity;
	Map<K, Node> map = new HashMap();
	MyDeque deque = new MyDeque();

	public LRUCache(int capacity) {
		this.capacity = capacity;
	}

	public V get(K key) {
		Node node = map.get(key);

		if (node == null) {
			return null;
		}

		deque.offer(deque.remove(node));
		return node.value;
	}

	public boolean containsKey(K key) {
		return map.containsKey(key);
	}

	public void put(K key, V value) {
		if(capacity <= 0) {
			return;
		}

		Node node = map.get(key);

		if (node != null) {
			deque.offer(deque.remove(node));
			node.value = value;
			return;
		}

		if(map.size() >= capacity) {
			Node deleted = deque.poll();
			map.remove(deleted.key);
		}
		Node n = new Node(key, value);
		deque.offer(n);
		map.put(key, n);
	}

	public void clear() {
		map.clear();
		deque.clear();
	}
}
