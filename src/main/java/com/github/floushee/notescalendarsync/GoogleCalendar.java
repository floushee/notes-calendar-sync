package com.github.floushee.notescalendarsync;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import java.util.List;

final class GoogleCalendar {

    private final Calendar calendar;

    public GoogleCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public void readEvents(String calendarId) throws Exception {
        DateTime now = new DateTime(System.currentTimeMillis());

        Events events = calendar.events().list(calendarId)
                .setOrderBy("startTime")
                .setTimeMin(now)
                .setSingleEvents(true)
                .execute();

        List<Event> items = events.getItems();
        for (Event event : items) {
            DateTime start = event.getStart().getDateTime();
            if (start == null) {
                start = event.getStart().getDate();
            }
            System.out.printf("%s: %s (%s)\n", event.getId(), event.getSummary(), start);
        }
    }

    public void createEvent(String calendarId, CalendarEntry entry) throws Exception {

        Event event = new Event()
                .setId(entry.getId())
                .setSummary(entry.getSubject());

        DateTime startDateTime = new DateTime(entry.getStart().getTime());
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime);
        event.setStart(start);

        DateTime endDateTime = new DateTime(entry.getEnd().getTime());
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime);
        event.setEnd(end);

        event = calendar.events().update(calendarId, entry.getId(), event).execute();
    }
}
