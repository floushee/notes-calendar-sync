package com.github.floushee.notescalendarsync;

import com.mindoo.domino.jna.gc.NotesGC;
import com.mindoo.domino.jna.utils.IDUtils;
import com.mindoo.domino.jna.utils.NotesInitUtils;
import lotus.domino.NotesThread;

import java.util.List;

final class App {

    public static void main(String[] args) {
        boolean notesInitialized = false;
        try {

            NotesInitUtils.notesInitExtended(new String[0]);
            notesInitialized = true;

            NotesThread.sinitThread();

            NotesGC.runWithAutoGC(() -> {
                System.out.println("Username of Notes ID: "+IDUtils.getIdUsername());
                run();
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            NotesThread.stermThread();
            if (notesInitialized) {
                NotesInitUtils.notesTerm();
            }
        }
    }

    private static void run() {
        List<CalendarEntry> entries = NotesCalendarReader.readNotesCalendarEntries("", "");
        entries.forEach(entry -> {
            System.out.println(entry);
        });
    }
}
