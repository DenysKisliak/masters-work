package com.gdx.cellular.elements.solid.movable;

import com.badlogic.gdx.math.Vector3;
import com.gdx.cellular.CellularMatrix;

public class Ember extends MovableSolid {

    public Ember(int x, int y) {
        super(x, y);
        vel = new Vector3(0f, -124f,0f);
        frictionFactor = .9f;
        inertialResistance = .99f;
        mass = 200;
        isIgnited = true;
        health = getRandomInt(100) + 250;
        temperature = 5;
        flammabilityResistance = 0;
        resetFlammabilityResistance = 20;
    }

    @Override
    public boolean infect(CellularMatrix matrix) {
        return false;
    }
}
