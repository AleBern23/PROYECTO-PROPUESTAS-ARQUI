package com.example.services;

import com.example.PersistenceManager;
import com.example.models.Rol;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/roles")
@Produces(MediaType.APPLICATION_JSON)
public class RolService {

    @PersistenceContext(unitName = "CiudadanosPU")
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
    public Response crearRol(Rol rol) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(rol);
            entityManager.getTransaction().commit();
            entityManager.refresh(rol);
        } catch (Throwable t) {
            t.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            rol = null;
        } finally {
            entityManager.clear();
            entityManager.close();
        }
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rol).build();
    }

    @PUT
    @Path("/modificar")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response modificarRol(Rol rol) {
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(rol);
            entityManager.getTransaction().commit();
        } catch (Throwable t) {
            t.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            rol = null;
        } finally {
            entityManager.clear();
            entityManager.close();
        }
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rol).build();
    }

    @DELETE
    @Path("/eliminar")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response eliminarRol(Rol rol) {
        Rol foundRol = entityManager.find(Rol.class, rol.getId());
        if (foundRol != null) {
            try {
                entityManager.getTransaction().begin();
                entityManager.remove(foundRol);
                entityManager.getTransaction().commit();
            } catch (Throwable t) {
                t.printStackTrace();
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                foundRol = null;
            } finally {
                entityManager.clear();
                entityManager.close();
            }
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(foundRol).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Rol not found").build();
        }
    }

    @GET
    @Path("/consultar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response consultarRol(Rol rol) {
        Rol rolConsulta = entityManager.find(Rol.class, rol.getId());
        if (rolConsulta != null) {
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rolConsulta).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Rol not found").build();
        }
    }

    @POST
    @Path("/reiniciarContador")
    @Produces(MediaType.APPLICATION_JSON)
    public Response reiniciarContador() {
        try {
            entityManager.getTransaction().begin();
            // Para H2
            entityManager.createNativeQuery("TRUNCATE TABLE roles").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE roles ALTER COLUMN id RESTART WITH 0").executeUpdate();
            // Para Derby
            // entityManager.createNativeQuery("ALTER TABLE roles ALTER COLUMN id RESTART WITH 1").executeUpdate();
            entityManager.getTransaction().commit();
        } catch (Throwable t) {
            t.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error reiniciando el contador").build();
        } finally {
            entityManager.clear();
            entityManager.close();
        }
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity("Contador reiniciado").build();
    }
}