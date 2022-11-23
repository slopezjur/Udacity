package com.udacity.project4.locationreminders.reminderslist

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.udacity.project4.R
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.FakeDataSource
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest : KoinTest {

    private val fakeDataSource: FakeDataSource by inject()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val activityRule = ActivityTestRule(RemindersActivity::class.java)

    @get:Rule
    var permissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

    @Before
    fun setup() {
        stopKoin()
        startKoin {
            androidContext(getApplicationContext())
            modules(listOf(testModules))
        }
    }

    @Test
    fun reminderListFragment_whenFragmentLoads_shouldSeeData() = runBlockingTest {
        fakeDataSource.deleteAllReminders()

        val remindersList = createFakeRepositoryList()
        remindersList.forEach {
            fakeDataSource.saveReminder(it)
        }

        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        delay(1000)

        onView(withText("title1")).check(matches(isDisplayed()))
        onView(withText("title2")).check(matches(isDisplayed()))
        onView(withText("title3")).check(matches(isDisplayed()))
    }

    @Test
    fun reminderListFragment_whenFragmentNotLoad_shouldSeeNoData() = runBlockingTest {
        fakeDataSource.deleteAllReminders()

        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        delay(1000)

        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
    }

    @Test
    fun reminderListFragment_whenFragmentLoadAndClickReminder_shouldGoToLocationReminder() =
        runBlockingTest {
            val scenario =
                launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
            val navController = mock(NavController::class.java)
            scenario.onFragment {
                it.view?.let { view ->
                    Navigation.setViewNavController(view, navController)
                }
            }
            delay(1000)

            onView(withId(R.id.addReminderFAB)).perform(ViewActions.click())

            verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())
        }

    @Test
    fun reminderListFragment_whenFragmentError_showSnackBar() =
        runBlockingTest {
            val message = "Unable to retrieve the reminder"

            fakeDataSource.setReturnError(true)

            launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
            delay(1000)

            onView(withText(message)).inRoot(RootMatchers.withDecorView(Matchers.not(activityRule.activity.window.decorView)))
                .check(matches(isDisplayed()))
        }

    private val testModules = module {
        viewModel {
            RemindersListViewModel(
                get(),
                get() as FakeDataSource
            )
        }

        single {
            RemindersLocalRepository(get())
        }

        single {
            FakeDataSource()
        }
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