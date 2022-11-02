package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    private lateinit var database: RemindersDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun saveRemindersAndGetById() = runBlockingTest {

        val remindersList = createFakeRepositoryList()
        remindersList.forEach {
            database.reminderDao().saveReminder(it)
        }

        val reminderLocal = remindersList.last()
        val reminderDatabase = database.reminderDao().getReminderById(reminderLocal.id)

        assertThat(reminderDatabase as ReminderDTO, notNullValue())
        assertThat(reminderDatabase.id, `is`(reminderLocal.id))
        assertThat(reminderDatabase.title, `is`(reminderLocal.title))
        assertThat(reminderDatabase.description, `is`(reminderLocal.description))
        assertThat(reminderDatabase.location, `is`(reminderLocal.location))
        assertThat(reminderDatabase.latitude, `is`(reminderLocal.latitude))
        assertThat(reminderDatabase.longitude, `is`(reminderLocal.longitude))
    }

    @Test
    fun saveRemindersAndGetReminders() = runBlockingTest {

        val remindersList = createFakeRepositoryList()
        remindersList.forEach {
            database.reminderDao().saveReminder(it)
        }

        val remindersListDatabase = database.reminderDao().getReminders()

        assertThat(remindersListDatabase, notNullValue())
        assertThat(remindersListDatabase[0], `is`(remindersList[0]))
        assertThat(remindersListDatabase[1], `is`(remindersList[1]))
        assertThat(remindersListDatabase[2], `is`(remindersList[2]))
        assertThat(remindersListDatabase.size, `is`(remindersList.size))
    }

    @Test
    fun saveRemindersAndDeleteAllReminders() = runBlockingTest {

        val remindersList = createFakeRepositoryList()
        remindersList.forEach {
            database.reminderDao().saveReminder(it)
        }

        database.reminderDao().deleteAllReminders()
        val remindersListDatabase = database.reminderDao().getReminders()

        assertThat(remindersListDatabase, null)
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