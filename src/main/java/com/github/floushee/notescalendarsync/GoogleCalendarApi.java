package com.github.floushee.notescalendarsync;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;

final class GoogleCalendarApi {

    private static final Logger logger = LoggerFactory.getLogger(GoogleCalendarApi.class);

    private static final String APPLICATION_NAME = "Notes Calendar Sync";

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_EVENTS);

    public static Optional<Calendar> accessGoogleCalendar(String credentialsFile, String tokensPath) {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            return Optional.of(new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, setHttpTimeout(getCredentials(HTTP_TRANSPORT, credentialsFile, tokensPath)))
                    .setApplicationName(APPLICATION_NAME)
                    .build());
        } catch (Exception e) {
            logger.error("Could not access Google calendar API", e);
            return empty();
        }
    }

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, String credentialsFile, String tokensPath) throws IOException {
        // Load client secrets.
        InputStream in = new FileInputStream(credentialsFile);
        if (in == null) {
            throw new FileNotFoundException("Credentials file not found: " + credentialsFile);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(tokensPath)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static HttpRequestInitializer setHttpTimeout(final HttpRequestInitializer requestInitializer) {
        return new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest httpRequest) throws IOException {
                requestInitializer.initialize(httpRequest);
                httpRequest.setConnectTimeout(30 * 60000);  // 3 minutes connect timeout
                httpRequest.setReadTimeout(30 * 60000);  // 3 minutes read timeout
            }
        };
    }
}
