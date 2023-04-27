package swrast;

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
			m_pixelComponents[index ] = 	 (byte)255;
			m_pixelComponents[index +1] = (byte)255;
			m_pixelComponents[index +2] = (byte)255;
			m_pixelComponents[index +3] = (byte)255;
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

	public void drawTriangleWire(int x0, int y0, int x1, int y1, int x2, int y2, byte r, byte g, byte b){
		drawLine(x0,y0,x1,y1,r,g,b);
		drawLine(x1,y1,x2,y2,r,g,b);
		drawLine(x2,y2,x0,y0,r,g,b);
	}

	/*
		y1 must be equal to y2
	 */
	public void drawFlatBottomTriangleFill(int x0, int y0, int x1, int y1, int x2, int y2, byte r, byte g, byte b){
		//Flat bottom first
		float inverse_slope_1 = (float)(x1 - x0)/(y1-y0);
		float inverse_slope_2 = (float)(x2 - x0)/(y2-y0);
		if(inverse_slope_1 > inverse_slope_2){
			float temp = inverse_slope_1; inverse_slope_1 = inverse_slope_2; inverse_slope_2 = temp;
		}


		float xstart =x0;
		float xend = x0;
		for(int y = y0; y<=y2; y++){
			//drawLine((int)xstart,y,(int)xend,y,r,g,b); //Could be expensive of function calls
			int index = 0;

			for(int x = (int)xstart ; x<=(int)xend; x++){
				//DrawPixel(x,y,r,g,b); //Could be expensive for function calls also
				index =(y*m_width+x)*4;
				m_pixelComponents[index]=(byte)255;
				m_pixelComponents[index+1]=r;
				m_pixelComponents[index+2]=g;
				m_pixelComponents[index+3]=b;
			}
			xstart+=inverse_slope_1;
			xend+=inverse_slope_2;
		}
	}

	/*
		y0 must be equal to y1 and x1 > x2
	 */
	public void drawFlatTopTriangleFill(int x0, int y0, int x1, int y1, int x2, int y2, byte r, byte g, byte b){
		float inverse_slope_1 = (float)(x2 - x0)/(y2-y0);
		float inverse_slope_2 = (float)(x2 - x1)/(y2-y1);
		if(inverse_slope_1 < inverse_slope_2){
			float temp = inverse_slope_1; inverse_slope_1 = inverse_slope_2; inverse_slope_2 = temp;
		}

		float xstart =x2;
		float xend = x2;

		for(int y = y2; y>=y0; y--){
			//drawLine((int)xstart,y,(int)xend,y,r,g,b); //Could be expensive of function calls
			int index = 0;
			for(int x = (int)xstart ; x<=(int)xend; x++){
				//DrawPixel(x,y,r,g,b); //Could be expensive for function calls also
				index =(y*m_width+x)*4;
				m_pixelComponents[index]=(byte)255;
				m_pixelComponents[index+1]=r;
				m_pixelComponents[index+2]=g;
				m_pixelComponents[index+3]=b;
			}
			xstart-=inverse_slope_1;
			xend-=inverse_slope_2;
		}
	}

	public void drawTriangleFill(int x0, int y0, int x1, int y1, int x2, int y2, byte r, byte g, byte b){
		//Sort by y so y0 < y1 < y2;
		int temp;
		if(y0 > y1){
			temp = y0;			y0=y1;			y1 = temp;
			temp = x0;			x0=x1;			x1 = temp;
		}
		if(y1 > y2){
			temp = y1;			y1=y2;			y2 = temp;
			temp = x1;			x1=x2;			x2 = temp;
		}
		if(y0 > y1){
			temp = y0;			y0 = y1;		y1=temp;
			temp = x0;			x0 = x1;		x1=temp;
		}

		// TODO: Division by zero case
		if(y1 == y2)
			drawFlatBottomTriangleFill(x0,y0,x1,y1,x2,y2,r,g,b);
		else if(y0==y1)
			drawFlatTopTriangleFill(x0,y0,x1,y1,x2,y2,r,g,b);
		else{
			//Mid point
			int My = y1;
			int Mx = (int) ((float) (y1 - y0) * (x2 - x0)) / (y2 - y0) + x0;
			drawFlatBottomTriangleFill(x0, y0, x1, y1, Mx, My, r, g, b);

			drawFlatTopTriangleFill(x1, y1, Mx, My, x2, y2, (byte) 255,(byte)0,(byte)0);
		}
	}

	public int[] getMidPoint(int x0, int y0, int x1, int y1, int x2, int y2){
		int temp;
		if(y0 > y1){
			 	temp = y0;			y0=y1;			y1 = temp;
				temp = x0;			x0=x1;			x1 = temp;
		}
		if(y1 > y2){
				temp = y1;			y1=y2;			y2 = temp;
				temp = x1;			x1=x2;			x2 = temp;
		}
		if(y0 > y1){
				temp = y0;			y0 = y1;		y1=temp;
				temp = x0;			x0 = x1;		x1=temp;
		}
		int My = y1;
		int Mx =(int)((float)(y1-y0)*(x2-x0))/(y2-y0) + x0;
		return new int[]{Mx,My};
	}

}
