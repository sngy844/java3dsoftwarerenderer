public class GfxMath {

    /*
     * Calculate area of a triangle from 3 point. Vertices are in clockwise winding
     * */
    public static float areaTriangle(float x0, float y0, 	float x1, float y1,	float x2, float y2 ){
        float ac_x = x2-x0; float ab_x = x1 - x0;
        float ac_y = y2-y0; float ab_y = y1 - y0;
        return (ac_x * ab_y - ac_y * ab_x)* 0.5f;
    }


    public class Vector2{
        public float x;
        public float y;

        public String toString(){
            return String.format("Vec2(%.03f,%.03f)",x,y);
        }
    }
}
