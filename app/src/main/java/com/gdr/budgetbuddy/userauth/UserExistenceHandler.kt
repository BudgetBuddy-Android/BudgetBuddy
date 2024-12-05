package com.gdr.budgetbuddy.userauth

/**
 * 유저 존재 여부 확인 인터페이스
 */
interface UserExistenceHandler {
    fun onExistUser()
    fun onNotExistUser()
}