package controller.other;

import java.util.*;

public class RunEvent implements EventListener {
    private final Map<MainFrame.Event, List<Runnable>> events;

    public RunEvent() {
        events = new TreeMap<>();
    }

    /**
     * registers an event listener
     * @param event the event to listen to
     * @param runnable a callback for when the event happens
     */
    public void addListener(MainFrame.Event event, Runnable runnable) {
        List<Runnable> listeners = events.computeIfAbsent(event, k -> new ArrayList<>());
        listeners.add(runnable);
    }

    /**
     * run in event map
     * @param event the event that happened
     */
    public void emit(MainFrame.Event event) {
        if (events.get(event) != null)
            events.get(event).forEach(Runnable::run);
    }
}