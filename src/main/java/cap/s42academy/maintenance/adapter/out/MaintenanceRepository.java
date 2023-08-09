package cap.s42academy.maintenance.adapter.out;

import cap.s42academy.maintenance.domain.entity.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {

    @Query("""
            select 
                count (m.id) > 0 
            from Maintenance m 
            where m.maintenanceStatus = cap.s42academy.maintenance.domain.valueobject.MaintenanceStatus.ACTIVE
            """)
    boolean existsActiveMaintenanceMode();
}
