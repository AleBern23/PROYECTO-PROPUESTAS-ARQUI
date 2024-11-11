package com.example.services;

import com.example.PersistenceManager;
import com.example.models.Rol;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/roles")
@Produces(MediaType.APPLICATION_JSON)
public class RolService {

    @PersistenceContext(unitName = "RolesPU")
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
    @Path("/registrar")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registrarRol(Rol rol) {
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
    @Path("/actualizar")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response actualizarRol(Rol rol) {
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
    @Path("/eliminar/{id}")
    public Response eliminarRol(@PathParam("id") Long id) {
        Rol foundRol = entityManager.find(Rol.class, id);
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
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error eliminando el rol")
                        .build();
            } finally {
                entityManager.clear();
                entityManager.close();
            }
            return Response.status(200).header("Access-Control-Allow-Origin", "*")
                    .entity("Rol eliminado exitosamente").build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Rol no encontrado").build();
        }
    }

    @GET
    @Path("/consultar/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response consultarRol(@PathParam("id") Long id) {
        Rol rolConsulta = entityManager.find(Rol.class, id);
        if (rolConsulta != null) {
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rolConsulta).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Rol no encontrado").build();
        }
    }
}