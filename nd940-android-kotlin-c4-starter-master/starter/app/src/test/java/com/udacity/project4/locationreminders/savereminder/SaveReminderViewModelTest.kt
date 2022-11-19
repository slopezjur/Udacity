package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.ResultState
import com.udacity.project4.locationreminders.data.local.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.util.MainCoroutineRule
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    private lateinit var reminderDataSource: FakeDataSource

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Roboelectric works "fine" with SDK 29
    @Config(sdk = [29])
    @Test
    fun validateAndSaveReminder_whenValidateAndSaveReminderCorrect_shouldSaveReminder() =
        mainCoroutineRule.runBlockingTest {
            stopKoin()
            reminderDataSource = FakeDataSource()
            fillOutFakeRepositoryReminders()
            val reminderDataItem = ReminderDataItem(
                title = "title1",
                description = "description1",
                location = "location1",
                latitude = 1.1,
                longitude = 1.2
            )

            val saveReminderViewModel =
                SaveReminderViewModel(
                    ApplicationProvider.getApplicationContext(),
                    reminderDataSource
                )

            mainCoroutineRule.pauseDispatcher()

            saveReminderViewModel.validateAndSaveReminder(reminderDataItem)

            assertEquals(saveReminderViewModel.showLoading.getOrAwaitValue(), true)
            mainCoroutineRule.resumeDispatcher()
            assertEquals(saveReminderViewModel.showLoading.getOrAwaitValue(), false)
            val reminderDto = reminderDataSource.getReminder(reminderDataItem.id)

            // No comments...
            if (reminderDto is ResultState.Success) {
                val reminderDataItemDatabase = ReminderDataItem(
                    title = reminderDto.data.title,
                    description = reminderDto.data.description,
                    location = reminderDto.data.location,
                    latitude = reminderDto.data.latitude,
                    longitude = reminderDto.data.longitude,
                    id = reminderDto.data.id
                )
                assertEquals(reminderDataItemDatabase, reminderDataItem)
                assertEquals(saveReminderViewModel.showToast.getOrAwaitValue(), "Reminder Saved !")
            }
        }

    private suspend fun fillOutFakeRepositoryReminders() {
        createFakeRepositoryList().forEach {
            reminderDataSource.saveReminder(it)
        }
    }

    // Roboelectric works "fine" with SDK 29
    @Config(sdk = [29])
    @Test
    fun validateAndSaveReminder_whenValidateAndSaveReminderNoLocation_shouldNotSaveReminder() =
        mainCoroutineRule.runBlockingTest {
            stopKoin()
            reminderDataSource = FakeDataSource()
            fillOutFakeRepositoryReminders()
            val reminderDataItem = ReminderDataItem(
                title = "title1",
                description = "description1",
                location = "",
                latitude = 1.1,
                longitude = 1.2
            )

            val saveReminderViewModel =
                SaveReminderViewModel(
                    ApplicationProvider.getApplicationContext(),
                    reminderDataSource
                )

            saveReminderViewModel.validateAndSaveReminder(reminderDataItem)

            assertEquals(
                saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
                R.string.err_select_location
            )
        }

    // Roboelectric works "fine" with SDK 29
    @Config(sdk = [29])
    @Test
    fun validateAndSaveReminder_whenValidateAndSaveReminderNoTitle_shouldNotSaveReminder() =
        mainCoroutineRule.runBlockingTest {
            stopKoin()
            reminderDataSource = FakeDataSource()
            fillOutFakeRepositoryReminders()
            val reminderDataItem = ReminderDataItem(
                title = "",
                description = "description1",
                location = "location1",
                latitude = 1.1,
                longitude = 1.2
            )

            val saveReminderViewModel =
                SaveReminderViewModel(
                    ApplicationProvider.getApplicationContext(),
                    reminderDataSource
                )

            saveReminderViewModel.validateAndSaveReminder(reminderDataItem)

            assertEquals(
                saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
                R.string.err_enter_title
            )
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