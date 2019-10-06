package com.pivot.pivot360.content.listeners;


import com.pivot.pivot360.content.graphql.EventAssignExpertMutation;

public interface EventAssignExpertResponseListener extends BaseResponseListener {
    void OnEventsFieldAssigned(EventAssignExpertMutation.AsEventField response);//for creating event
}
