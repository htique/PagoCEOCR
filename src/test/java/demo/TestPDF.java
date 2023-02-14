package demo;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.LoggerFactory;

import com.icafe4j.image.ImageColorType;
import com.icafe4j.image.ImageParam;
import com.icafe4j.image.options.TIFFOptions;
import com.icafe4j.image.quant.DitherMatrix;
import com.icafe4j.image.quant.DitherMethod;
import com.icafe4j.image.tiff.TIFFTweaker;
import com.icafe4j.image.tiff.TiffFieldEnum.Compression;
import com.icafe4j.io.FileCacheRandomAccessOutputStream;
import com.icafe4j.io.RandomAccessOutputStream;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
//Las 2 clases necesarias para usar regex
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestPDF {

    /**
     * Indica la resolucion de la imagen a generar.
     */
    private static final int RESOLUTION_PDI = 800;
    //private static final Logger logger = (Logger) LoggerFactory.getLogger(TestPDF.class);
    String pathInput = "C:\\Users\\htique\\Documents\\Seguros Bolivar\\Proceso pagos de consulta externa\\Data\\FACTURAS DE PRUEBA\\1157134\\Adjuntar_Factura_Asistencial1027310_FAC_1157134_1.pdf";
	String pathOutput = "C:\\Users\\htique\\Documents\\Seguros Bolivar\\Proceso pagos de consulta externa\\Data\\FACTURAS DE PRUEBA\\1157134\\OCR\\";
    String Prueba = "PruebaOCR";
	String text;
	/**
     * Convierte un PDF paginado en imagen Tiff con paginas.
     *
     * @param pathInput archivo a procesar.
     * @param pathOutput archivo procesado (path imgen tiff).
     * @throws IOException excepcion que se pueda generar cargando el PDF a memoria.
     */
    public void convertPDFAsTiff() throws IOException {
    	
    	
        var pdf = PDDocument.load(new File(pathInput));
        var images = new BufferedImage[pdf.getNumberOfPages()];        
        var pdfrender = new PDFRenderer(pdf);

        for (var i = 0; i < images.length; i++) {

            try {

                BufferedImage image = pdfrender.renderImageWithDPI(i, RESOLUTION_PDI, ImageType.GRAY);
                images[i] = image;

            } catch (IOException e) {
                //logger.info("Error convirtiendo el PDF a tiff con el archivo: " + pathInput + ", motivo: " 
                //        + e.getMessage());
            }
        }

        try (var fos = new FileOutputStream(pathOutput + Prueba +".TIFF")) {

            RandomAccessOutputStream rout = new FileCacheRandomAccessOutputStream(fos);

            ImageParam.ImageParamBuilder builder = ImageParam.getBuilder();

            var param = new ImageParam[1];

            var tiffOptions = new TIFFOptions();

            tiffOptions.setTiffCompression(Compression.CCITTFAX4);

            builder.imageOptions(tiffOptions);
            builder.colorType(ImageColorType.BILEVEL)
                .ditherMatrix(DitherMatrix.getBayer8x8Diag())
                .applyDither(true)
                .ditherMethod(DitherMethod.BAYER);

            param[0] = builder.build();

            TIFFTweaker.writeMultipageTIFF(rout, images, param);

            rout.close();
        }
    }
    
    public void Tesseract () {
		
		Tesseract tesseract = new Tesseract();
        try {
        	tesseract.setLanguage("spa");
            tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
  
            // the path of your tess data folder
            // inside the extracted file
            text = tesseract.doOCR(new File(pathOutput + Prueba +".TIFF"));
  
            // path of your image file
            System.out.print(text);
            
            List<Matcher> numeroautorizacion = new ArrayList<>();
            Pattern nAutorizacion1 = Pattern.compile("NUMERO_AUTORIZACIÓN ([a-zA-Z\\_0-9]+) ",Pattern.CASE_INSENSITIVE);
            Pattern nAutorizacion2 = Pattern.compile("NUMERO AUTORIZACIÓN ([a-zA-Z\\_0-9]+) ",Pattern.CASE_INSENSITIVE);
            Pattern nAutorizacion3 = Pattern.compile("NUMERO_AUTORIZACION ([a-zA-Z\\_0-9]+) ",Pattern.CASE_INSENSITIVE);
            Matcher nautorizacion1 = nAutorizacion1.matcher(text);
            Matcher nautorizacion2 = nAutorizacion2.matcher(text);
            Matcher nautorizacion3 = nAutorizacion3.matcher(text);
            numeroautorizacion.add(nautorizacion1);
            numeroautorizacion.add(nautorizacion2);
            numeroautorizacion.add(nautorizacion3);
            
        	List<Matcher> tipodocumento = new ArrayList<>();
            Pattern tDocumento1 = Pattern.compile("TIPO_DOCUMENTO_IDENTIFICACION ([a-zA-Z\\_0-9]+)",Pattern.CASE_INSENSITIVE);
            Pattern tDocumento2 = Pattern.compile("TIPO_DOCUMENTO IDENTIFICACION ([a-zA-Z\\_0-9]+)",Pattern.CASE_INSENSITIVE);
            Pattern tDocumento3 = Pattern.compile("TIPO DOCUMENTO IDENTIFICACION ([a-zA-Z\\_0-9]+)",Pattern.CASE_INSENSITIVE);
            Matcher tdocumento1 = tDocumento1.matcher(text);
            Matcher tdocumento2 = tDocumento2.matcher(text);
            Matcher tdocumento3 = tDocumento3.matcher(text);
            tipodocumento.add(tdocumento1);
            tipodocumento.add(tdocumento2);
            tipodocumento.add(tdocumento3);
            
            List<Matcher> numerodocumento = new ArrayList<>();
            Pattern nDocumento1 = Pattern.compile("NUMERO_DOCUMENTO_IDENTIFICACION ([a-zA-Z\\_0-9]+)",Pattern.CASE_INSENSITIVE);
            Pattern nDocumento2 = Pattern.compile("NUMERO_DOCUMENTO IDENTIFICACION ([a-zA-Z\\_0-9]+)",Pattern.CASE_INSENSITIVE);
            Pattern nDocumento3 = Pattern.compile("NUMERO DOCUMENTO_IDENTIFICACION ([a-zA-Z\\_0-9]+)",Pattern.CASE_INSENSITIVE);
            Matcher ndocumento1 = nDocumento1.matcher(text);
            Matcher ndocumento2 = nDocumento2.matcher(text);
            Matcher ndocumento3 = nDocumento3.matcher(text);
            numerodocumento.add(ndocumento1);
            numerodocumento.add(ndocumento2);
            numerodocumento.add(ndocumento3);
            
            List<Matcher> fechainicio = new ArrayList<>();
            Pattern fInicio1 = Pattern.compile("FECHA_INICIO_FACTURACIÓN USUARIO ([0-9]+[^s]+[0-9]+[^s]+[0-9])",Pattern.CASE_INSENSITIVE);
            Pattern fInicio2 = Pattern.compile("FECHA_INICIO FACTURACIÓN USUARIO ([0-9]+[^s]+[0-9]+[^s]+[0-9])",Pattern.CASE_INSENSITIVE);
            Pattern fInicio3 = Pattern.compile("FECHA INICIO FACTURACIÓN USUARIO ([0-9]+[^s]+[0-9]+[^s]+[0-9])",Pattern.CASE_INSENSITIVE);
            Matcher finicio1 = fInicio1.matcher(text);
            Matcher finicio2 = fInicio2.matcher(text);
            Matcher finicio3 = fInicio3.matcher(text);
            fechainicio.add(finicio1);
            fechainicio.add(finicio2);
            fechainicio.add(finicio3);
            
            List<Matcher> fechafin = new ArrayList<>();
            Pattern fFin1 = Pattern.compile("FECHA_FIN_FACTURACIÓN_ USUARIO ([0-9]+[^s]+[0-9]+[^s]+[0-9])",Pattern.CASE_INSENSITIVE);
            Pattern fFin2 = Pattern.compile("FECHA_FIN_FACTURACIÓN  USUARIO ([0-9]+[^s]+[0-9]+[^s]+[0-9])",Pattern.CASE_INSENSITIVE);
            Pattern fFin3 = Pattern.compile("FECHA_FIN FACTURACIÓN_ USUARIO ([0-9]+[^s]+[0-9]+[^s]+[0-9])",Pattern.CASE_INSENSITIVE);
            Matcher ffin1 = fFin1.matcher(text);
            Matcher ffin2 = fFin2.matcher(text);
            Matcher ffin3 = fFin3.matcher(text);
            fechafin.add(ffin1);
            fechafin.add(ffin2);
            fechafin.add(ffin3);
            
            List<Matcher> copago = new ArrayList<>();
            Pattern copago1 = Pattern.compile("COPAGO ([a-zA-Z\\_0-9]+)",Pattern.CASE_INSENSITIVE);
            Pattern copago2 = Pattern.compile("COPAGO ([a-zA-Z\\_0-9]+)",Pattern.CASE_INSENSITIVE);
            Pattern copago3 = Pattern.compile("COPAGO ([a-zA-Z\\_0-9]+)",Pattern.CASE_INSENSITIVE);
            Matcher Copago1 = copago1.matcher(text);
            Matcher Copago2 = copago2.matcher(text);
            Matcher Copago3 = copago3.matcher(text);
            copago.add(Copago1);
            copago.add(Copago2);
            copago.add(Copago3);
              
        	
            for (int i = 0; i <= numeroautorizacion.size(); i++ ) {
                //Ahora sí, vemos si coincide el patrón con el texto
                if (numeroautorizacion.get(i).find()) {	
                	//Coincidió => obtener el valor del grupo 1
                    String nAutorizacion = numeroautorizacion.get(i).group(1);
                    System.out.println("Número autorización: "+  nAutorizacion);
                    break;
                } else {
                    //No coincidió
                    System.out.println("No se encontro el numero de autorización");
                    
                }
            }
            
            for (int i = 0; i <= tipodocumento.size(); i++ ) {
            	//Ahora sí, vemos si coincide el patrón con el texto
            	if (tipodocumento.get(i).find()) {
                    //Coincidió => obtener el valor del grupo 1
                    String tDocumento = tipodocumento.get(i).group(1);
                    System.out.println("Tipo de Documento: " + tDocumento);
                    break;
                } else {
                    //No coincidió
                    System.out.println("No se encontro tipo de documento");
                    
                } 
            }
            
            for (int i = 0; i <= numerodocumento.size(); i++ ) {
            	if (numerodocumento.get(i).find()) {
                    //Coincidió => obtener el valor del grupo 1
                    String nDocumento = numerodocumento.get(i).group(1);
                    System.out.println("Número de documento: " + nDocumento);
                    break;
                } else {
                    //No coincidió
                    System.out.println("No se encontro el numero de documento");
                    
                }
            }
            
            for (int i = 0; i <= fechainicio.size(); i++ ) {
            	if (fechainicio.get(i).find()) {
                    //Coincidió => obtener el valor del grupo 1
                    String fInicio = fechainicio.get(i).group(1);
                    System.out.println("Fecha inicio facturación: " + fInicio);
                    break;
                } else {
                    //No coincidió
                    System.out.println("No se encontro fecha de inicio de facturación");
                    
                }
            }
            
            for (int i = 0; i < fechafin.size(); i++ ) {
            	if (fechafin.get(i).find()) {
                    //Coincidió => obtener el valor del grupo 1
                    String fFin = fechafin.get(i).group(1);
                    System.out.println("Fecha fin facturación: " + fFin);
                    break;
                } else {
                    //No coincidió
                    System.out.println("No se encontro fecha de fin de facturación");
                    
                }
            }
            
            for (int i = 0; i <= copago.size(); i++ ) {
            	if (copago.get(i).find()) {
                    //Coincidió => obtener el valor del grupo 1
                    String COPAGO = copago.get(i).group(1);
                    System.out.println("Valor copago: " + COPAGO);
                    break;
                } else {
                    //No coincidió
                    System.out.println("No se encontro copago");
                    
                }
            }   
        }
        catch (TesseractException e) {
            e.printStackTrace();
        }
	}
    
    public void clearFolder() {

        File fichero = new File(pathOutput +Prueba+".TIFF");

        if (fichero.delete())
            System.out.println("El fichero ha sido borrado satisfactoriamente");
        else
            System.out.println("El fichero no pudó ser borrado");
    }
    
    
    public static void main(String[] args) throws IOException {
		
    	TestPDF prueba = new TestPDF();
		//prueba.convertPDFAsTiff();
		prueba.Tesseract();
		//prueba.clearFolder();
			
	}
}