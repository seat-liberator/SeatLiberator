package com.seatliberator.seatliberator.reservation.book.infrastructure.persistence.jpa;

import com.seatliberator.seatliberator.reservation.book.application.port.out.SeatStore;
import com.seatliberator.seatliberator.reservation.book.infrastructure.persistence.jpa.repository.SeatRepository;
import com.seatliberator.seatliberator.reservation.domain.Seat;
import com.seatliberator.seatliberator.reservation.shared.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.shared.infrastructure.persistence.jpa.specification.CommonPredicates;
import com.seatliberator.seatliberator.reservation.shared.infrastructure.persistence.jpa.specification.SeatLocatorPredicates;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.DeleteSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaSeatStore implements SeatStore {

    private final SeatRepository repository;

    @Override
    public void save(Seat seat) {
        repository.save(seat);
    }

    @Override
    public Optional<Seat> findByRoomIdAndSeatId(String roomId, String seatId) {
        return repository.findByLocator_RoomIdAndLocator_SeatId(roomId, seatId);
    }

    @Override
    public Optional<Seat> findByLocator(SeatLocator locator) {
        return repository.findByLocator_RoomIdAndLocator_SeatId(locator.roomId(), locator.seatId());
    }

    @Override
    public Optional<Seat> findForUpdate(String roomId, String seatId) {
        return repository.findForUpdate(roomId, seatId);
    }

    @Override
    public void deleteByRoomIdAndSeatId(String roomId, String seatId) {
        repository.deleteByLocator_RoomIdAndLocator_SeatId(roomId, seatId);
    }

    @Override
    public void deleteByLocator(SeatLocator locator) {
        var spec = createLocatorDeleteSpecification(locator);
        repository.delete(spec);
    }

    @Override
    public boolean existsSeatConflict(String roomId, String seatId) {
        return repository.existsSeatConflict(roomId, seatId);
    }

    @Override
    public boolean existsByLocator(SeatLocator locator) {
        var spec = createLocatorSpecification(locator);
        return repository.exists(spec);
    }

    @Override
    public boolean existsSeatConflictExcept(Long id, String roomId, String seatId) {
        return repository.existsSeatConflictExcept(id, roomId, seatId);
    }

    @Override
    public boolean existsByLocatorWithExcludeIds(SeatLocator locator, Collection<Long> ids) {
        var spec = createLocatorSpecification(locator)
                .and(CommonPredicates.excludeIn(ids, from -> from.get("id")));
        return repository.exists(spec);
    }

    private Specification<Seat> createLocatorSpecification(SeatLocator locator) {
        return Specification.<Seat>unrestricted()
                .and(SeatLocatorPredicates.eq(locator, SeatLocatorPredicates.defaultLocatorPathFunction()));
    }

    private DeleteSpecification<Seat> createLocatorDeleteSpecification(SeatLocator locator) {
        return DeleteSpecification.<Seat>unrestricted()
                .and(SeatLocatorPredicates.eq(locator, SeatLocatorPredicates.defaultLocatorPathFunction()));
    }
}
