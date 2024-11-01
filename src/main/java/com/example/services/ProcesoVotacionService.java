package com.example.services;

import com.example.PersistenceManager;
import com.example.models.ProcesoVotacion;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/procesosvotacion")
@Produces(MediaType.APPLICATION_JSON)
public class ProcesoVotacionService {

    @PersistenceContext(unitName = "ProcesoVotacionPU")
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
    @Path("/crear")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response crearProcesoVotacion(ProcesoVotacion procesoVotacion) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(procesoVotacion);
            entityManager.getTransaction().commit();
            entityManager.refresh(procesoVotacion);
        } catch (Throwable t) {
            t.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            procesoVotacion = null;
        } finally {
            entityManager.clear();
            entityManager.close();
        }
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(procesoVotacion).build();
    }

    @PUT
    @Path("/modificar")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response modificarProcesoVotacion(ProcesoVotacion procesoVotacion) {
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(procesoVotacion);
            entityManager.getTransaction().commit();
        } catch (Throwable t) {
            t.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            procesoVotacion = null;
        } finally {
            entityManager.clear();
            entityManager.close();
        }
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(procesoVotacion).build();
    }

    @DELETE
    @Path("/eliminar/{id}")
    public Response eliminarProcesoVotacion(@PathParam("id") Long id) {
        ProcesoVotacion procesoVotacion = entityManager.find(ProcesoVotacion.class, id);
        if (procesoVotacion != null) {
            try {
                entityManager.getTransaction().begin();
                entityManager.remove(procesoVotacion);
                entityManager.getTransaction().commit();
            } catch (Throwable t) {
                t.printStackTrace();
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                procesoVotacion = null;
            } finally {
                entityManager.clear();
                entityManager.close();
            }
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(procesoVotacion).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("ProcesoVotacion not found").build();
        }
    }

    @GET
    @Path("/consultar/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response consultarProcesoVotacion(@PathParam("id") Long id) {
        ProcesoVotacion procesoVotacion = entityManager.find(ProcesoVotacion.class, id);
        if (procesoVotacion != null) {
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(procesoVotacion).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("ProcesoVotacion not found").build();
        }
    }
}