package com.metait.javafxseekpdffiles;

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

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import com.spire.pdf.PdfDocument;

public class SeekPdfFilesProcesses extends Service<String> {
    private static final String NEWLINE = System.getProperty("line.separator");

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
        OutputStream stdin = null;
        InputStream stderr = null;
        InputStream stdout = null;
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
    public SeekPdfFilesProcesses()
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
                       isEncryptedPdfFile(f)) ))
                   result.add(f.getAbsolutePath()+"\n");
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

    public static String getClassPathSeparator()
    {
        return (File.separatorChar == '\\' ? ";": ":");
    }

    public static String correctSpaceContainsClassPath(String classpath)
    {
        if (classpath == null)
            return classpath;
        if (classpath.trim().length() == 0)
            return classpath;
        if (!classpath.contains(" "))
            return classpath;
        int ind = classpath.indexOf(' ');
        if (ind == -1)
            return classpath;
        String classPathSeparator = (File.separatorChar == '\\' ? ";" : ":");
        int indclassPathSeparator = classpath.indexOf(classPathSeparator);
        if (indclassPathSeparator == -1)
            return classPathSeparator + SeekPdfFilesProcesses.getCorrectedOneClassPath(classpath) +classPathSeparator;

        String regexSeparator = "(" + classPathSeparator +"|$)";
        String [] arrClassPath = classpath.split(regexSeparator);
        if (arrClassPath == null || arrClassPath.length == 0)
            return classpath;
        StringBuffer sb = new StringBuffer();
        for(String oneCPath : arrClassPath)
        {
            if (oneCPath == null || oneCPath.trim().length() == 0)
                continue;
            sb.append(classPathSeparator +getCorrectedOneClassPath(oneCPath));
        }
        String ret = sb.toString();
        return ret;
    }

    public static String getCorrectedOneClassPath(String oneClassPathValue)
    {
        if (oneClassPathValue == null)
            return oneClassPathValue;
        if (oneClassPathValue.trim().length() == 0)
            return oneClassPathValue.trim();
        int ind = oneClassPathValue.indexOf(' ');
        if (ind == -1)
            return oneClassPathValue;
        return "\"" +oneClassPathValue +"\"";
    }
}
