package br.com.joaogd53.dao;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.joaogd53.model.Colony;
import br.com.joaogd53.model.Village;

/**
 * Handle Village info from Firebase
 */

public class VillageFirebaseDAO extends AbstractFirebaseDAO implements ValueEventListener {

    private static VillageFirebaseDAO instance;
    private SparseArray<Village> villageHashMap;

    public static VillageFirebaseDAO getInstance() {
        if (instance == null) instance = new VillageFirebaseDAO();
        return instance;
    }

    private VillageFirebaseDAO() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference("dev/Village");
        this.databaseReference.addValueEventListener(this);
        this.villageHashMap = new SparseArray<>();
    }

    public Village findByFirebaseKey(int firebaseKey){
        return this.villageHashMap.get(firebaseKey);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        for (DataSnapshot villageSnapshot : dataSnapshot.getChildren()) {
            int key = Integer.valueOf(villageSnapshot.getKey());
            Village village = this.villageHashMap.get(key);
            if(village == null) {
                village = Village.VillageBuilder.buildFromSnapshot(villageSnapshot);
                this.villageHashMap.put(village.getFirebaseKey(), village);
            } else {
                village.setName(villageSnapshot.child("Name").getValue().toString());
                village.setColonyGroup(villageSnapshot.child("Kolonie").getValue().toString());
                if(village.getColonyGroup().equals("")){
                    village.setColonyGroup("No name");
                }
                village.setCountry(villageSnapshot.child("Land").getValue().toString());
                Colony colony = Colony.ColonyBuilder.findColonyByName(village.getColonyGroup());
                if (colony != null) {
                    village.setHueColor(colony.getColor());
                    village.setIdColony(colony.getIdColony());
                } else {
                    village.setHueColor(BitmapDescriptorFactory.HUE_RED);
                }
                village.setLatitude(Double.valueOf(villageSnapshot.child("Latitude").getValue().toString()));
                village.setLongitude(Double.valueOf(villageSnapshot.child("Longitude").getValue().toString()));
                village.setSource(villageSnapshot.child("Source").getValue().toString());
            }
        }
        this.triggerFirebaseDAO();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

}
