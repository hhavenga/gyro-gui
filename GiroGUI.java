/**
 * (c) Hardus Havenga 2015
 * Reading Values From the 6 AXIS Giro and Accelerometer (ARDOINO)
 *
 * $RCSfile: GiroGUI.java,v $ $Revision: 1.4 $ $Date: 2015/04/24 13:15:07 $  $Author: hardus $
 *
 * $Log: GiroGUI.java,v $
 * Revision 1.4  2015/04/24 13:15:07  hardus
 * Cleanup
 *
 * Revision 1.3  2015/04/24 13:11:39  hardus
 * Added the Accelerometer Data
 *
 * Revision 1.2  2015/04/24 10:04:30  hardus
 * Added the CVS Header
 *
 *
 */
package za.co.cybercats.utils.giroreader;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.StringTokenizer;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class GiroGUI extends JApplet implements ActionListener, SerialPortEventListener {
	
	SerialPort serialPort;
	public static boolean debug=false;
	
	private static final long serialVersionUID = 1L;
	JPanel mainPanel=null,buttonPanel=null,tempPanel=null;
	JFrame mainFrame = null;
	JButton connectButton=null,exitButton = null;
	JLabel avgField = null, tempField = null, mainLabel=null, currLabel=null, avgLabel=null;
	
	//Giro
	JLabel xGField=null, yGField=null, zGField=null;
	JProgressBar xGBar=null, yGBar=null, zGBar=null;
	
	//Acceleromoter
	JProgressBar xABar=null, yABar=null, zABar=null;
	JLabel xAField=null, yAField=null, zAField=null;
	
	JProgressBar tempBar=null, avgBar=null;
	
	
	int AVG_SIZE = 250;
	double t_avg[];
	double t_avg_total;
	
	int t_counter=0;
	
	DecimalFormat df = new DecimalFormat("00.000");
	DecimalFormat af = new DecimalFormat("00000");
	
	private static final String PORT_NAMES[] = {
		"COM1",
		"COM2",
		"COM3",
		"COM4",
		"COM5",
		"COM6",
        "COM7",
        "COM8",
        "COM9",
        "COM10",
        "COM11",
		"COM12",
		"COM13",
		"COM14",
		"COM15",
		"COM16",
        "COM17",
        "COM18",
        "COM19",
        "COM20",
        "COM21",
		"COM22",
		"COM23",
		"COM24",
		"COM25",
		"COM26",
        "COM27",
        "COM28",
        "COM29",
        "COM30",
    };
	
	private BufferedReader input;
    private static OutputStream output;
    private static final int TIME_OUT = 2000;
    private static final int DATA_RATE = 115200;
	
	public static GiroGUI GIROG=null;
	
	public static void main(String[] args) {
		GIROG = new GiroGUI();
		GIROG.t_avg = new double[GIROG.AVG_SIZE];
	}
	
	public GiroGUI(){
		mainPanel = new JPanel();
		mainPanel.setLayout( new GridBagLayout());
		
		
		GridBagConstraints c = new GridBagConstraints();
		
		connectButton = new JButton("Connect");
		connectButton.addActionListener(this);
		
		exitButton = new JButton("Exit");
		exitButton.addActionListener(this);
		
		/********* Temp *********/
		tempField = new JLabel();
		tempField.setFont(new Font(tempField.getName(), Font.PLAIN, 26));
		tempField.setBackground(Color.BLACK);
		tempField.setForeground(Color.RED);
		tempField.setOpaque(true);
		tempField.setText("00.000");
		
		avgField = new JLabel();
		avgField.setFont(new Font(tempField.getName(), Font.PLAIN, 26));
		avgField.setBackground(Color.BLACK);
		avgField.setForeground(Color.RED);
		avgField.setOpaque(true);
		avgField.setText("00.000");
			
		c.ipady = 10;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 1;
		mainPanel.add(new JLabel("CURR TEMP"),c);
		
		c.gridx = 4;
		c.gridy = 1;
		mainPanel.add(tempField,c);
		
		c.gridx = 7;
		c.gridy = 1;
		tempBar = new JProgressBar();
		tempBar.setMinimum(0);
		tempBar.setMaximum(100);
		tempBar.setBackground(Color.BLUE);
		tempBar.setForeground(Color.RED);
	    
		mainPanel.add(tempBar,c);
		
		c.gridx = 0;
		c.gridy = 2;
		mainPanel.add(new JLabel(" AVERAGE "),c);
		
		c.gridx = 4;
		c.gridy = 2;
		mainPanel.add(avgField,c);
		
		c.gridx = 7;
		c.gridy = 2;
		avgBar = new JProgressBar();
		avgBar.setMinimum(0);
		avgBar.setMaximum(100);
		avgBar.setBackground(Color.BLUE);
		avgBar.setForeground(Color.RED);
		mainPanel.add(avgBar,c);

		/********* X-Axis (Giro) *********/
		xGField = new JLabel();
		xGField.setFont(new Font(xGField.getName(), Font.PLAIN, 26));
		xGField.setBackground(Color.BLACK);
		xGField.setForeground(Color.RED);
		xGField.setOpaque(true);
		xGField.setText("00.000");
		
		c.ipady = 10;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 3;
		mainPanel.add(new JLabel("Giro X-Axis"),c);
		
		c.gridx = 4;
		c.gridy = 3;
		mainPanel.add(xGField,c);
		
		c.gridx = 7;
		c.gridy = 3;
		xGBar = new JProgressBar();
		xGBar.setMinimum(-40000);
		xGBar.setMaximum(+40000);
		xGBar.setBackground(Color.BLUE);
		xGBar.setForeground(Color.RED);
	    
		mainPanel.add(xGBar,c);
		
		/********* Y-Axis (Giro) *********/
		yGField = new JLabel();
		yGField.setFont(new Font(yGField.getName(), Font.PLAIN, 26));
		yGField.setBackground(Color.BLACK);
		yGField.setForeground(Color.RED);
		yGField.setOpaque(true);
		yGField.setText("00.000");
		
		c.ipady = 10;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 4;
		mainPanel.add(new JLabel("Giro Y-Axis"),c);
		
		c.gridx = 4;
		c.gridy = 4;
		mainPanel.add(yGField,c);
		
		c.gridx = 7;
		c.gridy = 4;
		yGBar = new JProgressBar();
		yGBar.setMinimum(-40000);
		yGBar.setMaximum(+40000);
		yGBar.setBackground(Color.BLUE);
		yGBar.setForeground(Color.RED);
	    
		mainPanel.add(yGBar,c);

		/********* Z-Axis (Giro) *********/
		zGField = new JLabel();
		zGField.setFont(new Font(zGField.getName(), Font.PLAIN, 26));
		zGField.setBackground(Color.BLACK);
		zGField.setForeground(Color.RED);
		zGField.setOpaque(true);
		zGField.setText("00.000");
		
		c.ipady = 10;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 5;
		mainPanel.add(new JLabel("Giro Z-Axis"),c);
		
		c.gridx = 4;
		c.gridy = 5;
		mainPanel.add(zGField,c);
		
		c.gridx = 7;
		c.gridy = 5;
		zGBar = new JProgressBar();
		zGBar.setMinimum(-40000);
		zGBar.setMaximum(+40000);
		zGBar.setBackground(Color.BLUE);
		zGBar.setForeground(Color.RED);
	    
		mainPanel.add(zGBar,c);	
		
		/********* X-Axis (Accelerometer) *********/
		xAField = new JLabel();
		xAField.setFont(new Font(xAField.getName(), Font.PLAIN, 26));
		xAField.setBackground(Color.BLACK);
		xAField.setForeground(Color.RED);
		xAField.setOpaque(true);
		xAField.setText("00.000");
		
		c.ipady = 10;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 6;
		mainPanel.add(new JLabel("Accelerometer X-Axis"),c);
		
		c.gridx = 4;
		c.gridy = 6;
		mainPanel.add(xAField,c);
		
		c.gridx = 7;
		c.gridy = 6;
		xABar = new JProgressBar();
		xABar.setMinimum(-40000);
		xABar.setMaximum(+40000);
		xABar.setBackground(Color.BLUE);
		xABar.setForeground(Color.RED);
	    
		mainPanel.add(xABar,c);
		
		/********* Y-Axis (Accelerometer) *********/
		yAField = new JLabel();
		yAField.setFont(new Font(yAField.getName(), Font.PLAIN, 26));
		yAField.setBackground(Color.BLACK);
		yAField.setForeground(Color.RED);
		yAField.setOpaque(true);
		yAField.setText("00.000");
		
		c.ipady = 10;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 7;
		mainPanel.add(new JLabel("Accelerometer Y-Axis"),c);
		
		c.gridx = 4;
		c.gridy = 7;
		mainPanel.add(yAField,c);
		
		c.gridx = 7;
		c.gridy = 7;
		yABar = new JProgressBar();
		yABar.setMinimum(-40000);
		yABar.setMaximum(+40000);
		yABar.setBackground(Color.BLUE);
		yABar.setForeground(Color.RED);
	    
		mainPanel.add(yABar,c);

		/********* Z-Axis (Accelerometer) *********/
		zAField = new JLabel();
		zAField.setFont(new Font(zAField.getName(), Font.PLAIN, 26));
		zAField.setBackground(Color.BLACK);
		zAField.setForeground(Color.RED);
		zAField.setOpaque(true);
		zAField.setText("00.000");
		
		c.ipady = 10;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 8;
		mainPanel.add(new JLabel("Accelerometer Z-Axis"),c);
		
		c.gridx = 4;
		c.gridy = 8;
		mainPanel.add(zAField,c);
		
		c.gridx = 7;
		c.gridy = 8;
		zABar = new JProgressBar();
		zABar.setMinimum(-40000);
		zABar.setMaximum(+40000);
		zABar.setBackground(Color.BLUE);
		zABar.setForeground(Color.RED);
	    
		mainPanel.add(zABar,c);		
			
		/***** END *****/
		c.anchor = GridBagConstraints.PAGE_END; //bottom of space
		c.ipady = 5;
		c.gridwidth = 3;
		
		c.gridx = 0;
		c.gridy = 10;
		c.insets = new Insets(10,0,0,0);  //top padding
		mainPanel.add(connectButton,c);
		
		c.gridx = 6;
		c.gridy = 10;
		mainPanel.add(exitButton,c);
		
		mainFrame = new javax.swing.JFrame("GIRO Reader");
		mainFrame.getContentPane().add(mainPanel);
		mainFrame.pack();
		mainFrame.setSize (new Dimension (450, 450));
		mainFrame.setLocation(250, 250);
		mainFrame.setResizable(false);
		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		Object e = ae.getSource();
		
		if ( e == connectButton ){
			CommPortIdentifier portId = null;
			
			Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
			
	        while (portEnum.hasMoreElements()) {
	            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
	            for (String portName : PORT_NAMES) {
	                if (currPortId.getName().equals(portName)) {
	                    portId = currPortId;
	                    break;
	                }
	            }
	        }
	        if (portId == null) {
	            System.out.println("Could not find COM port.");
	            return;
	        }
			try{
				serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);
				serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,    SerialPort.PARITY_NONE);
				input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
	            output = serialPort.getOutputStream();

	            serialPort.addEventListener(this);
	            serialPort.notifyOnDataAvailable(true);
	            
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
		}else if(e == exitButton){
			if(GiroGUI.debug) System.out.println("Height ["+mainFrame.getSize().height+"], Width["+mainFrame.getSize().width+"]");
			System.exit(0);
		}
	}

	double temperature=0;
	double xaxis=0;
	double yaxis=0;
	double zaxis=0;
		
	@Override
	public void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                String inputLine=input.readLine();
                if(GiroGUI.debug) System.out.println(inputLine);
                
                StringTokenizer strt = new StringTokenizer(inputLine,":");
                
                /***** The Temperature *****/
                strt.nextToken();
                temperature = Double.parseDouble(strt.nextToken());
                
                tempField.setText(""+df.format(temperature) );
                tempBar.setValue(new Double(temperature).intValue()) ;
                
                double avg=temperature;
                avg = getTAvg(avg);
                
                avgField.setText(""+df.format(avg) );
                avgBar.setValue( new Double(avg).intValue());
                
                /***** X-Axis (Giro) *****/
                try{
                  strt.nextToken();
                  xaxis = Double.parseDouble(strt.nextToken());
                  if(GiroGUI.debug) System.out.println("X["+xaxis+"]");
                
                  if(xaxis>0)
                	  xGField.setText(" "+af.format(xaxis));
                  else
                	  xGField.setText(""+af.format(xaxis));
                
                  xGBar.setValue(new Double(xaxis).intValue()) ;
                }catch(Exception ex){
                	ex.printStackTrace();
                }
                               
                /***** Y-Axis (Giro) *****/  
                try{
                  strt.nextToken();
                  yaxis = Double.parseDouble(strt.nextToken());
                
                  if(GiroGUI.debug) System.out.println("Y["+yaxis+"]");
                
                  if(yaxis>0)
                	  yGField.setText(" "+af.format(yaxis));
                  else
                	  yGField.setText(""+af.format(yaxis));
                  
                  yGBar.setValue(new Double(yaxis).intValue()) ;
                } catch (Exception ey){
                	ey.printStackTrace();
                }
                
                /***** Z-Axis (Giro) *****/
                try{
                    strt.nextToken();
                    zaxis = Double.parseDouble(strt.nextToken());
                  
                    if(GiroGUI.debug) System.out.println("Z["+zaxis+"]");
                  
                    if(zaxis>0)
                  	  zGField.setText(" "+af.format(zaxis));
                    else
                  	  zGField.setText(""+af.format(zaxis));
                    
                    zGBar.setValue(new Double(zaxis).intValue()) ;
                  } catch (Exception ey){
                  	ey.printStackTrace();
                  }

                /***** X-Axis (Accelerometer) *****/
                try{
                  strt.nextToken();
                  xaxis = Double.parseDouble(strt.nextToken());
                  if(GiroGUI.debug) System.out.println("X["+xaxis+"]");
                
                  if(xaxis>0)
                	  xAField.setText(" "+af.format(xaxis));
                  else
                	  xAField.setText(""+af.format(xaxis));
                
                  xABar.setValue(new Double(xaxis).intValue()) ;
                }catch(Exception ex){
                	ex.printStackTrace();
                }
                               
                /***** Y-Axis (Accelerometer) *****/  
                try{
                  strt.nextToken();
                  yaxis = Double.parseDouble(strt.nextToken());
                
                  if(GiroGUI.debug) System.out.println("Y["+yaxis+"]");
                
                  if(yaxis>0)
                	  yAField.setText(" "+af.format(yaxis));
                  else
                	  yAField.setText(""+af.format(yaxis));
                  
                  yABar.setValue(new Double(yaxis).intValue()) ;
                } catch (Exception ey){
                	ey.printStackTrace();
                }
                
                /***** Z-Axis (Accelerometer) *****/
                try{
                    strt.nextToken();
                    zaxis = Double.parseDouble(strt.nextToken());
                  
                    if(GiroGUI.debug) System.out.println("Z["+zaxis+"]");
                  
                    if(zaxis>0)
                  	  zAField.setText(" "+af.format(zaxis));
                    else
                  	  zAField.setText(""+af.format(zaxis));
                    
                    zABar.setValue(new Double(zaxis).intValue()) ;
                  } catch (Exception ey){
                  	ey.printStackTrace();
                  }  
                
            } catch (Exception e) {
               e.getMessage();
            }
        }
	}
	
	double getTAvg(double val){
		  double v_pop = 0;
		  if ( t_counter < (AVG_SIZE) ){
			t_avg[t_counter++] = val;
		  } else {
		    v_pop=t_avg[0];
			for(int i=0;i< AVG_SIZE ;i++){
			  if(i< (AVG_SIZE -1) )
			    t_avg[i] = t_avg[i+1];
			  else
			    t_avg[i] = val;
			}
		  }
		  t_avg_total-=v_pop;
		  t_avg_total+=val;
		  return (double)(t_avg_total/t_counter);
	}
}
