package com.udacity.project4.locationreminders.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    private lateinit var reminderDao: RemindersDao

    private lateinit var database: RemindersDatabase

    private lateinit var remindersLocalRepository: RemindersLocalRepository

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()

        remindersLocalRepository = RemindersLocalRepository(reminderDao, Dispatchers.Main)
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun saveRemindersAndGetById() = runBlocking {
        val remindersList = createFakeRepositoryList()
        remindersList.forEach {
            remindersLocalRepository.saveReminder(it)
        }

        val reminderLocal = remindersList.last()
        val reminderDatabase = remindersLocalRepository.getReminder(reminderLocal.id)

        assertThat(reminderDatabase as ReminderDTO, notNullValue())
        assertThat(reminderDatabase.id, `is`(reminderLocal.id))
        assertThat(reminderDatabase.title, `is`(reminderLocal.title))
        assertThat(reminderDatabase.description, `is`(reminderLocal.description))
        assertThat(reminderDatabase.location, `is`(reminderLocal.location))
        assertThat(reminderDatabase.latitude, `is`(reminderLocal.latitude))
        assertThat(reminderDatabase.longitude, `is`(reminderLocal.longitude))
    }

    private fun createFakeRepositoryList(): MutableList<ReminderDTO> {
        return mutableListOf(
            ReminderDTO(
                title = "title1",
                description = "description1",
                location = "location1",
                latitude = 1.1,
                longitude = 1.2
            ),
            ReminderDTO(
                title = "title2",
                description = "description2",
                location = "location2",
                latitude = 2.1,
                longitude = 2.2
            ),
            ReminderDTO(
                title = "title3",
                description = "description3",
                location = "location3",
                latitude = 3.1,
                longitude = 3.2
            )
        )
    }
}