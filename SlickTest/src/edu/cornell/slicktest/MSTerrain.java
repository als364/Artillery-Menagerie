package edu.cornell.slicktest;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.*;

import org.jbox2d.*;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

    /// <summary>
    /// Simple class to maintain a terrain.
    /// </summary>
public class MSTerrain
{
        /// <summary>
        /// World to manage terrain in.
        /// </summary>
        public org.jbox2d.dynamics.World World;

        /// <summary>
        /// Center of terrain in world units.
        /// </summary>
        public org.jbox2d.common.Vec2 Center;

        /// <summary>
        /// Width of terrain in world units.
        /// </summary>
        public float Width;

        /// <summary>
        /// Height of terrain in world units.
        /// </summary>
        public float Height;

        /// <summary>
        /// Points per each world unit used to define the terrain in the point cloud.
        /// </summary>
        public int PointsPerUnit;

        /// <summary>
        /// Points per cell.
        /// </summary>
        public int CellSize;

        /// <summary>
        /// Points per sub cell.
        /// </summary>
        public int SubCellSize;

        /// <summary>
        /// Number of iterations to perform in the Marching Squares algorithm.
        /// Note: More then 3 has almost no effect on quality.
        /// </summary>
        public int Iterations = 2;

        /// <summary>
        /// Decomposer to use when regenerating terrain. Can be changed on the fly without consequence.
        /// Note: Some decomposerers are unstable.
        /// </summary>
        public Decomposer decomposer;

        /// <summary>
        /// Point cloud defining the terrain.
        /// </summary>
        private byte[][] _terrainMap;

        /// <summary>
        /// Generated bodies.
        /// </summary>
        private List<org.jbox2d.dynamics.Body>[][] _bodyMap;

        private float _localWidth;
        private float _localHeight;
        private int _xnum;
        private int _ynum;
        private org.jbox2d.collision.AABB _dirtyArea;
        private org.jbox2d.common.Vec2 _topLeft;

        public MSTerrain(org.jbox2d.dynamics.World world, org.jbox2d.collision.AABB area)
        {
            World = world;
            Width = area.getExtents().x * 2;
            Height = area.getExtents().y * 2;
            Center = area.getCenter();
        }

        /// <summary>
        /// Initialize the terrain for use.
        /// </summary>
        public void Initialize()
        {
            // find top left of terrain in world space
            _topLeft = new org.jbox2d.common.Vec2(Center.x - (Width * 0.5f), Center.y - (-Height * 0.5f));

            // convert the terrains size to a point cloud size
            _localWidth = Width * PointsPerUnit;
            _localHeight = Height * PointsPerUnit;

            _terrainMap = new byte[(int)_localWidth + 1][ (int)_localHeight + 1];

            for (int x = 0; x < _localWidth; x++)
            {
                for (int y = 0; y < _localHeight; y++)
                {
                    _terrainMap[x][ y] = 1;
                }
            }

            _xnum = (int)(_localWidth / CellSize);
            _ynum = (int)(_localHeight / CellSize);
            _bodyMap = new ArrayList[_xnum][ _ynum];
            for(int i = 0; i < _xnum; i++){
             for(int j = 0; j < _ynum; j++){
              _bodyMap[i][j] = new ArrayList<org.jbox2d.dynamics.Body>();
             }
            }

            // make sure to mark the dirty area to an infinitely small box
            _dirtyArea = new org.jbox2d.collision.AABB(new org.jbox2d.common.Vec2(Float.MAX_VALUE, Float.MAX_VALUE), new org.jbox2d.common.Vec2(Float.MIN_VALUE, Float.MIN_VALUE));
        }
        
        /// <summary>
        /// Return true if the specified color is inside the terrain.
        /// </summary>
        public boolean TerrainTester(Color color, int alpha){
         return color.a > alpha;
        }

        /// <summary>
        /// Apply a texture to the terrain using the specified TerrainTester.
        /// </summary>
        /// <param name="texture">Texture to apply.</param>
        /// <param name="position">Top left position of the texture relative to the terrain.</param>
        /// <param name="tester">Delegate method used to determine what colors should be included in the terrain.</param>
        public void ApplyTexture(Image texture, org.jbox2d.common.Vec2 position, int alpha)
        {
            Color[] colorData = new Color[texture.getWidth() * texture.getHeight()];

            for(int i = 0; i < texture.getWidth(); i++){
             for(int j = 0; j < texture.getHeight(); j++){
              colorData[j*texture.getWidth() + i] = texture.getColor(i,  j);
             }
            }

            for (int y = (int)position.y; y < texture.getHeight() + (int)position.y; y++)
            {
                for (int x = (int)position.x; x < texture.getWidth() + (int)position.x; x++)
                {
                    if (x >= 0 && x < _localWidth && y >= 0 && y < _localHeight)
                    {
                        boolean inside = TerrainTester(colorData[((y - (int)position.y) * texture.getWidth()) + (x - (int)position.x)], alpha);

                        if (!inside)
                            _terrainMap[x][ y] = 1;
                        else
                            _terrainMap[x][ y] = -1;
                    }
                }
            }

            // generate terrain
            for (int gy = 0; gy < _ynum; gy++)
            {
                for (int gx = 0; gx < _xnum; gx++)
                {
                    //remove old terrain object at grid cell
                    if (_bodyMap[gx][ gy] != null)
                    {
                        for (int i = 0; i < _bodyMap[gx][ gy].size(); i++)
                        {
                            World.destroyBody(_bodyMap[gx][ gy].get(i));
                        }
                    }

                    _bodyMap[gx][ gy] = null;

                    //generate new one
                    GenerateTerrain(gx, gy);
                }
            }
        }

        /// <summary>
        /// Apply a texture to the terrain using the specified TerrainTester.
        /// </summary>
        /// <param name="position">Top left position of the texture relative to the terrain.</param>
        public void ApplyData(byte[][] data, org.jbox2d.common.Vec2 position)
        {
            for (int y = (int)position.y; y < data[0].length + (int)position.y; y++)
            {
                for (int x = (int)position.x; x < data.length + (int)position.x; x++)
                {
                    if (x >= 0 && x < _localWidth && y >= 0 && y < _localHeight)
                    {
                        _terrainMap[x][ y] = data[x][ y];
                    }
                }
            }

            // generate terrain
            for (int gy = 0; gy < _ynum; gy++)
            {
                for (int gx = 0; gx < _xnum; gx++)
                {
                    //remove old terrain object at grid cell
                    if (_bodyMap[gx][ gy] != null)
                    {
                        for (int i = 0; i < _bodyMap[gx][ gy].size(); i++)
                        {
                            World.destroyBody(_bodyMap[gx][ gy].get(i));
                        }
                    }

                    _bodyMap[gx][ gy] = null;

                    //generate new one
                    GenerateTerrain(gx, gy);
                }
            }
        }

        /// <summary>
        /// Convert a texture to an btye array compatible with ApplyData().
        /// </summary>
        /// <param name="texture">Texture to convert.</param>
        /// <param name="tester"></param>
        /// <returns></returns>
        public byte[][] ConvertTextureToData(SpriteSheet texture, int alpha)
        {
            byte[][] data = new byte[texture.getWidth()][ texture.getHeight()];
            Color[] colorData = new Color[texture.getWidth() * texture.getHeight()];

            for(int i = 0; i < texture.getWidth(); i++){
             for(int j = 0; j < texture.getHeight(); j++){
              colorData[j*texture.getWidth() + i] = texture.getColor(i,  j);
             }
            }

            for (int y = 0; y < texture.getHeight(); y++)
            {
                for (int x = 0; x < texture.getWidth(); x++)
                {
                    boolean inside = TerrainTester(colorData[(y * texture.getWidth()) + x], alpha);

                    if (!inside)
                        data[x][ y] = 1;
                    else
                        data[x][ y] = -1;
                }
            }

            return data;
        }

        /// <summary>
        /// Modify a single point in the terrain.
        /// </summary>
        /// <param name="location">World location to modify. Automatically clipped.</param>
        /// <param name="value">-1 = inside terrain, 1 = outside terrain</param>
        public void ModifyTerrain(org.jbox2d.common.Vec2 location, byte value)
        {
            // find local position
            // make position local to map space
            //org.jbox2d.common.Vec2 p = location.sub(_topLeft);
         Vec2 p = location;

            // find map position for each axis
            //p.x = p.x * _localWidth / Width;
            //p.y = p.y * -_localHeight / Height;

            if (p.x >= 0 && p.x < _localWidth && p.y >= 0 && p.y < _localHeight)
            {
                _terrainMap[(int)p.x][ (int)p.y] = value;

                // expand dirty area
                if (p.x < _dirtyArea.lowerBound.x) _dirtyArea.lowerBound.x = p.x;
                if (p.x > _dirtyArea.upperBound.x) _dirtyArea.upperBound.x = p.x;

                if (p.y < _dirtyArea.lowerBound.y) _dirtyArea.lowerBound.y = p.y;
                if (p.y > _dirtyArea.upperBound.y) _dirtyArea.upperBound.y = p.y;
            }
        }

        /// <summary>
        /// Regenerate the terrain.
        /// </summary>
        public void RegenerateTerrain()
        {
            //iterate effected cells
            int gx0 = (int)(_dirtyArea.lowerBound.x / CellSize);
            int gx1 = (int)(_dirtyArea.upperBound.x / CellSize) + 1;
            if (gx0 < 0) gx0 = 0;
            if (gx1 > _xnum) gx1 = _xnum;
            int gy0 = (int)(_dirtyArea.lowerBound.y / CellSize);
            int gy1 = (int)(_dirtyArea.upperBound.y / CellSize) + 1;
            if (gy0 < 0) gy0 = 0;
            if (gy1 > _ynum) gy1 = _ynum;

            for (int gx = gx0; gx < gx1; gx++)
            {
                for (int gy = gy0; gy < gy1; gy++)
                {
                    //remove old terrain object at grid cell
                    if (_bodyMap[gx][ gy] != null)
                    {
                        for (int i = 0; i < _bodyMap[gx][ gy].size(); i++)
                        {
                            World.destroyBody(_bodyMap[gx][ gy].get(i));
                        }
                    }

                    _bodyMap[gx][ gy] = null;

                    //generate new one
                    GenerateTerrain(gx, gy);
                }
            }

            _dirtyArea = new org.jbox2d.collision.AABB(new org.jbox2d.common.Vec2(Float.MAX_VALUE, Float.MAX_VALUE), new org.jbox2d.common.Vec2(Float.MIN_VALUE, Float.MIN_VALUE));
        }
        
        public static boolean Collinear(Vec2 a, Vec2 b, Vec2 c, float tolerance){
         float temp = a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y);
         return (temp >= - tolerance && temp <= tolerance);
        }
        
        public static Vertices CollinearSimplify(Vertices vertices, float collinearityTolerance)
        {
            //We can't simplify polygons under 3 vertices
            if (vertices.size() < 3)
                return vertices;

            Vertices simplified = new Vertices();

            for (int i = 0; i < vertices.size(); i++)
            {
                int prevId = vertices.PreviousIndex(i);
                int nextId = vertices.NextIndex(i);

                org.jbox2d.common.Vec2 prev = vertices.get(prevId);
                org.jbox2d.common.Vec2 current = vertices.get(i);
                org.jbox2d.common.Vec2 next = vertices.get(nextId);

                //If they are collinear, continue
                if (Collinear(prev, current, next, collinearityTolerance))
                    continue;
                

                simplified.add(current);
            }

            return simplified;
        }

        private void GenerateTerrain(int gx, int gy)
        {
            float ax = gx * CellSize;
            float ay = gy * CellSize;

            if(ay > 400){
             @SuppressWarnings("unused")
    int useless = 0;
             useless++;
            }
            List<Vertices> polys = MarchingSquares.DetectSquares(new org.jbox2d.collision.AABB(new org.jbox2d.common.Vec2(ax, ay), new org.jbox2d.common.Vec2(ax + CellSize, ay + CellSize)), SubCellSize, SubCellSize, _terrainMap, Iterations, true);
            if (polys.size() == 0) return;

            _bodyMap[gx][ gy] = new ArrayList<org.jbox2d.dynamics.Body>();

            // create the scale vector
            org.jbox2d.common.Vec2 scale = new org.jbox2d.common.Vec2(1f / PointsPerUnit, 1f / -PointsPerUnit);

            // create physics object for this grid cell
            for (Vertices item : polys)
            {
                // does this need to be negative?
                // item.Scale(scale);
                // item.Translate(_topLeft);
                item.ForceCounterClockWise();
                Vertices p = CollinearSimplify(item, 0);
                List<Vertices> decompPolys = new ArrayList<Vertices>();
                Vertices[] p_store = { p };

                switch (decomposer)
                {
                    case Bayazit:
                        //decompPolys = Decomposition.BayazitDecomposer.ConvexPartition(p);
                        break;
                    case CDT:
                        //decompPolys = Decomposition.CDTDecomposer.ConvexPartition(p);
                        break;
                    case Earclip:
                        decompPolys = EarclipDecomposer.ConvexPartition(p_store);
                        break;
                    case Flipcode:
                        //decompPolys = Decomposition.FlipcodeDecomposer.ConvexPartition(p);
                        break;
                    case Seidel:
                        //decompPolys = Decomposition.SeidelDecomposer.ConvexPartition(p, 0.001f);
                        break;
                    default:
                        break;
                }

                for (Vertices poly : decompPolys)
                {
                    if (poly.size() > 2){
                     Filter groundFilter = new Filter();
                  groundFilter.categoryBits = 0x0002;
                  groundFilter.maskBits = 0x0004;
                     PolygonShape shape = new PolygonShape();
                     Vec2[] arr = poly.toArray();
                     Vec2 pos = new Vec2(ax, ay);
                     float x_avg = 0; float y_avg = 0;
                     for(int i = 0; i < arr.length; i++){
                      x_avg += arr[i].x;
                      y_avg += arr[i].y;
                     }
                     x_avg /= arr.length;  y_avg /= arr.length;
                     Vec2 diff = new Vec2(x_avg, y_avg);
                     for(int i = 0; i < arr.length; i++){
                      arr[i] = arr[i].sub(diff);
                     }
                     shape.set(arr, arr.length);
                     BodyDef bodyDef = new BodyDef();
                     bodyDef.position = pos;
                     bodyDef.type = BodyType.STATIC;
                     Body body = World.createBody(bodyDef);
                     body.createFixture(shape, 1);
                     body.m_fixtureList.m_friction = 1f;
                     body.m_fixtureList.setFilterData(groundFilter);
                     BattleScreen.terrain_pos.add(pos);
                     BattleScreen.terrain_vecs.add(arr);
                        _bodyMap[gx][ gy].add(body);
                    }
                }
            }
        }
    }
    

