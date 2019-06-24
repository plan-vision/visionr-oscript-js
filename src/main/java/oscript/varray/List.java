package oscript.varray;

import java.util.Collection;
import java.util.LinkedList;
import java.util.ListIterator;

import server.ValueConvertor;

/**
 * Implementation of list in oscript.
 *
 * @author  plan-vision
 * @version $LastChangedRevision: 11162 $
 * @date 	$LastChangedDate: 2010-11-29 19:18:39 +0200 (Mo, 29 Nov 2010) $
 * @project VisionR Server 
 */
public class List implements java.util.List {	
	private LinkedList list=null;
	
	public List() {
		list = new LinkedList();
	}
	public List(Collection col) {
		list = new LinkedList(col);
	}
	
	public Map toMap() {
		if (isEmpty())
			return new Map();
		return new Map(list);
	}

	public Vector toVector() {
		if (list == null)
			return new Vector();
		return new Vector(list);
	}
	
	public boolean add(Object o) {
		if (list == null)
			list = new LinkedList();
		return list.add(o);
	}
	
	public Object first() {
		if (list == null)
			return null;
		return ValueConvertor.convert(list.getFirst());
	}
	
	public Object last() {
		if (list == null)
			return null;
		return ValueConvertor.convert(list.getLast());
	}
	
	public Object get(int position) {
		return ValueConvertor.convert(list.get(position));
	}
	
	public IteratorWrapper iterator() {
		return new IteratorWrapper(list.iterator());
	}
	
	public int size() {		
		return list.size();
	}
	public boolean isEmpty() {
		return list.isEmpty();
	}
	public boolean contains(Object o) {
		return list.contains(o);
	}
	public Object[] toArray() {
		return list.toArray();
	}
	public Object[] toArray(Object[] a) {
		return list.toArray(a);
	}
	public boolean remove(Object o) {
		return list.remove(o);
	}
	public boolean containsAll(Collection c) {
		return list.containsAll(c);
	}
	public boolean addAll(Collection c) {
		return list.addAll(c);
	}
	public boolean addAll(int index, Collection c) {
		return list.addAll(index,c);
	}
	public boolean removeAll(Collection c) {
		return list.removeAll(c);
	}
	public boolean retainAll(Collection c) {
		return list.retainAll(c);
	}
	public void clear() {
		list.clear();
	}
	public Object set(int index, Object element) {
		return list.set(index,element);
	}
	public void add(int index, Object element) {
		list.add(index,element);
	}
	public Object remove(int index) {
		return list.remove(index);
	}
	public int indexOf(Object o) {
		return list.indexOf(o);
	}
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}
	public ListIterator listIterator() {
		return list.listIterator();
	}
	public ListIterator listIterator(int index) {
		return list.listIterator(index);
	}
	public java.util.List subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex,toIndex);
	}
}
