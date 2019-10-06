package com.pivot.pivot360.content.listeners;

import com.pivot.pivot360.content.graphql.SearchQuery;

public interface GetSerachResult extends BaseResponseListener {
    void onSearchResult(SearchQuery.AsSearchResults response);
}
