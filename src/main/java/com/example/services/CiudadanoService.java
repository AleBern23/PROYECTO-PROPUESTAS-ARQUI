package com.example.services;

import com.example.PersistenceManager;
import com.example.models.Ciudadano;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/ciudadanos")
@Produces(MediaType.APPLICATION_JSON)
public class CiudadanoService {

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
    @Path("/registrar")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registrarCiudadano(Ciudadano ciudadano) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(ciudadano);
            entityManager.getTransaction().commit();
            entityManager.refresh(ciudadano);
        } catch (Throwable t) {
            t.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            ciudadano = null;
        } finally {
            entityManager.clear();
            entityManager.close();
        }
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(ciudadano).build();
    }

    @PUT
    @Path("/actualizar")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response actualizarCiudadano(Ciudadano ciudadano) {
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(ciudadano);
            entityManager.getTransaction().commit();
        } catch (Throwable t) {
            t.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            ciudadano = null;
        } finally {
            entityManager.clear();
            entityManager.close();
        }
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(ciudadano).build();
    }

    @PUT
    @Path("/deshabilitar")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deshabilitarCiudadano(Ciudadano ciudadano) {
        Ciudadano foundCiudadano = entityManager.find(Ciudadano.class, ciudadano.getId());
        if (foundCiudadano != null) {
            try {
                entityManager.getTransaction().begin();
                foundCiudadano.setHabilitado(false);
                entityManager.merge(foundCiudadano);
                entityManager.getTransaction().commit();
            } catch (Throwable t) {
                t.printStackTrace();
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                foundCiudadano = null;
            } finally {
                entityManager.clear();
                entityManager.close();
            }
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(foundCiudadano).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Ciudadano not found").build();
        }
    }

    @GET
    @Path("/consultar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response consultarCiudadano(Ciudadano ciudadano) {
        Ciudadano ciudadanoConsulta = entityManager.find(Ciudadano.class, ciudadano.getId());
        if (ciudadanoConsulta != null) {
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(ciudadanoConsulta).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Ciudadano not found").build();
        }
    }

    @PUT
    @Path("/asignarRol")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response asignarRol(Ciudadano ciudadano, Rol rol) {
        Ciudadano foundCiudadano = entityManager.find(Ciudadano.class, ciudadano.getId());
        Rol foundRol = entityManager.find(Rol.class, rol.getId());
        if (foundCiudadano != null && foundRol != null) {
            try {
                entityManager.getTransaction().begin();
                foundCiudadano.getRoles().add(foundRol);
                entityManager.merge(foundCiudadano);
                entityManager.getTransaction().commit();
            } catch (Throwable t) {
                t.printStackTrace();
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                foundCiudadano = null;
            } finally {
                entityManager.clear();
                entityManager.close();
            }
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(foundCiudadano).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Ciudadano or Rol not found").build();
        }
    }
}