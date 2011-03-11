/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openxdata.server.serializer;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openxdata.server.util.XformUtil;

/**
 * Provides the default xform serialization and deserialization from and to the server.
 * An example of such clients could be mobile devices collecting data in for instance
 * offline mode, and then send it to the server when connected.
 *
 * For those who want a different serialization format for xforms,
 * just implement the SerializableData interface and specify the class
 * using the settings {xforms.xformSerializer}.
 * The jar containing this class can then be
 * put under the webapps/openxdata/web-inf/lib folder.
 * One of the reasons one could want a different serialization format
 * is for performance by doing a more optimized and compact format. Such an example
 * exists in the EpiHandy compact implementation of xforms.
 *
 * @author Mark
 * @modified hbuletwenda
 *
 */
public class JavaRosaXformSerializer implements XformSerializer, StudySerializer, UserSerializer {

    public JavaRosaXformSerializer() {
    }

    @SuppressWarnings("unchecked")
    public void serializeForms(OutputStream os, List<String> xforms, Integer studyId, String studyName, String studyKey) {

        try {

            BufferedOutputStream dos = new BufferedOutputStream(os);

            //String xhtml = org.openxdata.server.serializer.Util.XformUtil.fromXform2Xhtml(xforms.get(0), null);
            //dos.write(xhtml.getBytes());

            for (String xml : xforms) {

                String xhtml = org.openxdata.server.serializer.Util.XformUtil.fromXform2Xhtml(xforms.get(0), null);
                dos.write(xhtml.trim().getBytes());

                System.out.println("---------------------------------------------------------------------");
                System.out.println(org.openxdata.server.serializer.Util.XformUtil.getXSLT1());
                System.out.println("----------------------------654321-----------------------------------");
                System.out.println(xml);
                System.out.println("----------------------------123456-----------------------------------");
                System.out.println(xhtml);
                System.out.println("---------------------------------------------------------------------");
                //break;
            } 

            dos.flush();
            dos.close();

        } catch (Throwable ex) {

            throw new UnsupportedOperationException(ex);

        }

    }

    @SuppressWarnings("unchecked")
    public List<String> deSerialize(InputStream is, Map<Integer, String> map) {

        List<String> forms = new ArrayList<String>();
        /*
        int len = dis.readByte();
        for (int i = 0; i < len; i++) {
        forms.add(dis.readUTF());
        }
         */
        return forms;

    }

    @SuppressWarnings("unchecked")
    public void serializeSuccess(OutputStream os) {

        try {

            String str = "The data Has been successfully submitted to the server. Cross check with the server admin to clarify";

            BufferedOutputStream dos = new BufferedOutputStream(os);

            dos.write(str.getBytes());
            dos.flush();
            dos.close();

        } catch (IOException ex) {

            throw new UnsupportedOperationException(ex);

        }

    }

    @SuppressWarnings("unchecked")
    public void serializeAccessDenied(OutputStream os) {

        try {

            BufferedOutputStream dos = new BufferedOutputStream(os);

            String str = "<HTML><HEAD><TITLE>Data Submission Status</TITLE>"
                    + "</HEAD><BODY>Form Submitted Successfully!</BODY></HTML>";

            dos.write(str.getBytes());
            dos.flush();
            dos.close();

        } catch (IOException ex) {

            throw new UnsupportedOperationException(ex);

        }

    }

    @SuppressWarnings("unchecked")
    public void serializeFailure(OutputStream os, Exception ex) {

        try {

            BufferedOutputStream dos = new BufferedOutputStream(os);

            dos.write(ex.toString().getBytes());
            dos.flush();
            dos.close();

        } catch (IOException ex1) {

            throw new UnsupportedOperationException(ex1);

        }

    }

    @SuppressWarnings("unchecked")
    public void serializeStudies(OutputStream os, Object data) {

        try {

            BufferedOutputStream dos = new BufferedOutputStream(os);
           
            List<Object[]> studies = (List<Object[]>) data;

            String xml = "<?xml version='1.0'?><forms> ";
            for (Object[] study : studies) {

                int studyId = (Integer) study[0];
                String studyName = (String) study[1];

                xml += "<form url='http://localhost:8888/OpenXDataServerAdmin/formdownloadservlet?action=downloadforms&amp;uname=admin&amp;pw=openxdata&amp;formser=JRXformSerializer&amp;studyId=" + studyId + "'>" + studyName + "</form>";

            }
            xml += "</forms>";

            //dos.writeUTF(xml.trim());
            dos.write(xml.trim().getBytes());
            dos.flush();
            dos.close();
            //System.out.println(xml);

        } catch (IOException ex1) {

            throw new UnsupportedOperationException(ex1);

        }

    }

    @SuppressWarnings("unchecked")
    public void serializeUsers(OutputStream os, Object data) {

        try {

            DataOutputStream dos = new DataOutputStream(os);

            List<Object[]> users = (List<Object[]>) data;

            dos.writeByte(users.size());
            for (Object[] user : users) {

                dos.writeInt((Integer) user[0]);
                dos.writeUTF((String) user[1]);
                dos.writeUTF((String) user[2]);
                dos.writeUTF((String) user[3]);

            }
            dos.flush();
            dos.close();

        } catch (IOException ex1) {

            throw new UnsupportedOperationException(ex1);

        }

    }
}
