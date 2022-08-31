package app.retos.zonasreto.services;

import app.retos.zonasreto.models.Zones;
import app.retos.zonasreto.repository.ZoneRepository;
import com.mongodb.MongoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ZoneServiceImpl implements IZonesService {

    @Autowired
    ZoneRepository zoneRepository;

    @Override
    public List<Zones> encontrarZonas() {
        return zoneRepository.findAll();
    }

    @Override
    public Zones encontrarZonas(Integer codigo) {
        return zoneRepository.findByZoneCode(codigo);
    }

    @Override
    public Boolean crearZonas(Zones zones) {
        zones.setLocation(new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(zones.getLocation().get(0)).setScale(5, RoundingMode.HALF_UP).doubleValue(),
                BigDecimal.valueOf(zones.getLocation().get(1)).setScale(5, RoundingMode.HALF_UP).doubleValue())));
        zones.setZoneCode(zoneRepository.findAll().size());
        try {
            zoneRepository.save(zones);
            return true;
        } catch (MongoException e) {
            log.error("Error en la edición: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Integer crearZonasEvents(String idEvents, List<Double> location) {
        Zones newZone = new Zones();
        if (zoneRepository.findAll().isEmpty()) {
            newZone.setZoneCode(zoneRepository.findAll().size());
            newZone.setLocation(location);
            List<String> listaPrimera = new ArrayList<>(Collections.singletonList(idEvents));
            newZone.setIdEvents(listaPrimera);
            zoneRepository.save(newZone);
        } else {
            boolean bandera1 = false;
            for (int i = 0; i < zoneRepository.findAll().size(); i++) {
                Double distancia = distanciaCoord(zoneRepository.findAll().get(i).getLocation(), location);
                if (distancia <= 1 && !bandera1) {
                    newZone = zoneRepository.findByZoneCode(i);
                    List<String> listEvents = newZone.getIdEvents();
                    listEvents.add(idEvents);
                    newZone.setIdEvents(listEvents);
                    if (listEvents.size() <= 4) {
                        List<Double> listaNuevaLocalizacion = distanciaMedia(newZone.getLocation(), location);
                        newZone.setLocation(listaNuevaLocalizacion);
                    }
                    zoneRepository.save(newZone);
                    bandera1 = true;
                }
            }
            if (!bandera1) {
                List<String> lista = new ArrayList<>();
                newZone.setZoneCode(zoneRepository.findAll().size());
                newZone.setLocation(location);
                lista.add(idEvents);
                newZone.setIdEvents(lista);
                zoneRepository.save(newZone);
            }
        }
        return newZone.getZoneCode();
    }

    private Double distanciaCoord(List<Double> pos1, List<Double> pos2) {
        // double radioTierra = 3958.75;//en millas
        Double lat1 = pos1.get(0);
        Double lat2 = pos2.get(0);
        Double lon1 = pos1.get(1);
        Double lon2 = pos2.get(1);
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return (double) 0;
        } else {
            Double theta = lon1 - lon2;
            Double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2))
                    + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            dist = dist * 1.609344;
            return dist;
        }
    }

    private List<Double> distanciaMedia(List<Double> pos1, List<Double> pos2) {
        List<Double> lista = new ArrayList<Double>();
        Double lat1 = pos1.get(0);
        Double lon1 = pos1.get(1);
        Double lat2 = pos2.get(0);
        Double lon2 = pos1.get(1);

        Double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        Double Bx = Math.cos(lat2) * Math.cos(dLon);
        Double By = Math.cos(lat2) * Math.sin(dLon);
        Double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2),
                Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        Double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);
        lat3 = Math.toDegrees(lat3);
        lon3 = Math.toDegrees(lon3);
        BigDecimal bdlat3 = new BigDecimal(lat3).setScale(5, RoundingMode.HALF_UP);
        lat3 = bdlat3.doubleValue();
        BigDecimal bdlon3 = new BigDecimal(lon3).setScale(5, RoundingMode.HALF_UP);
        lon3 = bdlon3.doubleValue();
        lista.add(lat3);
        lista.add(lon3);
        return lista;
    }
}
