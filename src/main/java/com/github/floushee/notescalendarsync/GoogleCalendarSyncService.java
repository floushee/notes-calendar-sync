package com.github.floushee.notescalendarsync;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.lang.String.format;
import static java.lang.Thread.sleep;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.stream.Collectors.toList;

final class GoogleCalendarSyncService implements CalendarSyncService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleCalendarSyncService.class);

    private final Calendar calendar;
    private final String calendarId;

    public GoogleCalendarSyncService(Calendar calendar, String calendarId) {
        this.calendar = calendar;
        this.calendarId = calendarId;
    }

    @Override
    public void syncEvents(List<CalendarEvent> events) {

        List<String> existingEventIds;
        try {
            existingEventIds = getExistingGoogleEventIds();
            logger.info(format("Found %d existing google events", existingEventIds.size()));

            List<String> deletedEventIds = findDeletedEvents(events, existingEventIds);
            for (String eventId : deletedEventIds)
                deleteEvent(eventId);

            List<CalendarEvent> newEvents = findNewEvents(events, existingEventIds);
            for (CalendarEvent event : newEvents)
                createEvent(event);

            List<CalendarEvent> existingEvents = findExistingEvents(events, existingEventIds);
            // TODO: update existing events

            logger.info("Finished Google calendar synchronization");
        } catch (IOException e) {
            handleError(events, e);
        }
    }

    private List<String> getExistingGoogleEventIds() throws IOException {
        DateTime now = new DateTime(System.currentTimeMillis());

        Events events = calendar.events().list(calendarId)
                .setOrderBy("startTime")
                .setTimeMin(now)
                .setSingleEvents(true)
                .execute();

        return events.getItems().stream()
                .map(event -> event.getId())
                .collect(toList());
    }

    private List<String> findDeletedEvents(List<CalendarEvent> events, List<String> existingEventIds) {
        return existingEventIds.stream()
                .filter(id -> !events
                        .stream()
                        .filter(event -> id.equals(event.getId()))
                        .findAny()
                        .isPresent()
                ).collect(toList());
    }

    private List<CalendarEvent> findExistingEvents(List<CalendarEvent> events, List<String> existingEventIds) {
        return events
                .stream()
                .filter(event -> existingEventIds.contains(event.getId()))
                .collect(toList());
    }

    private List<CalendarEvent> findNewEvents(List<CalendarEvent> events, List<String> existingEventIds) {
        return events
                .stream()
                .filter(event -> !existingEventIds.contains(event.getId()))
                .collect(toList());
    }

    void deleteEvent(String eventId) {
        try {
            logger.debug("Deleting event " + eventId);
            calendar.events().delete(calendarId, eventId).execute();
        } catch (IOException e) {
            logger.error("Could not delete event " + eventId, e);
        }
    }

    private void createEvent(CalendarEvent event) throws IOException {
        logger.debug("Creating event " + event);
        Event googleEvent = new Event()
                .setId(event.getId())
                .setSummary(event.getSubject());

        if (event.isAllDay()) {
            googleEvent.setStart(toDate(event.getStart()));
            googleEvent.setEnd(toDate(event.getEnd()));
        } else {
            googleEvent.setStart(toDateTime(event.getStart()));
            googleEvent.setEnd(toDateTime(event.getEnd()));
        }
        calendar.events().insert(calendarId, googleEvent).execute();
        logger.debug("Successfully created the event " + event);
    }

    private EventDateTime toDateTime(LocalDateTime input) {
        DateTimeFormatter formatter = ofPattern("yyyy-MM-dd'T'HH:mm:ss+02:00");
        EventDateTime dateTime = new EventDateTime();
        dateTime.setDateTime(new DateTime(input.format(formatter)));
        dateTime.setTimeZone("Europe/Berlin");
        return dateTime;
    }

    private EventDateTime toDate(LocalDateTime input) {
        DateTimeFormatter formatter = ofPattern("yyyy-MM-dd");
        EventDateTime dateTime = new EventDateTime();
        dateTime.setDate(new DateTime(input.format(formatter)));
        return dateTime;
    }

    private void handleError(List<CalendarEvent> events, IOException e) {
        if (e instanceof GoogleJsonResponseException && ((GoogleJsonResponseException) e).getStatusCode() == 503) {
            logger.warn("The Google calendar API is currently not available. Retrying synchronization in 10 seconds..");
            try {
                sleep(10000);
            } catch (InterruptedException interruptedException) {
            }
            syncEvents(events);
        } else {
            logger.error("Could not synchronize Google calendar.", e);
        }
    }
}
