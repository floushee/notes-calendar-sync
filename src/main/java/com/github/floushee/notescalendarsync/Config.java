package com.github.floushee.notescalendarsync;

final class Config {

    private final String server;
    private final String filePath;
    private final String userId;
    private final String password;

    private Config(String server, String filePath, String userId, String password) {
        this.server = server;
        this.filePath = filePath;
        this.userId = userId;
        this.password = password;
    }

    public static Config fromArgs(String[] args) {
        if (args.length != 4) {
            throw new IllegalStateException("Usage: nodes-calender-sync.jar <server> <filepath> <userId> <password>");
        }
        return new Config(args[0], args[1], args[2], args[3]);
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
}
