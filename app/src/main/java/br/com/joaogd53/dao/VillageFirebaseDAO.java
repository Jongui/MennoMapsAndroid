package br.com.joaogd53.dao;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Handle Village info from Firebase
 */

public class VillageFirebaseDAO extends AbstractFirebaseDAO implements ValueEventListener {

    private static VillageFirebaseDAO instance;

    private VillageFirebaseDAO() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference("dev/Village");
        this.databaseReference.addValueEventListener(this);
    }

    private static VillageFirebaseDAO getInstance() {
        if (instance == null) instance = new VillageFirebaseDAO();
        return instance;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
