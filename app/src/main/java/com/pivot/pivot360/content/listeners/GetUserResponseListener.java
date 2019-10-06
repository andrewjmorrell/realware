package com.pivot.pivot360.content.listeners;

import com.pivot.pivot360.content.graphql.AccountQuery;


public interface GetUserResponseListener extends BaseResponseListener {
    void AccountField(AccountQuery.AsAccountField response);//for creating event
}
