package br.com.joaogd53.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import br.com.joaogd53.model.Colony;

/**
 * DAO Interface to handle Colony data
 */

@Dao
public interface ColonyDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertColonies(Colony...colonies);
    @Query("SELECT * FROM Colony")
    Colony[] loadAllColonies();
}
