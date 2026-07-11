package carzone.reporte;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DocumentoPDF {

    private static final Charset LATIN1 = Charset.forName("ISO-8859-1");
    private static final float ANCHO_PAGINA = 612f;
    private static final float ALTO_PAGINA = 792f;

    private final List<StringBuilder> paginas = new ArrayList<>();
    private StringBuilder paginaActual;

    private static final java.util.Map<Character, Integer> ANCHOS_CARACTER = new java.util.HashMap<>();

    static {
        for (char c = '0'; c <= '9'; c++) {
            ANCHOS_CARACTER.put(c, 556);
        }
        ANCHOS_CARACTER.put(',', 278);
        ANCHOS_CARACTER.put('.', 278);
        ANCHOS_CARACTER.put(' ', 278);
        ANCHOS_CARACTER.put('/', 278);
        ANCHOS_CARACTER.put('S', 667);
        ANCHOS_CARACTER.put('-', 333);
    }

    public DocumentoPDF() {
        nuevaPagina();
    }

    public float anchoAprox(String texto, float tamano) {
        if (texto == null || texto.isEmpty()) {
            return 0f;
        }
        int total = 0;
        for (int i = 0; i < texto.length(); i++) {
            total += ANCHOS_CARACTER.getOrDefault(texto.charAt(i), 556);
        }
        return total * tamano / 1000f;
    }

    public final void nuevaPagina() {
        paginaActual = new StringBuilder();
        paginas.add(paginaActual);
    }

    public float ancho() {
        return ANCHO_PAGINA;
    }

    public float alto() {
        return ALTO_PAGINA;
    }

    public void texto(float x, float y, String contenido, float tamano, boolean negrita) {
        texto(x, y, contenido, tamano, negrita, 0f, 0f, 0f);
    }

    public void texto(float x, float y, String contenido, float tamano, boolean negrita, float r, float g, float b) {
        String fuente = negrita ? "/F2" : "/F1";
        float pdfY = ALTO_PAGINA - y;
        paginaActual.append("BT ").append(num(r)).append(' ').append(num(g)).append(' ').append(num(b))
                .append(" rg ").append(fuente).append(' ')
                .append(num(tamano)).append(" Tf ")
                .append(num(x)).append(' ').append(num(pdfY)).append(" Td (")
                .append(escapar(contenido)).append(") Tj ET\n0 0 0 rg\n");
    }

    public void rectangulo(float x, float y, float w, float h, float r, float g, float b, boolean relleno) {
        float pdfY = ALTO_PAGINA - y - h;
        paginaActual.append(num(r)).append(' ').append(num(g)).append(' ').append(num(b))
                .append(relleno ? " rg\n" : " RG\n");
        paginaActual.append(num(x)).append(' ').append(num(pdfY)).append(' ')
                .append(num(w)).append(' ').append(num(h)).append(" re ")
                .append(relleno ? "f\n" : "S\n");
        paginaActual.append("0 0 0 rg\n0 0 0 RG\n");
    }

    public void linea(float x1, float y1, float x2, float y2) {
        float py1 = ALTO_PAGINA - y1;
        float py2 = ALTO_PAGINA - y2;
        paginaActual.append("0.5 w\n").append(num(x1)).append(' ').append(num(py1)).append(" m ")
                .append(num(x2)).append(' ').append(num(py2)).append(" l S\n");
    }

    public byte[] generar() {
        List<Integer> offsets = new ArrayList<>();
        StringBuilder out = new StringBuilder();

        out.append("%PDF-1.4\n");

        int numPaginas = paginas.size();
        int objCatalogo = 1;
        int objPages = 2;
        int objFontRegular = 3;
        int objFontBold = 4;
        int primerObjPagina = 5;
        int primerObjContenido = primerObjPagina + numPaginas;

        offsets.add(out.length());
        out.append(objCatalogo).append(" 0 obj\n<< /Type /Catalog /Pages ")
                .append(objPages).append(" 0 R >>\nendobj\n");

        offsets.add(out.length());
        StringBuilder kids = new StringBuilder();
        for (int i = 0; i < numPaginas; i++) {
            kids.append(primerObjPagina + i).append(" 0 R ");
        }
        out.append(objPages).append(" 0 obj\n<< /Type /Pages /Kids [ ").append(kids)
                .append("] /Count ").append(numPaginas).append(" >>\nendobj\n");

        offsets.add(out.length());
        out.append(objFontRegular).append(" 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>\nendobj\n");

        offsets.add(out.length());
        out.append(objFontBold).append(" 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold /Encoding /WinAnsiEncoding >>\nendobj\n");

        for (int i = 0; i < numPaginas; i++) {
            offsets.add(out.length());
            int objPagina = primerObjPagina + i;
            int objContenido = primerObjContenido + i;
            out.append(objPagina).append(" 0 obj\n<< /Type /Page /Parent ").append(objPages)
                    .append(" 0 R /MediaBox [0 0 ").append((int) ANCHO_PAGINA).append(' ').append((int) ALTO_PAGINA)
                    .append("] /Resources << /Font << /F1 ").append(objFontRegular).append(" 0 R /F2 ")
                    .append(objFontBold).append(" 0 R >> >> /Contents ").append(objContenido).append(" 0 R >>\nendobj\n");
        }

        for (int i = 0; i < numPaginas; i++) {
            offsets.add(out.length());
            int objContenido = primerObjContenido + i;
            String contenido = paginas.get(i).toString();
            int longitud = contenido.getBytes(LATIN1).length;
            out.append(objContenido).append(" 0 obj\n<< /Length ").append(longitud).append(" >>\nstream\n")
                    .append(contenido).append("endstream\nendobj\n");
        }

        int totalObjetos = primerObjContenido + numPaginas - 1;
        int xrefOffset = out.length();

        out.append("xref\n0 ").append(totalObjetos + 1).append('\n');
        out.append("0000000000 65535 f \n");
        for (int off : offsets) {
            out.append(String.format(Locale.US, "%010d 00000 n \n", off));
        }
        out.append("trailer\n<< /Size ").append(totalObjetos + 1).append(" /Root ").append(objCatalogo)
                .append(" 0 R >>\nstartxref\n").append(xrefOffset).append("\n%%EOF");

        return out.toString().getBytes(LATIN1);
    }

    private String num(float valor) {
        if (valor == (long) valor) {
            return String.valueOf((long) valor);
        }
        return String.format(Locale.US, "%.2f", valor);
    }

    private String escapar(String s) {
        if (s == null) {
            return "";
        }
        StringBuilder r = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(' || c == ')' || c == '\\') {
                r.append('\\');
            }
            if (c > 255) {
                c = '?';
            }
            r.append(c);
        }
        return r.toString();
    }
}
