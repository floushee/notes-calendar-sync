package com.github.floushee.notescalendarsync;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

final class GoogleCalendar {

    private static final Logger logger = LoggerFactory.getLogger(GoogleCalendar.class);

    private final Calendar calendar;

    public GoogleCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public Optional<List<String>> readEventIds(String calendarId) {
        try {
            DateTime now = new DateTime(System.currentTimeMillis());

            Events events = calendar.events().list(calendarId)
                    .setOrderBy("startTime")
                    .setTimeMin(now)
                    .setSingleEvents(true)
                    .execute();

            return Optional.of(
                    events.getItems().stream()
                            .map(event -> event.getId())
                            .collect(Collectors.toList())
            );
        } catch (Exception e) {
            logger.error("Could not read Google calendar events", e);
            return Optional.empty();
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
