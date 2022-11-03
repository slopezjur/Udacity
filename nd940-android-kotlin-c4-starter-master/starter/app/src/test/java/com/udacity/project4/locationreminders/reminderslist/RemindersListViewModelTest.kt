package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    private lateinit var reminderDataSource: FakeDataSource

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun loadReminders_whenLoadRemiders_resultIsSuccess() = mainCoroutineRule.runBlockingTest {
        stopKoin()
        reminderDataSource = FakeDataSource(createFakeRepositoryList())

        val remindersListViewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), reminderDataSource)

        mainCoroutineRule.pauseDispatcher()

        remindersListViewModel.loadReminders()

        assertEquals(remindersListViewModel.showLoading.getOrAwaitValue(), true)
        mainCoroutineRule.resumeDispatcher()
        assertEquals(remindersListViewModel.showLoading.getOrAwaitValue(), false)
        assertEquals(remindersListViewModel.remindersList.getOrAwaitValue().size, 3)
        assertEquals(remindersListViewModel.showNoData.getOrAwaitValue(), false)
    }

    @Test
    fun loadReminders_whenLoadRemiders_resultIsFail() = mainCoroutineRule.runBlockingTest {
        stopKoin()
        reminderDataSource = FakeDataSource(null)

        val remindersListViewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), reminderDataSource)

        mainCoroutineRule.pauseDispatcher()

        remindersListViewModel.loadReminders()

        assertEquals(remindersListViewModel.showLoading.getOrAwaitValue(), true)
        mainCoroutineRule.resumeDispatcher()
        assertEquals(remindersListViewModel.showLoading.getOrAwaitValue(), false)
        val exception = Exception("Reminder not found")
        assertEquals(remindersListViewModel.showSnackBar.getOrAwaitValue(), "$exception")
        assertEquals(remindersListViewModel.showNoData.getOrAwaitValue(), true)
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