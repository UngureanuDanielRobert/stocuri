/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package client;


import static client.StocuriOfflineDB.numarstoc;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.SocketException;
import java.net.UnknownHostException;
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
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;


/**
 *
 * @author Ungureanu Daniel-Robert
 */



public class StocuriOnline extends javax.swing.JFrame implements Runnable {
    private boolean instanta = false;
    private static Client c;
    /**
     * Creaza o interfata grafica de tipul Stocuri_vizual
     */
    public StocuriOnline() {
        
        initComponents();
        
        et10.setVisible(false);
        elem_t.setVisible(false);
        butonTrimite.setVisible(false);
        b3.setVisible(false);
        mesajeTextArea.append("Stocuri \n");
        mesajeTextArea.append("Adresa server (ex: 127.0.0.1 / localhost) si portul (ex: 1, 2, 355)\n");
    }
    
    
    private void print(Object s) {
        System.out.println(String.format("%s", s));
    }

    private int jTextToInt(javax.swing.JTextField jtf) {

        if (jtf == null) {
            return 0;
        }
        if (jtf.getText().length() < 1) {
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
        int[] cifre = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        String text = camp_text.getText();
        boolean contine = false;

        // Fiecare caracter al textului este preluat.
        for (int i = 0; i < text.length(); i++) {

            // Prin fiecare cifra listata.
            for (int j = 0; j < cifre.length; j++) {

                // Cifra din lista devine caracter
                cifre[j] = (char) cifre[j];

                // Se compara cifra cu caracterul din textul dat.
                if (cifre[j] != text.charAt(i)) {
                    contine = false;
                } else {
                    contine = true;
                }
            }
        }

        return contine;
    }

    // contorizarea coloanelor dintr-o interogare
    private int numarColoane(String interogare) {
        int ncol = 0;
        for (int i = 0; i < interogare.length(); i++) {
            // "" este pentru string, iar '' este pentru char
            if (interogare.charAt(i) == ',') {
                ncol++;
            } else {
            }
        }
        ncol++;
        return ncol;
    }

    // Funcia plasare valori pe tabel
    private JTable setValues2(
            JTable jTable,
            ResultSet rs,
            String coloane,
            int row) {
        reinitializeaza_tabel(jTable); // se reinitializeaza tabelul
        int i = 0;  //randuri
        int j = 1;  //coloane
        int numar_coloane = numarColoane(coloane);

        try {
            Object data;

            while (rs.next()) {

                print("coloana=" + j);

                if (i < jTable.getRowCount()) {
                    while (j <= numar_coloane + 1) {
                        data = rs.getObject(j);
                        jTable.setValueAt(data, i, j - 1);
                        print(data);
                        j++;
                    }
                    j = 1;
                    i++;
                }

                //else{i=0;} // afiseaza ultimele i randuri
            }
            print("S-a terminat cu succes!");
        } catch (SQLException sqle) {
            print(String.format("%s", "randul: " + i + ", coloana: " + j));

            sqle.printStackTrace();

            System.exit(-1);
        }
        return jTable;
    } // end setValues() functional.

    // Functia de afisare elemente din baza de date.
    private JTable afisareBD(JTable jTable, int row) {
        String clasa_driver = "oracle.jdbc.driver.OracleDriver";
        String db = "jdbc:oracle:thin:@localhost:1521:xe";
        String user = "Ungureanu";
        String pass = "dan123";
        String coloane = "idelement,denumire,cod_cont,unitate_masura,numar,pret_buc";

        try {
            Class.forName(clasa_driver);
            Class.forName("java.awt.Component");

            Connection con = DriverManager.getConnection(db, user, pass);

            Statement stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery("select " + coloane
                    + " from Ungureanu.stocuri ORDER BY idelement");

            setValues2(jTable, rs, coloane, row);

        } catch (ClassNotFoundException cnfe) {
            System.out.println("Nu aveti clasa " + clasa_driver);
            System.out.println("sau " + cnfe);
        } catch (SQLException sqle) {
            System.out.println("\n Cauza: ");
            sqle.printStackTrace();
        }
        return jTable;
    }

    // Functional, se da tabelul ca parametru si intoarce tabel nou plus rand.
    private javax.swing.JTable adauga_rand(javax.swing.JTable jTable) {
        String[] coloane
                = new String[]{
                    "Id", "Denumire", "Cod cont",
                    "Unitate masura", "Cantitate",
                    "Pret unitar", "Valoare", "TVA",
                    "Pret TVA", "Valoare TVA", "AC",
                    "Pret AC", "Valoare AC",
                    "Pret total", "Valoare total"
                };

        // Obiectul care face tabelul cu linii si coloane.
        Object[][] tabel = new Object[jTable.getRowCount() + 1][coloane.length];

        // Se preiau valorile din tabelul dat initial.
        for (int i = 0; i < jTable.getRowCount(); i++) {
            for (int j = 0; j < jTable.getColumnCount(); j++) {
                tabel[i][j] = jTable.getValueAt(i, j);
            }
        }

        // Odata stabilite valorile, se reface modelul tabelului.
        jTable.setModel(new javax.swing.table.DefaultTableModel(
                tabel,
                coloane
        ));

        return jTable;
    }

    private javax.swing.JTable reinitializeaza_tabel(javax.swing.JTable jTable) {
        String[] coloane = new String[]{
            "Id", "Denumire",
            "Cod cont", "Unitate masura",
            "Cantitate", "Pret unitar",
            "Valoare", "TVA", "Pret TVA",
            "Valoare TVA", "AC", "Pret AC",
            "Valoare AC", "Pret total", "Valoare total"
        };

        Object[][] tabel = new Object[jTable.getRowCount()][coloane.length];

        jTable.setModel(new javax.swing.table.DefaultTableModel(
                tabel,
                coloane
        ));

        return jTable;
    }

    private JTable curata_tabel(JTable jTable) {
        return reinitializeaza_tabel(jTable);
    }
/*
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

            if (fila.getName().endsWith("xls")) {
                return incarca_xls(jTable, flux_fisier);
            }

            if (fila.getName().endsWith("xlsx")) {
                //return jTable;
                //return incarca_xlsx(jTable, flux_fisier);
            }
        } catch (FileNotFoundException nfe) {
            System.out.println("Nu s-a gasit" + fila.getName());

        } catch (IOException nfe) {
            return jTable;
        }

        return jTable;
    }
/*
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
                                = celula.getNumericCellValue();

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
            XSSFRow rand = (XSSFRow) rand_iter.next();
            int j = 1;
            while (rand.iterator().hasNext()) {

                XSSFCell celula = rand.getCell(j);
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
                                = celula.getNumericCellValue();

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

    // Functii pentru fisiere
    private boolean accept(File fisier) {
        String ext = "";
        String s = fisier.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }

        return "xls".equals(ext) || "xlsx".equals(ext);
    }

    private File alege_fisier(javax.swing.JFileChooser alege_fisier) {
        File fisier;

        alege_fisier.showSaveDialog(null);

        fisier = alege_fisier.getSelectedFile();

        if (accept(fisier)) {
            return fisier;
        }

        return null;
    }
/*
    private void salveaza_excel(JTable jTable) {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet fila = wb.createSheet("Stocuri" + LocalDate.now() + "_" + numarstoc + 1);

        String[] coloane = new String[jTable.getColumnCount()];

        for (int i = 0; i < coloane.length; i++) {
            coloane[i] = jTable.getColumnName(i);
        }

        for (int i = 1; i < jTable.getRowCount(); i++) {
            Row rand = fila.createRow(i);
            for (int j = 0; j < jTable.getColumnCount(); j++) {
                if (jTable.getValueAt(i, j) == null) {
                    continue;
                }
                Cell cell = rand.createCell(j);
                Object data = jTable.getValueAt(i, j);
                if (data instanceof String string) {

                    cell.setCellValue(string);
                }
                if (data instanceof Integer integer) {

                    cell.setCellValue(integer);
                }
                if (data instanceof Float double1) {

                    cell.setCellValue(double1);
                }
                if (data instanceof Double double1) {

                    cell.setCellValue(double1);
                }
            }
        }

        JFileChooser alege_fisier = new JFileChooser();
        alege_fisier.showSaveDialog(this);
        File fisier = alege_fisier.getSelectedFile();

        try {
            wb.write(fisier);
            numarstoc++;
            wb.close();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }
*/
    private Double toDouble(Object ob) {
        return Double.parseDouble(ob.toString());
    }

    private JTable calculeaza_valori(JTable jTable) {
        String[] coloane = new String[jTable.getColumnCount()];

        for (int i = 0; i < jTable.getRowCount(); i++) {
            Double cantitate = Double.parseDouble(jTable.getValueAt(i, 4).toString());
            Double pret = Double.parseDouble(jTable.getValueAt(i, 5).toString());
            Double calcul = cantitate * pret;
            String format = String.format(Locale.ENGLISH, "%.2f", calcul);
            calcul = (Double) Double.parseDouble(format);
            jTable.setValueAt(calcul, i, 6);
        }

        return jTable;

    }

    private JTable calculeaza_tva(JTable jTable) {
        for (int i = 0; i < jTable.getRowCount(); i++) {
            if (jTable.getValueAt(i, 7) == null) {
                jTable.setValueAt(0.19d, i, 7);
            }
            // coloane 8 si 9
            double tva = toDouble(jTable.getValueAt(i, 7));
            double pret_tva = toDouble(jTable.getValueAt(i, 5)) * (1 + tva);
            jTable.setValueAt(pret_tva, i, 8);
            double valoare_tva = toDouble(jTable.getValueAt(i, 6)) * (1 + tva);
            jTable.setValueAt(valoare_tva, i, 9);
        }

        return jTable;
    }

    private double calculeaza_adaos(double valoare, double adaos) {
        if (adaos > 1) {
            adaos = adaos / 100.0d;
        }
        if (adaos < 0) {
            return valoare;
        }
        return valoare * adaos;
    }

    private javax.swing.JTable aplica_adaos(
            javax.swing.JTable jTable,
            JTextField jtf
    ) {
        double adaos = Double.parseDouble(jtf.getText());

        for (int i = 0; i < jTable.getRowCount(); i++) {
            if (jtf.getText() != null) {
                //System.out.println(jtf.getText());
                adaos = Double.parseDouble(jtf.getText());
            }
            jTable.setValueAt(adaos, i, 10);
            //System.out.println(jTable.getValueAt(i, 10));

            double pret_adaos = calculeaza_adaos(toDouble(jTable.getValueAt(i, 5)), adaos);
            //System.out.println(pret_adaos);
            jTable.setValueAt(pret_adaos, i, 11);

            double valoare_adaos = calculeaza_adaos(toDouble(jTable.getValueAt(i, 6)), adaos);
            System.out.println(jtf.getText() + "," + jTable.getValueAt(i, 10) + "," + pret_adaos + "," + valoare_adaos);
            jTable.setValueAt(valoare_adaos, i, 12);

        }

        return jTable;
    }

    private JTable calculeaza_total(JTable jTable) {

        for (int i = 0; i < jTable.getRowCount(); i++) {
            double tva = toDouble(jTable.getValueAt(i, 7));
            double adaos = toDouble(jTable.getValueAt(i, 10));
            if (adaos > 1) {
                adaos /= 100;
            }

            double pret_total = toDouble(
                    jTable.getValueAt(i, 5)) * (tva + adaos);
            jTable.setValueAt(pret_total, i, 13);

            double valoarea_totala = toDouble(
                    jTable.getValueAt(i, 6)) * (tva + adaos);
            jTable.setValueAt(valoarea_totala, i, 14);
        }

        return jTable;
    }

    
    
    @Override
    public synchronized void run() {
        while(!instanta) {  
            
            java.awt.EventQueue.invokeLater(() -> {
                this.setVisible(true);
            });
            instanta = true;
        }
        
        
    }

    /**
     * Metoda apelata din constructor pentru initializarea ferestrei
     * !!! NU modificati codul. Continutul acestei metode este
     * recuperat de editorul de ferestre (Form Editor).
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        butonConectare = new javax.swing.JButton();
        adr_t = new javax.swing.JTextField();
        et00 = new javax.swing.JLabel();
        et01 = new javax.swing.JLabel();
        port_t = new javax.swing.JTextField();
        et10 = new javax.swing.JLabel();
        elem_t = new javax.swing.JTextField();
        butonTrimite = new javax.swing.JButton();
        b3 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        mesajeTextArea = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        butonConectare.setText("Conectare");
        butonConectare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butonConectareActionPerformed(evt);
            }
        });

        et00.setText("Adresa server");

        et01.setText("Port");

        et10.setText("Raspunde la server");

        butonTrimite.setText("Trimite");
        butonTrimite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butonTrimiteActionPerformed(evt);
            }
        });

        b3.setText("Improspateaza");
        b3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b3ActionPerformed(evt);
            }
        });

        mesajeTextArea.setEditable(false);
        mesajeTextArea.setColumns(20);
        mesajeTextArea.setRows(5);
        mesajeTextArea.setMinimumSize(new java.awt.Dimension(480, 240));
        mesajeTextArea.setPreferredSize(new java.awt.Dimension(640, 484));
        jScrollPane2.setViewportView(mesajeTextArea);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(butonConectare)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(adr_t)
                            .addComponent(et00))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(port_t, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(et01)))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(elem_t, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(butonTrimite, javax.swing.GroupLayout.Alignment.LEADING))
                    .addComponent(b3)
                    .addComponent(et10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(et00)
                            .addComponent(et01))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(adr_t, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(port_t, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butonConectare)
                        .addGap(18, 18, 18)
                        .addComponent(et10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(elem_t, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butonTrimite)
                        .addGap(44, 44, 44)
                        .addComponent(b3))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE))
                .addGap(11, 11, 11))
        );

        jTabbedPane1.addTab("Comunicare server", jPanel2);

        jMenu1.setText("File");

        jMenuItem2.setText("Save text");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem1.setText("Exit");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 937, Short.MAX_VALUE)
                .addContainerGap(24, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 732, Short.MAX_VALUE)
                .addContainerGap(37, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void butonConectareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butonConectareActionPerformed
        // TODO :
        // daca nu se introduc adresa si port se afiseaza
        if(adr_t == null || port_t == null) {
            mesajeTextArea.append("Casutele nestabilite.");
            return;
        }
        // daca s-a introdus adresa si port, conectam
        String adr = adr_t.getText();
        Integer port = (Integer) Integer.parseInt(port_t.getText());
        
        
        if(port == 0 || "".equals(adr_t.getText())) {
            mesajeTextArea.append("\nCompletati casutele.\n"
                    + "Adresa " + adr_t.getText() +"\n"
                            + "Port " + port_t.getText());

            return;
        }
        
        if(casuta_contine_caracter(port_t)){
            mesajeTextArea.append("Portul este numar intreg intre 0 si 65535.\n");
            port_t.setText("");
            return;
        }
        
        try {
            this.c = new Client(adr, port);
            c.setSocket(adr, port);
            new Thread(c).start();
            butonTrimite.setVisible(true); elem_t.setVisible(true);
            et10.setVisible(true);
            b3.setVisible(true);
        } catch (NumberFormatException nfex) {
            Logger.getLogger(StocuriOnline.class.getName()).log(Level.OFF, null, nfex);
            adr_t.setText("localhost");
            port_t.setText("2023");
            mesajeTextArea.append("\n V-am completat ca exemplu: \n"
                    + "adresa: localhost \n"
                    + "portul: 2023.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(c.socket != null) {
            butonConectare.setVisible(false);
        }
    }//GEN-LAST:event_butonConectareActionPerformed
    
    private void b3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b3ActionPerformed
        // TODO add your handling code here:
        if(c.mesaj_server != null) {
            mesajeTextArea.append("\n" + c.mesaj_server);
        } else {
            mesajeTextArea.append("\n Nu s-a primit mesaj." );
        }
    }//GEN-LAST:event_b3ActionPerformed

    private void butonTrimiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butonTrimiteActionPerformed
        // TODO add your handling code here:
        // TODO :
        // daca nu se completeaza, ignora si sare peste
        
        try {
            c.iesire.println(elem_t.getText());
            // daca nu se regaseste raspunsul in consola
            if (!mesajeTextArea.getText().contains(elem_t.getText())) {
                // se adauga ceea ce raspundem ca text la intrebare in consola
                mesajeTextArea.append(" " + elem_t.getText());
            }
            elem_t.setText("");

            c.mesaj_server = c.intrare_s.readLine();
            
            mesajeTextArea.append("\n" + c.mesaj_server);

        } catch (SocketException e) {
            c = null;
            mesajeTextArea.append("\n" + "S-a terminat transferul." +
                    "\n Salveaza text, reconecteaza sau inchide. \n");
            e.printStackTrace();
            butonConectare.setVisible(true);
            butonTrimite.setVisible(false); elem_t.setVisible(false);
            et10.setVisible(false);
            b3.setVisible(false);
        } catch (IOException ie) {
            mesajeTextArea.append("\n" + "Exceptia : " + ie);
            ie.printStackTrace();
            butonConectare.setVisible(true);
        } catch (NumberFormatException nfe) {
            mesajeTextArea.append("Ati introdus 2.5 in loc de 2,5 sau vice versa");
            butonConectare.setVisible(true);
        }
    }//GEN-LAST:event_butonTrimiteActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        JFileChooser alege_fisier = new JFileChooser();
        alege_fisier.showSaveDialog(this);
        File fisier = alege_fisier.getSelectedFile();
        try {
            mesajeTextArea.write(new OutputStreamWriter(new FileOutputStream(fisier),"utf-8"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(StocuriOnline.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(StocuriOnline.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed
    
    public static void main(String args[]) throws IOException {
        StocuriOnline sv = new StocuriOnline();
        new Thread(sv).start();
        
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField adr_t;
    private javax.swing.JButton b3;
    private javax.swing.JButton butonConectare;
    private javax.swing.JButton butonTrimite;
    private javax.swing.JTextField elem_t;
    private javax.swing.JLabel et00;
    private javax.swing.JLabel et01;
    private javax.swing.JLabel et10;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea mesajeTextArea;
    private javax.swing.JTextField port_t;
    // End of variables declaration//GEN-END:variables
}
