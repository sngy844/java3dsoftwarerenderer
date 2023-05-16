package test;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import swrast.Display;
import swrast.GfxMath;
import swrast.GfxNative;
import swrast.RenderContext;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main_ObjReaderTest {
    static boolean drawPoint = false;
    static boolean drawWire= false;
    static boolean drawTexture =true;
    static float rotAmount = 0.00001f;
    static boolean culling = true;
    static boolean deptTest = true;

    public static void main(String[] args) {
        List<Float> vertices =  new ArrayList<>();
        List<Float> textCoords =  new ArrayList<>();
        List<Float> vertNormals =  new ArrayList<>();
        List<Integer> faceIndices = new ArrayList<>();

        BufferedReader reader;
        int dims [] = new int[2]; //Dim[0] width, dim[2] height
        byte[] pngData = GfxNative.openPNGFile("res/emd48.png",dims);
        int textureW = dims[0];
        int textureH = dims[1];
        //Convert to R-G-B
        byte[] textureData = new byte[textureH*textureW*4];
        for(int y =0 ; y< textureH; y++){
            for(int x =0 ; x<textureW; x++){
                int idx =(y*textureW+x)*4;
                textureData[idx] = (byte) 255;
                textureData[idx+3] =  pngData[idx+0];
                textureData[idx+2] =  pngData[idx+1];
                textureData[idx+1] =  pngData[idx+2];
            }
        }

        try {
            reader = new BufferedReader(new FileReader("res/leon_head.obj"));
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
                        textCoords.add(1.0f - Float.parseFloat(splitLine[2]));
                    }
                    if(isNormal){
                        vertNormals.add(Float.parseFloat(splitLine[1]));
                        vertNormals.add(Float.parseFloat(splitLine[2]));
                        vertNormals.add(Float.parseFloat(splitLine[3]));
                    }
                    if(isFace){
                        for(int i =1 ; i <= 3 ; i++) {
                            String[] xVxTxN =   splitLine[i].split("/");
                            faceIndices.add(Integer.parseInt(xVxTxN[0]) -1 );
                            faceIndices.add(Integer.parseInt(xVxTxN[1]) -1 );
                            faceIndices.add(Integer.parseInt(xVxTxN[2]) -1 );
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

        //Scale the model
//        float scale = 35;
//        for(int i =0 ; i< vertices.size();i+=3) {
//            vertices.set(i,     vertices.get(i)/scale );
//            vertices.set(i+1,   vertices.get(i+1)/scale );
//            vertices.set(i+2,   vertices.get(i+2)/scale );
//        }
//
        //Find min find max - probably do it during the read in file
        float minX = Float.MAX_VALUE; float minY = Float.MAX_VALUE; float minZ = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE; float maxY = Float.MIN_VALUE; float maxZ = Float.MIN_VALUE;
        for(int i =0 ; i< vertices.size();i+=3){
            if(minX > vertices.get(i)) minX =vertices.get(i);
            if(maxX < vertices.get(i)) maxX =vertices.get(i);

            if(minY > vertices.get(i+1)) minY =vertices.get(i+1);
            if(maxY < vertices.get(i+1)) maxY =vertices.get(i+1);

            if(minZ > vertices.get(i+2)) minZ =vertices.get(i+2);
            if(maxZ < vertices.get(i+2)) maxZ =vertices.get(i+2);
        }
        //Normalize X,Y,Z
        float deltaX = maxX - minX; float deltaY = maxY - minY; float deltaZ = maxZ - minZ;
        for(int i =0 ; i< vertices.size();i+=3) {
            vertices.set(i,   (vertices.get(i) -   minX)/deltaX  );
            vertices.set(i+1, (vertices.get(i+1) - minY)/deltaY);
            vertices.set(i+2, (vertices.get(i+2) - minZ)/deltaZ);

            vertices.set(i,     vertices.get(i)*2 - 1 );
            vertices.set(i+1,   vertices.get(i+1)*2 - 1 );
            vertices.set(i+2,   vertices.get(i+2)*2 - 1 );
        }

        //
        //Try projection and draw in buffer and save
        Display display = new Display(1024,768,1024,768, "Software Rendering - Obj, Perspective, Transform ,Backface Culling, Z Buffer Test");
        RenderContext target = display.GetFrameBuffer();



        target.Clear((byte) 125);
        target.bindTexture(null,textureW,textureH,0);
        target.setDepthTest(deptTest);
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
        float [] camPointOnFaceVec = new float[4];
        float [] v0 = new float[4]; float [] v1 = new float[4];float [] v2 = new float[4];
        double phi =0;

        display.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                //throw new NotImplementedException();
            }

            @Override
            public void keyPressed(KeyEvent e) {
                char keyCode = e.getKeyChar();
                if(keyCode == 't') drawTexture = !drawTexture;
                if(keyCode == 'c') culling = !culling;
                if(keyCode == 'p') drawPoint = !drawPoint;
                if(keyCode == 'w') drawWire = !drawWire;
                if(keyCode == 'd'){ deptTest = !deptTest; target.setDepthTest(deptTest);}
                if(keyCode == 'r') {
                    if(rotAmount == 0) rotAmount = 0.000005f;
                    else rotAmount = 0;
                }
                if((int)keyCode == 27){
                    System.exit(0);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                //throw new NotImplementedException();
            }
        });

    int k =0;
    while (true) {
        target.Clear((byte) 255, (byte) 0, (byte) 255);
        target.clearZBuffer();
        for(int i =0 ; i< vertices.size() && drawPoint ;i+=3){
            v0[0] = vertices.get(i);
            v0[1] = vertices.get(i+1);
            v0[2] = vertices.get(i+2)-3.5f;
            v0[3] = 1.0f;

            result_v0[0] = aspect_times_fovfactor * v0[0];
            result_v0[1] = fovFactor * v0[1];
            result_v0[2] = zfar_over_deltaz * v0[2] - zfar_over_deltaz_time_znear;
            result_v0[3] = v0[2]; //Keep original Z

            GfxMath.perspectiveDivide(result_v0);

            result_v0[0] *= (target.GetWidth()-1)/2.0f;
            result_v0[1] *= (target.GetHeight()-1)/2.0f;
            result_v0[0] += (target.GetWidth()-1)/2.0f;
            result_v0[1] += (target.GetHeight()-1)/2.0f;

            target.DrawPixel((int) result_v0[0], (int) result_v0[1],(byte) 0xff, (byte) 0xff, (byte) 0xff);
        }

        for(int i =0; i< faceIndices.size() && !drawPoint ; i+=9){
            int v0_idx = faceIndices.get(i) ;  int t0_idx = faceIndices.get(i+1); int n0_idx = faceIndices.get(i+2);
            int v1_idx = faceIndices.get(i+3); int t1_idx = faceIndices.get(i+4); int n1_idx = faceIndices.get(i+5);
            int v2_idx = faceIndices.get(i+6); int t2_idx = faceIndices.get(i+7); int n2_idx = faceIndices.get(i+8);

            v0_idx *=3; t0_idx*=2; n0_idx*=3;
            v1_idx *=3; t1_idx*=2; n1_idx*=3;
            v2_idx *=3; t2_idx*=2; n2_idx*=3;

            float v0_u = textCoords.get(t0_idx);        float v0_v = textCoords.get(t0_idx+1);
            float v1_u = textCoords.get(t1_idx);        float v1_v = textCoords.get(t1_idx+1);
            float v2_u = textCoords.get(t2_idx);        float v2_v = textCoords.get(t2_idx+1);

            v0[0] = vertices.get(v0_idx);        v1[0] = vertices.get(v1_idx+0);      v2[0] = vertices.get(v2_idx);
            v0[1] = vertices.get(v0_idx+1);      v1[1] = vertices.get(v1_idx+1);      v2[1] = vertices.get(v2_idx+1);
            v0[2] = vertices.get(v0_idx+2);      v1[2] = vertices.get(v1_idx+2);      v2[2] = vertices.get(v2_idx+2);
            v0[3] = 1.0f;                        v1[3] = 1.0f;                        v2[3] = 1.0f;

//            v0[2] -= 3.f;
//            v1[2] -= 3.f;
//            v2[2] -= 3.f;

            //Rotation X
            {
                phi =k * rotAmount; //Increase angle over time
                float y = (float) (Math.cos(phi) * v0[0] - Math.sin(phi) * v0[2]);
                float z = (float) (Math.sin(phi) * v0[0] + Math.cos(phi) * v0[2]);
                v0[0] = y;
                v0[2] = z - 4.5f;
                y = (float) (Math.cos(phi) * v1[0] - Math.sin(phi) * v1[2]);
                z = (float) (Math.sin(phi) * v1[0] + Math.cos(phi) * v1[2]);
                v1[0] = y;
                v1[2] = z - 4.5f;
                y = (float) (Math.cos(phi) * v2[0] - Math.sin(phi) * v2[2]);
                z = (float) (Math.sin(phi) * v2[0] + Math.cos(phi) * v2[2]);
                v2[0] = y;
                v2[2] = z - 4.5f;
                k++;
            }
            //Backface culling
            //Face normal
            if(culling) {
                result_v0[3] = 0.0f;
                calculateFaceNormal(result_v0, v0, v1, v2);
                normalizeVec(result_v0);
                camPointOnFaceVec[0] = -v0[0];
                camPointOnFaceVec[1] = -v0[1];
                camPointOnFaceVec[2] = -v0[2];
                camPointOnFaceVec[3] = 0;
                float angleDot = dot(camPointOnFaceVec, result_v0);
                if (angleDot < 0) {
                    continue;
                }
            }

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

//            target.DrawPixel((int) result_v0[0], (int) result_v0[1],(byte) 0xff, (byte) 0xff, (byte) 0xff);
//            target.DrawPixel((int) result_v1[0], (int) result_v1[1],(byte) 0xff, (byte) 0xff, (byte) 0xff);
//            target.DrawPixel((int) result_v2[0], (int) result_v2[1],(byte) 0xff, (byte) 0xff, (byte) 0xff);

//            target.drawTriangleFill((int) result_v0[0], (int) result_v0[1],
//                    (int) result_v1[0], (int) result_v1[1],
//                    (int) result_v2[0], (int) result_v2[1],
//                    (byte) 0xff, (byte) 0xff, (byte) 0xff
//                    );

            if(drawTexture)
            target.drawTriangleTexture((int) result_v0[0], (int) result_v0[1],result_v0[3],
                                    (int) result_v1[0], (int) result_v1[1],result_v1[3],
                                    (int) result_v2[0], (int) result_v2[1],result_v2[3],
                                    v0_u,v0_v,
                                    v1_u,v1_v,
                                    v2_u,v2_v
                                    );
            if(drawWire)
            target.drawTriangleWire((int) result_v0[0], (int) result_v0[1],
                    (int) result_v1[0], (int) result_v1[1],
                    (int) result_v2[0], (int) result_v2[1],
                    (byte) 0xff, (byte) 0xff, (byte) 0xff);

            //Depth Visulization
//            float[] zBuffer = target.getZBuffer();
//            float zMin = target.getZMin();
//            float zMaxMin = target.getZMax() - zMin;
//            for(int y =0 ; y < target.GetHeight(); y++)
//                for(int x =0 ; x < target.GetWidth(); x++){
//                    float norm =0;
//                    if(zMaxMin!=0)
//                        norm=((zBuffer[y*target.GetWidth()+x] - zMin)/(zMaxMin));
//                    byte pxDepthValue = (byte) (norm*255);
//
//                    target.DrawPixel(x,y,pxDepthValue,pxDepthValue,pxDepthValue);
//                }

        }
        display.SwapBuffers();
    }
}

    static void calculateFaceNormal(float[] result,float[] v0 , float[] v1, float[] v2){
        float v1v2_x = v2[0] -  v1[0];
        float v1v2_y = v2[1] -  v1[1];
        float v1v2_z = v2[2] - v1[2];

        float v1v0_x = v0[0] -  v1[0];
        float v1v0_y = v0[1] -  v1[1];
        float v1v0_z = v0[2] - v1[2];

        //Cross product
        result[0] = v1v2_y * v1v0_z - v1v2_z * v1v0_y;
        result[1] = v1v2_z * v1v0_x - v1v2_x * v1v0_z;
        result[2] = v1v2_x * v1v0_y - v1v2_y * v1v0_x;

    }

    static void normalizeVec(float [] result){
        float mag = (float) Math.sqrt(result[0]*result[0] + result[1]*result[1] + result[2]*result[2] + result[3]*result[3]);
        result[0] /= mag;
        result[1] /= mag;
        result[2] /= mag;
        result[3] /= mag;
    }

    static float dot(float[] v0 , float [] v1){
        return v0[0]*v1[0] +        v0[1]*v1[1] +        v0[2]*v1[2] +        v0[3]*v1[3];
    }

    void debugPrintIndices(List<Integer> faceIndices,List<Float> vertices, List<Float> textCoords, List<Float> vertNormals){
        //Reprint test
        for(int i =0; i< faceIndices.size() ; i+=9){
            String f1 =String.format("%d/%d/%d",faceIndices.get(i),faceIndices.get(i+1),faceIndices.get(i+2));
            String f2 =String.format("%d/%d/%d",faceIndices.get(i+3),faceIndices.get(i+4),faceIndices.get(i+5));
            String f3 =String.format("%d/%d/%d",faceIndices.get(i+6),faceIndices.get(i+7),faceIndices.get(i+8));
            System.out.println(String.format("f %s %s %s",f1,f2,f3));
        }

        for(int i =0; i< faceIndices.size() ; i+=9){
            int v0_idx = faceIndices.get(i) ;  int t0_idx = faceIndices.get(i+1); int n0_idx = faceIndices.get(i+2);
            int v1_idx = faceIndices.get(i+3); int t1_idx = faceIndices.get(i+4); int n1_idx = faceIndices.get(i+5);
            int v2_idx = faceIndices.get(i+6); int t2_idx = faceIndices.get(i+7); int n2_idx = faceIndices.get(i+8);

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
    }

}
