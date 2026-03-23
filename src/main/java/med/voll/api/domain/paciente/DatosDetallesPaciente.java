package med.voll.api.domain.paciente;


import med.voll.api.domain.direccion.DatosDireccion;

public record DatosDetallesPaciente(Long id, String nombre, String email,
                                    String documento, String telefono, DatosDireccion direccion) {

    public DatosDetallesPaciente(Paciente paciente) {
        this(paciente.getId(), paciente.getNombre(), paciente.getEmail(),
                paciente.getDocumento(), paciente.getTelefono(), new DatosDireccion(paciente.getDireccion()));
    }
}
