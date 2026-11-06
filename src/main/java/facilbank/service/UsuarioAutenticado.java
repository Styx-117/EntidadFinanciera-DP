package facilbank.service;

public class UsuarioAutenticado {
    private final int idUsuario;
    private final String rol;
    private final String nombre;
    private final Integer idCliente;

    public UsuarioAutenticado(int idUsuario, String rol, String nombre, Integer idCliente) {
        this.idUsuario = idUsuario;
        this.rol = rol;
        this.nombre = nombre;
        this.idCliente = idCliente;
    }

    public int getIdUsuario() { return idUsuario; }
    public String getRol() { return rol; }
    public String getNombre() { return nombre; }
    public Integer getIdCliente() { return idCliente; }
}
