import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main
{
	public static void main(String[] args) throws IOException {
		//
		Display display = new Display(1024, 1024, "Software Rendering");
		RenderContext target = display.GetFrameBuffer();

		Properties properties = new Properties();
		InputStream inputStream = Main.class.getResourceAsStream("vertices.properties");
		properties.load(inputStream);

		String verticesString[] =properties.getProperty("vertices").split(",");
		double vertices[] = new double[verticesString.length];
		for(int i =0 ; i< verticesString.length ;i++) {
			vertices[i] = Double.parseDouble(verticesString[i]) + 1.0;
		}
		for(int i =0 ; i< vertices.length ;i+=3) {
			vertices[i]   =	(vertices[i]*target.GetWidth()-1)/2.0;
			vertices[i+1] = target.GetHeight() -1 - (vertices[i+1]*target.GetHeight()-1)/2.0;
		}

		String indicesString[] =properties.getProperty("indices").split(",");
		int indices[] = new int[indicesString.length];
		for(int i =0 ; i< indicesString.length ;i++)
			indices[i] = (Integer.parseInt(indicesString[i]) - 1)*3 ;

		inputStream.close();
		properties.clear();



		long previousTime = System.nanoTime();
		double elapsedTime = 0;
		int frame = 0;

		int y = 600;
		int move =1;
		boolean drawWire = true;
		while(true)
		{
			long currentTime = System.nanoTime();
			float delta = (float)((currentTime - previousTime)/1000000.0);
			elapsedTime +=delta;
			previousTime = currentTime;

			target.Clear((byte) 0x00);
			//target.drawGrid();

//			target.DrawPixel(50,50,(byte)0xFF,(byte)0xFF,(byte)0xFF);
//
//			target.drawLine(200,200, 400,400, (byte)0xFF,(byte)0xFF,(byte)0xFF);
//			target.drawLine(200,300, 400,300, (byte)0xFF,(byte)0xFF,(byte)0xFF);
//			target.drawLine(500,500, 300 ,150, (byte)0xFF,(byte)0x00,(byte)0xFF);
//
//			target.drawLine(50,50, 400,50, (byte)0x00,(byte)0xFF,(byte)0xFF);
//			target.drawLine(50,50, 50,400, (byte)0xFF,(byte)0xFF,(byte)0xFF);
//			target.drawLine(50,400, 400,400, (byte)0xFF,(byte)0x00,(byte)0xFF);
//			target.drawLine(400,400, 400,50, (byte)125,(byte)125,(byte)0xFF);
//
//			target.drawLine(400,50, 50,400, (byte)125,(byte)125,(byte)125);
//			target.drawLine(50,50, 400,400, (byte)75,(byte)125,(byte)75);
//
//
//			target.drawTriangle(y, y, 600, 285, 400, 500, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF);
//			y+=move;
//			if(y == 768-1)
//				move = -1;
//			if(y == 1)
//				move =1;

			//Since vertices from obj range from -1.0 1.0 we add 1.0 to make range 0.0 to 2.0 then scaled with width and height for vx, vy
			for(int i =0 ; i< vertices.length ;i+=3){
				int v_x =(int)vertices[i];
				int v_y =(int)vertices[i+1];

				target.DrawPixel(v_x,v_y,(byte)0xFF,(byte)0xFF,(byte)0xFF);
			}
			if(drawWire) {
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


//					target.DrawPixel(v0_x, v0_y, (byte) 255, (byte) 0, (byte) 0);
//					target.DrawPixel(v1_x, v1_y, (byte) 255, (byte) 0, (byte) 0);
//					target.DrawPixel(v2_x, v2_y, (byte) 255, (byte) 0, (byte) 0);

				target.drawLine(v0_x,v0_y,v1_x,v1_y,(byte)255,(byte)255,(byte)255);
				target.drawLine(v1_x,v1_y,v2_x,v2_y,(byte)255,(byte)255,(byte)255);
				target.drawLine(v2_x,v2_y,v0_x,v0_y,(byte)255,(byte)255,(byte)255);
				}
			}

			display.SwapBuffers();

			frame++;

			if(elapsedTime >= 1000){
				System.out.println("FPS:"+frame);
				elapsedTime=0;
				frame =0;
				drawWire = !drawWire;
			}
		}
	}
}
