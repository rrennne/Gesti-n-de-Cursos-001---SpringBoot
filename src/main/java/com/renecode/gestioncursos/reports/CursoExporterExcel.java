package com.renecode.gestioncursos.reports;

import com.renecode.gestioncursos.entity.Curso;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;

public class CursoExporterExcel {

    private XSSFWorkbook workbook;

    private XSSFSheet sheet;

    private List<Curso> cursos;

    /**
     * <p>Se inicializa el contructor pasandole una lista de objetos <bold>Curso</bold>.</p>
     * También se inicializa el workbook.
     * */
    public CursoExporterExcel(List<Curso> cursos) {
        this.cursos = cursos;
        workbook = new XSSFWorkbook();

    }

    /**
     * <ul>
     *     <li>Creamos un página con el nombre de "Cursos".</li>
     *     <li>Que estara en en la fila 0.</li>
     *     <li>Diseñamos la fuente y se la aplicamos en la celdas de nuestra fila header.</li>
     * </ul>
     * */
    private void writeHeaderLine() {

        sheet = workbook.createSheet("Cursos");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "ID", style);
        createCell(row, 1, "Título", style);
        createCell(row, 2, "Descrinción", style);
        createCell(row, 3, "Nivel", style);
        createCell(row, 4, "Estado de publicación", style);

    }

    /**
     * Este método es una función auxiliar que crea una celda en la fila especifica con el valor
     * y estilo proporcionados.
     * */
    private void createCell(Row row, int columnCount, Object value, CellStyle style) {

        sheet.autoSizeColumn(columnCount); // Dependiendo del tipo de contenido ese será el tamaño de la columna.
        Cell cell = row.createCell(columnCount); // Se crea una celda en la fila y columna especifica por columnCount.

        if (value instanceof Integer) { // Si value es una instancia de Integer
            cell.setCellValue((Integer)value); // se asigna un valor númerico.

        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean)value);

        }
        else {
            cell.setCellValue((String)value);

        }
        cell.setCellStyle(style);

    }

    /**
     * Este método es el responsable de llenar la hoja con los datos de los cursos.
     * */
    private void writeDataLines() {

        int rowCount = 1; // Inicializa el contador de filas en 1, ya que la fila 0 se usa para el emcabezado.

        // Crea un estilo de celda para los datos.
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();

        // Configura el estilo de fuente para los datos.
        font.setFontHeight(14);
        style.setFont(font);

        // Itera sobre la lista de cursos.
        for (Curso curso : cursos) {

            // crea una nueva fila en la hoja en la hoja para cada curso.
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0; //Inicializa el contador de columnas en 0.

            // Llena la fila con los datos del curso.
            createCell(row, columnCount++, curso.getId(), style);
            createCell(row, columnCount++, curso.getTitulo(), style);
            createCell(row, columnCount++, curso.getDescripcion(), style);
            createCell(row, columnCount++, curso.getNivel(), style);
            createCell(row, columnCount++, curso.isPublicado(), style);

        }
    }

    /**
     * Este método es el encargado de escribir los datos de los cursos en la hoja
     * y enviar este archivo al cliente a través de una respuesta http.
     * */
    public void export(HttpServletResponse response) throws IOException {

        // Agrega el emcabezado y los datos.
        writeHeaderLine();
        writeDataLines();

        ServletOutputStream outputStream = response.getOutputStream(); // Obtiene el flujo de salida de la respusta http.
        workbook.write(outputStream); // Escribe el libro de trabajo en el flujo de salida.
        workbook.close(); // Cierra el libro de trabajo para liberar recursos.
        outputStream.close(); // Cierra el flujo de salida para liberar recursos.

    }
}