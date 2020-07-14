package com.github.floushee.notescalendarsync;

import java.time.LocalDateTime;

final class CalendarEvent {

    private final String id;
    private final String subject;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final boolean allDay;

    public CalendarEvent(String id, String subject, LocalDateTime start, LocalDateTime end) {
        this.id = id;
        this.subject = subject;
        this.start = start;
        if (end == null) {
            allDay = true;
            this.end = start;
        } else {
            allDay = false;
            this.end = end;
        }
    }

    public String getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public boolean isAllDay() {
        return allDay;
    }

    @Override
    public String toString() {
        return "CalendarEntry{" +
                "id='" + id + '\'' +
                "subject='" + subject + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", allDay=" + allDay +
                '}';
    }
}
