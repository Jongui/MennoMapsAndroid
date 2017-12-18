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

    private static boolean updateSQLite = false;
    private AppDatabase appDatabase;

    private static VillageFirebaseDAO instance;


    public static VillageFirebaseDAO getInstance() {
        if (instance == null) instance = new VillageFirebaseDAO();
        return instance;
    }

    private VillageFirebaseDAO() {

    }

    public static void setUpdateSQLite(boolean newValue) {
        updateSQLite = newValue;
    }

    public void init(Context context){
        this.databaseReference = FirebaseDatabase.getInstance().getReference("dev/Village");
        this.databaseReference.addValueEventListener(this);
        appDatabase = Room.databaseBuilder(context, AppDatabase.class, "mennomaps-database.db")
                .addMigrations(new Migration1To2(1,2), new Migration2To3(2,3), new Migration3To4(3,4),
                        new Migration4To5(4,5)).build();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        List<Village> villages = new ArrayList<>();
        for (DataSnapshot colonySnapshot : dataSnapshot.getChildren()) {
            Village village = Village.VillageBuilder.buildFromSnapshot(colonySnapshot);
            villages.add(village);
        }
        if (updateSQLite && appDatabase != null) {
            Village[] villagesUpdate = new Village[villages.size()];
            for(int i = 0; i < villages.size(); i++){
                villagesUpdate[i] = villages.get(i);
            }
            VillageDAO villageDAO = appDatabase.villageDAO();
            villageDAO.updateVillages(villagesUpdate);
            updateSQLite = false;
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

}
