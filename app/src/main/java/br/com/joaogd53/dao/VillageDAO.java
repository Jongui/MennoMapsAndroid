package br.com.joaogd53.dao;

import android.arch.persistence.room.Dao;
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
    /*private static VillageDAO instance;
    private List<Village> villages;

    private VillageDAO(){
        this.villages = new ArrayList<>();
    }
    public static VillageDAO getInstance(){
        if (instance == null) instance = new VillageDAO();
        return instance;
    }

    public LatLng averagePosition(){
        LatLng ret = new LatLng(0, 0);
        double lat = 0;
        double lon = 0;
        for(Village village : this.villages){
            LatLng tmp = village.getPosition();
            lat += tmp.latitude;
            lon += tmp.longitude;
        }

        int size = this.villages.size();

        if (size != 0){
            lat = lat / size;
            lon = lon / size;
            ret = new LatLng(lat, lon);
        }

        return ret;
    }



    public Village getVillageAtIndex(int index){
        return this.villages.get(index);
    }
*/
}
