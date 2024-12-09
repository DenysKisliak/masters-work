package com.gdx.cellular.particles;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.gdx.cellular.CellularMatrix;
import com.gdx.cellular.elements.Element;
import com.gdx.cellular.elements.ElementType;
import com.gdx.cellular.elements.EmptyCell;
import com.gdx.cellular.elements.gas.Gas;
import com.gdx.cellular.elements.liquid.Liquid;
import com.gdx.cellular.elements.solid.immoveable.ImmovableSolid;
import com.gdx.cellular.elements.solid.movable.MovableSolid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Explosion {

    private final CellularMatrix matrix;
    public int radius;
    public int strength;
    public Element sourceElement;
    int matrixX;
    int matrixY;

    public Explosion(CellularMatrix matrix, int radius, int strength, Element sourceElement) {
        this.matrix = matrix;
        this.radius = radius;
        this.strength = strength;
        this.sourceElement = sourceElement;
        this.matrixX = sourceElement.getMatrixX();
        this.matrixY = sourceElement.getMatrixY();
    }

    public Explosion(CellularMatrix matrix, int radius, int strength, int matrixX, int matrixY) {
        this.matrix = matrix;
        this.radius = radius;
        this.strength = strength;
        this.matrixX = matrixX;
        this.matrixY = matrixY;
    }

    public List<Explosion> enact() {
        int matrixX;
        int matrixY;
        if (sourceElement != null) {
            matrixX = sourceElement.getMatrixX();
            matrixY = sourceElement.getMatrixY();
        } else {
            matrixX = this.matrixX;
            matrixY = this.matrixY;
        }
        if (sourceElement != null && sourceElement.isDead()) {
            return new ArrayList<>();
        }
        Map<String, String> coordinatesCache = new HashMap<>();
        for (int x = radius; x >= radius * -1; x--) {
            for (int y = radius; y >= radius * -1; y--) {
                if (Math.abs(x) == radius || Math.abs(y) == radius) {

                        iterateBetweenTwoPoints(matrixX, matrixY, matrixX + x, matrixY + y, strength, coordinatesCache, matrix);
                }
            }
        }
        return null;
    }

    private void iterateBetweenTwoPoints(int matrixX, int matrixY, int newX, int newY, int strength, Map<String, String> cache, CellularMatrix matrix) {
        int matrixX1 = matrixX;
        int matrixY1 = matrixY;
        int matrixX2 = newX;
        int matrixY2 = newY;

        int localRadius = radius + getRandomVariation(radius);



        int xDiff = matrixX1 - matrixX2;
        int yDiff = matrixY1 - matrixY2;
        boolean xDiffIsLarger = Math.abs(xDiff) > Math.abs(yDiff);

        int xModifier = xDiff < 0 ? 1 : -1;
        int yModifier = yDiff < 0 ? 1 : -1;

        boolean onlyDarken = false;

        int upperBound = Math.max(Math.abs(xDiff), Math.abs(yDiff));
        int min = Math.min(Math.abs(xDiff), Math.abs(yDiff));
        float slope = (min == 0 || upperBound == 0) ? 0 : ((float) (min + 1) / (upperBound + 1));

        int smallerCount;
        for (int i = 0; i <= upperBound; i++) {
            smallerCount = (int) Math.floor(i * slope);
            int yIncrease, xIncrease;
            if (xDiffIsLarger) {
                xIncrease = i;
                yIncrease = smallerCount;
            } else {
                yIncrease = i;
                xIncrease = smallerCount;
            }
            int currentY = matrixY1 + (yIncrease * yModifier);
            int currentX = matrixX1 + (xIncrease * xModifier);


            String cachedResult = cache.get(String.valueOf(currentX) + currentY);
            if (cachedResult != null && cachedResult.equals(String.valueOf(true))) {
                continue;
            } else if (cachedResult != null && cachedResult.equals(String.valueOf(false))) {
                onlyDarken = true;
                continue;
            }
            if (!matrix.isWithinBounds(currentX, currentY)) {
                cache.put(String.valueOf(currentX) + currentY, String.valueOf(false));
                break;
            }
            int distance = matrix.distanceBetweenTwoPoints(matrixX1, currentX, matrixY1, currentY);
            if (distance < localRadius/2) {
                if (onlyDarken) {
                    Element element = matrix.get(currentX, currentY);
                    darkenElement(element, ((float) distance)/localRadius);
                    cache.put(String.valueOf(currentX) + currentY, String.valueOf(false));
                    if (Math.random() > .8) {
                        break;
                    }
                    continue;
                }
                Element element = matrix.get(currentX, currentY);
                if (element instanceof EmptyCell) {
                    if (Math.random() > 0.5) {
                        matrix.setElementAtIndex(currentX, currentY, ElementType.EXPLOSIONSPARK.createElementByMatrix(currentX, currentY));
                    }
                    cache.put(String.valueOf(currentX) + currentY, String.valueOf(true));
                } else {
                    boolean unstopped = element.explode(matrix, strength);
                    cache.put(String.valueOf(currentX) + currentY, String.valueOf(unstopped));
                    if (!unstopped) {
                        element.receiveHeat(matrix, 300);
                        darkenElement(element, ((float) distance)/localRadius);
                        onlyDarken = true;
                        continue;
                    }
                }
            } else if (distance < (localRadius/2 + Math.max(localRadius/4, 1))) {
                if (onlyDarken) {
                    Element element = matrix.get(currentX, currentY);
                    element.darkenColor(((float) distance)/localRadius);
                    cache.put(String.valueOf(currentX) + currentY, String.valueOf(false));
                    if (Math.random() > .6) {
                        break;
                    }
                    continue;
                }
                Element element = matrix.get(currentX, currentY);
                if (element instanceof EmptyCell) {
                    if (Math.random() > 0.5) {
                        matrix.setElementAtIndex(currentX, currentY, ElementType.EXPLOSIONSPARK.createElementByMatrix(currentX, currentY));
                    }
                    cache.put(String.valueOf(currentX) + currentY, String.valueOf(true));
                    continue;
                }
                darkenElement(element, ((float) distance)/(localRadius)*1.5f);
                element.receiveHeat(matrix, 300);
                Vector2 center = new Vector2(matrixX, matrixY);
                Vector2 newPoint = new Vector2(currentX, currentY);
                newPoint.sub(center).nor();
                matrix.particalizeByMatrix(currentX, currentY, new Vector3(newPoint.x * radius * 5, newPoint.y  * radius * 5, 0));
                if (Math.random() > .8) {
                    break;
                }
            }
        }
    }

    private int getRandomVariation(int radius) {
        if (Math.random() > 0.5f) {
            return 1;
        } else {
            return -1;
        }





    }



    private void darkenElement(Element element, float factor) {
        if (element instanceof MovableSolid) {
            ((MovableSolid) element).darkenColor(factor);
        } else if (element instanceof Liquid) {
            ((Liquid) element).darkenColor(factor);
        } else if (element instanceof ImmovableSolid) {
            ((ImmovableSolid) element).darkenColor(factor);
        } else if (element instanceof Gas) {
            ((Gas) element).darkenColor(factor);
        }
    }

}


