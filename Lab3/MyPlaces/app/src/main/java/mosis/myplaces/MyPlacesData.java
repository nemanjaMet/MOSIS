package mosis.myplaces;

import java.util.ArrayList;

/**
 * Created by Neca on 3.3.2016..
 */
public class MyPlacesData {

   private ArrayList<MyPlace> myPlaces;

    private MyPlacesData(){
        myPlaces = new ArrayList<MyPlace>();
       /* myPlaces.add(new MyPlace("Place A"));
        myPlaces.add(new MyPlace("Place B"));
        myPlaces.add(new MyPlace("Place C"));
        myPlaces.add(new MyPlace("Place D"));
        myPlaces.add(new MyPlace("Place E"));*/
    }

    private static class SingletonHolder {
        public static final MyPlacesData instance = new MyPlacesData();
    }

    public static MyPlacesData getIntance(){
        return SingletonHolder.instance;
    }

    public ArrayList<MyPlace> getMyPlaces()
    {
        return myPlaces;
    }

    public void addNewPlace(MyPlace place)
    {
        myPlaces.add(place);
    }

    public MyPlace getPlace(int index)
    {
        return myPlaces.get(index);
    }

    public void deletePlace(int index)
    {
        myPlaces.remove(index);
    }
}
