package demo;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
/*import org.apache.pdfbox.rendering.PDFRenderer;
//import org.apache.pdfbox.util.ImageIOUtil;

import com.icafe4j.image.ImageColorType;
import com.icafe4j.image.ImageParam;
import com.icafe4j.image.options.TIFFOptions;
import com.icafe4j.image.quant.DitherMatrix;
import com.icafe4j.image.quant.DitherMethod;
import com.icafe4j.image.tiff.TIFFTweaker;
import com.icafe4j.image.tiff.TiffFieldEnum.Compression;
import com.icafe4j.io.FileCacheRandomAccessOutputStream;
import com.icafe4j.io.RandomAccessOutputStream;
import com.itextpdf.layout.renderer.ListRenderer;

import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
*/import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.slf4j.LoggerFactory;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;
//Las 2 clases necesarias para usar regex
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
//import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

public class TestPDF3 {

	/**
	 * Indica la resolucion de la imagen a generar.
	 */
	private static final int RESOLUTION_PDI = 800;
	// private static final Logger logger = (Logger)
	// LoggerFactory.getLogger(TestPDF.class);
	static File folder1 = new File(
			"C:\\Users\\htique\\Documents\\Seguros Bolivar\\Proceso pagos de consulta externa\\Data\\FACTURAS DE PRUEBA\\VS10025693\\");
	static String FolderOCR = "\\OCR\\";
	static File directorio = new File(folder1 + FolderOCR);
	static String FileFolder;
	static String pdfatiff = "\\pdfatiff.txt";
	static String resultados = "\\resultado.txt";
	static String infosisalud = "\\infosisalud.txt";
	static File directorio1 = new File(folder1 + infosisalud);
	String linea = null;
	String linea1 = null;
	String linea2 = null;
	String linea3 = null;
	FileReader fr = null;
	BufferedReader br = null;
	FileReader fr1 = null;
	BufferedReader br1 = null;
	static int lNumeroLineas = 0;
	static int total = 1;
	static int total1 = 0;
	String text;

	/**
	 * Convierte un PDF paginado en imagen Tiff con paginas.
	 *
	 * @param pathInput  archivo a procesar.
	 * @param pathOutput archivo procesado (path imgen tiff).
	 * @throws IOException excepcion que se pueda generar cargando el PDF a memoria.
	 */
	public static void findAllFilesInFolder(File folder) throws IOException {

		// File directorio = new File(folder1 + FolderOCR );
		if (!directorio.exists()) {
			if (directorio.mkdirs()) {
				System.out.println("Directorio creado");
			} else {
				System.out.println("Error al crear directorio");
			}
		}

		for (File file : folder.listFiles()) {
			if (!file.isDirectory()) {
				if (file.getName().contains(".pdf") || file.getName().contains(".PDF")) {
					FileFolder = file.getName().replaceAll(".PDF", ".pdf");
					System.out.println(FileFolder);

					try (FileWriter escribir = new FileWriter((directorio + pdfatiff), true)) {
						escribir.write(FileFolder);
						escribir.write("\n");
						// System.out.println(FileFolder);
					}
				}

			} else {
				findAllFilesInFolder(file);
			}
		}
	}

	public void convertPDFAsTiff(File folder) throws IOException {

		fr = new FileReader(directorio + pdfatiff);
		Scanner sc = new Scanner(fr);
		int count = 0;
		while (sc.hasNextLine()) {
			sc.nextLine();
			total = count++;
		}
		System.out.println("Total Number of Lines: " + total);

		// sc.close();

		try {
			// Apertura del fichero y creacion de BufferedReader para poder
			// hacer una lectura comoda (disponer del metodo readLine()).
			// archivo = new File ("C:\\archivo.txt");
			fr = new FileReader(directorio + pdfatiff);
			br = new BufferedReader(fr);
			System.out.println("Total Number of Lines: " + total);

			// Lectura del fichero
			for (int i = 0; i <= total; i++) {
				if ((linea = br.readLine()) != null) {
					linea1 = linea;

					// System.out.println(linea);
					System.out.println(folder1 + "\\" + linea1);

					// String pdfFileName = "C:\\Users\\htique\\Documents\\Seguros Bolivar\\Proceso
					// pagos de consulta externa\\Data\\FACTURAS DE
					// PRUEBA\\1156360\\Adjuntar_Factura_Asistencial1024889_FAC_1156360_1.pdf";

					// System.out.println(folder1+"\\"+linea1);
					// String pdfFileName = "C:\\Users\\htique\\Documents\\Seguros Bolivar\\Proceso
					// pagos de consulta externa\\Data\\FACTURAS DE
					// PRUEBA\\1840812\\Adjuntar_Factura_Asistencial1079849_FACTURA_1840812_merged.pdf";
					PDDocument document = PDDocument.loadNonSeq(new File(folder1 + "\\" + linea1), null);

					List<PDPage> pdPages = document.getDocumentCatalog().getAllPages();

					int page = 0;

					for (PDPage pdPage : pdPages) {
						++page;
						BufferedImage bim = pdPage.convertToImage(BufferedImage.TYPE_INT_RGB, 300);
						ImageIOUtil.writeImage(bim,
								directorio + "\\" + linea1.replaceAll(".pdf", "") + "-" + page + ".TIFF", 300);

					}

					
				}
				
			}
			 
			BufferedWriter bw = new BufferedWriter(new FileWriter(directorio + pdfatiff));
			bw.write("");
			bw.close();
			
			for (File file : folder.listFiles()) {
				if (!file.isDirectory()) {
					if (file.getName().contains(".TIFF")) {
						FileFolder = file.getName();
						System.out.println(FileFolder);
						
						try (FileWriter escribir = new FileWriter((directorio + pdfatiff), true)) {
							escribir.write(FileFolder);
							escribir.write("\n");
							// System.out.println(FileFolder);
						}

					}
				} else {
					findAllFilesInFolder(file);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void Tesseract() throws IOException {

		Tesseract tesseract = new Tesseract();

		fr = new FileReader(directorio + pdfatiff);
		Scanner sc = new Scanner(fr);
		int count = 1;
		while (sc.hasNextLine()) {
			sc.nextLine();
			total = count++;
		}
		System.out.println("Total Number of Lineas archivo PDF a TIFF: " + total);
		try {

			// Apertura del fichero y creacion de BufferedReader para poder
			// hacer una lectura comoda (disponer del metodo readLine()).
			// archivo = new File ("C:\\archivo.txt");
			fr = new FileReader(directorio + pdfatiff);
			br = new BufferedReader(fr);

			// Lectura del fichero
			for (int i = 0; i <= total; i++) {
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
					text = tesseract.doOCR(new File(directorio + "\\" + linea1));
					String text1 = text.replaceAll(" ", "");
					// path of your image file
					System.out.print(text1);

					fr1 = new FileReader(directorio1);
					Scanner sc1 = new Scanner(fr1);
					int count1 = 1;
					while (sc1.hasNextLine()) {
						sc1.nextLine();
						total1 = count1++;
					}
					System.out.println("Total Number of Lineas archivo infosisalud: " + total1);

					fr1 = new FileReader(directorio1);
					br1 = new BufferedReader(fr1);

					List<String> prueba = new ArrayList<>();
					prueba.add("1");
					prueba.add("1");
					prueba.add("3");
					prueba.add("4");
					prueba.add("5");
					prueba.add("6");
					prueba.add("7");
					prueba.add("8");
					prueba.add("0");

					List<String> prueba1 = new ArrayList<>();
					prueba1.add("I");
					prueba1.add("L");
					prueba1.add("E");
					prueba1.add("A");
					prueba1.add("S");
					prueba1.add("G");
					prueba1.add("T");
					prueba1.add("B");
					prueba1.add("O");

					/*
					 * HashMap<String, String> map = new HashMap<>(); map.put("1", "I");
					 * map.put("3", "E"); map.put("4", "A"); map.put("5", "S"); map.put("6", "G");
					 * map.put("7", "T"); map.put("8", "B"); map.put("0", "O");
					 */
					for (int j = 0; j <= total1; j++) {

						if ((linea2 = br1.readLine()) != null) {
							linea3 = linea2;
							System.out.println("linea: " + linea3);

							if (linea3.matches("^[0-9]*$")) {
								if (text1.contains(linea3.replaceAll(" ", ""))) {
									try (FileWriter escribir = new FileWriter((directorio + resultados), true)) {
										escribir.write(linea3);
										escribir.write("\n");
										// System.out.println(FileFolder);
									}
									System.out.println(true);
								} else {
									System.out.println("No se encontro el número de Autorización");
								}
							}

							else if (linea3.matches("(.+)[a-zA-ZÀ-ÿ]")) {
								if (text1.toLowerCase().contains(linea3.replaceAll(" ", "").toLowerCase())) {
									try (FileWriter escribir = new FileWriter((directorio + resultados), true)) {
										escribir.write(linea3);
										escribir.write("\n");
										// System.out.println(FileFolder);

									}
									System.out.println(true);
								} else {
									System.out.println("No se encontro el número de Autorización");
								}
							} else {

								for (int k = 0; k < prueba.size(); k++) {
									// System.out.println("prueba " + linea3.replaceAll(" ",
									// "").replaceFirst(prueba.get(k), prueba1.get(k)));
									// System.out.println("prueba1 " + linea3.replaceAll(" ",
									// "").replaceFirst(prueba1.get(k), prueba.get(k)));
									if (text1.toLowerCase().contains(
											linea3.replaceAll(" ", "").replaceFirst(prueba.get(k), prueba1.get(k).toLowerCase()))
											|| text1.contains(linea3.replaceAll(" ", "").replaceFirst(prueba1.get(k),
													prueba.get(k)))) {
										try (FileWriter escribir = new FileWriter((directorio + resultados), true)) {
											escribir.write(linea3);
											escribir.write("\n");
											// System.out.println(FileFolder);

										}
										System.out.println(true);
									} else {
										System.out.println("No se encontro el número de Autorización");
									}
								}
							}

/*							String fileName = (directorio + resultados);
					        Set<String> list = new HashSet<String>();
					        Path path = Paths.get(fileName);
					        
					        try (Stream<String> lines = Files.lines(path)) {
					            list = lines
					                    .collect(Collectors.toSet());
					            
					            
					        } catch (IOException ex) {
					          //capturar la excepción
					        }
					        
					        //imprimir en consola
					        list.forEach(e -> System.out.println(e));
					        
					        //Reemplazar archivo
					        BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
					        Iterator<String> it = list.iterator(); 
					        while(it.hasNext()) {
					            out.write(it.next());
					            out.newLine();
					        }
					        out.close();
*/
				        }
					}
				}
			}
		} catch (TesseractException e) {
			e.printStackTrace();
		}
	}

	public void clearFolder() throws IOException {

		FileUtils.deleteDirectory(directorio);
	}

	public static void main(String[] args) throws IOException {

		TestPDF3 prueba = new TestPDF3();
		 prueba.findAllFilesInFolder(folder1);
		/*
		 * File folder = new File
		 * ("C:\\Users\\htique\\Documents\\Seguros Bolivar\\Proceso pagos de consulta externa\\Data\\FACTURAS DE PRUEBA\\MET10206"
		 * ); findAllFilesInFolder(folder);
		 */
		// prueba.convertPDFAsTiff(directorio);
		// prueba.Tesseract();
		// prueba.clearFolder();

	}
}