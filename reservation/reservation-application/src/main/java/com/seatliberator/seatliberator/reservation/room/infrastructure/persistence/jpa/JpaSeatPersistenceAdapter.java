package com.seatliberator.seatliberator.reservation.room.infrastructure.persistence.jpa;

import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.persistence.Seat;
import com.seatliberator.seatliberator.reservation.room.application.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.room.application.port.out.SeatStore;
import com.seatliberator.seatliberator.reservation.room.application.port.out.criteria.SeatExclusion;
import com.seatliberator.seatliberator.reservation.room.infrastructure.persistence.jpa.repository.SeatRepository;
import com.seatliberator.seatliberator.reservation.shared.infrastructure.persistence.jpa.specification.CommonPredicates;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.DeleteSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaSeatPersistenceAdapter implements SeatStore, SeatReader {

    private final SeatRepository repository;

    @Override
    public void save(Seat seat) {
        repository.save(seat);
    }

    @Override
    public Optional<Seat> findByRoomIdAndSeatId(String roomId, String seatId) {
        return repository.findByRoom_RoomIdAndSeatId(roomId, seatId);
    }

    @Override
    public Optional<Seat> findByLocator(SeatLocator locator) {
        return repository.findByRoom_RoomIdAndSeatId(locator.roomId(), locator.seatId());
    }

    @Override
    public Optional<Seat> findForUpdate(String roomId, String seatId) {
        return repository.findForUpdate(roomId, seatId);
    }

    @Override
    public List<Seat> findByRoomId(String roomId) {
        return repository.findByRoom_RoomId(roomId);
    }

    @Override
    public void deleteByLocator(SeatLocator locator) {
        var spec = createLocatorDeleteSpecification(locator);
        repository.delete(spec);
    }

    @Override
    public boolean existsByLocator(SeatLocator locator) {
        var spec = createLocatorSpecification(locator);
        return repository.exists(spec);
    }

    @Override
    public boolean existsByLocator(SeatLocator locator, SeatExclusion exclusion) {
        var spec = createLocatorSpecification(locator)
                .and(CommonPredicates.excludeIn(exclusion.ids(), from -> from.get("id")));
        return repository.exists(spec);
    }

    private Specification<Seat> createLocatorSpecification(SeatLocator locator) {
        return Specification.<Seat>unrestricted()
                .and(CommonPredicates.eq(locator.roomId(), from -> from.get("room").get("roomId")))
                .and(CommonPredicates.eq(locator.seatId(), from -> from.get("seatId")));
    }

    private DeleteSpecification<Seat> createLocatorDeleteSpecification(SeatLocator locator) {
        return DeleteSpecification.<Seat>unrestricted()
                .and(CommonPredicates.eq(locator.roomId(), from -> from.get("room").get("roomId")))
                .and(CommonPredicates.eq(locator.seatId(), from -> from.get("seatId")));
    }
}
