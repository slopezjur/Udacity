package com.udacity.project4.locationreminders.data.local

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {

    var error = false

    var remindersList: MutableList<ReminderDTO> = mutableListOf()

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return if(error) {
            Result.Error("Reminder not found")
        } else {
            Result.Success(remindersList)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        remindersList.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        val result = remindersList.firstOrNull { it.id == id }
        return result?.let {
            Result.Success(it)
        } ?: Result.Error("Reminder not found")
    }

    override suspend fun deleteAllReminders() {
        remindersList.clear()
    }

    fun setError() {
        error = true
    }
}