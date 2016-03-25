package mosis.myplaces;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Neca on 3.3.2016..
 */
public class MyPlacesData {

   private ArrayList<MyPlace> myPlaces;

    MyPlacesDBAdapter dbAdapter;
    private MyPlacesData(){
        myPlaces = new ArrayList<MyPlace>();
       /* myPlaces.add(new MyPlace("Place A"));
        myPlaces.add(new MyPlace("Place B"));
        myPlaces.add(new MyPlace("Place C"));
        myPlaces.add(new MyPlace("Place D"));
        myPlaces.add(new MyPlace("Place E"));*/

        // Lab 4 --- rad sa bazom ---
        dbAdapter = new MyPlacesDBAdapter(MyPlacesApplication.getContext());
        dbAdapter.open();
        this.myPlaces = dbAdapter.getAllEntries();
        // Dodat ifCheck kako ne bi pucala aplikacija, ako ne postoji baza(nije kreirana), posto tada getAllEntries vraca null
        if (this.myPlaces == null)
        {
            myPlaces = new ArrayList<MyPlace>();
        }
        dbAdapter.close();
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
        //myPlaces.add(place); // ! Baca exception ! Zato sto nije napravljen objekat myPlaces (null), iako ga konstruktor napravio -> ispravljeno u konstruktoru
        // --- Lab4 ---
        dbAdapter.open();
        long ID = dbAdapter.insertEntry(place);
        dbAdapter.close();
        // Znaci ako je uspesno unet u bazi onda ga prikazi korisniku;; Zbog duplikata koji se ne pamte u bazi
        if (ID > -1)
            myPlaces.add(place);
       /* else
            Toast.makeText(MyPlacesApplication.getContext(), "New place not added!", Toast.LENGTH_SHORT).show();*/
        place.setID(ID);
    }

    public MyPlace getPlace(int index)
    {
        return myPlaces.get(index);
    }

    public void deletePlace(int index)
    {
        //myPlaces.remove(index);
        // --- Lab4 ---
        MyPlace place = myPlaces.remove(index);
        dbAdapter.open();
        boolean success = dbAdapter.removeEntry(place.getID());
        dbAdapter.close();
    }

    public void updatePlace(MyPlace place)
    {
        dbAdapter.open();
        dbAdapter.updateEntry(place.getID(), place);
        dbAdapter.close();
    }
}
