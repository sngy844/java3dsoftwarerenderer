package swrast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DrawHead {
    final RenderContext target;
    private final double[] vertices;
    private final int[] indices;

    public DrawHead(RenderContext tgt,InputStream inputStream) throws IOException {
        this.target = tgt;
        Properties properties = new Properties();
        properties.load(inputStream);

        String verticesString[] =properties.getProperty("vertices").split(",");
        vertices = new double[verticesString.length];
        for(int i =0 ; i< verticesString.length ;i++) {
            vertices[i] = Double.parseDouble(verticesString[i]) + 1.0;
        }
        for(int i = 0; i< vertices.length ; i+=3) {
            vertices[i]   =	(vertices[i]*target.GetWidth()-1)/2.0;
            vertices[i+1] = target.GetHeight() -1 - (vertices[i+1]*target.GetHeight()-1)/2.0;
        }

        String indicesString[] =properties.getProperty("indices").split(",");
        indices = new int[indicesString.length];
        for(int i =0 ; i< indicesString.length ;i++)
            indices[i] = (Integer.parseInt(indicesString[i]) - 1)*3 ;

        properties.clear();
    }

    public void drawWire(){
        for (int i = 0; i < indices.length; i += 3) {
            int v0_idx = indices[i] ;
            int v1_idx = indices[i + 1];
            int v2_idx = indices[i + 2];

            int v0_x = (int)vertices[v0_idx];
            int v0_y = (int)vertices[v0_idx + 1];

            int v1_x = (int)vertices[v1_idx];
            int v1_y = (int)vertices[v1_idx + 1];

            int v2_x = (int)vertices[v2_idx];
            int v2_y = (int)vertices[v2_idx + 1];


            target.drawLine(v0_x,v0_y,v1_x,v1_y,(byte)255,(byte)255,(byte)255);
            target.drawLine(v1_x,v1_y,v2_x,v2_y,(byte)255,(byte)255,(byte)255);
            target.drawLine(v2_x,v2_y,v0_x,v0_y,(byte)255,(byte)255,(byte)255);
        }
    }

    public void drawPoints() {
        for(int i =0 ; i< vertices.length ;i+=3){
            int v_x =(int)vertices[i];
            int v_y =(int)vertices[i+1];

            target.DrawPixel(v_x,v_y,(byte)0xFF,(byte)0,(byte)0);
        }
    }
}
