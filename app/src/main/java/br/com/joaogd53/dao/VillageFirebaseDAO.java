package br.com.joaogd53.dao;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.joaogd53.model.AppDatabase;
import br.com.joaogd53.model.Village;

/**
 * Handle Village info from Firebase
 */

public class VillageFirebaseDAO extends AbstractFirebaseDAO implements ValueEventListener {

    private boolean updateSQLite;
    private Context context;

    private static VillageFirebaseDAO instance;


    public static VillageFirebaseDAO getInstance() {
        if (instance == null) instance = new VillageFirebaseDAO();
        return instance;
    }

    private VillageFirebaseDAO() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference("dev/Village");
        this.databaseReference.addValueEventListener(this);
        updateSQLite = false;
    }

    public void setUpdateSQLite(boolean updateSQLite) {
        this.updateSQLite = updateSQLite;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        List<Village> villages = new ArrayList<>();
        for (DataSnapshot colonySnapshot : dataSnapshot.getChildren()) {
            Village village = Village.VillageBuilder.buildFromSnapshot(colonySnapshot);
            villages.add(village);
        }
        if (updateSQLite && context != null) {
            Village[] villagesUpdate = new Village[villages.size()];
            for(int i = 0; i < villages.size(); i++){
                villagesUpdate[i] = villages.get(i);
            }
            AppDatabase appDatabase = Room.databaseBuilder(this.context, AppDatabase.class, "mennomaps-database.db").build();
            VillageDAO villageDAO = appDatabase.villageDAO();
            villageDAO.updateVillages(villagesUpdate);
            updateSQLite = false;
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
