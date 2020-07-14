package com.github.floushee.notescalendarsync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static com.github.floushee.notescalendarsync.GoogleCalendarApi.accessGoogleCalendar;
import static com.github.floushee.notescalendarsync.NotesCalendar.readNotesCalendarEvents;
import static com.github.floushee.notescalendarsync.NotesThread.runNotesThread;
import static java.lang.System.exit;

final class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {

        Config config = Config.fromArgs(args);

        Optional<GoogleCalendarSyncService> googleSync = accessGoogleCalendar(config.getGoogleClientCredentialsFile(), config.getGoogleOAuthTokensDirectory())
                .map(api -> new GoogleCalendarSyncService(api, config.getGoogleCalendarId()));

        if (!googleSync.isPresent()) {
            logger.error("Could not access Google calendar API. Shutting down application.");
            exit(1);
        }

        runNotesThread(config.getNotesUserId(), config.getNotesUserPassword(), () -> {
            return readNotesCalendarEvents(config.getNotesServer(), config.getNotesDatabasePath());
        }).ifPresent(entries -> googleSync.get().syncEvents(entries));
    }
}
