package com.github.floushee.notescalendarsync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.floushee.notescalendarsync.GoogleCalendarApi.accessGoogleCalendar;
import static com.github.floushee.notescalendarsync.NotesCalendar.readNotesCalendarEvents;
import static com.github.floushee.notescalendarsync.NotesThread.runNotesThread;
import static java.lang.System.exit;

final class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {

        if (args.length != 1) {
            logger.error("Usage: java -jar notes-calendar-sync.jar <path-to-config-file>");
            exit(1);
        }

        Config.fromFile(args[0])
                .ifPresent(config -> {
                    accessGoogleCalendar(config.getGoogleClientCredentialsFile(), config.getGoogleOAuthTokensDirectory())
                            .map(api -> new GoogleCalendarSyncService(api, config.getGoogleCalendarId()))
                            .ifPresent(googleSync -> {
                                runNotesThread(config.getNotesUserId(), config.getNotesUserPassword(), () -> {
                                    return readNotesCalendarEvents(config.getNotesServer(), config.getNotesDatabasePath());
                                }).ifPresent(entries -> googleSync.syncEvents(entries));
                            });
                });
    }
}
