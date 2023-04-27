package test;

import swrast.GfxMath;

public class Main_SignedAreaTest {

    public static void main(String [] args){
        float area3 = GfxMath.areaTriangle( 0,0 ,1,5,4,0);
        float area2 = GfxMath.areaTriangle( 1,1 ,3,3,7,1);
        float area1 = GfxMath.areaTriangle( 0,0 ,0,1,2,0);

        System.out.println(String.format("Area 1: %f - Area 2: %f - Area 3: %f",area1,area2,area3));

        GfxMath gfxMath = new GfxMath();
        GfxMath.Vector2 vec2 = gfxMath.new Vector2();
        vec2.x = 10;
        vec2.y = 11;
        System.out.println(vec2);
    }
}
