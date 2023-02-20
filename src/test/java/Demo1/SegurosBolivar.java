package Demo1;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
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

import demo.TestPDF;

public class SegurosBolivar {
	
	 /**
     * Indica la resolucion de la imagen a generar.
     */
    private static final int RESOLUTION_PDI = 800;
    //private static final Logger logger = (Logger) LoggerFactory.getLogger(TestPDF.class);
    static File folder = new File ("C:\\Users\\htique\\Documents\\Seguros Bolivar\\Proceso pagos de consulta externa\\Data\\FACTURAS DE PRUEBA\\MET10206\\");
    
    static String FileFolder;
    String pathInput = "C:\\Users\\htique\\Documents\\Seguros Bolivar\\Proceso pagos de consulta externa\\Data\\FACTURAS DE PRUEBA\\MET10206\\Adjuntar_Factura_Asistencial1140911_FAC_MET10206_0.pdf";
	static String pathOutput = "C:\\Users\\htique\\Documents\\Seguros Bolivar\\Proceso pagos de consulta externa\\Data\\FACTURAS DE PRUEBA\\MET10206\\OCR\\";
    static String Prueba = "PruebaOCR";
	String text;
	/**
     * Convierte un PDF paginado en imagen Tiff con paginas.
     *
     * @param pathInput archivo a procesar.
     * @param pathOutput archivo procesado (path imgen tiff).
     * @throws IOException excepcion que se pueda generar cargando el PDF a memoria.
     */
	public static void findAllFilesInFolder(File folder) throws IOException {
		
		for (File file : folder.listFiles()) {
			if (!file.isDirectory()) {
				FileFolder = file.getName();
				System.out.println(file.getName());
				//System.out.println(FileFolder);
			} else {
				findAllFilesInFolder(file);
			}
		}
	
    	System.out.println(FileFolder);
        var pdf = PDDocument.load(new File(folder+FileFolder));
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
        
        public static void main(String[] args) throws IOException  {
    		
        	TestPDF prueba = new TestPDF();
        	prueba.findAllFilesInFolder(folder);
        	/*File folder = new File ("C:\\Users\\htique\\Documents\\Seguros Bolivar\\Proceso pagos de consulta externa\\Data\\FACTURAS DE PRUEBA\\MET10206");
        	findAllFilesInFolder(folder);*/
    		//prueba.convertPDFAsTiff();
    		//prueba.Tesseract();
    		//prueba.clearFolder();
    		
    	}
	
}
