package com.MyWeChat.Model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhang on 17-5-19.
 */
public class Pack {
    public  static final int headLength=14;
    short id;
    int seq, Alength,Clength;
    final byte[] addition;
    final byte[] content;
    HashMap<String,String> additionMap=new HashMap<>();

    public Pack(short id, int seq, int alength, int clength, byte[] addition, byte[] content) {
        this.id = id;
        this.seq = seq;
        Alength = alength;
        Clength = clength;

        if (addition==null)
            this.addition=new byte[0];
        else
            this.addition = addition;

        if (content==null)
            this.content=new byte[0];
        else
            this.content = content;

        init();
    }

    public Pack(short id,HashMap<String,String> addition){
        this.id=id;

        String addi="";
        for (Map.Entry<String,String> entry:addition.entrySet()){
            String temp=entry.getKey()+":"+entry.getValue()+";";
            addi+=temp;
        }

        this.addition=addi.getBytes();
        Alength=this.addition.length;
        content=new byte[0];
    }

    public Pack(short id,HashMap<String,String> addition,byte[] content){
        this.id=id;

        String addi="";
        for (Map.Entry<String,String> entry:addition.entrySet()){
            String temp=entry.getKey()+":"+entry.getValue()+";";
            addi+=temp;
        }

        this.addition=addi.getBytes();
        Alength=this.addition.length;
        this.content= Arrays.copyOf(content,content.length);
        Clength=content.length;
    }


    public Pack(InputStream inputStream) throws IOException {
        byte []bytes=new byte[2048];
        inputStream.read(bytes,0,14);
        short id=(short)((bytes[0]<<8)|(bytes[1]&0xFF));


        int seq=getInt(bytes,2+0);
        int Alength=getInt(bytes,2+4);
        int Clength=getInt(bytes,2+8);


        byte[] addition=new byte[Alength];
        byte[] content=new byte[Clength];

        int len=0;
        while (len<Alength){
            int read=inputStream.read(addition,len,Alength-len);
            len+=read;

        }

        len=0;
        while (len<Clength){
            int read=inputStream.read(content,len,Clength-len);
            len+=read;
        }

        this.id = id;
        this.seq = seq;
        this.Alength = Alength;
        this.Clength = Alength;

        if (addition==null)
            this.addition=new byte[0];
        else
            this.addition = addition;

        if (content==null)
            this.content=new byte[0];
        else
            this.content = content;

        init();


    }

    private void init(){
        String temp=new String(addition);
        String []sArray=temp.split(";");
        for (String s:sArray){
            String []t=s.split(":");
            if (t.length>1)
                additionMap.put(t[0],t[1]);
        }

    }

    public static int getHeadLength() {
        return headLength;
    }

    public short getId() {
        return id;
    }

    public int getSeq() {
        return seq;
    }

    public int getAlength() {
        return Alength;
    }

    public int getClength() {
        return Clength;
    }


    public byte[] getContent() {
        return content;
    }

    public HashMap<String, String> getAdditionMap() {
        return additionMap;
    }
    public String getValue(String name){
        return additionMap.get(name);
    }

    public byte[] toBytes(){
        byte []bytes=new byte[headLength+addition.length+content.length];
        byte[][] bb=new byte[6][];

        bb[0]=shortToBytes(id);
        bb[1]=IntToBytes(seq);
        bb[2]=IntToBytes(Alength);
        bb[3]=IntToBytes(Clength);
        bb[4]=addition;
        bb[5]=content;
        int number=0;

        for (int i=0;i<bb.length;i++){
            for (int j=0;j<bb[i].length;j++){
                bytes[number++]=bb[i][j];
            }
        }

        return bytes;

    }

    private byte[] IntToBytes(int in){
        byte[] b=new byte[4];
        for (int i=3;in!=0;i--){
            b[i]=(byte)in;
            in=in>>8;
        }
        return b;

    }

    private byte[] shortToBytes(short in){
        byte[] b=new byte[2];
        for (int i=1;in!=0;i--){
            b[i]=(byte)(in&0x0FF);
            in=(short) (in>>8);
        }
        return b;

    }
    int getInt(byte []bytes,int off){
        if (bytes.length-off<4) {
            System.out.println("ERROR");
            return 0;
        }
        int ret=0;
        for (int i=0;i<4;i++){
            ret=(ret<<8)|bytes[off+i];
        }
        return ret;
    }


}
