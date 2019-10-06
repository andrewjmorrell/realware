package com.pivot.pivot360.content.listeners;

import com.pivot.pivot360.content.graphql.AssetQuery;

public interface SummaryResponseListener extends BaseResponseListener {
    void onAssetField(AssetQuery.AsAssetField response);

}
