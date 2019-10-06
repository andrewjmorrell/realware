package com.pivot.pivot360.content.listeners;

import com.pivot.pivot360.content.graphql.EventEditMutation;


public interface CloseEventResponseListener extends BaseResponseListener {
    void OnEventsFieldClosed(EventEditMutation.AsEventField response);//for creating event
}
