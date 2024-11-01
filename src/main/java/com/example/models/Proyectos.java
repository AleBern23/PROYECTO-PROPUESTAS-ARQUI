package com.example.models;

import javax.persistence.*;
import java.util.List;

@Entity
public class Proyectos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String identificador;
    private String alcance;
    private String periodoDeTiempoPrevisto;
    private String tipoDeProyecto;
    private String localidad;
    private Double presupuesto;
    private Integer numeroDeCiudadanosBeneficiados;
    private String impacto;

    @ElementCollection
    @CollectionTable(name = "aspectos_asociados", joinColumns = @JoinColumn(name = "proyecto_id"))
    @Column(name = "aspecto")
    private List<String> aspectosAsociados;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getAlcance() {
        return alcance;
    }

    public void setAlcance(String alcance) {
        this.alcance = alcance;
    }

    public String getPeriodoDeTiempoPrevisto() {
        return periodoDeTiempoPrevisto;
    }

    public void setPeriodoDeTiempoPrevisto(String periodoDeTiempoPrevisto) {
        this.periodoDeTiempoPrevisto = periodoDeTiempoPrevisto;
    }

    public String getTipoDeProyecto() {
        return tipoDeProyecto;
    }

    public void setTipoDeProyecto(String tipoDeProyecto) {
        this.tipoDeProyecto = tipoDeProyecto;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public Double getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(Double presupuesto) {
        this.presupuesto = presupuesto;
    }

    public Integer getNumeroDeCiudadanosBeneficiados() {
        return numeroDeCiudadanosBeneficiados;
    }

    public void setNumeroDeCiudadanosBeneficiados(Integer numeroDeCiudadanosBeneficiados) {
        this.numeroDeCiudadanosBeneficiados = numeroDeCiudadanosBeneficiados;
    }

    public String getImpacto() {
        return impacto;
    }

    public void setImpacto(String impacto) {
        this.impacto = impacto;
    }

    public List<String> getAspectosAsociados() {
        return aspectosAsociados;
    }

    public void setAspectosAsociados(List<String> aspectosAsociados) {
        this.aspectosAsociados = aspectosAsociados;
    }
}