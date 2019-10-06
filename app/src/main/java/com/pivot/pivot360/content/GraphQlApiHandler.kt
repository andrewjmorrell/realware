package com.pivot.pivot360.network

import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.exception.ApolloException
import com.pivot.pivot360.content.GraphQLHandler
import com.pivot.pivot360.content.graphql.*
import com.pivot.pivot360.content.listeners.GetUserResponseListener
import com.pivot.pivot360.content.listeners.SummaryResponseListener
import com.pivot.pivot360.content.listeners.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GraphQlApiHandler private constructor() {

    /**
     * Method to make user login
     *
     * @param mutation input mutation
     * @param listener listener for sending response to calling page
     */
    companion object {

        val instance = GraphQlApiHandler()
    }

//    fun <T> getData(mutation: T) where T : Q {
//        GlobalScope.launch(Dispatchers.Main) {
//
//            val response = GraphQLHandler.getApolloClient().query(mutation).toDeferred().await()
//            val assets = response.data()
//            //listener.onResponseMessageField("done")
//
//
//        }
//    }
//                assets.let {
//                    when (it) {
//                        is AssetQuery.AsResponseMessageField -> {
//                            listener.onResponseMessageField(it.message())
//                        }
//                        is AssetQuery.AsAuthInfoField -> {
//                            listener.onAuthInfoField(it.message())
//                        }
//                        is AssetQuery.AsAssetField -> {
//                            listener.onAssetField(it)
//                        }
//                    }
//                }
//
//            } catch (e: ApolloException) {
//                // you will end up here if .await() throws, most likely due to a transport or parsing error
//                listener.onError(e.message)
//            } catch (e: NullPointerException) {
//                // you will end up here if repositories!! throws above. This will happen if your server sends a response
//                // with missing fields or errors
//                listener.onError(e.message)
//
//            } catch (e: Exception) {
//                // you will end up here if repositories!! throws above. This will happen if your server sends a response
//                // with missing fields or errors
//                listener.onError(e.message)
//
//            }


    fun getAssets(mutation: AssetQuery, listener: SummaryResponseListener) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = GraphQLHandler.getApolloClient().query(mutation).toDeferred().await()
                val assets = response.data()?.asset()
                assets.let {
                    when (it) {
                        is AssetQuery.AsResponseMessageField -> {
                            listener.onResponseMessageField(it.message())
                        }
                        is AssetQuery.AsAuthInfoField -> {
                            listener.onAuthInfoField(it.message())
                        }
                        is AssetQuery.AsAssetField -> {
                            listener.onAssetField(it)
                        }
                    }
                }

            } catch (e: ApolloException) {
                // you will end up here if .await() throws, most likely due to a transport or parsing error
                listener.onError(e.message)
            } catch (e: NullPointerException) {
                // you will end up here if repositories!! throws above. This will happen if your server sends a response
                // with missing fields or errors
                listener.onError(e.message)

            } catch (e: Exception) {
                // you will end up here if repositories!! throws above. This will happen if your server sends a response
                // with missing fields or errors
                listener.onError(e.message)

            }
        }
    }

    fun makeLogin(mutation: AuthMutation, listener: LoginResponseListener) {
        try {
            GraphQLHandler.getApolloClient().mutate(mutation).enqueue(object : ApolloCall.Callback<AuthMutation.Data>() {
                override fun onResponse(response: Response<AuthMutation.Data>) {
                    if (response.data() != null && response.data()!!.auth() != null && response.data()!!.auth()!!.result() != null) {
                        response.data()!!.auth()!!.result().let {
                            when (it) {
                                is AuthMutation.AsResponseMessageField -> {
                                    val asResponseMessageField = response.data()!!.auth()!!.result() as AuthMutation.AsResponseMessageField?
                                    listener.onResponseMessageField(asResponseMessageField!!.message())
                                }
                                else -> {
                                    val asAuthField = response.data()!!.auth()!!.result() as AuthMutation.AsAuthField?
                                    listener.onAuthField(asAuthField)
                                }
                            }
                        }
                    }
                }

                override fun onFailure(e: ApolloException) {
                    listener.onError(e.message)
                }
            })
        } catch (t: Throwable) {
            listener.onError(t.message)
        }

    }

    /**
     * Method to get assets detail
     *
     * @param mutation input mutation
     * @param listener listener for sending response to calling page
     */


//    fun getAssets(mutation: AssetQuery, listener: SummaryResponseListener) {
//        try {
//            GraphQLHandler.getApolloClient().query(mutation).enqueue(object : ApolloCall.Callback<AssetQuery.Data>() {
//                override fun onResponse(response: Response<AssetQuery.Data>) {
//                    if (response.data() != null && response.data()?.asset() != null) {
//
//                        response.data()?.asset().let {
//                            when (it) {
//                                is AssetQuery.AsResponseMessageField -> {
//                                    listener.onResponseMessageField(it.message())
//                                }
//                                is AssetQuery.AsAuthInfoField -> {
//                                    listener.onAuthInfoField(it.message())
//                                }
//                                is AssetQuery.AsAssetField -> {
//                                    listener.onAssetField(it)
//                                }
//                            }
//                        }
//                    }
//                }
//
//                override fun onFailure(e: ApolloException) {
//                    listener.onError(e.message)
//                }
//            })
//        } catch (t: Throwable) {
//            listener.onError(t.message)
//        }
//
//    }

    /**
     * Method to get event detail
     *
     * @param mutation input mutation
     * @param listener listener for sending response to calling page
     */
    fun getEvent(mutation: EventQuery, listener: EventResponseListener) {
        try {
            GraphQLHandler.getApolloClient().query(mutation).enqueue(object : ApolloCall.Callback<EventQuery.Data>() {
                override fun onResponse(response: Response<EventQuery.Data>) {
                    if (response.data() != null && response.data()!!.event() != null) {
                        response.data()!!.event().let {
                            when (it) {
                                is EventQuery.AsResponseMessageField -> {

                                    listener.onResponseMessageField(it.message())
                                }
                                is EventQuery.AsAuthInfoField -> {

                                    listener.onAuthInfoField(it.message())
                                }
                                is EventQuery.AsEventField -> {
                                    listener.OnEventsField(it)
                                }
                            }
                        }
                    }

                }

                override fun onFailure(e: ApolloException) {
                    Log.d("wwe", e.message)
                }
            })
        } catch (t: Throwable) {
            listener.onError(t.message)
        }

    }

    /**
     * Method to create event
     *
     * @param mutation input mutation
     * @param listener listener for sending response to calling page
     */
    fun createEvent(mutation: EventCreateMutation, listener: CreateEventResponseListener) {

        try {
            GraphQLHandler.getApolloClient().mutate(mutation).enqueue(object : ApolloCall.Callback<EventCreateMutation.Data>() {
                override fun onResponse(response: Response<EventCreateMutation.Data>) {
                    if (response.data() != null && response.data()!!.eventCreate() != null && response.data()!!.eventCreate()!!.result() != null) {
                        response.data()!!.eventCreate()!!.result().let {
                            when (it) {
                                is EventCreateMutation.AsResponseMessageField -> {
                                    listener.onResponseMessageField(it.message())
                                }
                                is EventCreateMutation.AsAuthInfoField -> {

                                    listener.onResponseMessageField(it.message())
                                }
                                is EventCreateMutation.AsEventField -> {

                                    listener.OnEventsField(it)
                                }
                            }
                        }

                    }

                }

                override fun onFailure(e: ApolloException) {
                    listener.onError(e.message)
                }
            })
        } catch (t: Throwable) {
            listener.onError(t.message)
        }

    }

    fun closeEvent(mutation: EventEditMutation, listener: CloseEventResponseListener) {

        try {
            GraphQLHandler.getApolloClient().mutate(mutation).enqueue(object : ApolloCall.Callback<EventEditMutation.Data>() {
                override fun onResponse(response: Response<EventEditMutation.Data>) {
                    if (response.data() != null && response.data()!!.eventEdit() != null && response.data()!!.eventEdit()!!.result() != null) {
                        response.data()!!.eventEdit()!!.result().let {
                            when (it) {
                                is EventEditMutation.AsResponseMessageField -> {
                                    listener.onResponseMessageField(it.message())
                                }
                                is EventEditMutation.AsAuthInfoField -> {
                                    listener.onResponseMessageField(it.message())
                                }
                                is EventEditMutation.AsEventField -> {
                                    listener.OnEventsFieldClosed(it)
                                }
                            }

                        }

                    }

                }

                override fun onFailure(e: ApolloException) {
                    listener.onError(e.message)
                }
            })
        } catch (t: Throwable) {
            listener.onError(t.message)
        }

    }

    fun getAssets(query: AssetsQuery, listener: GetAssestsResponseListener) {
        GraphQLHandler.getApolloClient().query(query).enqueue(object : ApolloCall.Callback<AssetsQuery.Data>() {
            override fun onResponse(response: Response<AssetsQuery.Data>) {

                if (response.data() != null && response.data() != null && response.data()!!.assets() != null) {
                    response.data()!!.assets().let {
                        when (it) {
                            is AssetsQuery.AsResponseMessageField -> {
                                listener.onResponseMessageField(it.message())
                            }
                            is AssetsQuery.AsAuthInfoField -> {
                                listener.onResponseMessageField(it.message())
                            }
                            is AssetsQuery.AsAssetResults -> {
                                listener.onGetAssets(it)
                            }
                        }
                    }
                }
            }

            override fun onFailure(e: ApolloException) {

            }
        })
    }

    fun getEventByAssets(query: EventsByAssetQuery, listener: GetEventsByAssets) {
        GraphQLHandler.getApolloClient().query(query).enqueue(object : ApolloCall.Callback<EventsByAssetQuery.Data>() {
            override fun onResponse(response: Response<EventsByAssetQuery.Data>) {

                if (response.data() != null && response.data() != null && response.data()!!.eventsByAsset() != null) {
                    response.data()!!.eventsByAsset().let {
                        when (it) {
                            is EventsByAssetQuery.AsResponseMessageField -> {
                                listener.onResponseMessageField(it.message())
                            }
                            is EventsByAssetQuery.AsAuthInfoField -> {
                                listener.onAuthInfoField(it.message())
                            }
                            is EventsByAssetQuery.AsEventResults -> {
                                listener.onEventsByAssets(it)
                            }
                        }
                    }
                }
            }

            override fun onFailure(e: ApolloException) {
                Log.d("onFailure", e.message)
            }
        })
    }

    fun getUser(query: AccountQuery, listener: GetUserResponseListener) {
        try {
            GraphQLHandler.getApolloClient().query(query).enqueue(object : ApolloCall.Callback<AccountQuery.Data>() {
                override fun onResponse(response: Response<AccountQuery.Data>) {
                    if (response.data() != null && response.data()!!.account() != null) {
                        response.data()?.account()?.let {
                            when (it) {
                                is AccountQuery.AsResponseMessageField -> {
                                    listener.onResponseMessageField(it.message())
                                }
                                is AccountQuery.AsAuthInfoField -> {
                                    listener.onAuthInfoField(it.message())
                                }
                                is AccountQuery.AsAccountField -> {
                                    listener.AccountField(it)
                                }
                            }
                        }
                    }
                }


                override fun onFailure(e: ApolloException) {
                    listener.onError(e.message)
                }
            })
        } catch (t: Throwable) {
            listener.onError(t.message)
        }

    }

    fun getSearch(query: SearchQuery, listener: GetSerachResult) {
        GraphQLHandler.getApolloClient().query(query).enqueue(object : ApolloCall.Callback<SearchQuery.Data>() {
            override fun onResponse(response: Response<SearchQuery.Data>) {

                if (response.data() != null && response.data() != null && response.data()!!.search() != null) {
                    response.data()!!.search().let {
                        when (it) {

                            is SearchQuery.AsResponseMessageField -> {
                                listener.onResponseMessageField(it.message())
                            }
                            is SearchQuery.AsAuthInfoField -> {
                                listener.onAuthInfoField(it.message())
                            }
                            is SearchQuery.AsSearchResults -> {

                                listener.onSearchResult(it)
                            }
                        }
                    }
                }
            }

            override fun onFailure(e: ApolloException) {
                Log.d("onFailure", e.message)
            }
        })
    }

    fun getAccount(query: AccountQuery, listener: GetAccountResponseListener) {
        GraphQLHandler.getApolloClient().query(query).enqueue(object : ApolloCall.Callback<AccountQuery.Data>() {
            override fun onResponse(response: Response<AccountQuery.Data>) {

                if (response.data() != null && response.data() != null && response.data()!!.account() != null) {
                    response.data()!!.account().let {

                        when (it) {
                            is AccountQuery.AsResponseMessageField -> {
                                listener.onResponseMessageField(it.message())
                            }
                            is AccountQuery.AsAuthInfoField -> {
                                listener.onAuthInfoField(it.message())
                            }
                            is AccountQuery.AsAccountField -> {
                                listener.AccountResponse(it)
                            }
                        }
                    }
                }
            }


            override fun onFailure(e: ApolloException) {
                Log.d("onFailure", e.message)
            }
        })
    }

    fun getEventsByUser(query: EventsByUserQuery, listener: EventsByUserResponseListener) {
        try {
            GraphQLHandler.getApolloClient().query(query).enqueue(object : ApolloCall.Callback<EventsByUserQuery.Data>() {
                override fun onResponse(response: Response<EventsByUserQuery.Data>) {
                    if (response.data() != null && response.data()!!.eventsByUser() != null) {
                        when {
                            response.data()!!.eventsByUser() is EventsByUserQuery.AsResponseMessageField -> {
                                val asResponseMessageField = response.data()!!.eventsByUser() as EventsByUserQuery.AsResponseMessageField?
                                listener.onResponseMessageField(asResponseMessageField!!.message())

                            }
                            response.data()!!.eventsByUser() is EventsByUserQuery.AsAuthInfoField -> {
                                val asAuthInfoField = response.data()!!.eventsByUser() as EventsByUserQuery.AsAuthInfoField?
                                listener.onAuthInfoField(asAuthInfoField!!.message())
                            }
                            else -> {
                                val asAuthField = response.data()!!.eventsByUser() as EventsByUserQuery.AsEventResults?
                                listener.onEventResults(asAuthField)
                            }
                        }
                    }

                }

                override fun onFailure(e: ApolloException) {
                    listener.onError(e.message)
                }
            })
        } catch (t: Throwable) {
            listener.onError(t.message)
        }

    }

    fun getEvents(query: EventsQuery, listener: EventsResponseListener) {
        try {
            GraphQLHandler.getApolloClient().query(query).enqueue(object : ApolloCall.Callback<EventsQuery.Data>() {
                override fun onResponse(response: Response<EventsQuery.Data>) {
                    if (response.data() != null && response.data()!!.events() != null) {
                        when {
                            response.data()!!.events() is EventsByUserQuery.AsResponseMessageField -> {
                                val asResponseMessageField = response.data()!!.events() as EventsQuery.AsResponseMessageField?
                                listener.onResponseMessageField(asResponseMessageField!!.message())

                            }
                            response.data()!!.events() is EventsQuery.AsAuthInfoField -> {
                                val asAuthInfoField = response.data()!!.events() as EventsQuery.AsAuthInfoField?
                                listener.onAuthInfoField(asAuthInfoField!!.message())
                            }
                            else -> {
                                val asAuthField = response.data()!!.events() as EventsQuery.AsEventResults?
                                listener.onEventResults(asAuthField)
                            }
                        }
                    }

                }

                override fun onFailure(e: ApolloException) {
                    listener.onError(e.message)
                }
            })
        } catch (t: Throwable) {
            listener.onError(t.message)
        }

    }

    fun getSuggestedExperts(mutation: ExpertSearchQuery, listener: ExpertSearchResponseListener) {
        try {
            GraphQLHandler.getApolloClient().query(mutation).enqueue(object : ApolloCall.Callback<ExpertSearchQuery.Data>() {
                override fun onResponse(response: Response<ExpertSearchQuery.Data>) {
                    if (response.data() != null && response.data()!!.expertSearch() != null) {
                        response.data()!!.expertSearch().let {
                            when (it) {
                                is ExpertSearchQuery.AsResponseMessageField -> {
                                    listener.onResponseMessageField(it.message())
                                }
                                is ExpertSearchQuery.AsAuthInfoField -> {
                                    listener.onAuthInfoField(it.message())
                                }
                                is ExpertSearchQuery.AsAccountResults -> {
                                    listener.OnAccountResults(it)
                                }

                            }
                        }

                    }

                }

                override fun onFailure(e: ApolloException) {
                    listener.onError(e.message)
                }
            })
        } catch (t: Throwable) {
            listener.onError(t.message)
        }

    }


    fun getExpert(mutation: ExpertQuery, listener: ExpertResponseListener) {
        try {
            GraphQLHandler.getApolloClient().query(mutation).enqueue(object : ApolloCall.Callback<ExpertQuery.Data>() {
                override fun onResponse(response: Response<ExpertQuery.Data>) {
                    if (response.data() != null && response.data()!!.expert() != null) {

                        if (response.data()!!.expert() is ExpertQuery.AsResponseMessageField) {
                            val asResponseMessageField = response.data()!!.expert() as ExpertQuery.AsResponseMessageField?
                            listener.onResponseMessageField(asResponseMessageField!!.message())

                        } else if (response.data()!!.expert() is ExpertQuery.AsAuthInfoField) {
                            val asAuthField = response.data()!!.expert() as ExpertQuery.AsAuthInfoField?
                            listener.onAuthInfoField(asAuthField!!.message())
                        } else if (response.data()!!.expert() is ExpertQuery.AsAccountField) {
                            val asAssetField = response.data()!!.expert() as ExpertQuery.AsAccountField?
                            listener.OnAccountResults(asAssetField)
                        }
                    }

                }

                override fun onFailure(e: ApolloException) {
                    listener.onError(e.message)
                }
            })
        } catch (t: Throwable) {
            listener.onError(t.message)
        }

    }

    fun getSupervisor(mutation: SupervisorQuery, listener: SupervisorResponseListener) {
        try {
            GraphQLHandler.getApolloClient().query(mutation).enqueue(object : ApolloCall.Callback<SupervisorQuery.Data>() {
                override fun onResponse(response: Response<SupervisorQuery.Data>) {
                    if (response.data() != null && response.data()!!.supervisor() != null) {

                        if (response.data()!!.supervisor() is SupervisorQuery.AsResponseMessageField) {
                            val asResponseMessageField = response.data()!!.supervisor() as SupervisorQuery.AsResponseMessageField?
                            listener.onResponseMessageField(asResponseMessageField!!.message())

                        } else if (response.data()!!.supervisor() is SupervisorQuery.AsAuthInfoField) {
                            val asAuthField = response.data()!!.supervisor() as SupervisorQuery.AsAuthInfoField?
                            listener.onAuthInfoField(asAuthField!!.message())
                        } else if (response.data()!!.supervisor() is SupervisorQuery.AsAccountField) {
                            val asAssetField = response.data()!!.supervisor() as SupervisorQuery.AsAccountField?
                            listener.OnAccountResults(asAssetField)
                        }
                    }

                }

                override fun onFailure(e: ApolloException) {
                    listener.onError(e.message)
                }
            })
        } catch (t: Throwable) {
            listener.onError(t.message)
        }

    }

    fun eventAssignExpert(mutation: EventAssignExpertMutation, listener: EventAssignExpertResponseListener) {

        try {
            GraphQLHandler.getApolloClient().mutate(mutation).enqueue(object : ApolloCall.Callback<EventAssignExpertMutation.Data>() {
                override fun onResponse(response: Response<EventAssignExpertMutation.Data>) {
                    if (response.data() != null
                            && response.data()!!.eventAssignExpert() != null
                            && response.data()!!.eventAssignExpert()!!.result() != null) {
                        if (response.data()!!.eventAssignExpert()!!.result() is EventAssignExpertMutation.AsResponseMessageField) {
                            val asResponseMessageField = response.data()!!.eventAssignExpert()!!.result() as EventAssignExpertMutation.AsResponseMessageField?
                            listener.onResponseMessageField(asResponseMessageField!!.message())
                        } else if (response.data()!!.eventAssignExpert()!!.result() is EventAssignExpertMutation.AsAuthInfoField) {
                            val asAuthInfoField = response.data()!!.eventAssignExpert()!!.result() as EventAssignExpertMutation.AsAuthInfoField?
                            listener.onResponseMessageField(asAuthInfoField!!.message())
                        } else if (response.data()!!.eventAssignExpert()!!.result() is EventAssignExpertMutation.AsEventField) {
                            val asEventField = response.data()!!.eventAssignExpert()!!.result() as EventAssignExpertMutation.AsEventField?
                            listener.OnEventsFieldAssigned(asEventField)
                        }
                    }

                }

                override fun onFailure(e: ApolloException) {
                    listener.onError(e.message)
                }
            })
        } catch (t: Throwable) {
            listener.onError(t.message)
        }

    }

    fun eventSupervisorProcessExpertRequest(mutation: EventSupervisorProcessExpertRequestMutation, listener: EventSupervisorProcessExpertRequestResponseListener) {

        try {
            GraphQLHandler.getApolloClient().mutate(mutation).enqueue(object : ApolloCall.Callback<EventSupervisorProcessExpertRequestMutation.Data>() {
                override fun onResponse(response: Response<EventSupervisorProcessExpertRequestMutation.Data>) {
                    if (response.data() != null
                            && response.data()!!.eventSupervisorProcessExpertRequest() != null
                            && response.data()!!.eventSupervisorProcessExpertRequest()!!.result() != null) {
                        if (response.data()!!.eventSupervisorProcessExpertRequest()!!.result() is EventSupervisorProcessExpertRequestMutation.AsResponseMessageField) {
                            val asResponseMessageField = response.data()!!.eventSupervisorProcessExpertRequest()!!.result() as EventSupervisorProcessExpertRequestMutation.AsResponseMessageField?
                            listener.onResponseMessageField(asResponseMessageField!!.message())
                        } else if (response.data()!!.eventSupervisorProcessExpertRequest()!!.result() is EventSupervisorProcessExpertRequestMutation.AsAuthInfoField) {
                            val asAuthInfoField = response.data()!!.eventSupervisorProcessExpertRequest()!!.result() as EventSupervisorProcessExpertRequestMutation.AsAuthInfoField?
                            listener.onResponseMessageField(asAuthInfoField!!.message())
                        } else if (response.data()!!.eventSupervisorProcessExpertRequest()!!.result() is EventSupervisorProcessExpertRequestMutation.AsEventField) {
                            val asEventField = response.data()!!.eventSupervisorProcessExpertRequest()!!.result() as EventSupervisorProcessExpertRequestMutation.AsEventField?
                            listener.OnAccountResults(asEventField)
                        }
                    }

                }

                override fun onFailure(e: ApolloException) {
                    listener.onError(e.message)
                }
            })
        } catch (t: Throwable) {
            listener.onError(t.message)
        }

    }

    fun eventExpertManagementRequest(mutation: EventExpertManageRequestMutation, listener: ExpertExpertManagementResponseListener) {

        try {
            GraphQLHandler.getApolloClient().mutate(mutation).enqueue(object : ApolloCall.Callback<EventExpertManageRequestMutation.Data>() {
                override fun onResponse(response: Response<EventExpertManageRequestMutation.Data>) {
                    if (response.data() != null
                            && response.data()!!.eventExpertManageRequest() != null
                            && response.data()!!.eventExpertManageRequest()!!.result() != null) {
                        if (response.data()!!.eventExpertManageRequest()!!.result() is EventExpertManageRequestMutation.AsResponseMessageField) {
                            val asResponseMessageField = response.data()!!.eventExpertManageRequest()!!.result() as EventExpertManageRequestMutation.AsResponseMessageField?
                            listener.onResponseMessageField(asResponseMessageField!!.message())
                        } else if (response.data()!!.eventExpertManageRequest()!!.result() is EventExpertManageRequestMutation.AsAuthInfoField) {
                            val asAuthInfoField = response.data()!!.eventExpertManageRequest()!!.result() as EventExpertManageRequestMutation.AsAuthInfoField?
                            listener.onResponseMessageField(asAuthInfoField!!.message())
                        } else if (response.data()!!.eventExpertManageRequest()!!.result() is EventExpertManageRequestMutation.AsEventField) {
                            val asEventField = response.data()!!.eventExpertManageRequest()!!.result() as EventExpertManageRequestMutation.AsEventField?
                            listener.OnEventResults(asEventField)
                        }
                    }

                }

                override fun onFailure(e: ApolloException) {
                    listener.onError(e.message)
                }
            })
        } catch (t: Throwable) {
            listener.onError(t.message)
        }

    }

    fun getFeed(mutation: FeedQuery, listener: FeedResponseListener) {

        try {
            GraphQLHandler.getApolloClient().query(mutation).enqueue(object : ApolloCall.Callback<FeedQuery.Data>() {
                override fun onResponse(response: Response<FeedQuery.Data>) {
                    if (response.data() != null && response.data()!!.feed() != null) {
                        response.data()!!.feed()!!.let {
                            when (it) {
                                is FeedQuery.AsResponseMessageField -> {
                                    listener.onResponseMessageField(it.message())
                                }
                                is FeedQuery.AsFeedResults -> {
                                    listener.OnFeedResults(it)
                                }
                            }
                        }

                    }

                }

                override fun onFailure(e: ApolloException) {
                    listener.onError(e.message)
                }
            })
        } catch (t: Throwable) {
            listener.onError(t.message)
        }

    }

}


