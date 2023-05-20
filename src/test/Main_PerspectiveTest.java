package test;

import swrast.Display;
import swrast.GfxMath;
import swrast.RenderContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main_PerspectiveTest {
    public static void main(String[] args) throws IOException {
        InputStream inputStream = Main_SignedAreaTest.class.getResourceAsStream("texture.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        final String textureName = "brick";
        String [] brickPxString =properties.getProperty(textureName).split(",");
        final int textureW = Integer.parseInt(properties.getProperty(String.format("%s_width",textureName)));
        final int textureH = textureW;
        byte brickTexture[] = new byte[brickPxString.length];
        for(int i =0 ; i< brickPxString.length; i+=4){
            brickTexture [i] = (byte) Integer.parseInt(brickPxString[i+3],16); //Alpha
            brickTexture [i+1] = (byte) Integer.parseInt(brickPxString[i+0],16); //B
            brickTexture [i+2] = (byte) Integer.parseInt(brickPxString[i+1],16); //G
            brickTexture [i+3] = (byte) Integer.parseInt(brickPxString[i+2],16);//R
        }

        //
        Display display = new Display(1024, 768,1024,768, "Software Rendering - Perspective Projection Test");
        RenderContext target = display.GetFrameBuffer();

        long previousTime = System.nanoTime();
        double elapsedTime = 0;
        boolean isDrawVertices = false;
        //The original triangle vertices
        final float originalTris [] = new float[]{
                -1,-1.0f,4,
                -1,1,4,
                 1,1,4

//                0,0,4,
//                0,1,4,
//                1,1,4,
        };
        final float originalTrisUv[] = new float[]{
                0.0f,0,
                0,1,
                1f,1f,

                0.0f,0,
                1,0,
                1f,1f,

                0.0f,0,
                1,1,
                0f,1f,

                0.0f,0,
                1,0,
                1f,1f
        };

        //Buffer to hold transformed vertices
        float transformedTris[] = new float[originalTris.length];
        final float aspect = (float)display.getFrameBufferHeight() / display.getFramebufferWidth();
        final float znear = 0.1f;
        final float zfar =100.0f;
        final float fov = (float) (Math.PI/3.0f);

        final float [] [] perspectiveMatrix = GfxMath.perspective(fov,aspect,znear,zfar);
        //GfxMath.identity(perspectiveMatrix);

        final float fovFactor = 1.0f/ (float) (Math.tan(fov/2.0f));
        final float aspect_times_fovfactor= aspect * fovFactor;
        final float zfar_over_deltaz = znear / (zfar - znear);
        final float zfar_over_deltaz_time_znear = zfar_over_deltaz * znear;

        int frame =0; float frameTime =0;
        boolean useMatrixProj =true;
        double phi = 0; boolean once = true;
        while(true)
        {
            long currentTime = System.nanoTime();
            float delta = (float)((currentTime - previousTime)/1000000.0);
            elapsedTime +=delta;
            previousTime = currentTime;

            //Reset to original vertices. We CAN'T keep doing transformation of already transformed vertices due to truncation error
            //System.arraycopy(originalTris,0,transformedTris,0,originalTris.length);
            float [] vertex  = new float[4];
            float [] result =  new float[4];
            for(int i =0 ; i< originalTris.length; i+=3) {
                    vertex[0] = originalTris[i];
                    vertex[1] = originalTris[i+1];
                    vertex[2] = originalTris[i+2];
                    vertex[3] = 1.0f;

                    //There won't be any visual difference with projection using matrix or hand rolling...
                    if(useMatrixProj)
                        GfxMath.mat4_mult_vec4_project(result, perspectiveMatrix, vertex);
                    else {
                        //Hand rolling maybe more efficient since don't have multiple zeros with other?!
                        result[0] = aspect_times_fovfactor * vertex[0];
                        result[1] = fovFactor * vertex[1];
                        result[2] = zfar_over_deltaz * vertex[2] - zfar_over_deltaz_time_znear;
                        result[3] = vertex[2]; //Keep original Z
                    }
                    //Perspective divide also normalize x,y,z
                    GfxMath.perspectiveDivide(result);

                    result[0] *= (display.getFramebufferWidth()-1)/2.0f;
                    result[1] *= (display.getFrameBufferHeight()-1)/2.0f;

                    result[0] += (display.getFramebufferWidth()-1)/2.0f;
                    result[1] += (display.getFrameBufferHeight()-1)/2.0f;

                    transformedTris[i] =    result[0];
                    transformedTris[i+ 1] = result[1];
                    transformedTris[i+ 2] = result[2];
            }
            currentTime = System.nanoTime();
            target.Clear((byte) 0xFF);
//            target.drawGrid();

            //Rotation Test
//            double cenX=0,cenY=0;
//            for(int i =0 ; i< tris.length; i+=6) {
//                cenX += (tris[i] + tris[i + 2] + tris[i + 4]);
//                cenY += (tris[i + 1] + tris[i + 3] + tris[i + 5]);
//            }
//            cenX = cenX/(tris.length/2);
//            cenY = cenY/(tris.length/2);
//
//            for(int i =0 ; i< tris.length; i+=6){
//                tris[i]   -= cenX;
//                tris[i+1] -= cenY;
//                tris[i+2] -= cenX;
//                tris[i+3] -= cenY;
//                tris[i+4] -= cenX;
//                tris[i+5] -= cenY;
//
//                phi += 0.000; //Increase angle over time
//                double x = Math.cos(phi)*tris[i] - Math.sin(phi)*tris[i+1] ;
//                double y = Math.sin(phi)*tris[i] + Math.cos(phi)*tris[i+1] ;
//                tris[i] =   (int)(x+ cenX);
//                tris[i+1] = (int)(y+ cenY);
//
//                x = Math.cos(phi)*tris[i+2] - Math.sin(phi)*tris[i+3] ;
//                y = Math.sin(phi)*tris[i+2] + Math.cos(phi)*tris[i+3] ;
//                tris[i+2] = (int)(x+ cenX);
//                tris[i+3] = (int)(y+ cenY);
//
//                x = Math.cos(phi)*tris[i+4] - Math.sin(phi)*tris[i+5] ;
//                y = Math.sin(phi)*tris[i+4] + Math.cos(phi)*tris[i+5] ;
//                tris[i+4] = (int)(x + cenX);
//                tris[i+5] = (int)(y+ cenY);
//            }


            {

                for(int i =0 ; i< transformedTris.length; i+=9) {
                    if(i == 0)target.bindTexture(brickTexture, textureW, textureH, 0);
                    //if(i == 1)target.bindTexture(brickTexture, textW, 1);
                    target.drawTriangleFillSlope(
                            (int) transformedTris[i],     (int) transformedTris[i + 1],1,//2
                            (int) transformedTris[i + 3], (int) transformedTris[i + 4],1,//5
                            (int) transformedTris[i + 6], (int) transformedTris[i + 7],1,
                            originalTrisUv[i],     originalTrisUv[i + 1],
                            originalTrisUv[i + 2], originalTrisUv[i + 3],
                            originalTrisUv[i + 4], originalTrisUv[i + 5]
                    );
                }
            }



            display.SwapBuffers();
            if(once) {
                display.save();
                once =false;
            }
            frameTime += (System.nanoTime() - currentTime)/1000000.0;

            frame++;
            if(elapsedTime >=1000) {
                System.out.println("FPS:"+frame + " Frametime:"+frameTime/frame);
                isDrawVertices = !isDrawVertices;
                elapsedTime=0;
                frame =0;
                frameTime =0;
                useMatrixProj = !useMatrixProj;

            }

        }

    }
}
