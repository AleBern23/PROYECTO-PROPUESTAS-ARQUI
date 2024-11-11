package com.example.models;

import javax.persistence.*;

@Entity
public class Votos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ciudadano_id")
    private Ciudadano ciudadano;

    @ManyToOne
    @JoinColumn(name = "proyecto_id")
    private Proyectos proyecto;

    @ManyToOne
    @JoinColumn(name = "proceso_votacion_id")
    private ProcesoVotacion procesoVotacion;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ciudadano getCiudadano() {
        return ciudadano;
    }

    public void setCiudadano(Ciudadano ciudadano) {
        this.ciudadano = ciudadano;
    }

    public Proyectos getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyectos proyecto) {
        this.proyecto = proyecto;
    }

    public ProcesoVotacion getProcesoVotacion() {
        return procesoVotacion;
    }

    public void setProcesoVotacion(ProcesoVotacion procesoVotacion) {
        this.procesoVotacion = procesoVotacion;
    }
}