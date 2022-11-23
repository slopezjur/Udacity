package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.local.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.util.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    private lateinit var reminderDataSource: FakeDataSource

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Roboelectric works "fine" with SDK 29
    @Config(sdk = [29])
    @Test
    fun loadReminders_whenLoadRemiders_resultIsSuccess() = mainCoroutineRule.runBlockingTest {
        stopKoin()
        reminderDataSource = FakeDataSource()
        createFakeRepositoryList().forEach {
            reminderDataSource.saveReminder(it)
        }

        val remindersListViewModel =
            com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel(
                ApplicationProvider.getApplicationContext(),
                reminderDataSource
            )

        mainCoroutineRule.pauseDispatcher()

        remindersListViewModel.loadReminders()

        Assert.assertEquals(remindersListViewModel.showLoading.getOrAwaitValue(), true)
        mainCoroutineRule.resumeDispatcher()
        Assert.assertEquals(remindersListViewModel.showLoading.getOrAwaitValue(), false)
        Assert.assertEquals(remindersListViewModel.remindersList.getOrAwaitValue().size, 3)
        Assert.assertEquals(remindersListViewModel.showNoData.getOrAwaitValue(), false)
    }

    // Roboelectric works "fine" with SDK 29
    @Config(sdk = [29])
    @Test
    fun loadReminders_whenLoadRemiders_resultIsFail() = mainCoroutineRule.runBlockingTest {
        stopKoin()
        reminderDataSource = FakeDataSource()
        reminderDataSource.setReturnError(true)

        val remindersListViewModel =
            com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel(
                ApplicationProvider.getApplicationContext(),
                reminderDataSource
            )

        mainCoroutineRule.pauseDispatcher()

        remindersListViewModel.loadReminders()

        Assert.assertEquals(true, remindersListViewModel.showLoading.getOrAwaitValue())
        mainCoroutineRule.resumeDispatcher()
        Assert.assertEquals(false, remindersListViewModel.showLoading.getOrAwaitValue())
        val exception = "Unable to retrieve the reminder"
        Assert.assertEquals(exception, remindersListViewModel.showSnackBar.getOrAwaitValue())
        Assert.assertEquals(true, remindersListViewModel.showNoData.getOrAwaitValue())
    }

    private fun createFakeRepositoryList(): MutableList<com.udacity.project4.locationreminders.data.dto.ReminderDTO> {
        return mutableListOf(
            com.udacity.project4.locationreminders.data.dto.ReminderDTO(
                title = "title1",
                description = "description1",
                location = "location1",
                latitude = 1.1,
                longitude = 1.2
            ),
            com.udacity.project4.locationreminders.data.dto.ReminderDTO(
                title = "title2",
                description = "description2",
                location = "location2",
                latitude = 2.1,
                longitude = 2.2
            ),
            com.udacity.project4.locationreminders.data.dto.ReminderDTO(
                title = "title3",
                description = "description3",
                location = "location3",
                latitude = 3.1,
                longitude = 3.2
            )
        )
    }
}