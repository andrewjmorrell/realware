package com.pivot.pivot360.content.listeners;


import com.pivot.pivot360.content.graphql.EventsByUserQuery;

public interface EventsByUserResponseListener extends BaseResponseListener {
    void onEventResults(EventsByUserQuery.AsEventResults response);

}
