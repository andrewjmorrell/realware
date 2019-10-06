package com.pivot.pivot360.content.listeners;


import com.pivot.pivot360.content.graphql.EventCreateMutation;

public interface CreateEventResponseListener extends BaseResponseListener {
    void OnEventsField(EventCreateMutation.AsEventField response);//for creating event
}
