package sapadvrtisrproj.ms.sapientia.ro.sapientiaadvertiserproject.Data;

public class Visited {
    static private int visitedCount;
    static public void newCalculation(){
        visitedCount=0;
    }
    static public int visitorsCalculation(){
        return ++visitedCount;
    }
    static public int getVisitedCount(){
        return visitedCount;
    }
}
