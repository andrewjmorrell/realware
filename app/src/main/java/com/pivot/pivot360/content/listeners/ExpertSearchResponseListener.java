package com.pivot.pivot360.content.listeners;

import com.pivot.pivot360.content.graphql.ExpertSearchQuery;

public interface ExpertSearchResponseListener extends BaseResponseListener {
    void OnAccountResults(ExpertSearchQuery.AsAccountResults response);

}
