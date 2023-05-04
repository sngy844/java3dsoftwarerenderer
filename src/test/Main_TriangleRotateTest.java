package test;

import swrast.Display;
import swrast.RenderContext;

public class Main_TriangleRotateTest {
    public static void main(String[] args){
        //
        Display display = new Display(180, 180,1000,1000, "Software Rendering");
        RenderContext target = display.GetFrameBuffer();

        long previousTime = System.nanoTime();
        double elapsedTime = 0;
        boolean isDrawVertices = false;
        //The original triangle vertices
        final int originalTris [] = new int[]{480/5 ,100/5 , 650/5,800/5,150/5 ,550/5};

        //Buffer to hold transformed vertices
        int tris[] = new int[originalTris.length];
        System.arraycopy(originalTris,0,tris,0,originalTris.length);

        int frame =0;
        boolean toggle= true;
        double phi = 0;
        while(true)
        {
            long currentTime = System.nanoTime();
            float delta = (float)((currentTime - previousTime)/1000000.0);
            elapsedTime +=delta;
            previousTime = currentTime;

            target.Clear((byte) 0x00);

            //Reset to original vertices. We CAN'T keep doing transformation of already transformed vertices due to truncation error
            System.arraycopy(originalTris,0,tris,0,originalTris.length);

            for(int i =0 ; i< tris.length; i+=6){
                double cenX = (tris[i] + tris[i+2] + tris[4])/3.0;
                double cenY = (tris[i+1] + tris[i+3] + tris[5])/3.0;
                tris[i] -= cenX;
                tris[i+1] -= cenY;
                tris[i+2] -= cenX;
                tris[i+3] -= cenY;
                tris[i+4] -= cenX;
                tris[i+5] -= cenY;

                phi += 0.001; //Increase angle over time
                double x = Math.cos(phi)*tris[i] - Math.sin(phi)*tris[i+1] ;
                double y = Math.sin(phi)*tris[i] + Math.cos(phi)*tris[i+1] ;
                tris[i] =   (int)(x+ cenX);
                tris[i+1] = (int)(y+ cenY);

                x = Math.cos(phi)*tris[i+2] - Math.sin(phi)*tris[i+3] ;
                y = Math.sin(phi)*tris[i+2] + Math.cos(phi)*tris[i+3] ;
                tris[i+2] = (int)(x+ cenX);
                tris[i+3] = (int)(y+ cenY);

                x = Math.cos(phi)*tris[i+4] - Math.sin(phi)*tris[i+5] ;
                y = Math.sin(phi)*tris[i+4] + Math.cos(phi)*tris[i+5] ;
                tris[i+4] = (int)(x + cenX);
                tris[i+5] = (int)(y+ cenY);

            }


            {
                for(int i =0 ; i< tris.length; i+=6)
                    target.drawTriangleFillSlope(tris[i],tris[i+1],
                            tris[i+2],tris[i+3],
                            tris[i+4],tris[i+5]
                            //(byte)255,(byte)255,(byte)255)
                    );
            }

            for(int i =0 ; i< tris.length; i+=2) {
                target.drawPoint(tris[i], tris[i + 1], (byte) 255, (byte) 0, (byte) 0);
            }
            for(int i =0 ; i< tris.length; i+=6){
                int midPoint[] = target.getMidPoint(tris[i],tris[i+1],tris[i+2],tris[i+3],tris[i+4],tris[i+5]);
                target.drawPoint(midPoint[0], midPoint[1], (byte) 255, (byte) 0, (byte) 255);
            }




            display.SwapBuffers();

            frame++;
            if(elapsedTime >=1000) {
                System.out.println("FPS:"+frame);
                isDrawVertices = !isDrawVertices;
                elapsedTime=0;
                frame =0;
            }

        }

    }
}
