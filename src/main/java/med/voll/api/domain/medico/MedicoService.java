package med.voll.api.domain.medico;

import med.voll.api.infra.errores.ValidacionDeIntegridad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MedicoService {

    @Autowired
    private MedicoRepository medicoRepository;

    @Transactional
    public DatosRespuestaMedico nuevoMedico(DatosRegistroMedico registroMedico) {
        var medico = new Medico(registroMedico);
        medicoRepository.save(medico);
        return new DatosRespuestaMedico(medico);
    }

    @Transactional(readOnly = true)
    public Page<DatosListadoMedico> listarMedicos(Pageable paginacion) {
        return medicoRepository.findByActivoTrue(paginacion)
                .map(DatosListadoMedico::new);

    }

    @Transactional
    public DatosRespuestaMedico actualizarMedico(DatosActualizarMedico actualizarMedico) {
        var medico = medicoRepository.findById(actualizarMedico.id())
                .orElseThrow(() -> new ValidacionDeIntegridad("No existe el medico con id " + actualizarMedico.id()));
        medico.actualizarDatos(actualizarMedico);
        return new DatosRespuestaMedico(medico);
    }

    @Transactional
    public void desactivarMedico(Long medicoId) {
        var medico = medicoRepository.findById(medicoId).orElseThrow(()-> new ValidacionDeIntegridad("No existe el medico con id " + medicoId));
        medico.desactivarMedico();
    }

    @Transactional(readOnly = true)
    public DatosRespuestaMedico obtenerMedico(Long medicoId) {
        var medico = medicoRepository.findById(medicoId)
                .orElseThrow(()->new ValidacionDeIntegridad("No existe el " +
                "medico con ese id "));
        return new DatosRespuestaMedico(medico);
    }

}
