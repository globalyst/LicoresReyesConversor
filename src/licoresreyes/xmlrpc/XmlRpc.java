package licoresreyes.xmlrpc;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcTransport;
import org.apache.xmlrpc.client.XmlRpcTransportFactory;

import licoresreyes.model.OdooObject;
import licoresreyes.model.XmlField;
import licoresreyes.model.XmlField.Type;

public class XmlRpc {
	private final String TAG = "  RPC --> ";
	
	
	final String SERVER_URL = "https://mig.licoresreyes.es"  ;
	final String SERVER_DB = "licoresreyes9_migracion";
	final String SERVER_USERNAME = "admin";
	final String SERVER_PASSWORD = "licoresreyes";
	//final String SERVER_USERNAME = "aparrac88@gmail.com";
	//final String SERVER_PASSWORD = "pothe28len";
	
	/*
	final String SERVER_URL = "http://reyes-pro.jaqueasesores.com:8069"  ;
	final String SERVER_DB = "TEST-XMLRPC";
	final String SERVER_USERNAME = "admin";
	final String SERVER_PASSWORD = "pothe28len";
	*/
	private final String RPC_COMMON_URL = "/xmlrpc/2/common";
	private final String RPC_OBJECT_URL = "/xmlrpc/2/object"; 
	public int  uid = -1;
	
	public XmlRpc() {
		uid = login();
	}
	
	public Integer login() {
		int uid  = -1;

		XmlRpcClient client = new XmlRpcClient();
		final XmlRpcTransportFactory transportFactory = new XmlRpcTransportFactory()
		{
		    public XmlRpcTransport getTransport()
		    {
		        return new MessageLoggingTransport(client);
		    }
		};
		final XmlRpcClientConfigImpl common_config = new XmlRpcClientConfigImpl();
		client.setTransportFactory(transportFactory);
		try {
		common_config.setServerURL(new URL(String.format("%s" + RPC_COMMON_URL, SERVER_URL)));
		} catch(MalformedURLException e) {
			System.out.println("ERROR CREATING URL--> " + 	e.getMessage());
			return  uid ;
		}
		Object[] params_version = new Object[] {};
		Object result = null;
		try {
			result = client.execute(common_config, "version", params_version);
		} catch(XmlRpcException e) {
			System.out.println(" " + e.linkedException.getMessage()) ;
			System.out.println("  ERROR GETTING SERVER VERSION --> " + 	e.getMessage());
			for (int i = 0; i < e.getStackTrace().length ; i++) {
				System.out.println(e.getStackTrace()[i].toString());
			}
			return  uid ;
		}
		System.out.println("SERVER INFO RESULT --> " + result);

		
		
		Map<Object, Object> emptyMap = new HashMap<Object, Object>() ; 
		Object[] params_auth = new Object[] {SERVER_DB, SERVER_USERNAME, SERVER_PASSWORD, emptyMap};
		
		try {
			uid = (int)client.execute(common_config, "authenticate", params_auth);
		} catch(XmlRpcException e) {
			System.out.println("ERROR GETTING SERVER AUTHENTICATION --> " + e.getMessage());
		}
		
		System.out.println("AUTH UID : " + uid  );
		return  uid ;
	}

	public Object[] listRecord() {

		String model = "res.company";
		String action = "search"; 
		 Object[] paramsMethod = new Object[]{ 
				     new Object[] { 
				         new Object[] {"is_company", "=", true}, 
				         new Object[] {"customer", "=", true} 
				         } 
				     }; 
		HashMap<Object,Object> paramsRules = new HashMap<Object, Object>() ; 
		paramsRules.put("offset",10); 
		paramsRules.put("limit",5); 
		try {
			Object rLR = listRecord(uid, SERVER_URL, SERVER_DB, SERVER_USERNAME, SERVER_PASSWORD, model , action,paramsMethod,paramsRules);
			Object[] tRLR = (Object[])rLR;
		    return tRLR;
		     
		} catch (MalformedURLException | XmlRpcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     return new Object[]{};
	}
	
	public HashMap<Object,Object> getFields() {

		
		String model = "res.partner";
		String action = "fields_get"; 
		Object[] paramsMethod = new Object[]{ };

		Object[] field_attributes = {"string", "help", "type"};
		HashMap<Object,Object> paramsRules = new HashMap<Object, Object>() ; 
		paramsRules.put("attributes",field_attributes); 
		try {
			Object rLR = listRecord(uid, SERVER_URL, SERVER_DB, SERVER_USERNAME, SERVER_PASSWORD, model , action,paramsMethod,paramsRules);
			HashMap<Object,Object> tRLR = (HashMap<Object,Object>)rLR;
			System.out.println("testGetFields() RESULT: \n --> " + tRLR );
			for (Map.Entry<Object, Object> entry : tRLR.entrySet())
			{
			    System.out.println(entry.getKey() + "/" + entry.getValue());
			}

		    return tRLR;
		     
		} catch (MalformedURLException | XmlRpcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     return new HashMap<Object, Object>() ; 
	}
	
	public  Object[] search() {	

		String model = "res.partner";
		String action = "search_read"; 
		Object[] fields = {"name", "country_id", "comment"};
		Object[] paramsMethod = new Object[]{ 
				     new Object[] { 
				         new Object[] {"is_company", "=", true}, 
				         new Object[] {"customer", "=", true} 
				         } 
				     }; 
		HashMap<Object,Object> paramsRules = new HashMap<Object, Object>() ; 
		paramsRules.put("fields",fields); 
		paramsRules.put("limit",10); 
		try {
			Object rLR = listRecord(uid, SERVER_URL, SERVER_DB, SERVER_USERNAME, SERVER_PASSWORD, model , action,paramsMethod,paramsRules);
			
			Object[] tRLR = (Object[] ) rLR;
			
			for (int i = 0 ; i < tRLR.length; i++) {
				HashMap<Object,Object> auxRLR = (HashMap<Object,Object>)  tRLR[i];
				for (Map.Entry<Object, Object> entry : auxRLR.entrySet())
				{
				    System.out.println(entry.getKey() + " : " + entry.getValue());
				}
			    System.out.println("");
			}
		    return tRLR;
		     
		} catch (MalformedURLException | XmlRpcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return new Object[]{};
		
		
	}

	

	public 	int createExternalId(int idCreated, String external_id , String model) {
		if (idCreated != -1) {
			String action = "create"; 
			HashMap<Object,Object> fieldList = new HashMap<Object, Object>() ; 
			fieldList.put("name",external_id); 
			fieldList.put("complete_name",external_id); 
			fieldList.put("model",model); 
			fieldList.put("res_id",idCreated); 
			
			Object[] paramsMethod = new Object[]{fieldList}; 
			
			HashMap<Object,Object> paramsRules = new HashMap<Object, Object>() ; 
	
			try {
				Object rLR = listRecord(uid, SERVER_URL, SERVER_DB, SERVER_USERNAME, SERVER_PASSWORD, "ir.model.data" , action,paramsMethod,paramsRules);
				
				int tRLR = (Integer ) rLR;
			    return tRLR;
			     
			} catch (MalformedURLException | XmlRpcException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return -1;
	}
	public void delete() {

		String model = "res.partner";
		String action = "unlink"; 

		Object[] idList = {9111};

		Object[] paramsMethod = new Object[]{idList}; 
		
		HashMap<Object,Object> paramsRules = new HashMap<Object, Object>() ; 

		try {
			Object rLR = listRecord(uid, SERVER_URL, SERVER_DB, SERVER_USERNAME, SERVER_PASSWORD, model , action,paramsMethod,paramsRules);
			
			boolean  tRLR = (boolean ) rLR;
		    System.out.println("testDelete() --> Result :  " + tRLR);
		   // return tRLR;
		     
		} catch (MalformedURLException | XmlRpcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	public int create(String model, HashMap<Object,Object> fieldList ) throws XmlRpcException,MalformedURLException{
		String action = "create"; 
		Object[] paramsMethod = new Object[]{fieldList}; 
		HashMap<Object,Object> paramsRules = new HashMap<Object, Object>() ; 


		Object rLR = listRecord(uid, SERVER_URL, SERVER_DB, SERVER_USERNAME, SERVER_PASSWORD, model , action,paramsMethod,paramsRules);
			
		int tRLR = (Integer ) rLR;
		return tRLR;

	}
	
	
	public boolean update(String model, HashMap<Object,Object> fieldList, int id,int company) throws XmlRpcException,MalformedURLException{
		String action = "write"; 

		
		Object[] idList = {id};
		Object[] paramsMethod = new Object[]{idList,fieldList}; 
		
		HashMap<Object,Object> paramsRules = new HashMap<Object, Object>() ; 
		HashMap<Object,Object> context = new HashMap<Object, Object>() ; 
		context.put("force_company", company );
		context.put("company_id", company );
		paramsRules.put("context", context);
		//paramsRules.put("context", company);

		Object rLR = listRecord(uid, SERVER_URL, SERVER_DB, SERVER_USERNAME, SERVER_PASSWORD, model , action,paramsMethod,paramsRules);
			
		boolean  tRLR = (boolean ) rLR;
		System.out.println(TAG + " Update() --> Result :  " + tRLR);
	    return tRLR;
	}
	
	public int  getIdFromExternalId(String external_id) throws XmlRpcException {
		int result = -1;
		String model = "ir.model.data";
		String action = "search_read"; 
		Object[] fields = {"res_id"};
		Object[] paramsMethod = new Object[]{ 
				     new Object[] { 
				         new Object[] {"name", "=", external_id} 
				         } 
				     }; 
		
		HashMap<Object,Object> paramsRules = new HashMap<Object, Object>() ; 
		paramsRules.put("fields",fields); 
		try {

			Object rLR = listRecord(uid, SERVER_URL, SERVER_DB, SERVER_USERNAME, SERVER_PASSWORD, model , action,paramsMethod,paramsRules);
		
			Object[] tRLR = (Object[] ) rLR;

			if (tRLR.length == 1) {
				HashMap<Object,Object> auxRLR = (HashMap<Object,Object>)  tRLR[0];
				result = (Integer)auxRLR.get("res_id");
			}	  
		     
		} catch (MalformedURLException | XmlRpcException e) {
			e.printStackTrace();
		}
	    return result;
	}
	
	private final String COMPANY_FIELD = "company_id/id";
	private final String PARENT_FIELD = "parent_id";
	private final String ID_FIELD = "id";
	
	

	public void exportToOdoo(OdooObject OD) {
		System.out.println("\n" + TAG + "exportToOdoo()");
		boolean isCreated = false;
		
		int company_id = -1;
		try {
			company_id = getIdFromExternalId(OD.xmlObject.getCompany());
		} catch(XmlRpcException e) {
			System.out.println(TAG + "Vaya parece que no se puede encontrar la empresa " + OD.xmlObject.getCompany());
		}
		
		int percent = 0;
		System.out.println(TAG + "Insertando datos, filas : " + OD.data.size());
		for (int i = 0; i < OD.data.size(); i++) {
		//for (int i = 0; i < 3; i++) {
			boolean isDebug = 1 < 3;
			
			HashMap<Object,Object> rowMap = new HashMap<Object,Object> ();
			int id = -1;
			String extId = "-1";
			for (int j = 0; j < OD.data.get(i).size(); j++) {

				XmlField xmlField = OD.getXmlFieldByNameColumn(OD.headerList.get(j));
				//if (isDebug) System.out.println(TAG + "testInsertOD() ODColumn : " + xmlField.getODColumn() + " ; OBColumn : "+  xmlField.getOBColumn()  + " ; Prefix : "+ xmlField.getPrefix());
				if (xmlField != null  ) {
					String value = OD.data.get(i).get(j);
					int idOd = -1;
					Object[] idOd_list = null;
					boolean isId = xmlField.getODColumn().equals(ID_FIELD) ;
					//if (isDebug) System.out.println(TAG + "isId: " + isId + " ; isRel: " + xmlField.isRel() );
					
					if (xmlField.getType() == Type.MANY2MANY) {
						String[] values = value.split(",");
						idOd_list = new Object[values.length];
						for (int k = 0; k < values.length ; k++) {
							if (isDebug) System.out.println(TAG +  "columna: " + xmlField.getODColumn() + " ; value: " + value +  " ; idOd" + idOd + " ; TYPE : " + xmlField.getType()  );
							try {
								int idOd_temp = getIdFromExternalId(values[k]);
								idOd_list[k] = idOd_temp;
							} catch(XmlRpcException e) {
								System.out.println(TAG + "ERROR getIdFromExternalId : " + e.code);
							}
						}
						
					} else if (xmlField.isRel() || isId ) {
						try {
							idOd = getIdFromExternalId(value);
						} catch(XmlRpcException e) {
							System.out.println(TAG + "ERROR getIdFromExternalId : " + e.code);
						}
					}
					
					if (isId) {
						//if (isDebug) System.out.println(TAG + "idOd: " + idOd + " ; id: " + id + " ; extId: " + extId   );
						id = idOd;
						extId = value;
						
					} else if (xmlField.isExportable() && !xmlField.getODColumn().equals(PARENT_FIELD) ) {
						if (isDebug) System.out.println(TAG +  "columna: " + xmlField.getODColumn() + " ; value: " + value +  " ; idOd" + idOd + " ; TYPE : " + xmlField.getType()  );
						switch (xmlField.getType()) {
						//parent_id / categ_id
						case INTEGER:
							int _value = Integer.valueOf(value);
							rowMap.put(xmlField.getODColumn(), _value);
							break;
						case MANY2ONE:
							if (idOd != -1 ) {
								rowMap.put(xmlField.getODColumn(), idOd);
							}
							break;
						case ONE2MANY:
							if (idOd != -1) {
								rowMap.put(xmlField.getODColumn(), idOd);
							}
							break;
						case MANY2MANY:
							if (idOd_list != null) {
								//if (isDebug) System.out.println(TAG + "idOd: " + idOd + " ; id: " + id + " ; extId: " + extId   );
								if (isDebug) System.out.println(TAG + "idOd_list LENGTH : " + idOd_list.length + " ; idOd_list: " + idOd_list.toString() );
								//Object[][] m2m_value = new Object[][] { new Object[] { 6, 0, new Object[]{idOd}} };
								Object[][] m2m_value = new Object[][] { new Object[] { 6, 0, idOd_list} };
								rowMap.put(xmlField.getODColumn(), m2m_value);
							}
							break;
						default:
							rowMap.put(xmlField.getODColumn(), value);
							break;
						}
					}
				} else {
					System.out.println(TAG + "ERROR. XmlField es null");
				}
			}//END FOR
			
			/*
			if (company_id != -1) {
				rowMap.put(COMPANY_FIELD, company_id);
			}
			*/
			
			if (isDebug) System.out.println(TAG + "id: " + id + " ; extId : " + extId  );
			if (id == -1) {
				if (isDebug) {
					System.out.println(TAG + "CREATE, MAP: " + rowMap.toString());	
				}
				isCreated = true;
				
				if(OD.xmlObject.isCanCreate()) {
					try {
						int newId = create(OD.xmlObject.getModel(),rowMap);
						if (isDebug) {
							System.out.println(TAG + "createExternalId, newId: " + newId);	
						}
						createExternalId(newId,extId,OD.xmlObject.getModel());
						//getIdFromExternalId(ID_FIELD);
					} catch(XmlRpcException e) {
						System.out.println(TAG + "XmlRpcException CREATE : " + e.getLocalizedMessage());
					} catch(MalformedURLException  e) {
						System.out.println(TAG + "MalformedURLException CREATE : " + e.getLocalizedMessage());
					}
				} else {
					System.out.println(TAG + "Sin permisos de creación de nuevos ids");
				}
				
			} else {
				if (isDebug) {
						System.out.println(TAG + "UPDATE,MAP : " + rowMap.toString());
				}
				try {
					update(OD.xmlObject.getModel(),rowMap,id,company_id);
					//getIdFromExternalId(ID_FIELD);
				} catch(XmlRpcException e) {
					System.out.println(TAG + "XmlRpcException UPDATE  CODE: " + e.code + " ERROR: " + e.getLocalizedMessage());
				} catch(MalformedURLException  e) {
					System.out.println(TAG + "MalformedURLException UPDATE : " + e.getLocalizedMessage());
				}
			}
			
			
			if (   (i * 100 / OD.data.size() ) > percent  ) {
				percent = i * 100 /  OD.data.size();
				/*System.out.print(".");
				if (percent % 10 == 0)
					System.out.print( "(" + percent + "%)");*/
			}
		}//END FOR
		
	
		XmlField xmlField = OD.getXmlFieldByNameColumn(PARENT_FIELD);
		if (xmlField !=null && isCreated ) {
			System.out.print("\n\n" + TAG + "Creando árbol, añadiendo padres ");
			int columnIndexParent = OD.getColumnIndex(PARENT_FIELD);
			int columnIndex = OD.getColumnIndex(ID_FIELD);
			HashMap<Object,Object> rowMap = new HashMap<Object,Object> ();
			percent = 0;
			//System.out.print(TAG + "Cargando ");
			for (int i = 0; i < OD.data.size(); i++) {
			//for (int i = 0; i < 3; i++) {
				boolean isDebug = 1 < 3;
				String external_id_parent = OD.data.get(i).get(columnIndexParent);
				String external_id = OD.data.get(i).get(columnIndex);
				int idParent = -1;
				try {
					 idParent = getIdFromExternalId(external_id_parent);
				} catch(XmlRpcException e) {
					System.out.println(TAG + "ERROR getIdFromExternalId : " + e.code);
				}
				if (idParent != -1) {
					rowMap.put(xmlField.getODColumn(), idParent);
				}
				rowMap.put(PARENT_FIELD, idParent);
				
				int id = -1;
				try {
					id = getIdFromExternalId(external_id);
				} catch(XmlRpcException e) {
					System.out.println(TAG + "ERROR getIdFromExternalId : " + e.code );
				}
				
				if (isDebug) {
					System.out.println(TAG + " external_id_parent: " + external_id_parent + "; id: " + id + " ; idParent: " + idParent );
				}
				if (idParent != -1 && id != -1) {
					try {
						//getIdFromExternalId(ID_FIELD);
						if (isDebug) System.out.println(TAG + "UPDATE PARENT" +  rowMap);
						update(OD.xmlObject.getModel(),rowMap,id,company_id);
					} catch(XmlRpcException e) {
						System.out.println(TAG + "XmlRpcException UPDATE : " + e.getLocalizedMessage());
					} catch(MalformedURLException  e) {
						System.out.println(TAG + "MalformedURLException UPDATE : " + e.getLocalizedMessage());
					}
				} else {
					if (isDebug) {
						System.out.println(TAG + " NO UPDATE PARENT ");
					}
				}
				
				if (   (i * 100 / OD.data.size() ) > percent  ) {
					percent = i * 100 /  OD.data.size();
					/*System.out.print(".");
					if (percent % 10 == 0)
						System.out.print( "(" + percent + "%)");*/
				}
			}
			
		}
			
		System.out.println("\n" + TAG + "testInsertOD() END");
	}
	
	
	public Object listRecord(int uid, String url, String db, String username, String password, String resource, String action,
			 Object[] paramsMethod,  HashMap<Object,Object> paramsRules	) throws MalformedURLException, XmlRpcException {
			
	     XmlRpcClient client = new XmlRpcClient(); 
	     XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl(); 
	     config.setEnabledForExtensions(true); 

	     config.setServerURL(new URL(url+RPC_OBJECT_URL)); // OK 
	     client.setConfig(config); 
	   
	     Object[] params = new Object[] {db, uid, password, resource, action, paramsMethod, paramsRules}; 
	     Object result = client.execute("execute_kw", params); 
	     return result; 
	 }

}
