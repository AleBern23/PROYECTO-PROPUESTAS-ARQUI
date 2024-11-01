package com.example.services;

import com.example.PersistenceManager;
import com.example.models.TipoProyectos;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/tiposproyectos")
@Produces(MediaType.APPLICATION_JSON)
public class TipoProyectosService {

    @PersistenceContext(unitName = "TipoProyectosPU")
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
    public Response crearTipoProyecto(TipoProyectos tipoProyecto) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(tipoProyecto);
            entityManager.getTransaction().commit();
            entityManager.refresh(tipoProyecto);
        } catch (Throwable t) {
            t.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            tipoProyecto = null;
        } finally {
            entityManager.clear();
            entityManager.close();
        }
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(tipoProyecto).build();
    }

    @PUT
    @Path("/modificar")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response modificarTipoProyecto(TipoProyectos tipoProyecto) {
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(tipoProyecto);
            entityManager.getTransaction().commit();
        } catch (Throwable t) {
            t.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            tipoProyecto = null;
        } finally {
            entityManager.clear();
            entityManager.close();
        }
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(tipoProyecto).build();
    }

    @DELETE
    @Path("/eliminar/{id}")
    public Response eliminarTipoProyecto(@PathParam("id") Long id) {
        TipoProyectos tipoProyecto = entityManager.find(TipoProyectos.class, id);
        if (tipoProyecto != null) {
            try {
                entityManager.getTransaction().begin();
                entityManager.remove(tipoProyecto);
                entityManager.getTransaction().commit();
            } catch (Throwable t) {
                t.printStackTrace();
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                tipoProyecto = null;
            } finally {
                entityManager.clear();
                entityManager.close();
            }
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(tipoProyecto).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("TipoProyecto not found").build();
        }
    }

    @GET
    @Path("/consultar/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response consultarTipoProyecto(@PathParam("id") Long id) {
        TipoProyectos tipoProyecto = entityManager.find(TipoProyectos.class, id);
        if (tipoProyecto != null) {
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(tipoProyecto).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("TipoProyecto not found").build();
        }
    }
}