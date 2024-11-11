package com.example.services;

import com.example.PersistenceManager;
import com.example.models.Proyectos;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/estadisticas")
@Produces(MediaType.APPLICATION_JSON)
public class EstadisticasService {

    @PersistenceContext(unitName = "ProyectosPU")
    private EntityManager entityManager;

    @PostConstruct
    public void init() {
        try {
            entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GET
    @Path("/ganador/{localidad}")
    public Response getProyectoGanadorPorLocalidad(@PathParam("localidad") String localidad) {
        try {
            Query query = entityManager.createQuery(
                    "SELECT p FROM Proyectos p WHERE p.localidad = :localidad ORDER BY p.votos DESC");
            query.setParameter("localidad", localidad);
            query.setMaxResults(1);
            Proyectos proyectoGanador = (Proyectos) query.getSingleResult();
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(proyectoGanador).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al obtener el proyecto ganador")
                    .build();
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @GET
    @Path("/totalVotos/{localidad}")
    public Response getTotalVotosPorLocalidad(@PathParam("localidad") String localidad) {
        try {
            Query query = entityManager.createQuery(
                    "SELECT SUM(p.votos) FROM Proyectos p WHERE p.localidad = :localidad");
            query.setParameter("localidad", localidad);
            Long totalVotos = (Long) query.getSingleResult();
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(totalVotos).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al obtener el total de votos")
                    .build();
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @GET
    @Path("/votosPorProyecto/{localidad}")
    public Response getVotosPorProyectoDeLocalidad(@PathParam("localidad") String localidad) {
        try {
            Query query = entityManager.createQuery(
                    "SELECT p.nombre, p.votos FROM Proyectos p WHERE p.localidad = :localidad");
            query.setParameter("localidad", localidad);
            List<Object[]> resultados = query.getResultList();

            List<Map<String, Object>> votosPorProyecto = new ArrayList<>();
            for (Object[] resultado : resultados) {
                Map<String, Object> proyecto = new HashMap<>();
                proyecto.put("nombre", resultado[0]);
                proyecto.put("votos", resultado[1]);
                votosPorProyecto.add(proyecto);
            }

            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(votosPorProyecto).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al obtener los votos por proyecto").build();
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @GET
    @Path("/votosPorRangoEdad")
    public Response getVotosPorRangoEdad() {
        try {
            Query query = entityManager.createQuery(
                    "SELECT " +
                            "CASE " +
                            "WHEN c.fechaNacimiento BETWEEN :edad18 AND :edad25 THEN '18-25' " +
                            "WHEN c.fechaNacimiento BETWEEN :edad26 AND :edad35 THEN '26-35' " +
                            "WHEN c.fechaNacimiento BETWEEN :edad36 AND :edad45 THEN '36-45' " +
                            "WHEN c.fechaNacimiento BETWEEN :edad46 AND :edad55 THEN '46-55' " +
                            "ELSE '55+' END AS rangoEdad, " +
                            "COUNT(v.id) " +
                            "FROM Votos v " +
                            "JOIN v.ciudadano c " +
                            "GROUP BY rangoEdad");

            query.setParameter("edad18", new Date(System.currentTimeMillis() - 25L * 365 * 24 * 60 * 60 * 1000));
            query.setParameter("edad25", new Date(System.currentTimeMillis() - 18L * 365 * 24 * 60 * 60 * 1000));
            query.setParameter("edad26", new Date(System.currentTimeMillis() - 35L * 365 * 24 * 60 * 60 * 1000));
            query.setParameter("edad35", new Date(System.currentTimeMillis() - 26L * 365 * 24 * 60 * 60 * 1000));
            query.setParameter("edad36", new Date(System.currentTimeMillis() - 45L * 365 * 24 * 60 * 60 * 1000));
            query.setParameter("edad45", new Date(System.currentTimeMillis() - 36L * 365 * 24 * 60 * 60 * 1000));
            query.setParameter("edad46", new Date(System.currentTimeMillis() - 55L * 365 * 24 * 60 * 60 * 1000));
            query.setParameter("edad55", new Date(System.currentTimeMillis() - 46L * 365 * 24 * 60 * 60 * 1000));

            List<Object[]> resultados = query.getResultList();

            Map<String, Integer> votosPorRangoEdad = new HashMap<>();
            votosPorRangoEdad.put("18-25", 0);
            votosPorRangoEdad.put("26-35", 0);
            votosPorRangoEdad.put("36-45", 0);
            votosPorRangoEdad.put("46-55", 0);
            votosPorRangoEdad.put("55+", 0);

            for (Object[] resultado : resultados) {
                String rangoEdad = (String) resultado[0];
                Long votos = (Long) resultado[1];
                votosPorRangoEdad.put(rangoEdad, votos.intValue());
            }

            List<Map<String, Object>> responseList = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : votosPorRangoEdad.entrySet()) {
                Map<String, Object> rangoEdadMap = new HashMap<>();
                rangoEdadMap.put("rangoEdad", entry.getKey());
                rangoEdadMap.put("votos", entry.getValue());
                responseList.add(rangoEdadMap);
            }

            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(responseList).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al obtener los votos por rango de edad").build();
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}