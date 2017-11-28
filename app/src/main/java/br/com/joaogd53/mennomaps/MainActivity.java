package br.com.joaogd53.mennomaps;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import br.com.joaogd53.dao.ColonyDAO;
import br.com.joaogd53.dao.Migration1To2;
import br.com.joaogd53.dao.VillageDAO;
import br.com.joaogd53.model.AppDatabase;
import br.com.joaogd53.model.Colony;
import br.com.joaogd53.model.Village;

public class MainActivity extends AppCompatActivity {


    private int currentFragment = 1;

    // Declare a variable for the cluster manager.
    //private HashMap<String, ClusterManager<Village>> mHashClusterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppDatabase appDatabase = Room.databaseBuilder(this, AppDatabase.class, "mennomaps-database.db")
                    .addMigrations(new Migration1To2(1,2)).build();
        new DataBaseAsyncTask(this.getFragmentManager(), appDatabase).execute();
    }

    private static class DataBaseAsyncTask extends AsyncTask<Void, Void, Void>{

        private FragmentManager mFragmentManager;
        private ColonyDAO mColonyDAO;
        private VillageDAO mVillageDAO;
        private AppDatabase mAppDatabase;

        private DataBaseAsyncTask(FragmentManager fragmentManager, AppDatabase appDatabase){
            this.mFragmentManager = fragmentManager;
            mAppDatabase = appDatabase;
            //mAppDatabase = Room.databaseBuilder(this.mActivity, AppDatabase.class, "mennomaps-database.db")
            //        .addMigrations(new Migration1To2(1,2)).build();
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
            String tag = "MAIN_STORES";
            ft.replace(R.id.container, f, tag).addToBackStack("control").commit();
            return null;
        }
    }

}
