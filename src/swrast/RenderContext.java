package swrast;

import java.util.Arrays;

import static swrast.GfxMath.lerp;

public class RenderContext extends Bitmap
{
	//Index on framebuffer to draw grid
	int gridIndex [];
	final float weights[];
	final float zBuffer [];
	byte texture[];
	int textW;
	int textH;
	boolean depth_test;

	float zMin = 0;
	float zMax = 0;

	public RenderContext(int width, int height)	{
		super(width, height);
		this.weights= new float[3];
		this.zBuffer = new float[width*height];
		this.depth_test = false;
		final int spacing = 25;

		gridIndex  = new int[(int)Math.ceil(this.GetWidth()/(float)spacing)*(int)Math.ceil(this.GetHeight()/(float)spacing)];
		int count =0;
		for(int j = 0 ; j < height ; j+=spacing){
			for(int  i = 0 ; i< width ; i+=spacing) {
				gridIndex[count++] = (j * m_width + i) * 4;
			}
		}
	}

	public float[] getZBuffer(){
		return zBuffer;
	}

	public float getZMin(){
		return zMin;
	}

	public float getZMax(){
		return zMax;
	}

	public void clearZBuffer(){
		Arrays.fill(zBuffer,0.0f);
		zMin = 0;
		zMax =0;
	}


	public void drawGrid(){
//		int index ;
//		for(int i =0 ; i< gridIndex.length ; i++){
//			index = gridIndex[i];
//			m_pixelComponents[index ] = 	 (byte)255;
//			m_pixelComponents[index +1] = (byte)255;
//			m_pixelComponents[index +2] = (byte)255;
//			m_pixelComponents[index +3] = (byte)255;
//		}
		GfxNative.drawGrid(this.gridIndex,this.m_pixelComponents,gridIndex.length);
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
			if(x0 > x1){
				int temp = x0;
				x0=x1;
				x1= temp;
			}

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
				m_pixelComponents[index+1]=b;
				m_pixelComponents[index+2]=g;
				m_pixelComponents[index+3]=r;
			}
			xstart+=inverse_slope_1;
			xend+=inverse_slope_2;
		}
	}

	/*
		y0 must be equal to y1
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
				m_pixelComponents[index+1]=b;
				m_pixelComponents[index+2]=g;
				m_pixelComponents[index+3]=r;
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

		if(y1 == y2)
			drawFlatBottomTriangleFill(x0,y0,x1,y1,x2,y2,r,g,b);
		else if(y0==y1)
			drawFlatTopTriangleFill(x0,y0,x1,y1,x2,y2,r,g,b);
		else{
			//Mid point
			int My = y1;
			int Mx = (int) (((float) (y1 - y0) * (x2 - x0)) / (y2 - y0) + x0);
			drawFlatBottomTriangleFill(x0, y0, x1, y1, Mx, My, r, g, b);

			drawFlatTopTriangleFill(x1, y1, Mx, My, x2, y2, r,g,b);
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
		int Mx =(int)(((float)(y1-y0)*(x2-x0))/(y2-y0) + x0);    //Same with linear interpolation
		return new int[]{Mx,My};
	}
	int filter =0;
	//Running slop technique
	// All started from the following line equation
	//   slope * (x - x0) = y - y0 (1)
	//   			or
	//   slope * (x - x1) = y - y1 (2)
	// slope is the 'slope' of two points (x0,y0) and (x1,y1) , calculated by (y1-y0)/(x1-x0)
	// From above equation (1), given y you can calculate x using:
	//   	x = (y-y0)/slope + x0
	// <=>	x = x0 + (y-y0)*inverse_slope  -> this gives you an x-coordinate of a point that lay on the line at given y.
	//
	// Equation (2) can be expaned in similar way and will give the same result
	// Another way to think is to know how much to scale deltaX based on how much 1/deltaY: x= x0 + deltaX*k*1/deltaY
	public void drawFlatBottomTriangleSlopeFill(int x0, int y0, float w0, int x1, int y1, float w1,int x2, int y2, float w2,
												float u0, float v0, float u1, float v1, float u2, float v2){

		float inverse_slope_1 = (float)(x1 - x0)/(y1-y0); //left side
		float inverse_slope_2 = (float)(x2 - x0)/(y2-y0); //right side

		float xstart,xend;
		int index =0;
		//For each y , find xstart on left side and xend on right side then draw span/line from xstart to xend
		for(int y = y0; y<=y1; y++ ){
			//Same result
			//xstart = (x0+ (y - y0)*inverse_slope_1);
			//xend = 	 (x0+ (y - y0)*inverse_slope_2);
			xstart = (x0+ (y - y0)*inverse_slope_1); //x on left side
			xend = 	 (x2+ (y - y2)*inverse_slope_2); //x on right side
			if(xstart > xend){
				float temp = xstart;
				xstart = xend;
				xend = temp;
			}

			//Draw a span / line from xstart to xend
			for(int x=(int)(xstart+0.5); x<=(int)(xend);x++) {
				index =(y*m_width+x)*4;
				if(texture == null){
					m_pixelComponents[index]=(byte)255;
					m_pixelComponents[index+1]= (byte) 255;
					m_pixelComponents[index+2]= (byte) 255;
					m_pixelComponents[index+3]= (byte) 255;
					continue;
				}

				//Barcycentric weight Can be optimized here - like no need to recalculate area of the same big triangle
				//Alos need to take of the case point is on edge of triangle
				GfxMath.baryCentricWeight(x0,y0,x1,y1,x2,y2,x,y,weights);

				//Interpolate Z
				float finalZ =1.0f - ( weights[0]/w0 + weights[1]/w1 + weights[2]/w2 );
				if(finalZ < zBuffer[y*m_width+x] ){
					continue;
				}

				//Interpolate U V coordinate
				float finalU = weights[0]*u0 + weights[1]*u1 + weights[2]*u2;
				float finalV = weights[0]*v0 + weights[1]*v1 + weights[2]*v2;

				finalV = finalV < 0 ? 0 :finalV;
				finalU = finalU < 0 ? 0 :finalU;
				finalV = finalV > 1 ? 1 :finalV;
				finalU = finalU > 1 ? 1 :finalU;

				if(filter ==0) {
					//Coordinate on real texture - assume texture is square dimension
					int textX = (int) (finalU * (textW -1));
					int textY = (int) (finalV*  (textH -1));
					//Nearest neightbor (no filtering)
					byte textR = this.texture[(textY*textW+textX)*4+3];
					byte textG = this.texture[(textY*textW+textX)*4+2];
					byte textB = this.texture[(textY*textW+textX)*4+1];

					m_pixelComponents[index]=(byte)255;
					m_pixelComponents[index+1]= textB;
					m_pixelComponents[index+2]= textG;
					m_pixelComponents[index+3]= textR;

					zBuffer[y*m_width+x] = finalZ;
				}
				else if(filter == 1) {
					//Coordinate on real texture - assume texture is square dimension
					int textX = (int) (finalU * (textW -1));
					int textY = (int) (finalV*  (textH -1));

					//Billinear Filtering
					int textX1 = textX + 1 < textW ? textX + 1 : textX;
					int textY1 = textY;

					int textX2 = textX;
					int textX3 = textX + 1 < textW ? textX + 1 : textX;
					int textY2 = textY + 1 < textW ? textY + 1 : textY;
					int textY3 = textY + 1 < textW ? textY + 1 : textY;
					float tx = finalU * (textW - 1);
					float ty = finalV * (textW - 1);
					//interpolate horizontal and vertical
					float y_upB = lerp((texture[(textY * textW + textX) * 4 + 1] & 0xFF), (texture[(textY1 * textW + textX1) * 4 + 1] & 0xFF), (tx - textX) / (textX1 - textX));
					float y_downB = lerp((texture[(textY2 * textW + textX2) * 4 + 1] & 0xFF), (texture[(textY3 * textW + textX3) * 4 + 1] & 0xFF), (tx - textX2) / (textX3 - textX2));
					float y_targetB = lerp(y_upB, y_downB, (ty - textY) / (textY2 - textY));

					float y_upG = lerp((texture[(textY * textW + textX) * 4 + 2] & 0xFF), (texture[(textY1 * textW + textX1) * 4 + 2] & 0xFF), (tx - textX) / (textX1 - textX));
					float y_downG = lerp((texture[(textY2 * textW + textX2) * 4 + 2] & 0xFF), (texture[(textY3 * textW + textX3) * 4 + 2] & 0xFF), (tx - textX2) / (textX3 - textX2));
					float y_targetG = lerp(y_upG, y_downG, (ty - textY) / (textY2 - textY));

					float y_upR = lerp((texture[(textY * textW + textX) * 4 + 3] & 0xFF), (texture[(textY1 * textW + textX1) * 4 + 3] & 0xFF), (tx - textX) / (textX1 - textX));
					float y_downR = lerp((texture[(textY2 * textW + textX2) * 4 + 3] & 0xFF), (texture[(textY3 * textW + textX3) * 4 + 3] & 0xFF), (tx - textX2) / (textX3 - textX2));
					float y_targetR = lerp(y_upR, y_downR, (ty - textY) / (textY2 - textY));


					//DrawPixel(x,y,r,g,b); //Cost of function call
					m_pixelComponents[index] = (byte) 255;
					m_pixelComponents[index + 1] = (byte) y_targetB;
					m_pixelComponents[index + 2] = (byte) y_targetG;
					m_pixelComponents[index + 3] = (byte) y_targetR;
				}
			} // end for each x span start end
		}//end for each y scan line
	}

	public void drawFlatTopTriangleSlopeFill(int x0, int y0,float w0, int x1, int y1, float w1, int x2, int y2, float w2,float u0, float v0, float u1, float v1, float u2, float v2
											 ){
		float inverse_slope_1 = (float)(x2 - x0)/(y2-y0); //left side
		float inverse_slope_2 = (float)(x2 - x1)/(y2-y1); //right side

		float xstart,xend;
		int index =0;
		//For each y , find xstart on left side and xend on right side then draw span/line from xstart to xend
		for(int y = y0; y<=y2; y++ ){
			//Same result
			//xstart = (x0+ (y - y0)*inverse_slope_1);
			//xend = 	 (x0+ (y - y0)*inverse_slope_2);
			xstart = (x0+ (y - y0)*inverse_slope_1); //x on left side
			xend = 	 (x2+ (y - y2)*inverse_slope_2); //x on right side
			if(xstart > xend){
				float temp = xstart;
				xstart = xend;
				xend = temp;
			}

			//Draw a span / line from xstart to xend
			for(int x=(int)(xstart+0.5); x<=(int)xend;x++) {
				index =(y*m_width+x)*4;
				if(texture == null){
					m_pixelComponents[index]=(byte)255;
					m_pixelComponents[index+1]= (byte) 255;
					m_pixelComponents[index+2]= (byte) 255;
					m_pixelComponents[index+3]= (byte) 255;
					continue;
				}
				//Barcycentric weight Can be optimized here - like no need to recalculate area of the same big triangle
				//Alos need to take of the case point is on edge of triangle
				GfxMath.baryCentricWeight(x0,y0,x1,y1,x2,y2,x,y,weights);
				//Interpolate Z
				float finalZ =1.0f - ( weights[0]/w0 + weights[1]/w1 + weights[2]/w2 );
				if(finalZ < zBuffer[y*m_width+x] ){
					continue;
				}

				//Interpolate U V coordinate
				float finalU = weights[0]*u0 + weights[1]*u1 + weights[2]*u2;
				float finalV = weights[0]*v0 + weights[1]*v1 + weights[2]*v2;

				finalV = finalV < 0 ? 0 :finalV;
				finalU = finalU < 0 ? 0 :finalU;
				finalV = finalV > 1 ? 1 :finalV;
				finalU = finalU > 1 ? 1 :finalU;

				if(filter ==0) {
					//Coordinate on real texture - assume texture is square dimension
					int textX = (int) (finalU * (textW -1));
					int textY = (int) (finalV*  (textH -1));
					//Nearest neightbor (no filtering)
					byte textR = this.texture[(textY*textW+textX)*4+3];
					byte textG = this.texture[(textY*textW+textX)*4+2];
					byte textB = this.texture[(textY*textW+textX)*4+1];

					m_pixelComponents[index]=(byte)255;
					m_pixelComponents[index+1]= textB;
					m_pixelComponents[index+2]= textG;
					m_pixelComponents[index+3]= textR;

					zBuffer[y*m_width+x] = finalZ;
				}
				else if(filter == 1) {
					//Coordinate on real texture - assume texture is square dimension
					int textX = (int) (finalU * (textW -1));
					int textY = (int) (finalV*  (textH -1));

					//Billinear Filtering
					int textX1 = textX + 1 < textW ? textX + 1 : textX;
					int textY1 = textY;

					int textX2 = textX;
					int textX3 = textX + 1 < textW ? textX + 1 : textX;
					int textY2 = textY + 1 < textW ? textY + 1 : textY;
					int textY3 = textY + 1 < textW ? textY + 1 : textY;
					float tx = finalU * (textW - 1);
					float ty = finalV * (textW - 1);
					//interpolate horizontal and vertical
					float y_upB = lerp((texture[(textY * textW + textX) * 4 + 1] & 0xFF), (texture[(textY1 * textW + textX1) * 4 + 1] & 0xFF), (tx - textX) / (textX1 - textX));
					float y_downB = lerp((texture[(textY2 * textW + textX2) * 4 + 1] & 0xFF), (texture[(textY3 * textW + textX3) * 4 + 1] & 0xFF), (tx - textX2) / (textX3 - textX2));
					float y_targetB = lerp(y_upB, y_downB, (ty - textY) / (textY2 - textY));

					float y_upG = lerp((texture[(textY * textW + textX) * 4 + 2] & 0xFF), (texture[(textY1 * textW + textX1) * 4 + 2] & 0xFF), (tx - textX) / (textX1 - textX));
					float y_downG = lerp((texture[(textY2 * textW + textX2) * 4 + 2] & 0xFF), (texture[(textY3 * textW + textX3) * 4 + 2] & 0xFF), (tx - textX2) / (textX3 - textX2));
					float y_targetG = lerp(y_upG, y_downG, (ty - textY) / (textY2 - textY));

					float y_upR = lerp((texture[(textY * textW + textX) * 4 + 3] & 0xFF), (texture[(textY1 * textW + textX1) * 4 + 3] & 0xFF), (tx - textX) / (textX1 - textX));
					float y_downR = lerp((texture[(textY2 * textW + textX2) * 4 + 3] & 0xFF), (texture[(textY3 * textW + textX3) * 4 + 3] & 0xFF), (tx - textX2) / (textX3 - textX2));
					float y_targetR = lerp(y_upR, y_downR, (ty - textY) / (textY2 - textY));

					m_pixelComponents[index] = (byte) 255;
					m_pixelComponents[index + 1] = (byte) y_targetB;
					m_pixelComponents[index + 2] = (byte) y_targetG;
					m_pixelComponents[index + 3] = (byte) y_targetR;
				}
			} // end for each x span start end
		}//end for each y scan line
	}

	@Deprecated
	public void  drawTriangleFillSlope(int x0, int y0, float w0, int x1, int y1, float w1, int x2, int y2, float w2,
									   float u0, float v0,
									   float u1, float v1,
									   float u2, float v2){
		//Sort by y so y0 < y1 < y2;
		int temp;
		float tempF;
		if(y0 > y1){
			temp = x0;	x0=x1;	 x1 = temp;
			temp = y0;	y0=y1;	 y1 = temp;

			tempF = u0; u0=u1; u1= tempF;
			tempF = v0; v0=v1; v1= tempF;

			tempF = w0; w0=w1; w1= tempF;
		}
		if(y1 > y2){
			temp = y1;	y1=y2;	 y2 = temp;
			temp = x1;	x1=x2;	 x2 = temp;

			tempF = u1; u1=u2; u2= tempF;
			tempF = v1; v1=v2; v2= tempF;

			tempF = w1; w1=w2; w2= tempF;
		}
		if(y0 > y1){
			temp = y0;	y0 = y1; y1=temp;
			temp = x0;	x0 = x1; x1=temp;

			tempF = u0; u0=u1; u1= tempF;
			tempF = v0; v0=v1; v1= tempF;

			tempF = w0; w0=w1; w1= tempF;
		}

		if(y1 == y2)
			drawFlatBottomTriangleSlopeFill(x0,y0,w0,x1,y1,w1,x2,y2,w2, u0,v0, u1,v1,u2,v2);
//			GfxNative.drawFlatBottomTriangleSlopeFill(x0,y0,x1,y1,x2,y2, u0,v0, u1,v1,u2,v2
//					,filter, texture,textW,m_pixelComponents,m_width
//			);
		else if(y0==y1)
			drawFlatTopTriangleSlopeFill(x0,y0,w0,x1,y1,w1,x2,y2,w2,u0,v0,u1,v1,u2,v2);
//			GfxNative.drawFlatTopTriangleSlopeFill(x0,y0,x1,y1,x2,y2,u0,v0, u1,v1,u2,v2
//					,filter, texture,textW,m_pixelComponents,m_width
//			);
		else{
			float My = y1;
			float Mx = ( (My-y0)*(x2-x0)/(y2-y0) + x0);

			GfxMath.baryCentricWeight(x0,y0,x1,y1,x2,y2,Mx,My,weights);

			//Can't do this kind of linear interpolation
			//float Mv = v1;
			//float Mu = (Mv - v0)*(u2-u0)/(v2-v0)   + u0;

			// Have to do this kind of interpolation since u,v values are not linear in x-y space?!
			float Mv2 = weights[0]*v0 + weights[1]*v1 + weights[2]*v2;
			float Mu2 = weights[0]*u0 + weights[1]*u1 + weights[2]*u2;

			float Mw = weights[0]*w0 + weights[1]*w1 + weights[2]*w2;

			drawFlatBottomTriangleSlopeFill(x0,y0,w0, (int) Mx, (int) My, Mw,x1,y1,w1,
					u0,v0,
					Mu2,Mv2,
					u1,v1);
//			GfxNative.drawFlatBottomTriangleSlopeFill(x0,y0, (int) Mx, (int) My,x1,y1,
//					u0,v0,
//					Mu2,Mv2,
//					u1,v1
//					,filter, texture,textW,m_pixelComponents,m_width
//					);
			drawFlatTopTriangleSlopeFill(x1,y1,w1, (int) Mx, (int) My, Mw,x2,y2,w2,
					u1,v1,
					Mu2,Mv2,
					u2,v2);
//			GfxNative.drawFlatTopTriangleSlopeFill(x1,y1, (int) Mx, (int) My,x2,y2,
//					u1,v1,
//					Mu2,Mv2,
//					u2,v2
//					,filter, texture,textW,m_pixelComponents,m_width
//					);
		}
	}


	public void bindTexture(byte[] text, int textureW, int textureH, int filter){
		this.texture = text;
		this.textW = textureW;
		this.textH = textureH;
		this.filter = filter;
	}

	public void setDepthTest(boolean onOff){
		this.depth_test = onOff;
	}

	public void save(){
		IOUtils.save("buffer.ppm",m_pixelComponents,m_width,m_height);
	}

	public void saveTarga(){
		IOUtils.saveTarga("buffer.tga",m_pixelComponents,m_width,m_height);
	}



	public void drawTriangleTexture(
									int x0, int y0, float w0,
									int x1, int y1, float w1,
									int x2, int y2, float w2,
									float u0, float v0,
									float u1, float v1,
									float u2, float v2)
	{
		//Sort by y so y0 < y1 < y2;
		int temp;
		float tempF;
		if(y0 > y1){
			temp = x0;	x0=x1;	 x1 = temp;
			temp = y0;	y0=y1;	 y1 = temp;

			tempF = u0; u0=u1; u1= tempF;
			tempF = v0; v0=v1; v1= tempF;

			tempF = w0; w0=w1; w1= tempF;
		}
		if(y1 > y2){
			temp = y1;	y1=y2;	 y2 = temp;
			temp = x1;	x1=x2;	 x2 = temp;

			tempF = u1; u1=u2; u2= tempF;
			tempF = v1; v1=v2; v2= tempF;

			tempF = w1; w1=w2; w2= tempF;
		}
		if(y0 > y1){
			temp = y0;	y0 = y1; y1=temp;
			temp = x0;	x0 = x1; x1=temp;

			tempF = u0; u0=u1; u1= tempF;
			tempF = v0; v0=v1; v1= tempF;

			tempF = w0; w0=w1; w1= tempF;
		}

//		float TopMidX = x1 -x0  ; 	float TopMidY  = y1 - y0;
//		float TopBottomX = x2 -x0  ; float TopBottomY  = y2-  y0;
//		float cross = TopMidX * TopBottomY - TopBottomX*TopMidY;

		//A triangle can be just flat top or flat bottom or both
		float deltaY_1 = y1-y0;
		float deltaY_2 = y2-y0;

		float inverse_slope_1=0;
		float inverse_slope_2=0;

		if(deltaY_1 != 0) inverse_slope_1= (x1-x0) / deltaY_1;
		if(deltaY_2 != 0) inverse_slope_2= (x2-x0) / deltaY_2;

		//If it was flat top then don't do this - This is for FLAT BOTTOM
		if(deltaY_1 != 0){
			traverseAndFill(y0,y1,x0,y0,inverse_slope_1,inverse_slope_2,
							x0,y0,w0,
							x1,y1,w1,
							x2,y2,w2,
							u0,v0,
							u1,v1,
							u2,v2,true
			);
		}

		deltaY_1 = y2-y1; //Change from above
		deltaY_2 = y2-y0;

		inverse_slope_1=0;
		inverse_slope_2=0;

		if(deltaY_1 != 0) inverse_slope_1 = (x2-x1) / deltaY_1;
		if(deltaY_2 != 0) inverse_slope_2 = (x2-x0) / deltaY_2;

		//This is for FLAT TOP
		if(deltaY_1 != 0){
			traverseAndFill(y1,y2,x2,y2,inverse_slope_1,inverse_slope_2,
					x0,y0,w0,
					x1,y1,w1,
					x2,y2,w2,
					u0,v0,
					u1,v1,
					u2,v2,false);
		}
	}
	boolean is_top_left(int x0,int y0,int x1,int y1){
		int edge_x = x1 - x0;
		int edge_y = y1 - y0;

		boolean is_top_left =  edge_y == 0 && edge_x >0;
		boolean is_left_edge =  edge_y <0;

		return is_top_left || is_left_edge;
	}


	private void traverseAndFill(int yTop, int yDown, int xPassPoint , int yPassPoint,
								 float inverse_slope_1, float inverse_slope_2,
								 int x0, int y0, float w0,
								 int x1, int y1, float w1,
								 int x2, int y2, float w2,
								 float u0, float v0,
								 float u1, float v1,
								 float u2, float v2, boolean isBottomFlat
	)
	{
		final float preCalBigArea= GfxMath.areaParallelogram(x0,y0,x1,y1,x2,y2);
		for(int y = yTop ; y<=yDown; y++){
			float xstart =(xPassPoint + (y-yPassPoint)*inverse_slope_1);
			float xend = 	(xPassPoint + (y-yPassPoint)*inverse_slope_2);
			if(xstart > xend){
				float temp = xstart;
				xstart = xend;
				xend = temp;
			}
			for(int x = (int) xstart; x <= (int)xend ; x++){
				//Barcycentric weight Can be optimized here - like no need to recalculate area of the same big triangle
				//Alos need to take of the case point is on edge of triangle
				if(x == (int)xstart)
					GfxMath.baryCentricWeight(x0,y0,x1,y1,x2,y2,xstart,y,preCalBigArea,weights);
				else if(x == (int)xend)
					GfxMath.baryCentricWeight(x0,y0,x1,y1,x2,y2,xend,y,preCalBigArea,weights);
				else
					GfxMath.baryCentricWeight(x0,y0,x1,y1,x2,y2,x,y,preCalBigArea,weights);

//				if(weights[0] < 0 || weights[1] < 0 || weights[2] < 0)
//					continue;;

				//Interpolate Z
				if(depth_test) {
					float finalZ = 1.0f - (weights[0] / w0 + weights[1] / w1 + weights[2] / w2);
					if (finalZ < zBuffer[y * m_width + x]) {
						continue;
					}
					zBuffer[y * m_width + x] = finalZ;
					if(zMin > finalZ) zMin = finalZ;
					if(zMax < finalZ) zMax = finalZ;
				}

				final int index = (y * m_width + x) * 4;

				if(texture== null) {
					m_pixelComponents[index] = (byte) 255;
					m_pixelComponents[index + 1] = (byte) 125;
					m_pixelComponents[index + 2] = (byte) 125;
					m_pixelComponents[index + 3] = (byte) 125;
					continue;
				}

				//Interpolate U V coordinate
				float finalU = weights[0]*u0 + weights[1]*u1 + weights[2]*u2;
				float finalV = weights[0]*v0 + weights[1]*v1 + weights[2]*v2;

//				finalV = finalV < 0 ? 0 :finalV;
//				finalU = finalU < 0 ? 0 :finalU;
//				finalV = finalV > 1 ? 1 :finalV;
//				finalU = finalU > 1 ? 1 :finalU;

				if(filter ==0) {
					int textX = (int) (finalU * (textW -1));
					int textY = (int) (finalV*  (textH -1));

					if(textX < 0 || textX >= textW) throw new RuntimeException();
					if(textY < 0 || textY >= textH) throw new RuntimeException();

					//Nearest neightbor (no filtering)
					final int textIndex =(textY*textW+textX)*4;
//					byte textR = this.texture[textIndex+3];
//					byte textG = this.texture[textIndex+2];
//					byte textB = this.texture[textIndex+1];

					m_pixelComponents[index]=(byte)255;
					m_pixelComponents[index+1]= this.texture[textIndex+1];
					m_pixelComponents[index+2]= this.texture[textIndex+2];
					m_pixelComponents[index+3]= this.texture[textIndex+3];
				}

			}//end for x
		}//end for y
	}






}
