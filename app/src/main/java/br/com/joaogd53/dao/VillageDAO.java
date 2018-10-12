package br.com.joaogd53.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import br.com.joaogd53.model.Village;

/**
 * DAO class for Villages
 */

@Dao
public interface VillageDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertVillages(Village...villages);
    @Query("SELECT * FROM Village")
    Village[] loadAllVillages();
    @Update
    void updateVillages(Village...villages);
    @Update
    void updateVillage(Village village);
    @Delete
    void deleteVillages(Village...villages);
    @Query("SELECT * FROM Village WHERE idVillage = :id LIMIT 1")
    Village findById(int id);
}
