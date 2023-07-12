/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package client;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author diabl
 */
public class StocuriOfflineDB extends javax.swing.JFrame {
    static int numarstoc = 0;
    /**
     * Creates new form StocuriOfflineDB
     */
    public StocuriOfflineDB() {
        initComponents();
        initComponents2();
    }
    
    
    private void print(Object s) {
        System.out.println(String.format("%s",s));
    }
    
    

    private int jTextToInt(javax.swing.JTextField jtf) {
        
        if (jtf == null) {
            return 0;
        }
        if (jtf.getText().length()<1){
            return 0;
        }
        
        try {
            return Integer.parseInt(jtf.getText());
        } catch (NumberFormatException e) {
            print("Scrieti un numar in casuta numar randuri");
            
            return 0;
        }
    }
    
    // Functia pentru verificare contine caractere.
    private boolean casuta_contine_caracter(JTextField camp_text) {
        int[] cifre = {0,1,2,3,4,5,6,7,8,9};
        String text = camp_text.getText();
        boolean contine = false;
        
        // Fiecare caracter al textului este preluat.
        for(int i = 0; i < text.length(); i++) {
            
            // Prin fiecare cifra listata.
            for(int j = 0; j < cifre.length; j++) {
                
                // Cifra din lista devine caracter
                cifre[j] = (char) cifre[j];
                
                // Se compara cifra cu caracterul din textul dat.
                if(cifre[j] != text.charAt(i)) {
                    contine = false;
                }
                
                else {
                    contine = true;
                }
            }
        }
        
        return contine;
    }
    
    private void initComponents2() {
        // A doua initializare ascunde anumite elemente care nu sunt necesare.
        // Campul pentru introducerea numarului de coloane jTextField1.
        jScrollPane1.setSize(this.getSize());
        jTable1.setSize(jScrollPane1.getSize());
    }
    
    
    // contorizarea coloanelor dintr-o interogare
    private int numarColoane(String interogare) {
        int ncol = 0;
        for (int i = 0; i < interogare.length(); i++) {
            // "" este pentru string, iar '' este pentru char
            if(interogare.charAt(i) == ',') {
                ncol++;
            } else {}
        }
        ncol++;
        return ncol;
    }
    
    // Functia plasare valori pe tabel
    private void setValues2(
            ResultSet rs, 
            String coloane, 
            int row) 
    {
        reinitializeaza_tabel(jTable1,row); // se reinitializeaza tabelul
        int i = 0;  //randuri
        int j = 1;  //coloane
        int numar_coloane = numarColoane(coloane);
        
        try {
            Object data;
            
            while(rs.next()) { 
                
                print("coloana=" + j);
                
                if(i < jTable1.getRowCount()){
                    while(j <= numar_coloane) {
                        data = rs.getObject(j);
                        jTable1.setValueAt(data, i, j-1);
                        print(data);
                        j++;
                    }
                    j=1;
                    i++;
                }
                
                //else{i=0;} // afiseaza ultimele i randuri
            }
            print("S-a terminat cu succes!");
        } catch (SQLException sqle) {
            print(String.format("%s", "randul: "+ i +", coloana: "+j));
            
            sqle.printStackTrace();
            
            System.exit(-1);
        }
    } // end setValues() obiectual.
    
    // setValues functional
    
    // Funcia plasare valori pe tabel
    private JTable setValues2(
            JTable jTable,
            ResultSet rs, 
            String coloane, 
            int row) 
    {
        reinitializeaza_tabel(jTable,row); // se reinitializeaza tabelul
        int i = 0;  //randuri
        int j = 1;  //coloane
        int numar_coloane = numarColoane(coloane);
        
        try {
            Object data;
            
            while(rs.next()) { 
                
                print("coloana=" + j);
                
                if(i < jTable.getRowCount()) {
                    while(j <= numar_coloane+1) {
                        data = rs.getObject(j);
                        jTable.setValueAt(data, i, j-1);
                        print(data);
                        j++;
                    }
                    j=1;
                    i++;
                }
                
                //else{i=0;} // afiseaza ultimele i randuri
            }
            print("S-a terminat cu succes!");
        } catch (SQLException sqle) {
            print(String.format("%s", "randul: "+ i +", coloana: "+j));
            
            sqle.printStackTrace();
            
            System.exit(-1);
        }
        return jTable;
    } // end setValues() functional.
    
    
    
    // Functia de afisare elemente din baza de date.
    private void afisareBD(int row) {
        String clasa_driver = "oracle.jdbc.driver.OracleDriver";
        String db   = "jdbc:oracle:thin:@localhost:1521:xe";
        String user = "Ungureanu";
        String pass = "dan123";
        String coloane = "idelement,denumire,cod_cont,unitate_masura,numar,pret_buc";
        
        try {
            Class.forName(clasa_driver);
            Class.forName("java.awt.Component");
        
            Connection con = DriverManager.getConnection(db,user,pass);
            
            Statement stmt = con.createStatement();
            
            // Redam tabela sortata dupa numarul id.
            ResultSet rs = stmt.executeQuery("select " + coloane
                    + " from Ungureanu.stocuri ORDER BY idelement");
            
            setValues2(rs, coloane, row);
            
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Nu aveti clasa " + clasa_driver);
            System.out.println("sau " + cnfe);
        } catch (SQLException sqle) {
            System.out.println("\n Cauza: ");
            sqle.printStackTrace();
        }    
    }
    
    
    private JTable reinitializeaza_tabel(JTable jTable1, int row) {
        if (row < 0) {
            return null;
        } 
        
        String[] coloane = new String [] {
                "Id", "Denumire", 
            "Cod cont", "Unitate masura", 
            "Cantitate", "Pret unitar", 
            "Valoare", "TVA", "Pret TVA", 
            "Valoare TVA", "AC", "Pret AC", 
            "Valoare AC", "Pret total", "Valoare total"
            };
        
        Object[][] tabel = new Object[row][coloane.length];
        
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            tabel,
            coloane
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setMaximumSize(new java.awt.Dimension(1920, 1080));
        jTable1.setMinimumSize(new Dimension( (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight(), (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() ));
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);
        
        return jTable1;
    }

    private JTable curata_tabel(JTable jTable) {
        return reinitializeaza_tabel(jTable, jTable.getRowCount());
    }
    
    private Double toDouble(Integer intr) {
        return Double.parseDouble(String.format(".2f", intr));
    }
    
    private Double toDouble(Object ob) {
        return Double.parseDouble(ob.toString());
    }
    
    private JTable calculeaza_valori(JTable jTable) {
        String[] coloane = new String[jTable.getColumnCount()];
        
        
        for(int i = 0; i < jTable.getRowCount(); i++) {
            Double cantitate = Double.parseDouble(jTable.getValueAt(i, 4).toString()) ;
            Double pret = Double.parseDouble(jTable.getValueAt(i, 5).toString());
            Double calcul = cantitate * pret;
            String format = String.format(Locale.ENGLISH,"%.2f", calcul);
            calcul = (Double) Double.parseDouble(format);
            jTable.setValueAt(calcul, i, 6);
        }
        
        return jTable;
        
    }
    
    private JTable calculeaza_tva(JTable jTable) {
        for(int i = 0; i < jTable.getRowCount(); i++) {
            if (jTable.getValueAt(i, 7) == null) {
                jTable.setValueAt(0.19d, i, 7);
            }
            // coloane 8 si 9
            double tva = toDouble(jTable.getValueAt(i, 7));
            double pret_tva = toDouble(jTable.getValueAt(i, 5)) * (1+tva);
            jTable.setValueAt(pret_tva, i, 8);
            double valoare_tva = toDouble(jTable.getValueAt(i, 6)) * (1+tva);
            jTable.setValueAt(valoare_tva, i, 9);
        }
        
        return jTable;
    }
    
    private double calculeaza_adaos(double valoare, double adaos) {
        return valoare * adaos/100;     
    }
    
    private javax.swing.JTable aplica_adaos(
            javax.swing.JTable jTable, 
            JTextField jtf
    )
    {
        double adaos = Double.parseDouble(jtf.getText());
        
        for(int i = 0; i < jTable.getRowCount(); i++) {
            if (jtf.getText() != null) {
                //System.out.println(jtf.getText());
                adaos = Double.parseDouble(jtf.getText());
            }
            jTable.setValueAt(adaos, i, 10);
            //System.out.println(jTable.getValueAt(i, 10));
            
            double pret_adaos = calculeaza_adaos(toDouble(jTable.getValueAt(i, 5)),adaos);
            //System.out.println(pret_adaos);
            jTable.setValueAt(pret_adaos, i, 11);
            
            double valoare_adaos = calculeaza_adaos(toDouble(jTable.getValueAt(i, 6)),adaos);
            //System.out.println(jtf.getText()+","+jTable.getValueAt(i, 10)+","+pret_adaos+","+valoare_adaos);
            jTable.setValueAt(valoare_adaos, i, 12);
            
        }
        
        return jTable;
    }
    
    
    private JTable calculeaza_total(JTable jTable) {
        
        for(int i = 0; i < jTable.getRowCount(); i++) {
            if(jTable.getValueAt(i, 7) == null) {
                continue;
            }
            if(jTable.getValueAt(i, 10) == null) {
                continue;
            }
            double tva = toDouble(jTable.getValueAt(i, 7));
            double adaos = toDouble(jTable.getValueAt(i, 10));
            tva +=1; tva *= 100;
            
            
            double pret_unitar = toDouble(jTable.getValueAt(i, 5));
            double valoare     = toDouble(jTable.getValueAt(i, 6));
            
            System.out.println(adaos + "-" + tva);
            double pret_total = calculeaza_adaos(pret_unitar,(tva+adaos));
            jTable.setValueAt(pret_total, i, 13);
            
            double valoarea_totala = calculeaza_adaos(valoare,(tva+adaos));
            jTable.setValueAt(valoarea_totala, i, 14);
        }
        
        return jTable;
    }
    
    
    // Functii pentru fisiere
    
    private boolean accept(File fisier) {
        String ext = "";
        String s = fisier.getName();
        int i = s.lastIndexOf('.');
        
        if(i > 0 && i < s.length()-1) {
            ext = s.substring(i+1).toLowerCase();
        }
        
        return "xls".equals(ext) || "xlsx".equals(ext);
    }
    
    private File alege_fisier(javax.swing.JFileChooser alege_fisier) {
        File fisier;
        
        alege_fisier.showSaveDialog(null);
        
        fisier = alege_fisier.getSelectedFile();
        
        
        
        if(accept(fisier)){
            return fisier;
        }
        
        return null;
    }

    private void scrie_xls(JTable jTable, File fisier) {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet fila = wb.createSheet("Stocuri"+LocalDate.now()+"_"+numarstoc+1);
        
        String[] coloane = new String[jTable.getColumnCount()];
        
        for (int i = 0; i<coloane.length;i++){
            coloane[i] = jTable.getColumnName(i);
        }
        
        for(int i = 0; i < jTable.getRowCount(); i++) {
            Row rand = fila.createRow(i);
            for(int j = 0; j < jTable.getColumnCount(); j++) {
                Cell cell = rand.createCell(j);
                var data = jTable.getValueAt(i, j);
                System.out.println(data);
                if(data instanceof Double) {
                    cell.setCellValue(
                            new BigDecimal(data.toString()).doubleValue());
                }
                if(data instanceof Integer integer) {
                    cell.setCellValue(Double.valueOf(integer.toString()+".0"));
                }
                if(data instanceof String) {
                    cell.setCellValue(data.toString());
                }
                if(data instanceof Number) {
                    cell.setCellValue(new BigDecimal(data.toString()).doubleValue());
                }
            }
        }
        
        
        try(FileOutputStream fos = new FileOutputStream(fisier)) {
            
            wb.write(fos);
            
        } catch(IOException ie) {
        }
        numarstoc += 1;
    }
    
    /*
    private void scrie_xlsx(JTable jTable, File fisier) {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet fila = wb.createSheet("Stocuri"+LocalDate.now()+"_"+numarstoc+1);
        
        String[] coloane = new String[jTable.getColumnCount()];
        
        for (int i =0; i<coloane.length;i++){
            coloane[i] = jTable.getColumnName(i);
        }
        
        for(int i = 0; i < jTable.getRowCount(); i++) {
            XSSFRow rand = fila.createRow(i);
            for(int j = 0; j < jTable.getColumnCount(); j++) {
                XSSFCell cell = rand.createCell(j);
                var data = jTable.getValueAt(i, j);
                System.out.println(data);
                if(data instanceof Double) {
                    cell.setCellValue(
                            new BigDecimal(data.toString()).doubleValue());
                }
                if(data instanceof Integer integer) {
                    cell.setCellValue(Double.valueOf(integer.toString()+".0"));
                }
                if(data instanceof String) {
                    cell.setCellValue(data.toString());
                }
                if(data instanceof Number) {
                    cell.setCellValue(new BigDecimal(data.toString()).doubleValue());
                }
            }
        }
        
        
        try(FileOutputStream fos = new FileOutputStream(fisier)) {
            
            wb.write(fos);
        } catch(IOException ie) {
        }
        numarstoc += 1;
        
    }*/
    
    private void salveaza_excel(JTable jTable) {
        
        JFileChooser alege_fisier = new JFileChooser();
        alege_fisier.showSaveDialog(this);
        File fisier = alege_fisier.getSelectedFile();
        String nume = fisier.getName();
        System.out.println("Numele fisierului = "+nume);
        
        if(nume.endsWith(".xls")){
            scrie_xls(jTable, fisier);
            return;
        }
        if(nume.endsWith(".xlsx")){
            return;
        }
    }
    

    // Din tabel excel xls
    private javax.swing.JTable incarca_xls(
            javax.swing.JTable jTable,
            FileInputStream flux_fisier
    )
            throws IOException {
        String[] coloane = new String[jTable.getColumnCount()];

        // libraria org.apache.poi.hssf.usermodel
        // Reda exceptia de tip java.io.IOException
        // Preiua fisier excel prin fluxul de fisiere
        HSSFWorkbook excel = new HSSFWorkbook(flux_fisier);

        // Preiua prima fisa din fisierul excel
        HSSFSheet fila = excel.getSheetAt(0);

        // Iau numarul de randuri
        int randuri_fila = fila.getLastRowNum();

        for (int i = 0; i < coloane.length; i++) {
            coloane[i] = jTable.getColumnName(i);
        }

        Object[][] tabel = new Object[randuri_fila+1][coloane.length];

        Iterator<Row> rand_iter = fila.iterator();

        while (rand_iter.hasNext()) {
            Row rand = rand_iter.next();
            int j = 0;
            while (rand.iterator().hasNext()) {

                Cell celula = rand.getCell(j);
                if (celula == null) {
                    break;
                }
                int rand_celula = celula.getAddress().getRow();
                if (rand_celula < tabel.length
                        && j < tabel.length) {
                    if (celula.getCellTypeEnum() == CellType.STRING) {
                        tabel[rand_celula][j]
                                = celula.getStringCellValue();

                        System.out.println(
                                tabel[rand_celula][j] + " = "
                                + celula.getStringCellValue());
                    }
                    if (celula.getCellTypeEnum() == CellType.NUMERIC) {
                        double getnumval =  celula.getNumericCellValue();
                        
                        String formatnum = String.format("%.2f", getnumval);
                        
                        /*
                        if(celula.getAddress().getColumn() == 0) {
                            getnumval = (int) getnumval;
                        }
                        else {
                        
                            for(int k = 0; k < formatnum.length(); k++) {
                                if(formatnum.charAt(k) == '.') {
                                    getnumval = Double.parseDouble(formatnum.substring(0,k+3));
                                    k=formatnum.length();
                                }
                            }
                        }*/
                        if(j==0) {
                            tabel[rand_celula][j] = (int) getnumval;
                            
                        }
                        else{
                            tabel[rand_celula][j] = Double.parseDouble(
                                formatnum.replace(",", ".")
                                    .substring
                                (0, 
                                formatnum.length() - formatnum.indexOf(".")-1));
                        }
                        
                        System.out.println(
                                tabel[rand_celula][j] + " = "
                                + celula.getNumericCellValue());
                    }

                }
                j++;
            }
        }
        jTable.setModel(new javax.swing.table.DefaultTableModel(
                tabel,
                coloane
        ));

        return jTable;
    }

    /*
    // Functia pentru excel 2007-... de tip xlsx
    private javax.swing.JTable incarca_xlsx(
            javax.swing.JTable jTable,
            FileInputStream flux_fisier
    ) throws IOException {

        String[] coloane = new String[jTable.getColumnCount()];

        // libraria org.apache.poi.hssf.usermodel
        // Reda exceptia de tip java.io.IOException
        // Preiua fisier excel prin fluxul de fisiere
        XSSFWorkbook excel = new XSSFWorkbook(flux_fisier);

        // Preiua prima fisa din fisierul excel
        XSSFSheet fila = excel.getSheetAt(0);

        // Iau numarul de randuri
        int randuri_fila = fila.getLastRowNum();

        for (int i = 0; i < coloane.length; i++) {
            coloane[i] = jTable.getColumnName(i);
        }

        Object[][] tabel = new Object[randuri_fila][coloane.length];

        Iterator<Row> rand_iter = fila.iterator();

        while (rand_iter.hasNext()) {
            Row rand = rand_iter.next();
            int j = 1;
            while (rand.iterator().hasNext()) {

                Cell celula = rand.getCell(j);
                if (celula == null) {
                    break;
                }
                int rand_celula = celula.getAddress().getRow();
                if (rand_celula < tabel.length
                        && j < coloane.length) { 
                    if (celula.getCellTypeEnum() == CellType.STRING) {
                        tabel[rand_celula][j]
                                = celula.getStringCellValue();

                        System.out.println(
                                tabel[rand_celula][j] + " = "
                                + celula.getStringCellValue());
                    }
                    if (celula.getCellTypeEnum() == CellType.NUMERIC) {
                        tabel[rand_celula][j]
                                = (Number) celula.getNumericCellValue();

                        System.out.println(
                                tabel[rand_celula][j] + " = "
                                + celula.getNumericCellValue());
                    }

                }
                j++;
            }
        }
        jTable.setModel(new javax.swing.table.DefaultTableModel(
                tabel,
                coloane
        ));

        return jTable;
    }*/
    
    // Functia principala
    private javax.swing.JTable incarca_excel(
            javax.swing.JTable jTable,
            File fila
    ) {
        if (fila == null) {
            return jTable;
        }
        try {
            FileInputStream flux_fisier = new FileInputStream(fila);

            if (fila.getName().endsWith(".xls")) {
                return incarca_xls(jTable, flux_fisier);
            }

            if (fila.getName().endsWith(".xlsx")) {
                return jTable;
                //return incarca_xlsx(jTable, flux_fisier);
            }
        } catch (FileNotFoundException nfe) {
            System.out.println("Nu s-a gasit" + fila.getName());

        } catch (IOException nfe) {
            return jTable;
        }

        return jTable;
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButton6 = new javax.swing.JButton();
        jTextField2 = new javax.swing.JTextField();
        jTextField1 = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        adaosTextField = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton6.setText("Calculeaza total");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jTextField2.setMaximumSize(new java.awt.Dimension(128, 32));
        jTextField2.setMinimumSize(new java.awt.Dimension(64, 22));
        jTextField2.setName(""); // NOI18N
        jTextField2.setPreferredSize(new java.awt.Dimension(64, 22));

        jTextField1.setEditable(false);
        jTextField1.setText("Număr rânduri:");

        jButton4.setText("Aplică TVA");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("Aplică adaos");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton2.setText("Afișare date");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        adaosTextField.setMaximumSize(new java.awt.Dimension(128, 32));
        adaosTextField.setMinimumSize(new java.awt.Dimension(64, 22));
        adaosTextField.setName(""); // NOI18N
        adaosTextField.setPreferredSize(new java.awt.Dimension(64, 22));

        jTextField3.setEditable(false);
        jTextField3.setText("Adaos comercial:");

        jButton1.setText("Curăță tabel");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton3.setText("Calculeaza valori");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(adaosTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton6)))
                .addContainerGap(134, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(adaosTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton1)
                        .addComponent(jButton2)
                        .addComponent(jButton3)
                        .addComponent(jButton4)
                        .addComponent(jButton5)
                        .addComponent(jButton6))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Id", "Denumire", "Cod cont", "Unitate masura", "Cantitate", "Pret unitar", "Valoare", "TVA", "Pret TVA", "Valoare TVA", "AC", "Pret AC", "Valoare AC", "Pret total", "Valoare total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setMaximumSize(new java.awt.Dimension(1920, 1080));
        jTable1.setMinimumSize(new Dimension( (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2), (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2) ));
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 972, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTextField4.setEditable(false);
        jTextField4.setText("Student:");

        jTextField5.setEditable(false);
        jTextField5.setText("Coordonator:");

        jLabel5.setText("Contact:");

        jTextField6.setEditable(false);
        jTextField6.setText("Ungureanu Daniel-Robert");

        jLabel6.setText("ungureanudanielrobert@yahoo.com");

        jTextField7.setEditable(false);
        jTextField7.setText("Prof. univ. dr. Mihailescu Marius Iulian");

        jLabel7.setText("Student:");

        jLabel8.setText("Profesor:");

        jLabel9.setText("m.mihailescu.mi@spiruharet.ro");

        jLabel3.setText("Anul:");

        jLabel4.setText("2023");

        jLabel1.setText("Titlu:");

        jLabel2.setText("Sistem Informatic pentru Administrarea Stocurilor ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel1)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel2)
                            .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)
                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addGap(112, 112, 112))
        );

        jMenu1.setText("File");

        jMenuItem3.setText("Salvare excel");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuItem4.setText("Încarcă excel...");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuItem1.setText("Exit");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu4.setText("About");

        jMenuItem5.setText("About...");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem5);

        jMenuItem6.setText("Inapoi");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem6);

        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO ...
        jTable1 = curata_tabel(jTable1);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO ...
        int row = 0;
        row = jTextToInt(jTextField2);
        
        //col = jTextToInt(jTextField3);
        //int col = jTable1.getColumnCount();
        //int row = jTable1.getRowCount();
        if (row > 1000000) {
            print("Nu adaugati un numar mai mare de 1000000.");
            return;
        }
        afisareBD(row);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        jTable1 = calculeaza_valori(jTable1);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
        salveaza_excel(jTable1);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        jTable1 = calculeaza_tva(jTable1);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        if(adaosTextField.getText() == null) {
            return;
        }
        if("".equals(adaosTextField.getText())) {
            return;
        }
        if(casuta_contine_caracter(adaosTextField)) {
            return;
        }
        jTable1 = aplica_adaos(jTable1,adaosTextField);
        
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        jTable1 = calculeaza_total(jTable1);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // TODO add your handling code here:
        File fisier = alege_fisier(new JFileChooser());
        jTable1 = incarca_excel(jTable1,fisier);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        // TODO add your handling code here:
        jPanel2.setVisible(false);
        jPanel3.setVisible(false);
        jPanel1.setVisible(true);
        jMenuItem4.setVisible(true);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        // TODO add your handling code here:
        jMenuItem4.setVisible(false);
        jPanel1.setVisible(false);
        jPanel2.setVisible(true);
        jPanel3.setVisible(true);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(StocuriOfflineDB.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(StocuriOfflineDB.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(StocuriOfflineDB.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(StocuriOfflineDB.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new StocuriOfflineDB().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField adaosTextField;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    // End of variables declaration//GEN-END:variables

}
