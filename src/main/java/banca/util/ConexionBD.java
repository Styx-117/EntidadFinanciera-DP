package banca.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class ConexionBD {

    private static volatile ConexionBD instancia;

    private final String url;
    private final String usuario;
    private final String clave;
    private Connection conexion;

    private ConexionBD() {
        Properties p = new Properties();
        try (InputStream in = ConexionBD.class.getResourceAsStream("/db.properties")) {
            if (in == null) {
                throw new IllegalStateException("No se encontró db.properties en src/main/resources");
            }
            p.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer db.properties", e);
        }
        this.url = p.getProperty("db.url");
        this.usuario = p.getProperty("db.user");
        this.clave = p.getProperty("db.password");
    }

    public static ConexionBD getInstancia() {
        if (instancia == null) {
            synchronized (ConexionBD.class) {
                if (instancia == null) {
                    instancia = new ConexionBD();
                }
            }
        }
        return instancia;
    }
    public synchronized Connection getConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(url, usuario, clave);
        }
        return conexion;
    }
    public Connection nuevaConexion() throws SQLException {
        return DriverManager.getConnection(url, usuario, clave);
    }
}
