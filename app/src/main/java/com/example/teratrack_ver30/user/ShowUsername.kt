package com.example.teratrack_ver30.user

import com.example.teratrack_ver30.user.CreateAccount

class ShowUsername(val account: CreateAccount){
    fun getUsername(): String{
        return account.username
    }
}