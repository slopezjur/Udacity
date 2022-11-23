package com.udacity.project4.locationreminders.data.local

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.ResultState

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {

    private var remindersList: MutableList<ReminderDTO> = mutableListOf()

    private var shouldReturnError = false

    override suspend fun getReminders(): ResultState<List<ReminderDTO>> {
        return if (shouldReturnError) {
            getErrorResult()
        } else {
            ResultState.Success(remindersList)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        remindersList.add(reminder)
    }

    override suspend fun getReminder(id: String): ResultState<ReminderDTO> {
        return if (shouldReturnError) {
            getErrorResult()
        } else {
            val result = remindersList.firstOrNull { it.id == id }
            result?.let {
                ResultState.Success(it)
            } ?: getErrorResult()
        }
    }

    private fun getErrorResult() = ResultState.Error(Exception("Unable to retrieve the reminder"))

    override suspend fun deleteAllReminders() {
        remindersList.clear()
    }

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }
}