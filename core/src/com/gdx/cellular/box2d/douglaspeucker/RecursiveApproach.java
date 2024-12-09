package com.gdx.cellular.box2d.douglaspeucker;

import com.badlogic.gdx.math.Vector2;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecursiveApproach {

    private Vector2 sum(Vector2 v1, Vector2 v2) {
        return new Vector2(v1.x + v2.x, v1.y + v2.y);
    }

    private Vector2 diff(Vector2 v1, Vector2 v2) {
        return new Vector2(v1.x - v2.x, v1.y - v2.y);
    }

    private Vector2 prod(Vector2 v1, Vector2 v2) {
        return new Vector2(v1.x * v2.x, v1.y * v2.y);
    }

    private float dot(Vector2 v1, Vector2 v2) {
        return (v1.x * v2.x) + (v1.y * v2.y);
    }

    private float norm2(Vector2 v) {
        return (v.x * v.x) + (v.y * v.y);
    }

    private float norm(Vector2 v) {
        return (float) Math.sqrt(norm2(v));
    }

    private float d2(Vector2 v1, Vector2 v2) {
        return norm2(diff(v1,v2));
    }

    private float d(Vector2 v1, Vector2 v2) {
        return norm(diff(v1,v2));
    }

    public void simplifyDP(float tolerance, Vector2[] vertices, int startIndex, int endIndex, int[] markerBuffer) {
        if (startIndex <= endIndex + 1) {
            return;
        }

        int maxi = startIndex;
        int maxd2 = 0;
        float tol2 = tolerance * tolerance;
        Vector2[] segment = new Vector2[] {vertices[startIndex], vertices[endIndex]};
        Vector2 direction = diff(segment[1], segment[0]);
        float squaredSegmentLength = norm2(direction);



        Vector2 w;
        Vector2 Pb;
        float b, cw, dv2;
        for (int i=startIndex + 1; i < endIndex; i++) {

            w = diff(vertices[i], segment[0]);
            cw = dot(w, direction);
            if ( cw <= 0 ) {
                dv2 = d2(vertices[i], segment[0]);
            } else if ( squaredSegmentLength <= cw ) {
                dv2 = d2(vertices[i], segment[1]);
            } else {
                b = cw / squaredSegmentLength;
                Pb = new Vector2(segment[0].x+b*direction.x, segment[0].y+b*direction.y);
                dv2 = d2(vertices[i], Pb);
            }

            if (dv2 <= maxd2) {
                continue;
            }

            maxi = i;
            maxd2 = (int) dv2;
        }
        if (maxd2 > tol2) {

            markerBuffer[maxi] = 1;

            simplifyDP( tolerance, vertices, startIndex, maxi, markerBuffer );
            simplifyDP( tolerance, vertices, maxi, endIndex, markerBuffer );
        }

    }

    public List<Vector2> simplify(List<Vector2> vertices, float tolerance) {
        int n = vertices.size();
        Vector2[] savedVerts = new Vector2[n];
        int i, k, m, pv;
        float tol2 = tolerance * tolerance;
        Vector2[] vertexBuffer = new Vector2[n];
        int[] markerBuffer = new int[vertices.size()];


        vertexBuffer[0] = vertices.get(0);
        for (i=k=1, pv=0; i<n; i++) {
            if (d2(vertices.get(i), vertices.get(pv)) < tol2) {
                continue;
            }
            vertexBuffer[k++] = vertices.get(i);
            pv = i;
        }
        if (pv < n-1) {
            vertexBuffer[k++] = vertices.get(n - 1);
        }


        markerBuffer[0] = markerBuffer[k-1] = 1;
        simplifyDP(tolerance, vertexBuffer, 0, k-1, markerBuffer);


        for (i = m = 0; i < k; i++) {
            if (markerBuffer[i] != 0) {
                savedVerts[m++] = vertexBuffer[i];
            }
        }
        return Arrays.asList(savedVerts);
    }










































































































}
