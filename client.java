/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.net.*;
import java.io.*;
/**
 *
 * @author USER
 */
public class Client {
/**
     * @param args the command line arguments
     */
    public static String sockaddr = "10.151.40.123";
    public static void main(String args[]) throws Exception
    {
        
        Socket soc=new Socket(sockaddr ,4444);
        transferfileClient t=new transferfileClient(soc);
        t.displayMenu();
        
    }
    
}
class transferfileClient
{
    Socket ClientSoc;

    DataInputStream din;
    DataOutputStream dout;
    BufferedReader br;
    transferfileClient(Socket soc)
    {
        try
        {
            ClientSoc=soc;
            din=new DataInputStream(ClientSoc.getInputStream());
            dout=new DataOutputStream(ClientSoc.getOutputStream());
            br=new BufferedReader(new InputStreamReader(System.in));
        }
        catch(Exception ex)
        {
        }        
    }
    void SendFile() throws Exception
    {        
        
        String filename;
        System.out.print("Masukkan Nama File:");
        filename=br.readLine();
            
        File f=new File(filename);
        if(!f.exists())
        {
            System.out.println("File Tidak Ada");
            dout.writeUTF("Tidak Ada");
            return;
        }
        
        dout.writeUTF(filename);
        
        String msgFromServer=din.readUTF();
        if(msgFromServer.compareTo("File Exists")==0)
        {
            String Option;
            System.out.println("OverWrite (Y/N) ?");
            Option=br.readLine();            
            if(Option=="Y")    
            {
                dout.writeUTF("Y");
            }
            else
            {
                dout.writeUTF("N");
                return;
            }
        }
        
        System.out.println("Mengirim File");
        FileInputStream fin=new FileInputStream(f);
        int ch;
        do
        {
            ch=fin.read();
            dout.writeUTF(String.valueOf(ch));
        }
        while(ch!=-1);
        fin.close();
        System.out.println(din.readUTF());
        
    }
    
    void ReceiveFile() throws Exception
    {
        String fileName;
        System.out.print("Masukkan Nama File:");
        fileName=br.readLine();
        dout.writeUTF(fileName);
        String msgFromServer=din.readUTF();
        
        if(msgFromServer.compareTo("Tidak Ada")==0)
        {
            System.out.println("File Tidak Ditemukan");
            return;
        }
        else if(msgFromServer.compareTo("READY")==0)
        {
            System.out.println("Menerima File");
            File f=new File(fileName);
            if(f.exists())
            {
                String Option;
                System.out.println("OverWrite (Y/N) ?");
                Option=br.readLine();            
                if(Option=="N")    
                {
                    dout.flush();
                    return;    
                }                
            }
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
            System.out.println(din.readUTF());
                
        }
    }
    
    public void transfile() throws Exception
    {
        String filename = din.readUTF();
        
            File f=new File(filename);
        
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
            System.out.println(din.readUTF());
        }
        

    public void displayMenu() throws Exception
    {
        while(true)
        {    
            System.out.println("[ MENU ]");
            System.out.println("1. Send File");
            System.out.println("2. Receive File");
            System.out.println("3. Idle");
            System.out.println("4. Exit");
            int choice;
            choice=Integer.parseInt(br.readLine());
            if(choice==1)
            {
                dout.writeUTF("SEND");
                System.out.println("Masukkan IP Tujuan");
                String IP = br.readLine();
                dout.writeUTF(IP);
                SendFile();
            }
            else if(choice==2)
            {
                dout.writeUTF("GET");
                ReceiveFile();
            }
            
            else if(choice==3)
            {
                transfile();
            }
            
            else
            {
                dout.writeUTF("DISCONNECT");
                ClientSoc.close();
                din.close();
                dout.close();
                
                return;
            }
        }
    }
}
