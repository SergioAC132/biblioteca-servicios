package com.api.biblioteca.models;

import jakarta.persistence.Column;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categoria_libro")
public class Libro {

    @Id
    @Column(name = "cve_lib", length = 20)
    private String codigo;

    @Column(name = "tit_lib", nullable = false, length = 150)
    private String titulo;

    @Column(name = "aut_lib", length = 100)
    private String autor;

    @Column(name = "edo_lib", nullable = false, length = 1)
    @Convert(converter = EstadoCatalogoConverter.class)
    private Boolean disponible;

    @JsonIgnore
    @OneToMany(mappedBy = "libro")
    private List<Prestamo> prestamos = new ArrayList<>();

    public Libro() {
    }

    public Libro(String codigo, String titulo, String autor, Boolean disponible) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.autor = autor;
        this.disponible = disponible;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public List<Prestamo> getPrestamos() { return prestamos; }

    public Boolean getDisponible() { return disponible; }
    public void setDisponible(Boolean disponible) { this.disponible = disponible; }
}
