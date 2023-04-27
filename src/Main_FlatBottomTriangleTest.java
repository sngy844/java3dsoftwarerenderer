public class Main_FlatBottomTriangleTest {
    public static void main(String[] args){
    //
    Display display = new Display(1024, 1024, "Software Rendering");
    RenderContext target = display.GetFrameBuffer();

    long previousTime = System.nanoTime();
    double elapsedTime = 0;
    boolean isDrawVertices = false;
    // TODO: check winding
    int tris[] = new int[]{
                        50,300,10,500,130,500,
                        150,300,170,500,250,500,
                        350,300,300,500,500,500,

                        710,300,510,500,600,500,

                        500,600,310,980,700,980
    };

    int frame =0;
    while(true)
    {
        long currentTime = System.nanoTime();
        float delta = (float)((currentTime - previousTime)/1000000.0);
        elapsedTime +=delta;
        previousTime = currentTime;

        target.Clear((byte) 0x00);

        for(int i =0 ; i< tris.length; i+=2)
            target.DrawPixel(tris[i], tris[i+1], (byte) 255, (byte) 0, (byte) 0);

        if(isDrawVertices){
            for(int i =0 ; i< tris.length; i+=6)
                        target.drawFlatBottomTriangleFill(tris[i],tris[i+1],
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

