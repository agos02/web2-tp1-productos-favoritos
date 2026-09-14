package ar.edu.unvime.apiblank.model;

import java.time.LocalDateTime;

/** Entidad de dominio: un producto externo marcado como favorito por el usuario. */

public class Favorito {

    private Long id;
    private Integer productoId;
    private String nota;
    private LocalDateTime fechaAgregado;

    public Favorito(Long id, Integer productoId, String nota, LocalDateTime fechaAgregado) {
        this.id = id;
        this.productoId = productoId;
        this.nota = nota;
        this.fechaAgregado = fechaAgregado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public LocalDateTime getFechaAgregado() {
        return fechaAgregado;
    }

    public void setFechaAgregado(LocalDateTime fechaAgregado) {
        this.fechaAgregado = fechaAgregado;
    }
}