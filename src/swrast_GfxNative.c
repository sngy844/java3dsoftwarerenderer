#include "swrast_GfxNative.h"

// Implementation of the native method sayHello()
JNIEXPORT void JNICALL Java_swrast_GfxNative_testNative(JNIEnv* env, jobject thisObj) {
	printf("Hello World!\n");


}

JNIEXPORT void JNICALL Java_swrast_GfxNative_testPassInteger(JNIEnv* env, jclass ojb, jint intValue)
{
	printf("Integer value:%i",intValue);

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
