package br.com.joaogd53.dao;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for Firebase Database connection
 */

public class AbstractFirebaseDAO {
    protected DatabaseReference databaseReference;
    protected List<FirebaseDAO> firebaseDAOList = new ArrayList<>();

    public void addFirebaseDAO(FirebaseDAO firebaseDAO){
        firebaseDAOList.add(firebaseDAO);
    }

    protected void triggerFirebaseDAO(){
        for(FirebaseDAO fb : firebaseDAOList){
            fb.atLoadFinished();
        }
    }

}
