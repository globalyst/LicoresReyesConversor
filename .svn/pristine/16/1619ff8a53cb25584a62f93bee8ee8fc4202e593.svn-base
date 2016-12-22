package licoresreyes.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class OpenBravoObject extends LRObject {

	private static final String TAG = "  OB --> ";
	private static final int READ_MODE_NORMAL = 0;
	private static final int READ_MODE_HEAD = 1;
	private static final int READ_MODE_WAITING = 2;
	private static final int READ_MODE_FIELD = 3;
	//private static final String ESPECIAL_CHARS = "·‡‰ÈËÎÌÏÔÛÚˆ˙˘uÒ¡¿ƒ…»ÀÕÃœ”“÷⁄Ÿ‹—Á«";
	
	private XmlTable xmlTable;
	public OpenBravoObject(String filename, XmlTable xmlTable) {
		this.filename = filename;
		this.xmlTable  = xmlTable;
		init();
	}
	private void init() {
		System.out.println("\n" + TAG + "Iniciando OpenBravoObject desde la tabla " +  filename );
		int readMode = READ_MODE_NORMAL;
		boolean hasHeader = false;
		
		try {

			List<String> sqlData = LoadFile();
			StringBuilder tempWord = new StringBuilder("");
	
			List<String> headerList = new ArrayList<String>();
			List<List<String>> data  = new ArrayList<List<String>>();;
			
			List<String> tempData  = new ArrayList<String>();

			boolean isReadingValues = false;
			boolean isReadingText = false;
		
			int percent = 0;
			System.out.println("\n" + TAG + "Interpretando fichero: " +  filename );
			System.out.print(TAG + "Cargando ");
			for (int j = 0; j < sqlData.size(); j++) {
				for (int i = 0; i < sqlData.get(j).length(); i++) {
					char c = sqlData.get(j).charAt(i);

					switch (readMode) {
					case READ_MODE_NORMAL:
						//Buscar un (
						if (c == '(') {
							readMode = READ_MODE_HEAD;
						}
						break;
					case READ_MODE_HEAD:
							//Buscar una , y guardar la palabra
							if (c == ')' ) {
								readMode = READ_MODE_WAITING;
	
								if (!hasHeader) {
									hasHeader = true;
									headerList.add(tempWord.toString());
									tempWord.delete(0, tempWord.length());
								}
								
							} else if (!hasHeader) {
								if (c == ',') {
									//siguiente palabra
									headerList.add(tempWord.toString());
									tempWord.delete(0, tempWord.length());
								
								}	else {
									//guardar char en palabra
									tempWord.append(c);
								}	
							}
						break;
					case READ_MODE_WAITING:
						if (c == '(' ) {
							readMode = READ_MODE_FIELD;
							tempData = new ArrayList<String>();
						}
						//Buscar un (
						break;
					case READ_MODE_FIELD:
						//Buscar una , y guardar la palabra
						// Si el caracter es una apertura de parentesis, significa que el parÈntesis forma parte del texto
						if (c == '('  && !isReadingText ) {
							isReadingValues = true;
							tempWord.append(c);
						} else if (c == ';' ) {
							tempWord.append(',');
						} else if (c == '\n' ) {
							if ( !isReadingText) 
								tempWord.append(c);
						} else  if (c == '\'' ) {
							isReadingText = !isReadingText;
						} else  if (c == ')'  && !isReadingText ) {
							if (isReadingValues) {
								tempWord.append(c);
								isReadingValues = false;
							} else {

								tempData.add(tempWord.toString());
								tempWord.delete(0, tempWord.length());
								readMode = READ_MODE_NORMAL;
								data.add(tempData);

							}
						//
						} else if (c == ',' && !isReadingValues && !isReadingText ) {
							tempData.add(tempWord.toString());
							tempWord.delete(0, tempWord.length());
						} else {
							//guardar char en palabra
							tempWord.append(c);
						}
						break;
					}
				} 
				
				if (   (j * 100 /sqlData.size() ) > percent  ) {
					percent = j * 100 / sqlData.size();
					if (percent % 10 == 0)
						System.out.print( "(" + percent + "%)");
				}
			}
			this.headerList = headerList;
			this.data = data;
			System.out.println("\n" + TAG + "OK. COLUMNAS: " + headerList.size() +  " ; FILAS: " + data.size());
			
		} catch(Exception e) {
			String error  = TAG +  "Se ha producido un error al crear el objeto OB" + filename +  " : " + e.getMessage();
			System.out.println(error);
		}
	 }
	
	private final int LOG_SPEED = 250000;
	private List<String> LoadFile () {
		System.out.println(TAG + "Cargando fichero");
		StringBuilder builder = new StringBuilder("");  
		List<String> result = new ArrayList<String>();
		FileInputStream inputStream = null;
		Scanner sc = null;
		String filePath = pathInput + filename + ".sql";
		try {
		    inputStream = new FileInputStream(filePath);
		    sc = new Scanner(inputStream, "UTF-8");
		    
		    //System.out.println(TAG +  "Leyendo  "  + filename + ".sql" );
		    long index = 0;
		    long  begin = System.currentTimeMillis();
		    while (sc.hasNextLine()) {
		        String line = sc.nextLine();
		    	builder.append(line).append("\n");
		        //result += line + "\n";
		        if (index % LOG_SPEED == 0 && index != 0) { 
		            long  now = System.currentTimeMillis();
		            float seconds = (now - begin) / 1000.0f;
		            int vel = (int) (index / seconds) ;
		            System.out.println(TAG + seconds + " segundos para leer " + index + " lineas --> " + vel + " lineas / segundo. ");
		            
		            String woo =  builder.toString();
		    		result.add(woo);
		    		builder = new StringBuilder(""); 

		        }
		        index++;
		    }
		    result.add(builder.toString());
		 //   System.out.println(TAG + filename + ".sql" + "  leido correctamente");
		    if (sc.ioException() != null) {
		        throw sc.ioException();
		    }
		} catch (FileNotFoundException e) {
			String error  = TAG +  "No se puede encontrar el archivo " + filePath +  " --> " + e.getMessage();
			System.out.println(error);
			e.printStackTrace();
		} catch (IOException e) {
			String error  = TAG +  "Error al leer el archivo " + filePath +  " --> " + e.getMessage();
			System.out.println(error);
			e.printStackTrace();
		} finally {
		    if (inputStream != null) {
		        try {
					inputStream.close();
				} catch (IOException e) {
					String error  = TAG +  "Error cerrando inputStream, despues de leer el fichero " + filePath +  " --> " + e.getMessage();
					System.out.println(error);
					e.printStackTrace();
				}
		    }
		    if (sc != null) {
		        sc.close();
		    }
		}
		System.out.println(TAG + "OK");
		return result;
	}


	public XmlTable getXmlTable() {
		return xmlTable;
	}
 
	public void fuse(OpenBravoObject toFuse) {
		System.out.println("\n" + TAG + "fuse() " + filename + " con " + toFuse.filename);
		String rel = toFuse.getXmlTable().getRelation();

		
		int columnIndex = getColumnIndex(rel);
		int fuseColumnIndex = toFuse.getColumnIndex(rel);
		
		for (int i = 0; i < toFuse.getColumnCount(); i++) {
			String column = toFuse.headerList.get(i);
			if (!hasColumn(column)) {
				addColumn(column);
			}
		}
		
		if (columnIndex < 0) {
			System.out.println(TAG + "ERROR columnIndex No se puede encontrar en la tabla " + filename + " la columna " + rel);
		}
		if (fuseColumnIndex < 0) {
			System.out.println(TAG + "ERROR fuseColumnIndex No se puede encontrar en la tabla " + toFuse.filename + " la columna " + rel);
		}
		
		if (fuseColumnIndex >= 0) {
			int percent = 0;
			System.out.print(TAG +  "Cargando");
			for (int i = 0; i < data.size(); i++) {
				String idToFind = data.get(i).get(columnIndex);
				
				
				List<Integer> fuseRowsIndex  = toFuse.find(fuseColumnIndex,idToFind);
				for (Iterator<Integer> iterator = fuseRowsIndex.iterator(); iterator.hasNext();) {
				    int fuseRowIndex = iterator.next();
				    if (fuseRowIndex >= 0) {
				    	for (int j = 0 ;j < toFuse.getColumnCount(); j++) {
							 int columnIndexToWrite = getColumnIndex(toFuse.headerList.get(j));
							 String _from = toFuse.data.get(fuseRowIndex).get(j);
							 String _to = data.get(i).get(columnIndexToWrite);
							 if (_to.isEmpty())
								 setData(columnIndexToWrite, i, _from);
						}
				    }
				    
				}
				
				if (  (i * 100 / data.size() ) > percent  ) {
					percent = i * 100 / data.size();
					//System.out.print(".");
					if (percent % 10 == 0)
						System.out.print( "(" + percent + "%)");
				}
				

			}
			this.getXmlTable().getFields().addAll(toFuse.getXmlTable().getFields());
		}
	}
	
}
