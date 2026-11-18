package com.example.pesatrack.data.repository

import com.example.pesatrack.data.local.UserDao
import com.example.pesatrack.data.model.UserEntity

class UserRepository(
    private val userDao: UserDao
) {
    suspend fun insertUser(user: UserEntity): Long {
        return userDao.insertUser(user)
    }

    suspend fun getUserByEmail(email: String): UserEntity? {
        return userDao.getUserByEmail(email)
    }

    suspend fun login(email: String, password: String): UserEntity? {
        return userDao.login(email, password)
    }
}
