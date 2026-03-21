package com.avito.auth.api

interface CurrentUserRepository {

    fun isUserLoggedIn() : Boolean

}