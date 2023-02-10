package demo;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

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

//Las 2 clases necesarias para usar regex
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestPDF {

    /**
     * Indica la resolucion de la imagen a generar.
     */
    private static final int RESOLUTION_PDI = 1000;
    String pathInput = "C:\\Users\\htique\\Documents\\Seguros Bolivar\\Proceso pagos de consulta externa\\Data\\FACTURAS DE PRUEBA\\1156870\\Adjuntar_Factura_Asistencial1025898_FAC_1156870_0.pdf";
	String pathOutput = "C:\\Users\\htique\\Documents\\Seguros Bolivar\\Proceso pagos de consulta externa\\Data\\FACTURAS DE PRUEBA\\1156360\\OCR\\";
    String Prueba = "PruebaOCR";
	String text = "";
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
                //log.error("Error convirtiendo el PDF a tiff con el archivo: " + pathInput + ", motivo: " 
                //        + e.getMessage());
            }
        }

        try (var fos = new FileOutputStream(pathOutput + Prueba +".tiff")) {

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
            text = tesseract.doOCR(new File(pathOutput +Prueba+".TIFF"));
  
            // path of your image file
            //System.out.print(text);
            
            Pattern pattern = Pattern.compile("NUMERO_AUTORIZACIÓN ([a-zA-Z\\_0-9]+) ",Pattern.CASE_INSENSITIVE);
            Pattern pattern1 = Pattern.compile("TIPO_DOCUMENTO_IDENTIFICACION ([a-zA-Z\\_0-9]+)",Pattern.CASE_INSENSITIVE);
            Pattern pattern2 = Pattern.compile("NUMERO_DOCUMENTO_IDENTIFICACION ([a-zA-Z\\_0-9]+)",Pattern.CASE_INSENSITIVE);
            Pattern pattern3 = Pattern.compile("FECHA_INICIO_FACTURACIÓN USUARIO ([0-9]+[^s]+[0-9]+[^s]+[0-9])",Pattern.CASE_INSENSITIVE);
            Pattern pattern4 = Pattern.compile("FECHA_FIN_FACTURACIÓN_ USUARIO ([0-9]+[^s]+[0-9]+[^s]+[0-9])",Pattern.CASE_INSENSITIVE);
            Pattern pattern5 = Pattern.compile("COPAGO ([a-zA-Z\\_0-9]+)",Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);
            Matcher matcher1 = pattern1.matcher(text);
            Matcher matcher2 = pattern2.matcher(text);
            Matcher matcher3 = pattern3.matcher(text);
            Matcher matcher4 = pattern4.matcher(text);
            Matcher matcher5 = pattern5.matcher(text);
            
          /*  NUMERO_DOCUMENTO_IDENTIFICACION
            NUMERO_POLIZA
            COPAGO
            FECHA_INICIO_FACTURACIÓN USUARIO
            FECHA_FIN_FACTURACIÓN_ USUARIO
            */
          //Ahora sí, vemos si coincide el patrón con el texto
            if (matcher.find()) {
                //Coincidió => obtener el valor del grupo 1
                String nAutorizacion = matcher.group(1);
                System.out.println("Número autorización: "+  nAutorizacion);
            } else {
                //No coincidió
                System.out.println("No se encontro el numero de autorización");
            }
            
            
            if (matcher1.find()) {
                //Coincidió => obtener el valor del grupo 1
                String tDocumento = matcher1.group(1);
                System.out.println("Tipo de Documento: " + tDocumento);
            } else {
                //No coincidió
                System.out.println("No se encontro tipo de documento");
            } 
            
            if (matcher2.find()) {
                //Coincidió => obtener el valor del grupo 1
                String nDocumento = matcher2.group(1);
                System.out.println("Número de documento: " + nDocumento);
            } else {
                //No coincidió
                System.out.println("No se encontro el numero de documento");
            }
            
            if (matcher3.find()) {
                //Coincidió => obtener el valor del grupo 1
                String fInicio = matcher3.group(1);
                System.out.println("Fecha inicio facturación: " + fInicio);
            } else {
                //No coincidió
                System.out.println("No se encontro fecha de inicio de facturación");
            }
            if (matcher4.find()) {
                //Coincidió => obtener el valor del grupo 1
                String fFin = matcher4.group(1);
                System.out.println("Fecha fin facturación:: " + fFin);
            } else {
                //No coincidió
                System.out.println("No se encontro fecha de fin de facturación");
            }
            if (matcher5.find()) {
                //Coincidió => obtener el valor del grupo 1
                String copago = matcher5.group(1);
                System.out.println("Valor copago: " + copago);
            } else {
                //No coincidió
                System.out.println("No se encontro copago");
            }
            
            
            
        }
        catch (TesseractException e) {
            e.printStackTrace();
        }
	}
    
    public static void main(String[] args) throws IOException {
		
    	TestPDF prueba = new TestPDF();
		//prueba.convertPDFAsTiff();
		prueba.Tesseract();
		
		
		
	}
}