# Software Triangle Rasterization Experiment
!["Doge"](doc/doge.png?raw=true "Optional Title")
>
>Just want to have some fun!  

# Features / Limitation
- Simple, just two main triangle rasterization functions. No need context creation, windows etc...  
- No Texture Filtering, just nearest neighbor for now. There is codes for billinear texture filter but was removed from the main filling function.
- Texture mapping, but without perspective corrected implemented ( PS1 Vibes !!!!!!!!!!!!!!!!)
- No frustum clipping. - Focus on screenspace rasterization for now.
- No lighting added. - Should be easy, decide to add later.
- No Matrix built in, mostly hand rolling for quick test.  
- Implemented in Java (Uh-oh is it a feature ?!)

# About the codes
- The core rasterizer is just two function `RenderContext::drawTriangleTexture` and `RenderContext::traverseAndFill`.
Function `RenderContext::drawTriangleTexture` breakdowns incoming triangle into bottom flat and flat top triangle then call `RenderContext::traverseAndFill` on each.
Coordinate for `RenderContext::drawTriangleTexture` is in **screen-space** which mean x,y are after projection and viewport transform. 
The `w` argument is original Z of the vertex before perspective projection (but after modelview transform). See `Main_ObjReaderTest.java`, and `Main_PerspectiveTest.java`
- See `Main_WindowlessTest.java` for headless and just triangle rasterization demo.
- Demo code quality is pretty bad, will need some clean up.
- Please use IntelliJ to open the project.

# Technique
## Filling algorithm
Not like in other online tutorials that use modern half-space / Pineda's algorithm
in this repo the classic edge walking (aka scan line ) algorithm is used to fill an arbitrary triangle. <br/>
The big difference between edge-walking and Pineda's algorithm is that during the edge-walking process we know exactly which pixel is belong to a triangle.
while in Pineda's algorithm we have to check each pixel in triangle's bounding box to determine which pixel is in a triangle.<br/>
!["Edge Walking"](doc/edge_walking_hilevel.png?raw=true "Edge Walking")
<br/>At high level the edge walking algorithm works as follows:

- Given a triangle with **(x0,y0)** , **(x1,y1)**, **(x2,y2)** are three vertices and assume that **y2 > y1 > y0**.
- First break down a triangle to two different triangles: a bottom flat one running from y0 to y1, and a flat top one running from y1 to y2
- To fill flat bottom (upper triangle) triangle, we calculate the `xstart` and `xend` for each y where y increase by one in range [y0,y1]. Then we fill those pixels from `xstart` to `xend`.
  - xstart is calculated by: `xstart = x0 + (y-y0)*1/m1`
  - xend is calculated by:   `xend = x0 + (y-y0)*1/m2`
  - Where `m1= (x2-x0)/(y2-y0)`,  `m2= (x1-x0)/(y1-y0)`   are the slopes of two left and right edges.
- To fill flat top (lower triangle) triangle, we calculate the `xstart` and `xend` for each y where y increase by one in range [y1,y2]. Then we fill those pixels from `xstart` to `xend`.
  - xstart is calculated by: `xstart = x2 + (y-y2)*1/m1`
  - xend is calculated by:   `xend = x2 + (y-y2)*1/m2`
  - Where `m1= (x2-x0)/(y2-y0)`,  `m2= (x2-x1)/(y2-y1)`   are the slopes of two left and right edges.   

## Vertex Attributes Interpolation Inside Triangle
In the classic edge-walking algorithm, people normally linear interpolate to get vertex attributes 
at xstart and xend respectively, then again linear interpolate attributes between xstart and xend to get vertex attribute at each pixel in between.
In my filling function I don't use this approach, instead I use **barycentric coordinate** to calculate the weights of 3 vertices at each pixel. 
Once you have the weights at each pixel, vertex attribute at target pixel can be interpolated by
```
 attribute = weights[0]*v0.attribute + weightss[1]*v1.attribute + weights[2]*v2.attribute
 
 // attribute can be : u,v, r,g,b etc....
```
Please note that I currently don't implement perspective correct interpolation.

# Relation to line equation, triangle similarity, and linear interpolation
This section explains how we come up with the formula to calculate `xstart` and `xend`. 
## Line equation
The calculation for `xstart` and `xend` above is derived from the following line equation:
```
 m(x-x0)=(y-y0) // Line equation in point-slope form
```
Where m is the slope and `m = (x1-x0)/(y1-y0)` also **(x1,y1)** and **(x0,y0)** are two points on the line.<br/> 
This means for any value ynew we can calculate a new x value: `xnew = x0 + (ynew-y0)/m`.

## Triangle similarity
Given a line passing two points **(x0,y0)** and **(x1,y1)** by applying x-y coordinate we will have the following figure:  <br/>
!["Triangle similarity"](doc/tris_similarity.png?raw=true "Triangle similarity")
<br/>
The task is to find an xnew value for a given value ynew so that (xnew,ynew) will be a point on the line. Using triangle similarity we have
```
blue_segment / red_segment = green_segment / (green_segment + orange_segment) 
```
However `blue_segment = (x0-xnew)`, `red_segment = (x0-x1)`, `green_segment = (ynew -y0)`, `(green_segment + orange_segment) = (ynew - y0) + (y1 - ynew)  =(y1-y0)`
Replugging in the triangle similarity equation we have
```
(x0-xnew)/(x0-x1) = (ynew-y0)/(y1-y0)
```
=> `xnew = x0 + (ynew-y0)*(x1-x0)/(y1-y0)`  notices `(x1-x0)/(y1-y0)` is the inverse slope of the line.<br/> 
Rewriting it we will get the same equation `xnew = x0 + (ynew-y0)/m`

## Linear Interpolation

!["Triangle similarity"](doc/tris_similarity_delta.png?raw=true "Triangle similarity")
<br/>
Reusing the figure in `triangle similarity` section, you will notice that when y = y0 we have to pick x = x0 (of course point (x0,y0) is on the line), again when y = y1 then x= x1. 
<br/>Notice the length of `Delta_Xnew` is changed when we move ynew up and down between [y0,y1].<br/> 
The length of `Delta_Xnew = Delta_X` when `ynew = y0` and `Delta_Xnew =0` when `ynew = y1`.
<br/>So now the problem is to find how much we can scale (_call it a scale factor_) `Delta_X` to get `Delta_Xnew`. Once we get `Delta_xnew` we can get xnew simply by `xnew = x1 + Delta_Xnew`
But the scale factor can be calculated by `factor = Delta_Ynew / Delta_Y = (y-y0)/(y1-y0)`. <br/>
Now we have `xnew = x1 + factor*Delta_X` 


# About filling convention
The `traverseAndFill` function use top-left filling rules. A pixel center (x+0.5, y+0.5) is used to test if current pixel should be lit or not.
Details about filling convention & explanation will be added later. 
I found that the doc at MS about filling rules is a bit confusing https://learn.microsoft.com/en-us/windows/win32/direct3d11/d3d10-graphics-programming-guide-rasterizer-stage-rules
but still leave it here for reference.

# References:
- http://www.sunshine2k.de/coding/java/TriangleRasterization/TriangleRasterization.html
- https://trenki2.github.io/blog/2017/06/06/developing-a-software-renderer-part1/
- Pikuma How Does Triangle Rasterization Work? https://www.youtube.com/watch?v=k5wtuKWmV48