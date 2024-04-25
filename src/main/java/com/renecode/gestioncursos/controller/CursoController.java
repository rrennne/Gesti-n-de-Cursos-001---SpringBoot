package com.renecode.gestioncursos.controller;

import com.renecode.gestioncursos.entity.Curso;
import com.renecode.gestioncursos.reports.CursoExporterExcel;
import com.renecode.gestioncursos.reports.CursoExporterPDF;
import com.renecode.gestioncursos.repository.ICursoRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.collection.spi.PersistentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page; // Esta clase representa una página de datos en el contexto de paginación. Facilita el manejo y visualización.

import org.springframework.data.domain.PageRequest; // Esta clase se utiliza para crear una solicitud de página especifica.
                                                    // Puedo especificar el número de página, el tamaño de la página y la dirección de la clasificación (asc ó des) AL CREAR UNA INSTANCIA DE ESTA CLASE PageRequest.

import org.springframework.data.domain.Pageable; // Esta interfaz define los métodos para acceder a la información de paginación,
                                                 // como el número de página actual, el tamaño de la página y la dirección.
                                                 // Se utiliza por los métodos de los repositorios de Spring Data para paginar los resultados de las consultas.

import org.springframework.data.repository.query.Param; // Esta ANOTACION se utiliza para vincular parámetros de método a los parámetros de consulta
                                                        // En los métodos de los repositorios de Spring Data. Pasa parámetros dinámicos a las consultas.
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class CursoController {

    @Autowired
    private ICursoRepository iCursoRepository;

    @GetMapping
    public String home() {

        return "redirect:/cursos"; // Cuidado con el redirect: ya que solo lo podemos usar para redirigir a un endpoint que ya hemos visitado.
    }

    /**
     * {WARNING} Si creamos el atributo 'cursosList' pero no lo estamos recibiendo/utilizando en nuestro html, generara un error.
     * La clase Model, es una clase que permite agregar atributos para que los envie directamente a la vista.
     */
    @GetMapping("/cursos") // NUESTRO ENDPOINT principal.
    public String listarCursos(Model model, @RequestParam(name = "keyword", required = false) String keyword, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "3") int size) {

        try {
            List<Curso> cursos = new ArrayList<>();
            Pageable paging = PageRequest.of(page - 1, size); // Preparo una instancia de Pageable el cual nos sirve para la paginación cuando hacemos una consulta.
                                                                         // Hace un filtro a la consulta el cual trae los datos dependiendo del los argumentos de este objeto 'paging'.

            Page<Curso> pageCursos = null; // Esta instancia de Page, toma la forma de una página de tipo Curso

            if (keyword == null) {
                pageCursos = iCursoRepository.findAll(paging); // Esta (Page) página de tipo Curso le metemos la lista de cursos dependiendo los parametros de paging como la pagina actual y el tamaño de la página.
            } else {
                // el método .findByTituloContainingIgnoreCase Es una (Page) la cual recibe (keyword) la cual buscara, y (paging) con los datos que se le definieron arriba hara la paginacion (page , size).
                pageCursos = iCursoRepository.findByTituloContainingIgnoreCase(keyword, paging); // Toda la paginacion se guarda en la (Page) pageCursos
                model.addAttribute("keyword", keyword); // Esa keyword la mandamos a la vista
            }

            cursos = pageCursos.getContent(); // Ahora esa Page ya paginada metemos todoh el contenido a una lista normal.

            model.addAttribute("cursosList", cursos); // mandamos a la vista la lista
            model.addAttribute("currentPage",pageCursos.getNumber() + 1); // Obtiene la pagina actual y suma 1 para que el cliente vea una secuencia normal
            model.addAttribute("totalItems", pageCursos.getTotalElements()); // Total de cursos
            model.addAttribute("totalPages", pageCursos.getTotalPages()); // Total de páginas
            model.addAttribute("pageSize", size); // Tamaño de la página
        } catch (Exception exception) {
            model.addAttribute("message", exception.getMessage()); // Si una exception la mostrare en un alert con el message
        }
//        List<Curso> cursos = iCursoRepository.findAll();
//        cursos = iCursoRepository.findAll();
//
//        model.addAttribute("cursosList", cursos);
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

    /**
     * Se usa un @GetMapping, ya que tiene que redireccionar a un  archivo y en ese archivo van a cargar los datos que queramos actualizar.
     */
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

    @GetMapping("/export/pdf")
    public void generarReportePdf(HttpServletResponse response) throws IOException { // Método que maneja la solicitud y genera el PDF.

        response.setContentType("application/pdf"); // Establece el tipo de contenido de la respuesta como PDF.

        // Crea un formato de fecha y hora y Formatea la fecha y hora actual.
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormat.format(new Date());

        String headerKey = "Content-Disposition"; // Define la clave del emcabezado para la decarga del archivo.
        String headerValue = "attachment; filename=cursos" + currentDateTime + ".pdf"; // Define el valor del emcabezado con el nombre del archivo.
        response.setHeader(headerKey, headerValue); // Establece el encabezado en la respuesta para indicar que es un archivo adjunto.

        List<Curso> cursos = iCursoRepository.findAll(); // Recupera todos los cursos de la base de datos.

        CursoExporterPDF exporterPDF = new CursoExporterPDF(cursos); // Crea una instancia de CursoExporterPDf con la lista de cursos.
        exporterPDF.export(response); // Genera el PDF y lo escribe en la respuesta http.

    }


    @GetMapping("/export/excel")
    public void generarReporteExcel(HttpServletResponse response) throws IOException {

        response.setContentType("application/octet-stream");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormat.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=cursos" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<Curso> cursos = iCursoRepository.findAll();

        CursoExporterExcel exporterExcel = new CursoExporterExcel(cursos);
        exporterExcel.export(response);

    }
}
