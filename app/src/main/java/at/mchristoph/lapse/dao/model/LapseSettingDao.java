package at.mchristoph.lapse.dao.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import at.mchristoph.lapse.dao.model.LapseSetting;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "LAPSE_SETTING".
*/
public class LapseSettingDao extends AbstractDao<LapseSetting, Long> {

    public static final String TABLENAME = "LAPSE_SETTING";

    /**
     * Properties of entity LapseSetting.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property Description = new Property(2, String.class, "description", false, "DESCRIPTION");
        public final static Property Framerate = new Property(3, long.class, "framerate", false, "FRAMERATE");
        public final static Property Interval = new Property(4, long.class, "interval", false, "INTERVAL");
        public final static Property MovieTime = new Property(5, long.class, "movieTime", false, "MOVIE_TIME");
        public final static Property MovieTimeHours = new Property(6, int.class, "movieTimeHours", false, "MOVIE_TIME_HOURS");
        public final static Property MovieTimeMinutes = new Property(7, int.class, "movieTimeMinutes", false, "MOVIE_TIME_MINUTES");
        public final static Property MovieTimeSeconds = new Property(8, int.class, "movieTimeSeconds", false, "MOVIE_TIME_SECONDS");
        public final static Property Created = new Property(9, java.util.Date.class, "created", false, "CREATED");
    };


    public LapseSettingDao(DaoConfig config) {
        super(config);
    }
    
    public LapseSettingDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"LAPSE_SETTING\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"NAME\" TEXT NOT NULL ," + // 1: name
                "\"DESCRIPTION\" TEXT," + // 2: description
                "\"FRAMERATE\" INTEGER NOT NULL ," + // 3: framerate
                "\"INTERVAL\" INTEGER NOT NULL ," + // 4: interval
                "\"MOVIE_TIME\" INTEGER NOT NULL ," + // 5: movieTime
                "\"MOVIE_TIME_HOURS\" INTEGER NOT NULL ," + // 6: movieTimeHours
                "\"MOVIE_TIME_MINUTES\" INTEGER NOT NULL ," + // 7: movieTimeMinutes
                "\"MOVIE_TIME_SECONDS\" INTEGER NOT NULL ," + // 8: movieTimeSeconds
                "\"CREATED\" INTEGER NOT NULL );"); // 9: created
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"LAPSE_SETTING\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, LapseSetting entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getName());
 
        String description = entity.getDescription();
        if (description != null) {
            stmt.bindString(3, description);
        }
        stmt.bindLong(4, entity.getFramerate());
        stmt.bindLong(5, entity.getInterval());
        stmt.bindLong(6, entity.getMovieTime());
        stmt.bindLong(7, entity.getMovieTimeHours());
        stmt.bindLong(8, entity.getMovieTimeMinutes());
        stmt.bindLong(9, entity.getMovieTimeSeconds());
        stmt.bindLong(10, entity.getCreated().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public LapseSetting readEntity(Cursor cursor, int offset) {
        LapseSetting entity = new LapseSetting( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // name
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // description
            cursor.getLong(offset + 3), // framerate
            cursor.getLong(offset + 4), // interval
            cursor.getLong(offset + 5), // movieTime
            cursor.getInt(offset + 6), // movieTimeHours
            cursor.getInt(offset + 7), // movieTimeMinutes
            cursor.getInt(offset + 8), // movieTimeSeconds
            new java.util.Date(cursor.getLong(offset + 9)) // created
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, LapseSetting entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setName(cursor.getString(offset + 1));
        entity.setDescription(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setFramerate(cursor.getLong(offset + 3));
        entity.setInterval(cursor.getLong(offset + 4));
        entity.setMovieTime(cursor.getLong(offset + 5));
        entity.setMovieTimeHours(cursor.getInt(offset + 6));
        entity.setMovieTimeMinutes(cursor.getInt(offset + 7));
        entity.setMovieTimeSeconds(cursor.getInt(offset + 8));
        entity.setCreated(new java.util.Date(cursor.getLong(offset + 9)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(LapseSetting entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(LapseSetting entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
