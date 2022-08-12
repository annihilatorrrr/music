package com.zionhuang.music.db.daos

import androidx.paging.PagingSource
import androidx.room.*
import com.zionhuang.music.db.entities.Album
import com.zionhuang.music.db.entities.AlbumEntity
import com.zionhuang.music.db.entities.SongAlbumMap

@Dao
interface AlbumDao {
    @Transaction
    @Query("SELECT *, (SELECT COUNT(*) FROM song_album_map WHERE albumId = album.id) AS songCount FROM album")
    fun getAllAlbumsAsPagingSource(): PagingSource<Int, Album>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(album: AlbumEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(songAlbumMap: SongAlbumMap): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(songAlbumMaps: List<SongAlbumMap>): List<Long>

    @Update
    fun update(songAlbumMaps: List<SongAlbumMap>)

    suspend fun upsert(songAlbumMaps: List<SongAlbumMap>) {
        insert(songAlbumMaps)
            .withIndex()
            .mapNotNull { if (it.value == -1L) songAlbumMaps[it.index] else null }
            .let { insert(it) }
    }
}