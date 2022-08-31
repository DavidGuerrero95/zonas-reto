package app.retos.zonasreto.repository;

import app.retos.zonasreto.models.Zones;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

public interface ZoneRepository extends MongoRepository<Zones, String> {

    @RestResource(path = "find-zone")
    Zones findByZoneCode(@Param("zoneCode") Integer zoneCode);

    @RestResource(path = "exists-muro")
    Boolean existsByZoneCode(@Param("zoneCode") Integer zoneCode);

}
