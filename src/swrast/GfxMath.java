package swrast;

public class GfxMath {

    /*
      Calculate area of a triangle from 3 point.

      The signed area of the solution is positive when the vertices
      are oriented counterclockwise and
      negative when oriented clockwise.
     */
    public static float areaTriangle(float x0, float y0, 	float x1, float y1,	float x2, float y2 ){
        float ac_x = x2-x0; float ab_x = x1 - x0;
        float ac_y = y2-y0; float ab_y = y1 - y0;
        return (ac_x * ab_y - ac_y * ab_x)* 0.5f;
    }

    public static float[] baryCentricWeight(float ax, float ay,
                                         float bx, float by,
                                         float cx, float cy,
                                         float px, float py,
                                         float weights[]
    )    {
        //NOTE: On left hand coordinate, the Z is negative from screen to eye , and positive from screen inward

        // AC AB vectors
        float ac_x = cx - ax; float ab_x = bx - ax;
        float ac_y = cy - ay; float ab_y = by - ay;
        // Cross AC AB , gives signed area of big triangle
        float area_abc = ac_x * ab_y - ac_y *ab_x;

        // PC PB
        float pc_x = cx - px; float pb_x = bx - px;
        float pc_y = cy - py; float pb_y = by - py;
        // Cross PC PB, gives signed area of CPA -> weighted for A
        float area_cpb = pc_x*pb_y - pc_y *pb_x;

        /*float ac_x = cx - ax;*/ float ap_x = px - ax;
        /*float ac_y = cy - ay;*/ float ap_y = py - ay;
        // Cross AC AP, give signed area of APC -> weighted for B
        float area_apc = ac_x * ap_y - ac_y*ap_x;

        //Just for testing
        // Cross AP AB, give signed area of BPA -> weighted for C
        /*float ap_x = px - ax; float ab_x = bx - ax;*/
        /*float ap_y = py - ay; float ab_y = by - ay;*/
//        float area_bpa = ap_x*ab_y - ap_y*ab_x;
//
//        System.out.println("Area_abc:"+area_abc/2.0);
//        System.out.println("area_cpb:"+area_cpb/2.0 + " alpha:"+area_cpb/area_abc);
//        System.out.println("area_apc:"+area_apc/2.0 + " beta:"+area_apc/area_abc);
//        System.out.println("area_bpa:"+area_bpa/2.0 + " gamma:"+area_bpa/area_abc);
//
//        System.out.println("Bary sum:"+  (area_cpb +   area_apc + area_bpa)/area_abc);
//
//        System.out.println("gamma:"+ (1 - (area_cpb + area_apc)/area_abc  ));

        //Notice that point P take two vertices from big triangle to create a small triangle that pull/weight the 3rd vertex of big triangle.
        //E.G: Triangle CPB pull/weight A, triangle apc pull/weight B
        weights[0] = area_cpb/area_abc; //Alpha
        weights[1] = area_apc/area_abc; //Beta
        weights[2] = 1 - weights[0] - weights[1]; //Gamma
        return weights;
    }

    static float lerp( float a, float b, float t){
        return a + t*(b-a);
    }

    public class Vector2{
        public float x;
        public float y;

        public String toString(){
            return String.format("Vec2(%.03f,%.03f)",x,y);
        }
    }
}
