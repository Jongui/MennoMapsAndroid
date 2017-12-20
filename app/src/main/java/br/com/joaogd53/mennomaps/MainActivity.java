package br.com.joaogd53.mennomaps;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.arch.persistence.room.Room;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import br.com.joaogd53.dao.ColonyDAO;
import br.com.joaogd53.dao.ColonyFirebaseDAO;
import br.com.joaogd53.dao.FirebaseDAO;
import br.com.joaogd53.dao.Migration1To2;
import br.com.joaogd53.dao.Migration2To3;
import br.com.joaogd53.dao.Migration3To4;
import br.com.joaogd53.dao.Migration4To5;
import br.com.joaogd53.dao.VillageDAO;
import br.com.joaogd53.dao.VillageFirebaseDAO;
import br.com.joaogd53.model.AppDatabase;
import br.com.joaogd53.model.Colony;
import br.com.joaogd53.model.Village;
import br.com.joaogd53.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    private int currentFragment = 1;
    private AppDatabase appDatabase;

    // Declare a variable for the cluster manager.
    //private HashMap<String, ClusterManager<Village>> mHashClusterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appDatabase = Room.databaseBuilder(this, AppDatabase.class, "mennomaps-database.db")
                .addMigrations(new Migration1To2(1, 2), new Migration2To3(2, 3), new Migration3To4(3, 4),
                        new Migration4To5(4, 5)).build();
        if (NetworkUtils.networkIsConnected(this)) {
            this.loadDataOnline();
        } else {
            new DataBaseAsyncTask(this.getFragmentManager(), appDatabase).execute();
        }

    }

    private void loadDataOnline() {
        ColonyFirebaseDAO.getInstance().addFirebaseDAO(new FirebaseDAO() {
            @Override
            public void atLoadFinished() {
                VillageFirebaseDAO villageFirebaseDAO = VillageFirebaseDAO.getInstance();
                villageFirebaseDAO.addFirebaseDAO(new FirebaseDAO() {
                    @Override
                    public void atLoadFinished() {
                        ColonyDAO colonyDAO = appDatabase.colonyDAO();
                        VillageDAO villageDAO = appDatabase.villageDAO();
                        new UpdateSQLiteAsyncTask(colonyDAO, villageDAO, MainActivity.this.getFragmentManager()).execute();
                    }
                });
            }
        });
    }

    private static class UpdateSQLiteAsyncTask extends AsyncTask<Void, Void, Void>{

        private ColonyDAO mColonyDAO;
        private VillageDAO mVillageDAO;
        private FragmentManager mFragmentManager;

        private UpdateSQLiteAsyncTask(ColonyDAO colonyDAO, VillageDAO villageDAO, FragmentManager fragmentManager){
            this.mColonyDAO = colonyDAO;
            this.mVillageDAO = villageDAO;
            this.mFragmentManager = fragmentManager;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            this.updInsColonies();
            List<Village> villages = Village.getVillages();
            Village[] villagesUpdate = new Village[villages.size()];
            for(int i = 0; i < villages.size(); i++){
                Village village = villages.get(i);
                try{
                    mVillageDAO.updateVillage(village);
                } catch (SQLiteConstraintException ex){
                    ex.printStackTrace();
                }
                villagesUpdate[i] = villages.get(i);
            }
            mVillageDAO.updateVillages(villagesUpdate);
            Fragment f = new MapsFragment();
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            mFragmentManager.popBackStack("control", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            String tag = "MAPS_FRAGMENT";
            ft.replace(R.id.container, f, tag).addToBackStack("control").commit();
            return null;
        }

        private void updInsColonies() {
            List<Colony> colsMemory = Colony.getColonies();
//            int lastId = mColonyDAO.lastIndex();
            Colony[] colsSQLite = mColonyDAO.loadAllColonies();
            List<Colony> coloniesUpdate = new ArrayList<>();
            List<Colony> coloniesInsert = new ArrayList<>();
            for(int i = 0; i < colsMemory.size(); i++){
                Colony colonyMemory = colsMemory.get(i);
                boolean updCol = false;
                for(Colony colonySQLite : colsSQLite){
                    if(colonyMemory.getIdColony() == colonySQLite.getIdColony()){
                        updCol = true;
                        break;
                    }
                }
                if(updCol){
                    coloniesUpdate.add(colonyMemory);
                } else {
                    coloniesInsert.add(colonyMemory);
                }
            }
            Colony[] sqlite = new Colony[coloniesInsert.size()];
            for(int i = 0; i < coloniesInsert.size(); i++){
                sqlite[i] = coloniesInsert.get(i);
            }
            mColonyDAO.insertColonies(sqlite);
            sqlite = new Colony[coloniesUpdate.size()];
            for(int i = 0; i < coloniesUpdate.size(); i++){
                sqlite[i] = coloniesUpdate.get(i);
            }
            mColonyDAO.updateColonies(sqlite);
        }
    }

    private static class DataBaseAsyncTask extends AsyncTask<Void, Void, Void> {

        private FragmentManager mFragmentManager;
        private ColonyDAO mColonyDAO;
        private VillageDAO mVillageDAO;
        private AppDatabase mAppDatabase;

        private DataBaseAsyncTask(FragmentManager fragmentManager, AppDatabase appDatabase) {
            this.mFragmentManager = fragmentManager;
            mAppDatabase = appDatabase;
            mColonyDAO = mAppDatabase.colonyDAO();
            mVillageDAO = mAppDatabase.villageDAO();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Colony[] cols = mColonyDAO.loadAllColonies();
            Village[] vils = mVillageDAO.loadAllVillages();
            Colony.ColonyBuilder.buildFromArray(cols);
            Village.VillageBuilder.buildFromArray(vils);
            Fragment f = new MapsFragment();
            FragmentTransaction ft = this.mFragmentManager.beginTransaction();
            this.mFragmentManager.popBackStack("control", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            String tag = "MAPS_FRAGMENT";
            ft.replace(R.id.container, f, tag).addToBackStack("control").commit();
            return null;
        }
    }

}