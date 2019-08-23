package com.theblackdiamonds.autoaugmented;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.Plane;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.Collection;

public class MainActivity extends AppCompatActivity {

    boolean isModelPlaced = false;
    private ArFragment arFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);
        assert arFragment != null;
        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdate);
    }

    public void onUpdate(FrameTime frameTime) {

        if (isModelPlaced)
            return;

        Frame frame = arFragment.getArSceneView().getArFrame();
        assert frame != null;
        Collection<Plane> planes = frame.getUpdatedTrackables(Plane.class);

        for (Plane plane : planes) {
            if (plane.getTrackingState() == TrackingState.TRACKING) {
                Anchor anchor = plane.createAnchor(plane.getCenterPose());
                makeCube(anchor);
                break;
            }
        }
    }

    private void makeCube(Anchor anchor) {
        MaterialFactory.makeOpaqueWithColor(MainActivity.this, new Color(android.graphics.Color.RED))
                .thenAccept(material -> {
                    ModelRenderable cubeRenderable = ShapeFactory.makeCube(new Vector3(0.3f, 0.3f, 0.3f),
                            new Vector3(0f, 0.3f, 0f), material);
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setRenderable(cubeRenderable);
                    arFragment.getArSceneView().getScene().addChild(anchorNode);
                });
    }
}