package com.pivot.pivot360.content.listeners;

import com.pivot.pivot360.content.graphql.FeedQuery;

public interface FeedResponseListener extends BaseResponseListener {
    void OnFeedResults(FeedQuery.AsFeedResults response);

}
