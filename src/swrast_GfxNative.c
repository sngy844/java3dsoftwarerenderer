#include "swrast_GfxNative.h"

// Implementation of the native method sayHello()
JNIEXPORT void JNICALL Java_swrast_GfxNative_testNative(JNIEnv* env, jobject thisObj) {
	printf("Hello World!\n");


}

JNIEXPORT void JNICALL Java_swrast_GfxNative_testPassInteger(JNIEnv* env, jclass ojb, jint intValue)
{
	printf("Integer value:%i",intValue);

}

JNIEXPORT void JNICALL Java_swrast_GfxNative_printGfxNativeVersion(JNIEnv* env, jclass obj) {
#ifdef NDEBUG
	printf("Gfx Native - RELEASE\n");
#else
	printf("Gfx Native - DEBUG\n");
#endif
}

JNIEXPORT void JNICALL Java_swrast_GfxNative_testDirectBuffer (JNIEnv *env, jclass jclass, jobject jbuffer){
    int * buffer =(*env)->GetDirectBufferAddress(env,jbuffer);
    jlong length = (*env)->GetDirectBufferCapacity(env,jbuffer);
    if(buffer){
        for(int i =0 ; i < length;i++)
            buffer[i] = i;
    }
}



JNIEXPORT void JNICALL Java_swrast_GfxNative_baryCentricWeight
(JNIEnv* env, jclass obj, jfloat ax, jfloat ay, jfloat bx, jfloat by, jfloat cx, jfloat cy, jfloat px, jfloat py, jfloatArray weights) {
	jfloat * inCweights =(*env)->GetFloatArrayElements(env, weights, NULL);
	/*inCweights[0] = 2.5f;
	inCweights[1] = 3.7f;
	inCweights[2] = 4.8f;*/

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


	(*env)->ReleaseFloatArrayElements(env, weights, inCweights,0);
}

JNIEXPORT jfloat JNICALL Java_swrast_GfxNative_areaTriangle(JNIEnv* env, jclass obj, jfloat x0, jfloat y0, jfloat x1, jfloat y1, jfloat x2, jfloat y2)
{
    float ac_x = x2 - x0; float ab_x = x1 - x0;
    float ac_y = y2 - y0; float ab_y = y1 - y0;
    return (ac_x * ab_y - ac_y * ab_x) * 0.5f;
}

JNIEXPORT void JNICALL Java_swrast_GfxNative_drawGrid(JNIEnv* env, jclass obj, jintArray jGridIndex, jbyteArray jPixelComponent, jint length) {
    int * gridIndex = (*env)->GetPrimitiveArrayCritical(env, jGridIndex, 0);
    char * pixelComponent = (*env)->GetPrimitiveArrayCritical(env, jPixelComponent, 0);

	long index;
    for (long i = 0; i < length; i++) {
        index = gridIndex[i];
        pixelComponent[index] = 255;
        pixelComponent[index + 1] = 255;
        pixelComponent[index + 2] = 255;
        pixelComponent[index + 3] = 255;
    }

    (*env)->ReleasePrimitiveArrayCritical(env, jPixelComponent, pixelComponent, 0);
    (*env)->ReleasePrimitiveArrayCritical(env, jGridIndex, gridIndex, 0);
}


static void baryCentricWeight(float ax, float ay, float bx, float by, float cx, float cy, float px, float py,float * inCweights) {
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

static float lerp(float a, float b, float t) {
	return a + t * (b - a);
}

static int numThreadToRun =1;

JNIEXPORT void JNICALL Java_swrast_GfxNative_drawFlatBottomTriangleSlopeFill
(JNIEnv* env, jclass obj,
    jint x0, jint y0, jint x1, jint y1, jint x2, jint y2,
    jfloat u0, jfloat v0, jfloat u1, jfloat v1, jfloat u2, jfloat v2,
    jint filter, jbyteArray jTexture, jint textW ,jbyteArray jPixelComponent, jint m_width) {

//    jboolean isCopy=0;
//	char * m_pixelComponents = (*env)->GetByteArrayElements(env, jPixelComponent, &isCopy);
//	if(isCopy){
//	    (*env)->ReleaseByteArrayElements(env, jPixelComponent, m_pixelComponents, 0);
//	    return;
//	}
//	isCopy=0;
//	char * texture = (*env)->GetByteArrayElements(env, jTexture, &isCopy);
//    if(isCopy){
//    (*env)->ReleaseByteArrayElements(env, jTexture, texture, 0);
//	    return;
//	}
//
	char * m_pixelComponents= (*env)->GetPrimitiveArrayCritical(env,jPixelComponent,0);
	if(!m_pixelComponents) return;
	char * texture= (*env)->GetPrimitiveArrayCritical(env,jTexture,0);
	if(!texture) return;

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
#pragma omp parallel for num_threads(numThreadToRun)
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
				//Nearest neightbor (no filtering)
				char textR = texture[(textY * textW + textX) * 4 + 3];
				char textG = texture[(textY * textW + textX) * 4 + 2];
				char textB = texture[(textY * textW + textX) * 4 + 1];
				//DrawPixel(x,y,r,g,b); //Cost of function call
				int index = (y * m_width + x) * 4;
				m_pixelComponents[index] = 255;
				m_pixelComponents[index + 1] = textB;
				m_pixelComponents[index + 2] = textG;
				m_pixelComponents[index + 3] = textR;
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
				m_pixelComponents[index] = 255;
				m_pixelComponents[index + 1] = (char)y_targetB;
				m_pixelComponents[index + 2] = (char)y_targetG;
				m_pixelComponents[index + 3] = (char)y_targetR;
			}
		} // end for each x span start end
	}//end for each y scan line
	
	//printf("From C\n");
	
//	(*env)->ReleaseByteArrayElements(env, jPixelComponent, m_pixelComponents, 0);
//	(*env)->ReleaseByteArrayElements(env, jTexture, texture, 0);

    (*env)->ReleasePrimitiveArrayCritical(env,jPixelComponent, m_pixelComponents, 0);
    (*env)->ReleasePrimitiveArrayCritical(env, jTexture, texture, 0);
}



JNIEXPORT void JNICALL Java_swrast_GfxNative_drawFlatTopTriangleSlopeFill
(JNIEnv* env, jclass obj,
	jint x0, jint y0, jint x1, jint y1, jint x2, jint y2,
	jfloat u0, jfloat v0, jfloat u1, jfloat v1, jfloat u2, jfloat v2,
	jint filter, jbyteArray jTexture, jint textW, jbyteArray jPixelComponent, jint m_width) {

	//    jboolean isCopy=0;
    //	char * m_pixelComponents = (*env)->GetByteArrayElements(env, jPixelComponent, &isCopy);
    //	if(isCopy){
    //	    (*env)->ReleaseByteArrayElements(env, jPixelComponent, m_pixelComponents, 0);
    //	    return;
    //	}
    //	isCopy=0;
    //	char * texture = (*env)->GetByteArrayElements(env, jTexture, &isCopy);
    //    if(isCopy){
    //    (*env)->ReleaseByteArrayElements(env, jTexture, texture, 0);
    //	    return;
    //	}
    //
    char * m_pixelComponents= (*env)->GetPrimitiveArrayCritical(env,jPixelComponent,0);
    if(!m_pixelComponents) return;
    char * texture= (*env)->GetPrimitiveArrayCritical(env,jTexture,0);
    if(!texture) return;

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
#pragma omp parallel for num_threads(numThreadToRun)
		for (x = (int)(xstart); x <= (int)xend; x++) {
	/*		int numThread = omp_get_thread_num();
			printf("Num Thread:%d\n", numThread);*/
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
				//Nearest neightbor (no filtering)
				char textR = texture[(textY * textW + textX) * 4 + 3];
				char textG = texture[(textY * textW + textX) * 4 + 2];
				char textB = texture[(textY * textW + textX) * 4 + 1];
				//DrawPixel(x,y,r,g,b); //Cost of function call
				int index = (y * m_width + x) * 4;
				m_pixelComponents[index] = 255;
				m_pixelComponents[index + 1] = textB;
				m_pixelComponents[index + 2] = textG;
				m_pixelComponents[index + 3] = textR;
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
				m_pixelComponents[index] = 255;
				m_pixelComponents[index + 1] = (char)y_targetB;
				m_pixelComponents[index + 2] = (char)y_targetG;
				m_pixelComponents[index + 3] = (char)y_targetR;
			}
		} // end for each x span start end
	}//end for each y scan line


//	(*env)->ReleaseByteArrayElements(env, jPixelComponent, m_pixelComponents, 0);
//	(*env)->ReleaseByteArrayElements(env, jTexture, texture, 0);

    (*env)->ReleasePrimitiveArrayCritical(env,jPixelComponent, m_pixelComponents, 0);
    (*env)->ReleasePrimitiveArrayCritical(env, jTexture, texture, 0);

}



JNIEXPORT void JNICALL Java_swrast_GfxNative_copyToByteArray(JNIEnv* env, jclass obj, jbyteArray jdest, jbyteArray jPixelComponent, jint totalPixels) {

	char* m_pixelComponents = (*env)->GetPrimitiveArrayCritical(env, jPixelComponent, 0);
	if (!m_pixelComponents) return;
	char* dest = (*env)->GetPrimitiveArrayCritical(env, jdest, 0);
	if (!dest) return;

	int i = 0;
#pragma omp parallel for num_threads(1)
	for (i = 0; i < totalPixels; i++)
	{
		int index3 = i * 3; 		int index4 = i * 4;
		dest[index3] =		m_pixelComponents[index4 + 1];
		dest[index3 + 1] = m_pixelComponents[index4 + 2];
		dest[index3 + 2] = m_pixelComponents[index4 + 3];
	}


	(*env)->ReleasePrimitiveArrayCritical(env, jPixelComponent, m_pixelComponents, 0);
	(*env)->ReleasePrimitiveArrayCritical(env, jdest, dest, 0);
}