package licoresreyes.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import licoresreyes.model.OdooObject;
import licoresreyes.model.OpenBravoObject;
import licoresreyes.model.XmlFilter;
import licoresreyes.model.XmlLoader;
import licoresreyes.model.XmlObject;
import licoresreyes.model.XmlTable;
import licoresreyes.view.PanelView;


public class LRController {
	private static final String TAG = "CTR --> ";
	private XmlLoader xmlLoader = null;
	String configFileName = "config";
	
	public LRController() {

	}
	
	public XmlLoader getXmlLoader(){
		return xmlLoader;
	}
	
	
	public boolean loadXmlConfig(String filename) {
		System.out.println("\n" + TAG + " loadXmlConfig() init file " + filename);
		xmlLoader = new XmlLoader();
    	boolean result = xmlLoader.loadObjects(filename);
    	configFileName = filename;
    	if (result) {
    		System.out.println("\n" + TAG + " loadXmlConfig() OK" );
    	}
    	return result;
	}
	
	public OdooObject smartLoadODObject(XmlObject xmlObject, XmlObject aux) {
		System.out.println("\n" + TAG + " smartLoadODObject() INIT: " + xmlObject.getId());
		XmlTable xmlTable = xmlObject.getMainTable();
		String filename = xmlTable.getId();
		
		OpenBravoObject mainOB = new OpenBravoObject(filename,xmlTable);

		List<OpenBravoObject> relOB = new ArrayList<OpenBravoObject>();
	
	
		for(int j = 0; j < xmlObject.getReferenceTableList().size(); j++) {
			xmlTable = xmlObject.getReferenceTableList().get(j);
			filename = xmlTable.getId();
			OpenBravoObject OB = new OpenBravoObject(filename,xmlTable);

			relOB.add(OB);
		}
		
		//FUSIONAR OBJETOS OB
		System.out.println("\n" + TAG + " smartLoadODObject() fusionando OB");
		relOB = fuseOBObjects(relOB);
		
		if (isHasExportOb()) {
			mainOB.SaveFile("");
			for (int i = 0; i < relOB.size(); i++) {
				relOB.get(i).SaveFile("");
			}
		}
		System.out.println("\n\n" + TAG + " smartLoadODObject() cargando OD");
		OdooObject	OD = new OdooObject(xmlObject, xmlObject.getId(), mainOB, relOB );
	

		
		smartUpdateDates(OD);
		smartApplyFilterList(OD,aux);
		smartApplyFilters(OD);
		smartaAddPrefix(OD);
		System.out.println("\n" + TAG + " smartLoadODObject() END");
		return OD;
	}
	
	public OdooObject smartUpdateDates(OdooObject OD) {
		if (isFilterDate()) {
			System.out.println("\n" + TAG + " smartLoadODObject() filtrando fechas");
			Calendar updateDateFrom = Calendar.getInstance();
			updateDateFrom.set(Calendar.YEAR, yearFrom);
			updateDateFrom.set(Calendar.MONTH,monthFrom -1);
			updateDateFrom.set(Calendar.DAY_OF_MONTH, dayFrom);
			
			Calendar updateDateTo= Calendar.getInstance();
			updateDateTo.set(Calendar.YEAR, yearTo);
			updateDateTo.set(Calendar.MONTH,monthTo -1);
			updateDateTo.set(Calendar.DAY_OF_MONTH, dayTo);
			

			OD.onlyUpdated(updateDateFrom,updateDateTo) ;
		}
		return OD;
	}
	
	public OdooObject smartApplyFilters(OdooObject OD) {
		System.out.println("\n" + TAG + "smartLoadODObject() aplicando filtros");
		for(int i = 0; i < OD.xmlObject.getFilterList().size(); i++) {
			OD.ApplyFilter(OD.xmlObject.getFilterList().get(i));
		}

		return OD;
	}
	
	public OdooObject smartaAddPrefix(OdooObject OD) {
		System.out.println("\n" + TAG + "smartLoadODObject() A�adiendo prefijos");
		OD.addPrefix(OD.xmlObject);
		return OD;
	}
	public OdooObject smartApplyFilterList(OdooObject OD, XmlObject aux) {
		if (aux != null) {
			System.out.println("\n" + TAG + "smartLoadODObject() aplicando filtro de Lista");
			String[] accountFilter = OD.xmlObject.getAccountFilter().split("/");
			String columnNameFilter = accountFilter[0];
			String columnNameCurrent = accountFilter[1];
			
			System.out.println(TAG + "columnNameFilter: " + columnNameFilter  + " ; columnNameCurrent: " + columnNameCurrent + " ; accountFilter:" + OD.xmlObject.getAccountFilter());
			OdooObject	auxOD = smartLoadODObject(aux,null);
			int index = auxOD.getColumnIndex(columnNameFilter);
			
			if (index != -1 ) {
				List<String> filterList = new ArrayList<String>();
				for (int i = 0; i < auxOD.data.size(); i++) {
					String value = auxOD.data.get(i).get(index);
					//if (i < 30)System.out.println(TAG + " value : " + value + ";  List(count) : " + filterList.contains(value));
					if (!value.isEmpty() && !filterList.contains(value)) {
						filterList.add(value);
					}
				}
				OD.ApplyFilterList(filterList,columnNameCurrent);
			}
		}
		return OD;
	}
	
	public OdooObject loadODObject(XmlObject xmlObject) {
		System.out.println("\n" + TAG + " loadODObject()");
		XmlTable xmlTable = xmlObject.getMainTable();
		String filename = xmlTable.getId();
		
		OpenBravoObject mainOB = new OpenBravoObject(filename,xmlTable);

		List<OpenBravoObject> relOB = new ArrayList<OpenBravoObject>();
	
	
		for(int j = 0; j < xmlObject.getReferenceTableList().size(); j++) {
			xmlTable = xmlObject.getReferenceTableList().get(j);
			filename = xmlTable.getId();
			OpenBravoObject OB = new OpenBravoObject(filename,xmlTable);

			relOB.add(OB);
		}
		
		//FUSIONAR OBJETOS OB
		
		relOB = fuseOBObjects(relOB);
		
		if (isHasExportOb()) {
			mainOB.SaveFile("");
			for (int i = 0; i < relOB.size(); i++) {
				relOB.get(i).SaveFile("");
			}
		}
		
		
		System.out.println("\n" + TAG + " loadODObject() cargando OD");
		OdooObject	OD = new OdooObject(xmlObject, xmlObject.getId(), mainOB, relOB );
		
		System.out.println("\n" + TAG + " loadODObject() aplicando filtros :" + xmlObject.getFilterList().size());
		for(int i = 0; i < xmlObject.getFilterList().size(); i++) {
			OD.ApplyFilter(xmlObject.getFilterList().get(i));
		}
		
		if (isFilterDate()) {
			System.out.println("\n" + TAG + " loadODObject() filtrando fechas");
			Calendar updateDateFrom = Calendar.getInstance();
			updateDateFrom.set(Calendar.YEAR, yearFrom);
			updateDateFrom.set(Calendar.MONTH,monthFrom -1);
			updateDateFrom.set(Calendar.DAY_OF_MONTH, dayFrom);
			
			Calendar updateDateTo= Calendar.getInstance();
			updateDateFrom.set(Calendar.YEAR, yearTo);
			updateDateFrom.set(Calendar.MONTH,monthTo -1);
			updateDateFrom.set(Calendar.DAY_OF_MONTH, dayTo);
			
			
			OD.onlyUpdated(updateDateFrom,updateDateTo) ;
		}
		System.out.println("\n" + TAG + " loadODObject() a�adiendo prefijos");
		OD.addPrefix(xmlObject);
		
		OD.SaveFile("");
		
		return OD;
	}
	
	

	public List<OpenBravoObject> fuseOBObjects(List<OpenBravoObject> relOB) {
		List<OpenBravoObject> result = relOB;
		List<OpenBravoObject> toRemove = new ArrayList<OpenBravoObject>();
		
		boolean isFuse = false;
		for(int i = 0; i < relOB.size(); i++) {
			String fuse = relOB.get(i).getXmlTable().getFuse();
			if (!fuse.isEmpty() ) {
				OpenBravoObject toFuse = null;
				for (int j = 0; j < relOB.size() ; j++ ) {
					if (relOB.get(j).getXmlTable().getId().equals(fuse)) {
						toFuse = relOB.get(j);
						toRemove.add(toFuse);
						isFuse = true;
						break;
					}
				}
				if (toFuse != null) {
					relOB.get(i).fuse(toFuse);
				}
			}
		}
	
		if (isFuse) {
			for (Iterator<OpenBravoObject> iterator = relOB.iterator(); iterator.hasNext();) {
				OpenBravoObject row = iterator.next();
			 
				for(int i = 0; i < toRemove.size(); i++) {
					if (row.filename == toRemove.get(i).filename) {
					    iterator.remove();
				    }
				}
			}
		}
		return result;
		
	}

   private static boolean hasFilterDate = false;
	
	public static boolean isFilterDate() {
		return hasFilterDate;
	}

	public static void setFilterDate(boolean filterdate) {
		LRController.hasFilterDate = filterdate;
	}
	
	private static boolean hasExportOb = false;
	
	public static boolean isHasExportOb() {
		return hasExportOb;
	}

	public static void setHasExportOb(boolean hasExportOb) {
		LRController.hasExportOb = hasExportOb;
	}

	public static int yearFrom = 2000;
	public static int monthFrom = 12;
	public static int dayFrom = 31;
	
	public static int yearTo = 2000;
	public static int monthTo = 12;
	public static int dayTo = 31;
	
	public static void setDate(int _dayFrom, int _monthFrom, int _yearFrom, int _dayTo, int _monthTo, int _yearTo ) {
		LRController.dayFrom = _dayFrom;
		LRController.monthFrom = _monthFrom;
		LRController.yearFrom = _yearFrom;
		
		LRController.dayTo = _dayTo;
		LRController.monthTo = _monthTo;
		LRController.yearTo = _yearTo;
		System.out.println(TAG + "setDate() From : "  + LRController.dayFrom + "/" + LRController.monthFrom + "/" + LRController.yearFrom +
				 			"; To :" + + LRController.dayTo + "/" + LRController.monthTo + "/" + LRController.yearTo );
	}
	public void loadData(PanelView panelView) {
		
    	for (int i = 0; i < xmlLoader.getObjectCount(); i++) {
    		if (panelView.isSelected(xmlLoader.getObjectList().get(i).getId())) {
        		XmlObject xmlObject = xmlLoader.getObjectList().get(i);
        		loadODObject(xmlObject);
    		}

    	}
	}
	
	public OdooObject loadOD(PanelView panelView) {
		
		OdooObject ODresult = null;
    	for (int i = 0; i < xmlLoader.getObjectCount(); i++) {
    		if (panelView.isSelected(xmlLoader.getObjectList().get(i).getId())) {
        		XmlObject xmlObject = xmlLoader.getObjectList().get(i);
        		//xmlObject.print();
        		/*
        		ODresult = smartLoadODObject(xmlObject);
        		break;
        		*/
        		//APPLY FILTER ACCOUNT
        		
        		if (xmlObject.getAccountFilter().isEmpty()) {
        			ODresult = smartLoadODObject(xmlObject,null);
        		} else {
        			ODresult = smartLoadODObject(xmlObject, xmlLoader.getObjectByName(xmlObject.getAux()));
        		}
        		break;
        	
    		}

    	}
    	return ODresult;
	}
	
	private final String FICHERO_PRODUCTPRICE = "9.5 - product.pricelist.version.rules.Total";
	private final String FICHERO_TARIFA_GENERAL = "9.4 - product.pricelist.version.rules.TarifaGeneral";
	private final String FICHERO_TARIFAS = "9 - product.pricelist";
	private final String FICHERO_DESCUENTOS = "9.21 - product.pricelist.version.rules.ventas";
	
	private final String COLUMNA_PRODUCTPRICE_TARIFAS = "price_list_id/id";
	private final String COLUMNA_PRODUCTPRICE_PRODUCTOS = "id";
	private final String COLUMNA_PRODUCTPRICE_PRECIO = "list_price";
	
	private final String COLUMNA_TARIFA_GENERAL_ID = "id";
	private final String COLUMNA_TARIFA_GENERAL_PRECIO = "list_price";
	
	private final String COLUMNA_TARIFAS_ID = "id";	
	private final String COLUMNA_TARIFAS_NOMBRE = "name";
	
	private final String COLUMNA_DESCUENTO = "price_discount";
	
	private final String COLUMNA_BASADO_EN = "base";
	private final String VALOR_BASADO_EN_PRECIO_PUBLICO = "1";
	private final String VALOR_BASADO_EN_PRECIO_COSTE = "2";
	
	
	
	public void loadSalePriceList(PanelView panelView) {
		
		/* ARCHIVO CON TODOS LOS PRECIOS DE TODAS LAS VERSIONES DE TARIFA */
		OdooObject ODlistaPrecios = null;
    	for (int i = 0; i < xmlLoader.getObjectCount(); i++) {
    		XmlObject xmlObject = xmlLoader.getObjectList().get(i);
    		if (xmlObject.getId().equals(FICHERO_PRODUCTPRICE)) {
    			ODlistaPrecios =loadODObject(xmlObject);
    		}
    	}
    	
		int ODlistaPrecios_columnIndexTarifaId = ODlistaPrecios.getColumnIndex(COLUMNA_PRODUCTPRICE_TARIFAS);
	    int ODlistaPrecios_columnIndexProduct = ODlistaPrecios.getColumnIndex(COLUMNA_PRODUCTPRICE_PRODUCTOS);
	    int ODlistaPrecios_columnIndexPrice = ODlistaPrecios.getColumnIndex(COLUMNA_PRODUCTPRICE_PRECIO);
	    
		/* ARCHIVO CON LOS PRECIOS FINALES DE LOS PRODUCTOS PARA LA TARIFA GENERAL,
		 *  DEBEMOS PARTIRLO EN TROZOS, SEPARARLO POR TARIFAS, FILTRAR POR FECHA, Y RECOMPONER */
	    
		OdooObject ODTarifaGeneral = null;
    	for (int i = 0; i < xmlLoader.getObjectCount(); i++) {
    		XmlObject xmlObject = xmlLoader.getObjectList().get(i);
    		if (xmlObject.getId().equals(FICHERO_TARIFA_GENERAL)) {
    			ODTarifaGeneral =loadODObject(xmlObject);
    		}
    	}
    	

		int ODTarifaGeneral_columnIndexProduct = ODTarifaGeneral.getColumnIndex(COLUMNA_TARIFA_GENERAL_ID);
		int ODTarifaGeneral_columnIndexPrice = ODTarifaGeneral.getColumnIndex(COLUMNA_TARIFA_GENERAL_PRECIO);
		
		ODTarifaGeneral.SaveFile("");
    	/* ARCHIVO CON TODAS LAS TARIFAS, QUE USAREMOS PARA FRACTURAR EL FICHERO DE PRECIOS EN TROCITOS */
		OdooObject ODTarifas = null;
    	for (int i = 0; i < xmlLoader.getObjectCount(); i++) {
    		XmlObject xmlObject = xmlLoader.getObjectList().get(i);
    		if (xmlObject.getId().equals(FICHERO_TARIFAS)) {
    			ODTarifas =loadODObject(xmlObject);
        		
    		}
    	}
    	
    	XmlFilter  xmlFilter = new XmlFilter("BYNAME","name","TC","type","purchase","sale");
    	ODTarifas.ApplyFilter(xmlFilter);
    	
    	  xmlFilter = new XmlFilter("FILTER","type","sale","","","");
      	ODTarifas.ApplyFilter(xmlFilter);
      	
		
    	int ODTarifas_columnIndexId = ODTarifas.getColumnIndex(COLUMNA_TARIFAS_ID);
    	int ODTarifas_columnIndexName = ODTarifas.getColumnIndex(COLUMNA_TARIFAS_NOMBRE);
    	
    	
    	
    	
    	/* LISTA CON LOS TROCITOS DE FICHERO */
    	List<OdooObject> listODs = new ArrayList<OdooObject>();
    	
    	/* RECORREMOS LAS TARIFAS */
    	 for (int i = 0; i < ODTarifas.getRowCount(); i++) {
    		 
    		 /* ID Y NOMBRE DE TARIFA, QUE USAREMOS PARA COMPARAR Y PARA NOMBRAR*/
    		String idTarifa = ODTarifas.getData(i, ODTarifas_columnIndexId);
    		String nameTarifa = ODTarifas.getData(i, ODTarifas_columnIndexName);
    		
    		 /* TROCITO DE FICHERO */
    		OdooObject tarifaDeTal = new OdooObject("0 - " +  nameTarifa);
    		System.out.println("Cargando tarifa " + "0 - " +  nameTarifa);
    		
    		/* A�ADIMOS CABECERAS A TARIFA DE TAL */
    		for (int j = 0; j < ODlistaPrecios.getColumnCount(); j++) { 
    			 tarifaDeTal.addColumn(ODlistaPrecios.headerList.get(j));
    		}
    		 
    		 /* RECORREMOS LA LISTA DE PRECIOS, BUSCANDO LAS COINCIDENCIAS DE ID CON LA TARIFA */
    		 for (int j = 0; j < ODlistaPrecios.getRowCount(); j++) {
    			 String idToCompare =  ODlistaPrecios.getData(j, ODlistaPrecios_columnIndexTarifaId);
    			//System.out.println(idTarifa + " == " +  idToCompare);
    			 /* SI HAY COINCIDENCIA, COGEMOS LA FILA DE LA LISTA DE PRECIOS, Y LA A�ADIMOS AL TROCITO*/
    			 if (idTarifa.equals(idToCompare)) {
    				 List<String> row = ODlistaPrecios.data.get(j);
    				 tarifaDeTal.data.add(row);
    			 }
    		 }
    		
    		/* ESTE FILTRO DEBER�A  HACER QUE S�LO NOS QUEDEMOS CON EL �LTIMO PRECIO*/
    		 xmlFilter = new XmlFilter("ADVANCED","id","HIGH","valid_from","","");
    		tarifaDeTal.ApplyFilter(xmlFilter);
    		
    		 /* UNA VEZ FILTRADO, VAMOS A COMPARAR EL PRECIO CON LA TARIFA GENERAL,
    		  * PRIMERO VAMOS A NECESITAR A�ADIR UNA NUEVA COLUMNA PARA EL VALOR DE DESCUENTO */
    		tarifaDeTal.addColumn(COLUMNA_DESCUENTO);
    		int columnDiscountIndex = tarifaDeTal.getColumnIndex(COLUMNA_DESCUENTO);
    		
    		/* AHORA RECORREMOS LAS FILAS DEL TROCITO, PARA COMPARAR EL PRECIO CON LA TARIFA GENERAL*/
    		for (int j = 0; j < tarifaDeTal.getRowCount(); j++) {
    			 /* NECESITAMOS EL ID DEL PRODUCTO QUE DEBEMOS BUSCAR, Y EL PRECIO QUE TIENE */
				 String product = tarifaDeTal.getData(j, ODlistaPrecios_columnIndexProduct);
    			 String price  = tarifaDeTal.getData(j, ODlistaPrecios_columnIndexPrice);
    			 
				 /* VALUE LO USAREMOS PARA ALMACENAR LA DIFERENCIA PORCENTUAL DE PRECIOS */
    			 float value = 0.0f;
    			 
    			 /* BUSCAMOS ESTE PRODUCTO EN LA TARIFA GENERAL, ASI QUE LA RECORREMOS */
    			 for (int z = 0; z < ODTarifaGeneral.getRowCount(); z++) {
    				 /* NECESITAMOS EL ID DEL PRODUCTO A COMPARAR, Y EL PRECIO QUE TIENE */
    				 String productToCompare =  ODTarifaGeneral.getData(z, ODTarifaGeneral_columnIndexProduct);
    				 String priceTarifaGeneral = ODTarifaGeneral.getData(z, ODTarifaGeneral_columnIndexPrice);

    				 
        			 /*CUANDO ENCONTREMOS LA COINCIDENCIA, CALCULAREMOS LA DIFERENCIA DE PRECIOS CON DICHO MATCH */
    				 if (product.equals(productToCompare)) {
    					 float Tg = Float.parseFloat(priceTarifaGeneral);
    					 float Tx = Float.parseFloat(price);

    					 
    					 if (Tx != 0.0f && Tg != 0.0f) {
    						// floatValue = (Tg - Tx) * 100.0f /  Tx;
    						 value = (Tx /  Tg) - 1 ;
        					 if (value > 100.0f || value < -100.0f) {
        						 System.out.println("ATENCI�N  Tg: " + Tg + " ; Tx : " + Tx + " --> TOTAL : " + value + " " + nameTarifa + " " + product + " " + productToCompare);
        						 
        					 }
    					 }

    				 }
    				 
    			 }
    			 
    			 if (value != 0.0f) {
    				 tarifaDeTal.setData(columnDiscountIndex, j, String.valueOf(value));
    			 }
    			 
    		 }
    		 /*A�ADIMOS EL TROCITO YA FILTRADO Y CON EL DESCUENTO CALCULADO */
    		
    		 //tarifaDeTal.SaveFile();
    		 listODs.add(tarifaDeTal);
    	}
    	 
    	 /*VAMOS A JUNTAR TODOS LOS TROCITOS EN ESTE FICHERO */
    	 OdooObject result = new OdooObject(FICHERO_DESCUENTOS);
    	 
    	 /*LE A�ADIMOS LAS COLUMNAS QUE TIENE UN TROCITO CUALQUIERA, USAMOS EL PRIMERO  */
    	 for (int i = 0; i < listODs.get(0).getColumnCount(); i++) {
    		 String header = listODs.get(0).headerList.get(i);
    		 //System.out.println("A�adiendo cabecera : " + header);
    		 result.addColumn(header);
    	 }
    	 
    	 /*RECORREMOS LOS TROCITOS  */
    	 for (int i = 0; i < listODs.size(); i++) {
    		 /*RECORREMOS LAS FILAS DE LOS TROCITOS  */
    		 for (int j = 0; j < listODs.get(i).getRowCount(); j++) {
				 List<String> row = listODs.get(i).data.get(j);
				 result.data.add(row);
    		 }
    	 }
    	 
    	 
    	 xmlFilter = new XmlFilter("FILTER_EX",COLUMNA_DESCUENTO,"","","","");
    	 result.ApplyFilter(xmlFilter);
    	 
    	 xmlFilter = new XmlFilter("FILTER_EX",COLUMNA_DESCUENTO,"0.0","","","");
    	 result.ApplyFilter(xmlFilter);
    	 
    	 xmlFilter = new XmlFilter("FILTER_EX",COLUMNA_DESCUENTO,"1.0","","","");
    	 result.ApplyFilter(xmlFilter);
 		
    	 result.addColumn(COLUMNA_BASADO_EN);
    	 int baseIndex = result.getColumnIndex(COLUMNA_BASADO_EN);
    	
    	 for (int i = 0; i < result.getRowCount(); i++) {
    		 result.setData(baseIndex, i, VALOR_BASADO_EN_PRECIO_PUBLICO);
    	 
    	 }
    	 
    	 /* GUARDAMOS EL FICHERO UNIFICADO*/
    	 result.SaveFile("");
	}
	
	private final String FICHERO_TARIFAS_COMPRA = "9.22 - product.pricelist.version.rules.compras";
	
	public void loadBuyPriceList(PanelView panelView) {
		
		/* ARCHIVO CON TODOS LOS PRECIOS DE TODAS LAS VERSIONES DE TARIFA */
		OdooObject ODlistaPrecios = null;
    	for (int i = 0; i < xmlLoader.getObjectCount(); i++) {
    		XmlObject xmlObject = xmlLoader.getObjectList().get(i);
    		if (xmlObject.getId().equals(FICHERO_PRODUCTPRICE)) {
    			ODlistaPrecios =loadODObject(xmlObject);
    		}
    	}
    	
		int ODlistaPrecios_columnIndexTarifaId = ODlistaPrecios.getColumnIndex(COLUMNA_PRODUCTPRICE_TARIFAS);
	    
		/* ARCHIVO CON LOS PRECIOS FINALES DE LOS PRODUCTOS PARA LA TARIFA GENERAL,
		 *  DEBEMOS PARTIRLO EN TROZOS, SEPARARLO POR TARIFAS, FILTRAR POR FECHA, Y RECOMPONER */
	    
		OdooObject ODTarifaGeneral = null;
    	for (int i = 0; i < xmlLoader.getObjectCount(); i++) {
    		XmlObject xmlObject = xmlLoader.getObjectList().get(i);
    		if (xmlObject.getId().equals(FICHERO_TARIFA_GENERAL)) {
    			ODTarifaGeneral =loadODObject(xmlObject);
    		}
    	}
		
		ODTarifaGeneral.SaveFile("");
    	/* ARCHIVO CON TODAS LAS TARIFAS, QUE USAREMOS PARA FRACTURAR EL FICHERO DE PRECIOS EN TROCITOS */
		OdooObject ODTarifas = null;
    	for (int i = 0; i < xmlLoader.getObjectCount(); i++) {
    		XmlObject xmlObject = xmlLoader.getObjectList().get(i);
    		if (xmlObject.getId().equals(FICHERO_TARIFAS)) {
    			ODTarifas =loadODObject(xmlObject);
        		
    		}
    	}
    	
    	XmlFilter  xmlFilter = new XmlFilter("BYNAME","name","TC","type","purchase","sale");
    	ODTarifas.ApplyFilter(xmlFilter);
    	
    	  xmlFilter = new XmlFilter("FILTER","type","purchase","","","");
      	ODTarifas.ApplyFilter(xmlFilter);
      	
		
    	int ODTarifas_columnIndexId = ODTarifas.getColumnIndex(COLUMNA_TARIFAS_ID);
    	int ODTarifas_columnIndexName = ODTarifas.getColumnIndex(COLUMNA_TARIFAS_NOMBRE);
    	
    	
    	
    	
    	/* LISTA CON LOS TROCITOS DE FICHERO */
    	List<OdooObject> listODs = new ArrayList<OdooObject>();
    	
    	/* RECORREMOS LAS TARIFAS */
    	 for (int i = 0; i < ODTarifas.getRowCount(); i++) {
    		 
    		 /* ID Y NOMBRE DE TARIFA, QUE USAREMOS PARA COMPARAR Y PARA NOMBRAR*/
    		String idTarifa = ODTarifas.getData(i, ODTarifas_columnIndexId);
    		String nameTarifa = ODTarifas.getData(i, ODTarifas_columnIndexName);
    		
    		 /* TROCITO DE FICHERO */
    		OdooObject tarifaDeTal = new OdooObject("0 - " +  nameTarifa);
  
    		/* A�ADIMOS CABECERAS A TARIFA DE TAL */
    		for (int j = 0; j < ODlistaPrecios.getColumnCount(); j++) { 
    			 tarifaDeTal.addColumn(ODlistaPrecios.headerList.get(j));
    		}
    		 
    		 /* RECORREMOS LA LISTA DE PRECIOS, BUSCANDO LAS COINCIDENCIAS DE ID CON LA TARIFA */
    		 for (int j = 0; j < ODlistaPrecios.getRowCount(); j++) {
    			 String idToCompare =  ODlistaPrecios.getData(j, ODlistaPrecios_columnIndexTarifaId);
    			//System.out.println(idTarifa + " == " +  idToCompare);
    			 /* SI HAY COINCIDENCIA, COGEMOS LA FILA DE LA LISTA DE PRECIOS, Y LA A�ADIMOS AL TROCITO*/
    			 if (idTarifa.equals(idToCompare)) {
    				 List<String> row = ODlistaPrecios.data.get(j);
    				 tarifaDeTal.data.add(row);
    			 }
    		 }
    		
    		/* ESTE FILTRO DEBER�A  HACER QUE S�LO NOS QUEDEMOS CON EL �LTIMO PRECIO*/
    		 xmlFilter = new XmlFilter("ADVANCED","id","HIGH","valid_from","","");
    		tarifaDeTal.ApplyFilter(xmlFilter);
    		
    		 //tarifaDeTal.SaveFile();
    		 listODs.add(tarifaDeTal);
    	}
    	 
    	 /*VAMOS A JUNTAR TODOS LOS TROCITOS EN ESTE FICHERO */
    	 OdooObject result = new OdooObject(FICHERO_TARIFAS_COMPRA);
    	 
    	 /*LE A�ADIMOS LAS COLUMNAS QUE TIENE UN TROCITO CUALQUIERA, USAMOS EL PRIMERO  */
    	 for (int i = 0; i < listODs.get(0).getColumnCount(); i++) {
    		 String header = listODs.get(0).headerList.get(i);
    		 //System.out.println("A�adiendo cabecera : " + header);
    		 result.addColumn(header);
    	 }
    	 
    	 /*RECORREMOS LOS TROCITOS  */
    	 for (int i = 0; i < listODs.size(); i++) {
    		 /*RECORREMOS LAS FILAS DE LOS TROCITOS  */
    		 for (int j = 0; j < listODs.get(i).getRowCount(); j++) {
				 List<String> row = listODs.get(i).data.get(j);
				 result.data.add(row);
    		 }
    	 }
    	 
 		
    	 result.addColumn(COLUMNA_BASADO_EN);
    	 int baseIndex = result.getColumnIndex(COLUMNA_BASADO_EN);
    	
    	 for (int i = 0; i < result.getRowCount(); i++) {
    		 result.setData(baseIndex, i, VALOR_BASADO_EN_PRECIO_COSTE);
    	 
    	 }
    	 
    	 result.addColumn(COLUMNA_DESCUENTO);
    	 int discountIndex = result.getColumnIndex(COLUMNA_DESCUENTO);
    	
    	 for (int i = 0; i < result.getRowCount(); i++) {
    		 result.setData(discountIndex, i, "-1.0");
    	 
    	 }
    	 
    	 /* GUARDAMOS EL FICHERO UNIFICADO*/
    	 result.SaveFile("");
	}

}
