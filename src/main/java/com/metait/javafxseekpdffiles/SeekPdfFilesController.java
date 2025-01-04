package com.metait.javafxseekpdffiles;

import com.spire.ms.System.Exception;
import com.spire.pdf.interactive.digitalsignatures.IOCSPService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ArrayChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Clipboard;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Objects;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import com.spire.pdf.FileFormat;
import com.spire.pdf.PdfDocument;

import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;

// import java.awt.Desktop;
import javafx.util.Callback;

// import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SeekPdfFilesController {
    @FXML
    private Button buttonSelectBaseDir;
    @FXML
    private Label labelMsg;
    @FXML
    private Label labelExec;
    @FXML
    private Button buttonExec;
    @FXML
    private Button buttonCancel;
    @FXML
    private ListView<String> listPdfs;
    @FXML
    private CheckBox checkBoxPssWdPdfs;
    @FXML
    private TextField textFieldDir;
    @FXML
    private CheckBox checkBoxReadAllSubDirs;
    @FXML
    private TextField textFieldFileName;
    @FXML
    private Button buttonSelectWriteDir;
    @FXML
    private TextField textFieldDir21;
    @FXML
    private RadioButton radioBoxRemoveBMakrs;
    @FXML
    private Button buttonWritePdf;
    @FXML
    private RadioButton radoBoxConvertWordDoc;
    @FXML
    private RadioButton radoBoxPassWords;
    @FXML
    private TextField textFieldOwnerPwd;
    @FXML
    private TextField textFieldUserPwd;
    @FXML
    private HBox hBox1, hBox2;
    @FXML
    private VBox vBoxMain;
    @FXML
    private CheckBox checkBoxPrint;
    @FXML
    private CheckBox checkBoxAssembleDocument;
    @FXML
    private CheckBox checkBoxAllowExtraction;
    @FXML
    private CheckBox checkBoxCanExtractForAccessibility;
    @FXML
    private CheckBox checkBoxAllowFillingInForm;
    @FXML
    private CheckBox checkBoxAllowModifications;
    @FXML
    private CheckBox checkBoxAllowAnnotationModification;
    @FXML
    private CheckBox checkBoxCanPrintFaithful;
    @FXML
    private CheckBox checkBoxReadPermissions;
    @FXML
    private TextField textFieldSelected;
    @FXML
    private CheckBox checkBoxReallyPssWdPdfs;
    @FXML
    private ProgressIndicator processIndicatorExec;

    // private Desktop desktop = null;
    private boolean bExecuted = false;
    private final SeekPdfFilesProcesses2 processes = new SeekPdfFilesProcesses2();
    private boolean bProssesRestarted = false;
    private ArrayList<String> listResultItems = new ArrayList<String>();
    private ObservableList<String> listObservableListItems = FXCollections.observableArrayList(listResultItems);
    private String strResult = null;
    private SEEKTYPES executeType = SEEKTYPES.SEEK_ENCRYPTED_PDF_FILES;

    private File inputPdfFile;
    private Stage mainStage;
    private File baseDir = null;
    private File writeDir = null;

    public void setStage(Stage p_stage)
    {
        mainStage = p_stage;
    }

    @FXML
    protected void initialize()
    {
        processIndicatorExec.setProgress(0.0);
        textFieldSelected.setEditable(false);
        hBox1.setVisible(false);
        hBox2.setVisible(false);

//        if (Desktop.isDesktopSupported())
  //          desktop = Desktop.getDesktop();
        labelMsg.setText("Welcome to JavaFX Application!");
        buttonExec.setDisable(true);
        buttonCancel.setDisable(true);
        bExecuted = false;
        buttonWritePdf.setDisable(true);
        /*
        listResultItems.add("!");
        listResultItems.add("!1");
        listResultItems.add("!2");
        listResultItems.add("!12");
         */
        listPdfs.setItems(listObservableListItems);
        listObservableListItems.setAll(listResultItems);

        listPdfs.setOnMouseClicked(me -> {
            if (me.getButton() == MouseButton.PRIMARY && me.getClickCount() == 2) {
                int newPosition = listPdfs.getSelectionModel().getSelectedIndex();
                if (newPosition == -1)
                    return;
                System.out.println("New Position is: " + newPosition);

                String item = listPdfs.getSelectionModel().getSelectedItem();
                if (item == null || item.isEmpty())
                    return;
                System.out.println("Clicked: " + item);
                try {
                    // if (desktop != null)
                    //   desktop.open(new File(item));
                    ProcessBuilder processBuilder = new ProcessBuilder();
                    // -- Linux --
                    // Run a shell command
                    processBuilder.command("bash", "-c", "open " + item);

                    Process process = processBuilder.start();
                    StringBuilder output = new StringBuilder();
                    BufferedReader reader = new BufferedReader(
                                new InputStreamReader(process.getInputStream()));

                    String line;
                    while ((line = reader.readLine()) != null) {
                            output.append(line + "\n");
                    }

                    int exitVal = process.waitFor();
                    if (exitVal == 0) {
                        System.out.println("Success!");
                        System.out.println(output);
                        setLabelMsg("Opened file: " + item);
                    } else {
                        //abnormal...
                        setLabelMsg("Cannot open: " + item);
                    }
                } catch (IOException e) {
                        e.printStackTrace();
                    setLabelMsg("Error: " +e.getMessage());
                } catch (InterruptedException e) {
                        e.printStackTrace();
                    setLabelMsg("Error: " +e.getMessage());
                }

            } else {
                int newPosition = listPdfs.getSelectionModel().getSelectedIndex();
                if (newPosition == -1)
                    return;
                System.out.println("New Position is: " + newPosition);

                String item = listPdfs.getSelectionModel().getSelectedItem();
                if (item == null || item.isEmpty())
                    return;
                System.out.println("Clicked: " + item);
                try {
                    ClipboardContent content = new ClipboardContent();
                    content.putString(item);
                    // content.putHtml("<b>Bold</b> text");
                    Clipboard.getSystemClipboard().setContent(content);
                    setLabelMsg("Into clipboard: " +item);
                    if (checkBoxReadPermissions.isSelected())
                        readPdfPermissions(item);
                    else
                    {
                        checkBoxPrint.setSelected(false);
                        checkBoxAssembleDocument.setSelected(false);
                        checkBoxAllowExtraction.setSelected(false);
                        checkBoxCanExtractForAccessibility.setSelected(false);
                        checkBoxAllowFillingInForm.setSelected(false);
                        checkBoxAllowModifications.setSelected(false);
                        checkBoxAllowAnnotationModification.setSelected(false);
                        checkBoxCanPrintFaithful.setSelected(false);
                        textFieldSelected.setText("");
                    }
                    // if (desktop != null)
                    //   desktop.open(new File(item));
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                        setLabelMsg("Error: " +ioe.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                        setLabelMsg("Error: " +e.getMessage());
                    }
            }
        });
    }

    private void readPdfPermissions(String selectedFile)
            throws IOException, Exception
    {
        PDDocument document = Loader.loadPDF(new File(selectedFile));
        // Replace with your desired user password
        // String userPassword = "your_user_password";
        // Replace with your desired owner password
        // String ownerPassword = "your_owner_password";
        AccessPermission accessPermission = document.getCurrentAccessPermission();

        checkBoxPrint.setSelected(accessPermission.canPrint());
        checkBoxAssembleDocument.setSelected(accessPermission.canAssembleDocument());
        checkBoxAllowExtraction.setSelected(accessPermission.canExtractContent());
        checkBoxCanExtractForAccessibility.setSelected(accessPermission.canExtractForAccessibility());
        checkBoxAllowFillingInForm.setSelected(accessPermission.canFillInForm());
        checkBoxAllowModifications.setSelected(accessPermission.canModify());
        checkBoxAllowAnnotationModification.setSelected(accessPermission.canModifyAnnotations());
        checkBoxCanPrintFaithful.setSelected(accessPermission.canPrintFaithful());
        textFieldSelected.setText(selectedFile);
    }

    @FXML
    protected void radioButtonPressed()
    {
        setPassWordCtlsVisible();
    }

    private void setPassWordCtlsVisible() {
        if (radoBoxPassWords.isSelected()) {
            hBox1.setVisible(true);
            hBox2.setVisible(true);
            textFieldOwnerPwd.setDisable(false);
            textFieldUserPwd.setDisable(false);
            vBoxMain.setVisible(true);
            mainStage.show();
        } else {
            hBox1.setVisible(false);
            hBox2.setVisible(false);
            textFieldOwnerPwd.setDisable(true);
            textFieldUserPwd.setDisable(false);
            vBoxMain.setVisible(true);
            mainStage.show();
        }
    }

    @FXML
    protected void radoBoxPassWordsPressed()
    {
       setPassWordCtlsVisible();
    }

    @FXML
    protected void buttonWritePdfPressed()
    {
        int iSelected = listPdfs.getSelectionModel().getSelectedIndex();
        if (iSelected == -1)
            return;
        String strTmp = textFieldFileName.getText().trim();
        if (strTmp == null || strTmp.isEmpty()) {
            setLabelMsg("Give write file name!");
            return;
        }
        String strTmp2 = textFieldDir21.getText().trim();
        if (strTmp2 == null || strTmp2.isEmpty()) {
            setLabelMsg("Give write directory or selected correspond button!");
            return;
        }
        File tmpDir = new File(strTmp2);
        if (!tmpDir.exists()) {
            setLabelMsg("Given write directory does not exiat! Selected correspond button!");
            return;
        }
        writeDir = tmpDir;
        if (!radioBoxRemoveBMakrs.isSelected() && !radoBoxConvertWordDoc.isSelected() && !radoBoxPassWords.isSelected() ) {
            setLabelMsg("Selected some radiobutton!");
            return;
        }

        String strTmp3 = textFieldFileName.getText().trim();
        if (strTmp3 == null || strTmp3.isEmpty()) {
            setLabelMsg("Give write file name!");
            return;
        }
        if ((radioBoxRemoveBMakrs.isSelected() || radoBoxPassWords.isSelected()) && !strTmp3.endsWith(".pdf")) {
            setLabelMsg("Given write file name must end with: .pdf!");
            return;
        }
        if (radoBoxConvertWordDoc.isSelected() && !strTmp3.endsWith(".docx")) {
            setLabelMsg("Given write file name must end with: .docx!");
            return;
        }

        File witeFile = new File(writeDir.getAbsolutePath() +File.separatorChar +strTmp3);
        if (witeFile.exists())
        {
            setLabelMsg("Given write file name exists all ready!");
            return;
        }
        String selected = listPdfs.getSelectionModel().getSelectedItem();
        if (selected == null || selected.isEmpty())
        {
            setLabelMsg("There is no selected pdf in the found pdf file list!");
            return;
        }
        inputPdfFile = new File(selected);
        if (!inputPdfFile.exists())
        {
            setLabelMsg("There exists no selected pdf file in the list!");
            return;
        }

        if (inputPdfFile != null && witeFile != null && inputPdfFile.getAbsolutePath().equals(witeFile.getAbsolutePath()))
        {
            setLabelMsg("The input file and output file cannot be the same!");
            return;
        }

        if (radioBoxRemoveBMakrs.isSelected()) {
            removeBookMarksFromCopyFile(inputPdfFile, witeFile);
        }
        if (radoBoxConvertWordDoc.isSelected()) {
            convertIntoWordDocFromCopyFile(inputPdfFile, witeFile);
        }
        if (radoBoxPassWords.isSelected()){
            String strTmp4 = textFieldUserPwd.getText().trim();
            if (strTmp4 == null || strTmp4.isEmpty()) {
                setLabelMsg("Given user password is empty!");
                return;
            }
            String strTmp5 = textFieldOwnerPwd.getText().trim();
            if (strTmp5 == null || strTmp5.isEmpty()) {
                setLabelMsg("Given owner password is empty!");
                return;
            }
            setPassWordsIntoPdfFromCopyFile(inputPdfFile, witeFile, textFieldUserPwd.getText(), textFieldOwnerPwd.getText());
        }
    }

    private void setPassWordsIntoPdfFromCopyFile(File inputPdfFile, File witeFile, String userPassword, String ownerPassword)
    {
        try {
            try {
                // Your code to password protect the document will go here

                // Replace with the path to your input PDF file
                PDDocument document = Loader.loadPDF(inputPdfFile);
                // Replace with your desired user password
                // String userPassword = "your_user_password";
                // Replace with your desired owner password
                // String ownerPassword = "your_owner_password";
                AccessPermission accessPermission = new AccessPermission();
                // Set to true if you want to allow printing
                accessPermission.setCanPrint(checkBoxPrint.isSelected());
                accessPermission.setCanAssembleDocument(checkBoxAssembleDocument.isSelected());
                accessPermission.setCanExtractContent(checkBoxAllowExtraction.isSelected());
                accessPermission.setCanExtractForAccessibility(checkBoxCanExtractForAccessibility.isSelected());
                accessPermission.setCanFillInForm(checkBoxAllowFillingInForm.isSelected());
                accessPermission.setCanModify(checkBoxAllowModifications.isSelected());
                accessPermission.setCanModifyAnnotations(checkBoxAllowAnnotationModification.isSelected());
                accessPermission.setCanPrintFaithful(checkBoxCanPrintFaithful.isSelected());
                /*
                 */
                StandardProtectionPolicy protectionPolicy = new StandardProtectionPolicy(ownerPassword, userPassword, accessPermission);
                protectionPolicy.setEncryptionKeyLength(128);
                document.protect(protectionPolicy);
                document.save(witeFile);
                document.close();
                setLabelMsg("PDF file is password protected successfully!");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }catch (Exception e){
            e.printStackTrace();
            setLabelMsg("Error: " +e.getMessage());
        }
    }

    private void convertIntoWordDocFromCopyFile(File inputPdfFile, File witeFile)
    {
        try {
            PdfDocument pdf = new PdfDocument();
            //Load the PDF file
            pdf.loadFromFile(inputPdfFile.getAbsolutePath());
            //Save the result file
            pdf.saveToFile(witeFile.getAbsolutePath());
            //Convert PDF to Docx and save it to a specified path
            pdf.saveToFile(witeFile.getAbsolutePath(), FileFormat.DOCX);
        }catch (Exception e){
            e.printStackTrace();
            setLabelMsg("Error: " +e.getMessage());
        }
    }

    private void removeBookMarksFromCopyFile(File inputPdfFile, File witeFile)
    {
        try {
            PdfDocument pdf = new PdfDocument();
            //Load the PDF file
            pdf.loadFromFile(inputPdfFile.getAbsolutePath());
            //Delete the first child bookmark
            //pdf.getBookmarks().get(0).removeAt(0);
            //Delete the first bookmark along with its child bookmark
            //pdf.getBookmarks().removeAt(0);
            //Delete all the bookmarks
            pdf.getBookmarks().clear();
            //Save the result file
            pdf.saveToFile(witeFile.getAbsolutePath());
        }catch (Exception e){
            e.printStackTrace();
            setLabelMsg("Error: " +e.getMessage());
        }
    }

    @FXML
    protected void buttonSelectWriteDirPressed()
    {
        int iSelected = listPdfs.getSelectionModel().getSelectedIndex();
        if (iSelected == -1)
            return;
    try {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select pdf write directory");
        buttonWritePdf.setDisable(true);
        if (writeDir != null)
            directoryChooser.setInitialDirectory(writeDir);
        else
            directoryChooser.setInitialDirectory(new File("."));
        File dir = directoryChooser.showDialog(mainStage);
        if (dir != null && dir.exists() && dir.isDirectory())
        {
            labelMsg.setText("Selected dir: " +dir.getAbsolutePath());
            writeDir = dir;
            textFieldDir21.setText(dir.getAbsolutePath());
            buttonWritePdf.setDisable(false);
        }
    } catch (Exception e) {
        e.printStackTrace();
        labelMsg.setText("Error: " +e.getMessage());
    }
    }

    @FXML
    protected void checkBoxPssWdPdfsPressed()
    {
        if (checkBoxPssWdPdfs.isSelected())
            executeType = SEEKTYPES.SEEK_ENCRYPTED_PDF_FILES;
        else
            executeType = SEEKTYPES.SEEK_NORMAL_PDF_FILES;
    }

    @FXML
    protected void buttonSelectBaseDirPressed() {
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Open search pdf directory");
            buttonExec.setDisable(true);
            if (baseDir != null)
                directoryChooser.setInitialDirectory(baseDir);
            else
                directoryChooser.setInitialDirectory(new File("."));
            File dir = directoryChooser.showDialog(mainStage);
            if (dir != null && dir.exists() && dir.isDirectory())
            {
                labelMsg.setText("Selected dir: " +dir.getAbsolutePath());
                baseDir = dir;
                textFieldDir.setText(dir.getAbsolutePath());
                buttonExec.setDisable(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            labelMsg.setText("Error: " +e.getMessage());
        }
    }

    protected String [] removeEndOfLns(String [] arrStr)
    {
        if (arrStr == null)
            return null;
        String [] ret = new String[arrStr.length];
        if (arrStr.length == 0)
            return null;
        int max = arrStr.length;
        List<String> items = new ArrayList<>();
        String strValue;
        for (int i = 0; i < max; i++) {
            strValue = arrStr[i].replaceAll("\\\\n","");
            if (strValue.isEmpty())
                continue;
            items.add(strValue);
        }
        ret = new String[items.size()];
        ret = items.toArray(ret);
        return ret;
    }

    @FXML
    protected void buttonExecPressed()
    {
        if (baseDir != null && baseDir.exists() && baseDir.isDirectory())
        {
            try {
                bExecuted = true;
               setLabelMsg("");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        listPdfs.getItems().clear();
                        processIndicatorExec.setProgress(0.0);
                        buttonCancel.setDisable(false);
                        buttonExec.setDisable(true);
                    }
                });

                processes.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent t) {
                        setLabelMsg("Task done!");
                        System.out.println("done:" /* + t.getSource().getValue() */);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                buttonExec.setDisable(false);
                                buttonCancel.setDisable(true);
                                processIndicatorExec.setProgress(0.0);
                            }
                        });

                        if (bProssesRestarted)
                            return;

                        /*
                        Object arrValue = t.getSource().getValue();
                        if (arrValue != null)
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    listPdfs.getItems().clear();
                                    listPdfs.getItems().addAll(removeEndOfLns(arrValue.toString().split("(\n|$)")
                                         )); // TODO: TARKISTA TAMA!
                                    buttonExec.setDisable(false);
                                    buttonCancel.setDisable(true);
                                }
                            });

                         */
                    }
                });

                /*
                listPdfs.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
                    @Override
                    public ListCell<String> call(ListView<String> list) {
                        return new CellFactory(true, listResult);
                    }
                });
*/

                processes.setOnFailed(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent t) {
                        System.out.println("Task failed:" /* + t.getSource().getValue() */);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                buttonExec.setDisable(false);
                                buttonCancel.setDisable(true);
                                processIndicatorExec.setProgress(0.0);
                            }
                        });

                        if (bProssesRestarted)
                            return;
                        setLabelMsg("Task failed!");
                        /*
                        listPdfs.getItems().clear();
                        Object arrValue = t.getSource().getValue();
                        if (arrValue != null)
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                listPdfs.getItems().addAll(arrValue.toString()); // TODO: TARKISTA TAMA!
                                buttonExec.setDisable(false);
                                buttonCancel.setDisable(true);
                            }
                        });
                    */
                        bExecuted = false;
                    }
                });

                processes.setOnCancelled(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent t) {
                        setLabelMsg("Task canceled!");
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                buttonExec.setDisable(false);
                                buttonCancel.setDisable(true);
                                processIndicatorExec.setProgress(0.0);
                            }
                        });
                        System.out.println("canceled:" /* + t.getSource().getValue() */);
                        if (bProssesRestarted)
                            return;
                        /*
                        listPdfs.getItems().clear();
                        Object arrValue = t.getSource().getValue();
                        if (arrValue != null)
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                listPdfs.getItems().addAll(arrValue.toString()/); // TODO: TARKISTA TAMA!
                                buttonExec.setDisable(false);
                                buttonCancel.setDisable(true);
                            }
                        });
                        */
                        bExecuted = false;
                    }
                });

                this.processes.setExecutionData(baseDir.getAbsolutePath(), executeType,
                        checkBoxReadAllSubDirs.isSelected());
                this.setLabelMsg("Executing...");
                this.bExecuted = true;
                // test buttons: Thread.sleep(10000);
                if (this.processes.getState() != Worker.State.READY) {
                    this.bProssesRestarted = true; // this boolean variable into true, because to prevent
                    // of extra call below:
                    this.processes.restart(); // restart causes an extra call into onsuccess etc
                    bProssesRestarted = false; // after an extra handler catt, after this can do "normal" handler call
                }
                else
                    this.processes.start();
            } catch (Exception e) {
                labelMsg.setText(e.getMessage());
            }
        }
    }

    @FXML
    private void cancelExectionPressed()
    {
        System.out.println("cancelExection");
        processes.cancelProcess();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                buttonCancel.setDisable(true);
                buttonExec.setDisable(false);
            }
        });

        this.bExecuted = false;
    }

    private void setLabelMsg(String str)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                labelMsg.setText(str);
            }
        });
    }


    private class SeekPdfFilesProcesses2 extends Service<String> {
//        private static final String NEWLINE = System.getProperty("line.separator");

        private int exitValue = -1;
        //  private StringBuilder result;
        private StringBuilder resultError;
        private String m_strWorkingDir = null;
        private SEEKTYPES seektype = null;
        private String strMessage = null;
        private volatile TaskExecuted executed = TaskExecuted.NOT_STARTED_YET;
        public TaskExecuted getExecuted() { return executed; }
        public StringBuilder sbFiles = null;
        public String getSBFiles() { return sbFiles == null ? null : sbFiles.toString(); }
        private StringProperty result = new SimpleStringProperty();
        private boolean bReadAllSubDirs = false;

        public final void setResult(String value) {
            result.set(value);
        }

        public final String getResult() {
            return result.get();
        }

        public final StringProperty resultProperty() {
            return url;
        }

        public String getStrMessage() { return this.strMessage; }
        public void setExecutionData(String p_strWorkingDir, SEEKTYPES p_seektypes,
                                     boolean p_bReadAllSubDirs)
        {
            this.m_strWorkingDir = p_strWorkingDir;
            this.seektype = p_seektypes;
            this.bReadAllSubDirs = p_bReadAllSubDirs;
        }

        public int getExitValue() { return exitValue; }
        public String getResultString() {
            if (result == null)
                return null;
            return result.toString();
        }

        public String getErrorString() {
            if (resultError == null)
                return null;
            return resultError.toString();
        }

        public boolean cancelProcess()
        {
            if (this.executed != TaskExecuted.HAS_BEEN_STARTED)
                return false;
            if (isRunning())
                if (cancelProcess()) {
                    this.executed = TaskExecuted.USER_CANCELED;
                    return true;
                }
            return false;
        }

        /**
         * @param command the command to run
         * @return the output of the command
         * @throws IOException if an I/O error occurs
         */
        protected final String runSeekPassWordPdfFiles(String strWorkingDir, SEEKTYPES strCommand,
                                                       boolean bReadAllSubDirs)
                throws IOException, InterruptedException
        {
            strMessage = null;
            this.exitValue = -1;
            result.setValue("");
            StringBuffer sb = new StringBuffer();
            resultError = new StringBuilder(80);
            try {
                File baseDir = new File(m_strWorkingDir);
                if (!baseDir.exists())
                {
                    this.executed = TaskExecuted.ERROR;
                    this.strMessage = "" + m_strWorkingDir +" does not exists!";
                    cancelProcess();
                    return null;
                }
                if (baseDir.isFile())
                {
                    this.executed = TaskExecuted.ERROR;
                    this.strMessage = "" + m_strWorkingDir +" is a file!";
                    cancelProcess();
                    return null;
                }
                if (!baseDir.canRead())
                {
                    this.executed = TaskExecuted.ERROR;
                    this.strMessage = "" + m_strWorkingDir +" cannot to read!";
                    cancelProcess();
                    return null;
                }

                List<String> files = findFiles(Paths.get(baseDir.getAbsolutePath()),
                        "pdf", bReadAllSubDirs);
                //  files.forEach(x -> System.out.println(x));

                exitValue = 0;

                System.out.println("exitValue: " +exitValue);
                // exitValue = process.exitValue();
                System.out.println("Done.");
                if (files != null && !files.isEmpty()) {
                    int max = files.size();
                    for (int i = 0; i < max; i++) {
                        sb.append(files.get(i)+"\n");
                    }
                    result.setValue(sb.toString());
                }
                buttonCancel.setDisable(true);
                buttonExec.setDisable(false);
            }
            catch (IOException err) {
                err.printStackTrace();
                exitValue = -1;
                strMessage = err.getMessage();
                throw err;
            }
            return sb.toString();
        }

        /**
         * Prevent construction.
         */
        public SeekPdfFilesProcesses2()
        {
            super();
        }

        private StringProperty url = new SimpleStringProperty();
        public final String getWorkingDir() {
            return m_strWorkingDir;
        }
        public final SEEKTYPES getExecuteType() {
            return this.seektype;
        }

        public final StringProperty urlProperty() {
            return url;
        }

        protected Task<String> createTask() {
            final String _workingdir = getWorkingDir();
            final SEEKTYPES _strExecution = getExecuteType();
            return new Task<String>() {
                protected String call()
                        throws IOException, InterruptedException {
                    //           try {
                    // if (_strExecution == SEEKTYPES.SEEK_ENCRYPTED_PDF_FILES)
                    return runSeekPassWordPdfFiles(_workingdir, _strExecution, bReadAllSubDirs);
                    //  return null;
                }
            };
        }

        @Override protected void succeeded() {
            super.succeeded();
            this.executed = TaskExecuted.SUCCEED;
            this.strMessage = "Done!";
        }

        @Override protected void cancelled() {
            super.cancelled();
            this.strMessage = "Cancelled!";
            this.executed = TaskExecuted.USER_CANCELED;
        }

        public List<String> findFilesFromSubDirs(Path path, String fileExtension, boolean seekAllSubDirs)
                throws IOException {

            if (!Files.isDirectory(path)) {
                throw new IllegalArgumentException("Path must be a directory!");
            }

            List<String> result = new ArrayList<>();
            List<String> resultSub;

            File dir = path.toFile();
            File [] files = dir.listFiles();
            int max = files.length;
            File f;
            for (int i = 0; i < max ; i++) {
                f = files[i];
                if (f == null || f.isFile() || !f.exists())
                    continue;
                resultSub = findFiles(Paths.get(f.getAbsolutePath()), fileExtension, seekAllSubDirs);
                if (resultSub == null || !resultSub.isEmpty())
                    continue;
                result.addAll(resultSub);
            }
            if (result.isEmpty())
                result = null;

            return result;
        }

        public List<String> findFiles(Path path, String fileExtension, boolean seekAllSubDirs)
                throws IOException {

            if (!Files.isDirectory(path)) {
                throw new IllegalArgumentException("Path must be a directory!");
            }

            List<String> result = new ArrayList<>();
            File dir = path.toFile();
            File [] files = dir.listFiles();
            File f;
            try {
                int max = files.length;
                for (int i = 0; i < max ; i++) {
                    f = files[i];
                    if (f == null)
                        continue;
                    if (!f.exists())
                        continue;
                    if (!f.isFile())
                        continue;
                    if (f.getName().toLowerCase().endsWith((fileExtension))
                            && (seektype == SEEKTYPES.SEEK_NORMAL_PDF_FILES
                            || (seektype == SEEKTYPES.SEEK_ENCRYPTED_PDF_FILES &&
                            isEncryptedPdfFile(f)) )) {
                        result.add(f.getAbsolutePath() + "\n");
                        // listResultItems.add(f.getAbsolutePath());
                        final File f2 = f;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                listPdfs.getItems().add(f2.getAbsolutePath());
                                double indProcess = processIndicatorExec.getProgress();
                                if (indProcess > 0.98)
                                    indProcess = 0.10;
                                processIndicatorExec.setProgress(indProcess+0.05);
                            }
                        });
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        /*
        try (Stream<Path> walk = Files.walk(path)) {
            result = walk
                    .filter(p -> !Files.isDirectory(p))
                    // this is a path, not string,
                    // this only test if path end with a certain path
                    //.filter(p -> p.endsWith(fileExtension))
                    // convert path to string first
                    .map(Path::toString)
                    .filter(f -> f.toLowerCase().endsWith(fileExtension) &&
                            seektype != SEEKTYPES.SEEK_ENCRYPTED_PDF_FILES ||
                            isEncryptedPdfFile(new File(f)) )
                    .collect(Collectors.toList());
        }
         */

            if (seekAllSubDirs)
            {
                List<String> resultSub;
                resultSub = findFilesFromSubDirs(path, fileExtension, seekAllSubDirs);
                if (resultSub != null && !resultSub.isEmpty())
                    result.addAll(resultSub);
            }
            if (result.isEmpty())
                result = null;

            return result;
        }

        public boolean isEncryptedPdfFile(File f)
        {
            boolean isProtected = PdfDocument.isPasswordProtected(f.getAbsolutePath());

            /*
                            var file = Objects.requireNonNull(this.getClass().getResource(TEST_SECRET_PDF)).getFile();
            assertThrowsExactly(InvalidPasswordException.class, () ->
                    SecretDocument.readAndCopySecretFile(file)

             */
            if (isProtected && checkBoxReallyPssWdPdfs.isSelected()) {
                try {
                    PDDocument document = Loader.loadPDF(f);
                } catch (IOException ioe /* InvalidPasswordException ipe */) {
                    return true;
                }
                return false;
            }
            return isProtected;

        /*
        Boolean isEncrypted = Boolean.FALSE;
        byte[] byteArray;
        String pdfContent;
        int lastTrailerIndex;
        try {
            byteArray = Files.readAllBytes(Paths.get(f.getAbsolutePath()));
            //Convert the binary bytes to String. Caution, it can result in loss of data. But for our purposes, we are simply interested in the String portion of the binary pdf data. So we should be fine.
            pdfContent = new String(byteArray);
            lastTrailerIndex = pdfContent.lastIndexOf("trailer");
            if(lastTrailerIndex >= 0 && lastTrailerIndex < pdfContent.length()) {
                String newString =  pdfContent.substring(lastTrailerIndex, pdfContent.length());
                int firstEOFIndex = newString.indexOf("%%EOF");
                if (firstEOFIndex == -1)
                    return false;
                String trailer = newString.substring(0, firstEOFIndex);
                if(trailer.contains("/Encrypt"))
                    isEncrypted = Boolean.TRUE;
            }
        }
        catch (NoSuchFileException suchFileException){
            suchFileException.printStackTrace();
            resultError.append("No such file: " +f.getAbsolutePath() +"\n");
            return false;
        }
        catch(Exception e) {
            e.printStackTrace();
            resultError.append(f.getAbsolutePath() +": " +e.getMessage());
            return false;
            //Do nothing
        }
        return isEncrypted;
         */
        }
    }

}