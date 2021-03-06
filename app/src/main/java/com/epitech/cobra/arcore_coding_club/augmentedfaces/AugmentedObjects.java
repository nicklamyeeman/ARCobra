package com.epitech.cobra.arcore_coding_club.augmentedfaces;

import android.content.Context;

import com.epitech.cobra.arcore_coding_club.common.rendering.ObjectRenderer;
import com.google.ar.core.AugmentedFace;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class AugmentedObjects {

    protected enum FACE_AREA {
        FOREHEAD_LEFT,
        FOREHEAD_RIGHT,
        CENTER
    }
    private static class SingleObj {
        protected ObjectRenderer renderer;
        protected float[] matrix;
        protected FACE_AREA area;

        public SingleObj(ObjectRenderer renderer, float[] matrix, FACE_AREA area) {
            this.renderer = renderer;
            this.matrix = matrix;
            this.area = area;
        }

        public ObjectRenderer getRenderer() { return this.renderer; }
        public float[] getMatrix() { return this.matrix; }
        public FACE_AREA getArea() { return this.area; }
        public void setRenderer(ObjectRenderer renderer) { this.renderer = renderer; }
        public void setMatrix(float[] matrix) { this.matrix = matrix; }
        public void setArea(FACE_AREA area) { this.area = area; }
    };

    HashMap<String, SingleObj> augmentedObjects = new HashMap<String, SingleObj>();

    public void addObject(Context context, String name, String objName, String materialName, FACE_AREA area) throws IOException {
        ObjectRenderer renderer = new ObjectRenderer();
        renderer.createOnGlThread(context, objName, materialName);
        renderer.setMaterialProperties(0.0f, 1.0f, 0.1f, 6.0f);
        renderer.setBlendMode(ObjectRenderer.BlendMode.AlphaBlending);
        this.augmentedObjects.put(name, new SingleObj(renderer, new float[16], area));
    }

    public void updateObjectsMatrix(AugmentedFace face) {
        augmentedObjects.forEach((keys, value) -> {
            if (value.getArea() == FACE_AREA.CENTER)
                face.getRegionPose(AugmentedFace.RegionType.NOSE_TIP).toMatrix(value.getMatrix(), 0);
            if (value.getArea() == FACE_AREA.FOREHEAD_LEFT)
                face.getRegionPose(AugmentedFace.RegionType.FOREHEAD_LEFT).toMatrix(value.getMatrix(), 0);
            if (value.getArea() == FACE_AREA.FOREHEAD_RIGHT)
                face.getRegionPose(AugmentedFace.RegionType.FOREHEAD_RIGHT).toMatrix(value.getMatrix(), 0);
        });
    }

    public void updateObject(String name, float[] scaleFactor, float[] rotateFactor, float[] translateFactor) {
        if (scaleFactor == null)
            scaleFactor = new float[]{1.0f, 1.0f, 1.0f};
        if (rotateFactor == null)
            rotateFactor = new float[]{0.0f, 1.0f, 1.0f, 1.0f};
        if (translateFactor == null)
            translateFactor = new float[]{0.0f, 0.0f, 0.0f};
        scaleRotateTranslateObject(name, scaleFactor, rotateFactor, translateFactor);
    }

    public void scaleObject(String name, float[] scaleFactor) { getObjectRenderer(name).scaleModelMatrix(getObjectMatrix(name), scaleFactor); }
    public void rotateObject(String name, float[] rotateFactor) { getObjectRenderer(name).rotateModelMatrix(getObjectMatrix(name), rotateFactor[0], rotateFactor); }
    public void translateObject(String name, float[] translateFactor) { getObjectRenderer(name).translateModelMatrix(getObjectMatrix(name), translateFactor); }
    public void scaleRotateObject(String name, float[] scaleFactor, float[] rotateFactor) { getObjectRenderer(name).scaleRotateModelMatrix(getObjectMatrix(name), scaleFactor, rotateFactor[0], rotateFactor); }
    public void scaleTranslateObject(String name, float[] scaleFactor, float[] translateFactor) { getObjectRenderer(name).scaleTranslateModelMatrix(getObjectMatrix(name), scaleFactor, translateFactor); }
    public void rotateTranslateObject(String name, float[] rotateFactor, float[] translateFactor) { getObjectRenderer(name).rotateTranslateModelMatrix(getObjectMatrix(name), rotateFactor[0], rotateFactor, translateFactor); }
    public void scaleRotateTranslateObject(String name, float[] scaleFactor, float[] rotateFactor, float[] translateFactor) { getObjectRenderer(name).scaleRotateTranslateModelMatrix(getObjectMatrix(name), scaleFactor, rotateFactor[0], rotateFactor, translateFactor); }

    public void drawObjects(float[] viewMatrix, float[] projectionMatrix, float[] colorCorrectionRgba, float[] color) {
        augmentedObjects.forEach((keys, value) -> {
            if (value.getArea() == FACE_AREA.FOREHEAD_LEFT || value.getArea() == FACE_AREA.FOREHEAD_RIGHT)
                value.getRenderer().draw(viewMatrix, projectionMatrix, colorCorrectionRgba, color);
        });
        augmentedObjects.forEach((keys, value) -> {
            if (value.getArea() == FACE_AREA.CENTER)
                value.getRenderer().draw(viewMatrix, projectionMatrix, colorCorrectionRgba, color);
        });
    }

    public ObjectRenderer getObjectRenderer(String name) { return Objects.requireNonNull(this.augmentedObjects.get(name)).getRenderer(); }
    public float[] getObjectMatrix(String name) { return Objects.requireNonNull(this.augmentedObjects.get(name)).getMatrix(); }
    public FACE_AREA getObjectArea(String name) { return Objects.requireNonNull(this.augmentedObjects.get(name)).getArea(); }

}

