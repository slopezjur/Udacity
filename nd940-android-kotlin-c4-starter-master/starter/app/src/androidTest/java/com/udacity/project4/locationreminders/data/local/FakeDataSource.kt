package com.udacity.project4.locationreminders.data.local

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(
    var remindersList: MutableList<ReminderDTO>? = mutableListOf()
) : ReminderDataSource {

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return remindersList?.let {
            Result.Success(ArrayList(it))
        } ?: Result.Error(Exception("Reminder not found").toString())
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        remindersList?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        return remindersList?.let { list ->
            val result = list.firstOrNull { it.id == id }
            result?.let {
                Result.Success(it)
            } ?: Result.Error(Exception("Reminder not found").toString())
        } ?: Result.Error(Exception("Reminder not found").toString())
    }

    override suspend fun deleteAllReminders() {
        remindersList = null
    }
}