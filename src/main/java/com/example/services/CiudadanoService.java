package com.example.services;

import com.example.PersistenceManager;
import com.example.models.Ciudadano;
import com.example.models.Rol;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codehaus.jettison.json.JSONObject;

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
    @Path("/consultar/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response consultarCiudadano(@PathParam("id") Long id) {
        Ciudadano ciudadanoConsulta = entityManager.find(Ciudadano.class, id);
        if (ciudadanoConsulta != null) {
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(ciudadanoConsulta).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Ciudadano no encontrado").build();
        }
    }

    @PUT
    @Path("/asignarRol")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response asignarRol(@QueryParam("ciudadanoId") Long ciudadanoId, @QueryParam("rolId") Long rolId) {
        Ciudadano foundCiudadano = entityManager.find(Ciudadano.class, ciudadanoId);
        Rol foundRol = entityManager.find(Rol.class, rolId);

        if (foundCiudadano != null && foundRol != null) {
            try {
                entityManager.getTransaction().begin();
                if (!foundCiudadano.getRoles().contains(foundRol)) {
                    foundCiudadano.getRoles().add(foundRol);
                    entityManager.merge(foundCiudadano);
                    entityManager.getTransaction().commit();
                } else {
                    entityManager.getTransaction().rollback();
                    return Response.status(Response.Status.CONFLICT).entity("El rol ya está asignado al ciudadano")
                            .build();
                }
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

    @DELETE
    @Path("/eliminar/{id}")
    public Response eliminarCiudadano(@PathParam("id") Long id) {
        Ciudadano foundCiudadano = entityManager.find(Ciudadano.class, id);
        if (foundCiudadano != null) {
            try {
                entityManager.getTransaction().begin();
                entityManager.remove(foundCiudadano);
                entityManager.getTransaction().commit();
            } catch (Throwable t) {
                t.printStackTrace();
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error eliminando el ciudadano")
                        .build();
            } finally {
                entityManager.clear();
                entityManager.close();
            }
            return Response.status(200).header("Access-Control-Allow-Origin", "*")
                    .entity("Ciudadano eliminado exitosamente").build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Ciudadano no encontrado").build();
        }
    }

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(JSONObject loginData) {
        String correo = loginData.optString("correo");
        String password = loginData.optString("password");

        Query q = entityManager
                .createQuery("select c from Ciudadano c where c.correo = :correo and c.password = :password");
        q.setParameter("correo", correo);
        q.setParameter("password", password);
        List<Ciudadano> ciudadanos = q.getResultList();
        if (ciudadanos.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Correo o contraseña inválidos")
                    .build();
        }

        Ciudadano ciudadano = ciudadanos.get(0);
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(ciudadano).build();
    }

}