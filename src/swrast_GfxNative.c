#include "swrast_GfxNative.h"
#include "CGfx.h"
#include "upng.h"

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

    baryCentricWeight(ax,ay,bx,by,cx,cy,px,py,inCweights);

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

	char * m_pixelComponents= (*env)->GetPrimitiveArrayCritical(env,jPixelComponent,0);
	if(!m_pixelComponents) return;
	char * texture= (*env)->GetPrimitiveArrayCritical(env,jTexture,0);
	if(!texture) return;

	drawFlatBottomTriangleSlopeFill(x0,y0,x1,y1,x2,y2,u0,v0,u1,v1,u2,v2,filter,texture,textW,m_pixelComponents,m_width);

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


JNIEXPORT jbyteArray JNICALL Java_swrast_GfxNative_openPNGFile(JNIEnv* env, jclass clss, jstring filePath, jintArray jdims) {
    const char* inCStr = (*env)->GetStringUTFChars(env, filePath, NULL);
    printf("In Png file:%s\n", inCStr);
    upng_t * png = upng_new_from_file(inCStr);
    (*env)->ReleaseStringUTFChars(env, filePath, inCStr); //Release java string
    
    if (!png) {
        printf("Can't open png file.\n");
        upng_free(png);
    }

    upng_decode(png);
    const unsigned char* pngPixels = upng_get_buffer(png);
    const int pngBufferSize = upng_get_size(png);
    unsigned int pngWidth = upng_get_width(png);
    unsigned int pngHeight = upng_get_height(png);
    printf("Png Buffer Size:%d\n", pngBufferSize);

    //Return width & height for java size
    int * cDims = (*env)->GetPrimitiveArrayCritical(env, jdims, NULL);
    cDims[0] = pngWidth;
    cDims[1] = pngHeight;
    (*env)->ReleasePrimitiveArrayCritical(env, jdims, cDims, 0);

    //Allocate for java size
    jbyteArray retByteArray = (*env)->NewByteArray(env, pngBufferSize);
    //Copy to java size 
    char * cRetByteArray = (*env)->GetPrimitiveArrayCritical(env, retByteArray,NULL );
    for (long i = 0; i < pngBufferSize; i++)
        cRetByteArray[i] = pngPixels[i];

    (*env)->ReleasePrimitiveArrayCritical(env, retByteArray, cRetByteArray, 0);

    upng_free(png);

    return retByteArray;

}