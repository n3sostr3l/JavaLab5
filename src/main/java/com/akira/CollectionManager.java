package com.akira;
import com.akira.LabWork;
import java.util.Hashtable;

public class CollectionManager {
    static Hashtable<Integer, LabWork> labworks = new Hashtable<Integer, LabWork>();
    public static Hashtable<Integer, LabWork> add(LabWork lab){
        labworks.put(Integer.valueOf(labworks.size()), lab);
        return labworks;
    }

    public static Hashtable<Integer, LabWork> clear(){
        labworks.clear();
        return labworks;
    }

}