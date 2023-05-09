/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class swrast_GfxNative */

#ifndef _Included_swrast_GfxNative
#define _Included_swrast_GfxNative
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     swrast_GfxNative
 * Method:    testDirectBuffer
 * Signature: (Ljava/nio/ByteBuffer;)V
 */
JNIEXPORT void JNICALL Java_swrast_GfxNative_testDirectBuffer
  (JNIEnv *, jclass, jobject);

/*
 * Class:     swrast_GfxNative
 * Method:    testNative
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_swrast_GfxNative_testNative
  (JNIEnv *, jclass);

/*
 * Class:     swrast_GfxNative
 * Method:    testPassInteger
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_swrast_GfxNative_testPassInteger
  (JNIEnv *, jclass, jint);

/*
 * Class:     swrast_GfxNative
 * Method:    printGfxNativeVersion
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_swrast_GfxNative_printGfxNativeVersion
  (JNIEnv *, jclass);

/*
 * Class:     swrast_GfxNative
 * Method:    baryCentricWeight
 * Signature: (FFFFFFFF[F)V
 */
JNIEXPORT void JNICALL Java_swrast_GfxNative_baryCentricWeight
  (JNIEnv *, jclass, jfloat, jfloat, jfloat, jfloat, jfloat, jfloat, jfloat, jfloat, jfloatArray);

/*
 * Class:     swrast_GfxNative
 * Method:    areaTriangle
 * Signature: (FFFFFF)F
 */
JNIEXPORT jfloat JNICALL Java_swrast_GfxNative_areaTriangle
  (JNIEnv *, jclass, jfloat, jfloat, jfloat, jfloat, jfloat, jfloat);

/*
 * Class:     swrast_GfxNative
 * Method:    drawGrid
 * Signature: ([I[BI)V
 */
JNIEXPORT void JNICALL Java_swrast_GfxNative_drawGrid
  (JNIEnv *, jclass, jintArray, jbyteArray, jint);

/*
 * Class:     swrast_GfxNative
 * Method:    drawFlatBottomTriangleSlopeFill
 * Signature: (IIIIIIFFFFFFI[BI[BI)V
 */
JNIEXPORT void JNICALL Java_swrast_GfxNative_drawFlatBottomTriangleSlopeFill
  (JNIEnv *, jclass, jint, jint, jint, jint, jint, jint, jfloat, jfloat, jfloat, jfloat, jfloat, jfloat, jint, jbyteArray, jint, jbyteArray, jint);

/*
 * Class:     swrast_GfxNative
 * Method:    drawFlatTopTriangleSlopeFill
 * Signature: (IIIIIIFFFFFFI[BI[BI)V
 */
JNIEXPORT void JNICALL Java_swrast_GfxNative_drawFlatTopTriangleSlopeFill
  (JNIEnv *, jclass, jint, jint, jint, jint, jint, jint, jfloat, jfloat, jfloat, jfloat, jfloat, jfloat, jint, jbyteArray, jint, jbyteArray, jint);

/*
 * Class:     swrast_GfxNative
 * Method:    copyToByteArray
 * Signature: ([B[BI)V
 */
JNIEXPORT void JNICALL Java_swrast_GfxNative_copyToByteArray
  (JNIEnv *, jclass, jbyteArray, jbyteArray, jint);

#ifdef __cplusplus
}
#endif
#endif
