package com.mindorks.example.coroutines.learn.retrofit.series

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindorks.example.coroutines.data.api.ApiHelper
import com.mindorks.example.coroutines.data.model.ApiUser
import com.mindorks.example.coroutines.utils.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class ParallelNetworkCallsViewModel(private val apiHelper: ApiHelper) : ViewModel() {

    private val users = MutableLiveData<Resource<List<ApiUser>>>()

    fun fetchUsers() {
        viewModelScope.launch {
            users.postValue(Resource.loading(null))
            try {
                coroutineScope {
                    val usersFromApiDeferred = async { apiHelper.getUsers() }
                    val moreUsersFromApiDeferred = async { apiHelper.getMoreUsers() }

                    val usersFromApi = usersFromApiDeferred.await()
                    val moreUsersFromApi = moreUsersFromApiDeferred.await()

                    val allUsersFromApi = mutableListOf<ApiUser>()
                    allUsersFromApi.addAll(usersFromApi)
                    allUsersFromApi.addAll(moreUsersFromApi)

                    users.postValue(Resource.success(allUsersFromApi))
                }
            } catch (e: Exception) {
                users.postValue(Resource.error("Something Went Wrong", null))
            }
        }
    }

    fun getUsers(): LiveData<Resource<List<ApiUser>>> {
        return users
    }

}