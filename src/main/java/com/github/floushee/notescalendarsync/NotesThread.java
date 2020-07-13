package com.github.floushee.notescalendarsync;

import com.mindoo.domino.jna.gc.NotesGC;
import com.mindoo.domino.jna.utils.IDUtils;
import com.mindoo.domino.jna.utils.NotesInitUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Supplier;

public class NotesThread {

    private static final Logger logger = LoggerFactory.getLogger(NotesThread.class);

    public static <T> Optional<T> run(String userId, String password, Supplier<T> supplier) {

        boolean notesInitialized = false;

        T result = null;

        try {

            NotesInitUtils.notesInitExtended(new String[0]);
            notesInitialized = true;

            result = NotesGC.runWithAutoGC(() -> {
                IDUtils.switchToId(userId, password, true);
                System.out.println("Username of Notes ID: " + IDUtils.getIdUsername());
                return supplier.get();
            });
        } catch (Exception e) {
            logger.error("Could not execute Notes thread", e);
        } finally {
            // terminating the thread on my mac os machine is causing nsd
            if (!isRunningOnMac()) {
                lotus.domino.NotesThread.stermThread();
                if (notesInitialized) {
                    NotesInitUtils.notesTerm();
                }
            }
        }
        return Optional.ofNullable(result);
    }

    private static boolean isRunningOnMac() {
        return "Mac OS X".equals(System.getProperty("os.name"));
    }
}
