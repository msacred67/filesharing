/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.net.*;
import java.io.*;
import java.util.*;
/**
 *
 * @author USER
 */
public class Server {

    //public static String[] clientlist;
    //public static int i = 0;
    public static LinkedList clients = new LinkedList();
    public static ListIterator<transferfile> iterator = clients.listIterator();
    public static void main(String args[]) throws Exception
    {
        ServerSocket soc=new ServerSocket(4444);
        System.out.println(soc.getLocalPort());
        while(true)
        {
            System.out.println("Menunggu Panggilan");
            transferfile t=new transferfile(soc.accept()); 
            clients.add(t);
        }
    }
}

class transferfile extends Thread
{
    String filet;
    Socket ClientSoc;

    DataInputStream din;
    DataOutputStream dout;
    
    transferfile(Socket soc)
    {
        try
        {
            ClientSoc=soc;                        
            din=new DataInputStream(ClientSoc.getInputStream());
            dout=new DataOutputStream(ClientSoc.getOutputStream());
            
            System.out.println(soc.getInetAddress() + " telah masuk");
            //InetAddress address = InetAddress.getLocalHost();
           // FTPServer.clientlist[i] = address.getHostAddress();
           // System.out.println(address.getHostAddress());
            //i++;
            start();
            
        }
        catch(Exception ex)
        {
        }        
    }
    
    String GetIp() throws Exception
    {
        InetAddress address = InetAddress.getLocalHost();
        return address.getHostAddress();
    }
    void SendFile() throws Exception
    {        
        String filename=din.readUTF();
        File f=new File(filename);
        if(!f.exists())
        {
            dout.writeUTF("Tidak Ada");
            return;
        }
        else
        {
            dout.writeUTF("READY");
            FileInputStream fin=new FileInputStream(f);
            int ch;
            do
            {
                ch=fin.read();
                dout.writeUTF(String.valueOf(ch));
            }
            while(ch!=-1);    
            fin.close();    
            dout.writeUTF("File Receive Successfully");                            
        }
    }
    
    void ReceiveFile() throws Exception
    {
        String filename=din.readUTF();
        filet = filename;
        if(filename.compareTo("Tidak Ada")==0)
        {
            return;
        }
        File f=new File(filename);
        String option;
        
        if(f.exists())
        {
            dout.writeUTF("File Exists");
            option=din.readUTF();
        }
        else
        {
            dout.writeUTF("SendFile");
            option="Y";
        }
            
            if(option.compareTo("Y")==0)
            {
                FileOutputStream fout=new FileOutputStream(f);
                int ch;
                String temp;
                do
                {
                    temp=din.readUTF();
                    ch=Integer.parseInt(temp);
                    if(ch!=-1)
                    {
                        fout.write(ch);                    
                    }
                }while(ch!=-1);
                fout.close();
                dout.writeUTF("File Send Successfully");
            }
            else
            {
                return;
            }
            
    }

    public void transffile() throws Exception
    {
        String filename=filet;
        File f=new File(filename);
        dout.writeUTF(filet);
        
            FileInputStream fin=new FileInputStream(f);
            int ch;
            do
            {
                ch=fin.read();
                dout.writeUTF(String.valueOf(ch));
            }
            while(ch!=-1);    
            fin.close();    
            dout.writeUTF("File Receive Successfully");   
    }

    public void run()
    {
        while(true)
        {
            try
            {
            String Command=din.readUTF();
            if(Command.compareTo("GET")==0)
            {
                System.out.println("\tGET Command");
                SendFile();
                continue;
            }
            else if(Command.compareTo("SEND")==0)
            {
                System.out.println("\tSEND Command");   
                String IPT = din.readUTF();
                ReceiveFile();
                while(Server.iterator.hasNext())
                {
                    transferfile x = Server.iterator.next();
                    if(x.GetIp().equals(IPT))
                    {
                        x.transffile();
                    }
                    Server.iterator = (ListIterator<transferfile>) Server.iterator.next();
                }
                continue;
            }
            
            else if(Command.compareTo("DISCONNECT")==0)
            {
                System.out.println("\tDisconnect Command");
                ClientSoc.close();
                din.close();
                dout.close();
                break;
            }
            }
            catch(Exception ex)
            {
            }
        }
    }
}
