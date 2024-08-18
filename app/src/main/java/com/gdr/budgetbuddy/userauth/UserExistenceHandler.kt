package com.gdr.budgetbuddy.userauth

interface UserExistenceHandler {
    fun onExistUser()
    fun onNotExistUser()
}