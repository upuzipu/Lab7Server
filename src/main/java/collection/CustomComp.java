package collection;

import java.util.Comparator;

public class CustomComp implements Comparator<Car> {

    @Override
    public int compare(Car o1, Car o2) {
        if (o1 == null) {
            if (o2==null){return 0;}
            else{return -1;}
        }
        return o1.compareTo(o2);
    }
}
