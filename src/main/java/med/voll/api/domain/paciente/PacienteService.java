package med.voll.api.domain.paciente;

import med.voll.api.infra.errores.ValidacionDeIntegridad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Transactional
    public DatosDetallesPaciente guardarPaciente(DatosRegistroPaciente paciente) {

        var newPaciente = new Paciente(paciente);
        pacienteRepository.save(newPaciente);
        return new DatosDetallesPaciente(newPaciente);
    }

    @Transactional(readOnly = true)
    public Page<DatosListaPaciente> listarPacientes(Pageable paginacion) {
        var pagina = pacienteRepository.findAllByActivoTrue(paginacion)
                .map(DatosListaPaciente::new);
        return pagina;
    }

    @Transactional
    public DatosDetallesPaciente actualizarPaciente(DatosActualizacionPaciente paciente) {
        var newPaciente = pacienteRepository.findById(paciente.id())
                .orElseThrow(()-> new ValidacionDeIntegridad("No existe el paciente con el id: " + paciente.id()));
        newPaciente.actualizarInformacion(paciente);

        return new DatosDetallesPaciente(newPaciente);
    }

    @Transactional
    public void eliminarPaciente(Long id) throws  ValidacionDeIntegridad {
        var paciente = pacienteRepository.findById(id)
                .orElseThrow(()-> new ValidacionDeIntegridad("Paciente no encontrado!"));
        paciente.eliminar();
    }

    @Transactional(readOnly = true)
    public DatosDetallesPaciente detallarPaciente(Long id) {
        var paciente = pacienteRepository.findById(id)
                .orElseThrow(()-> new ValidacionDeIntegridad("Paciente no encontrado!"));
        return new DatosDetallesPaciente(paciente);
    }


}
