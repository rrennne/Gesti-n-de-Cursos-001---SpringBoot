package com.renecode.gestioncursos.reports;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.renecode.gestioncursos.entity.Curso;
import jakarta.servlet.http.HttpServletResponse;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class CursoExporterPDF {

    private List<Curso> listaCursos;

    /**
     * El constructor toma la lista de objetos Cursos como argumento y lo asigna al campo listaCursos.
     * */
    public CursoExporterPDF(List<Curso> listaCursos) {
        this.listaCursos = listaCursos;
    }

    /**
     * Este método es para escribir el titulo de cada celda y darle un estilo a la fuente.
     */
    private void writeTableHeader(PdfPTable table) {

        PdfPCell cell = new PdfPCell(); // Crea una celda.

        // Se le da un estilo a la celda.
        cell.setBackgroundColor(Color.BLUE);
        cell.setPadding(5);

        // También creamos la fuente y la configuramos.
        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.WHITE);

        // Iremos agregando una nueva celda con un nombre y su fuente a la tabla
        cell.setPhrase(new Phrase("ID", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Titulo", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Descripcion", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Nivel", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Publicado", font));
        table.addCell(cell);

    }

    /**
     * Iteramos la lista de cursos y tomamos cada valor en una celda.
     */
    private void writeTableData(PdfPTable table) {

        for (Curso curso : listaCursos) {
            table.addCell(String.valueOf(curso.getId()));
            table.addCell(curso.getTitulo());
            table.addCell(curso.getDescripcion());
            table.addCell(String.valueOf(curso.getNivel()));
            table.addCell(String.valueOf(curso.isPublicado())); // El get no va. // es isPublicado.
        }
    }

    /**
     * Esté es el método más importante ya que es el que se encarga de crear el pdf y escribe
     * el pdf en el cuerpo de la respuesta Http
     */
    public void export(HttpServletResponse response) throws IOException {

        Document document = new Document(PageSize.A4); // Se crea un Document con un tamaño de pagina.
        /*
          se obtiene una instacia de 'PdfWriter' que escribe en el flujo de salida
          lo que permite escribir el contenido del documento PDF directamente en la
          respuesta HTTP qu será enviada al cliente.
        */
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open(); // Se abre el documento para empezar a editarlo.

        // Se crea una instancia de Font para darle un estilo a la fuente.
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(18);
        font.setColor(Color.BLUE);

        // Creamos un parrafo con una cadena de texto , y su estilo previamente creado.
        Paragraph p = new Paragraph("Lista de cursos", font);
        p.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(p); // Se agrega ese parrafo al documento.

        PdfPTable table = new PdfPTable(5); // Crea una tabla con 5 columnas.
        table.setWidthPercentage(100f); // El tamaño de la tabla con respecto al ancho de la pagina es de 100, osea el todal de la pagina.
        table.setWidths(new float[]{1.3f, 3.5f, 3.5f, 2.0f, 1.5f}); // Y cada columna tendra un tamaño especifico.
        table.setSpacingBefore(10); // Antes de la tabla se le dara un espaciado, separando el contendio al readedor de la tabla de 10.

        // Escribimos el emcabezado y los datos de la tabla.
        writeTableHeader(table);
        writeTableData(table);

        document.add(table); // Ahora se agrega toda la tabla al documento.

        document.close(); // Y se cierra el documento.

    }

}
