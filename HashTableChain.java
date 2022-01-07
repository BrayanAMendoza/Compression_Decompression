import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class HashTableChain<K,V> implements KWHashMap<K,V>{
    public static class Entry<K,V>{
        private final K key;
        private V value;
        public Entry(K key, V value){
            this.key = key;
            this.value = value;
        }
        public K getKey(){
            return key;
        }

        public V getValue(){
            return value;
        }

        public V setValue(V val){
            V oldVal = value;
            value = val;
            return oldVal;

        }
        public String toString(){
            return key.toString() + "=" + value.toString();
        }


    }

    private LinkedList<Entry<K,V>>[] table;
    private int numKeys;
    private int rehashCount = 0;
    private static final int CAPACITY = 101;
    private static final double LOAD_THRESHHOLD = 3.0;


    public HashTableChain(){
        table = new LinkedList[CAPACITY];
        numKeys = 0;

    }

    public HashTableChain(int cap){
        table = new LinkedList[cap];
        numKeys = 0;
    }

    public V get(Object Key){
        int index = Key.hashCode() % table.length;

        if (index < 0){
            index += table.length;

        }
        if (table[index] == null){
            return null;
        }

        for (Entry<K,V> nextItem: table[index]){
            if(nextItem.getKey().equals(Key)){
                return nextItem.getValue();
            }
        }
        return null;
    }
    
    public V put(K key, V value){
        int index = key.hashCode()%table.length;
        if(index<0){
            index += table.length;
        }
        if(table[index]==null){
            table[index] = new LinkedList<>();
        }

        for (Entry<K,V> nextItem:table[index]){
            if((nextItem.getKey().equals(key))){
                V oldVal = nextItem.getValue();
                nextItem.setValue(value);
                return oldVal;
            }
        }

        table[index].addFirst(new Entry<>(key,value));
        numKeys++;
        if(numKeys>(LOAD_THRESHHOLD*table.length)){
            rehash();
        }
        return null;
    }

    public V remove(Object key){ //  variables for our function.
        int index = key.hashCode() % table.length;
        Iterator<Entry<K,V>> iter = table[index].iterator();
        Entry<K,V> nextItem;


        if (index < 0){
            index += table.length;
        }
        if (table[index] == null){
            return null;    
        }
        while(iter.hasNext()){ 
            nextItem = iter.next();
            if (nextItem.getKey().equals(key)){ 
                V oldVal = nextItem.getValue();
                iter.remove();
                numKeys--;
                if (table[index].size() == 0){// List is empty in our hash table
                    table[index] = null;
                }
                return oldVal;
            }
        }
        return null;
    }
    

    private void rehash()
    { // Declared variables for our function.
        Iterator<Entry<K,V>> iter;
        // Save a reference to oldTable.
        LinkedList<Entry<K,V>>[] oldTable = table;
        // Double capacity of this table.
        int capacity = (2 * oldTable.length) + 1;
        // make sure table size is prime.
        if (isPrime(capacity) == true){
            table = new LinkedList[capacity];
        }
        else{
            for(int i = 0; i < capacity; i++)
            {
                if(isPrime(capacity+i) == true){
                    table = new LinkedList[capacity +i];
                }
            }
        }
        //reinsert all items in oldTable into expanded table.
        numKeys = 0;
        for(int i = 0; i < oldTable.length;i++)
        {
            if(oldTable[i] != null)
            {
                for(Entry<K,V> nextItem : oldTable[i])
                {
                    put(nextItem.getKey(), nextItem.getValue());
                }
            }
        }
        rehashCount++;//increment our rehashCount.
    }
    public int size(){
        return numKeys;
    }

    public boolean isEmpty(){
        if (numKeys == 0 ){
            return true;
        }
        else{
            return false;
        }
    }

    public static boolean isPrime(int num){
        if(num <= 1){
            return false;
        }
        for(int i = 2; i <= Math.sqrt(num); i++){
            if (num% i == 0){
                return false;
            }
        }
        return true;
    }

    public String toString(){
        String result = "";
        for (int i = 0; i<table.length; i++){
            if(table[i] != null){
                Iterator<Entry<K,V>> iter = table[i].iterator();
                Entry<K,V>nextItem;
                while(iter.hasNext()){
                    nextItem = iter.next();
                    result += (nextItem.getKey() + "=" + nextItem.getValue() + "\n");

                }
            }
        }
        return result;
    }

    public int rehashCount(){
        return rehashCount;
    }

}
