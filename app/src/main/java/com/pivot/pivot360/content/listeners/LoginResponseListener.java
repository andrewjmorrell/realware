package com.pivot.pivot360.content.listeners;

import com.pivot.pivot360.content.graphql.AuthMutation;

public interface LoginResponseListener extends BaseResponseListener {
    void onAuthField(AuthMutation.AsAuthField response);
}
