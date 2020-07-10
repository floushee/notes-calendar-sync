package com.github.floushee.notescalendarsync;

import com.mindoo.domino.jna.NotesCollection;
import com.mindoo.domino.jna.NotesDatabase;
import com.mindoo.domino.jna.NotesViewEntryData;
import com.mindoo.domino.jna.constants.Navigate;
import com.mindoo.domino.jna.constants.ReadMask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.List;

final class NotesCalendarReader {

    private static final String COLUMN_START = "$134";
    private static final String COLUMN_END = "$146";
    private static final String COLUMN_SUBJECT = "$147";

    public static List<CalendarEntry> readNotesCalendarEntries(String server, String filePath) {
        NotesDatabase database = new NotesDatabase(server, filePath, "");

        NotesCollection calendar = database.openCollectionByName("Calendar");

        String startPos = "0";
        int skipCount = 1;
        EnumSet<Navigate> navigationType = EnumSet.of(Navigate.NEXT_NONCATEGORY);
        int count = Integer.MAX_VALUE;
        int preloadEntryCount = Integer.MAX_VALUE;

        EnumSet<ReadMask> readMask = EnumSet.of(ReadMask.NOTEUNID, ReadMask.SUMMARYVALUES);

        List<NotesViewEntryData> viewEntries = calendar.getAllEntries(startPos,
                skipCount, navigationType, preloadEntryCount, readMask,
                new NotesCollection.EntriesAsListCallback(count));

        List<CalendarEntry> entries = new ArrayList<>();

        for (NotesViewEntryData currEntry : viewEntries) {
            currEntry.setPreferNotesTimeDates(true);

            String subject = currEntry.getAsString(COLUMN_SUBJECT, null);
            Calendar start = currEntry.getAsCalendar(COLUMN_START, null);
            Calendar end = currEntry.getAsCalendar(COLUMN_END, null);

            if (start.after(Calendar.getInstance())) {
                entries.add(new CalendarEntry(currEntry.getUNID(), subject, start, end));
            }
        }

        return entries;
    }
}
