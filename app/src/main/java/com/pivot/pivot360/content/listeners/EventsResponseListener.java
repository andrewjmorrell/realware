package com.pivot.pivot360.content.listeners;

import com.pivot.pivot360.content.graphql.EventsQuery;

public interface EventsResponseListener extends BaseResponseListener {
    void onEventResults(EventsQuery.AsEventResults response);

}
