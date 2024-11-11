package com.example.services;

import com.example.PersistenceManager;
import com.example.models.Ciudadano;
import com.example.models.Proyectos;
import com.example.models.ProcesoVotacion;
import com.example.models.Votos;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/votos")
@Produces(MediaType.APPLICATION_JSON)
public class VotosServices {

    @PersistenceContext(unitName = "ProyectosPU")
    EntityManager entityManager;

    @PostConstruct
    public void init() {
        try {
            entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @POST
    @Path("/agregar")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response agregarVoto(VotoRequest votoRequest) {
        try {
            entityManager.getTransaction().begin();

            Ciudadano ciudadano = entityManager.find(Ciudadano.class, votoRequest.getCiudadanoId());
            Proyectos proyecto = entityManager.find(Proyectos.class, votoRequest.getProyectoId());
            ProcesoVotacion procesoVotacion = entityManager.find(ProcesoVotacion.class, votoRequest.getProcesoVotacionId());

            if (ciudadano == null || proyecto == null || procesoVotacion == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Invalid IDs provided").build();
            }

            Votos voto = new Votos();
            voto.setCiudadano(ciudadano);
            voto.setProyecto(proyecto);
            voto.setProcesoVotacion(procesoVotacion);

            entityManager.persist(voto);

            // Incrementar el número de votos del proyecto
            proyecto.incrementarVotos();
            entityManager.merge(proyecto);

            entityManager.getTransaction().commit();

            return Response.status(Response.Status.OK).header("Access-Control-Allow-Origin", "*").entity(voto).build();
        } catch (Throwable t) {
            t.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error processing vote").build();
        } finally {
            entityManager.clear();
            entityManager.close();
        }
    }

    public static class VotoRequest {
        private Long ciudadanoId;
        private Long proyectoId;
        private Long procesoVotacionId;

        // Getters and Setters

        public Long getCiudadanoId() {
            return ciudadanoId;
        }

        public void setCiudadanoId(Long ciudadanoId) {
            this.ciudadanoId = ciudadanoId;
        }

        public Long getProyectoId() {
            return proyectoId;
        }

        public void setProyectoId(Long proyectoId) {
            this.proyectoId = proyectoId;
        }

        public Long getProcesoVotacionId() {
            return procesoVotacionId;
        }

        public void setProcesoVotacionId(Long procesoVotacionId) {
            this.procesoVotacionId = procesoVotacionId;
        }
    }
}