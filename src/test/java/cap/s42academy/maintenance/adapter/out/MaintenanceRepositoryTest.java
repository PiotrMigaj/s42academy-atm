package cap.s42academy.maintenance.adapter.out;

import cap.s42academy.maintenance.domain.entity.Maintenance;
import cap.s42academy.maintenance.domain.valueobject.MaintenanceStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;

import static cap.s42academy.SampleTestDataFactory.maintenance;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MaintenanceRepositoryTest {

    @Autowired
    private MaintenanceRepository maintenanceRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void shouldReturnTrue_whenAtmIsInMaintenanceMode(){
        //given
        Maintenance maintenance = maintenance(MaintenanceStatus.ACTIVE, LocalDateTime.now(), null);
        testEntityManager.persist(maintenance);
        //when
        boolean result = maintenanceRepository.existsActiveMaintenanceMode();
        //then
        assertThat(result).isTrue();
    }
    @Test
    void shouldReturnFalse_whenThereIsNoMaintenanceModeStoredInDb(){
        //given
        //when
        boolean result = maintenanceRepository.existsActiveMaintenanceMode();
        //then
        assertThat(result).isFalse();
    }
    @Test
    void shouldReturnTrue_whenThereIsFinishedMaintenanceModeStoredInDb(){
        //given
        Maintenance maintenance = maintenance(MaintenanceStatus.FINISHED, LocalDateTime.now(), LocalDateTime.now());
        testEntityManager.persist(maintenance);
        //when
        boolean result = maintenanceRepository.existsActiveMaintenanceMode();
        //then
        assertThat(result).isFalse();
    }
}
