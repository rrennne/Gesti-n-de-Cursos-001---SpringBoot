package com.renecode.gestioncursos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "cursos")
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Esto es para para sea auto incrementable.
    private Integer id;

    @Column(length = 128, nullable = false) // tamaño, que no sea vacio.
    private String titulo;

    @Column(length = 256, nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private int nivel;

    @Column(name = "estado_publicacion") // Cuando se crea esta columna se llamara así.
    private boolean isPublicado; // Es una buena practica/practica llamar asi a las variables booleanas.


}
