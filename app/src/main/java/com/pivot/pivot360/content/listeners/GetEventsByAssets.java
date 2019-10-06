package com.pivot.pivot360.content.listeners;

import com.pivot.pivot360.content.graphql.EventsByAssetQuery;

public interface GetEventsByAssets extends BaseResponseListener {
    void onEventsByAssets(EventsByAssetQuery.AsEventResults response);
}
