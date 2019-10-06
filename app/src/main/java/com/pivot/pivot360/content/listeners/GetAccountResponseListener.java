package com.pivot.pivot360.content.listeners;

import com.pivot.pivot360.content.graphql.AccountQuery;

public interface GetAccountResponseListener extends BaseResponseListener {
    void AccountResponse(AccountQuery.AsAccountField response);//for creating event
}
