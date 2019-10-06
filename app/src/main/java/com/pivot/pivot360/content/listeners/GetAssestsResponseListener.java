package com.pivot.pivot360.content.listeners;

import com.pivot.pivot360.content.graphql.AssetsQuery;

public interface GetAssestsResponseListener extends BaseResponseListener {
    void onGetAssets(AssetsQuery.AsAssetResults response);

}
