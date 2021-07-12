package com.example.simplephotoapp.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Dao
interface PhotoDao {

    @Query("select * from photoentity")
    fun getPhotos(): LiveData<List<PhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoEntity)

    @Query("select * from photoentity where id = :photoId")
    suspend fun isPhotoFavorite(photoId: String): PhotoEntity?

    @Delete
    suspend fun deletePhoto(photo: PhotoEntity)

    @Query("delete from photoentity")
    suspend fun clear()

}


@Database(entities = [PhotoEntity::class], version = 2)
abstract class PhotoDatabase: RoomDatabase() {
    abstract val photoDao: PhotoDao
}



private lateinit var INSTANCE: PhotoDatabase

fun getDatabase(context: Context): PhotoDatabase {
    synchronized(PhotoDatabase::class.java){
        if(!::INSTANCE.isInitialized){
            INSTANCE = Room.databaseBuilder(context.applicationContext,
            PhotoDatabase::class.java,
            "photos")
                .addMigrations(MIGRATION_1_2)
                .build()
        }
    }
    return INSTANCE
}

val MIGRATION_1_2 = object : Migration(1,2){
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE photoentity")
        database.execSQL("CREATE TABLE photoentity(" +
                "id TEXT PRIMARY KEY NOT NULL," +
                "photoUrl TEXT NOT NULL," +
                "creatorNickname TEXT NOT NULL," +
                "imageDescription TEXT," +
                "photoLikes INTEGER," +
                "photoDownloadUrl TEXT NOT NULL)")
    }

}
