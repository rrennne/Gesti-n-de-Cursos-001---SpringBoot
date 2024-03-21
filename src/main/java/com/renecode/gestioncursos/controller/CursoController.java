package com.renecode.gestioncursos.controller;

import com.renecode.gestioncursos.entity.Curso;
import com.renecode.gestioncursos.repository.ICursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CursoController {

    @Autowired
    private ICursoRepository iCursoRepository;

    @GetMapping
    public String home() {

        return "redirect:/cursos"; //! Cuidado con el redirect: ya que solo lo podemos usar para redirigir a un endpoint que ya hemos visitado.
    }

    @GetMapping("/cursos") // NUESTRO ENDPOINT principal.
    public String listarCursos(Model model) { // La clase Model, es una clase que permite agregar atributos para que los envie directamente a la vista.
        //! Si creamos el atributo 'cursosList' pero no lo estamos recibiendo/utilizando en nuestro html, generara un error.
        List<Curso> cursos = iCursoRepository.findAll();
        cursos = iCursoRepository.findAll();

        model.addAttribute("cursosList", cursos);
        return "cursos"; // Solo la primera vez que llamamos a cursos es que usamos return acecas.
    }

    @GetMapping("/cursos/nuevo")
    public String agregarCurso(Model model) {

        Curso curso = new Curso();
        curso.setPublicado(true);

        model.addAttribute("curso", curso);
        model.addAttribute("pageTitle", "Nuevo curso");

        return "form_agregar_curso";
    }

    @PostMapping("/cursos/save")
    public String guardarCurso(Curso curso, RedirectAttributes redirectAttributes) {

        try {
            iCursoRepository.save(curso);
            redirectAttributes.addFlashAttribute("message", "Guardado con éxito");
        } catch (Exception e) {
            redirectAttributes.addAttribute("message", e.getMessage());
        }

        return "redirect:/cursos"; // Como esta es la tercera vez que llamamos a cursos entonces usamos un redirect:/
    }

    /**Se usa un @GetMapping, ya que tiene que redireccionar a un  archivo y en ese archivo van a cargar los datos que queramos actualizar.*/
    @GetMapping("/cursos/{id}") // Al editar metemos el id.
    public String editarCurso(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {

        try {
            Curso curso = iCursoRepository.findById(id).get();

            model.addAttribute("curso", curso);
            model.addAttribute("pageTitle", "Editar curso :" + id);
            //redirectAttributes.addFlashAttribute("message", "Actualizado con éxito");
            return "form_agregar_curso";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }

        return "redirect:/cursos";
    }

    @GetMapping("/cursos/eliminar/{id}") // Al eliminar metemos el id.
    public String eliminarCurso(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {

        try {
            iCursoRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Eliminado con éxito");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }

        return "redirect:/cursos";
    }
}
