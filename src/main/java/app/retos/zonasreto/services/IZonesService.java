package app.retos.zonasreto.services;

import app.retos.zonasreto.models.Zones;

import java.util.List;

public interface IZonesService {

    List<Zones> encontrarZonas();

    Zones encontrarZonas(Integer codigo);

    Boolean crearZonas(Zones zones);

    Integer crearZonasPostes(Integer idEvents, List<Double> location);

    Integer crearZonasEvents(String idEvents, List<Double> location);

    Integer modificarZonasEvents(String idEvents, List<Double> location, Integer zoneCode);
}
