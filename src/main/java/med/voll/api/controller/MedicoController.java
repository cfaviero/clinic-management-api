package med.voll.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import med.voll.api.domain.direccion.DatosDireccion;
import med.voll.api.domain.medico.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/medicos")
@SecurityRequirement(name = "bearer-key")
public class MedicoController {

    @Autowired
    private MedicoService medicoService;

    @PostMapping
    @Operation(summary = "Registra un nuevo medico en la base de datos")
    public ResponseEntity<DatosRespuestaMedico> registrarMedico(@RequestBody @Valid DatosRegistroMedico datosRegistroMedico,
                                                                UriComponentsBuilder uriComponentsBuilder) {
        var nuevo = medicoService.nuevoMedico(datosRegistroMedico);

        URI url = uriComponentsBuilder.path("/medicos/{id}").buildAndExpand(nuevo.id()).toUri();
        return ResponseEntity.created(url).body(nuevo);

    }

    @GetMapping
    @Operation(summary = "Obtiene el listado de medicos")
    public ResponseEntity<Page<DatosListadoMedico>> listadoMedicos(@PageableDefault(size = 7) Pageable paginacion) {
        var medicos = medicoService.listarMedicos(paginacion);
        return ResponseEntity.ok(medicos);
    }

    @PutMapping
    @Operation(summary = "Actualiza los datos de un medico existente")
    public ResponseEntity actualizarMedico(@RequestBody @Valid DatosActualizarMedico datosActualizarMedico) {
        var medico = medicoService.actualizarMedico(datosActualizarMedico);
        return ResponseEntity.ok(medico);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un medico registrado - inactivo")
    public ResponseEntity eliminarMedico(@PathVariable Long id) {
        medicoService.desactivarMedico(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene los registros del medico con ID")
    public ResponseEntity<DatosRespuestaMedico> retornaDatosMedico(@PathVariable Long id) {
        var medicoBuscado = medicoService.obtenerMedico(id);
        return ResponseEntity.ok(medicoBuscado);
    }

}
