package test;

import swrast.Display;
import swrast.RenderContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main_TriangleRotateTest {
    public static void main(String[] args) throws IOException {
        InputStream inputStream = Main_SignedAreaTest.class.getResourceAsStream("texture.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        final String textureName = "brick";
        String [] brickPxString =properties.getProperty(textureName).split(",");
        final int textW = Integer.parseInt(properties.getProperty(String.format("%s_width",textureName)));
        final int textH = textW;
        byte brickTexture[] = new byte[brickPxString.length];
        for(int i =0 ; i< brickPxString.length; i+=4){
            brickTexture [i] = (byte) Integer.parseInt(brickPxString[i+3],16); //Alpha
            brickTexture [i+1] = (byte) Integer.parseInt(brickPxString[i+0],16); //B
            brickTexture [i+2] = (byte) Integer.parseInt(brickPxString[i+1],16); //G
            brickTexture [i+3] = (byte) Integer.parseInt(brickPxString[i+2],16);//R
        }

        //
        Display display = new Display(1024, 768,1024,768, "Software Rendering - Texture Mapping And Filtering Test");
        RenderContext target = display.GetFrameBuffer();

        long previousTime = System.nanoTime();
        double elapsedTime = 0;
        boolean isDrawVertices = false;
        //The original triangle vertices
        final int originalTris [] = new int[]{
                0,0,500,500,0,500,

                0,0,500,0,500,500,

                //2nd Square
                511,0,1011,500,511,500,

                511,0,1011,0,1011,500,

        };
        final float originalTrisUv[] = new float[]{
                 0.0f,0,
                1,1,
                0f,1f,

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
        int tris[] = new int[originalTris.length];
        System.arraycopy(originalTris,0,tris,0,originalTris.length);

        int frame =0; float frameTime =0;
        int filter =0;
        double phi = 0;
        while(true)
        {
            long currentTime = System.nanoTime();
            float delta = (float)((currentTime - previousTime)/1000000.0);
            elapsedTime +=delta;
            previousTime = currentTime;

            //Reset to original vertices. We CAN'T keep doing transformation of already transformed vertices due to truncation error
            System.arraycopy(originalTris,0,tris,0,originalTris.length);

            currentTime = System.nanoTime();
            target.Clear((byte) 0x00);
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

                for(int i =0 ; i< tris.length; i+=6) {
                    if(i == 0)target.bindTexture(brickTexture, textW, textH, 0);
                    if(i == 12)target.bindTexture(brickTexture, textW, textH, 1);
                    target.drawTriangleFillSlope(tris[i], tris[i + 1],0,
                            tris[i + 2], tris[i + 3],0,
                            tris[i + 4], tris[i + 5],0,
                            originalTrisUv[i], originalTrisUv[i + 1],
                            originalTrisUv[i + 2], originalTrisUv[i + 3],
                            originalTrisUv[i + 4], originalTrisUv[i + 5]
                    );
                }
            }

//            for(int i =0 ; i< tris.length; i+=2) {
//                target.drawPoint(tris[i], tris[i + 1], (byte) 255, (byte) 0, (byte) 0);
//            }
//            for(int i =0 ; i< tris.length; i+=6){
//                int midPoint[] = target.getMidPoint(tris[i],tris[i+1],tris[i+2],tris[i+3],tris[i+4],tris[i+5]);
//                target.drawPoint(midPoint[0], midPoint[1], (byte) 255, (byte) 0, (byte) 255);
//            }

//            target.drawPoint((int) cenX, (int) cenY, (byte) 255, (byte) 0, (byte) 255);

//            for(int x =0 ; x<textW; x++)
//                for(int y =0 ; y<textW; y++){
//                    byte r = brickTexture[(y*textW+x)*4 +3];
//                    byte g = brickTexture[(y*textW+x)*4 +2];
//                    byte b = brickTexture[(y*textW+x)*4 +1];
//                    target.DrawPixel(x,y,r,g,b);
//                }


            display.SwapBuffers();

            frameTime += (System.nanoTime() - currentTime)/1000000.0;

            frame++;
            if(elapsedTime >=1000) {
                System.out.println("FPS:"+frame + " Frametime:"+frameTime/frame);
                isDrawVertices = !isDrawVertices;
                elapsedTime=0;
                frame =0;
                frameTime =0;

            }

        }

    }
}
