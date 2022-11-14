package com.udacity.project4.locationreminders.data.local

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.ResultState

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {

    var remindersList: MutableList<ReminderDTO> = mutableListOf()

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
        val result = remindersList.firstOrNull { it.id == id }
        return if (shouldReturnError) {
            result?.let {
                ResultState.Success(it)
            } ?: getErrorResult()
        } else {
            getErrorResult()
        }
    }

    private fun getErrorResult() = ResultState.Error(Exception("Reminder not found"))

    override suspend fun deleteAllReminders() {
        remindersList.clear()
    }

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }
}