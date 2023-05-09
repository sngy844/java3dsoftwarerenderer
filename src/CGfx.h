#ifndef CGFX_H
#define CGFX_H


float lerp(float a, float b, float t);
void baryCentricWeight(float ax, float ay, float bx, float by, float cx, float cy, float px, float py,float * inCweights);


void drawFlatBottomTriangleSlopeFill(int x0, int y0, int x1, int y1, int x2, int y2,
                                     float u0, float v0, float u1, float v1, float u2, float v2,
                                     int filter, char * texture, int textW ,char * pixelComponent, int m_width);

void drawFlatTopTriangleSlopeFill(  int x0, int y0, int x1, int y1, int x2, int y2,
                                    float u0, float v0, float u1, float v1, float u2, float v2,
                                    int filter, char * texture, int textW, char * pixelComponents, int m_width);
#endif
