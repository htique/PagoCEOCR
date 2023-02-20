package demo;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;

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
import java.util.Scanner;
import java.util.logging.Logger;
//Las 2 clases necesarias para usar regex
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class TestPDF {

	/**
	 * Indica la resolucion de la imagen a generar.
	 */
	private static final int RESOLUTION_PDI = 800;
	// private static final Logger logger = (Logger)
	// LoggerFactory.getLogger(TestPDF.class);
	static File folder1 = new File(
			"C:\\Users\\htique\\Documents\\Seguros Bolivar\\Proceso pagos de consulta externa\\Data\\FACTURAS DE PRUEBA\\1157134\\");
	static String FolderOCR = "\\OCR\\";
	static File directorio = new File(folder1 + FolderOCR );
	static String FileFolder;
	static String archivotxt = "\\texto.txt";
	String linea = null;
	String linea1 = null;
	FileReader fr = null;
	BufferedReader br = null;
	static int lNumeroLineas = 0;
	static int total= 0;
	// static File archivo=new File("C:\\Users\\htique\\Documents\\Seguros
	// Bolivar\\Proceso pagos de consulta externa\\Data\\FACTURAS DE
	// PRUEBA\\MET10206\\texto.txt");
	// static File archivo1=new File("C:\\Users\\htique\\Documents\\Seguros
	// Bolivar\\Proceso pagos de consulta externa\\Data\\FACTURAS DE
	// PRUEBA\\MET10206\\texto.txt");

	/*
	 * String pathInput =
	 * "C:\\Users\\htique\\Documents\\Seguros Bolivar\\Proceso pagos de consulta externa\\Data\\FACTURAS DE PRUEBA\\MET10206\\Adjuntar_Factura_Asistencial1140911_FAC_MET10206_0.pdf"
	 * ; String pathOutput =
	 * "C:\\Users\\htique\\Documents\\Seguros Bolivar\\Proceso pagos de consulta externa\\Data\\FACTURAS DE PRUEBA\\MET10206\\OCR\\"
	 * ; String Prueba = "PruebaOCR";
	 */
	String text;

	/**
	 * Convierte un PDF paginado en imagen Tiff con paginas.
	 *
	 * @param pathInput  archivo a procesar.
	 * @param pathOutput archivo procesado (path imgen tiff).
	 * @throws IOException excepcion que se pueda generar cargando el PDF a memoria.
	 */
	public static void findAllFilesInFolder(File folder) throws IOException {
		
		//File directorio = new File(folder1 + FolderOCR );
        if (!directorio.exists()) {
            if (directorio.mkdirs()) {
                System.out.println("Directorio creado");
            } else {
                System.out.println("Error al crear directorio");
            }
        }
        
		for (File file : folder.listFiles()) {
			if (!file.isDirectory()) {
				FileFolder = file.getName();
				System.out.println(FileFolder);
				// archivo=new File("C:\\Users\\htique\\Documents\\Seguros Bolivar\\Proceso
				// pagos de consulta externa\\Data\\FACTURAS DE PRUEBA\\MET10206\\texto.txt");
				try (FileWriter escribir = new FileWriter((directorio + archivotxt), true)) {
					escribir.write(FileFolder);
					escribir.write("\n");
					// System.out.println(FileFolder);
				}
				
				
			} else {
				findAllFilesInFolder(file);
			}
		}
	}
	
	
	public void convertPDFAsTiff() throws IOException {
		
		fr = new FileReader(directorio + archivotxt);
		Scanner sc = new Scanner(fr);
		int count = 0;
		while(sc.hasNextLine()) {
	        sc.nextLine();
	        total = count++;
	      }
	      System.out.println("Total Number of Lines: " + total);

	      //sc.close();
		
		try {
			// Apertura del fichero y creacion de BufferedReader para poder
			// hacer una lectura comoda (disponer del metodo readLine()).
			// archivo = new File ("C:\\archivo.txt");
			fr = new FileReader(directorio + archivotxt);
			br = new BufferedReader(fr);
			System.out.println("Total Number of Lines: " + total);	
			
			// Lectura del fichero
			for (int i = 0; i < total; i++) {
				if ((linea = br.readLine()) != null) {
					linea1 = linea;
					
					// System.out.println(linea);
					System.out.println(folder1 + "\\" + linea1);
					// System.out.println(folder1+"\\"+linea1);
					var pdf = PDDocument.load(new File(folder1 + "\\" + linea1));
					var images = new BufferedImage[pdf.getNumberOfPages()];
					var pdfrender = new PDFRenderer(pdf);

					for (var j = 0; j < images.length; j++) {

						try {

							BufferedImage image = pdfrender.renderImageWithDPI(j, RESOLUTION_PDI, ImageType.GRAY);
							images[j] = image;

						} catch (IOException e) {
							// logger.info("Error convirtiendo el PDF a tiff con el archivo: " + pathInput +
							// ", motivo: "
							// + e.getMessage());
						}
					}

					try (var fos = new FileOutputStream(directorio +"\\"+ linea1.replaceAll(".pdf", ".TIFF"))) {

						RandomAccessOutputStream rout = new FileCacheRandomAccessOutputStream(fos);

						ImageParam.ImageParamBuilder builder = ImageParam.getBuilder();

						var param = new ImageParam[1];

						var tiffOptions = new TIFFOptions();

						tiffOptions.setTiffCompression(Compression.CCITTFAX4);

						builder.imageOptions(tiffOptions);
						builder.colorType(ImageColorType.BILEVEL).ditherMatrix(DitherMatrix.getBayer8x8Diag())
								.applyDither(true).ditherMethod(DitherMethod.BAYER);

						param[0] = builder.build();

						TIFFTweaker.writeMultipageTIFF(rout, images, param);

						rout.close();
					}
				}
			} 
			/*
			 * while((linea=br.readLine())!=null) linea1 = linea;
			 * //System.out.println(linea); System.out.println(folder1+"\\"+linea1);
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void Tesseract() throws IOException {

		Tesseract tesseract = new Tesseract();
		
		fr = new FileReader(directorio + archivotxt);
		Scanner sc = new Scanner(fr);
		int count = 0;
		while(sc.hasNextLine()) {
	        sc.nextLine();
	        total = count++;
	      }
	      System.out.println("Total Number of Lines: " + total);
		try {

			// Apertura del fichero y creacion de BufferedReader para poder
			// hacer una lectura comoda (disponer del metodo readLine()).
			// archivo = new File ("C:\\archivo.txt");
			fr = new FileReader(directorio + archivotxt);
			br = new BufferedReader(fr);

			// Lectura del fichero
			for (int i = 0; i < total; i++) {
				if ((linea = br.readLine()) != null) {
					linea1 = linea;
					// System.out.println(linea);
					System.out.println(folder1 + "\\" + linea1);
					// System.out.println(folder1+"\\"+linea1);

					tesseract.setLanguage("spa");
					tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
					System.out.println(linea1);
					// the path of your tess data folder
					// inside the extracted file
					text = tesseract.doOCR(new File(directorio +"\\"+ linea1.replaceAll(".pdf", ".TIFF")));

					// path of your image file
					//System.out.print(text);

					List<Matcher> numeroautorizacion = new ArrayList<>();
					Pattern nAutorizacion1 = Pattern.compile("NUMERO_AUTORIZACIÓN ([a-zA-Z\\_0-9]+)", //OK
							Pattern.CASE_INSENSITIVE);
					Pattern nAutorizacion2 = Pattern.compile(
							"AUTORIZACION t ([a-zA-Z\\_0-9]+[ t n x0B f r]+[a-zA-Z\\_0-9]+) ", //OK
							Pattern.CASE_INSENSITIVE);
					Pattern nAutorizacion3 = Pattern.compile("NUMERO_AUTORIZACION ([a-zA-Z\\_0-9]+) ",
							Pattern.CASE_INSENSITIVE);
					Matcher nautorizacion1 = nAutorizacion1.matcher(text);
					Matcher nautorizacion2 = nAutorizacion2.matcher(text);
					Matcher nautorizacion3 = nAutorizacion3.matcher(text);
					numeroautorizacion.add(nautorizacion1);
					numeroautorizacion.add(nautorizacion2);
					numeroautorizacion.add(nautorizacion3);

					List<Matcher> tipodocumento = new ArrayList<>();
					Pattern tDocumento1 = Pattern.compile("TIPO_DOCUMENTO_IDENTIFICACION ([a-zA-Z\\_0-9]+)",  //OK
							Pattern.CASE_INSENSITIVE);
					Pattern tDocumento2 = Pattern.compile("Identificación: ([a-zA-Z\\_0-9]+)", //OK
							Pattern.CASE_INSENSITIVE);
					Pattern tDocumento3 = Pattern.compile("TIPO DOCUMENTO IDENTIFICACION ([a-zA-Z\\_0-9]+)",
							Pattern.CASE_INSENSITIVE);
					Matcher tdocumento1 = tDocumento1.matcher(text);
					Matcher tdocumento2 = tDocumento2.matcher(text);
					Matcher tdocumento3 = tDocumento3.matcher(text);
					tipodocumento.add(tdocumento1);
					tipodocumento.add(tdocumento2);
					tipodocumento.add(tdocumento3);

					List<Matcher> numerodocumento = new ArrayList<>();
					Pattern nDocumento1 = Pattern.compile("NUMERO_DOCUMENTO_IDENTIFICACION ([a-zA-Z\\_0-9]+)", //OK
							Pattern.CASE_INSENSITIVE);
					Pattern nDocumento2 = Pattern.compile(
							"Identificación: ([a-zA-Z\\_0-9]+[ t n x0B f r]+[a-zA-Z\\_0-9]+)", //OK
							Pattern.CASE_INSENSITIVE);
					Pattern nDocumento3 = Pattern.compile("NUMERO DOCUMENTO_IDENTIFICACION ([a-zA-Z\\_0-9]+)",
							Pattern.CASE_INSENSITIVE);
					Matcher ndocumento1 = nDocumento1.matcher(text);
					Matcher ndocumento2 = nDocumento2.matcher(text);
					Matcher ndocumento3 = nDocumento3.matcher(text);
					numerodocumento.add(ndocumento1);
					numerodocumento.add(ndocumento2);
					numerodocumento.add(ndocumento3);

					List<Matcher> nombrepaciente = new ArrayList<>();
					Pattern nPaciente1 = Pattern.compile(
							"PACIENTE: ([a-zA-ZÀ-ÿ\\u00f1\\u00d1]+[ t n x0B f r]+[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+[ t n x0B f r]+[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+)"); //OK
					Pattern nPaciente2 = Pattern.compile("PACIENTE: ([a-zA-ZÀ-ÿ\\u00f1\\u00d1]+[ t n x0B f r]+[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+)",
							Pattern.CASE_INSENSITIVE);
					Pattern nPaciente3 = Pattern.compile("PACIENTE: ([a-zA-Z\\_0-9]+[S]+[a-zA-Z\\_0-9]) ");
					Matcher npaciente1 = nPaciente1.matcher(text);
					Matcher npaciente2 = nPaciente2.matcher(text);
					Matcher npaciente3 = nPaciente3.matcher(text);
					nombrepaciente.add(npaciente1);
					nombrepaciente.add(npaciente2);
					nombrepaciente.add(npaciente3);

					List<Matcher> fechainicio = new ArrayList<>();
					Pattern fInicio1 = Pattern.compile("FECHA_INICIO_FACTURACIÓN USUARIO ([0-9]+[^s]+[0-9]+[^s]+[0-9])",
							Pattern.CASE_INSENSITIVE);
					Pattern fInicio2 = Pattern.compile("FECHA DE INGRESO: ([0-9]+[^s]+[0-9]+[^s]+[0-9])",
							Pattern.CASE_INSENSITIVE);
					Pattern fInicio3 = Pattern.compile("FECHA INICIO FACTURACIÓN USUARIO ([0-9]+[^s]+[0-9]+[^s]+[0-9])",
							Pattern.CASE_INSENSITIVE);
					Matcher finicio1 = fInicio1.matcher(text);
					Matcher finicio2 = fInicio2.matcher(text);
					Matcher finicio3 = fInicio3.matcher(text);
					fechainicio.add(finicio1);
					fechainicio.add(finicio2);
					fechainicio.add(finicio3);

					List<Matcher> fechafin = new ArrayList<>();
					Pattern fFin1 = Pattern.compile("FECHA_FIN_FACTURACIÓN_ USUARIO ([0-9]+[^s]+[0-9]+[^s]+[0-9])",
							Pattern.CASE_INSENSITIVE);
					Pattern fFin2 = Pattern.compile("FECHA_FIN_FACTURACIÓN  USUARIO ([0-9]+[^s]+[0-9]+[^s]+[0-9])",
							Pattern.CASE_INSENSITIVE);
					Pattern fFin3 = Pattern.compile("FECHA_FIN_FACTURACIÓN USUARIO ([0-9]+[^s]+[0-9]+[^s]+[0-9])",
							Pattern.CASE_INSENSITIVE);
					Matcher ffin1 = fFin1.matcher(text);
					Matcher ffin2 = fFin2.matcher(text);
					Matcher ffin3 = fFin3.matcher(text);
					fechafin.add(ffin1);
					fechafin.add(ffin2);
					fechafin.add(ffin3);

					List<Matcher> copago = new ArrayList<>();
					Pattern copago1 = Pattern.compile("COPAGO ([a-zA-Z\\_0-9]+)", Pattern.CASE_INSENSITIVE); //OK
					Pattern copago2 = Pattern.compile("COPAGO ([a-zA-Z\\_0-9]+)", Pattern.CASE_INSENSITIVE);
					Pattern copago3 = Pattern.compile("COPAGO ([a-zA-Z\\_0-9]+)", Pattern.CASE_INSENSITIVE);
					Matcher Copago1 = copago1.matcher(text);
					Matcher Copago2 = copago2.matcher(text);
					Matcher Copago3 = copago3.matcher(text);
					copago.add(Copago1);
					copago.add(Copago2);
					copago.add(Copago3);

					for (int j = 0; j < numeroautorizacion.size(); j++) {
						// Ahora sí, vemos si coincide el patrón con el texto
						if (numeroautorizacion.get(j).find()) {
							// Coincidió => obtener el valor del grupo 1
							String nAutorizacion = numeroautorizacion.get(j).group(1).replace(" ", "");
							System.out.println("Número autorización: " + nAutorizacion);
							break;
						} else {
							// No coincidió
							System.out.println("No se encontro el numero de autorización");

						}
					}

					for (int j = 0; j < tipodocumento.size(); j++) {
						// Ahora sí, vemos si coincide el patrón con el texto
						if (tipodocumento.get(j).find()) {
							// Coincidió => obtener el valor del grupo 1
							String tDocumento = tipodocumento.get(j).group(1);
							System.out.println("Tipo de Documento: " + tDocumento);
							break;
						} else {
							// No coincidió
							System.out.println("No se encontro tipo de documento");

						}
					}

					for (int j = 0; j < numerodocumento.size(); j++) {
						if (numerodocumento.get(j).find()) {
							// Coincidió => obtener el valor del grupo 1
							String nDocumento = numerodocumento.get(j).group(1).replace(" ", "");
							char[] numerodoc = nDocumento.toCharArray();
							String n = "";
							for (int k = 0; k < numerodoc.length; k++) {
								if (Character.isDigit(numerodoc[k])) {
									n += numerodoc[k];
								}
							}
							System.out.println("Número de documento: " + n);
							break;
						} else {
							// No coincidió
							System.out.println("No se encontro el numero de documento");

						}
					}

					for (int j = 0; j < nombrepaciente.size(); j++) {
						if (nombrepaciente.get(j).find()) {
							// Coincidió => obtener el valor del grupo 1
							String nPaciente = nombrepaciente.get(j).group(1);
							System.out.println("Nombre paciente: " + nPaciente);
							break;
						} else {
							// No coincidió
							System.out.println("No se encontro el nombre del paciente");

						}
					}

					for (int j = 0; j < fechainicio.size(); j++) {
						if (fechainicio.get(j).find()) {
							// Coincidió => obtener el valor del grupo 1
							String fInicio = fechainicio.get(j).group(1);
							//System.out.println("Fecha inicio facturación: " + fInicio);
							break;
						} else {
							// No coincidió
							//System.out.println("No se encontro fecha de inicio de facturación");

						}
					}

					for (int j = 0; j < fechafin.size(); j++) {
						if (fechafin.get(j).find()) {
							// Coincidió => obtener el valor del grupo 1
							String fFin = fechafin.get(j).group(1);
							//System.out.println("Fecha fin facturación: " + fFin);
							break;
						} else {
							// No coincidió
							//System.out.println("No se encontro fecha de fin de facturación");

						}
					}

					for (int j = 0; j < copago.size(); j++) {
						if (copago.get(j).find()) {
							// Coincidió => obtener el valor del grupo 1
							String COPAGO = copago.get(j).group(1);
							System.out.println("Valor copago: " + COPAGO);
							break;
						} else {
							// No coincidió
							System.out.println("No se encontro copago");

						}
					}
				}
			}
		} catch (TesseractException e) {
			e.printStackTrace();
		}
	}

	public void clearFolder() throws IOException {

		/*fr = new FileReader(folder1 + archivotxt);
		br = new BufferedReader(fr);

		// Lectura del fichero
		for (int i = 0; i < 2; i++) {
			if ((linea = br.readLine()) != null) {
				linea1 = linea;
				// System.out.println(linea);
				System.out.println(folder1 + "\\" + linea1.replaceAll(".pdf", ".TIFF"));

				File fichero = new File(folder1 + FolderOCR + linea1.replaceAll(".pdf", ".TIFF"));

				if (fichero.delete())
					System.out.println("El fichero ha sido borrado satisfactoriamente");
				else
					System.out.println("El fichero no pudó ser borrado");
			}
		}*/
		
		//String sDirectorio = "c:\\directorio";
		
		/*		
		if (directorio.delete())
		 System.out.println("El fichero " + directorio + " ha sido borrado correctamente");
		else
		 System.out.println("El fichero " + directorio + " no se ha podido borrar");*/
		FileUtils.deleteDirectory(directorio);
	}

	public static void main(String[] args) throws IOException {

		TestPDF prueba = new TestPDF();
		// prueba.findAllFilesInFolder(folder1);
		/*
		 * File folder = new File
		 * ("C:\\Users\\htique\\Documents\\Seguros Bolivar\\Proceso pagos de consulta externa\\Data\\FACTURAS DE PRUEBA\\MET10206"
		 * ); findAllFilesInFolder(folder);
		 */
		// prueba.convertPDFAsTiff();
		 prueba.Tesseract();
		// prueba.clearFolder();

	}
}