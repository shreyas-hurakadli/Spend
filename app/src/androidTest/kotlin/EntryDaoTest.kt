import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.spend.data.room.entry.Entry
import com.example.spend.data.room.entry.EntryDao
import com.example.spend.data.room.RoomDatabaseClass
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class EntryDaoTest {
    private lateinit var entryDao: EntryDao
    private lateinit var roomDatabaseClass: RoomDatabaseClass

    @Before
    fun create_db() {
        val context: Context = ApplicationProvider.getApplicationContext()

        roomDatabaseClass = Room.inMemoryDatabaseBuilder(context, RoomDatabaseClass::class.java)
            .allowMainThreadQueries()
            .build()

        entryDao = roomDatabaseClass.entryDao()
    }

    @After
    @Throws(IOException::class)
    fun close_db() {
        roomDatabaseClass.close()
    }

    private var entry1: Entry = Entry(1, "Canteen", "Food", 69.00, 1740911745724L)
    private var entry2: Entry = Entry(2, "Soap", "Essentials", 120.00, 1740911745724L)

    private suspend fun addEntryToDb(entry: Entry) {
        entryDao.insert(entry)
    }

    private suspend fun addTwoEntriesToDb() {
        addEntryToDb(entry1)
        addEntryToDb(entry2)
    }

    @Test
    @Throws(Exception::class)
    fun daoInsert_insertsEntryIntoDb() = runBlocking {
        addEntryToDb(entry1)
        val allEntries = entryDao.getAllEntries().first()
        assertEquals(allEntries[0], entry1)
    }

    @Test
    @Throws(Exception::class)
    fun daoGetAllEntries_returnsAllItemsFromDb() = runBlocking {
        addTwoEntriesToDb()
        val allEntries = entryDao.getAllEntries().first()
        assertEquals(allEntries[0], entry1)
        assertEquals(allEntries[1], entry2)
    }

    @Test
    @Throws(Exception::class)
    fun daoGetRecentEntries_returns5RecentEntriesFromDb() = runBlocking {
        addTwoEntriesToDb()
        val recentEntries = entryDao.getRecentEntries()
            .take(1)
            .toList()
            .flatten()
        assertEquals(recentEntries[1], entry1)
        assertEquals(recentEntries[0], entry2)
    }
}