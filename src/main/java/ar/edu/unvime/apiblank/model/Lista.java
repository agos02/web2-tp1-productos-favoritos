package ar.edu.unvime.apiblank.model;

/** Entidad de dominio: una lista que agrupa favoritos (por ejemplo "Regalos"). */
public class Lista {

    private Long id;
    private String nombre;

    public Lista(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

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
}