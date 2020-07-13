package com.github.floushee.notescalendarsync;

final class Config {

    private final String server;
    private final String filePath;
    private final String userId;
    private final String password;
    private final String calendarId;
    private final String credentialsFile;
    private final String tokensPath;

    private Config(String server, String filePath, String userId, String password, String calendarId, String credentialsFile, String tokensPath) {
        this.server = server;
        this.filePath = filePath;
        this.userId = userId;
        this.password = password;
        this.calendarId = calendarId;
        this.credentialsFile = credentialsFile;
        this.tokensPath = tokensPath;
    }

    public static Config fromArgs(String[] args) {
        if (args.length != 7) {
            throw new IllegalStateException("Usage: nodes-calender-sync.jar <server> <filepath> <userId> <password> <calendarId> <client-credentials-file>");
        }
        return new Config(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
    }

    public String getServer() {
        return server;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getCalendarId() {
        return calendarId;
    }

    public String getCredentialsFile() {
        return credentialsFile;
    }

    public String getTokensPath() {
        return tokensPath;
    }
}
