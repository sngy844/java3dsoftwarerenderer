#include "CGfx.h"


float lerp(float a, float b, float t) {
	return a + t * (b - a);
}


void baryCentricWeight(float ax, float ay, float bx, float by, float cx, float cy, float px, float py,float * inCweights) {
	float ac_x = cx - ax; float ab_x = bx - ax;
	float ac_y = cy - ay; float ab_y = by - ay;
	// Cross AC AB , gives signed area of big triangle
	float area_abc = ac_x * ab_y - ac_y * ab_x;

	// PC PB
	float pc_x = cx - px; float pb_x = bx - px;
	float pc_y = cy - py; float pb_y = by - py;
	// Cross PC PB, gives signed area of CPA -> weighted for A
	float area_cpb = pc_x * pb_y - pc_y * pb_x;

	/*float ac_x = cx - ax;*/ float ap_x = px - ax;
	/*float ac_y = cy - ay;*/ float ap_y = py - ay;
	// Cross AC AP, give signed area of APC -> weighted for B
	float area_apc = ac_x * ap_y - ac_y * ap_x;

	inCweights[0] = area_cpb / area_abc; //Alpha
	inCweights[1] = area_apc / area_abc; //Beta
	inCweights[2] = 1 - inCweights[0] - inCweights[1]; //Gamma

}

void drawFlatBottomTriangleSlopeFill(int x0, int y0, int x1, int y1, int x2, int y2,
                                     float u0, float v0, float u1, float v1, float u2, float v2,
                                     int filter, char * texture, int textW ,char * pixelComponents, int m_width){

    float inverse_slope_1 = (float)(x1 - x0) / (y1 - y0); //left side
	float inverse_slope_2 = (float)(x2 - x0) / (y2 - y0); //right side

	float xstart, xend;
	//int index = 0;
	//For each y , find xstart on left side and xend on right side then draw span/line from xstart to xend
	for (int y = y0; y <= y1; y++) {
		//Same result
		//xstart = (x0+ (y - y0)*inverse_slope_1);
		//xend = 	 (x0+ (y - y0)*inverse_slope_2);
		xstart = (x0 + (y - y0) * inverse_slope_1); //x on left side
		xend = (x2 + (y - y2) * inverse_slope_2); //x on right side
		if (xstart > xend) {
			float temp = xstart;
			xstart = xend;
			xend = temp;
		}

		int x = 0;
		//Draw a span / line from xstart to xend
#pragma omp parallel for
		for ( x = (int)(xstart); x <= (int)(xend); x++) {
			float weights[] = { 0,0,0 };
			//Barcycentric weight Can be optimized here - like no need to recalculate area of the same big triangle
			//Alos need to take of the case point is on edge of triangle
			baryCentricWeight(x0, y0, x1, y1, x2, y2, x, y, weights);

			//Interpolate U V coordinate
			float finalU = weights[0] * u0 + weights[1] * u1 + weights[2] * u2;
			float finalV = weights[0] * v0 + weights[1] * v1 + weights[2] * v2;

			if (filter == 0) {
				//Coordinate on real texture - assume texture is square dimension
				int textX = (int)(finalU * (textW - 1));
				int textY = (int)(finalV * (textW - 1));
				//DrawPixel(x,y,r,g,b); //Cost of function call
				int index = (y * m_width + x) * 4;
#ifdef SDL_FORMAT
                //Nearest neightbor (no filtering)
				char textR = texture[(textY * textW + textX) * 4 + 2];
				char textG = texture[(textY * textW + textX) * 4 + 1];
				char textB = texture[(textY * textW + textX) * 4 + 0];
				pixelComponents[index] = textB;
				pixelComponents[index + 1] = textG;
				pixelComponents[index + 2] = textR;
				pixelComponents[index + 3] = 255;
#else
                //Nearest neightbor (no filtering)
				char textR = texture[(textY * textW + textX) * 4 + 3];
				char textG = texture[(textY * textW + textX) * 4 + 2];
				char textB = texture[(textY * textW + textX) * 4 + 1];
				pixelComponents[index] = 255;
				pixelComponents[index + 1] = textB;
				pixelComponents[index + 2] = textG;
				pixelComponents[index + 3] = textR;
#endif
			}
			else if (filter == 1) {
				//Coordinate on real texture - assume texture is square dimension
				int textX = (int)(finalU * (textW - 1));
				int textY = (int)(finalV * (textW - 1));

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
				int index = (y * m_width + x) * 4;
				pixelComponents[index] = 255;
				pixelComponents[index + 1] = (char)y_targetB;
				pixelComponents[index + 2] = (char)y_targetG;
				pixelComponents[index + 3] = (char)y_targetR;
			}
		} // end for each x span start end
	}//end for each y scan line
}


void drawFlatTopTriangleSlopeFill(  int x0, int y0, int x1, int y1, int x2, int y2,
                                    float u0, float v0, float u1, float v1, float u2, float v2,
                                    int filter, char * texture, int textW, char * pixelComponents, int m_width){
const float inverse_slope_1 = (float)(x2 - x0) / (y2 - y0); //left side
	const float inverse_slope_2 = (float)(x2 - x1) / (y2 - y1); //right side


	float xstart, xend;

	//For each y , find xstart on left side and xend on right side then draw span/line from xstart to xend
	for (int y = y0; y <= y2; y++) {
		//Same result
		//xstart = (x0+ (y - y0)*inverse_slope_1);
		//xend = 	 (x0+ (y - y0)*inverse_slope_2);
		xstart = (x0 + (y - y0) * inverse_slope_1); //x on left side
		xend = (x2 + (y - y2) * inverse_slope_2); //x on right side
		if (xstart > xend) {
			float temp = xstart;
			xstart = xend;
			xend = temp;
		}

		int x = 0;
		//Draw a span / line from xstart to xend
#pragma omp parallel for
		for (x = (int)(xstart); x <= (int)xend; x++) {
			//Barcycentric weight Can be optimized here - like no need to recalculate area of the same big triangle
			//Alos need to take of the case point is on edge of triangle
			float weights[] = { 0,0,0 };
			baryCentricWeight(x0, y0, x1, y1, x2, y2, x, y, weights);

			//Interpolate U V coordinate
			float finalU = weights[0] * u0 + weights[1] * u1 + weights[2] * u2;
			float finalV = weights[0] * v0 + weights[1] * v1 + weights[2] * v2;

			if (filter == 0) {
				//Coordinate on real texture - assume texture is square dimension
				int textX = (int)(finalU * (textW - 1));
				int textY = (int)(finalV * (textW - 1));

				int index = (y * m_width + x) * 4;
#ifdef SDL_FORMAT
                //Nearest neightbor (no filtering)
				char textR = texture[(textY * textW + textX) * 4 + 2];
				char textG = texture[(textY * textW + textX) * 4 + 1];
				char textB = texture[(textY * textW + textX) * 4 + 0];
				pixelComponents[index] = textB;
				pixelComponents[index + 1] = textG;
				pixelComponents[index + 2] = textR;
				pixelComponents[index + 3] = 255;
#else
                //Nearest neightbor (no filtering)
				char textR = texture[(textY * textW + textX) * 4 + 3];
				char textG = texture[(textY * textW + textX) * 4 + 2];
				char textB = texture[(textY * textW + textX) * 4 + 1];
				pixelComponents[index] = 255;
				pixelComponents[index + 1] = textB;
				pixelComponents[index + 2] = textG;
				pixelComponents[index + 3] = textR;
#endif
			}
			else if (filter == 1) {
				//Coordinate on real texture - assume texture is square dimension
				int textX = (int)(finalU * (textW - 1));
				int textY = (int)(finalV * (textW - 1));

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
				int index = (y * m_width + x) * 4;
				pixelComponents[index] = 255;
				pixelComponents[index + 1] = (char)y_targetB;
				pixelComponents[index + 2] = (char)y_targetG;
				pixelComponents[index + 3] = (char)y_targetR;
			}
		} // end for each x span start end
	}//end for each y scan line


}