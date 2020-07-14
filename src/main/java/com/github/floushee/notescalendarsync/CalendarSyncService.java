package com.github.floushee.notescalendarsync;

import java.util.List;

interface CalendarSyncService {

    public void syncEvents(List<CalendarEvent> events);
}
