package com.github.floushee.notescalendarsync;

import com.mindoo.domino.jna.utils.IDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Supplier;

import static com.mindoo.domino.jna.gc.NotesGC.runWithAutoGC;
import static com.mindoo.domino.jna.utils.IDUtils.switchToId;
import static com.mindoo.domino.jna.utils.NotesInitUtils.notesInitExtended;
import static com.mindoo.domino.jna.utils.NotesInitUtils.notesTerm;
import static java.util.Optional.ofNullable;

final class NotesThread {

    private static final Logger logger = LoggerFactory.getLogger(NotesThread.class);

    public static <T> Optional<T> runNotesThread(String userId, String password, Supplier<T> supplier) {

        boolean notesInitialized = false;

        T result = null;

        try {

            logger.info("Initializing Notes thread...");
            notesInitExtended(new String[0]);
            logger.info("Successfully initialized Notes thread");

            notesInitialized = true;

            result = runWithAutoGC(() -> {
                switchToId(userId, password, true);
                logger.info("Switched to user id: " + IDUtils.getIdUsername());
                return supplier.get();
            });
        } catch (Exception e) {
            logger.error("Could not execute Notes thread", e);
        } finally {
            // terminating the thread on my mac os machine is causing nsd
            if (!isRunningOnMac()) {
                if (notesInitialized) {
                    logger.info("Terminating Notes thread...");
                    notesTerm();
                    logger.info("Successfully terminated Notes thread.");
                }
            } else {
                logger.warn("Skipped Notes thread termination because the app is running oin macOS");
            }
        }
        return ofNullable(result);
    }

    private static boolean isRunningOnMac() {
        return "Mac OS X".equals(System.getProperty("os.name"));
    }
}
