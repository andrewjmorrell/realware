package com.pivot.pivot360.content.listeners;

import com.pivot.pivot360.content.graphql.EventExpertManageRequestMutation;

public interface ExpertExpertManagementResponseListener extends BaseResponseListener {
    void OnEventResults(EventExpertManageRequestMutation.AsEventField response);

}
