package licoresreyes.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import licoresreyes.controller.LRController;
import licoresreyes.model.OdooObject;
import licoresreyes.model.XmlFilter;
import licoresreyes.xmlrpc.XmlRpc;


public class PanelView  extends JPanel{
	private static final String TAG = "VIEW --> ";
	private static final long serialVersionUID = 1L;
	
	LRController lrController = new LRController(); 
	List<JCheckBox> boxes = new ArrayList<JCheckBox>();
	
	private final String TAG_EXPORT_OB = "Exportar OBs";
	JCheckBox checkExportOB= new JCheckBox(TAG_EXPORT_OB);
	
	String[] configStrings = { "config_maestros", "config_central", "config_reyes", "config_dist", "config_gse", "config_licof"};
	JComboBox  configFileNameBox = new JComboBox (configStrings);
	private final String TAG_DATE_VALIDATOR = "Fechas";
	JCheckBox checkDateValidator	= new JCheckBox(TAG_DATE_VALIDATOR);
	
	JTextField dateFieldFrom = new JTextField();
	JTextField monthFieldFrom  = new JTextField();
	JTextField yearFieldFrom  = new JTextField();
	
	JTextField dateFieldTo = new JTextField();
	JTextField monthFieldTo = new JTextField();
	JTextField yearFieldTo = new JTextField();
	
	private final String  CONFIG_FILENAME_DEFAULT = "config";
    Font fontTitle = new Font("Monospaced", Font.BOLD, 14);
	 
	public PanelView() {
		_init();
	}
	
	JPanel leftPanel = new JPanel();
	JPanel ODPanel = new JPanel();
	JPanel topPanel = new JPanel();
	JPanel bottomPanel = new JPanel();
	JPanel datePanel = new JPanel();
	JScrollPane scrollPanel = new JScrollPane(leftPanel);
	
	private final String[] DEFAULT_DATE_FROM = {"01","01","2007"};
	
	public void _init() {
		
		 this.setLayout (new BorderLayout());
	
		
		 loadTopPanel();
		 loadODPanel(BorderLayout.CENTER,fontTitle);
		 loadObjectPanel(BorderLayout.WEST,fontTitle);
		 
		 configFileNameBox.setSelectedIndex(0);

		 loadDate() ;
		 
	}
	
	public void loadTopPanel() {
		 loadDatePanel();
		 topPanel.add(datePanel);
		 topPanel.add(checkExportOB);
		 topPanel.add(configFileNameBox);
		 loadButtons();
		 
		 
		 add(topPanel,BorderLayout.NORTH);
		 
		
	}

	private void loadDate() {
		 Calendar today = Calendar.getInstance();
			
		 //Set default value for DATE FROM
		 dateFieldFrom.setText(DEFAULT_DATE_FROM[0]);
		 dateFieldFrom.setColumns(2);

		 monthFieldFrom.setText(DEFAULT_DATE_FROM[1]);
		 monthFieldFrom.setColumns(2);
		 
		 yearFieldFrom.setText(DEFAULT_DATE_FROM[2]);
		 yearFieldFrom.setColumns(4);

		 //Set default value for DATE TO
		 dateFieldTo.setText(String.valueOf(today.get(Calendar.DAY_OF_MONTH)));
		 dateFieldTo.setColumns(2);

		 monthFieldTo.setText(String.valueOf(today.get(Calendar.MONTH) + 1));
		 monthFieldTo.setColumns(2);
		 
		 yearFieldTo.setText(String.valueOf(today.get(Calendar.YEAR)));
		 yearFieldTo.setColumns(4);
		 
	}
	

	
	public void loadDatePanel() {
		
		JPanel auxPanel = new JPanel();
		auxPanel.setLayout(new BoxLayout(auxPanel, BoxLayout.Y_AXIS));
		
			JPanel dateFromPanel = new JPanel();
			
			dateFromPanel.add(dateFieldFrom);
			dateFromPanel.add(monthFieldFrom);
			dateFromPanel.add(yearFieldFrom);
			
			JPanel dateToPanel = new JPanel();
			
			dateToPanel.add(dateFieldTo);
			dateToPanel.add(monthFieldTo);
			dateToPanel.add(yearFieldTo);
			
		auxPanel.add(dateFromPanel);
		auxPanel.add(dateToPanel);
		
		datePanel.add(auxPanel);
		datePanel.add(checkDateValidator);
	}
	
	public void loadButtons() {
 
		 JButton buttonLoadConfig = new JButton("Cargar CONFIG");
		 buttonLoadConfig.addActionListener(new ActionListener() {
		     public void actionPerformed(ActionEvent e) {
		    	 loadConfig();
		     }
		 });

		 topPanel.add(buttonLoadConfig);

	     
		    JButton buttonODLoad = new JButton("Cargar OD");
		    buttonODLoad.setBackground(Color.red);
		    buttonODLoad.setForeground(Color.white);
		    buttonODLoad.addActionListener(new ActionListener()
		     {
		         public void actionPerformed(ActionEvent e)
		         {	
		        	
		        	 int resp=JOptionPane.showConfirmDialog(null,"Se va a cargar el siguiente objeto OD:\n" + getSelectedItems());
		             if (JOptionPane.OK_OPTION == resp){
		            	 loadOD();
		             } else {
		            	 System.out.println("Se ha cancelado la carga.");
		             }
		         }
		       });

		     topPanel.add(buttonODLoad);
		     
	     JButton buttonLoadSalePriceList= new JButton("Tarifas Venta");
	     buttonLoadSalePriceList.addActionListener(new ActionListener()
	     {
	         public void actionPerformed(ActionEvent e)
	         {	
	        	
	        	 int resp=JOptionPane.showConfirmDialog(null,"Se va a comenzar la carga de tarifas de venta, �Continuar?");
	             if (JOptionPane.OK_OPTION == resp){
	            	 loadSalePriceList();
	             } else {
	            	 System.out.println("Se ha cancelado la carga.");
	             }
	         }
	       });

	     topPanel.add(buttonLoadSalePriceList);
	     
	     JButton buttonLoadBuyPriceList= new JButton("Tarifas compra");
	     buttonLoadBuyPriceList.addActionListener(new ActionListener()
	     {
	         public void actionPerformed(ActionEvent e)
	         {	
	        	
	        	 int resp=JOptionPane.showConfirmDialog(null,"Se va a comenzar la carga de tarifas de compra, �Continuar?");
	             if (JOptionPane.OK_OPTION == resp){
	            	 loadBuyPriceList();
	             } else {
	            	 System.out.println("Se ha cancelado la carga.");
	             }
	         }
	       });

	     topPanel.add(buttonLoadBuyPriceList);
	     
	     
    JButton buttonLoad= new JButton("Iniciar Carga");
    buttonLoad.setBackground(Color.red);
    buttonLoad.setForeground(Color.white);
     buttonLoad.addActionListener(new ActionListener()
     {
         public void actionPerformed(ActionEvent e)
         {	
        	
        	 int resp=JOptionPane.showConfirmDialog(null,"Se va a comenzar la carga para los objetos:\n" + getSelectedItems());
             if (JOptionPane.OK_OPTION == resp){
            	 loadData();
             } else {
            	 System.out.println("Se ha cancelado la carga.");
             }
         }
       });

     topPanel.add(buttonLoad);
     
	}
	
	public void loadObjectPanel(String align,Font fontTitle) {
		
	    leftPanel.setLayout(new BoxLayout(leftPanel,BoxLayout.Y_AXIS));
        JLabel leftTitle = new JLabel(" ---- SELECCIONAR LOS OBJETOS ODOO A IMPORTAR ---- ", JLabel.CENTER);
	    leftTitle.setFont(fontTitle);
	    leftPanel.add(leftTitle);

		 
   		scrollPanel.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPanel,BorderLayout.WEST);
		 
	}

	public void loadObjectContent() {
	     leftPanel.removeAll();
	     for (int i = 0; i < lrController.getXmlLoader().getObjectCount();i++) {
		    	
	    	 String ODTag = lrController.getXmlLoader().getObjectList().get(i).getId();
	    	
	    	 JCheckBox check = new JCheckBox(ODTag);
	    	 JPanel ODPanel = new JPanel();
	    	 ODPanel.setLayout(new BoxLayout (ODPanel,BoxLayout.Y_AXIS));
	    	 String OBTag = "    " + lrController.getXmlLoader().getObjectList().get(i).getMainTable().getId();
	    	 JLabel OBLabel = new JLabel(OBTag,JLabel.RIGHT);
	    	 ODPanel.add(OBLabel);
	    	 
	    	 for (int j = 0; j < lrController.getXmlLoader().getObjectList().get(i).getReferenceTableList().size(); j++) {
	    		 OBTag = "    " + lrController.getXmlLoader().getObjectList().get(i).getReferenceTableList().get(j).getId();
	    		 JLabel OBLabelRef = new JLabel(OBTag,JLabel.RIGHT);
	    		 ODPanel.add(OBLabelRef);
	    	 }
	    	 
	    	 leftPanel.add(check);
	    	 boxes.add(check);
	    	 leftPanel.add(ODPanel);
	     }
	     leftPanel.revalidate();
	     leftPanel.repaint();

	}
	
	public void updateControllerConfig() {
		LRController.setHasExportOb(checkExportOB.isSelected());
		LRController.setFilterDate(checkDateValidator.isSelected());
		
		LRController.setDate(Integer.parseInt((dateFieldFrom.getText())),
							 Integer.parseInt((monthFieldFrom.getText())), 
							 Integer.parseInt((yearFieldFrom.getText())),
							Integer.parseInt((dateFieldTo.getText())),
							Integer.parseInt((monthFieldTo.getText())), 
							Integer.parseInt((yearFieldTo.getText())));
	}
	public void loadData() {
		updateControllerConfig();
		lrController.loadData(this);
	}
	
	/* WOOOOOO */
	public void loadSalePriceList() {
		//XmlRpc xmlRpc = new XmlRpc();
		//xmlRpc.testInsertOD(OD);
		//xmlRpc.testSearch();
		//xmlRpc.testCreate();
		//xmlRpc.testCreateExternalId();
		//xmlRpc.testGetIdFromExternalId();
		//xmlRpc.testUpdate();
		//xmlRpc.testDelete();
		//lrController.loadSalePriceList(this);
	}
	
	public void loadBuyPriceList() {
		lrController.loadBuyPriceList(this);
	}
	
	
	public boolean isSelected(String toFind) {
		boolean result = false;
		for (int i = 0; i < boxes.size(); i++) {
			if (boxes.get(i).getText().equals(toFind)) {
				result = boxes.get(i).isSelected();
			}
		}
		return result;
	}
	
	public String getSelectedItems() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < boxes.size(); i++) {
			if (boxes.get(i).isSelected())
				builder.append(boxes.get(i).getText() + "\n");
		}
		if (builder.length() == 0) {
			builder.append(" NO SE HAN SELECCIONADO OBJETOS \n");
		}
		return builder.toString();
	}

	
	public void loadConfig() {
		lrController.loadXmlConfig((String)configFileNameBox.getSelectedItem());
		loadObjectContent();
	}
  
    
    public static void createAndShowGUI() {
    	 
        //Create and set up the window.
        JFrame frame = new JFrame("LRConversor");
        frame.setTitle("LR Conversor");
        frame.setSize(1280, 720);
        frame.setLocationRelativeTo(null);      
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Create and set up the content pane.
        PanelView panelView = new PanelView();
        panelView.setOpaque(true); //content panes must be opaque
        frame.setContentPane(panelView);
 
        //Display the window.
       // frame.pack();
        frame.setVisible(true);
    }
    

    JPanel ODPanelInfo = new JPanel();
    JPanel ODPanelButtons = new JPanel();
    JPanel ODPanelData = new JPanel();

    JTable columnTable = new JTable();
    JTable filterTable = new JTable();
    
    JScrollPane ODscrollPanelColumns = new JScrollPane(columnTable);
    JScrollPane ODscrollPanelFilters = new JScrollPane(filterTable);
    

    
    //ELEMENTOS DEL PANEL INFO
    JLabel lblFilename = new JLabel("Filename : ");
    JTextField txtFilenameData = new JTextField("model.filename",40);
    
    JLabel lblColumnCount = new JLabel("Columnas : ");
    JLabel lblColumnCountData = new JLabel("00");
    
    JLabel lblRowCount = new JLabel("Filas : ");
    JLabel lblRowCountData = new JLabel("00");
    
    //ELEMENTOS DEL PANEL DE BOTONES
    JButton btnApplyFilters = new JButton("ApplyFilters");
    JButton btnFilterDates = new JButton("Filtrar fechas");
    JButton btnToCSV = new JButton("To CSV");
    JButton btnExport = new JButton("Exportar a Odoo");
    
	public void loadODPanel(String align,Font fontTitle) {


	     
		ODPanel.setLayout(new BorderLayout());
		
		loadODPanelInfo();
		loadODPanelButton();
		
		
		loadODPanelColumns();
		loadODPanelFilters();
		ODPanel.add(ODPanelData,BorderLayout.CENTER);
		
		
		ODPanel.setBackground(new Color(0.0f,0.0f,0.0f));
		add(ODPanel,align);
	}
	
	public void loadODPanelInfo() {
		ODPanelInfo.add(lblFilename);
		ODPanelInfo.add(txtFilenameData);
		ODPanelInfo.add(lblColumnCount);
		ODPanelInfo.add(lblColumnCountData);
		ODPanelInfo.add(lblRowCount);
		ODPanelInfo.add(lblRowCountData);
		ODPanelInfo.setBackground(new Color(0.7f,0.7f,0.7f));
		ODPanelInfo.setAlignmentX(LEFT_ALIGNMENT);
		ODPanel.add(ODPanelInfo,BorderLayout.NORTH);
	}	
	public void loadODPanelButton() {	
		btnApplyFilters.addActionListener(new ActionListener()
		     {
		         public void actionPerformed(ActionEvent e)
		         {	
		        	 applyFilters();
		         }
		       });

		ODPanelButtons.add(btnApplyFilters);
		
		
		btnFilterDates.addActionListener(new ActionListener()
		     {
		         public void actionPerformed(ActionEvent e)
		         {	
		        	 updateDates();
		         }
		       });

		ODPanelButtons.add(btnFilterDates);
		
		btnExport.addActionListener(new ActionListener()
	     {
	         public void actionPerformed(ActionEvent e)
	         {	
	        	 export();
	         }
	       });
		
		btnExport.setBackground(Color.red);
		btnExport.setForeground(Color.white);
		ODPanelButtons.add(btnExport);
		
		btnToCSV.addActionListener(new ActionListener()
	     {
	         public void actionPerformed(ActionEvent e)
	         {	
	        	 toCSV();
	         }
	       });
		btnToCSV.setBackground(Color.BLUE);
		btnToCSV.setForeground(Color.white);
		
		ODPanelButtons.add(btnToCSV);
		
		ODPanelButtons.setBackground(new Color(0.7f,0.7f,0.7f));
		ODPanel.add(ODPanelButtons,BorderLayout.SOUTH);
	}
	
	public void loadODPanelColumns() {
		ODPanelData.setLayout(new BoxLayout(ODPanelData,BoxLayout.Y_AXIS));
		
		ODPanelData.add(ODscrollPanelColumns);
	}
	
	public void loadODPanelFilters() {

		ODPanelData.add(ODscrollPanelFilters);
	}

	public void setInfoData(String filename, int columnCount, int rowCount) {
		//System.out.println("PANEL_VIEW SET INFO DATA --> Filename : " + filename + " ; columnCount : " + columnCount + " ; rowCount : " + rowCount );
		txtFilenameData.setText(filename);
		lblColumnCountData.setText(String.valueOf(columnCount));
		lblRowCountData.setText(String.valueOf(rowCount));
		
		ODPanelInfo.revalidate();
		ODPanelInfo.repaint();
	}
	
	public void setColumnData() {
		if (OD != null) {
			Object columnNames[] = { "OD_COLUMN", "PREFIX", "FILTER", "DEFAULT_VALUE", "OB_COLUMN", "TABLE","EXPORTABLE"};
			int rowCount = 0;
			for (int i = 0; i < OD.xmlObject.getMainTable().getFields().size(); i++ ) {
				rowCount++;
			}
			for (int j = 0; j < OD.xmlObject.getReferenceTableList().size(); j++ ) {
				for (int i = 0; i < OD.xmlObject.getReferenceTableList().get(j).getFields().size(); i++ ) {
					rowCount++;
				}
			}
			Object rowData[][] = new Object[rowCount][columnNames.length];
	
			int auxCounter = 0;
			for (int i = 0; i < OD.xmlObject.getMainTable().getFields().size(); i++ ) {
				//System.out.println("Main Table Field " + i + " --> " + OD.xmlObject.getMainTable().getFields().get(i).getODColumn() );
				rowData[i][0] = OD.xmlObject.getMainTable().getFields().get(i).getODColumn();
				rowData[i][1] = OD.xmlObject.getMainTable().getFields().get(i).getPrefix();
				rowData[i][2] = OD.xmlObject.getMainTable().getFields().get(i).getFilter();
				rowData[i][3] = OD.xmlObject.getMainTable().getFields().get(i).getDefaultvalue();
				rowData[i][4] = OD.xmlObject.getMainTable().getFields().get(i).getOBColumn();
				rowData[i][5] = OD.xmlObject.getMainTable().getId();
				rowData[i][6] = OD.xmlObject.getMainTable().getFields().get(i).getExportable();
				auxCounter++;
			}
			
			for (int j = 0; j < OD.xmlObject.getReferenceTableList().size(); j++ ) {
				//System.out.println("Ref Table Field j: " + j + " ; Id : " +  OD.xmlObject.getReferenceTableList().get(j).getId() + " ; size:  " + OD.xmlObject.getReferenceTableList().get(j).getFields().size() );
				for (int i = 0; i < OD.xmlObject.getReferenceTableList().get(j).getFields().size(); i++ ) {
					
					//System.out.println("Ref Table i : " + i + " ; auxCounter : " + auxCounter + " --> " + OD.xmlObject.getReferenceTableList().get(j).getFields().get(i).getODColumn() );
					rowData[auxCounter][0] = OD.xmlObject.getReferenceTableList().get(j).getFields().get(i).getODColumn();
					rowData[auxCounter][1] = OD.xmlObject.getReferenceTableList().get(j).getFields().get(i).getPrefix();
					rowData[auxCounter][2] = OD.xmlObject.getReferenceTableList().get(j).getFields().get(i).getFilter();
					rowData[auxCounter][3] = OD.xmlObject.getReferenceTableList().get(j).getFields().get(i).getDefaultvalue();
					rowData[auxCounter][4] = OD.xmlObject.getReferenceTableList().get(j).getFields().get(i).getOBColumn();
					rowData[auxCounter][5] = OD.xmlObject.getReferenceTableList().get(j).getId();
					rowData[auxCounter][6] = OD.xmlObject.getReferenceTableList().get(j).getFields().get(i).getExportable();
					auxCounter++;
				}
			}
			
			TableModel model = new DefaultTableModel(rowData, columnNames);
			columnTable.setModel(model);
			columnTable.revalidate();
			columnTable.repaint();
		}
	}
	
	public void setFilterData() {
		if (OD != null) {
			Object columnNames[] = { "TYPE", "COLUMN", "BY", "VALUE", "SET1", "SET2"};
			Object rowData[][] = new Object[OD.xmlObject.getFilterList().size()][columnNames.length];

			List<XmlFilter> filters = OD.xmlObject.getFilterList();
			for (int i = 0; i < filters.size(); i++) {

				rowData[i][0] = filters.get(i).getType().toString();
				rowData[i][1] = filters.get(i).getColumn();
				rowData[i][2] = filters.get(i).getBy();
				rowData[i][3] = filters.get(i).getValue();
				rowData[i][4] = filters.get(i).getSet1();
				rowData[i][5] = filters.get(i).getSet2();

			}

			TableModel model = new DefaultTableModel(rowData, columnNames);
			filterTable.setModel(model);
			filterTable.revalidate();
			filterTable.repaint();
		}
	}
	
	OdooObject OD = null;
	public void loadOD() {
		updateControllerConfig();
		
		OD = lrController.loadOD(this);
		if (OD != null) {
			setInfoData(OD.filename,OD.getColumnCount(),OD.getRowCount());
			setFilterData() ;
			setColumnData();
		} else {
			System.out.println("\n" + TAG + "ERROR. No se han seleccionado objetos");
		}
	}
	
	public void applyFilters() {
		if (OD != null) {	
			int resp=JOptionPane.showConfirmDialog(null,"Se va a aplicar filtros Y prefijos al objeto OD :" + OD.filename);
            if (JOptionPane.OK_OPTION == resp){
            	OD =  lrController.smartApplyFilters(OD);
            	setInfoData(OD.filename,OD.getColumnCount(),OD.getRowCount());
            } 
		} else {
			System.out.println("\n" + TAG + "ERROR. No se han seleccionado objetos");
		}
	}
	
	public void updateDates() {
		updateControllerConfig();
		if (OD != null) {
			OD = lrController.smartUpdateDates(OD);
			setInfoData(OD.filename,OD.getColumnCount(),OD.getRowCount());
		}else {
			System.out.println("\n" + TAG + "ERROR. No se han seleccionado objetos");
		}
	}
	public void toCSV() {
		if (OD != null) {	
			OD.SaveFile("");
		}else {
			System.out.println("\n" + TAG + "ERROR. No se han seleccionado objetos");
		}
	}

	public void export() {
		if (OD != null) {	
			XmlRpc xmlRpc = new XmlRpc();
			if (xmlRpc.uid != -1){
				xmlRpc.exportToOdoo(OD);
			} else {
				System.out.println("\n" + TAG + "ERROR. Parece que el uid de XML-RPC no es v�lido.");
			}
		}else {
			System.out.println("\n" + TAG + "ERROR. No se han seleccionado objetos");
		}
	}
}
