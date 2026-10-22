package com.api.biblioteca.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "estudiante")
public class Estudiante {

    @Id
    @Column(length = 9)
    private String matricula;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String correo;

    @Column(nullable = false)
    private boolean activo;

    @JsonIgnore
    @OneToMany(mappedBy = "estudiante")
    private List<Prestamo> prestamos = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "estudiante")
    private List<Notificacion> notificaciones = new ArrayList<>();

    public Estudiante() {
    }

    public Estudiante(String matricula, String nombre, String correo, boolean activo) {
        this.matricula = matricula;
        this.nombre = nombre;
        this.correo = correo;
        this.activo = activo;
    }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public List<Prestamo> getPrestamos() { return prestamos; }

    public List<Notificacion> getNotificaciones() { return notificaciones; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
