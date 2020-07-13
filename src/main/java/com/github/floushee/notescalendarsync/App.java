package com.github.floushee.notescalendarsync;

final class App {

    public static void main(String[] args) {

        Config config = Config.fromArgs(args);

        NotesThread.run(config.getUserId(), config.getPassword(), () -> {
            return NotesCalendar.readNotesCalendarEntries(config.getServer(), config.getFilePath());
        }).ifPresent(entries -> {
            entries.forEach(entry -> {
                System.out.println(entry);
            });
        });
    }
}
