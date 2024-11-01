package com.example.services;

import com.example.PersistenceManager;
import com.example.models.Proyectos;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/proyectos")
@Produces(MediaType.APPLICATION_JSON)
public class ProyectosService {

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
    @Path("/crear")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response crearProyecto(Proyectos proyecto) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(proyecto);
            entityManager.getTransaction().commit();
            entityManager.refresh(proyecto);
        } catch (Throwable t) {
            t.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            proyecto = null;
        } finally {
            entityManager.clear();
            entityManager.close();
        }
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(proyecto).build();
    }

    @PUT
    @Path("/modificar")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response modificarProyecto(Proyectos proyecto) {
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(proyecto);
            entityManager.getTransaction().commit();
        } catch (Throwable t) {
            t.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            proyecto = null;
        } finally {
            entityManager.clear();
            entityManager.close();
        }
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(proyecto).build();
    }

    @DELETE
    @Path("/eliminar/{id}")
    public Response eliminarProyecto(@PathParam("id") Long id) {
        Proyectos proyecto = entityManager.find(Proyectos.class, id);
        if (proyecto != null) {
            try {
                entityManager.getTransaction().begin();
                entityManager.remove(proyecto);
                entityManager.getTransaction().commit();
            } catch (Throwable t) {
                t.printStackTrace();
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                proyecto = null;
            } finally {
                entityManager.clear();
                entityManager.close();
            }
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(proyecto).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Proyecto not found").build();
        }
    }

    @GET
    @Path("/consultar/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response consultarProyecto(@PathParam("id") Long id) {
        Proyectos proyecto = entityManager.find(Proyectos.class, id);
        if (proyecto != null) {
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(proyecto).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Proyecto not found").build();
        }
    }
}