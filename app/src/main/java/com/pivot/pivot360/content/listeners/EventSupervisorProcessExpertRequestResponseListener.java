package com.pivot.pivot360.content.listeners;


import com.pivot.pivot360.content.graphql.EventSupervisorProcessExpertRequestMutation;

public interface EventSupervisorProcessExpertRequestResponseListener extends BaseResponseListener {
    void OnAccountResults(EventSupervisorProcessExpertRequestMutation.AsEventField response);

}
