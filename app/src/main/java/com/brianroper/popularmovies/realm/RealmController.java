package com.brianroper.popularmovies.realm;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import com.brianroper.popularmovies.model.Favorite;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by brianroper on 10/12/16.
 */
public class RealmController {

    private static RealmController instance;
    private final Realm realm;

    public RealmController(Application application){
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment){
        if(instance == null){
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmController with(Activity activity){
        if(instance == null){
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application){
        if(instance == null){
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController getInstance(){
        return instance;
    }

    public Realm getRealm(){
        return realm;
    }

    /*public void refresh(){
        realm.refresh();
    }*/

    /*public void clearAll(){
        realm.beginTransaction();
        realm.clear(Favorite.class);
        realm.commitTransaction();
    }*/

    public RealmResults<Favorite> getFavorites(){
        return realm.where(Favorite.class).findAll();
    }

    public Favorite getFavorite(String id){
        return realm.where(Favorite.class).equalTo("id", id).findFirst();
    }

   /* public boolean hasFavorites(){
        return !realm.allObjects(Favorite.class).isEmpty();
    }*/
}
