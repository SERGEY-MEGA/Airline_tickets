package digital.zil.hl.module1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import digital.zil.hl.module1.repository.BookingRepository;
import digital.zil.hl.module1.repository.FlightRepository;
import digital.zil.hl.module1.repository.PassengerRepository;
import digital.zil.hl.module1.repository.jpa.JpaBookingRepository;
import digital.zil.hl.module1.repository.jpa.JpaFlightRepository;
import digital.zil.hl.module1.repository.jpa.JpaPassengerRepository;
import digital.zil.hl.module1.repository.jpa.springdata.SpringDataBookingJpaRepository;
import digital.zil.hl.module1.repository.jpa.springdata.SpringDataFlightJpaRepository;
import digital.zil.hl.module1.repository.jpa.springdata.SpringDataPassengerJpaRepository;
import digital.zil.hl.module1.repository.memory.InMemoryBookingRepository;
import digital.zil.hl.module1.repository.memory.InMemoryFlightRepository;
import digital.zil.hl.module1.repository.memory.InMemoryPassengerRepository;

/**
 * Явно объявляет реализации репозиториев для каждого профиля.
 * Это делает wiring стабильным и прозрачным для LAB6/LAB7 и Docker-окружения.
 */
@Configuration
public class RepositoryConfiguration {

    @Bean
    @Profile("postgres")
    public FlightRepository flightRepository(SpringDataFlightJpaRepository repository) {
        return new JpaFlightRepository(repository);
    }

    @Bean
    @Profile("postgres")
    public PassengerRepository passengerRepository(SpringDataPassengerJpaRepository repository) {
        return new JpaPassengerRepository(repository);
    }

    @Bean
    @Profile("postgres")
    public BookingRepository bookingRepository(
            SpringDataBookingJpaRepository repository,
            SpringDataFlightJpaRepository flightRepository
    ) {
        return new JpaBookingRepository(repository, flightRepository);
    }

    @Bean
    @Profile("memory")
    public FlightRepository memoryFlightRepository() {
        return new InMemoryFlightRepository();
    }

    @Bean
    @Profile("memory")
    public PassengerRepository memoryPassengerRepository() {
        return new InMemoryPassengerRepository();
    }

    @Bean
    @Profile("memory")
    public BookingRepository memoryBookingRepository() {
        return new InMemoryBookingRepository();
    }
}
