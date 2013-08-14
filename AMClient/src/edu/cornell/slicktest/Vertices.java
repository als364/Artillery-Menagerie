package edu.cornell.slicktest;

import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.*;

import org.jbox2d.*;
import org.jbox2d.common.Vec2;
//import System;
//using System.Collections.Generic;
//using System.Diagnostics;
//using System.Text;
//using FarseerPhysics.Collision;
//using Microsoft.xna.Framework;
    public class Vertices extends ArrayList<org.jbox2d.common.Vec2>
    {
        private static final double MIN_AREA = 1.192092896e-07f;
		private static final float MIN_ANGLE = (float).0001;
		private static final float MIN_LINEAR_DIST = .01f;
		private static final int MaxPolygonVertices = 8;
		public Vertices()
        {
        }
		
		public Vertices(int size)
        {
			this.ensureCapacity(3);
        }

        public Vertices(org.jbox2d.common.Vec2[] vector2)
        {
            for (int i = 0; i < vector2.length; i++)
            {
                add(vector2[i]);
            }
        }

        public Vertices(List<org.jbox2d.common.Vec2> vertices)
        {
            for (int i = 0; i < vertices.size(); i++)
            {
                add(vertices.get(i));
            }
        }

        /// <summary>
        /// Nexts the index.
        /// </summary>
        /// <param name="index">The index.</param>
        /// <returns></returns>
        public int NextIndex(int index)
        {
            if (index == size() - 1)
            {
                return 0;
            }
            return index + 1;
        }

        public org.jbox2d.common.Vec2 NextVertex(int index)
        {
            return this.get(NextIndex(index));
        }

        /// <summary>
        /// Gets the previous index.
        /// </summary>
        /// <param name="index">The index.</param>
        /// <returns></returns>
        public int PreviousIndex(int index)
        {
            if (index == 0)
            {
                return size() - 1;
            }
            return index - 1;
        }

        public org.jbox2d.common.Vec2 PreviousVertex(int index)
        {
            return this.get(PreviousIndex(index));
        }

        /// <summary>
        /// Gets the signed area.
        /// </summary>
        /// <returns></returns>
        public float GetSignedArea()
        {
            int i;
            float area = 0;

            for (i = 0; i < size(); i++)
            {
                int j = (i + 1) % size();
                area += this.get(i).x * this.get(j).y;
                area -= this.get(i).y * this.get(j).x;
            }
            area /= 2.0f;
            return area;
        }

        /// <summary>
        /// Gets the area.
        /// </summary>
        /// <returns></returns>
        public float GetArea()
        {
            int i;
            float area = 0;

            for (i = 0; i < size(); i++)
            {
                int j = (i + 1) % size();
                area += this.get(i).x * this.get(j).y;
                area -= this.get(i).y * this.get(j).x;
            }
            area /= 2.0f;
            return (area < 0 ? -area : area);
        }

        /// <summary>
        /// Gets the centroid.
        /// </summary>
        /// <returns></returns>
        public org.jbox2d.common.Vec2 GetCentroid()
        {
            // Same algorithm is used by Box2D

            org.jbox2d.common.Vec2 c = new org.jbox2d.common.Vec2(0, 0);
            float area = 0.0f;

            final float inv3 = 1.0f / 3.0f;
            org.jbox2d.common.Vec2 pRef = new org.jbox2d.common.Vec2(0, 0);
            for (int i = 0; i < size(); ++i)
            {
                // Triangle vertices.
                org.jbox2d.common.Vec2 p1 = pRef;
                org.jbox2d.common.Vec2 p2 = this.get(i);
                org.jbox2d.common.Vec2 p3 = i + 1 < size() ? this.get(i + 1) : this.get(0);

                org.jbox2d.common.Vec2 e1 = p2.sub(p1);
                org.jbox2d.common.Vec2 e2 = p3.sub(p1);

                float D = org.jbox2d.common.Vec2.cross(e1, e2);

                float triangleArea = 0.5f * D;
                area += triangleArea;

                // Area weighted centroid
                c = c.add((p1.add(p2).add(p3)).mul(triangleArea).mul(inv3));
            }

            // Centroid
            c = c.mul(1.0f / area);
            return c;
        }

        /// <summary>
        /// Gets the radius based on area.
        /// </summary>
        /// <returns></returns>
        public float GetRadius()
        {
            float area = GetSignedArea();

            double radiusSqrd = (double)area / Math.PI;
            if (radiusSqrd < 0)
            {
                radiusSqrd *= -1;
            }

            return (float)Math.sqrt(radiusSqrd);
        }

        /// <summary>
        /// Returns an AABB for vertex.
        /// </summary>
        /// <returns></returns>
        public org.jbox2d.collision.AABB GetCollisionBox()
        {
            org.jbox2d.collision.AABB aabb;
            org.jbox2d.common.Vec2 lowerBound = new org.jbox2d.common.Vec2(Float.MAX_VALUE, Float.MAX_VALUE);
            org.jbox2d.common.Vec2 upperBound = new org.jbox2d.common.Vec2(Float.MIN_VALUE, Float.MIN_VALUE);

            for (int i = 0; i < size(); ++i)
            {
                if (this.get(i).x < lowerBound.x)
                {
                    lowerBound.x = this.get(i).x;
                }
                if (this.get(i).x > upperBound.x)
                {
                    upperBound.x = this.get(i).x;
                }

                if (this.get(i).y < lowerBound.y)
                {
                    lowerBound.y = this.get(i).y;
                }
                if (this.get(i).y > upperBound.y)
                {
                    upperBound.y = this.get(i).y;
                }
            }

            aabb = new org.jbox2d.collision.AABB(lowerBound, upperBound);
            return aabb;
        }

        /// <summary>
        /// Translates the vertices with the specified vector.
        /// </summary>
        /// <param name="vector">The vector.</param>
        public void Translate( org.jbox2d.common.Vec2 vector)
        {
            for (int i = 0; i < size(); i++)
                this.set(i, this.get(i).add(vector));
        }

        /// <summary>
        /// Scales the vertices with the specified vector.
        /// </summary>
        /// <param name="value">The Value.</param>
        public void Scale( org.jbox2d.common.Vec2 value)
        {
            for (int i = 0; i < size(); i++){
            	org.jbox2d.common.Vec2 temp = this.get(i);
                this.set(i, (new org.jbox2d.common.Vec2(value.x*temp.x, value.y*temp.y)));
            }
        }

        /// <summary>
        /// Rotate the vertices with the defined value in radians.
        /// </summary>
        /// <param name="value">The amount to rotate by in radians.</param>
        public void Rotate(float value)
        {
            AffineTransform rotationMatrix = AffineTransform.getRotateInstance(value);

            
            for (int i = 0; i < size(); i++){
                Point2D temp = new Point2D.Float(this.get(i).x, this.get(i).y);
                Point2D dest = new Point2D.Float();
                rotationMatrix.deltaTransform(temp, dest);
                this.get(i).x = (float)dest.getX();
                this.get(i).y = (float)dest.getY();
            }
        }
        
        public void Reverse(){
        	if(!this.isEmpty()){
        		Vertices temp = (Vertices)clone();
        		for(int i = 0; i < size(); i++){
        			this.set(size() - i - 1, temp.get(i));
        		}
        	}
        }

        /// <summary>
        /// Assuming the polygon is simple; determines whether the polygon is convex.
        /// NOTE: It will also return false if the input contains colinear edges.
        /// </summary>
        /// <returns>
        /// 	<c>true</c> if it is convex; otherwise, <c>false</c>.
        /// </returns>
        public boolean IsConvex()
        {
            // Ensure the polygon is convex and the interior
            // is to the left of each edge.
            for (int i = 0; i < size(); ++i)
            {
                int i1 = i;
                int i2 = i + 1 < size() ? i + 1 : 0;
                org.jbox2d.common.Vec2 edge = this.get(i2).sub(this.get(i1));

                for (int j = 0; j < size(); ++j)
                {
                    // Don't check vertices on the current edge.
                    if (j == i1 || j == i2)
                    {
                        continue;
                    }

                    org.jbox2d.common.Vec2 r = this.get(j).sub(this.get(i1));

                    float s = edge.x * r.y - edge.y * r.x;

                    if (s <= 0.0f)
                        return false;
                }
            }
            return true;
        }

        public boolean IsCounterClockWise()
        {
            //We just return true for lines
            if (size() < 3)
                return true;

            return (GetSignedArea() > 0.0f);
        }

        /// <summary>
        /// Forces counter clock wise order.
        /// </summary>
        public void ForceCounterClockWise()
        {
            if (!IsCounterClockWise())
            {
            	this.Reverse();
            }
        }

        /// <summary>
        /// Check for edge crossings
        /// </summary>
        /// <returns></returns>
        public boolean IsSimple()
        {
            for (int i = 0; i < size(); ++i)
            {
                int iplus = (i + 1 > size() - 1) ? 0 : i + 1;
                org.jbox2d.common.Vec2 a1 = new org.jbox2d.common.Vec2(this.get(i).x, this.get(i).y);
                org.jbox2d.common.Vec2 a2 = new org.jbox2d.common.Vec2(this.get(iplus).x, this.get(iplus).y);
                for (int j = i + 1; j < size(); ++j)
                {
                    int jplus = (j + 1 > size() - 1) ? 0 : j + 1;
                    org.jbox2d.common.Vec2 b1 = new org.jbox2d.common.Vec2(this.get(j).x, this.get(j).y);
                    org.jbox2d.common.Vec2 b2 = new org.jbox2d.common.Vec2(this.get(jplus).x, this.get(jplus).y);

                    if (Line2D.linesIntersect(a1.x, a1.y, a2.x, a2.y, b1.x, b1.y, b2.x, b2.y))
                    {
                        return false;
                    }
                }
            }
            return true;
        }

        //TODO: Test
        //Implementation found here: http://www.gamedev.net/community/forums/topic.asp?topic_id=548477
        public boolean IsSimple2()
        {
            for (int i = 0; i < size(); ++i)
            {
                if (i < size() - 1)
                {
                    for (int h = i + 1; h < size(); ++h)
                    {
                        // Do two vertices lie on top of one another?
                        if (this.get(i) == this.get(h))
                        {
                            return true;
                        }
                    }
                }

                int j = (i + 1) % size();
                org.jbox2d.common.Vec2 iToj = this.get(j).sub(this.get(i));
                org.jbox2d.common.Vec2 iTojNormal = new org.jbox2d.common.Vec2(iToj.y, -iToj.x);

                // i is the first vertex and j is the second
                int startK = (j + 1) % size();
                int endK = (i - 1 + size()) % size();
                endK += startK < endK ? 0 : startK + 1;
                int k = startK;
                org.jbox2d.common.Vec2 iTok = this.get(k).sub(this.get(i));
                boolean onLeftSide = org.jbox2d.common.Vec2.dot(iTok, iTojNormal) >= 0;
                org.jbox2d.common.Vec2 prevK = this.get(k);
                ++k;
                for (; k <= endK; ++k)
                {
                    int modK = k % size();
                    iTok = this.get(modK).sub(this.get(i));
                    if (onLeftSide != org.jbox2d.common.Vec2.dot(iTok, iTojNormal) >= 0)
                    {
                        org.jbox2d.common.Vec2 prevKtoK = this.get(modK).sub(prevK);
                        org.jbox2d.common.Vec2 prevKtoKNormal = new org.jbox2d.common.Vec2(prevKtoK.y, -prevKtoK.x);
                        if ((org.jbox2d.common.Vec2.dot(this.get(i).sub(prevK), prevKtoKNormal) >= 0) !=
                            (org.jbox2d.common.Vec2.dot(this.get(j).sub(prevK), prevKtoKNormal) >= 0))
                        {
                            return true;
                        }
                    }
                    onLeftSide = org.jbox2d.common.Vec2.dot(iTok, iTojNormal) > 0;
                    prevK = this.get(modK);
                }
            }
            return false;
        }

        // From Eric Jordan's convex decomposition library

        /// <summary>
        /// Checks if polygon is valid for use in Box2d engine.
        /// Last ditch effort to ensure no invalid polygons are
        /// added to world geometry.
        ///
        /// Performs a full check, for simplicity, convexity,
        /// orientation, minimum angle, and volume.  This won't
        /// be very efficient, and a lot of it is redundant when
        /// other tools in this section are used.
        /// </summary>
        /// <returns></returns>
        public boolean CheckPolygon()
        {
            int error = -1;
            if (size() < 3 || size() > MaxPolygonVertices)
            {
                error = 0;
            }
            if (!IsConvex())
            {
                error = 1;
            }
            if (!IsSimple())
            {
                error = 2;
            }
            if (GetArea() < MIN_AREA)
            {
                error = 3;
            }

            //Compute normals
            org.jbox2d.common.Vec2[] normals = new org.jbox2d.common.Vec2[size()];
            Vertices vertices = new Vertices(size());
            for (int i = 0; i < size(); ++i)
            {
                vertices.add(new org.jbox2d.common.Vec2(this.get(i).x, this.get(i).y));
                int i1 = i;
                int i2 = i + 1 < size() ? i + 1 : 0;
                org.jbox2d.common.Vec2 edge = new org.jbox2d.common.Vec2(this.get(i2).x - this.get(i1).x, this.get(i2).y - this.get(i1).y);
                normals[i] = org.jbox2d.common.Vec2.cross(edge, 1.0f);
                normals[i].normalize();
            }

            //Required side checks
            for (int i = 0; i < size(); ++i)
            {
                int iminus = (i == 0) ? size() - 1 : i - 1;

                //Parallel sides check
                float cross = org.jbox2d.common.Vec2.cross(normals[iminus], normals[i]);
                cross = Math.max(Math.min(cross, 1.0f), -1.0f);
                float angle = (float)Math.asin(cross);
                if (angle <= MIN_ANGLE)
                {
                    error = 4;
                    break;
                }

                //Too skinny check
                for (int j = 0; j < size(); ++j)
                {
                    if (j == i || j == (i + 1) % size())
                    {
                        continue;
                    }
                    float s = org.jbox2d.common.Vec2.dot(normals[i], vertices.get(j).sub(vertices.get(i)));
                    if (s >= -MIN_LINEAR_DIST)
                    {
                        error = 5;
                    }
                }


                org.jbox2d.common.Vec2 centroid = vertices.GetCentroid();
                org.jbox2d.common.Vec2 n1 = normals[iminus];
                org.jbox2d.common.Vec2 n2 = normals[i];
                org.jbox2d.common.Vec2 v = vertices.get(i).sub(centroid);

                org.jbox2d.common.Vec2 d = new org.jbox2d.common.Vec2();
                d.x = org.jbox2d.common.Vec2.dot(n1, v); // - toiSlop;
                d.y = org.jbox2d.common.Vec2.dot(n2, v); // - toiSlop;

                // Shifting the edge inward by toiSlop should
                // not cause the plane to pass the centroid.
                if ((d.x < 0.0f) || (d.y < 0.0f))
                {
                    error = 6;
                }
            }

            if (error != -1)
            {
                //Debug.WriteLine("Found invalid polygon, ");
                switch (error)
                {
                    case 0:
                        //Debug.WriteLine(string.Format("must have between 3 and {0} vertices.\n",
                        //                              Settings.MaxPolygonVertices));
                        break;
                    case 1:
                        //Debug.WriteLine("must be convex.\n");
                        break;
                    case 2:
                        //Debug.WriteLine("must be simple (cannot intersect itself).\n");
                        break;
                    case 3:
                        //Debug.WriteLine("area is too small.\n");
                        break;
                    case 4:
                        //Debug.WriteLine("sides are too close to parallel.\n");
                        break;
                    case 5:
                        //Debug.WriteLine("polygon is too thin.\n");
                        break;
                    case 6:
                        //Debug.WriteLine("core shape generation would move edge past centroid (too thin).\n");
                        break;
                    default:
                        //Debug.WriteLine("don't know why.\n");
                        break;
                }
            }
            return error != -1;
        }

        // From Eric Jordan's convex decomposition library

        /// <summary>
        /// Trace the edge of a non-simple polygon and return a simple polygon.
        /// 
        /// Method:
        /// Start at vertex with minimum y (pick maximum x one if there are two).
        /// We aim our "lastDir" vector at (1.0, 0)
        /// We look at the two rays going off from our start vertex, and follow whichever
        /// has the smallest angle (in -Pi . Pi) wrt lastDir ("rightest" turn)
        /// Loop until we hit starting vertex:
        /// We add our current vertex to the list.
        /// We check the seg from current vertex to next vertex for intersections
        /// - if no intersections, follow to next vertex and continue
        /// - if intersections, pick one with minimum distance
        /// - if more than one, pick one with "rightest" next point (two possibilities for each)
        /// </summary>
        /// <param name="verts">The vertices.</param>
        /// <returns></returns>
        public Vertices TraceEdge(Vertices verts)
        {
            PolyNode[] nodes = new PolyNode[verts.size() * verts.size()];
            //overkill, but sufficient (order of mag. is right)
            int nNodes = 0;

            //Add base nodes (raw outline)
            for (int i = 0; i < verts.size(); ++i)
            {
                org.jbox2d.common.Vec2 pos = new org.jbox2d.common.Vec2(verts.get(i).x, verts.get(i).y);
                nodes[i].Position = pos;
                ++nNodes;
                int iplus = (i == verts.size() - 1) ? 0 : i + 1;
                int iminus = (i == 0) ? verts.size() - 1 : i - 1;
                nodes[i].AddConnection(nodes[iplus]);
                nodes[i].AddConnection(nodes[iminus]);
            }

            //Process intersection nodes - horribly inefficient
            boolean dirty = true;
            int counter = 0;
            while (dirty)
            {
                dirty = false;
            	outerLoop:
                for (int i = 0; i < nNodes; ++i)
                {
                    for (int j = 0; j < nodes[i].NConnected; ++j)
                    {
                        for (int k = 0; k < nNodes; ++k)
                        {
                            if (k == i || nodes[k] == nodes[i].Connected[j]) continue;
                            for (int l = 0; l < nodes[k].NConnected; ++l)
                            {
                                if (nodes[k].Connected[l] == nodes[i].Connected[j] ||
                                    nodes[k].Connected[l] == nodes[i]) continue;

                                //Check intersection
                                org.jbox2d.common.Vec2 intersectPt = new org.jbox2d.common.Vec2();

                                boolean crosses = Line2D.linesIntersect(nodes[i].Position.x, nodes[i].Position.y, nodes[i].Connected[j].Position.x,
                                		nodes[i].Connected[j].Position.y, nodes[k].Position.x, nodes[k].Position.y, nodes[k].Connected[l].Position.x,
                                		nodes[k].Connected[l].Position.y);
                                if (crosses)
                                {
                                    dirty = true;
                                    //Destroy and re-hook connections at crossing point
                                    PolyNode connj = nodes[i].Connected[j];
                                    PolyNode connl = nodes[k].Connected[l];
                                    nodes[i].Connected[j].RemoveConnection(nodes[i]);
                                    nodes[i].RemoveConnection(connj);
                                    nodes[k].Connected[l].RemoveConnection(nodes[k]);
                                    nodes[k].RemoveConnection(connl);
                                    nodes[nNodes] = new PolyNode(intersectPt);
                                    nodes[nNodes].AddConnection(nodes[i]);
                                    nodes[i].AddConnection(nodes[nNodes]);
                                    nodes[nNodes].AddConnection(nodes[k]);
                                    nodes[k].AddConnection(nodes[nNodes]);
                                    nodes[nNodes].AddConnection(connj);
                                    connj.AddConnection(nodes[nNodes]);
                                    nodes[nNodes].AddConnection(connl);
                                    connl.AddConnection(nodes[nNodes]);
                                    ++nNodes;
                                    break outerLoop;
                                }
                            }
                        }
                    }
                }
                ++counter;
            }

            //Collapse duplicate points
            boolean foundDupe = true;
            int nActive = nNodes;
            while (foundDupe)
            {
                foundDupe = false;
                for (int i = 0; i < nNodes; ++i)
                {
                    if (nodes[i].NConnected == 0) continue;
                    for (int j = i + 1; j < nNodes; ++j)
                    {
                        if (nodes[j].NConnected == 0) continue;
                        org.jbox2d.common.Vec2 diff = nodes[i].Position.sub(nodes[j].Position);
                        if (diff.lengthSquared() <= Math.pow(MIN_AREA, 2))
                        {
                            if (nActive <= 3)
                                return new Vertices();

                            //printf("Found dupe, %d left\n",nActive);
                            --nActive;
                            foundDupe = true;
                            PolyNode inode = nodes[i];
                            PolyNode jnode = nodes[j];
                            //Move all of j's connections to i, and orphan j
                            int njConn = jnode.NConnected;
                            for (int k = 0; k < njConn; ++k)
                            {
                                PolyNode knode = jnode.Connected[k];
                                assert(knode != jnode);
                                if (knode != inode)
                                {
                                    inode.AddConnection(knode);
                                    knode.AddConnection(inode);
                                }
                                knode.RemoveConnection(jnode);
                            }
                            jnode.NConnected = 0;
                        }
                    }
                }
            }

            //Now walk the edge of the list

            //Find node with minimum y value (max x if equal)
            float minY = Float.MAX_VALUE;
            float maxX = -Float.MAX_VALUE;
            int minYIndex = -1;
            for (int i = 0; i < nNodes; ++i)
            {
                if (nodes[i].Position.y < minY && nodes[i].NConnected > 1)
                {
                    minY = nodes[i].Position.y;
                    minYIndex = i;
                    maxX = nodes[i].Position.x;
                }
                else if (nodes[i].Position.y == minY && nodes[i].Position.x > maxX && nodes[i].NConnected > 1)
                {
                    minYIndex = i;
                    maxX = nodes[i].Position.x;
                }
            }

            org.jbox2d.common.Vec2 origDir = new org.jbox2d.common.Vec2(1.0f, 0.0f);
            org.jbox2d.common.Vec2[] resultVecs = new org.jbox2d.common.Vec2[4 * nNodes];
            // nodes may be visited more than once, unfortunately - change to growable array!
            int nResultVecs = 0;
            PolyNode currentNode = nodes[minYIndex];
            PolyNode startNode = currentNode;
            assert(currentNode.NConnected > 0);
            PolyNode nextNode = currentNode.GetRightestConnection(origDir);
            if (nextNode == null)
            {
                Vertices vertices = new Vertices(nResultVecs);

                for (int i = 0; i < nResultVecs; ++i)
                {
                    vertices.add(resultVecs[i]);
                }

                return vertices;
            }

            // Borked, clean up our mess and return
            resultVecs[0] = startNode.Position;
            ++nResultVecs;
            while (nextNode != startNode)
            {
                if (nResultVecs > 4 * nNodes)
                {
                    assert(false);
                }
                resultVecs[nResultVecs++] = nextNode.Position;
                PolyNode oldNode = currentNode;
                currentNode = nextNode;
                nextNode = currentNode.GetRightestConnection(oldNode);
                if (nextNode == null)
                {
                    Vertices vertices = new Vertices(nResultVecs);
                    for (int i = 0; i < nResultVecs; ++i)
                    {
                        vertices.add(resultVecs[i]);
                    }
                    return vertices;
                }
                // There was a problem, so jump out of the loop and use whatever garbage we've generated so far
            }

            return new Vertices();
        }

        private class PolyNode
        {
            private final int MaxConnected = 32;

            /*
             * Given sines and cosines, tells if A's angle is less than B's on -Pi, Pi
             * (in other words, is A "righter" than B)
             */
            public PolyNode[] Connected = new PolyNode[MaxConnected];
            public int NConnected;
            public org.jbox2d.common.Vec2 Position;

            public PolyNode(org.jbox2d.common.Vec2 pos)
            {
                Position = pos;
                NConnected = 0;
            }

            private boolean IsRighter(float sinA, float cosA, float sinB, float cosB)
            {
                if (sinA < 0)
                {
                    if (sinB > 0 || cosA <= cosB) return true;
                    else return false;
                }
                else
                {
                    if (sinB < 0 || cosA <= cosB) return false;
                    else return true;
                }
            }

            public void AddConnection(PolyNode toMe)
            {
                assert(NConnected < MaxConnected);

                // Ignore duplicate additions
                for (int i = 0; i < NConnected; ++i)
                {
                    if (Connected[i] == toMe) return;
                }
                Connected[NConnected] = toMe;
                ++NConnected;
            }

            public void RemoveConnection(PolyNode fromMe)
            {
                boolean isFound = false;
                int foundIndex = -1;
                for (int i = 0; i < NConnected; ++i)
                {
                    if (fromMe == Connected[i])
                    {
                        //.position == connected[i].position){
                        isFound = true;
                        foundIndex = i;
                        break;
                    }
                }
                assert(isFound);
                --NConnected;
                for (int i = foundIndex; i < NConnected; ++i)
                {
                    Connected[i] = Connected[i + 1];
                }
            }

            public PolyNode GetRightestConnection(PolyNode incoming)
            {
                if (NConnected == 0) assert(false); // This means the connection graph is inconsistent
                if (NConnected == 1)
                {
                    //b2Assert(false);
                    // Because of the possibility of collapsing nearby points,
                    // we may end up with "spider legs" dangling off of a region.
                    // The correct behavior here is to turn around.
                    return incoming;
                }
                org.jbox2d.common.Vec2 inDir = Position.sub(incoming.Position);

                float inLength = inDir.length();
                inDir.normalize();

                assert(inLength > MIN_AREA);

                PolyNode result = null;
                for (int i = 0; i < NConnected; ++i)
                {
                    if (Connected[i] == incoming) continue;
                    org.jbox2d.common.Vec2 testDir = Connected[i].Position.sub(Position);
                    float testLengthSqr = testDir.lengthSquared();
                    testDir.normalize();
                    assert(testLengthSqr >= MIN_AREA * MIN_AREA);
                    float myCos = org.jbox2d.common.Vec2.dot(inDir, testDir);
                    float mySin = org.jbox2d.common.Vec2.cross(inDir, testDir);
                    if (result != null)
                    {
                        org.jbox2d.common.Vec2 resultDir = result.Position.sub(Position);
                        resultDir.normalize();
                        float resCos = org.jbox2d.common.Vec2.dot(inDir, resultDir);
                        float resSin = org.jbox2d.common.Vec2.cross(inDir, resultDir);
                        if (IsRighter(mySin, myCos, resSin, resCos))
                        {
                            result = Connected[i];
                        }
                    }
                    else
                    {
                        result = Connected[i];
                    }
                }

                assert(result != null);

                return result;
            }

            public PolyNode GetRightestConnection(org.jbox2d.common.Vec2 incomingDir)
            {
                org.jbox2d.common.Vec2 diff = Position.sub(incomingDir);
                PolyNode temp = new PolyNode(diff);
                PolyNode res = GetRightestConnection(temp);
                assert(res != null);
                return res;
            }
        }

        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < size(); i++)
            {
                builder.append(this.get(i).toString());
                if (i < size() - 1)
                {
                    builder.append(" ");
                }
            }
            return builder.toString();
        }

        /// <summary>
        /// Projects to axis.
        /// </summary>
        /// <param name="axis">The axis.</param>
        /// <param name="min">The min.</param>
        /// <param name="max">The max.</param>
        public float[] ProjectToAxis( org.jbox2d.common.Vec2 axis, float min, float max)
        {
            // To project a point on an axis use the dot product
            float dotProduct = org.jbox2d.common.Vec2.dot(axis, this.get(0));
            min = dotProduct;
            max = dotProduct;

            for (int i = 0; i < size(); i++)
            {
                dotProduct = org.jbox2d.common.Vec2.dot(this.get(i), axis);
                if (dotProduct < min)
                {
                    min = dotProduct;
                }
                else
                {
                    if (dotProduct > max)
                    {
                        max = dotProduct;
                    }
                }
            }
            float[] toReturn = {min, max};
            return toReturn;
        }

        /// <summary>
        /// Winding number test for a point in a polygon.
        /// </summary>
        /// See more info about the algorithm here: http://softsurfer.com/Archive/algorithm_0103/algorithm_0103.htm
        /// <param name="point">The point to be tested.</param>
        /// <returns>-1 if the winding number is zero and the point is outside
        /// the polygon, 1 if the point is inside the polygon, and 0 if the point
        /// is on the polygons edge.</returns>
        public int PointInPolygon( org.jbox2d.common.Vec2 point)
        {
            // Winding number
            int wn = 0;

            // Iterate through polygon's edges
            for (int i = 0; i < size(); i++)
            {
                // Get points
                org.jbox2d.common.Vec2 p1 = this.get(i);
                org.jbox2d.common.Vec2 p2 = this.get(NextIndex(i));

                // Test if a point is directly on the edge
                org.jbox2d.common.Vec2 edge = p2.sub(p1);
                float area = p1.x * (p2.y - point.y) + p2.x * (point.y - p1.y) + point.x * (p1.y - p2.y);
                if (area == 0f && org.jbox2d.common.Vec2.dot(point.sub(p1), edge) >= 0f && org.jbox2d.common.Vec2.dot(point.sub(p2), edge) <= 0f)
                {
                    return 0;
                }
                // Test edge for intersection with ray from point
                if (p1.y <= point.y)
                {
                    if (p2.y > point.y && area > 0f)
                    {
                        ++wn;
                    }
                }
                else
                {
                    if (p2.y <= point.y && area < 0f)
                    {
                        --wn;
                    }
                }
            }
            return (wn == 0 ? -1 : 1);
        }

        /// <summary>
        /// Compute the sum of the angles made between the test point and each pair of points making up the polygon. 
        /// If this sum is 2pi then the point is an interior point, if 0 then the point is an exterior point. 
        /// : http://ozviz.wasp.uwa.edu.au/~pbourke/geometry/insidepoly/  - Solution 2 
        /// </summary>
        public boolean PointInPolygonAngle( org.jbox2d.common.Vec2 point)
        {
            double angle = 0;

            // Iterate through polygon's edges
            for (int i = 0; i < size(); i++)
            {
                // Get points
                org.jbox2d.common.Vec2 p1 = this.get(i).sub(point);
                org.jbox2d.common.Vec2 p2 = this.get(NextIndex(i)).sub(point);

                angle += VectorAngle( p1,  p2);
            }

            if (Math.abs(angle) < Math.PI)
            {
                return false;
            }

            return true;
        }

        public static double VectorAngle(org.jbox2d.common.Vec2 p1, org.jbox2d.common.Vec2 p2)
        {
            double theta1 = Math.atan2(p1.y, p1.x);
            double theta2 = Math.atan2(p2.y, p2.x);
            double dtheta = theta2 - theta1;
            while (dtheta > Math.PI)
                dtheta -= (2 * Math.PI);
            while (dtheta < -Math.PI)
                dtheta += (2 * Math.PI);

            return (dtheta);
        }
        
        public Vec2[] toArray(){
        	Vec2[] toReturn = new Vec2[size()];
        	for(int i = 0; i < size(); i++){
        		toReturn[i] = get(i);
        	}
        	return toReturn;
        }
    }
