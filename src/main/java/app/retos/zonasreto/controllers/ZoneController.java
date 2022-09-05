package app.retos.zonasreto.controllers;

import app.retos.zonasreto.models.Zones;
import app.retos.zonasreto.repository.ZoneRepository;
import app.retos.zonasreto.services.IZonesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/zonas")
public class ZoneController {

    @Autowired
    ZoneRepository zoneRepository;
    @Autowired
    IZonesService zonesService;

    // CREAR ZONA
    @PostMapping("/crear")
    @ResponseStatus(code = HttpStatus.CREATED)
    public String crearZonas(@RequestBody @Validated Zones zones) {
        if (zonesService.crearZonas(zones))
            return "Zona creada satisfactoriamente";
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en la creación de la zona");
    }

    // MICROSERVICIO SENSORES -> CREAR
    @PostMapping("/sensores/crear/")
    public Integer crearZonesPosts(@RequestParam("idEvents") Integer idPosts,
                                   @RequestParam("location") List<Double> location) throws IOException {
        try {
            return zonesService.crearZonasPostes(idPosts, location);
        } catch (Exception e2) {
            throw new IOException("Error crear proyectos, muro: " + e2.getMessage());
        }
    }

    // MICROSERVICIO EVENTS -> CREAR
    @GetMapping("/events/crear/")
    public Integer obtainZonesEvents(@RequestParam("idEvents") String idEvents,
                                     @RequestParam("location") List<Double> location) throws IOException {
        try {
            return zonesService.crearZonasEvents(idEvents, location);
        } catch (Exception e2) {
            throw new IOException("Error crear proyectos, muro: " + e2.getMessage());
        }
    }

    @GetMapping("/events/actualizar/")
    public Integer obtainZonesEventsManyTimes(@RequestParam("idEvents") String idEvents,
                                              @RequestParam("location") List<Double> location,
                                              @RequestParam("zoneCode") Integer zoneCode) throws IOException {
        try {
            return zonesService.modificarZonasEvents(idEvents, location, zoneCode);
        } catch (Exception e2) {
            throw new IOException("Error crear proyectos, muro: " + e2.getMessage());
        }
    }

    // LISTAR ZONAS
    @GetMapping("/listar")
    @ResponseStatus(code = HttpStatus.OK)
    public List<Zones> listarZonas() {
        return zonesService.encontrarZonas();
    }

    // VER CANTIDAD REPORTES
    @GetMapping("/numero/eventos/{codigo}")
    @ResponseStatus(code = HttpStatus.OK)
    public Integer numeroDelitos(@PathVariable("codigo") Integer codigo) {
        if (zoneRepository.existsByZoneCode(codigo))
            return zoneRepository.findByZoneCode(codigo).getIdEvents().size();
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La zona no existe");
    }

    // OBTENER ZONA
    @GetMapping("/buscar/{codigo}")
    @ResponseStatus(code = HttpStatus.FOUND)
    public Zones getMuroCodigo(@PathVariable("codigo") Integer codigo) throws IOException {
        if (zoneRepository.existsByZoneCode(codigo))
            return zonesService.encontrarZonas(codigo);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La zona no existe");
    }

    @DeleteMapping("/eliminar/{codigo}")
    @ResponseStatus(code = HttpStatus.OK)
    public boolean eliminarZona(@PathVariable("codigo") Integer codigo) {
        if (zoneRepository.existsByZoneCode(codigo)) {
            zoneRepository.deleteByZoneCode(codigo);
            return true;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La zona no existe");
    }

    @DeleteMapping("/eliminar/all")
    @ResponseStatus(code = HttpStatus.OK)
    public void eliminarAll() {
        zoneRepository.deleteAll();
    }

    @PutMapping("/arreglar")
    @ResponseStatus(HttpStatus.OK)
    public void arreglar() {
        List<Zones> z = zoneRepository.findAll();
        z.forEach(x -> {
            Zones z2 = zoneRepository.findByZoneCode(x.getZoneCode());
            List<Double> newLocation = new ArrayList<>(Arrays.asList(
                    BigDecimal.valueOf(x.getLocation().get(0)).setScale(5, RoundingMode.HALF_UP).doubleValue(),
                    BigDecimal.valueOf(x.getLocation().get(1)).setScale(5, RoundingMode.HALF_UP).doubleValue()));
            z2.setLocation(newLocation);
            zoneRepository.save(z2);
        });
    }
}
