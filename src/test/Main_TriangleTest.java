package test;

import swrast.Display;
import swrast.RenderContext;

public class Main_TriangleTest {
    public static void main(String[] args){
        //
        Display display = new Display(1024, 1024, "Software Rendering");
        RenderContext target = display.GetFrameBuffer();

        long previousTime = System.nanoTime();
        double elapsedTime = 0;
        boolean isDrawVertices = false;
        // TODO: check winding
        int tris[] = new int[]{
                250,500 , 300,800, 100,970 ,

                450,500 , 320,800, 500,970 ,

                10,10, 150,420, 10,420,

                150,10,250,420,320,420,

                430,10,700,10,430,420
        };

        int frame =0;
        boolean toggle= true;
        while(true)
        {
            long currentTime = System.nanoTime();
            float delta = (float)((currentTime - previousTime)/1000000.0);
            elapsedTime +=delta;
            previousTime = currentTime;

            target.Clear((byte) 0x00);

            for(int i =0 ; i< tris.length; i+=2) {
                target.drawPoint(tris[i], tris[i + 1], (byte) 255, (byte) 0, (byte) 0);
            }
            for(int i =0 ; i< tris.length; i+=6){
                int midPoint[] = target.getMidPoint(tris[i],tris[i+1],tris[i+2],tris[i+3],tris[i+4],tris[i+5]);
                target.drawPoint(midPoint[0], midPoint[1], (byte) 0, (byte) 0, (byte) 255);
            }

            if(isDrawVertices && toggle){
                for(int i =0 ; i< tris.length; i+=6)
                    target.drawTriangleFill(tris[i],tris[i+1],
                            tris[i+2],tris[i+3],
                            tris[i+4],tris[i+5],
                            (byte)255,(byte)255,(byte)255);
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
