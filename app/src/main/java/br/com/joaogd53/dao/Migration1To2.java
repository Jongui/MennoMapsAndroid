package br.com.joaogd53.dao;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

/**
 * Migration class for 1 to 2 DB version
 */

public class Migration1To2 extends Migration {
    /**
     * Creates a new migration between {@code startVersion} and {@code endVersion}.
     *
     * @param startVersion The start version of the database.
     * @param endVersion   The end version of the database after this migration is applied.
     */
    public Migration1To2(int startVersion, int endVersion) {
        super(startVersion, endVersion);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.execSQL("ALTER TABLE Village "
                + " ADD COLUMN country TEXT");
    }
}
