package com.github.floushee.notescalendarsync;

import com.mindoo.domino.jna.NotesCollection;
import com.mindoo.domino.jna.NotesDatabase;
import com.mindoo.domino.jna.NotesViewEntryData;
import com.mindoo.domino.jna.constants.Navigate;
import com.mindoo.domino.jna.constants.ReadMask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.time.LocalDateTime.now;

final class NotesCalendar {

    private static final Logger logger = LoggerFactory.getLogger(NotesCalendar.class);

    private static final String COLUMN_START = "$134";
    private static final String COLUMN_END = "$146";
    private static final String COLUMN_SUBJECT = "$147";

    public static List<CalendarEvent> readNotesCalendarEvents(String server, String filePath) {

        logger.info(format("Accessing Domino server [%s, %s]", server, filePath));
        NotesDatabase database = new NotesDatabase(server, filePath, "");

        logger.info("Opening Notes calendar collection");
        NotesCollection calendar = database.openCollectionByName("Calendar");

        List<CalendarEvent> events = readNotesEntries(calendar)
                .stream()
                .map(NotesCalendar::convertToEvent)
                .filter(NotesCalendar::isFutureEvent)
                .filter(NotesCalendar::isNotYouAreDeputyEvent)
                .collect(Collectors.toList());

        logger.info(format("Successfully read %d Notes calendar entries", events.size()));
        return events;
    }

    private static CalendarEvent convertToEvent(NotesViewEntryData entry) {
        entry.setPreferNotesTimeDates(true);
        String subject = entry.getAsString(COLUMN_SUBJECT, null);
        Calendar start = entry.getAsCalendar(COLUMN_START, null);
        Calendar end = entry.getAsCalendar(COLUMN_END, null);
        // id will be used for google events and must be lower case
        // add timestamp because repeating events share the same unid
        String id = entry.getUNID().toLowerCase() + start.getTime().getTime();
        return new CalendarEvent(id, subject, toLocalDateTime(start), toLocalDateTime(end));
    }

    private static LocalDateTime toLocalDateTime(Calendar calendar) {
        if (calendar == null)
            return null;
        TimeZone tz = calendar.getTimeZone();
        return LocalDateTime.ofInstant(calendar.toInstant(), tz.toZoneId());
    }

    private static List<NotesViewEntryData> readNotesEntries(NotesCollection calendar) {
        String startPos = "0";
        int skipCount = 1;
        EnumSet<Navigate> navigationType = EnumSet.of(Navigate.NEXT_NONCATEGORY);
        int count = Integer.MAX_VALUE;
        int preloadEntryCount = Integer.MAX_VALUE;

        EnumSet<ReadMask> readMask = EnumSet.of(ReadMask.NOTEUNID, ReadMask.SUMMARYVALUES);

        logger.info("Reading Notes calendar entries");
        return calendar.getAllEntries(startPos,
                skipCount, navigationType, preloadEntryCount, readMask,
                new NotesCollection.EntriesAsListCallback(count));
    }

    private static boolean isFutureEvent(CalendarEvent event) {
        return event.getStart().isAfter(now());
    }

    private static boolean isNotYouAreDeputyEvent(CalendarEvent event) {
        return !event.getSubject().toLowerCase().contains("you are deputy");
    }
}
