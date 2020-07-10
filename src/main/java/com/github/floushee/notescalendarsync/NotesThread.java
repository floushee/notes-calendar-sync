package com.github.floushee.notescalendarsync;

import com.mindoo.domino.jna.gc.NotesGC;
import com.mindoo.domino.jna.utils.IDUtils;
import com.mindoo.domino.jna.utils.NotesInitUtils;

import java.util.Optional;
import java.util.function.Supplier;

public class NotesThread {

    public static <T> Optional<T> run(Config config, Supplier<T> supplier) {

        boolean notesInitialized = false;

        T result = null;

        try {

            NotesInitUtils.notesInitExtended(new String[0]);
            notesInitialized = true;

            lotus.domino.NotesThread.sinitThread();

            result = NotesGC.runWithAutoGC(() -> {
                IDUtils.switchToId(config.getUserId(), config.getPassword(), true);
                System.out.println("Username of Notes ID: "+IDUtils.getIdUsername());
                return supplier.get();
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lotus.domino.NotesThread.stermThread();
            if (notesInitialized) {
                NotesInitUtils.notesTerm();
            }
        }
        return Optional.ofNullable(result);
    }
}
