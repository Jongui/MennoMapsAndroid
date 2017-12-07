package br.com.joaogd53.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import br.com.joaogd53.dao.ColonyDAO;
import br.com.joaogd53.dao.VillageDAO;

/**
 * DataBase class for the app
 */

@Database(entities = {Colony.class, Village.class}, version = 4)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ColonyDAO colonyDAO();
    public abstract VillageDAO villageDAO();
}
