package com.udacity.project4.locationreminders.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import junit.framework.TestCase
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

    private lateinit var database: RemindersDatabase

    private lateinit var remindersLocalRepository: RemindersLocalRepository

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            RemindersDatabase::class.java
        ).build()

        remindersLocalRepository =
            RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
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
        val reminderDatabase =
            remindersLocalRepository.getReminder(reminderLocal.id) as Result.Success<ReminderDTO>
        val reminderDatabaseDto = reminderDatabase.data

        assertThat(reminderDatabaseDto, notNullValue())
        assertThat(reminderDatabaseDto.id, `is`(reminderLocal.id))
        assertThat(reminderDatabaseDto.title, `is`(reminderLocal.title))
        assertThat(reminderDatabaseDto.description, `is`(reminderLocal.description))
        assertThat(reminderDatabaseDto.location, `is`(reminderLocal.location))
        assertThat(reminderDatabaseDto.latitude, `is`(reminderLocal.latitude))
        assertThat(reminderDatabaseDto.longitude, `is`(reminderLocal.longitude))
    }

    @Test
    fun whenSaveRemindersAndGetById_thenGetRminderErrorMessage() = runBlocking {
        // GIVEN
        val remindersList = createFakeRepositoryList()
        val errorMessage = "Reminder not found!"

        // WHEN
        val reminderDatabaseError = remindersLocalRepository.getReminder(
            remindersList.last().id
        ) as Result.Error

        // THEN
        assertThat(reminderDatabaseError, notNullValue())
        assertThat(reminderDatabaseError.message, `is`(errorMessage))
    }

    @Test
    fun saveRemindersAndGetReminders() = runBlocking {
        val remindersList = createFakeRepositoryList()
        remindersList.forEach {
            remindersLocalRepository.saveReminder(it)
        }

        val remindersListDatabase =
            remindersLocalRepository.getReminders() as Result.Success<List<ReminderDTO>>
        val remindersListDatabaseDto = remindersListDatabase.data

        assertThat(remindersListDatabaseDto, notNullValue())
        assertThat(remindersListDatabaseDto[0], `is`(remindersList[0]))
        assertThat(remindersListDatabaseDto[1], `is`(remindersList[1]))
        assertThat(remindersListDatabaseDto[2], `is`(remindersList[2]))
        assertThat(remindersListDatabaseDto.size, `is`(remindersList.size))
    }

    @Test
    fun saveRemindersAndDeleteAllReminders() = runBlocking {
        val remindersList = createFakeRepositoryList()
        remindersList.forEach {
            remindersLocalRepository.saveReminder(it)
        }

        remindersLocalRepository.deleteAllReminders()
        val remindersListDatabase =
            remindersLocalRepository.getReminders() as Result.Success<List<ReminderDTO>>

        TestCase.assertEquals(remindersListDatabase.data, emptyList<ReminderDTO>())
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