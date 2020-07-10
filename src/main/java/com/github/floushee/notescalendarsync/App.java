package com.github.floushee.notescalendarsync;

import com.mindoo.domino.jna.gc.NotesGC;
import com.mindoo.domino.jna.utils.IDUtils;
import com.mindoo.domino.jna.utils.NotesInitUtils;
import lotus.domino.NotesThread;

import java.util.List;

import static java.lang.System.exit;

final class App {

    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Usage: app.jar <server>, <filepath>");
            exit(1);
        }

        String server = args[0];
        String filePath = args[1];

        boolean notesInitialized = false;
        try {

            NotesInitUtils.notesInitExtended(new String[0]);
            notesInitialized = true;

            NotesThread.sinitThread();

            NotesGC.runWithAutoGC(() -> {
                System.out.println("Username of Notes ID: "+IDUtils.getIdUsername());
                run(server, filePath);
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
            exit(1);
        } finally {
            NotesThread.stermThread();
            if (notesInitialized) {
                NotesInitUtils.notesTerm();
            }
        }
        exit(0);
    }

    private static void run(String server, String filePath) {
        List<CalendarEntry> entries = NotesCalendarReader.readNotesCalendarEntries(server, filePath);
        entries.forEach(entry -> {
            System.out.println(entry);
        });
    }
}
