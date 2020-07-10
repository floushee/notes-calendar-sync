package com.github.floushee.notescalendarsync;

import java.text.SimpleDateFormat;
import java.util.Calendar;

final class CalendarEntry {

    private final String id;
    private final String subject;
    private final Calendar start;
    private final Calendar end;
    private final boolean allDay;

    public CalendarEntry(String id, String subject, Calendar start, Calendar end) {
        this.id = id;
        this.subject = subject;
        this.start = start;
        if (end == null) {
            this.end = start;
            allDay = true;
        } else {
            this.end = end;
            allDay = false;
        }
    }

    public String getSubject() {
        return subject;
    }

    public Calendar getStart() {
        return start;
    }

    public Calendar getEnd() {
        return end;
    }

    public boolean isAllDay() {
        return allDay;
    }

    @Override
    public String toString() {

        SimpleDateFormat formatter = allDay ? new SimpleDateFormat("dd.MM.yyyy") : new SimpleDateFormat("dd.MM.yyyy HH:mm");

        return "CalendarEntry{" +
                "subject='" + subject + '\'' +
                ", start=" + formatter.format(start.getTime()) +
                ", end=" + formatter.format(end.getTime()) +
                ", allDay=" + allDay +
                '}';
    }
}
