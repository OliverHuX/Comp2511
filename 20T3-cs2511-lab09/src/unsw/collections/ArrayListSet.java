/**
 *
 */
package unsw.collections;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * An implementation of Set that uses an ArrayList to store the elements.
 *
 * @invariant All e in elements occur only once
 *
 * @author Robert Clifton-Everest
 *
 */
public class ArrayListSet<E> implements Set<E> {

    private ArrayList<E> elements;

    public ArrayListSet() {
        elements = new ArrayList<>();
    }

    @Override
    public void add(E e) {
        if (!elements.contains(e)) {
            elements.add(e);
        }
    }

    @Override
    public void remove(E e) {
        elements.remove(e);
    }

    @Override
    public boolean contains(Object e) {
        return elements.contains(e);
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public boolean subsetOf(Set<?> other) {
        if(other.size() < elements.size()) {
            return false;
        } else {
            for(E e: elements) {
                if(!other.contains(e)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public Iterator<E> iterator() {
        return elements.iterator();
    }

    @Override
    public Set<E> union(Set<? extends E> other) {
        Set<E> list = new ArrayListSet<>();
        for(E e: other) {
            if (!list.contains(e)) {
                list.add(e);
            }
        }
        for(E e: elements) {
            if (!list.contains(e)) {
                list.add(e);
            }
        }
        return list;
    }

    @Override
    public Set<E> intersection(Set<? extends E> other) {
        Set<E> list = new ArrayListSet<>();
        for(E e: other) {
            if(elements.contains(e)){
                list.add(e);
            }
        }
        return list;
    }

    /**
     * For this method, it should be possible to compare all other possible sets
     * for equality with this set. For example, if an ArrayListSet<Fruit> and a
     * LinkedListSet<Fruit> both contain the same elements they are equal.
     * Similarly, if a Set<Apple> contains the same elements as a Set<Fruit>
     * they are also equal.
     */
    @Override
    public boolean equals(Object other) {
        Set<?> set = (Set<?>) other;
        if(this == other) {
            return true;
        } else if(other == null) {
            return false;
        } else if(!(other instanceof Set<?>)) {
            return false;
        } else if(this.size() != set.size()) {
            return false;
        }
        Iterator<?> setIter = set.iterator();
        while(setIter.hasNext()) {
            if(!contains(setIter.next())) {
                return false;
            }
        }

        return true;
    }

}
