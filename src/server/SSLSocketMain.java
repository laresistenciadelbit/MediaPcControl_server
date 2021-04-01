/*
 MediaPcControl (2019-2021) laresistenciadelbit
 Servidor en java que recibe peticiones de una app de móvil para mandar teclas multimedia o comunes, o el portapapeles al PC

 -> Parte del servidor basada en este ejemplo:  http://adblogcat.com/ssl-sockets-android-and-server-using-a-certificate/ (creo que ya no está online)
 -> Clase de lectura/escritura del registro de windows que se usará para configurar el autoarranque (el mensaje es del propio autor, no encontré la fuente oficial) https://stackoverflow.com/questions/62289/read-write-to-windows-registry-using-java/1982033#1982033
 -> Conversión de teclas a códigos de teclas para la clase Robot (https://stackoverflow.com/questions/1248510/convert-string-to-keyevents)
 -> Controlmedia.dll para enviar teclas multimedia ya que java no permite mapear este tipo de teclas. Basado en este programa: https://batchloaf.wordpress.com/2012/04/17/simulating-a-keystroke-in-win32-c-or-c-using-sendinput/
    \_ en realidad estoy simulando un ejecutable como dll, la forma correcta de hacerlo sería esta: https://stackoverflow.com/questions/30221022/how-to-emulate-pressing-media-keys-in-java/55321025#55321025 , https://github.com/nsnave/java-media-keys
*/
package server;

import java.awt.event.ActionListener;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.swing.JOptionPane;
 
public class SSLSocketMain
{
    static public boolean debug=false;
    private static Robot robot;
    
    // convertimos teclas a códigos de teclas para Robot (https://stackoverflow.com/questions/1248510/convert-string-to-keyevents)
    private static void doType(int[] keyCodes, int offset, int length) {
        if (length == 0)
            return;
        robot.keyPress(keyCodes[offset]);
        doType(keyCodes, offset + 1, length - 1);
        robot.keyRelease(keyCodes[offset]);
    }
    private static void doType(int... keyCodes) {   
        doType(keyCodes, 0, keyCodes.length);
    }
    private static void do_key(int character) 
    {
        switch (character) {
        case 'a': doType(KeyEvent.VK_A); break;
        case 'b': doType(KeyEvent.VK_B); break;
        case 'c': doType(KeyEvent.VK_C); break;
        case 'd': doType(KeyEvent.VK_D); break;
        case 'e': doType(KeyEvent.VK_E); break;
        case 'f': doType(KeyEvent.VK_F); break;
        case 'g': doType(KeyEvent.VK_G); break;
        case 'h': doType(KeyEvent.VK_H); break;
        case 'i': doType(KeyEvent.VK_I); break;
        case 'j': doType(KeyEvent.VK_J); break;
        case 'k': doType(KeyEvent.VK_K); break;
        case 'l': doType(KeyEvent.VK_L); break;
        case 'm': doType(KeyEvent.VK_M); break;
        case 'n': doType(KeyEvent.VK_N); break;
        case 'o': doType(KeyEvent.VK_O); break;
        case 'p': doType(KeyEvent.VK_P); break;
        case 'q': doType(KeyEvent.VK_Q); break;
        case 'r': doType(KeyEvent.VK_R); break;
        case 's': doType(KeyEvent.VK_S); break;
        case 't': doType(KeyEvent.VK_T); break;
        case 'u': doType(KeyEvent.VK_U); break;
        case 'v': doType(KeyEvent.VK_V); break;
        case 'w': doType(KeyEvent.VK_W); break;
        case 'x': doType(KeyEvent.VK_X); break;
        case 'y': doType(KeyEvent.VK_Y); break;
        case 'z': doType(KeyEvent.VK_Z); break;
        case 'A': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_A); break;
        case 'B': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_B); break;
        case 'C': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_C); break;
        case 'D': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_D); break;
        case 'E': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_E); break;
        case 'F': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_F); break;
        case 'G': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_G); break;
        case 'H': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_H); break;
        case 'I': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_I); break;
        case 'J': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_J); break;
        case 'K': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_K); break;
        case 'L': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_L); break;
        case 'M': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_M); break;
        case 'N': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_N); break;
        case 'O': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_O); break;
        case 'P': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_P); break;
        case 'Q': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Q); break;
        case 'R': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_R); break;
        case 'S': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_S); break;
        case 'T': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_T); break;
        case 'U': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_U); break;
        case 'V': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_V); break;
        case 'W': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_W); break;
        case 'X': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_X); break;
        case 'Y': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Y); break;
        case 'Z': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Z); break;
        case '`': doType(KeyEvent.VK_BACK_QUOTE); break;
        case '0': doType(KeyEvent.VK_0); break;
        case '1': doType(KeyEvent.VK_1); break;
        case '2': doType(KeyEvent.VK_2); break;
        case '3': doType(KeyEvent.VK_3); break;
        case '4': doType(KeyEvent.VK_4); break;
        case '5': doType(KeyEvent.VK_5); break;
        case '6': doType(KeyEvent.VK_6); break;
        case '7': doType(KeyEvent.VK_7); break;
        case '8': doType(KeyEvent.VK_8); break;
        case '9': doType(KeyEvent.VK_9); break;
        case '-': doType(KeyEvent.VK_MINUS); break;
        case '=': doType(KeyEvent.VK_EQUALS); break;
        case '~': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_QUOTE); break;
        case '!': doType(KeyEvent.VK_EXCLAMATION_MARK); break;
        case '@': doType(KeyEvent.VK_AT); break;
        case '#': doType(KeyEvent.VK_NUMBER_SIGN); break;
        case '$': doType(KeyEvent.VK_DOLLAR); break;
        case '%': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_5); break;
        case '^': doType(KeyEvent.VK_CIRCUMFLEX); break;
        case '&': doType(KeyEvent.VK_AMPERSAND); break;
        case '*': doType(KeyEvent.VK_ASTERISK); break;
        case '(': doType(KeyEvent.VK_LEFT_PARENTHESIS); break;
        case ')': doType(KeyEvent.VK_RIGHT_PARENTHESIS); break;
        case '_': doType(KeyEvent.VK_UNDERSCORE); break;
        case '+': doType(KeyEvent.VK_PLUS); break;
        case '\t': doType(KeyEvent.VK_TAB); break;
        case '\n': doType(KeyEvent.VK_ENTER); break;
        case '[': doType(KeyEvent.VK_OPEN_BRACKET); break;
        case ']': doType(KeyEvent.VK_CLOSE_BRACKET); break;
        case '\\': doType(KeyEvent.VK_BACK_SLASH); break;
        case '{': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_OPEN_BRACKET); break;
        case '}': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_CLOSE_BRACKET); break;
        case '|': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_SLASH); break;
        case ';': doType(KeyEvent.VK_SEMICOLON); break;
        case ':': doType(KeyEvent.VK_COLON); break;
        case '\'': doType(KeyEvent.VK_QUOTE); break;
        case '"': doType(KeyEvent.VK_QUOTEDBL); break;
        case ',': doType(KeyEvent.VK_COMMA); break;
        case '<': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_COMMA); break;
        case '.': doType(KeyEvent.VK_PERIOD); break;
        case '>': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_PERIOD); break;
        case '/': doType(KeyEvent.VK_SLASH); break;
        case '?': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_SLASH); break;
        case ' ': doType(KeyEvent.VK_SPACE); break;
        case 'ñ': 
            robot.keyPress(KeyEvent.VK_ALT);
                robot.keyPress(KeyEvent.VK_NUMPAD1);robot.keyRelease(KeyEvent.VK_NUMPAD1);
                robot.keyPress(KeyEvent.VK_NUMPAD6);robot.keyRelease(KeyEvent.VK_NUMPAD6);
                robot.keyPress(KeyEvent.VK_NUMPAD4);robot.keyRelease(KeyEvent.VK_NUMPAD4);
            robot.keyRelease(KeyEvent.VK_ALT);
        break;
        case 'Ñ': 
            robot.keyPress(KeyEvent.VK_ALT);
                robot.keyPress(KeyEvent.VK_NUMPAD1);robot.keyRelease(KeyEvent.VK_NUMPAD1);
                robot.keyPress(KeyEvent.VK_NUMPAD6);robot.keyRelease(KeyEvent.VK_NUMPAD6);
                robot.keyPress(KeyEvent.VK_NUMPAD5);robot.keyRelease(KeyEvent.VK_NUMPAD5);
            robot.keyRelease(KeyEvent.VK_ALT);
        break;

        case '®':   //simulamos la tacla de borrar como el símbolo ® cuando lo recibimos
            doType(KeyEvent.VK_BACK_SPACE); 
            if(debug)
                System.out.println(" SIMULADO BACKSPACE ");
        break;
        
        default:
            if(debug)
                System.out.println("Cannot type character " + character);
        }
    }
    //añadimos slashes a las rutas de ficheros (para el autoarranque del ejecutable del servidor)
    static public String add_scape_slash_to_file(String f)
    {
        String f2="";
        for(int i=0;i<f.length();i++)
        {
            if(f.charAt(i)=='/')
            {
                f2=f2+f.charAt(i);
                f2=f2+f.charAt(i);
            }
            else
                f2=f2+f.charAt(i);
        }            
        return f2;
    }
    //verificamos que el equipo esté conectado a la red
    public static boolean interface_up() throws SocketException
    {
        boolean up=false;
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
          NetworkInterface interf = interfaces.nextElement();
          if (interf.isUp() && !interf.isLoopback())
            up=true;
        }
        return up;
    }
    
    public static void main(String[] args) throws UnknownHostException, AWTException
    {
        String str_cmd;
        String message;
        robot = new Robot();
        
        String port="8889";
        String keystore = "testserverkeys";//args[1];

        String reg_key="media_pc_control";
        String[] reg_args = new String[] {reg_key};
        
        String jar_path;
        try {
            jar_path=new File(SSLSocketMain.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile()+"\\";
        } catch (URISyntaxException ex) {
            //Logger.getLogger(SSLSocketMain.class.getName()).log(Level.SEVERE, null, ex);
            jar_path="";
        }
        
    //TRAY ICON:
        if (SystemTray.isSupported()) 
        {
            //LEEMOS EL REGISTRO PARA SABER SI ES AUTOARRANCABLE
            String autostart_string="";
            WindowsReqistry w=new WindowsReqistry();
            if( w.regmain(reg_args) == 0)
                autostart_string="disable autostart";
            else
                autostart_string="enable autostart";

            //CREAMOS LOS MENÚS DEL PROGRAMA (BOTÓN DERECHO)
            PopupMenu popMenu= new PopupMenu();
            MenuItem item1 = new MenuItem("ip: " + InetAddress.getLocalHost().getHostAddress());
            MenuItem item2 = new MenuItem("port: " + port); //args[0]);
            MenuItem item5 = new MenuItem(autostart_string); //args[0]);
            MenuItem item3 = new MenuItem("exit");
              //MenuItem item4 = new MenuItem("Config");

              //System.out.println("---> "+new File(SSLSocketMain.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath() +"<----");
              //System.exit(0);

            ActionListener al;
            ActionListener al2;

            al = new ActionListener() {
              public void actionPerformed(ActionEvent e) {  System.exit(0);  }
            };
            item3.addActionListener(al);

            //opción de autoarranque en el menú del programa (activar/desactivar)
            al2 = new ActionListener() {
                public void actionPerformed(ActionEvent e) {  

                    if( w.regmain(reg_args) == 1)
                    {
                        try {
                            Runtime.getRuntime().exec("REG ADD HKEY_LOCAL_MACHINE\\Software\\Microsoft\\Windows\\CurrentVersion\\Run /v "+reg_key+" /t REG_SZ /d "+add_scape_slash_to_file( new File(SSLSocketMain.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath() )+" /f");
                            item5.setLabel("disable autostart");

                            //JOptionPane.showMessageDialog(null, "REG ADD HKEY_LOCAL_MACHINE\\Software\\Microsoft\\Windows\\CurrentVersion\\Run /v "+reg_key+" /t REG_SZ /d "+add_scape_slash_to_file( new File(SSLSocketMain.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath() )+" /f", "warn", JOptionPane.WARNING_MESSAGE);
                        } catch (IOException | URISyntaxException ex) {
                            Logger.getLogger(SSLSocketMain.class.getName()).log(Level.SEVERE, null, ex);
                            //JOptionPane.showMessageDialog(null, ex, "err", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    else
                    {
                        try {
                            Runtime.getRuntime().exec("REG DELETE HKEY_LOCAL_MACHINE\\Software\\Microsoft\\Windows\\CurrentVersion\\Run /v "+reg_key+" /f");
                            item5.setLabel("enable autostart");
                        } catch (IOException ex) {
                            Logger.getLogger(SSLSocketMain.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            };
            item5.addActionListener(al2);

            /* menú de configuración para más adelante:

            al = new ActionListener() {
              public void actionPerformed(ActionEvent e) {  System.exit(0);  }
            };
            item4.addActionListener(al);*/

            popMenu.add(item1);
            popMenu.add(item2);
            popMenu.addSeparator();
            popMenu.add(item5);
            //popMenu.add(item4);
            popMenu.add(item3);
            Image img = Toolkit.getDefaultToolkit().getImage(jar_path+"trayicon"); //trayicon.png
            TrayIcon trayIcon = new TrayIcon(img, "Media pc control", popMenu);
            SystemTray.getSystemTray().add(trayIcon);
        }
    
    //SOCKET
        int socket = Integer.parseInt(port);
        char keystorepass[] = "test-key".toCharArray();
        char keypassword[] = "test-key".toCharArray();
        SSLServerSocket serverSocket = null;
        SSLSocket client;
        
        //si no hay interface de red al encender el pc o al arrancar la aplicación, duerme durante 4 segundos y vuelve a probar hasta 2 intentos más.
        for(int i=0;i<3;i++)
        {
            try {
                if(!interface_up())
                {
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(SSLSocketMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else
                    break;
            } catch (SocketException ex) {
                Logger.getLogger(SSLSocketMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //CREAMOS EL SOCKET POR TLS
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(jar_path+keystore),keystorepass);
            KeyManagerFactory kmf = 
                KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, keypassword);
 
            SSLContext sslcontext = 
                SSLContext.getInstance("TLS");
 
            sslcontext.init(kmf.getKeyManagers(), null, null);
 
            ServerSocketFactory ssf = 
                sslcontext.getServerSocketFactory();
 
            serverSocket = (SSLServerSocket) 
            ssf.createServerSocket(socket);
 
            System.out.println("Server started!");
        } catch (IOException e) {
            if(debug) 
                e.printStackTrace();
            
            System.out.println("Could not listen on port "+socket);
            
            JOptionPane.showMessageDialog(null,
             "Application already running or port busy"+e.toString(),
             "Could not listen on port "+socket,
            JOptionPane.ERROR_MESSAGE);
            
            System.exit(-1);
            
        } catch (KeyStoreException e) {
            if(debug) e.printStackTrace();
            System.out.println("Could not get key store");
            System.exit(-1);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("There is no algorithm in ks.load");
            if(debug)e.printStackTrace();
            System.exit(-1);
        } catch (CertificateException e) {
            if(debug)e.printStackTrace();
            System.exit(-1);
        } catch (UnrecoverableKeyException e) {
            if(debug) e.printStackTrace();
            System.out.println("kmf.init() no key");
            System.exit(-1);
        } catch (KeyManagementException e) {
            if(debug) e.printStackTrace();
            System.out.println("sslcontext.init keymanagementexception");
            System.exit(-1);
        }
        //ESCUCHAMOS CONEXIONES EN EL SOCKET

        while(true)
        {
            try {
                client = (SSLSocket) serverSocket.accept();
                if(debug)System.out.println("client connected");
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                message = in.readLine();
                if(debug)System.out.println("Client's message: "+message);
                if(debug)System.out.println("Responding same message: "+message);
                
                //mute next play prev Vdown Vup stop snapshot
                // 0    1    2    3    4     5    6    7
                boolean reply=false;
                if(debug)
                    reply=true;
                
                str_cmd="";	//separamos la parte de mensaje y la parte de portapapeles
                
                if(message.startsWith("cliptopc"))
                {
                    str_cmd=message.substring(("cliptopc").length());
                    message="cliptopc";

                }
                
                if(message.length()>40 )
                {
                    message="FAIL";
                }
                
                switch(message)
                {
                    case "mute":
                        Runtime.getRuntime().exec("controlmedia.dll 0");
                    break;
                    case "next":
                        Runtime.getRuntime().exec("controlmedia.dll 1");
                    break;
                    case "play":
                        Runtime.getRuntime().exec("controlmedia.dll 2");
                    break;
                    case "prev":
                        Runtime.getRuntime().exec("controlmedia.dll 3");
                    break;
                    case "vdown":
                        Runtime.getRuntime().exec("controlmedia.dll 4");
                    break;
                    case "vup":
                        Runtime.getRuntime().exec("controlmedia.dll 5");
                    break;
                    case "stop":
                        Runtime.getRuntime().exec("controlmedia.dll 6");
                    break;                   
                    case "enter":
                        robot = new Robot();
                        robot.keyPress  ( 10 );
                        robot.keyRelease( 10 );
                    break;
                    case "space":
                        robot = new Robot();
                        robot.keyPress  ( 32 );
                        robot.keyRelease( 32 );
                    break;
                    
                    case "cliptopc":    
                        Runtime.getRuntime().exec( "cmd /c echo "+str_cmd+"|clip" );
                    break;
                    
                    case "clipfrompc":
                        try {
                            // DEVOLVEMOS EL PORTAPAPELES
                            message=(String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                            reply=true;
                        } catch (UnsupportedFlavorException ex) {
                            Logger.getLogger(SSLSocketMain.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    break;

                    case "left":
                        robot = new Robot();
                        robot.keyPress  ( KeyEvent.VK_LEFT  );
                        robot.keyRelease( KeyEvent.VK_LEFT );
                    break;
                    case "right":
                        robot = new Robot();
                        robot.keyPress  ( KeyEvent.VK_RIGHT );
                        robot.keyRelease( KeyEvent.VK_RIGHT );
                    break;
					
                    default:
                        //actúa como teclado si no recibió otro tipo de mensaje
                        if(message.length()==1 && message.charAt(0)>31 && message.charAt(0)<255 )
                        {
                            do_key( message.charAt(0) );
                        }
                }
                
                if(reply)   //solo si el comando requiere respuesta hacia el cliente
                {
                    out.write(message);
                    out.flush();
                    out.close();
                }
                
                in.close();
                client.close();
                //serverSocket.close(); <- se mantiene a la escucha todo el rato
            } catch (IOException e) {
                if(debug)System.out.println("Accept connection failed on "+socket);
                if(debug) e.printStackTrace();
                if(debug) System.exit(-1); //sin debug (de normal) no sale de la aplicación aunque el intento de conxión falle
            }
        }
    }
}