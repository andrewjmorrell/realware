package com.pivot.pivot360.pivoteye

import android.content.Intent
import androidx.annotation.IntegerRes
import com.moxtra.sdk.meet.model.Meet
import com.moxtra.sdk.meet.model.MeetSession
import com.pivot.pivot360.content.graphql.EventQuery

data class AttachmentData(
    var attachment: EventQuery.Attachment?,
    var meet: Meet?,
    var title: String?,
    var intent: Intent?
)