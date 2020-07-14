package com.github.floushee.notescalendarsync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class Config {

    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    private final String notesServer;
    private final String notesDatabasePath;
    private final String notesUserId;
    private final String notesUserPassword;
    private final String googleCalendarId;
    private final String googleClientCredentialsFile;
    private final String googleOAuthTokensDirectory;

    private Config(String notesServer, String notesDatabasePath, String notesUserId, String notesUserPassword, String googleCalendarId, String googleClientCredentialsFile, String googleOAuthTokensDirectory) {
        this.notesServer = notesServer;
        this.notesDatabasePath = notesDatabasePath;
        this.notesUserId = notesUserId;
        this.notesUserPassword = notesUserPassword;
        this.googleCalendarId = googleCalendarId;
        this.googleClientCredentialsFile = googleClientCredentialsFile;
        this.googleOAuthTokensDirectory = googleOAuthTokensDirectory;
    }

    public static Config fromArgs(String[] args) {
        if (args.length != 7) {
            logger.error("Config is not complete");
            throw new IllegalStateException("Usage: nodes-calender-sync.jar <server> <filepath> <userId> <password> <calendarId> <client-credentials-file>");
        }
        return new Config(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
    }

    public String getNotesServer() {
        return notesServer;
    }

    public String getNotesDatabasePath() {
        return notesDatabasePath;
    }

    public String getNotesUserId() {
        return notesUserId;
    }

    public String getNotesUserPassword() {
        return notesUserPassword;
    }

    public String getGoogleCalendarId() {
        return googleCalendarId;
    }

    public String getGoogleClientCredentialsFile() {
        return googleClientCredentialsFile;
    }

    public String getGoogleOAuthTokensDirectory() {
        return googleOAuthTokensDirectory;
    }
}
