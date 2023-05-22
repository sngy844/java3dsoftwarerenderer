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

    public static boolean isWindingCW(float x0, float y0, 	float x1, float y1,	float x2, float y2 ){
        float v0_x = x1 -x0; float v0_y = y1 -y0;
        float v1_x = x2 -x0; float v1_y = y2 -y0;
        return v0_x * v1_y - v1_x*v0_y >= 0 ;
    }

    public static float areaParallelogram(float ax, float ay,
                                          float bx, float by,
                                          float cx, float cy){
        float ac_x = cx - ax; float ab_x = bx - ax;
        float ac_y = cy - ay; float ab_y = by - ay;
        // Cross AC AB , gives signed area of big triangle
        return  ac_x * ab_y - ac_y *ab_x;
    }

    public static float[] baryCentricWeight(float ax, float ay,
                                            float bx, float by,
                                            float cx, float cy,
                                            float px, float py,
                                            final float preCalBigArea,
                                            float weights[]){
        final float epsilon = 0.0000001f;
        // PC PB
        float pc_x = cx - px; float pb_x = bx - px;
        float pc_y = cy - py; float pb_y = by - py;
        // Cross PC PB, gives signed area of CPA -> weighted for A
        float area_cpb = pc_x*pb_y - pc_y *pb_x;

        float ac_x = cx - ax; float ap_x = px - ax;
        float ac_y = cy - ay; float ap_y = py - ay;
        // Cross AC AP, give signed area of APC -> weighted for B
        float area_apc = ac_x * ap_y - ac_y*ap_x;


        weights[0] = area_cpb/preCalBigArea; //Alpha
        weights[1] = area_apc/preCalBigArea; //Beta
        weights[2] = 1 - (weights[0] +weights[1]); //Gamma

        if(weights[0] < epsilon) weights[0] = 0;
        if(weights[1] < epsilon) weights[1] = 0;
        if(weights[2] < epsilon) weights[2] = 0;
        return  weights;
    }

    public static float[] baryCentricWeight(float ax, float ay,
                                         float bx, float by,
                                         float cx, float cy,
                                         float px, float py,
                                         float weights[]
    )    {
        final float epsilon = 0.0000001f;
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
        weights[2] = 1 - (weights[0] + weights[1]); //Gamma
//        if(weights[0] < epsilon) weights[0] = 0;
//        if(weights[1] < epsilon) weights[1] = 0;
//        if(weights[2] < epsilon) weights[2] = 0;
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

    public static float [][] perspective(float fov, float aspect, float znear, float zfar){

        float  [][] matrix = new float[4][4];
        matrix[0][0] = (float) (aspect * (1 / Math.tan(fov / 2)));
        matrix[1][1] = (float) (1 / Math.tan(fov / 2));
        matrix[2][2] = zfar / (zfar - znear);
        matrix[2][3] = (-zfar * znear) / (zfar - znear);
        matrix[3][2] = 1.0f;

        return matrix;
    }

    public static void mat4_mult_vec4(float[] result, float [][] mat, float [] vec4){
        result[0] = mat[0][0] * vec4[0] + mat[0][1] * vec4[1] + mat[0][2] * vec4[2] + mat[0][3] * vec4[3];
        result[1] = mat[1][0] * vec4[0] + mat[1][1] * vec4[1] + mat[1][2] * vec4[2] + mat[1][3] * vec4[3];
        result[2] = mat[2][0] * vec4[0] + mat[2][1] * vec4[1] + mat[2][2] * vec4[2] + mat[2][3] * vec4[3];
        result[3] = mat[3][0] * vec4[0] + mat[3][1] * vec4[1] + mat[3][2] * vec4[2] + mat[3][3] * vec4[3];
    }

    public static void perspectiveDivide(float [] result){
         if(result[3] != 0.0){
            result[0] /=result[3];
            result[1] /=result[3];
            result[2] /=result[3];
        }
    }

    public static void mat4_mult_vec4_project(float[] result, float [][] mat_proj, float [] vec4){
        mat4_mult_vec4(result,mat_proj,vec4);

    }

    public static void mat4_mult_mat4(float[][] result, float [][] a , float [][]b){
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i][j] = a[i][0] * b[0][j] + a[i][1] * b[1][j] + a[i][2] * b[2][j] + a[i][3] * b[3][j];
            }
        }
    }

    public static void identity(float [][] a){
        for(int i =0 ; i< 4; i++)
            for(int k=0; k<4; k++) {
                if(i == k)
                    a[i][k]=1f;
                else
                    a[i][k] = 0;
            }
    }

    static public float[][] mat4_identity(){
        float [][]  mat = new float[4][4];
        identity(mat);
        return mat;
    }

}
