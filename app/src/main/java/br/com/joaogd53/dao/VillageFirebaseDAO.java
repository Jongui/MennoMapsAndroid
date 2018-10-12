package br.com.joaogd53.dao;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import br.com.joaogd53.model.Village;

/**
 * Handle Village info from Firebase
 */

public class VillageFirebaseDAO extends AbstractFirebaseDAO implements ValueEventListener {

    private static VillageFirebaseDAO instance;
    private HashMap<Integer, Village> villageHashMap;

    public static VillageFirebaseDAO getInstance() {
        if (instance == null) instance = new VillageFirebaseDAO();
        return instance;
    }

    private VillageFirebaseDAO() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference("dev/Village");
        this.databaseReference.addValueEventListener(this);
        this.villageHashMap = new HashMap<>();
    }

    public Village findByFirebaseKey(int firebaseKey){
        return this.villageHashMap.get(firebaseKey);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot colonySnapshot : dataSnapshot.getChildren()) {
            Village village = Village.VillageBuilder.buildFromSnapshot(colonySnapshot);
            this.villageHashMap.put(village.getFirebaseKey(), village);
        }
        this.triggerFirebaseDAO();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

}
