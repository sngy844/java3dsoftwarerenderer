public class RenderContext extends Bitmap
{
	//Index on framebuffer to draw grid
	int gridIndex [];

	public RenderContext(int width, int height)	{
		super(width, height);
		final int spacing = 25;

		gridIndex  = new int[(int)Math.ceil(this.GetWidth()/(float)spacing)*(int)Math.ceil(this.GetHeight()/(float)spacing)];
		int count =0;
		for(int j = 0 ; j < height ; j+=spacing){
			for(int  i = 0 ; i< width ; i+=spacing) {
				gridIndex[count++] = (j * m_width + i) * 4;
			}
		}
	}

	public void drawGrid(){
		int index ;
		for(int i =0 ; i< gridIndex.length ; i++){
			index = gridIndex[i];
			m_components[index ] = 	 (byte)255;
			m_components[index +1] = (byte)255;
			m_components[index +2] = (byte)255;
			m_components[index +3] = (byte)255;
		}
	}

	public void drawGrid(){
		int index ;
		//Gird coordinate can be precomputed
		for(int i = 0 ; i < this.GetWidth() ; i+=10){
			for(int j = 0 ; j < this.GetHeight() ; j+=10){
				index = (j*m_width + i)*4;
				m_components[index ] = (byte)255;
				m_components[index +1] = (byte)255;
				m_components[index +2] = (byte)255;
				m_components[index +3] = (byte)255;

				//DrawPixel(i,j,(byte)255,(byte) 255,(byte)255); //Clean but cost function call
			}
		}
	}

	public void drawLine(int x0, int y0, int x1, int y1, byte r, byte g, byte b){
		//DDA
		final int delta_x = x1 - x0;
		final int delta_y = y1 - y0;

		//Optimize for the case y=x line
		if(delta_x == 0){
			if(y0 > y1){
				int temp = y0;
				y0 = y1;
				y1 = temp;
			}
			for(int i =y0 ; i<= y1; i++ ){
				this.DrawPixel(x0, i , r, g, b);
			}
		}
		else if(delta_y  == 0){
			for(int i =x0 ; i<= x1; i++ ){
				this.DrawPixel(i, y1 , r, g, b);
			}
		}
		else {
			final float side_length = Math.abs(delta_x) > Math.abs(delta_y) ? Math.abs(delta_x) : Math.abs(delta_y);

			final float increment_x = delta_x / (float) side_length;
			final float increment_y = delta_y / (float) side_length;

			float currentX = x0;
			float currentY = y0;

			for (int i = 0; i <= side_length; i++) {
				this.DrawPixel((int) currentX, (int) currentY, r, g, b);
				currentX += increment_x;
				currentY += increment_y;
			}
		}
	}

	public void drawTriangle(int x0, int y0, int x1, int y1, int x2, int y2, byte r, byte g, byte b){
		drawLine(x0,y0,x1,y1,r,g,b);
		drawLine(x1,y1,x2,y2,r,g,b);
		drawLine(x2,y2,x0,y0,r,g,b);
	}

}
