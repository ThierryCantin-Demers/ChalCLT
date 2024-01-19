package ca.ulaval.glo2004.gui.events;

import ca.ulaval.glo2004.util.math.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that manages custom events using the observer pattern.
 * Observers can subscribe to an event type with a callback function which will be called when the event is triggered.
 */
public class EventManager {
    /**
     * Interface for the callback function of an event.
     */
    public interface Event {
        void execute();
    }

    /**
     * The map of observers for each event type.
     */
    private final Map<EventType, ArrayList<Pair<Observer, Event>>> listeners = new HashMap<>();

    /**
     * Creates a new event manager.
     */
    public EventManager() {
        for (EventType eventType : EventType.values()) {
            this.listeners.put(eventType, new ArrayList<>());
        }
    }

    /**
     * Subscribes an observer to an event type with a callback function.
     * @param eventType_ : The event type to subscribe to.
     * @param observer_ : The observer to subscribe.
     * @param event_ : The callback function to call when the event is triggered.
     */
    public void subscribe(EventType eventType_, Observer observer_, Event event_) {
        ArrayList<Pair<Observer, Event>> users = listeners.get(eventType_);
        users.add(new Pair<>(observer_, event_));
    }

    /**
     * Unsubscribes an observer from an event type.
     * @param eventType_ : The event type to unsubscribe from.
     * @param observer_ : The observer to unsubscribe.
     */
    public void unsubscribe(EventType eventType_, Observer observer_) {
        ArrayList<Pair<Observer, Event>> users = listeners.get(eventType_);
        users.removeIf((pair -> (pair.getKey() == observer_)));
    }

    /**
     * Unsubscribes an observer from all events.
     * @param observer_ : The observer to unsubscribe.
     */
    public void unsubscribe(Observer observer_) {
        for(Map.Entry<EventType, ArrayList<Pair<Observer, Event>>> eventTypePair: listeners.entrySet())
        {
            eventTypePair.getValue().removeIf((pair -> (pair.getKey().equals(observer_))));
        }
    }

    /**
     * Notifies all observers of an event type.
     * Executes the callback function of each observer that is subscribed to the specified event type.
     * @param eventType : The event type to notify.
     */
    public void notify(EventType eventType) {
        if(!listeners.containsKey(eventType))
            return;
        ArrayList<Pair<Observer, Event>> users = (ArrayList<Pair<Observer, Event>>) listeners.get(eventType).clone();
        for (Pair<Observer, Event> eventPair : users) {
            eventPair.getValue().execute();
        }
    }
}