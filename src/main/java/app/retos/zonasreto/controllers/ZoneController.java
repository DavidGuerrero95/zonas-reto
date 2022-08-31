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
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/zona")
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

    // MICROSERVICIO EVENTS -> CREAR
    @PostMapping("/events/crear/")
    public Integer crearZonesEvents(@RequestParam("idEvents") String idEvents,
                                    @RequestParam("location") List<Double> location) throws IOException {
        try {
            return zonesService.crearZonasEvents(idEvents, location);
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

    // OBTENER ZONA
    @GetMapping("/buscar/{codigo}")
    @ResponseStatus(code = HttpStatus.FOUND)
    public Zones getMuroCodigo(@PathVariable("codigo") Integer codigo) throws IOException {
        if (zoneRepository.existsByZoneCode(codigo))
            return zonesService.encontrarZonas(codigo);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La zona no existe");
    }
}
