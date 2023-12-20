package designpatterns.framework;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeSet;

/*
 *http://stackoverflow.com/questions/35961157/how-to-create-combinations-of-values-in-java
 **/


public class CombinationsIterator implements Iterator<String[]>{
    // Immutable fields
    private final int combinationLength;//get the length of the combination
    private final Object[][] values;
    private final int[] maxIndexes;

    // Mutable fields
    private final int[] currentIndexes;
    private boolean hasNext;
    
    //store the order of keys
    private ArrayList<String> keyOrder;
    
    public CombinationsIterator(final Map<String,Object[]> map) {
    	
        combinationLength = map.size();
        
        values = new Object[combinationLength][];
        
        maxIndexes = new int[combinationLength];
        
        currentIndexes = new int[combinationLength];

        keyOrder = new ArrayList<>();
        
        if (combinationLength == 0) {
            hasNext = false;
            return;
        }

        hasNext = true;

        // Reorganize the map to array.
        // Map is not actually needed and would unnecessarily complicate the algorithm.
        int valuesIndex = 0;
        for (final String key : new TreeSet<>(map.keySet())) {
            values[valuesIndex++] = map.get(key);
            keyOrder.add(key);
        }

        // Fill in the arrays of max indexes and current indexes.
        for (int i = 0; i < combinationLength; ++i) {
            if (values[i].length == 0) {
                // Set hasNext to false if at least one of the value-arrays is empty.
                // Stop the loop as the behavior of the iterator is already defined in this case:
                // the iterator will just return no combinations.
                hasNext = false;
                return;
            }

            maxIndexes[i] = values[i].length - 1;
            currentIndexes[i] = 0;
        }
    }
    
    public ArrayList<String> getKeyOrder()
    {
    	return keyOrder;
    }
    
    @Override
    public boolean hasNext() {
        return hasNext;
    }


    public Object[] Next() {
        if (!hasNext) {
            throw new NoSuchElementException("No more combinations are available");
        }
        final Object[] combination = getCombinationByCurrentIndexes();
        nextIndexesCombination();
        return combination;
    }

    private Object[] getCombinationByCurrentIndexes() {
        final Object[] combination = new Object[combinationLength];
        for (int i = 0; i < combinationLength; ++i) {
            combination[i] = values[i][currentIndexes[i]];
        }
        return combination;
    }
    
    private void nextIndexesCombination() {
        // A slightly modified "increment number by one" algorithm.

        // This loop seems more natural, but it would return combinations in a different order than in your example:
//      for (int i = 0; i < combinationLength; ++i) {

        // This loop returns combinations in the order which matches your example:
        for (int i = combinationLength - 1; i >= 0; --i) {
            if (currentIndexes[i] < maxIndexes[i]) {
                // Increment the current index
                ++currentIndexes[i];
                return;
            } else {
                // Current index at max: 
                // reset it to zero and "carry" to the next index
                currentIndexes[i] = 0;
            }
        }
        // If we are here, then all current indexes are at max, and there are no more combinations
        hasNext = false;
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove operation is not supported");
    }

	public String[] next() {
		// TODO Auto-generated method stub
		return null;
	}
    
    
}
