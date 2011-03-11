/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main.old;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 *
 * @author Administrator
 */
public class BPelTest
{

    public static void main(String[] args) throws FileNotFoundException, IOException
    {
        InputStream fis = new FileInputStream("hellotest.xml");
        BufferedReader bis = new BufferedReader(new InputStreamReader(fis));
        String read = null;
        StringBuffer bf = new StringBuffer();

        PrintStream sout = System.out;
        while ((read = bis.readLine()) != null)
        {
            bf.append(read).append('\n');
        }

        System.out.println(bf);
        final Socket socket = new Socket("127.0.0.1", 9001);

        DataOutputStream out =
                new DataOutputStream(socket.getOutputStream());

        out.writeUTF(bf.toString());

        DataInputStream din = new DataInputStream(socket.getInputStream());
        String readUTF = din.readUTF();

        sout.println(readUTF);

        out.close();
        din.close();
        fis.close();
        
    }
}
