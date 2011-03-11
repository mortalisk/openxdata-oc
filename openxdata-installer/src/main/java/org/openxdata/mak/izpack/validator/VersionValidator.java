package org.openxdata.mak.izpack.validator;

import com.izforge.izpack.installer.AutomatedInstallData;
import com.izforge.izpack.installer.DataValidator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 *
 * @author Ronald.K
 */
public class VersionValidator implements DataValidator
{
    public static final String VERSION_READ_ERROR_MSG = "Installer Is Not Able To Read Version Info of OpenXData.\n" + "This May Lead to a Downgrade or Update!!!!!\n" + "Do You Wish to Continue?";
    String error;
    PrintStream out = System.out;

    public Status validateData(AutomatedInstallData aData)
    {
        out.println("=======VALIDATING UPDATE DATA===========");
        Status status = Status.ERROR;
        try {

            String version = aData.getVariable("installed.dir");
            File f = new File(version + "/version");

            out.println(f.getAbsolutePath());

            String instRev = readInstalledRevision(f);


            if (instRev == null) {
                status = Status.WARNING;
                error = VERSION_READ_ERROR_MSG;

            } else {
                String updateRev = readUpdateVersion();

                out.println("This version: " + updateRev + "\n"
                        + "Installed Version: " + instRev);

                try {
                    int instNum = Integer.parseInt(instRev.replace(".", ""));
                    int upRev = Integer.parseInt(updateRev.replace(".", ""));
                    if (instNum < upRev) {
                        status = Status.OK;
                    } else if (instNum == upRev) {
                        error = "The Update Version is Equal to the Current Version";
                    } else {
                        error = "The Update Version is Lower than the Current Version";
                    }
                } catch (NumberFormatException ex) {
                    error = "Updater experienced an error while reading the versions.This may"
                            + "Lead to an ugrade or risk of downgrade.Do you wish to continue?";
                    status = Status.WARNING;
                }
            }
        } catch (FileNotFoundException ex) {
            error = "The Updater Cannot Continue Because it Has Experienced an Error";
            status = Status.ERROR;
            ex.printStackTrace();
        } catch (IOException ex) {
            error = ex.getMessage();
            ex.printStackTrace();
            status = Status.ERROR;
        } catch (Exception e) {
            error = "The Got an enxpected error(" + e.getMessage() + ")";
            status = Status.ERROR;
            e.printStackTrace();

        }
        if (status == Status.ERROR) {
            JOptionPane.showMessageDialog(null, error, "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        return status;
    }

    public String getErrorMessageId()
    {
        return error;
    }

    public String getWarningMessageId()
    {
        return error;
    }

    public boolean getDefaultAnswer()
    {
        return true;
    }

    private String readInstalledRevision(File f)
    {

        String vs = null;
        try {
            Properties p = new Properties();
            FileReader fr = new FileReader(f);
            p.load(fr);

            vs = p.getProperty("version");
            fr.close();
        } catch (Exception ex) {
        }
        return getRevisionFromString(vs);
    }

    private String readUpdateVersion() throws FileNotFoundException, IOException
    {
        String vs = null;
        Properties p = new Properties();
        InputStream inStream = getClass().getResourceAsStream("/version");
        p.load(inStream);

        vs = p.getProperty("version");
        inStream.close();
        return getRevisionFromString(vs);
    }

    private static String getRevisionFromString(String revision)
    {
        try{
            Integer.parseInt(revision);
            return revision;
        }catch(Exception e){
        }
        int lastIndexOf = revision.lastIndexOf('r');
        return revision.substring(lastIndexOf+1).trim();
    }

    public static void main(String[] args)
    {
        String revisionFromString = getRevisionFromString("OpenXData-Install-1.2-SNAPSHOT.r3077");
        System.out.println(revisionFromString);
    }
}
