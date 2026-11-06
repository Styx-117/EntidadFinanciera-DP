package facilbank.patterns.state;

public class EstadoAprobado implements EstadoCredito {
    @Override
    public String nombre() { return "APROBADO"; }

    @Override
    public EstadoCredito evaluar() {
        throw new IllegalStateException("La solicitud ya fue aprobada");
    }

    @Override
    public EstadoCredito aprobar() {
        throw new IllegalStateException("La solicitud ya fue aprobada");
    }

    @Override
    public EstadoCredito rechazar() {
        throw new IllegalStateException("Una solicitud aprobada no puede rechazarse");
    }
}
