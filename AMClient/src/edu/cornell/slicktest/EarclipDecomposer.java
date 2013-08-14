package edu.cornell.slicktest;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jbox2d.common.Vec2;

/*
 * C# Version Ported by Matt Bettcher and Ian Qvist 2009-2010
 * 
 * Original C++ Version Copyright (c) 2007 Eric Jordan
 *
 * This software is provided 'as-is', without any express or implied
 * warranty.  In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 1. The origin of this software must not be misrepresented; you must not
 * claim that you wrote the original software. If you use this software
 * in a product, an acknowledgment in the product documentation would be
 * appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 * misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
*/

    /// <summary>
    /// Ported from jBox2D. Original author: ewjordan 
    /// Triangulates a polygon using simple ear-clipping algorithm.
    /// 
    /// Only works on simple polygons.
    /// 
    /// Triangles may be degenerate, especially if you have identical points
    /// in the input to the algorithm.  Check this before you use them.
    /// </summary>
    public class EarclipDecomposer
    {
        private static final int MaxPolygonVertices = 8;
		//box2D rev 32 - for details, see http://www.box2d.org/forum/viewtopic.php?f=4&t=83&start=50

        private static final float Tol = .001f;

        /// <summary>
        /// Decomposes a non-convex polygon into a number of convex polygons, up
        /// to maxPolys (remaining pieces are thrown out).
        ///
        /// Each resulting polygon will have no more than Settings.MaxPolygonVertices
        /// vertices.
        /// 
        /// Warning: Only works on simple polygons
        /// </summary>
        /// <param name="vertices">The vertices.</param>
        /// <returns></returns>
        public static List<Vertices> ConvexPartition(Vertices[] vertices)
        {
            return ConvexPartition(vertices, Integer.MAX_VALUE, 0);
        }

        /// <summary>
        /// Decomposes a non-convex polygon into a number of convex polygons, up
        /// to maxPolys (remaining pieces are thrown out).
        /// Each resulting polygon will have no more than Settings.MaxPolygonVertices
        /// vertices.
        /// Warning: Only works on simple polygons
        /// </summary>
        /// <param name="vertices">The vertices.</param>
        /// <param name="maxPolys">The maximum number of polygons.</param>
        /// <param name="tolerance">The tolerance.</param>
        /// <returns></returns>
        public static List<Vertices> ConvexPartition(Vertices[] vertices, int maxPolys, float tolerance)
        {
            if (vertices[0].size() < 3){
            	List<Vertices> toReturn = new LinkedList<Vertices>();
            	toReturn.add(vertices[0]);
                return toReturn;
            }
            /*
            if (vertices.IsConvex() && vertices.size() <= Settings.MaxPolygonVertices)
            {
                if (vertices.IsCounterClockWise())
                {
                    Vertices tempP = new Vertices(vertices);
                    tempP.Reverse();
                    tempP = SimplifyTools.CollinearSimplify(tempP);
                    tempP.ForceCounterClockWise();
                    return new List<Vertices> { tempP };
                }
                vertices = SimplifyTools.CollinearSimplify(vertices);
                vertices.ForceCounterClockWise();
                return new List<Vertices> { vertices };
            }
            */
            List<MSTriangle> triangulated;

            if (vertices[0].IsCounterClockWise())
            {
                Vertices[] tempP = {new Vertices(vertices[0])};
                tempP[0].Reverse();
                triangulated = TriangulatePolygon(tempP);
            }
            else
            {
                triangulated = TriangulatePolygon(vertices);
            }
            if (triangulated.size() < 1)
            {
                //Still no luck?  Oh well...
                //throw new Exception("Can't triangulate your polygon.");
            }

            List<Vertices> polygonizedTriangles = PolygonizeTriangles(triangulated, maxPolys, tolerance);

            //The polygonized triangles are not guaranteed to be without collinear points. We remove
            //them to be sure.
            for (int i = 0; i < polygonizedTriangles.size(); i++)
            {
                polygonizedTriangles.set(i, MSTerrain.CollinearSimplify(polygonizedTriangles.get(i), 0));
            }

            //Remove empty vertice collections
            for (int i = polygonizedTriangles.size() - 1; i >= 0; i--)
            {
                if (polygonizedTriangles.get(i).size() == 0)
                    polygonizedTriangles.remove(i);
            }

            return polygonizedTriangles;
        }

        /// <summary>
        /// Turns a list of triangles into a list of convex polygons. Very simple
        /// method - start with a seed triangle, keep adding triangles to it until
        /// you can't add any more without making the polygon non-convex.
        ///
        /// Returns an integer telling how many polygons were created.  Will fill
        /// polys array up to polysLength entries, which may be smaller or larger
        /// than the return value.
        /// 
        /// Takes O(N///P) where P is the number of resultant polygons, N is triangle
        /// size().
        /// 
        /// The final polygon list will not necessarily be minimal, though in
        /// practice it works fairly well.
        /// </summary>
        /// <param name="triangulated">The triangulated.</param>
        ///<param name="maxPolys">The maximun number of polygons</param>
        ///<param name="tolerance">The tolerance</param>
        ///<returns></returns>
        public static List<Vertices> PolygonizeTriangles(List<MSTriangle> triangulated, int maxPolys, float tolerance)
        {
            List<Vertices> polys = new ArrayList<Vertices>(50);

            int polyIndex = 0;

            if (triangulated.size() <= 0)
            {
                //return empty polygon list
                return polys;
            }

            boolean[] covered = new boolean[triangulated.size()];
            for (int i = 0; i < triangulated.size(); ++i)
            {
                covered[i] = false;

                //Check here for degenerate triangles
                if (((triangulated.get(i).X[0] == triangulated.get(i).X[1]) && (triangulated.get(i).Y[0] == triangulated.get(i).Y[1]))
                    ||
                    ((triangulated.get(i).X[1] == triangulated.get(i).X[2]) && (triangulated.get(i).Y[1] == triangulated.get(i).Y[2]))
                    ||
                    ((triangulated.get(i).X[0] == triangulated.get(i).X[2]) && (triangulated.get(i).Y[0] == triangulated.get(i).Y[2])))
                {
                    covered[i] = true;
                }
            }

            boolean notDone = true;
            while (notDone)
            {
                int currTri = -1;
                for (int i = 0; i < triangulated.size(); ++i)
                {
                    if (covered[i])
                        continue;
                    currTri = i;
                    break;
                }
                if (currTri == -1)
                {
                    notDone = false;
                }
                else
                {
                    Vertices poly = new Vertices(3);

                    for (int i = 0; i < 3; i++)
                    {
                        poly.add(new Vec2(triangulated.get(currTri).X[i], triangulated.get(currTri).Y[i]));
                    }

                    covered[currTri] = true;
                    int index = 0;
                    for (int i = 0; i < 2 * triangulated.size(); ++i, ++index)
                    {
                        while (index >= triangulated.size()) index -= triangulated.size();
                        if (covered[index])
                        {
                            continue;
                        }
                        Vertices newP = AddTriangle(triangulated.get(index), poly);
                        if (newP == null)
                            continue; // is this right

                        if (newP.size() > MaxPolygonVertices)
                            continue;

                        if (newP.IsConvex())
                        {
                            //Or should it be IsUsable?  Maybe re-write IsConvex to apply the angle threshold from Box2d
                            poly = new Vertices(newP);
                            covered[index] = true;
                        }
                    }

                    //We have a maximum of polygons that we need to keep under.
                    if (polyIndex < maxPolys)
                    {
                        //SimplifyTools.MergeParallelEdges(poly, tolerance);

                        //If identical points are present, a triangle gets
                        //borked by the MergeParallelEdges function, hence
                        //the vertex number check
                        if (poly.size() >= 3)
                            polys.add(new Vertices(poly));
                        //else
                        //    printf("Skipping corrupt poly\n");
                    }
                    if (poly.size() >= 3)
                        polyIndex++; //Must be outside (polyIndex < polysLength) test
                }
            }

            return polys;
        }

        /// <summary>
        /// Triangulates a polygon using simple ear-clipping algorithm. Returns
        /// size of Triangle array unless the polygon can't be triangulated.
        /// This should only happen if the polygon self-intersects,
        /// though it will not _always_ return null for a bad polygon - it is the
        /// caller's responsibility to check for self-intersection, and if it
        /// doesn't, it should at least check that the return value is non-null
        /// before using. You're warned!
        ///
        /// Triangles may be degenerate, especially if you have identical points
        /// in the input to the algorithm.  Check this before you use them.
        ///
        /// This is totally unoptimized, so for large polygons it should not be part
        /// of the simulation loop.
        ///
        /// Warning: Only works on simple polygons.
        /// </summary>
        /// <returns></returns>
        public static List<MSTriangle> TriangulatePolygon(Vertices[] vertices)
        {
            List<MSTriangle> results = new ArrayList<MSTriangle>();
            if (vertices[0].size() < 3)
                return new ArrayList<MSTriangle>();

            //Recurse and split on pinch points
            Vertices[] pA = new Vertices[1], pB = new Vertices[1];
            Vertices pin = new Vertices(vertices[0]);
            if (ResolvePinchPoint(pin, pA, pB))
            {
                List<MSTriangle> mergeA = TriangulatePolygon(pA);
                List<MSTriangle> mergeB = TriangulatePolygon(pB);

                if (mergeA.size() == -1 || mergeB.size() == -1)
                    //throw new Exception("Can't triangulate your polygon.");

                for (int i = 0; i < mergeA.size(); ++i)
                {
                    results.add(new MSTriangle(mergeA.get(i)));
                }
                for (int i = 0; i < mergeB.size(); ++i)
                {
                    results.add(new MSTriangle(mergeB.get(i)));
                }

                return results;
            }

            MSTriangle[] buffer = new MSTriangle[vertices[0].size() - 2];
            int bufferSize = 0;
            float[] xrem = new float[vertices[0].size()];
            float[] yrem = new float[vertices[0].size()];
            for (int i = 0; i < vertices[0].size(); ++i)
            {
                xrem[i] = vertices[0].get(i).x;
                yrem[i] = vertices[0].get(i).y;
            }

            int vNum = vertices[0].size();

            while (vNum > 3)
            {
                // Find an ear
                int earIndex = -1;
                float earMaxMinCross = -10.0f;
                for (int i = 0; i < vNum; ++i)
                {
                    if (IsEar(i, xrem, yrem, vNum))
                    {
                        int lower = Remainder(i - 1, vNum);
                        int upper = Remainder(i + 1, vNum);
                        Vec2 d1 = new Vec2(xrem[upper] - xrem[i], yrem[upper] - yrem[i]);
                        Vec2 d2 = new Vec2(xrem[i] - xrem[lower], yrem[i] - yrem[lower]);
                        Vec2 d3 = new Vec2(xrem[lower] - xrem[upper], yrem[lower] - yrem[upper]);

                        d1.normalize();
                        d2.normalize();
                        d3.normalize();
                        float cross12 = Vec2.cross(d1,  d2);
                        cross12 = Math.abs(cross12);

                        float cross23 = Vec2.cross(d2,  d3);
                        cross23 = Math.abs(cross23);

                        float cross31 = Vec2.cross(d3,  d1);
                        cross31 = Math.abs(cross31);

                        //Find the maximum minimum angle
                        float minCross = Math.min(cross12, Math.min(cross23, cross31));
                        if (minCross > earMaxMinCross)
                        {
                            earIndex = i;
                            earMaxMinCross = minCross;
                        }
                    }
                }

                // If we still haven't found an ear, we're screwed.
                // Note: sometimes this is happening because the
                // remaining points are collinear.  Really these
                // should just be thrown out without halting triangulation.
                if (earIndex == -1)
                {
                    for (int i = 0; i < bufferSize; i++)
                    {
                        results.add(new MSTriangle(buffer[i]));
                    }

                    return results;
                }

                // Clip off the ear:
                // - remove the ear tip from the list

                --vNum;
                float[] newx = new float[vNum];
                float[] newy = new float[vNum];
                int currDest = 0;
                for (int i = 0; i < vNum; ++i)
                {
                    if (currDest == earIndex) ++currDest;
                    newx[i] = xrem[currDest];
                    newy[i] = yrem[currDest];
                    ++currDest;
                }

                // - add the clipped triangle to the triangle list
                int under = (earIndex == 0) ? (vNum) : (earIndex - 1);
                int over = (earIndex == vNum) ? 0 : (earIndex + 1);
                MSTriangle toAdd = new MSTriangle(xrem[earIndex], yrem[earIndex], xrem[over], yrem[over], xrem[under],
                                              yrem[under]);
                buffer[bufferSize] = toAdd;
                ++bufferSize;

                // - replace the old list with the new one
                xrem = newx;
                yrem = newy;
            }

            MSTriangle tooAdd = new MSTriangle(xrem[1], yrem[1], xrem[2], yrem[2], xrem[0], yrem[0]);
            buffer[bufferSize] = tooAdd;
            ++bufferSize;

            for (int i = 0; i < bufferSize; i++)
            {
                results.add(new MSTriangle(buffer[i]));
            }

            return results;
        }

        /// <summary>
        /// Finds and fixes "pinch points," points where two polygon
        /// vertices are at the same point.
        /// 
        /// If a pinch point is found, pin is broken up into poutA and poutB
        /// and true is returned; otherwise, returns false.
        /// 
        /// Mostly for internal use.
        /// 
        /// O(N^2) time, which sucks...
        /// </summary>
        /// <param name="pin">The pin.</param>
        /// <param name="poutA">The pout A.</param>
        /// <param name="poutB">The pout B.</param>
        /// <returns></returns>
        private static boolean ResolvePinchPoint(Vertices pin, Vertices[] poutA, Vertices[] poutB)
        {
            poutA[0] = new Vertices();
            poutB[0] = new Vertices();

            if (pin.size() < 3)
                return false;

            boolean hasPinchPoint = false;
            int pinchIndexA = -1;
            int pinchIndexB = -1;
            for (int i = 0; i < pin.size(); ++i)
            {
                for (int j = i + 1; j < pin.size(); ++j)
                {
                    //Don't worry about pinch points where the points
                    //are actually just dupe neighbors
                    if (Math.abs(pin.get(i).x - pin.get(j).x) < Tol && Math.abs(pin.get(i).y - pin.get(j).y) < Tol && j != i + 1)
                    {
                        pinchIndexA = i;
                        pinchIndexB = j;
                        hasPinchPoint = true;
                        break;
                    }
                }
                if (hasPinchPoint) break;
            }
            if (hasPinchPoint)
            {
                int sizeA = pinchIndexB - pinchIndexA;
                if (sizeA == pin.size()) return false; //has dupe points at wraparound, not a problem here
                for (int i = 0; i < sizeA; ++i)
                {
                    int ind = Remainder(pinchIndexA + i, pin.size()); // is this right
                    poutA[0].add(pin.get(ind));
                }

                int sizeB = pin.size() - sizeA;
                for (int i = 0; i < sizeB; ++i)
                {
                    int ind = Remainder(pinchIndexB + i, pin.size()); // is this right    
                    poutB[0].add(pin.get(ind));
                }
            }
            return hasPinchPoint;
        }

        /// <summary>
        /// Fix for obnoxious behavior for the % operator for negative numbers...
        /// </summary>
        /// <param name="x">The x.</param>
        /// <param name="modulus">The modulus.</param>
        /// <returns></returns>
        private static int Remainder(int x, int modulus)
        {
            int rem = x % modulus;
            while (rem < 0)
            {
                rem += modulus;
            }
            return rem;
        }

        private static Vertices AddTriangle(MSTriangle t, Vertices vertices)
        {
            // First, find vertices that connect
            int firstP = -1;
            int firstT = -1;
            int secondP = -1;
            int secondT = -1;
            for (int i = 0; i < vertices.size(); i++)
            {
                if (t.X[0] == vertices.get(i).x && t.Y[0] == vertices.get(i).y)
                {
                    if (firstP == -1)
                    {
                        firstP = i;
                        firstT = 0;
                    }
                    else
                    {
                        secondP = i;
                        secondT = 0;
                    }
                }
                else if (t.X[1] == vertices.get(i).x && t.Y[1] == vertices.get(i).y)
                {
                    if (firstP == -1)
                    {
                        firstP = i;
                        firstT = 1;
                    }
                    else
                    {
                        secondP = i;
                        secondT = 1;
                    }
                }
                else if (t.X[2] == vertices.get(i).x && t.Y[2] == vertices.get(i).y)
                {
                    if (firstP == -1)
                    {
                        firstP = i;
                        firstT = 2;
                    }
                    else
                    {
                        secondP = i;
                        secondT = 2;
                    }
                }
            }
            // Fix ordering if first should be last vertex of poly
            if (firstP == 0 && secondP == vertices.size() - 1)
            {
                firstP = vertices.size() - 1;
                secondP = 0;
            }

            // Didn't find it
            if (secondP == -1)
            {
                return null;
            }

            // Find tip index on triangle
            int tipT = 0;
            if (tipT == firstT || tipT == secondT)
                tipT = 1;
            if (tipT == firstT || tipT == secondT)
                tipT = 2;

            Vertices result = new Vertices(vertices.size() + 1);
            for (int i = 0; i < vertices.size(); i++)
            {
                result.add(vertices.get(i));

                if (i == firstP)
                    result.add(new Vec2(t.X[tipT], t.Y[tipT]));
            }

            return result;
        }

        /// <summary>
        /// Checks if vertex i is the tip of an ear in polygon defined by xv[] and
        /// yv[].
        ///
        /// Assumes clockwise orientation of polygon...ick
        /// </summary>
        /// <param name="i">The i.</param>
        /// <param name="xv">The xv.</param>
        /// <param name="yv">The yv.</param>
        /// <param name="xvLength">Length of the xv.</param>
        /// <returns>
        /// 	<c>true</c> if the specified i is ear; otherwise, <c>false</c>.
        /// </returns>
        private static boolean IsEar(int i, float[] xv, float[] yv, int xvLength)
        {
            float dx0, dy0, dx1, dy1;
            if (i >= xvLength || i < 0 || xvLength < 3)
            {
                return false;
            }
            int upper = i + 1;
            int lower = i - 1;
            if (i == 0)
            {
                dx0 = xv[0] - xv[xvLength - 1];
                dy0 = yv[0] - yv[xvLength - 1];
                dx1 = xv[1] - xv[0];
                dy1 = yv[1] - yv[0];
                lower = xvLength - 1;
            }
            else if (i == xvLength - 1)
            {
                dx0 = xv[i] - xv[i - 1];
                dy0 = yv[i] - yv[i - 1];
                dx1 = xv[0] - xv[i];
                dy1 = yv[0] - yv[i];
                upper = 0;
            }
            else
            {
                dx0 = xv[i] - xv[i - 1];
                dy0 = yv[i] - yv[i - 1];
                dx1 = xv[i + 1] - xv[i];
                dy1 = yv[i + 1] - yv[i];
            }
            float cross = dx0 * dy1 - dx1 * dy0;
            if (cross > 0)
                return false;
            MSTriangle myTri = new MSTriangle(xv[i], yv[i], xv[upper], yv[upper],
                                          xv[lower], yv[lower]);
            for (int j = 0; j < xvLength; ++j)
            {
                if (j == i || j == lower || j == upper)
                    continue;
                if (myTri.IsInside(xv[j], yv[j]))
                    return false;
            }
            return true;
        }
    }

