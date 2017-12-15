package br.com.joaogd53.dao;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.joaogd53.model.Colony;

/**
 * Colony from Firebase
 */

public class ColonyFirebaseDAO extends AbstractFirebaseDAO implements ValueEventListener {

    private static ColonyFirebaseDAO instance;

    private ColonyFirebaseDAO(){
        this.databaseReference = FirebaseDatabase.getInstance().getReference("dev/Colony");
        this.databaseReference.addValueEventListener(this);
    }

    public static ColonyFirebaseDAO getInstance(){
        if(instance == null) instance = new ColonyFirebaseDAO();
        return instance;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot colonySnapshot: dataSnapshot.getChildren()) {
            Colony colony = Colony.ColonyBuilder.buildFromSnapshot(colonySnapshot);
        }

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
