package com.pivot.pivot360.pivoteye


object Constants {
    var BASE_URL = ""
    val CLIENT_ID = "gVT4402EnwU"
    val CLIENT_SECRET = "gz5L0B4Ng1o"
    val BASE_DOMAIN = "sandbox.moxtra.com"

    const val STATUS_IN_PROGRESS = "In progress"
    const val STATUS_CLOSED = "Closed"
    const val STATUS_ARCHIVE = "Archive"

    const val TYPE_ASSETS = "assets"
    const val TYPE_EVENTS = "events"
    const val TYPE_CONVERSATIONS = "conversations"

    const val EVENT_STATUS_OPENED = "opened"
    const val EVENT_STATUS_IN_PROGRESS = "in_progress"
    const val EVENT_STATUS_CLOSED = "closed"

    const val EVENT_STATUS_ARCHIVE = "archive"
    const val EVENT_STATUS_ACCEPTED = "accepted"
    const val EVENT_STATUS_AWAITING = "awaiting"
    const val EVENT_STATUS_REQUESTED = "requested"


    const val IDENTITY = "identity"
    const val TOKEN = "token"
    const val UNIQUEID = "uniqueid"

    const val SEARCH_TAB_ALL = "All"
    const val SEARCH_TAB_ASSETS = "Assets"
    const val SEARCH_TAB_EVENTS = "Events"
    const val SEARCH_TAB_CONVERSATION = "Conversation"


    const val FEED_VERB_SME = "sme"
    const val FEED_VERB_NOTIFICATION = "notification"
    const val FEED_VERB_EVENT_OPENED = "event_opened"
    const val FEED_VERB_EVENT_CLOSED = "event_closed"


    const val TIME_FORMAT_YMDTHMS = "yyyy-MM-dd'T'HH:mm:ss"
    const val TIME_FORMAT_YMDHMS = "yyyy-MM-dd HH:mm:ss"
    const val TIME_FORMAT_DATE_OPEN_CLOSED_EVENT = "MMM dd, yyyy hh:mm a"
    const val TIME_FORMAT_DATE_TASK_RESPONSE = "MMMM dd, yyyy hh:mm a"
    const val TIME_FORMAT_DATE_CLOSED_EVENT_STATUS = "MMMM dd, yyyy"
    const val TIME_FORMAT_DATE_SME = "hh:mm a MMM dd yyyy "
    const val TIME_FORMAT_ATTACHMENT = "MMM dd, yyyy hh:mm a"
    const val TIME_FORMAT_TASKS = "hh:mm a | MM/dd/yyyy" //" "08:30am | 12/21/2019"

    const val UPDATE_VIEW_BROADCAST = "updateView"

    const val TASK_STATUS = "status"
    const val TASK_STATUS_NOT_STARTED = "not_started"
    const val TASK_STATUS_COMPLETED = "completed"
    const val TASK_STATUS_FAILED = "failed"
    const val TASK_STATUS_PASSED = "passed"
    const val TASK_STATUS_IN_PROGRESS = "in_progress"
    const val TASK_STATUS_STOPPED = "stopped"
    const val TASK_STATUS_TEXT_COUNTDOWN_COMPLETE = "Countdown Completed "
    const val TASK_STATUS_TEXT_FAILED = "Failed "
    const val TASK_STATUS_TEXT_STOPPED_EARLY = "Countdown Stopped Early "
    const val TASK_STATUS_TEXT_PASSED = "Passed "
    const val TASK_STATUS_TEXT_START = "Start Test"
    const val TASK_STATUS_TEXT_STOP_THE_CLOCK = "Stop the clock"
    const val TASK_STATUS_TEXT_SKIP_THE_COUNTDOWN = "Skip the countdown"

    const val TASK_CARD_TYPE_COUNTDOWN = "countdown"
    const val TASK_CARD_TYPE_STOPWATCH = "stopwatch"
    const val TASK_CARD_TYPE_TEXT = "text"

    const val USER_TASK_TYPE_CONVERSATION = "conversation"
    const val USER_TASK_TYPE_FORM = "form"

}
