package edu.cornell.slicktest;

public class MSTriangle
{
    public float[] X;
    public float[] Y;

    //Constructor automatically fixes orientation to ccw
    public MSTriangle(float x1, float y1, float x2, float y2, float x3, float y3)
    {
        X = new float[3];
        Y = new float[3];
        float dx1 = x2 - x1;
        float dx2 = x3 - x1;
        float dy1 = y2 - y1;
        float dy2 = y3 - y1;
        float cross = dx1 * dy2 - dx2 * dy1;
        boolean ccw = (cross > 0);
        if (ccw)
        {
            X[0] = x1;
            X[1] = x2;
            X[2] = x3;
            Y[0] = y1;
            Y[1] = y2;
            Y[2] = y3;
        }
        else
        {
            X[0] = x1;
            X[1] = x3;
            X[2] = x2;
            Y[0] = y1;
            Y[1] = y3;
            Y[2] = y2;
        }
    }

    public MSTriangle(MSTriangle t)
    {
        X = new float[3];
        Y = new float[3];

        X[0] = t.X[0];
        X[1] = t.X[1];
        X[2] = t.X[2];
        Y[0] = t.Y[0];
        Y[1] = t.Y[1];
        Y[2] = t.Y[2];
    }

    public boolean IsInside(float x, float y)
    {
        if (x < X[0] && x < X[1] && x < X[2]) return false;
        if (x > X[0] && x > X[1] && x > X[2]) return false;
        if (y < Y[0] && y < Y[1] && y < Y[2]) return false;
        if (y > Y[0] && y > Y[1] && y > Y[2]) return false;

        float vx2 = x - X[0];
        float vy2 = y - Y[0];
        float vx1 = X[1] - X[0];
        float vy1 = Y[1] - Y[0];
        float vx0 = X[2] - X[0];
        float vy0 = Y[2] - Y[0];

        float dot00 = vx0 * vx0 + vy0 * vy0;
        float dot01 = vx0 * vx1 + vy0 * vy1;
        float dot02 = vx0 * vx2 + vy0 * vy2;
        float dot11 = vx1 * vx1 + vy1 * vy1;
        float dot12 = vx1 * vx2 + vy1 * vy2;
        float invDenom = 1.0f / (dot00 * dot11 - dot01 * dot01);
        float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
        float v = (dot00 * dot12 - dot01 * dot02) * invDenom;

        return ((u > 0) && (v > 0) && (u + v < 1));
    }
}
