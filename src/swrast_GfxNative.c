#include "swrast_GfxNative.h"
#include "CGfx.h"

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

	drawFlatBottomTriangleSlopeFill(x0,y0,x1,y1,x2,y2,u0,v0,u1,v1,u2,v2,filter,texture,textW,m_pixelComponents,m_width);
	
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

    char * m_pixelComponents= (*env)->GetPrimitiveArrayCritical(env,jPixelComponent,0);
    if(!m_pixelComponents) return;
    char * texture= (*env)->GetPrimitiveArrayCritical(env,jTexture,0);
    if(!texture) return;


    drawFlatTopTriangleSlopeFill(x0,y0,x1,y1,x2,y2,u0,v0,u1,v1,u2,v2,filter,texture,textW,m_pixelComponents,m_width);


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