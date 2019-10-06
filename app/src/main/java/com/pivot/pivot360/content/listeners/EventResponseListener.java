package com.pivot.pivot360.content.listeners;


import com.pivot.pivot360.content.graphql.EventQuery;

public interface EventResponseListener extends BaseResponseListener {
    void OnEventsField(EventQuery.AsEventField response);//for getting event detail

}
