package test;

import swrast.GfxMath;

public class Main_SignedAreaTest {

    public static void main(String [] args) {
        float area3 = GfxMath.areaTriangle(0, 0, 1, 5, 4, 0);
        float area2 = GfxMath.areaTriangle(1, 1, 3, 3, 7, 1);
        float area1 = GfxMath.areaTriangle(0, 0, 0, 1, 2, 0);

        System.out.println(String.format("Area 1: %f - Area 2: %f - Area 3: %f\n\n\n", area1, area2, area3));

        //Barycentric wieght only care winding order (clockwise in this case)
        //Choosing which vertex to start doesn't matter as long as the winding is correct
        float weights[] = new float[]{0.0f, 0.0f, 0.0f};
        GfxMath.barycentricWeight(0, 0, 0, 1, 2, 0, 0.5f, 0.5f, weights);
        System.out.println("alpha:" + weights[0]);
        System.out.println("beta:" + weights[1]);
        System.out.println("gamma:" + weights[2]);
        System.out.println("Bary sum:" + (weights[0] + weights[1] + weights[2]) + "\n");

        float px = weights[0] * 0 + weights[1] * 0 + weights[2] * 2;
        float py = weights[0] * 0 + weights[1] * 1 + weights[2] * 0;

        //
        GfxMath.barycentricWeight(2, 0, 0, 0, 0, 1, 0.5f, 0.5f, weights);
        System.out.println("alpha:" + weights[0]);
        System.out.println("beta:" + weights[1]);
        System.out.println("gamma:" + weights[2]);
        System.out.println("Bary sum:" + (weights[0] + weights[1] + weights[2]) + "\n");

        px = weights[0] * 0 + weights[1] * 2 + weights[2] * 0;
        py = weights[0] * 0 + weights[1] * 0 + weights[2] * 1;

        //
        GfxMath.barycentricWeight(0, 1,2, 0, 0, 0, 0.5f, 0.5f, weights);
        System.out.println("alpha:" + weights[0]);
        System.out.println("beta:" + weights[1]);
        System.out.println("gamma:" + weights[2]);
        System.out.println("Bary sum:" + (weights[0] + weights[1] + weights[2]) + "\n");

        px = weights[0] * 0 + weights[1] * 2 + weights[2] * 0;
        py = weights[0] * 1 + weights[1] * 0 + weights[2] * 0;
    }
}
