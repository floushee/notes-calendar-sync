package com.github.floushee.notescalendarsync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

import static java.lang.String.format;
import static java.util.Optional.empty;

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


    public static Optional<Config> fromFile(String configFile) {

        try (InputStream in = new FileInputStream(configFile)) {

            Properties props = new Properties();
            props.load(in);

            String notesServer = props.getProperty("notes.server");
            String notesDatabasePath = props.getProperty("notes.database.path");
            String notesUserId = props.getProperty("notes.user.id");
            String notesUserPassword = props.getProperty("notes.user.password");
            String googleCalendarId = props.getProperty("google.calendar.id");
            String googleClientCredentialsFile = props.getProperty("google.client.credentials.file");
            String googleOAuthTokensDirectory = props.getProperty("google.oauth.tokens.directory");

            if (isNullOrEmpty(notesServer)
                    || isNullOrEmpty(notesDatabasePath)
                    || isNullOrEmpty(notesUserId)
                    || isNullOrEmpty(notesUserPassword)
                    || isNullOrEmpty(googleCalendarId)
                    || isNullOrEmpty(googleClientCredentialsFile)
                    || isNullOrEmpty(googleOAuthTokensDirectory)
            ) {
                logger.error("The config file is not complete");
                return empty();
            }

            return Optional.of(new Config(notesServer, notesDatabasePath, notesUserId, notesUserPassword, googleCalendarId, googleClientCredentialsFile, googleOAuthTokensDirectory));
        } catch (Exception e) {
            logger.error(format("Could not read config file from %s", configFile), e);
            return empty();
        }
    }

    private static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
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
