package com.politecnicomalaga.controlador;
import com.politecnicomalaga.algoritmos.Encriptador;
import com.politecnicomalaga.algoritmos.EncriptadorFactory;
import com.politecnicomalaga.dao.CredencialesDAO;
import com.politecnicomalaga.modelo.Credencial;
import com.politecnicomalaga.modelo.Credenciales;
import java.sql.SQLException;
import java.util.ArrayList;

public class ControladorCredenciales {

    private CredencialesDAO credencialesDAO;
    private Credenciales credenciales;
    private Encriptador encriptador;

    public final static String ALGORITMO_SHA512 = "SHA-512";
    public final static String ALGORITMO_BCRYPT = "BCRYPT";

    public ControladorCredenciales() {
        credencialesDAO = new CredencialesDAO();
    }

    public void actualizarListaCredenciales() {
        try {
            credenciales = new Credenciales(credencialesDAO.getItems());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminarCredencial(ArrayList<String> nombresCredenciales) {
        for (String nombreCredencial: nombresCredenciales) {
            Credencial credencial = getCredencialPorNombre(nombreCredencial);
            try {
                credencialesDAO.borrar(credencial.getId());
                credenciales.borrarCredencial(credencial);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void crearCredencial(String nombre, String password, String algoritmo) {
        try {
            Credencial credencial = encriptarContraseña(password, algoritmo);
            credencial.setNombre(nombre);
            credencialesDAO.crear(credencial);
            credenciales.addCredencial(credencial);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Credencial encriptarContraseña(String password, String algoritmo) {
        encriptador = EncriptadorFactory.obtenerEncriptador(algoritmo);
        return encriptador.crearContraseñaEncriptada(password);
    }

    public boolean comprobarCredenciales(String nombre, String password) {
        Credencial credencial = getCredencialPorNombre(nombre);
        encriptador = EncriptadorFactory.obtenerEncriptador(credencial.getAlgoritmo());
        return encriptador.comprobarContraseñaEncriptada(password, credencial);
    }

    public Credenciales getCredenciales() {
        return credenciales;
    }

    public Credencial getCredencialPorNombre(String nombre) {
        return credenciales.getCredencialPorNombre(nombre);
    }

    public boolean existeUsuario(String nombre) {
        return getCredencialPorNombre(nombre) != null;
    }
}
