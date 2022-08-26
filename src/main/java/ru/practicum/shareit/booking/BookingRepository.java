package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Collection<Booking> findByBooker_IdOrderByStartDesc(long id);
    Page<Booking> findByBooker_IdOrderByStartDesc(Pageable pageable, long id);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id=:id AND b.end<:nowTime AND upper(b.status) = UPPER('APPROVED')" +
            "ORDER BY b.start DESC")
    Collection<Booking> findByBookerIdStatePast(@Param("id") long id, @Param("nowTime") long nowTime);

    Collection<Booking> findByBooker_IdAndStatus(long id, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.booker.id=:useId AND b.end >= :nowTime AND :nowTime >= b.start " +
            "ORDER BY b.start DESC")
    Collection<Booking> findByBookerIdStateCurrent(@Param("useId") long useId, @Param("nowTime") long nowTime);

    @Query(value = "SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.start > :dateNow ORDER BY b.start DESC")
    Collection<Booking> findFuture(@Param("userId") long useId, @Param("dateNow") long dateNow);

    @Query("SELECT b FROM Booking b JOIN b.item i ON b.item = i WHERE i.owner.id = :ownerId ORDER BY b.start DESC")
    Collection<Booking> findOwnerAll(long ownerId);
    @Query("SELECT b FROM Booking b JOIN b.item i ON b.item = i WHERE i.owner.id = :ownerId ORDER BY b.start DESC")
    Page<Booking> findOwnerAll(Pageable pageable, long ownerId);

    @Query("SELECT b FROM Booking b JOIN b.item i ON b.item = i WHERE  i.owner.id = :userId AND b.start> :timeNow " +
            "ORDER BY b.start DESC")
    Collection<Booking> findOwnerFuture(@Param("userId") long userId, @Param("timeNow") long timeNow);

    @Query("SELECT b FROM Booking b JOIN b.item i ON b.item = i WHERE i.owner.id = :userId " +
            "AND b.start <= :timeNow AND b.end >= :timeNow ORDER BY b.start DESC ")
    Collection<Booking> findOwnerCurrent(@Param("userId") long userId, @Param("timeNow") long timeNow);

    @Query("SELECT b FROM Booking b JOIN b.item i ON b.item = i WHERE i.owner.id = :userId AND b.status = :status")
    Collection<Booking> findByOwnerIdAndStatus(@Param("userId") long userId, @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b JOIN b.item i ON b.item = i WHERE i.owner.id = :userId AND b.end< :timeNow")
    Collection<Booking> findOwnerPast(@Param("userId") long userId, @Param("timeNow") long timeNow);

    @Query("SELECT  b FROM Booking b WHERE b.item.id= :itemId AND b.start<:timeNow AND b.end < :timeNow")
    Collection<Booking> findLastBookingToItem(@Param("itemId") long itemId, @Param("timeNow") long timeNow);

    @Query("SELECT  b FROM Booking b WHERE b.item.id= :itemId AND  b.start > :timeNow ")
    Collection<Booking> findNextBookingToItem(@Param("itemId") long itemId, @Param("timeNow") long timeNow);

    @Query(value = "SELECT COUNT(DISTINCT b.user_id) FROM bookings  b " +
            "WHERE b.user_id = :userId AND b.item_id= :itemId " +
            "AND b.status = 'APPROVED' AND b.start_time<= :timeNow", nativeQuery = true)
    int usedCount(@Param("userId") long userId, @Param("itemId") long itemId, @Param("timeNow") long timeNow);
}
