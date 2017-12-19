package br.com.joaogd53.dao;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.joaogd53.model.Village;

/**
 * Handle Village info from Firebase
 */

public class VillageFirebaseDAO extends AbstractFirebaseDAO implements ValueEventListener {

    private static VillageFirebaseDAO instance;


    public static VillageFirebaseDAO getInstance() {
        if (instance == null) instance = new VillageFirebaseDAO();
        return instance;
    }

    private VillageFirebaseDAO() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference("dev/Village");
        this.databaseReference.addValueEventListener(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        List<Village> villages = new ArrayList<>();
        for (DataSnapshot colonySnapshot : dataSnapshot.getChildren()) {
            Village village = Village.VillageBuilder.buildFromSnapshot(colonySnapshot);
            villages.add(village);
        }
        this.triggerFirebaseDAO();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

}
