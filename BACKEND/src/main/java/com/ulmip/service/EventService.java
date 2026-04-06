package com.ulmip.service;

import com.ulmip.model.Event;
import com.ulmip.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAllByOrderByDateAscTimeAsc();
    }

    public List<Event> getUpcomingEvents() {
        return eventRepository.findByDateGreaterThanEqualOrderByDateAscTimeAsc(LocalDate.now());
    }

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
}
