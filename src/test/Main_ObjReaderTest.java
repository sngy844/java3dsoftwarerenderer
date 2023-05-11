package test;

import swrast.GfxMath;
import swrast.RenderContext;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main_ObjReaderTest {
    public static void main(String[] args) {
        List<Float> vertices =  new ArrayList<>();
        List<Float> textCoords =  new ArrayList<>();
        List<Float> vertNormals =  new ArrayList<>();

        List<Integer> faceIndices = new ArrayList<>();

        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader("res/f22.obj"));
            String line = reader.readLine();

            while (line != null) {
                String [] splitLine = line.split(" ");
                if(splitLine.length >0) {
                    boolean isVertex = splitLine[0].equals("v");
                    boolean isTextCoord = splitLine[0].equals("vt");
                    boolean isNormal = splitLine[0].equals("vn");
                    boolean isFace = splitLine[0].equals("f");

                    if(isVertex){
                        vertices.add(Float.parseFloat(splitLine[1]));
                        vertices.add(Float.parseFloat(splitLine[2]));
                        vertices.add(Float.parseFloat(splitLine[3]));
                    }
                    if(isTextCoord){
                        textCoords.add(Float.parseFloat(splitLine[1]));
                        textCoords.add(Float.parseFloat(splitLine[2]));
                    }
                    if(isNormal){
                        vertNormals.add(Float.parseFloat(splitLine[1]));
                        vertNormals.add(Float.parseFloat(splitLine[2]));
                        vertNormals.add(Float.parseFloat(splitLine[3]));
                    }
                    if(isFace){
                        for(int i =1 ; i <= 3 ; i++) {
                            String[] xVxTxN =   splitLine[i].split("/");
                            faceIndices.add(Integer.parseInt(xVxTxN[0]) );
                            faceIndices.add(Integer.parseInt(xVxTxN[1]) );
                            faceIndices.add(Integer.parseInt(xVxTxN[2]) );
                        }
                    }

                }
                // read next line
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Find min find max - probably do it during the read in file
//        float minX = Float.MAX_VALUE; float minY = Float.MAX_VALUE; float minZ = Float.MAX_VALUE;
//        float maxX = Float.MIN_VALUE; float maxY = Float.MIN_VALUE; float maxZ = Float.MIN_VALUE;
//        for(int i =0 ; i< vertices.size();i+=3){
//            if(minX > vertices.get(i)) minX =vertices.get(i);
//            if(maxX < vertices.get(i)) maxX =vertices.get(i);
//
//            if(minY > vertices.get(i+1)) minY =vertices.get(i+1);
//            if(maxY < vertices.get(i+1)) maxY =vertices.get(i+1);
//
//            if(minZ > vertices.get(i+2)) minZ =vertices.get(i+2);
//            if(maxZ < vertices.get(i+2)) maxZ =vertices.get(i+2);
//        }
//        //Normalize X,Y,Z
//        for(int i =0 ; i< vertices.size();i+=3) {
//            vertices.set(i,  1.0f + (vertices.get(i) - minX)/(maxX-minX));
//            vertices.set(i+1,1.0f + (vertices.get(i+1) - minY)/(maxY-minY));
//            vertices.set(i+2,1.0f + (vertices.get(i+2) - minZ)/(maxZ-minZ));
//        }

        //Reprint test
        for(int i =0; i< faceIndices.size() ; i+=9){
            String f1 =String.format("%d/%d/%d",faceIndices.get(i),faceIndices.get(i+1),faceIndices.get(i+2));
            String f2 =String.format("%d/%d/%d",faceIndices.get(i+3),faceIndices.get(i+4),faceIndices.get(i+5));
            String f3 =String.format("%d/%d/%d",faceIndices.get(i+6),faceIndices.get(i+7),faceIndices.get(i+8));
            System.out.println(String.format("f %s %s %s",f1,f2,f3));
        }

        //
        for(int i =0; i< faceIndices.size() ; i+=9){
           int v0_idx = faceIndices.get(i)-1 ;  int t0_idx = faceIndices.get(i+1)-1; int n0_idx = faceIndices.get(i+2)-1;
           int v1_idx = faceIndices.get(i+3)-1; int t1_idx = faceIndices.get(i+4)-1; int n1_idx = faceIndices.get(i+5)-1;
           int v2_idx = faceIndices.get(i+6)-1; int t2_idx = faceIndices.get(i+7)-1; int n2_idx = faceIndices.get(i+8)-1;

           v0_idx *=3; t0_idx*=2; n0_idx*=3;
           v1_idx *=3; t1_idx*=2; n1_idx*=3;
           v2_idx *=3; t2_idx*=2; n2_idx*=3;

           float v0_x = vertices.get(v0_idx);           float v0_y = vertices.get(v0_idx+1);               float v0_z = vertices.get(v0_idx+2);
           float v0_tu = textCoords.get(t0_idx);        float v0_tv = textCoords.get(t0_idx+1);
           float v0_nx = vertNormals.get(n0_idx);       float v0_ny = vertNormals.get(n0_idx+1);           float v0_nz = vertNormals.get(n0_idx+2);

           float v1_x = vertices.get(v1_idx);           float v1_y = vertices.get(v1_idx+1);               float v1_z = vertices.get(v1_idx+2);
           float v1_tu = textCoords.get(t1_idx);        float v1_tv = textCoords.get(t1_idx+1);
           float v1_nx = vertNormals.get(n1_idx);       float v1_ny = vertNormals.get(n1_idx+1);           float v1_nz = vertNormals.get(n1_idx+2);

           float v2_x = vertices.get(v2_idx);           float v2_y = vertices.get(v2_idx+1);               float v2_z = vertices.get(v2_idx+2);
           float v2_tu = textCoords.get(t2_idx);        float v2_tv = textCoords.get(t2_idx+1);
           float v2_nx = vertNormals.get(n2_idx);       float v2_ny = vertNormals.get(n2_idx+1);           float v2_nz = vertNormals.get(n2_idx+2);

           System.out.println("Face:"+i/9);
           System.out.println(String.format("%f %f %f",v0_x,v0_y,v0_z));
           System.out.println(String.format("%f %f %f",v1_x,v1_y,v1_z));
           System.out.println(String.format("%f %f %f",v2_x,v2_y,v2_z));
        }
        //Try projection and draw in buffer and save
        RenderContext target = new RenderContext(1024,768);
        final float aspect = (float)target.GetHeight() / target.GetWidth();
        final float znear = 0.1f;
        final float zfar =100.0f;
        final float fov = (float) (Math.PI/3.0f);
        //
        final float fovFactor = 1.0f/ (float) (Math.tan(fov/2.0f));
        final float aspect_times_fovfactor= aspect * fovFactor;
        final float zfar_over_deltaz = znear / (zfar - znear);
        final float zfar_over_deltaz_time_znear = zfar_over_deltaz * znear;

        float [] result_v0 = new float[4]; float [] result_v1 = new float[4]; float [] result_v2 = new float[4];
        float [] v0 = new float[4]; float [] v1 = new float[4];float [] v2 = new float[4];
        for(int i =0 ; i< vertices.size();i+=9){
            v0[0] = vertices.get(i);   v1[0] = vertices.get(i+3);   v2[0] = vertices.get(i+6);
            v0[1] = vertices.get(i+1); v1[1] = vertices.get(i+4);   v2[1] = vertices.get(i+7);
            v0[2] = vertices.get(i+2); v1[2] = vertices.get(i+5);   v2[2] = vertices.get(i+8);
            v0[3] = 1.0f;              v1[3] = 1.0f;                v2[3] = 1.0f;

            result_v0[0] = aspect_times_fovfactor * v0[0];
            result_v0[1] = fovFactor * v0[1];
            result_v0[2] = zfar_over_deltaz * v0[2] - zfar_over_deltaz_time_znear;
            result_v0[3] = v0[2]; //Keep original Z

            result_v1[0] = aspect_times_fovfactor * v1[0];
            result_v1[1] = fovFactor * v1[1];
            result_v1[2] = zfar_over_deltaz * v1[2] - zfar_over_deltaz_time_znear;
            result_v1[3] = v1[2]; //Keep original Z

            result_v2[0] = aspect_times_fovfactor * v2[0];
            result_v2[1] = fovFactor * v2[1];
            result_v2[2] = zfar_over_deltaz * v2[2] - zfar_over_deltaz_time_znear;
            result_v2[3] = v2[2]; //Keep original Z

            GfxMath.perspectiveDivide(result_v0);
            GfxMath.perspectiveDivide(result_v1);
            GfxMath.perspectiveDivide(result_v2);

            result_v0[0] *= (target.GetWidth()-1)/2.0f;  result_v1[0] *= (target.GetWidth()-1)/2.0f;  result_v2[0] *= (target.GetWidth()-1)/2.0f;
            result_v0[1] *= (target.GetHeight()-1)/2.0f; result_v1[1] *= (target.GetHeight()-1)/2.0f; result_v2[1] *= (target.GetHeight()-1)/2.0f;
            result_v0[0] += (target.GetWidth()-1)/2.0f;  result_v1[0] += (target.GetWidth()-1)/2.0f;  result_v2[0] += (target.GetWidth()-1)/2.0f;
            result_v0[1] += (target.GetHeight()-1)/2.0f; result_v1[1] += (target.GetHeight()-1)/2.0f; result_v2[1] += (target.GetHeight()-1)/2.0f;

            target.drawLine((int) result_v0[0], (int) result_v0[1],
                    (int) result_v1[0], (int) result_v1[1], (byte) 0xff, (byte) 0xff, (byte) 0xff);
        }


        target.save();
    }


}
