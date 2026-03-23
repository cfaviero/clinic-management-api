package med.voll.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import med.voll.api.domain.paciente.*;
import med.voll.api.infra.errores.ValidacionDeIntegridad;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/pacientes")
@SecurityRequirement(name = "bearer-key")
//@SuppressWarnings("all")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @PostMapping
    @Operation(summary = "Registra un nuevo paciente")
    public ResponseEntity registrar(@RequestBody @Valid DatosRegistroPaciente datos, UriComponentsBuilder uriBuilder) throws ValidacionDeIntegridad {
        var paciente = pacienteService.guardarPaciente(datos);
        var uri = uriBuilder.path("/pacientes/{id}").buildAndExpand(paciente.id()).toUri();

        return ResponseEntity.created(uri).body(paciente);
    }

    @GetMapping
    @Operation(summary = "Obtiene el listado para los pacientes")
    public ResponseEntity<Page<DatosListaPaciente>> listar(@PageableDefault(size = 10, sort = {"nombre"}) Pageable paginacion) {
        Page<DatosListaPaciente> page = pacienteService.listarPacientes(paginacion);
        return ResponseEntity.ok(page);
    }

    @PutMapping
    @Operation(summary = "Actualiza las informaciones para el paciente")
    public ResponseEntity actualizar(@RequestBody @Valid DatosActualizacionPaciente datos) throws ValidacionDeIntegridad {
        var paciente = pacienteService.actualizarPaciente(datos);
        return ResponseEntity.ok(paciente);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un paciente a partir del ID")
    public ResponseEntity eliminar(@PathVariable Long id) {
        pacienteService.eliminarPaciente(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "obtiene los detalles para el paciente con el ID indicado")
    public ResponseEntity detallar(@PathVariable Long id) {
        var paciente = pacienteService.detallarPaciente(id);
        return ResponseEntity.ok(paciente);
    }


}

