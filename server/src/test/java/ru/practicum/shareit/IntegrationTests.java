package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingServise;
import ru.practicum.shareit.item.ItemServise;
import ru.practicum.shareit.user.UserServise;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class IntegrationTests {
    private final BookingServise bookingServise;
    private final ItemServise itemServise;
    private final UserServise userServise;

}