package com.github.floushee.notescalendarsync;

import com.mindoo.domino.jna.gc.NotesGC;
import com.mindoo.domino.jna.utils.IDUtils;
import com.mindoo.domino.jna.utils.NotesInitUtils;

import java.util.List;
import java.util.Optional;

final class App {

    public static void main(String[] args) {

        Config config = Config.fromArgs(args);

        NotesThread.run(config, () -> {
            return NotesCalendarReader.readNotesCalendarEntries(config.getServer(), config.getFilePath());
        }).ifPresent(entries -> {
            entries.forEach(entry -> {
                System.out.println(entry);
            });
                });
    }
}
