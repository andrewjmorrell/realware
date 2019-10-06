package com.pivot.pivot360.content.listeners;

import com.pivot.pivot360.content.graphql.SupervisorQuery;

public interface SupervisorResponseListener extends BaseResponseListener {
    void OnAccountResults(SupervisorQuery.AsAccountField response);

}
