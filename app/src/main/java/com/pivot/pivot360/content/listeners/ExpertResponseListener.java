package com.pivot.pivot360.content.listeners;

import com.pivot.pivot360.content.graphql.ExpertQuery;

public interface ExpertResponseListener extends BaseResponseListener {
    void OnAccountResults(ExpertQuery.AsAccountField response);

}
